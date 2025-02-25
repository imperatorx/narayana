/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.hp.mwtests.ts.jta.jts.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import jakarta.transaction.Synchronization;
import jakarta.transaction.TransactionSynchronizationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.ORBPackage.InvalidName;

import com.arjuna.ats.internal.jta.transaction.jts.TransactionSynchronizationRegistryImple;
import com.arjuna.ats.internal.jts.ORBManager;
import com.arjuna.ats.jta.common.jtaPropertyManager;
import com.arjuna.orbportability.OA;
import com.arjuna.orbportability.ORB;
import com.arjuna.orbportability.RootOA;

/**
 * Exercise the TransactionSynchronizationRegistry implementation.
 */
public class TransactionSynchronizationRegistryTest {
    private ORB myORB;
    private RootOA myOA;

    @Before
    public void setup() throws InvalidName, SystemException {
        myORB = ORB.getInstance("test");
        myOA = OA.getRootOA(myORB);

        myORB.initORB(new String[] {}, null);
        myOA.initOA();

        ORBManager.setORB(myORB);
        ORBManager.setPOA(myOA);

        jtaPropertyManager.getJTAEnvironmentBean().setTransactionManagerClassName(
                com.arjuna.ats.internal.jta.transaction.jts.TransactionManagerImple.class.getName());
        jtaPropertyManager.getJTAEnvironmentBean().setUserTransactionClassName(
                com.arjuna.ats.internal.jta.transaction.jts.UserTransactionImple.class.getName());
    }

    @After
    public void teardown() {
        myOA.destroy();
        myORB.shutdown();
    }

    @Test
    public void testTSR() throws Exception {

        jakarta.transaction.TransactionManager tm = com.arjuna.ats.jta.TransactionManager.transactionManager();

        TransactionSynchronizationRegistry tsr = new TransactionSynchronizationRegistryImple();

        assertNull(tsr.getTransactionKey());

        assertEquals(tm.getStatus(), tsr.getTransactionStatus());

        tm.begin();

        assertNotNull(tsr.getTransactionKey());
        assertEquals(tm.getStatus(), tsr.getTransactionStatus());

        String key = "key";
        Object value = new Object();
        assertNull(tsr.getResource(key));
        tsr.putResource(key, value);
        assertEquals(value, tsr.getResource(key));

        Synchronization synchronization = new com.hp.mwtests.ts.jta.jts.common.Synchronization();
        tsr.registerInterposedSynchronization(synchronization);

        assertFalse(tsr.getRollbackOnly());
        tsr.setRollbackOnly();
        assertTrue(tsr.getRollbackOnly());

        boolean gotExpectedException = false;
        try {
            tsr.registerInterposedSynchronization(synchronization);
        } catch (IllegalStateException e) {
            gotExpectedException = true;
        }
        assertTrue(gotExpectedException);

        tm.rollback();

        assertEquals(tm.getStatus(), tsr.getTransactionStatus());
    }

    @Test
    public void testTSRUseAfterCompletion() throws Exception {

        jakarta.transaction.TransactionManager tm = com.arjuna.ats.jta.TransactionManager.transactionManager();

        final CompletionCountLock ccl = new CompletionCountLock(2);
        tm.begin();
        final TransactionSynchronizationRegistry tsr = new TransactionSynchronizationRegistryImple();
        tsr.registerInterposedSynchronization(new Synchronization() {
            @Override
            public void afterCompletion(int status) {
                String key = "key";
                Object value = new Object();
                try {
                    tsr.getResource(key);
                    ccl.incrementFailedCount();
                } catch (IllegalStateException ise) {
                    // Expected
                    ccl.incrementCount();
                }
                try {
                    tsr.putResource(key, value);
                    ccl.incrementFailedCount();
                } catch (IllegalStateException ise) {
                    // Expected
                    ccl.incrementCount();
                }
            }

            @Override
            public void beforeCompletion() {
                // TODO Auto-generated method stub

            }
        });

        tm.commit();

        assertTrue(ccl.waitForCompletion());

    }

    private class CompletionCountLock {
        private int successCount;
        private int failedCount;
        private int maxCount;

        public CompletionCountLock(int maxCount) {
            this.maxCount = maxCount;
        }

        public synchronized boolean waitForCompletion() throws InterruptedException {
            while (successCount + failedCount < maxCount) {
                wait();
            }
            return failedCount == 0;
        }

        public synchronized void incrementCount() {
            this.successCount++;
            this.notify();
        }

        public synchronized void incrementFailedCount() {
            this.failedCount++;
            this.notify();
        }
    }
}