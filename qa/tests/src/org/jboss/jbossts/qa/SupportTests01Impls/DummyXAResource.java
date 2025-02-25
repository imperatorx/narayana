/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.jboss.jbossts.qa.SupportTests01Impls;

import org.jboss.jbossts.qa.RawResources01.*;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.io.Serializable;


public class DummyXAResource implements XAResource, Serializable
{

	public void DummyXA()
	{
		_timeout = 0;  // no timeout
	}

	public void commit(Xid xid, boolean onePhase) throws XAException
	{
		System.err.println("DummyXA.commit called");

		if (_lastCalled == EndLastCalled)
		{
			_lastCalled = CommitLastCalledError;
		}
		else
		{
			_lastCalled = CommitLastCalled;
		}
	}

	public void clearLastCalled()
	{
		_lastCalled = Nothing;
	}

	public void start(Xid xid, int flags) throws XAException
	{
		System.err.println("DummyXA.start called");

		_lastCalled = StartLastCalled;
	}

	public void end(Xid xid, int flags) throws XAException
	{
		System.err.println("DummyXA.end called");

		_lastCalled = EndLastCalled;
	}

	public void forget(Xid xid) throws XAException
	{
		System.err.println("DummyXA.forget called");

		_lastCalled = ForgetLastCalled;
	}

	public int getTransactionTimeout() throws XAException
	{
		System.err.println("DummyXA.getTransactionTimeout called");

		return _timeout;
	}

	public Xid[] recover(int flag) throws XAException
	{
		System.err.println("DummyXA.recover called");

		_lastCalled = RecoverLastCalled;

		return null;
	}

	public int prepare(Xid xid) throws XAException
	{
		System.err.println("DummyXA.prepare called");

		_lastCalled = PrepareLastCalled;

		return XAResource.XA_OK;
	}

	public void rollback(Xid xid) throws XAException
	{
		System.err.println("DummyXA.rollback called");

		_lastCalled = RollbackLastCalled;
	}

	public boolean setTransactionTimeout(int seconds) throws XAException
	{
		System.err.println("DummyXA.setTransactionTimeout called");

		_timeout = seconds;

		return true;
	}

	public boolean isSameRM(XAResource xares) throws XAException
	{
		System.err.println("DummyXA.isSameRM called");

		return (xares == this);
	}

	public int getLastCalled()
	{
		return _lastCalled;
	}

	public String getLastCalledString()
	{
		return _lastCalledString[_lastCalled];
	}

	private int _timeout;
	private boolean _donePrepare = false;
	private int _lastCalled = Nothing;

	public final static int StartLastCalled = 0,
			EndLastCalled = 1,
			PrepareLastCalled = 2,
			CommitLastCalled = 3,
			RollbackLastCalled = 4,
			RecoverLastCalled = 5,
			ForgetLastCalled = 6,
			CommitLastCalledError = 7,
			Nothing = 8;

	private final static String _lastCalledString[] = {"StartLastCalled", "EndLastCalled", "PrepareLastCalled",
			"CommitLastCalled", "RollbackLastCalled", "RecoverLastCalled",
			"ForgetLastCalled", "CommitLastCalledError", "Nothing"};
};