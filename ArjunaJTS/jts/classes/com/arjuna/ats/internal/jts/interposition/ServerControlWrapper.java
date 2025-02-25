/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.ats.internal.jts.interposition;

import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INVALID_TRANSACTION;
import org.omg.CORBA.SystemException;
import org.omg.CosTransactions.Control;
import org.omg.CosTransactions.Coordinator;
import org.omg.CosTransactions.Inactive;
import org.omg.CosTransactions.NoTransaction;
import org.omg.CosTransactions.SubtransactionsUnavailable;
import org.omg.CosTransactions.Unavailable;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.coordinator.ActionStatus;
import com.arjuna.ats.internal.jts.ControlWrapper;
import com.arjuna.ats.internal.jts.interposition.resources.arjuna.Interposition;
import com.arjuna.ats.internal.jts.orbspecific.ControlImple;
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
 * @author Mark Little (mark.little@arjuna.com)
 * @version $Id: ServerControlWrapper.java 2342 2006-03-30 13:06:17Z  $
 * @since JTS 3.3.
 */

/*
 * We create and destroy instances of this class regularly simply because
 * otherwise we would never know.
 */

public class ServerControlWrapper extends ControlWrapper
{

	public ServerControlWrapper (Control c)
	{
		super(c);
	}

	public ServerControlWrapper (ControlImple impl)
	{
		super(impl);
	}

	public ServerControlWrapper (Control c, ControlImple impl)
	{
		super(c, impl);
	}

	public ServerControlWrapper (Control c, Uid u)
	{
		super(c, u);
	}

	/*
	 * Override some Reapable methods.
	 */

	public int cancel ()
	{
		try
		{
			Interposition.destroy(super.get_uid());

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
            jtsLogger.i18NLogger.warn_interposition_cwabort(ex);

            return ActionStatus.INVALID;
        }
	}

	public ControlWrapper create_subtransaction () throws Unavailable,
			Inactive, SubtransactionsUnavailable, SystemException
	{
		Coordinator coord = null;

		try
		{
			coord = get_coordinator();
		}
		catch (SystemException e)
		{
			coord = null;
		}

		if (coord != null)
		{
			return new ServerControlWrapper(coord.create_subtransaction());
		}
		else
		{
			if (jtsLogger.logger.isTraceEnabled()) {
                jtsLogger.logger.trace("ServerControlWrapper::create_subtransaction - subtransaction parent is inactive.");
            }

			throw new INVALID_TRANSACTION(
					ExceptionCodes.UNAVAILABLE_COORDINATOR,
					CompletionStatus.COMPLETED_NO);
		}
	}

}