/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.ats.internal.arjuna;

import com.arjuna.ats.arjuna.ObjectType;
import com.arjuna.ats.arjuna.StateManager;
import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.coordinator.RecordType;
import com.arjuna.ats.arjuna.coordinator.TwoPhaseOutcome;
import com.arjuna.ats.arjuna.exceptions.ObjectStoreException;
import com.arjuna.ats.arjuna.logging.tsLogger;
import com.arjuna.ats.arjuna.objectstore.ParticipantStore;
import com.arjuna.ats.arjuna.state.OutputObjectState;
import com.arjuna.ats.internal.arjuna.abstractrecords.PersistenceRecord;

/**
 * Needs further consideration and then completion.
 */

public class TxLogWritePersistenceRecord extends PersistenceRecord
{

    public TxLogWritePersistenceRecord (OutputObjectState state, ParticipantStore participantStore, StateManager sm)
    {
	super(state, participantStore, sm);
    }
    
    public int typeIs ()
    {
	return RecordType.TXLOG_PERSISTENCE;
    }

    /**
     * commit the state saved during the prepare phase.
     */

    public int topLevelCommit ()
    {
	boolean result = false;
	LogWriteStateManager sm = null;
	boolean writeToLog = true;
	
	try
	{
	    sm = (LogWriteStateManager) super.objectAddr;

	    writeToLog = sm.writeOptimisation();
	}
	catch (ClassCastException ex)
	{
	    writeToLog = false;
	}
	
	if (targetParticipantStore != null)
	{
	    try
	    {
		if (shadowMade)
		{
		    result = targetParticipantStore.commit_state(order(), super.getTypeOfObject());
			    
		    if (!result) {
                tsLogger.i18NLogger.warn_PersistenceRecord_2(order());
            }
		}
		else
		{
		    if (topLevelState != null)
		    {
			if (!writeToLog)
			    result = targetParticipantStore.write_committed(order(), super.getTypeOfObject(), topLevelState);
			else
			    result = true;
		    }
		}
	    }
	    catch (ObjectStoreException e)
	    {
		result = false;
	    }
	}
	else
	{
	}
	
	if (!result)
	{
	}
	
	super.forgetAction(true);
	
	return ((result) ? TwoPhaseOutcome.FINISH_OK : TwoPhaseOutcome.FINISH_ERROR);
    }
	
    /**
     * topLevelPrepare attempts to save the object.
     * It will either do this in the action intention list or directly
     * in the object store by using the 'deactivate' function of the object
     * depending upon the size of the state.
     * To ensure that objects are correctly hidden while they are in an
     * uncommitted state if we use the abbreviated protocol then we write an
     * EMPTY object state as the shadow state - THIS MUST NOT BE COMMITTED.
     * Instead we write_committed the one saved in the intention list.
     * If the store cannot cope with being given an empty state we revert to
     * the old protocol.
     */

    public int topLevelPrepare ()
    {
	int result = TwoPhaseOutcome.PREPARE_NOTOK;
	StateManager sm = super.objectAddr;
	LogWriteStateManager lwsm = null;
	boolean writeToLog = true;

	try
	{
	    lwsm = (LogWriteStateManager) sm;
	    
	    writeToLog = lwsm.writeOptimisation();
	}
	catch (ClassCastException ex)
	{
	    writeToLog = false;
	}
	
	if ((sm != null) && (targetParticipantStore != null))
	{
	    topLevelState = new OutputObjectState(sm.get_uid(), sm.type());
	    
	    if (writeToLog || (!targetParticipantStore.fullCommitNeeded() &&
			       (sm.save_state(topLevelState, ObjectType.ANDPERSISTENT)) &&
			       (topLevelState.size() <= PersistenceRecord.MAX_OBJECT_SIZE)))
	    {
		if (PersistenceRecord.classicPrepare)
		{
		    OutputObjectState dummy = new OutputObjectState(Uid.nullUid(), null);

		    /*
		     * Write an empty shadow state to the store to indicate
		     * one exists, and to prevent bogus activation in the case
		     * where crash recovery hasn't run yet.
		     */
		    
		    try
		    {
			targetParticipantStore.write_uncommitted(sm.get_uid(), sm.type(), dummy);
			result = TwoPhaseOutcome.PREPARE_OK;
		    }
		    catch (ObjectStoreException e) {
                tsLogger.i18NLogger.warn_PersistenceRecord_21(e);
            }
		
		    dummy = null;
		}
		else
	        {
		    result = TwoPhaseOutcome.PREPARE_OK;
		}
	    }
	    else
	    {
		if (sm.deactivate(targetParticipantStore.getStoreName(), false))
		{
 		    shadowMade = true;
		    
		    result = TwoPhaseOutcome.PREPARE_OK;
		}
		else {
            tsLogger.i18NLogger.warn_PersistenceRecord_7();
        }
	    }
	}
	else {
        tsLogger.i18NLogger.warn_PersistenceRecord_8();
    }

	return result;
    }

    public String type ()
    {
	return "/StateManager/AbstractRecord/RecoveryRecord/PersistenceRecord/TxLogPersistenceRecord";
    }

    public TxLogWritePersistenceRecord ()
    {
	super();
    }

}