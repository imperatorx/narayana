/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.hp.mwtests.ts.jta.commitmarkable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.util.Enumeration;
import java.util.Vector;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.h2.jdbcx.JdbcDataSource;
import org.jboss.byteman.contrib.bmunit.BMScript;
import org.jboss.byteman.contrib.bmunit.BMUnitRunner;
import org.jboss.byteman.rule.exception.ExecuteException;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.arjuna.ats.arjuna.recovery.RecoveryModule;
import com.arjuna.ats.internal.jta.recovery.arjunacore.CommitMarkableResourceRecordRecoveryModule;
import com.arjuna.ats.internal.jta.recovery.arjunacore.XARecoveryModule;
import com.arjuna.ats.jta.recovery.XAResourceRecoveryHelper;

@RunWith(BMUnitRunner.class)
public class TestCommitMarkableResourceFailActivate extends
		TestCommitMarkableResourceBase {

	JDBCConnectableResource nonXAResource;
	boolean failed = false;

	protected SimpleXAResource xaResource;

	@Test
	@BMScript("commitMarkableResourceFailAfterCommit")
	public void testFailAfterPrepare() throws Exception {
		final DataSource dataSource = new JdbcDataSource();
		((JdbcDataSource) dataSource)
				.setURL("jdbc:h2:mem:JBTMDB;DB_CLOSE_DELAY=-1");

		// Test code
		Utils.createTables(dataSource.getConnection());

		// We can't just instantiate one as we need to be using the
		// same one as
		// the transaction
		// manager would have used to mark the transaction for GC
		CommitMarkableResourceRecordRecoveryModule recoveryModule = null;
		Vector recoveryModules = manager.getModules();
		if (recoveryModules != null) {
			Enumeration modules = recoveryModules.elements();

			while (modules.hasMoreElements()) {
				RecoveryModule m = (RecoveryModule) modules.nextElement();

				if (m instanceof CommitMarkableResourceRecordRecoveryModule) {
					recoveryModule = (CommitMarkableResourceRecordRecoveryModule) m;
				} else if (m instanceof XARecoveryModule) {
                    XARecoveryModule  xarm = (XARecoveryModule) m;
                    xarm.addXAResourceRecoveryHelper(new XAResourceRecoveryHelper() {
                        public boolean initialise(String p) throws Exception {
                            return true;
                        }

                        public XAResource[] getXAResources() throws Exception {
                            return new XAResource[] {xaResource};
                        }
                    });
                }
			}
		}
		// final Object o = new Object();
		// synchronized (o) {

		Thread foo = new Thread(new Runnable() {

			public void run() {

				try {
					jakarta.transaction.TransactionManager tm = com.arjuna.ats.jta.TransactionManager
							.transactionManager();

					tm.begin();

					Connection localJDBCConnection = dataSource.getConnection();
					localJDBCConnection.setAutoCommit(false);
					nonXAResource = new JDBCConnectableResource(
							localJDBCConnection);
					tm.getTransaction().enlistResource(nonXAResource);

					xaResource = new SimpleXAResource();
					tm.getTransaction().enlistResource(xaResource);

					localJDBCConnection.createStatement().execute(
							"INSERT INTO foo (bar) VALUES (1)");

					tm.commit();
				} catch (ExecuteException t) {
				} catch (Exception t) {
					t.printStackTrace();
					failed = true;
				} catch (Error t) {
				}
			}
		});
		foo.start();
		foo.join();

		assertFalse(failed);

		// Now we need to correctly complete the transaction
		manager.scan();

		assertFalse(xaResource.wasCommitted());
		assertFalse(xaResource.wasRolledback());

		// This is test code, it allows us to verify that the
		// correct XID was
		// removed
		Xid committed = ((JDBCConnectableResource) nonXAResource)
				.getStartedXid();
		assertNotNull(committed);
		// The recovery module has to perform lookups
		new InitialContext().rebind("commitmarkableresource", dataSource);

		// Run the first pass it will load the committed Xids into so we can
		// indepently verify that the item was committed
		recoveryModule.periodicWorkFirstPass();
		recoveryModule.periodicWorkSecondPass();
		assertTrue(recoveryModule.wasCommitted("commitmarkableresource",
				committed));

		// Run the scan to clear the content
		manager.scan();

		assertTrue(xaResource.wasCommitted());
		assertFalse(xaResource.wasRolledback());

		// Make sure that the resource was GC'd by the CRRRM
		assertTrue(recoveryModule.wasCommitted("commitmarkableresource",
				committed));
		recoveryModule.periodicWorkFirstPass();
		assertTrue(recoveryModule.wasCommitted("commitmarkableresource",
				committed));
		// This is the pass that will delete it
		recoveryModule.periodicWorkSecondPass();
		assertFalse(recoveryModule.wasCommitted("commitmarkableresource",
				committed));
	}
}