/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package org.jboss.jbossts.xts.recovery.coordinator.ba;

import org.jboss.jbossts.xts.recovery.logging.RecoveryLogger;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.coordinator.ActionStatus;
import com.arjuna.ats.arjuna.coordinator.RecordListIterator;
import com.arjuna.ats.arjuna.coordinator.AbstractRecord;

import com.arjuna.mwlabs.wscf.model.sagas.arjunacore.BACoordinator;
import com.arjuna.mwlabs.wscf.model.sagas.arjunacore.ParticipantRecord;

/**
 * This class is a plug-in module for the recovery manager.
 * It is responsible for recovering failed WSBA ACCoordinator transactions.
 *
 */
public class RecoveryBACoordinator extends BACoordinator {

   /**
    * Re-creates/activates an AtomicAction for the specified
    * transaction Uid.
    */
   public RecoveryBACoordinator( Uid rcvUid )
   {
      super( rcvUid ) ;
      _activated = activate() ;
      if (_activated) {
          setRecoveryCoordinator();
      }
   }

    /**
     * provide the recovered participants with a handle on this coordinator so they can
     * propagate events through to it.
     */

    public void setRecoveryCoordinator()
    {
        if (preparedList != null)
        {
            RecordListIterator iter = new RecordListIterator(preparedList);
            AbstractRecord absRec = iter.iterate();

            while (absRec != null)
            {
                if (absRec instanceof ParticipantRecord)
                {
                    ParticipantRecord pr = (ParticipantRecord) absRec;
                }

                absRec = iter.iterate();
            }
        }
    }

   /**
    * Replays phase 2 of the commit protocol.
    */
   public void replayPhase2()
   {
       final int status = status();

       if (RecoveryLogger.logger.isDebugEnabled()) {
           RecoveryLogger.logger.debugv("RecoveryBACoordinator.replayPhase2 recovering {0} ActionStatus is {1}", new Object[]{get_uid(), ActionStatus.stringForm(status)});
       }

       if ( _activated )
       {
           // we only need to rerun phase2 if the action status is  PREPARED, which happens
           // when we crash between a successful complete beginning a close, or COMMITTING, which
           // happens when we get a comms timeout from one of the participants after sending it a CLOSE
           // message. in the former case all participant records will be listed in the prepared list.
           // in the latter case the failed participant record(s) will have been reinstated in the
           // prepared list and the participant stub engine reactivated, where necessary,
           // under the call to activate() when this coordinator was created.

           // we can also arrive here when the action status is ABORTING. This happens when we
           // get a comms timeout from one of the participants after sending it a CANCEL message
           // or if we get a comms timeout from one of the participants after sending it a COMPENSATE
           // message.

       if ((status == ActionStatus.PREPARED) ||
               (status == ActionStatus.COMMITTING) ||
               (status == ActionStatus.COMMITTED) ||
               (status == ActionStatus.H_COMMIT) ||
               (status == ActionStatus.H_MIXED) ||
               (status == ActionStatus.H_HAZARD))
	   {
	       super.phase2Commit( _reportHeuristics ) ;
	   } else if ((status ==  ActionStatus.ABORTED) ||
               (status == ActionStatus.H_ROLLBACK) ||
               (status == ActionStatus.ABORTING) ||
               (status == ActionStatus.ABORT_ONLY))
       {
           super.phase2Abort( _reportHeuristics ) ;
       }

       if (RecoveryLogger.logger.isDebugEnabled()) {
           RecoveryLogger.logger.debugv("RecoveryBACoordinator.replayPhase2( {0} )  finished", new Object[]{get_uid()});
       }
       }
       else
       {
           RecoveryLogger.i18NLogger.warn_coordinator_ba_RecoveryBACoordinator_4(get_uid());
       }
   }

   // Flag to indicate that this transaction has been re-activated
   // successfully.
   private boolean _activated = false ;

   // whether heuristic reporting on phase 2 commit is enabled.
   private boolean _reportHeuristics = true ;
}
