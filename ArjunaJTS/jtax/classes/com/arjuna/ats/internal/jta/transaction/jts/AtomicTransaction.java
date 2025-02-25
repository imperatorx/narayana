/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.ats.internal.jta.transaction.jts;

import javax.transaction.xa.Xid;

import org.omg.CORBA.SystemException;
import org.omg.CORBA.TRANSACTION_ROLLEDBACK;
import org.omg.CORBA.WrongTransaction;
import org.omg.CosTransactions.HeuristicHazard;
import org.omg.CosTransactions.HeuristicMixed;
import org.omg.CosTransactions.NoTransaction;
import org.omg.CosTransactions.Status;
import org.omg.CosTransactions.Unavailable;

import com.arjuna.ats.arjuna.coordinator.TransactionReaper;
import com.arjuna.ats.internal.jta.utils.jtaxLogger;
import com.arjuna.ats.internal.jta.utils.jts.XidUtils;
import com.arjuna.ats.internal.jts.ControlWrapper;
import com.arjuna.ats.internal.jts.orbspecific.ControlImple;

/**
 * An extension of the AtomicTransaction class so we can create new instances
 * without having to create a new transaction. Useful for when we work with
 * imported transactions.
 * 
 * @author Mark Little (mark_little@hp.com)
 * @version $Id: AtomicTransaction.java 2342 2006-03-30 13:06:17Z  $
 * @since JTS 3.0.
 */

public class AtomicTransaction extends
		com.arjuna.ats.jts.extensions.AtomicTransaction
{

	public AtomicTransaction ()
	{
		super();
	}

	public AtomicTransaction (ControlWrapper tx)
	{
		super(tx);
	}

	/*
	 * public synchronized void begin () throws SubtransactionsUnavailable,
	 * SystemException { if (jtaxLogger.loggerI18N.isWarnEnabled()) {
	 * jtaxLogger.loggerI18N.warn("com.arjuna.ats.internal.jta.transaction.jts.atomictxnobegin"); }
	 * 
	 * throw new INVALID_TRANSACTION(ExceptionCodes.ALREADY_BEGUN,
	 * CompletionStatus.COMPLETED_NO); }
	 */

	/**
	 * Does not change thread-to-tx association as base class commit does.
	 */

	public synchronized void end (boolean report_heuristics)
			throws NoTransaction, HeuristicMixed, HeuristicHazard,
			WrongTransaction, SystemException
	{
		if (jtaxLogger.logger.isTraceEnabled()) {
            jtaxLogger.logger.trace("AtomicTransaction::end ( "
                    + report_heuristics + " ) for " + _theAction);
        }

		if (_theAction == null)
		{
			throw new NoTransaction();
		}

		try
		{
			_theAction.commit(report_heuristics);

			_theStatus = Status.StatusCommitted;
		}
		catch (Unavailable e)
		{
			_theStatus = Status.StatusNoTransaction;

			throw new NoTransaction();
		}
		catch (HeuristicMixed e)
		{
			_theStatus = getStatus();

			throw e;
		}
		catch (HeuristicHazard e)
		{
			_theStatus = getStatus();

			throw e;
		}
		catch (TRANSACTION_ROLLEDBACK e)
		{
			_theStatus = Status.StatusRolledBack;

			throw e;
		}
		catch (SystemException e)
		{
			_theStatus = getStatus();

			throw e;
		}
		finally
		{
			// now remove it from the reaper
			
			TransactionReaper.transactionReaper().remove(_theAction);
		}
	}

	/**
	 * Does not change thread-to-tx association as base class rollback does.
	 */

	public synchronized void abort () throws NoTransaction, WrongTransaction,
			SystemException
	{
		if (jtaxLogger.logger.isTraceEnabled()) {
            jtaxLogger.logger.trace("AtomicTransaction::abort for "
                    + _theAction);
        }

		if (_theAction == null)
		{
			throw new NoTransaction();
		}

		try
		{
			_theAction.rollback();
		}
		catch (Unavailable e)
		{
			_theStatus = Status.StatusNoTransaction;  // unknown?

			throw new NoTransaction();
		}
		catch (TRANSACTION_ROLLEDBACK e)
		{
			_theStatus = Status.StatusRolledBack;

			throw e;
		}
		catch (SystemException e)
		{
			_theStatus = getStatus();

			throw e;
		}
		finally
		{
			// now remove it from the reaper
			
			TransactionReaper.transactionReaper().remove(_theAction);
		}
	}

	public final ControlWrapper getControlWrapper ()
	{
		return _theAction;
	}

	public final Xid get_xid (boolean branch) throws SystemException
	{
        ControlImple controlImple = _theAction.getImple();
        if (controlImple != null) {
            return XidUtils.getXid(controlImple.get_uid(), branch);
        } else {
            return XidUtils.getXid(_theAction.getControl(), branch);
        }
    }
}