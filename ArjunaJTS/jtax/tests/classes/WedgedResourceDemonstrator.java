/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */


import static org.junit.Assert.fail;

import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.TransactionManager;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.junit.Test;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.ORBPackage.InvalidName;

import com.arjuna.ats.arjuna.recovery.RecoveryManager;
import com.arjuna.orbportability.OA;
import com.arjuna.orbportability.ORB;
import com.arjuna.orbportability.RootOA;

public class WedgedResourceDemonstrator {

	@Test
	public void testWedge() throws InvalidName, SystemException,
			NotSupportedException, jakarta.transaction.SystemException,
			IllegalStateException, RollbackException, SecurityException,
			HeuristicMixedException, HeuristicRollbackException,
			InterruptedException {

		String mode = "jts";
		if (mode.equals("jts")) {
			ORB myORB = ORB.getInstance("test");
			RootOA myOA = OA.getRootOA(myORB);

			myORB.initORB(new String[0], null);
			myOA.initOA();

			com.arjuna.ats.internal.jts.ORBManager.setORB(myORB);
			com.arjuna.ats.internal.jts.ORBManager.setPOA(myOA);

			RecoveryManager.manager().initialize();
		}

		TransactionManager transactionManager = mode.equals("jts") ? new com.arjuna.ats.internal.jta.transaction.jts.TransactionManagerImple()
				: new com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionManagerImple();
		transactionManager.setTransactionTimeout(2);
		transactionManager.begin();
		transactionManager.getTransaction().enlistResource(
				new TimeoutOnFirstRollbackResource());

		// Business logic
		Thread.currentThread().sleep(5000);

		try {
			transactionManager.commit();
			fail("Should not have been able to commit");
		} catch (RollbackException e) {
			// This is fine
		} catch (IllegalStateException e) {
			// This is fine
		} finally {
			if (mode.equals("jts")) {
				RecoveryManager.manager().terminate();

				ORB myORB = ORB.getInstance("test");
				RootOA myOA = OA.getRootOA(myORB);
				myOA.destroy();
				myORB.shutdown();
			}
		}
	}

	private static class TimeoutOnFirstRollbackResource implements XAResource {

		public void rollback(Xid arg0) throws XAException {
			synchronized (this) {
				long initialTime = System.currentTimeMillis();
				try {
					// This would wait forever in theory, I have reduced it
					// just so the app will be able to clean up
					this.wait(7000);
				} catch (InterruptedException e) {
					throw new NullPointerException(
							"Interrupted, simulating jacorb");
				}
			}
		}

		public void commit(Xid arg0, boolean arg1) throws XAException {
		}

		public void end(Xid arg0, int arg1) throws XAException {
		}

		public void forget(Xid arg0) throws XAException {
		}

		public int getTransactionTimeout() throws XAException {
			return 0;
		}

		public boolean isSameRM(XAResource arg0) throws XAException {
			return false;
		}

		public int prepare(Xid arg0) throws XAException {
			return 0;
		}

		public Xid[] recover(int arg0) throws XAException {
			return null;
		}

		public boolean setTransactionTimeout(int arg0) throws XAException {
			return false;
		}

		public void start(Xid arg0, int arg1) throws XAException {
		}
	}
}