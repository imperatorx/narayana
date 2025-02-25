/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.ats.internal.arjuna.abstractrecords;

import java.io.PrintWriter;

import com.arjuna.ats.arjuna.StateManager;
import com.arjuna.ats.arjuna.coordinator.AbstractRecord;
import com.arjuna.ats.arjuna.coordinator.RecordType;
import com.arjuna.ats.arjuna.coordinator.TwoPhaseOutcome;
import com.arjuna.ats.arjuna.exceptions.ObjectStoreException;
import com.arjuna.ats.arjuna.logging.tsLogger;
import com.arjuna.ats.arjuna.objectstore.ParticipantStore;
import com.arjuna.ats.arjuna.state.OutputObjectState;

/**
 * Cadaver records are created whenever a persistent object is deleted while
 * still in the scope of an atomic action. This ensures that if the
 * action commits the state of the persistent objects gets properly
 * reflected back in the object participantStore. For objects that are only
 * recoverable such work is unnecessary. Cadaver records replace
 * PersistenceRecords in the record list of an atomic action so they must
 * be merged with such records to enable both commits and aborts to occur.
 *
 * @author Mark Little (mark@arjuna.com)
 * @version $Id: CadaverRecord.java 2342 2006-03-30 13:06:17Z  $
 * @since JTS 1.0.
 */

public class CadaverRecord extends PersistenceRecord
{

    /**
     * Create a new instance, passing in the object that is being managed.
     *
     * @param os the state of the object that is being
     * removed.
     * @param participantStore the object participantStore instance used to manipulate the
     * persistent state.
     * @param sm the object being removed.
     */

    public CadaverRecord (OutputObjectState os, ParticipantStore participantStore,
			  StateManager sm)
    {
	super(os, participantStore, sm);

	newStateIsValid = ((os != null) ? true : false);
	oldState = null;
	oType = RecordType.NONE_RECORD;

	if (tsLogger.logger.isTraceEnabled()) {
        tsLogger.logger.trace("CadaverRecord::CadaverRecord(" + os + ", " + sm.get_uid() + ")");
    }
    }

    /**
     * Override default AbstractRecord method. CadaverRecords are propagated
     * regardless of the termination condition.
     *
     * @return <code>true</code>
     */

    public boolean propagateOnAbort ()
    {
	return true;
    }

    /**
     * The type of the record.
     *
     * @return RecordType.PERSISTENT
     * @see com.arjuna.ats.arjuna.coordinator.RecordType
     */

    public int typeIs ()
    {
	return RecordType.PERSISTENCE;
    }

    /**
     * The nested transaction has aborted. The record will invalidate any
     * new state.
     */

    public int nestedAbort ()
    {
	if (tsLogger.logger.isTraceEnabled()) {
        tsLogger.logger.trace("CadaverRecord::nestedAbort() for " + order());
    }

	if (oldState != null)
	    newStateIsValid = false;

	if (oType == RecordType.RECOVERY) {
        tsLogger.i18NLogger.warn_CadaverRecord_1(order(), getTypeOfObject());
    }

	/*
	 * No need to forget the action since this object is
	 * being deleted so it is unlikely to have modified called
	 * on it!
	 */

	//	super.forgetAction(false);

	return TwoPhaseOutcome.FINISH_OK;
    }

    /**
     * The nested transaction is preparing. If there is any new state for
     * the object being removed, and that state is valid, then this record
     * will call nestedPrepare on the object being removed.
     *
     * If we have no new state then we cannot commit and must force an
     * abort. Do this by failing the prepare phase.
     */

    public int nestedPrepare ()
    {
	if (tsLogger.logger.isTraceEnabled()) {
        tsLogger.logger.trace("CadaverRecord::nestedPrepare() for " + order());
    }

	if (newStateIsValid)
	    return super.nestedPrepare();
	else
	    return TwoPhaseOutcome.PREPARE_NOTOK;
    }

    /**
     * The nested transaction has aborted. Invalidate any new state.
     */

    public int topLevelAbort ()
    {
	if (tsLogger.logger.isTraceEnabled()) {
        tsLogger.logger.trace("CadaverRecord::topLevelAbort() for " + order());
    }

	newStateIsValid = false;

	if (oType == RecordType.RECOVERY) {
        tsLogger.i18NLogger.warn_CadaverRecord_1(order(), getTypeOfObject());
    }

	// super.forgetAction(false);

	return TwoPhaseOutcome.FINISH_OK;
    }

    /**
     * At topLevelCommit we commit the uncommitted version already saved
     * into object participantStore.
     * Cannot use inherited version since that assumes object is alive
     * instead talk directly to the object participantStore itself.
     */

    public int topLevelCommit ()
    {
	if (tsLogger.logger.isTraceEnabled()) {
        tsLogger.logger.trace("CadaverRecord::topLevelCommit() for " + order());
    }

	boolean res = true;
	OutputObjectState oState = super.state;

	if ((oState != null) && (oType == RecordType.PERSISTENCE))
	{
	    if (targetParticipantStore == null)
		return TwoPhaseOutcome.FINISH_ERROR;

	    try
	    {
		res = targetParticipantStore.commit_state(oState.stateUid(), oState.type());
	    }
	    catch (ObjectStoreException e)
	    {
		res = false;
	    }
	}

	// super.forgetAction(false);

	return ((res) ? TwoPhaseOutcome.FINISH_OK : TwoPhaseOutcome.FINISH_ERROR);
    }

    /**
     * At topLevelPrepare write uncommitted version into object participantStore.
     * Cannot use inherited version since that assumes object is alive
     * instead talk directly to the object participantStore itself.
     */

    public int topLevelPrepare ()
    {
	if (tsLogger.logger.isTraceEnabled()) {
        tsLogger.logger.trace("CadaverRecord::topLevelPrepare() for " + order());
    }

	int tlpOk = TwoPhaseOutcome.PREPARE_NOTOK;
	OutputObjectState oState = (newStateIsValid ? super.state : oldState);

	if (oState != null)
	{
	    if (oType == RecordType.PERSISTENCE)
	    {
		if (targetParticipantStore == null)
		    return TwoPhaseOutcome.PREPARE_NOTOK;

		try
		{
		    if (targetParticipantStore.write_uncommitted(oState.stateUid(), oState.type(), oState))
		    {
			if (shadowForced())
			    tlpOk = TwoPhaseOutcome.PREPARE_OK;
		    }
		}
		catch (final ObjectStoreException e)
		{
		    e.printStackTrace();
		}
	    }
	    else
		tlpOk = TwoPhaseOutcome.PREPARE_OK;
	}

	return tlpOk;
    }

    /**
     * Override AbstractRecord.print to write specific information to
     * the specified stream.
     *
     * @param strm the stream to use.
     */

    public void print (PrintWriter strm)
    {
	strm.println("Cadaver for:");
	super.print(strm);
    }

    /**
     * The type of the class - may be used to save information in an
     * hierarchical manner in the object participantStore.
     */

    public String type()
    {
	return "/StateManager/AbstractRecord/RecoveryRecord/PersistenceRecord/CadaverRecord";
    }

    /**
     * Override the AbstractRecord.doSave.
     *
     * @return <code>true</code> if the object being removed is a persistent
     * object (RecordType.PERSISTENT). <code>false</code> otherwise.
     * @see com.arjuna.ats.arjuna.coordinator.RecordType
     */

    public boolean doSave ()
    {
	if (oType == RecordType.PERSISTENCE)
	    return true;
	else
	    return false;
    }

    /**
     * merge takes the information from the incoming PersistenceRecord and
     * uses it to initialise the oldState information. This is required
     * for processing of action aborts since CadaverRecords maintain the
     * final state of an object normally - which is required if the action
     * commits.
     *
     * @param mergewith The record to merge with.
     */

    public void merge (AbstractRecord mergewith)
    {
	/*
	 *  Following assumes that value returns a pointer to the
	 *  old state maintained in the PersistenceRecord (as an ObjectState).
	 *  Here we create a copy of that state allowing the original
	 *  to be deleted
	 */

	oType = mergewith.typeIs();

	if (oldState != null)
	{
	    if (newStateIsValid)
	    {
		oldState = null;
	    }
	    else
	    {
		setValue(oldState);
		newStateIsValid = true;
	    }
	}

	oldState = new OutputObjectState((OutputObjectState)(mergewith.value()));
    }

    /**
     * Overrides AbstractRecord.shouldMerge
     *
     * @param ar the record to potentially merge with.
     *
     * @return <code>true</code> if this instance and the parameter have the
     * same id (order()) and the parameter is either persistent or recoverable.
     * <code>false</code> otherwise.
     * @see com.arjuna.ats.arjuna.coordinator.RecordType
     */

    public boolean shouldMerge (AbstractRecord ar)
    {
	return (((order().equals(ar.order())) &&
		 ((ar.typeIs() == RecordType.PERSISTENCE) ||
		  (ar.typeIs() == RecordType.RECOVERY)))
		? true : false);
    }

    /**
     * Overrides AbstractRecord.shouldReplace
     *
     * @param ar the record to potentially replace this
     * instance.
     *
     * @return <code>true</code> if this instance and the parameter have the
     * same id (order()) and the parameter is either persistent or recoverable.
     * <code>false</code> otherwise.
     * @see com.arjuna.ats.arjuna.coordinator.RecordType
     */

    public boolean shouldReplace (AbstractRecord ar)
    {
	return (((order().equals(ar.order())) &&
		 ((ar.typeIs() == RecordType.PERSISTENCE) ||
		  (ar.typeIs() == RecordType.RECOVERY)))
		? true : false);
    }

    /**
     * Create a new instance using default values. Typically used during
     * failure recovery.
     */

    public CadaverRecord ()
    {
	super();

	newStateIsValid = false;
	oldState = null;
	oType = RecordType.NONE_RECORD;
	targetParticipantStore = null;

	if (tsLogger.logger.isTraceEnabled()) {
        tsLogger.logger.trace("CadaverRecord::CadaverRecord ()");
    }
    }

    private boolean           newStateIsValid;
    private OutputObjectState oldState;
    private int               oType;
}