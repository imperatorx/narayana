/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.ats.internal.arjuna.abstractrecords;

import java.io.PrintWriter;

import com.arjuna.ats.arjuna.ObjectType;
import com.arjuna.ats.arjuna.StateManager;
import com.arjuna.ats.arjuna.coordinator.AbstractRecord;
import com.arjuna.ats.arjuna.coordinator.BasicAction;
import com.arjuna.ats.arjuna.coordinator.RecordType;
import com.arjuna.ats.arjuna.coordinator.TwoPhaseOutcome;
import com.arjuna.ats.arjuna.logging.tsLogger;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputObjectState;

public class RecoveryRecord extends AbstractRecord
{

    /**
     * This constructor is used to create a new instance of a RecoveryRecord.
     */

    public RecoveryRecord(OutputObjectState os, StateManager sm)
    {
        super(sm.get_uid(), sm.type(), ObjectType.ANDPERSISTENT);

        if (tsLogger.logger.isDebugEnabled()) {
            tsLogger.logger.debug("RecoveryRecord::RecoveryRecord(" + os + ", "
                    + sm.get_uid() + ")");
        }

        objectAddr = sm;
        state = os;
        actionHandle = BasicAction.Current();
    }

    public int typeIs ()
    {
        return RecordType.RECOVERY;
    }

    public Object value ()
    {
        return state;
    }

    public void setValue (Object newState)
    {
        if (newState instanceof OutputObjectState)
            state = (OutputObjectState) newState;
        else {
            tsLogger.i18NLogger.warn_RecoveryRecord_1();
        }
    }

    /**
     * nestedAbort causes the restore_state function of the object to be invoked
     * passing it the saved ObjectState.
     */

    public int nestedAbort ()
    {
        if (tsLogger.logger.isDebugEnabled()) {
            tsLogger.logger.debug("RecoveryRecord::nestedAbort() for " + order());
        }

        /*
         * First check that we have a state. We won't have for records created
         * by crash recovery.
         */

        forgetAction(false);

        if (state != null)
        {
            if (state.notempty()) /* anything to restore ? */
            {
                InputObjectState oldState = new InputObjectState(state);

                int result = objectAddr.restore_state(oldState,
                        ObjectType.RECOVERABLE) ? TwoPhaseOutcome.FINISH_OK
                        : TwoPhaseOutcome.FINISH_ERROR;

                if (result == TwoPhaseOutcome.FINISH_ERROR) {
                    tsLogger.i18NLogger.warn_RecoveryRecord_2();
                }

                return result;
            }
        }

        return TwoPhaseOutcome.FINISH_OK;
    }

    /**
     * nestedCommit does nothing since the passing of the state up to the parent
     * action is handled by the record list merging system. In fact since
     * nestedPrepare returns PREPARE_READONLY this function should never
     * actually be called
     */

    public int nestedCommit ()
    {
        if (tsLogger.logger.isDebugEnabled()) {
            tsLogger.logger.debug("RecoveryRecord::nestedCommit() for " + order());
        }

        return TwoPhaseOutcome.FINISH_OK;
    }

    public int nestedPrepare ()
    {
        if (tsLogger.logger.isDebugEnabled()) {
            tsLogger.logger.debug("RecoveryRecord::nestedPrepare() for " + order());
        }

        forgetAction(true);

        return TwoPhaseOutcome.PREPARE_READONLY;
    }

    /**
     * topLevelAbort for Recovery records implies the object state should be
     * restored to the saved state exactly like a nested abort.
     */

    public int topLevelAbort ()
    {
        if (tsLogger.logger.isDebugEnabled()) {
            tsLogger.logger.debug("RecoveryRecord::topLevelAbort() for " + order());
        }

        return nestedAbort(); /* i.e., same as nested case */
    }

    /**
     * topLevelCommit has nothing to do for RecoveryRecords as no changes have
     * been made in the object store. In fact since topLevelPrepare returns
     * PREPARE_READONLY this function should never actually be called
     */

    public int topLevelCommit ()
    {
        if (tsLogger.logger.isDebugEnabled()) {
            tsLogger.logger.debug("RecoveryRecord::topLevelCommit() for " + order());
        }

        forgetAction(true);

        return TwoPhaseOutcome.FINISH_OK;
    }

    /**
     * topLevelPrepare can return PREPARE_READONLY to avoid topLevelCommit being
     * called in the action commit case
     */

    public int topLevelPrepare ()
    {
        if (tsLogger.logger.isDebugEnabled()) {
            tsLogger.logger.debug("RecoveryRecord::topLevelPrepare() for " + order());
        }

        return TwoPhaseOutcome.PREPARE_READONLY;
    }

    /*
     * Saving of RecoveryRecords is only undertaken during the Prepare phase of
     * the top level 2PC. Since the managed objects are only recoverable (not
     * persistent) there is no need to save any information (or restore any
     * either). However, persistence records (derived from recovery records)
     * need to be saved for crash recovery purposes.
     */

    public boolean doSave ()
    {
        return false;
    }

    public boolean restore_state (InputObjectState os, int ot)
    {
        return super.restore_state(os, ot);
    }

    public boolean save_state (OutputObjectState os, int ot)
    {
        return super.save_state(os, ot);
    }

    public void print (PrintWriter strm)
    {
        super.print(strm);
        strm.println("RecoveryRecord with state:\n" + state);
    }

    public String type ()
    {
        return "/StateManager/AbstractRecord/RecoveryRecord";
    }

    public void merge (AbstractRecord a)
    {
    }

    public void alter (AbstractRecord a)
    {
    }

    /*
     * should_merge and should_replace are invoked by the record list manager to
     * determine if two records should be merged togethor or if the 'newer'
     * should replace the older. shouldAdd determines if the new record should
     * be added in addition to the existing record and is currently only invoked
     * if both of should_merge and should_replace return false Default
     * implementations here always return false - ie new records do not override
     * old.
     */

    public boolean shouldAdd (AbstractRecord a)
    {
        return false;
    }

    public boolean shouldAlter (AbstractRecord a)
    {
        return false;
    }

    public boolean shouldMerge (AbstractRecord a)
    {
        return false;
    }

    public boolean shouldReplace (AbstractRecord a)
    {
        return false;
    }

    /*
     * Creates a 'blank' recovery record. This is used during crash recovery
     * when recreating the prepared list of a server atomic action.
     */

    public RecoveryRecord()
    {
        super();

        if (tsLogger.logger.isDebugEnabled()) {
            tsLogger.logger.debug("RecoveryRecord::RecoveryRecord()"
                    + " - crash recovery constructor");
        }

        objectAddr = null;
        state = null;
        actionHandle = null;
    }

    /*
     * Can we use this to force our parent to "remember" us when we commit, and
     * prevent the system from creating another record in that action?
     */

    protected final void forgetAction (boolean commit)
    {
        if ((actionHandle != null) && (objectAddr != null))
        {
            StateManagerFriend.forgetAction(objectAddr, actionHandle, commit, RecordType.RECOVERY);
            actionHandle = null; // only do this once!
        }
    }

    protected StateManager objectAddr;

    protected OutputObjectState state;

    private BasicAction actionHandle;

}