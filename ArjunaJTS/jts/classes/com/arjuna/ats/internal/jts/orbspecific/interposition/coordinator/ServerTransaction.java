/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.ats.internal.jts.orbspecific.interposition.coordinator;

import java.io.IOException;

import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INVALID_TRANSACTION;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TRANSACTION_ROLLEDBACK;
import org.omg.CosTransactions.Control;
import org.omg.CosTransactions.Coordinator;
import org.omg.CosTransactions.HeuristicHazard;
import org.omg.CosTransactions.Inactive;
import org.omg.CosTransactions.RecoveryCoordinator;
import org.omg.CosTransactions.RecoveryCoordinatorHelper;
import org.omg.CosTransactions.Synchronization;
import org.omg.CosTransactions.SynchronizationUnavailable;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.coordinator.ActionManager;
import com.arjuna.ats.arjuna.coordinator.ActionStatus;
import com.arjuna.ats.arjuna.coordinator.TwoPhaseOutcome;
import com.arjuna.ats.arjuna.coordinator.TxControl;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputObjectState;
import com.arjuna.ats.internal.jts.ORBManager;
import com.arjuna.ats.internal.jts.orbspecific.coordinator.ArjunaTransactionImple;
import com.arjuna.ats.internal.jts.orbspecific.interposition.ServerControl;
import com.arjuna.ats.internal.jts.orbspecific.interposition.resources.ServerSynchronization;
import com.arjuna.ats.jts.common.jtsPropertyManager;
import com.arjuna.ats.jts.exceptions.ExceptionCodes;
import com.arjuna.ats.jts.logging.jtsLogger;
import com.arjuna.ats.jts.utils.Utility;

/**
 * This looks like an Transaction, but is only created for interposition
 * purposes. The classes ServerTopLevelAction and ServerNestedAction use
 * instances of this class to drive the server-side protocol.
 * 
 * @author Mark Little (mark@arjuna.com)
 * @version $Id: ServerTransaction.java 2342 2006-03-30 13:06:17Z  $
 * @since JTS 1.0.
 */

/*
 * This class shouldn't need to be synchronized much since any given instance
 * should be assigned to at most one resource.
 */

public class ServerTransaction extends ArjunaTransactionImple
{

	public ServerTransaction (Uid actUid, Control myParent)
	{
		this(actUid, myParent, null);
	}

	public ServerTransaction (Uid actUid, Control myParent, ArjunaTransactionImple parentImpl)
	{
		super(actUid, myParent, parentImpl);

		subordinate = true;

		if (jtsLogger.logger.isTraceEnabled()) {
            jtsLogger.logger.trace("ServerTransaction::ServerTransaction ( "
                    + actUid
                    + ", Control myParent, "
                    + ((parentImpl != null) ? parentImpl.get_uid()
                    : Uid.nullUid()) + " )");
        }

		_savingUid = new Uid();
		_sync = null;
		_beforeCompleted = false;
		_recoveryCoordinator = null;
		_prepState = ActionStatus.COMMITTING;
	}

    @Override
	public void finalize ()
	{
		if (jtsLogger.logger.isTraceEnabled()) {
            jtsLogger.logger.trace("ServerTransaction.finalize ( "
                    + get_uid() + " )");
        }

		_savingUid = null;

		if (_sync != null)
		{
			_sync.destroy();
			_sync = null;
		}

		/*
		 * Remember to destroy the recovery coordinator.
		 */

		_recoveryCoordinator = null;

		super.finalizeInternal();
	}

	public String type ()
	{
		return typeName();
	}

	public Uid getSavingUid ()
	{
		return _savingUid;
	}

	public final int doPrepare ()
	{
		if (jtsLogger.logger.isTraceEnabled()) {
            jtsLogger.logger.trace("ServerTransaction::doPrepare ( "
                    + get_uid() + " )");
        }

		/*
		 * If transaction has already been prepared then return error status.
		 */

		org.omg.CosTransactions.Status s = get_status();

		if ((s != org.omg.CosTransactions.Status.StatusActive) &&
				(s != org.omg.CosTransactions.Status.StatusMarkedRollback))
		{
			return TwoPhaseOutcome.INVALID_TRANSACTION;
		}

		_prepState = ActionStatus.PREPARED;

		/*
		 * If we do not have an interposed synchronization then
		 * before_completions will not have been called yet. So, do it now.
		 */

		if (!_interposedSynch)
		{
		    if ((s != org.omg.CosTransactions.Status.StatusMarkedRollback) || TxControl.isBeforeCompletionWhenRollbackOnly())
		    {
			try
			{
				doBeforeCompletion();
			}
			catch (Exception e)
			{
				/*
				 * Transaction will have been put into a state which forces it
				 * to rollback, so do nothing here.
				 */
			}			
		    }
		}

		if (!_beforeCompleted && (_sync != null)) {
            /*
                * Synchronizations should have been called by now if we have them!
                */

            jtsLogger.i18NLogger.warn_orbspecific_interposition_coordinator_syncerror("ServerTransaction.doPrepare");

            /*
                * Prevent commit!
                */

            super.preventCommit();
        }
	              
		int res = super.prepare(true);

		/*
		 * If read-only, the coordinator will not talk to us again, so do commit
		 * now and destroy the transaction.
		 * 
		 * Otherwise, the transaction is destroyed when the commit/abort/forget
		 * protocol completes.
		 */

		if (res == TwoPhaseOutcome.PREPARE_READONLY)
		{
			doPhase2Commit();
		}

		return res;
	}

	public final void doForget ()
	{
		super.destroyAction();
	}

	public final int doPhase2Commit ()
	{
		if (jtsLogger.logger.isTraceEnabled()) {
            jtsLogger.logger.trace("ServerTransaction::doPhase2Commit ( "
                    + get_uid() + " )");
        }

		/*
		 * If the transaction has already terminated, then return the status.
		 */

		org.omg.CosTransactions.Status s = get_status();

		if (s != org.omg.CosTransactions.Status.StatusPrepared) {
            jtsLogger.i18NLogger.warn_orbspecific_interposition_coordinator_txnotprepared("ServerTransaction.doPhase2Commit", Utility.stringStatus(s));

            return finalStatus();
        }

		super.phase2Commit(true);
		if (super.failedList != null && super.failedList.size() > 0) {
		    return ActionStatus.COMMITTING;
		}

		/*
		 * Now do after completion stuff.
		 */

		try
		{
			doAfterCompletion(get_status());
		}
		catch (Exception e)
		{
		}

		if (parentTransaction != null)
			parentTransaction.removeChildAction(this);

		super.destroyAction();

		ActionManager.manager().remove(get_uid());

		return finalStatus();
	}

	public final int doPhase2Abort ()
	{
		if (jtsLogger.logger.isTraceEnabled()) {
            jtsLogger.logger.trace("ServerTransaction::doPhase2Abort ( "
                    + get_uid() + " )");
        }

		/*
		 * If the transaction has already terminated, then return the status. If
		 * there hasn't been a heuristic outcome then we try to massage the result
		 * to be consistent with what the caller expects: the fact that the
		 * transaction is marked as committed during prepare without any problems means
		 * that the intentions lists are zero so it's fine to say that it aborted instead.
		 */

		org.omg.CosTransactions.Status s = get_status();

		if ((s == org.omg.CosTransactions.Status.StatusCommitted && preparedList.size() == 0)
				|| (s == org.omg.CosTransactions.Status.StatusRolledBack))
		{
		    int status = finalStatus();
		    
		    switch (status)
		    {
		    case ActionStatus.COMMITTED:
		    case ActionStatus.COMMITTING:
		    case ActionStatus.ABORTED:
		    case ActionStatus.ABORTING:
		        return ActionStatus.ABORTED;
		    default:
		        return status;
		    }
		}

		super.phase2Abort(true);
		
		/*
		 * Now do after completion stuff.
		 */

		try
		{
			doAfterCompletion(get_status());
		}
		catch (Exception e)
		{
		}

		if (parentTransaction != null)
		{
			parentTransaction.removeChildAction(this);
		}

		super.destroyAction();

		ActionManager.manager().remove(get_uid());

		return finalStatus();
	}

	/*
	 * Called for one-phase commit.
	 */

	public void doCommit (boolean report_heuristics) throws HeuristicHazard,
			SystemException
	{
		int outcome = super.status();

		if ((outcome == ActionStatus.RUNNING)
				|| (outcome == ActionStatus.ABORT_ONLY)) // have we already been
														 // committed?
		{
			if (!_interposedSynch)
			{
			    if ((outcome != ActionStatus.ABORT_ONLY) || TxControl.isBeforeCompletionWhenRollbackOnly())
			    {
				try
				{
					doBeforeCompletion();
				}
				catch (Exception e)
				{
					/*
					 * Transaction will have been put into a state which forces
					 * it to rollback, so do nothing here.
					 */
				}			
			    }
			}

			outcome = super.End(report_heuristics);
		}
		else
		{
			/*
			 * Differentiate between us committing the transaction and some
			 * other thread doing it.
			 */

			if (parentTransaction != null)
				parentTransaction.removeChildAction(this);

			throw new INVALID_TRANSACTION(ExceptionCodes.INVALID_ACTION,
					CompletionStatus.COMPLETED_NO);
		}

		/*
		 * Now do after completion stuff.
		 */

		try
		{
			doAfterCompletion(get_status());
		}
		catch (Exception e)
		{
		}

		if (parentTransaction != null)
			parentTransaction.removeChildAction(this);

		super.destroyAction();

		switch (outcome)
		{
		case ActionStatus.COMMITTED:
		case ActionStatus.H_COMMIT:
		case ActionStatus.COMMITTING: // in case asynchronous commit!
			return;
		case ActionStatus.ABORTING:
		case ActionStatus.ABORTED:
		case ActionStatus.H_ROLLBACK:
			throw new TRANSACTION_ROLLEDBACK(ExceptionCodes.FAILED_TO_COMMIT,
					CompletionStatus.COMPLETED_NO);
		case ActionStatus.H_HAZARD:
		default:
			throw new HeuristicHazard();
		}
	}

	public void rollback () throws SystemException
	{
		super.rollback();
	}

	/**
	 * Registering a synchronization with interposition is a bit complicated!
	 * Synchronizations must be called prior to prepare; if no
	 * interposed-synchronization is used then either synchronizations would be
	 * registered locally (and then ignored by the commit protocol) or they
	 * would need to be registered remotely, which would mean a cross-address
	 * space call for each synchronization!
	 * 
	 * The first time a synchronization is registered locally, we register a
	 * proxy back with the real coordinator. When that transaction commits, it
	 * will call this proxy, which will then drive the locally registered
	 * synchronizations (actually it calls appropriate on the transaction to do
	 * this.)
	 * 
	 * However, one-phase commit complicates matters even more since we call
	 * commit on the interposed coordinator, which runs through the commit and
	 * then the after_completion code before returning to the real coordinator's
	 * commit call. Rather than separate commit and synchronization code
	 * completely from the transaction (in which case we could just call the
	 * commit portion here) we let after_completion get called before returning
	 * the commit response, and simply ignore the real coordinator's subsequent
	 * call to after_completion.
	 */

	public synchronized void register_synchronization (Synchronization theSync)
			throws Inactive, SynchronizationUnavailable, SystemException
	{
		if (!is_top_level_transaction()) // are we a top-level transaction?
		{
			throw new SynchronizationUnavailable();
		}
		else
		{
			/*
			 * If we support interposed synchronizations then add one now,
			 * otherwise just add all synchronizations locally.
			 */

			if (_interposedSynch)
			{
				if (_sync == null)
				{
					_sync = new ServerSynchronization(this);

					Coordinator realCoord = null;

					/*
					 * First register interposed-synchronization.
					 */

					try
					{
						ServerControl control = (ServerControl) super.controlHandle;

						if (controlHandle != null)
						{
							realCoord = control.originalCoordinator();

							if (realCoord != null)
							{
								realCoord.register_synchronization(_sync.getSynchronization());
							}
							else
								throw new BAD_OPERATION(
										ExceptionCodes.NO_TRANSACTION,
										CompletionStatus.COMPLETED_NO);
						}
						else
							throw new BAD_OPERATION(
									ExceptionCodes.NO_TRANSACTION,
									CompletionStatus.COMPLETED_NO);
					}
					catch (Inactive e1)
					{
						realCoord = null;

						throw e1;
					}
					catch (SynchronizationUnavailable e2)
					{
						realCoord = null;

						throw e2;
					}
					catch (SystemException e3)
					{
						realCoord = null;

						throw e3;
					}

					realCoord = null;
				}
			}

			/*
			 * Now register the synchronization locally.
			 */

			super.register_synchronization(theSync);
		}
	}

	/*
	 * These methods are here to make protected methods in
	 * ArjunaTransactionImple available to ServerSynchronization.
	 */

	public void doBeforeCompletion () throws SystemException
	{
	    if (!_beforeCompleted)
	    {
		_beforeCompleted = true;

		super.doBeforeCompletion();
	    }
	}

	public void doAfterCompletion (org.omg.CosTransactions.Status s)
			throws SystemException
	{
		super.doAfterCompletion(s);
	}

	public static String typeName ()
	{
		return "/StateManager/BasicAction/TwoPhaseCoordinator/ArjunaTransactionImple/ServerTransaction";
	}

	public final synchronized void setRecoveryCoordinator (RecoveryCoordinator recCoord)
	{
		_recoveryCoordinator = recCoord;
	}

	/*
	 * If this is a top-level transaction then we should have a recovery
	 * coordinator reference, so save it away.
	 */

	public boolean save_state (OutputObjectState os, int ot)
	{
		try
		{
			if (_recoveryCoordinator != null)
			{
				os.packBoolean(true);
				os.packString(ORBManager.getORB().orb().object_to_string(_recoveryCoordinator));
			}
			else
				os.packBoolean(false);

			return super.save_state(os, ot);
		}
		catch (IOException e)
		{
            jtsLogger.i18NLogger.warn_orbspecific_interposition_coordinator_generror("ServerTransaction.save_state", e);
		}

		return false;
	}

	public boolean restore_state (InputObjectState os, int ot)
	{
		try
		{
			boolean haveRecCoord = os.unpackBoolean();

			if (haveRecCoord)
			{
				try
				{
					String ior = os.unpackString();
					org.omg.CORBA.Object objRef = ORBManager.getORB().orb().string_to_object(ior);
					_recoveryCoordinator = RecoveryCoordinatorHelper.narrow(objRef);
				}
				catch (Exception e)
				{
                    jtsLogger.i18NLogger.warn_orbspecific_interposition_coordinator_generror("ServerTransaction.restore_state", e);

					return false;
				}
			}
			else
				_recoveryCoordinator = null;

			return super.restore_state(os, ot);
		}
		catch (IOException ex)
		{
            jtsLogger.i18NLogger.warn_orbspecific_interposition_coordinator_generror("ServerTransaction.restore_state", ex);
		}

		return false;
	}

	public String toString ()
	{
		return "ServerTransaction < " + get_uid() + " >";
	}

	/**
	 * Used during crash recovery. The Uid is the identity of the state which
	 * this transaction's log is stored in. It is not the identity of the
	 * transaction!
	 * 
	 * Therefore you may pass nullUid to the base transaction and rely on activating the
	 * transaction state to set up the transaction id.
	 */

	protected ServerTransaction (Uid recoveringActUid)
	{
	    super(recoveringActUid);

		if (jtsLogger.logger.isTraceEnabled()) {
            jtsLogger.logger.trace("ServerTransaction::ServerTransaction ( "
                    + recoveringActUid + " )");
        }

		_savingUid = recoveringActUid;
		_sync = null;
		_beforeCompleted = false;
		_recoveryCoordinator = null;
		_prepState = ActionStatus.COMMITTING;
	}

	protected final synchronized int preparedStatus ()
	{
		return _prepState;
	}

	private final int finalStatus ()
	{
		int heuristic = super.getHeuristicDecision();

		switch (heuristic)
		{
		case TwoPhaseOutcome.PREPARE_OK:
		case TwoPhaseOutcome.FINISH_OK:
			return super.status();
		case TwoPhaseOutcome.HEURISTIC_ROLLBACK:
			return ActionStatus.H_ROLLBACK;
		case TwoPhaseOutcome.HEURISTIC_COMMIT:
			return ActionStatus.H_COMMIT;
		case TwoPhaseOutcome.HEURISTIC_MIXED:
			return ActionStatus.H_MIXED;
		case TwoPhaseOutcome.HEURISTIC_HAZARD:
		default:
			return ActionStatus.H_HAZARD;
		}
	}

	protected RecoveryCoordinator _recoveryCoordinator;

	private Uid _savingUid;

	private ServerSynchronization _sync;

	private boolean _beforeCompleted;

	private int _prepState;

	private static final boolean _interposedSynch = jtsPropertyManager.getJTSEnvironmentBean()
            .isSupportInterposedSynchronization();

}