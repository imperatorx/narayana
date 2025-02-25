/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.ats.internal.jts;

import java.util.Collections;

import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INVALID_TRANSACTION;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TRANSACTION_ROLLEDBACK;
import org.omg.CORBA.UNKNOWN;
import org.omg.CosTransactions.Control;
import org.omg.CosTransactions.Coordinator;
import org.omg.CosTransactions.HeuristicHazard;
import org.omg.CosTransactions.HeuristicMixed;
import org.omg.CosTransactions.Inactive;
import org.omg.CosTransactions.NoTransaction;
import org.omg.CosTransactions.NotSubtransaction;
import org.omg.CosTransactions.Resource;
import org.omg.CosTransactions.Status;
import org.omg.CosTransactions.SubtransactionAwareResource;
import org.omg.CosTransactions.SubtransactionsUnavailable;
import org.omg.CosTransactions.Synchronization;
import org.omg.CosTransactions.SynchronizationUnavailable;
import org.omg.CosTransactions.Terminator;
import org.omg.CosTransactions.Unavailable;

import com.arjuna.ArjunaOTS.UidCoordinator;
import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.coordinator.ActionStatus;
import com.arjuna.ats.arjuna.coordinator.Reapable;
import com.arjuna.ats.internal.jts.orbspecific.ControlImple;
import com.arjuna.ats.internal.jts.orbspecific.coordinator.ArjunaTransactionImple;
import com.arjuna.ats.internal.jts.utils.Helper;
import com.arjuna.ats.jts.common.jtsPropertyManager;
import com.arjuna.ats.jts.exceptions.ExceptionCodes;
import com.arjuna.ats.jts.logging.jtsLogger;

/**
 * This class attempts to mask the local/remote control issue. We try to use
 * local controls directly as much as possible and not register them with the
 * ORB until the last minute. This improves performance *significantly*. At
 * present we only do this for top-level transactions, but extending for nested
 * transactions is straightforward.
 * 
 * It also acts as a convenience class for ease of use. Therefore, some
 * Coordinator and Terminator methods may be found directly on this class.
 * Because of the way in which the implementation works, however, some of their
 * signatures may be slightly different.
 * 
 * @author Mark Little (mark_little@hp.com)
 * @version $Id: ControlWrapper.java 2342 2006-03-30 13:06:17Z  $
 * @since JTS 2.0.
 */

/*
 * We create and destroy instances of this class regularly simply because
 * otherwise we would never know.
 */

public class ControlWrapper implements Reapable
{

	public ControlWrapper (Control c)
	{
		this(c, (Uid) null);

		/*
		 * Now get the Uid for this control or generate one if it isn't a
		 * JBoss transaction.
		 */

		try
		{
			UidCoordinator uidCoord = Helper.getUidCoordinator(c);

			_theUid = Helper.getUid(uidCoord);
		}
		catch (Exception e)
		{
			_theUid = new Uid();
		}
	}

	public ControlWrapper (ControlImple impl)
	{
		_control = null;
		_controlImpl = impl;
		_checkedLocality = true;
		_theUid = impl.get_uid();
	}

	public ControlWrapper (Control c, ControlImple impl)
	{
		_control = c;
		_controlImpl = impl;
		_checkedLocality = (impl != null);
		_theUid = ((impl == null) ? Uid.nullUid() : impl.get_uid());
	}

	public ControlWrapper (Control c, Uid u)
	{
		_control = c;
		_controlImpl = null;
		_checkedLocality = false;
		_theUid = u;
	}

	/*
	 * The Reapable methods.
	 */

	public boolean running ()
	{
		try
		{
			org.omg.CosTransactions.Status status = get_status();

			switch (status.value())
			{
			case Status._StatusActive:
			case Status._StatusMarkedRollback:
				return true;
			default:
				return false;
			}
		}
		catch (Exception ex)
		{
			return true;
		}
	}

	public boolean preventCommit ()
	{
		try
		{
			rollback_only();

			return true;
		}
		catch (Exception ex) {
            jtsLogger.i18NLogger.warn_cwcommit(ex);

            return false;
        }
	}

	public int cancel ()
	{
		try
		{
			rollback();

			return ActionStatus.ABORTED;
		}
		catch (Unavailable ex)
		{
			return ActionStatus.INVALID;
		}
		catch (NoTransaction ex)
		{
			return ActionStatus.NO_ACTION;
		}
		catch (Exception ex) {
            jtsLogger.i18NLogger.warn_cwabort(ex);

            return ActionStatus.INVALID;
        }
	}

	public Uid get_uid ()
	{
		return _theUid;
	}

	public ControlWrapper create_subtransaction () throws Unavailable,
			Inactive, SubtransactionsUnavailable, SystemException
	{
		Coordinator coord = null;

		try
		{
			coord = ((_control != null) ? _control.get_coordinator()
					: _controlImpl.get_coordinator());
		}
		catch (SystemException e)
		{
			coord = null;
		}

		if (coord != null)
		{
			return new ControlWrapper(coord.create_subtransaction());
		}
		else
		{
			if (jtsLogger.logger.isTraceEnabled()) {
                jtsLogger.logger.trace("ControlWrapper::create_subtransaction - subtransaction parent is inactive.");
            }

			throw new INVALID_TRANSACTION(
					ExceptionCodes.UNAVAILABLE_COORDINATOR,
					CompletionStatus.COMPLETED_NO);
		}
	}

	public final void commit (boolean report_heuristics) throws Unavailable,
			HeuristicMixed, HeuristicHazard, SystemException
	{
		try
		{
			if (_controlImpl != null)
				_controlImpl.getImplHandle().commit(report_heuristics);
			else
			{
				Terminator t = null;

				try
				{
					t = _control.get_terminator();
				}
				catch (SystemException e)
				{
					throw new Unavailable();
				}
				catch (final NullPointerException ex)
				{
				    throw new Unavailable();
				}

				if (t != null)
					t.commit(report_heuristics);
				else
					throw new Unavailable();
			}
		}
		catch (NullPointerException ex)  // if local handle is null then it was terminated (probably by reaper)
		{
		    // check termination status
		    
		    if (_controlImpl.getFinalStatus() != org.omg.CosTransactions.Status.StatusCommitted)
                        throw new TRANSACTION_ROLLEDBACK();
		}
	}

	public final void rollback () throws Unavailable, NoTransaction,
			SystemException
	{
		try
		{
			if (_controlImpl != null)
				_controlImpl.getImplHandle().rollback();
			else
			{
				Terminator t = null;

				try
				{
					t = _control.get_terminator();
				}
				catch (SystemException e)
				{
					throw new Unavailable();
				}
				catch (final NullPointerException ex)
                                {
                                    throw new Unavailable();
                                }
				
				if (t != null)
					t.rollback();
				else
					throw new Unavailable();
			}
		}
		catch (NullPointerException ex)
		{
			return;
		}
	}

	public final void rollback_only () throws Unavailable, NoTransaction,
			Inactive, SystemException
	{
		try
		{
			if (_controlImpl != null)
				_controlImpl.getImplHandle().rollback_only();
			else
			{
				Coordinator c = null;

				try
				{
					c = _control.get_coordinator();
				}
				catch (SystemException e)
				{
					throw new Unavailable();
				}
				catch (final NullPointerException ex)
                                {
                                    throw new Unavailable();
                                }
				
				if (c != null)
					c.rollback_only();
				else
					throw new Unavailable();
			}
		}
		catch (NullPointerException ex)
		{
			throw new Inactive();
		}
	}

	public final org.omg.CosTransactions.RecoveryCoordinator register_resource (Resource r)
			throws Inactive, SystemException
	{
		try
		{
			if (_controlImpl != null)
				return _controlImpl.getImplHandle().register_resource(r);
			else
			{
				try
				{
					Coordinator coord = _control.get_coordinator();

					return coord.register_resource(r);
				}
				catch (Unavailable e2)
				{
					throw new Inactive();
				}
				catch (SystemException e3)
				{
					throw new UNKNOWN();
				}
				catch (final NullPointerException ex)
                                {
                                    throw new UNKNOWN();
                                }
			}
		}
		catch (NullPointerException e1)
		{
			// no transaction

			throw new Inactive();
		}
	}

	public final void register_subtran_aware (SubtransactionAwareResource sr)
			throws Inactive, NotSubtransaction, SystemException
	{
		try
		{
			if (_controlImpl != null)
				_controlImpl.getImplHandle().register_subtran_aware(sr);
			else
			{
				try
				{
					Coordinator coord = _control.get_coordinator();

					coord.register_subtran_aware(sr);
				}
				catch (Unavailable e2)
				{
					throw new Inactive();
				}
				catch (SystemException e3)
				{
					throw new UNKNOWN();
				}
				catch (final NullPointerException ex)
                                {
                                    throw new UNKNOWN();
                                }
			}
		}
		catch (NullPointerException e1)
		{
			// no transaction

			throw new Inactive();
		}
	}

	public final void register_synchronization (Synchronization sync)
			throws Inactive, SynchronizationUnavailable, SystemException
	{
		try
		{
			if (_controlImpl != null)
				_controlImpl.getImplHandle().register_synchronization(sync);
			else
			{
				try
				{
					Coordinator coord = _control.get_coordinator();

					coord.register_synchronization(sync);
				}
				catch (final Unavailable e2)
				{
					throw new Inactive();
				}
				catch (final Exception e3)
				{
					throw new UNKNOWN();
				}
			}
		}
		catch (final NullPointerException e1)
		{
			// not available

			throw new Inactive();
		}
	}

    public java.util.Map<Uid, String> getSynchronizations()
    {
        if ( _controlImpl.getImplHandle() == null)
            return Collections.EMPTY_MAP;
        else
            return _controlImpl.getImplHandle().getSynchronizations();    
    }

    public final org.omg.CosTransactions.Status get_status () throws SystemException
    {
        if (_controlImpl != null)
        {
            ArjunaTransactionImple tx = _controlImpl.getImplHandle();
            
            if (tx == null)
                return _controlImpl.getFinalStatus();
            else
                return tx.get_status();
        }
        else
        {
            Coordinator c = null;

            try
            {
                if (_control != null)
                    c = _control.get_coordinator();
                else
                    return org.omg.CosTransactions.Status.StatusUnknown;
            }
            catch (final OBJECT_NOT_EXIST ex)
            {
                // definitely/maybe not there so rolled back.
                
                /*
                 * If checked transactions are enabled then only the starting thread
                 * should be able to terminate the transaction. In which case, anyone
                 * else asking for the state of the transaction is either a participant
                 * or tooling/application and in which case OBJECT_NOT_EXIST means the
                 * transaction rolled back (otherwise why would the participant be asking
                 * when there's no log?) or it could have committed (and in which case the
                 * tool needs to take care of any possible ambiguity).
                 * 
                 * However, if checked transactions aren't enabled then multiple threads can
                 * terminate the transaction, meaning the ambiguity could impact at the application
                 * level and become a source of possible inconsistency. So we err on the safe side
                 * here and say "we don't really know".
                 */
                
                if (jtsPropertyManager.getJTSEnvironmentBean().isCheckedTransactions())
                    return org.omg.CosTransactions.Status.StatusRolledBack;
                else
                    return org.omg.CosTransactions.Status.StatusUnknown;
            }
            catch (final Exception e)
            {
                return org.omg.CosTransactions.Status.StatusUnknown;
            }

            try
            {
                return c.get_status();
            }
            catch (final Exception e)
            {
                // who knows?!

                return org.omg.CosTransactions.Status.StatusUnknown;
            }
        }
    }

	public final String get_transaction_name () throws SystemException
	{
		try
		{
			if (_controlImpl != null)
				return _controlImpl.getImplHandle().get_transaction_name();
			else
			{
				try
				{
					return _control.get_coordinator().get_transaction_name();
				}
				catch (Unavailable e1)
				{
					return null;
				}
				catch (SystemException e2)
				{
					throw e2;
				}
				catch (final NullPointerException ex)
                                {
                                    throw new UNKNOWN();
                                }
			}
		}
		catch (NullPointerException e3)
		{
			throw new UNKNOWN();
		}
	}

	public final Control get_control ()
			throws org.omg.CosTransactions.Unavailable, SystemException
	{
		try
		{
			if (_controlImpl != null)
				return _controlImpl.getControl();
			else
				return _control;
		}
		catch (NullPointerException e)
		{
			throw new Unavailable();
		}
	}

	public final Coordinator get_coordinator () throws SystemException,
			org.omg.CosTransactions.Unavailable
	{
		try
		{
			if (_controlImpl != null)
				return _controlImpl.get_coordinator();
			else
				return _control.get_coordinator();
		}
		catch (NullPointerException e)
		{
			throw new org.omg.CosTransactions.Unavailable();
		}
	}

	public final Terminator get_terminator () throws SystemException,
			org.omg.CosTransactions.Unavailable
	{
		try
		{
			if (_controlImpl != null)
				return _controlImpl.get_terminator();
			else
				return _control.get_terminator();
		}
		catch (NullPointerException e)
		{
			throw new org.omg.CosTransactions.Unavailable();
		}
	}

	public final int hash_transaction () throws SystemException
	{
		try
		{
			if (_controlImpl != null)
				return _controlImpl.getImplHandle().hash_transaction();
			else
			{
				try
				{
					Coordinator coord = _control.get_coordinator();

					return coord.hash_transaction();
				}
				catch (SystemException ex)
				{
					throw ex;
				}
			}
		}
		catch (Exception e)
		{
			return -1;
		}
	}

	/**
	 * Overrides Object.equals
	 * 
	 * Does not compare Uids because a foreign transaction may have been
	 * imported more than once and given different local ids.
	 */

	public boolean equals (Object e)
	{
		if (e instanceof Uid)
		{
			return _theUid.equals(e);
		}

		if (e instanceof ControlWrapper)
		{
			ControlWrapper c = (ControlWrapper) e;

			if (c.isLocal() && isLocal())
			{
				return c.getImple().equals(_controlImpl);
			}
			else
			{
				/*
				 * One of them is not local, so we have to revert to indirect
				 * comparison.
				 */

				Coordinator coord = null;

				try
				{
					coord = _control.get_coordinator();
				}
				catch (Exception e1)
				{
					return false;
				}

				Coordinator myCoord = null;

				try
				{
					myCoord = get_coordinator();
				}
				catch (Exception e2)
				{
					return false;
				}

				try
				{
					return coord.is_same_transaction(myCoord);
				}
				catch (Exception e3)
				{
				}
			}
		}

		return false;
	}

	/**
	 * Override Object.toString.
	 * 
	 * @since JTS 2.1.1.
	 */

	public String toString ()
	{
		try
		{
			return get_transaction_name() + " : " + super.toString();
		}
		catch (Exception e)
		{
			return "Invalid";
		}
	}

	/**
	 * Override Object.hashCode. We always return a positive value.
	 */

	public int hashCode ()
	{
		try
		{
			return hash_transaction();
		}
		catch (Exception e)
		{
			return -1;
		}
	}

	public final Control getControl ()
	{
		return _control;
	}

	public final ControlImple getImple ()
	{
		return _controlImpl;
	}

	public final boolean isLocal ()
	{
		return ((_controlImpl == null) ? false : true);
	}

	/**
	 * Determine whether or not we are a local control. Only do this once since
	 * locality is not likely to change!
	 */

	public final void determineLocality ()
	{
		if (!_checkedLocality)
		{
			_controlImpl = Helper.localControl(_control);

			/*
			 * Could be a proxy for a remote control, in which case we say we
			 * are remote.
			 */

			if ((_controlImpl != null)
					&& (_controlImpl.getImplHandle() == null))
			{
				_theUid = _controlImpl.get_uid();
				_controlImpl = null;
			}

			_checkedLocality = true;
		}
	}

	private Control _control;
	private ControlImple _controlImpl;
	private boolean _checkedLocality;
	private Uid _theUid;

}