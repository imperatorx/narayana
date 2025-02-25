/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.wstx.tests.arq.basic;

import jakarta.xml.ws.soap.SOAPFaultException;

import com.arjuna.mw.wst11.UserTransaction;
import com.arjuna.mw.wst11.UserTransactionFactory;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.arjuna.mw.wst11.client.WSTXFeature;
import com.arjuna.wstx.tests.arq.WarDeployment;
import com.arjuna.wstx.tests.common.TestService;
import com.arjuna.wstx.tests.common.TestServiceAT;
import com.arjuna.wstx.tests.common.TestServiceATClient;
import com.arjuna.wstx.tests.common.TestServiceATImple;
import com.arjuna.wstx.tests.common.TestServiceClient;
import com.arjuna.wstx.tests.common.TestServiceImple;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Tests WSTXFeature and EnabledWSTXHandler with WS-AT protocol.
 *
 * <code>default-context-propagation</code> in standalone.xml has to be enabled.
 *
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
@RunWith(Arquillian.class)
public final class EnabledContextPropagationTest {

    @Deployment
    public static WebArchive getDeployment() {
        return WarDeployment.getDeployment(TestServiceAT.class, TestServiceATImple.class,
                TestServiceATClient.class, TestService.class, TestServiceImple.class, TestServiceClient.class,
                WSTXFeature.class).addAsResource("context-handlers.xml");
    }

    @After
    public void after() {
        UserTransaction userTransaction = UserTransactionFactory.userTransaction();

        rollbackIfActive(userTransaction);
    }

    /**
     * Tests commit without WSTXFeature and with enabled WSTX handler.
     *
     * Prepare and commit calls are expected.
     *
     * @throws Exception
     */
    @Test
    public void testCommitWithoutFeature() throws Exception {
        TestServiceAT client = getATClientWithoutFeature();

        beginTransaction();
        client.increment();
        commitTransaction();

        assertInvocations(client.getTwoPhaseCommitInvocations(), "prepare", "commit");
    }

    /**
     * Tests rollback without WSTXFeature and with enabled WSTX handler.
     *
     * Rollback call is expected.
     */
    @Test
    public void testRollbackWithoutFeature() {
        TestServiceAT client = getATClientWithoutFeature();

        beginTransaction();
        client.increment();
        rollbackTransaction();

        assertInvocations(client.getTwoPhaseCommitInvocations(), "rollback");
    }

    /**
     * Tests service invocation without transaction context, WSTXFeature, and with enabled WSTX handler.
     *
     * No two phase commit calls are expected.
     */
    @Test
    public void testNoTransactionWithoutFeature() {
        TestServiceAT client = getATClientWithoutFeature();

        client.increment();

        assertInvocations(client.getTwoPhaseCommitInvocations());
    }

    /**
     * Tests invocation to the non-transactional service without transaction context, WSTXFeature, and with enabled WSTX
     * handler.
     *
     * No two phase commit calls and no exception is expected.
     */
    @Test
    public void testNonTransactionalServiceWithoutFeature() {
        TestService client = getClientWithoutFeature();

        beginTransaction();
        client.increment();
        commitTransaction();
    }

    /**
     * Tests commit with enabled WSTXFeature and with enabled WSTX handler.
     *
     * Prepare and commit calls are expected.
     */
    @Test
    public void testCommitWithEnabledFeature() {
        TestServiceAT client = getATClientWithFeature(true);

        beginTransaction();
        client.increment();
        commitTransaction();

        assertInvocations(client.getTwoPhaseCommitInvocations(), "prepare", "commit");
    }

    /**
     * Tests rollback with enabled WSTXFeature and with enabled WSTX handler.
     *
     * Rollback call is expected.
     */
    @Test
    public void testRollbackWithEnabledFeature() throws Exception {
        TestServiceAT client = getATClientWithFeature(true);

        beginTransaction();
        client.increment();
        rollbackTransaction();

        assertInvocations(client.getTwoPhaseCommitInvocations(), "rollback");
    }

    /**
     * Tests service invocation without transaction context but with enabled WSTXFeature and with enabled WSTX handler.
     *
     * No two phase commit calls are expected.
     */
    @Test
    public void testNoTransactionWithEnabledFeature() {
        TestServiceAT client = getATClientWithFeature(true);

        client.increment();

        assertInvocations(client.getTwoPhaseCommitInvocations());
    }

    /**
     * Tests invocation to the non-transactional service without transaction context but with enabled WSTXFeature, and with
     * enabled WSTX handler.
     *
     * SOAPFaultException is expected.
     */
    @Test
    public void testNonTransactionalServiceWithEnabledFeature() {
        TestService client = getClientWithFeature(true);

        beginTransaction();

        try {
            client.increment();
            throw new RuntimeException("SOAPFaultException was expected");
        } catch (SOAPFaultException e) {
            // TODO for some reason <code>@Test(expected = SOAPFaultException.class)</code> did not work.
        }
    }

    /**
     * Tests commit with disabled WSTXFeature and with enabled WSTX handler.
     *
     * No two phase commit calls are expected.
     */
    @Test
    public void testCommitWithDisabledFeature() {
        TestServiceAT client = getATClientWithFeature(false);

        beginTransaction();
        client.increment();
        commitTransaction();

        assertInvocations(client.getTwoPhaseCommitInvocations());
    }

    /**
     * Tests rollback with disabled WSTXFeature and with enabled WSTX handler.
     *
     * No two phase commit calls are expected.
     */
    @Test
    public void testRollbackWithDisabledFeature() {
        TestServiceAT client = getATClientWithFeature(false);

        beginTransaction();
        client.increment();
        rollbackTransaction();

        assertInvocations(client.getTwoPhaseCommitInvocations());
    }

    /**
     * Tests service invocation without transaction context but with disabled WSTXFeature and enabled WSTX handler.
     *
     * No two phase commit calls are expected.
     */
    @Test
    public void testNoTransactionWithDisabledFeature() {
        TestServiceAT client = getATClientWithFeature(false);

        client.increment();

        assertInvocations(client.getTwoPhaseCommitInvocations());
    }

    /**
     * Tests invocation to the non-transactional service without transaction context but with disabled WSTXFeature, and with
     * enabled WSTX handler.
     *
     * No two phase commit calls and no exceptions are expected.
     */
    @Test
    public void testNonTransactionalServiceWithDisabledFeature() {
        TestService client = getClientWithFeature(false);

        beginTransaction();
        client.increment();
        commitTransaction();
    }

    /**
     * Tests service invocation using client which registers JaxWSHeaderContextProcessor with binding provider manually.
     *
     * Prepare and commit calls are expected.
     *
     * @throws InterruptedException
     */
    @Test
    public void testCommitWithFeatureUnawareClient() throws InterruptedException {
        TestServiceAT client = getATClientWithManuallyAddedHandler();

        beginTransaction();
        client.increment();
        commitTransaction();

        assertInvocations(client.getTwoPhaseCommitInvocations(), "prepare", "commit");
    }

    /**
     * Tests service invocation using client which registers JaxWSHeaderContextProcessor with binding provider manually.
     *
     * Rollback call is expected.
     *
     * @throws InterruptedException
     */
    @Test
    public void testRollbackWithFeatureUnawareClient() {
        TestServiceAT client = getATClientWithManuallyAddedHandler();

        beginTransaction();
        client.increment();
        rollbackTransaction();

        assertInvocations(client.getTwoPhaseCommitInvocations(), "rollback");
    }

    /**
     * Creates and resets WS-AT and WS-BA unaware client.
     *
     * @return TestService
     */
    private TestService getClientWithoutFeature() {
        TestService client;

        try {
            client = TestServiceClient.getClientWithoutFeature();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Client creation failed.", e);
        }

        client.reset();

        return client;
    }

    /**
     * Creates and resets WS-AT and WS-BA unaware client with WSTXFeature.
     *
     * @param isWSTXFeatureEnabled
     * @return TestServiceClient
     */
    private TestService getClientWithFeature(final boolean isWSTXFeatureEnabled) {
        TestService client;

        try {
            client = TestServiceClient.getClientWithWSTXFeature(isWSTXFeatureEnabled);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Client creation failed.", e);
        }

        client.reset();

        return client;
    }

    /**
     * Creates and resets WS-AT aware client.
     *
     * @return TestServiceAT
     */
    private TestServiceAT getATClientWithoutFeature() {
        TestServiceAT client;

        try {
            client = TestServiceATClient.getClientWithoutFeature();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Client creation failed.", e);
        }

        client.reset();

        return client;
    }

    /**
     * Creates and resets WS-AT aware client with WSTXFeature.
     *
     * @param isWSTXFeatureEnabled
     * @return TestServiceAT
     */
    private TestServiceAT getATClientWithFeature(final boolean isWSTXFeatureEnabled) {
        TestServiceAT client;

        try {
            client = TestServiceATClient.getClientWithWSTXFeature(isWSTXFeatureEnabled);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Client creation failed.", e);
        }

        client.reset();

        return client;
    }

    /**
     * Creates and resets WS-AT client with manually added handler.
     *
     * @return TestServiceAT
     */
    private TestServiceAT getATClientWithManuallyAddedHandler() {
        TestServiceAT client;

        try {
            client = TestServiceATClient.getClientWithManuallyAddedHandler();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Client creation failed.", e);
        }

        client.reset();

        return client;
    }

    /**
     * Begins WS-AT transaction.
     */
    private void beginTransaction() {
        UserTransaction userTransaction = UserTransactionFactory.userTransaction();

        try {
            userTransaction.begin();
        } catch (Exception e) {
            throw new RuntimeException("Begin transaction failed.", e);
        }
    }

    /**
     * Commits current WS-AT transaction.
     */
    private void commitTransaction() {
        UserTransaction userTransaction = UserTransactionFactory.userTransaction();

        try {
            userTransaction.commit();
        } catch (Exception e) {
            throw new RuntimeException("Commit transaction failed.", e);
        }
    }

    /**
     * Rolls back current WS-AT transaction.
     */
    private void rollbackTransaction() {
        UserTransaction userTransaction = UserTransactionFactory.userTransaction();

        try {
            userTransaction.rollback();
        } catch (Exception e) {
            throw new RuntimeException("Rollback transaction failed.", e);
        }
    }

    /**
     * Rolls back given transaction if it's active.
     *
     * @param userTransaction
     */
    private void rollbackIfActive(UserTransaction userTransaction) {
        try {
            userTransaction.rollback();
        } catch (Throwable t) {
        }
    }

    /**
     * Compares and asserts two invocation lists.
     *
     * @param actual
     * @param expected
     */
    private void assertInvocations(List<String> actual, String... expected) {
        Assert.assertArrayEquals(expected, actual.toArray());
    }

}