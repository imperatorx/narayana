/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.jboss.jbossts.qa.junit.testgroup;

import org.jboss.jbossts.qa.junit.*;
import org.junit.*;

// Automatically generated by XML2JUnit
public class TestGroup_perfprofile01_i extends TestGroupBase
{
	public String getTestGroupName()
	{
		return "perfprofile01_i_ait01_implicitobject_notran";
	}

	protected Task server0 = null;

	@Before public void setUp()
	{
		super.setUp();
		server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
	}

	@After public void tearDown()
	{
		try {
			server0.terminate();
		Task task0 = createTask("task0", org.jboss.jbossts.qa.Utils.RemoveServerIORStore.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		task0.perform("$(1)");
		} finally {
			super.tearDown();
		}
	}
 
	@Test public void PerfProfile01_I_AIT01_ImplicitObject_NoTran_NoTranNullOper()
	{
		setTestName("AIT01_ImplicitObject_NoTran_NoTranNullOper");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.PerfProfile01Servers.Server_AIT01_ImplicitObject.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.PerfProfile01Clients.Client_ImplicitObject_NoTran_NoTranNullOper.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("AIT01", "10000", "$(1)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void PerfProfile01_I_AIT01_ImplicitObject_NoTran_TranCommitNullOper()
	{
		setTestName("AIT01_ImplicitObject_NoTran_TranCommitNullOper");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.PerfProfile01Servers.Server_AIT01_ImplicitObject.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.PerfProfile01Clients.Client_ImplicitObject_NoTran_TranCommitNullOper.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("AIT01", "1000", "$(1)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void PerfProfile01_I_AIT01_ImplicitObject_NoTran_TranCommitReadLock()
	{
		setTestName("AIT01_ImplicitObject_NoTran_TranCommitReadLock");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.PerfProfile01Servers.Server_AIT01_ImplicitObject.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.PerfProfile01Clients.Client_ImplicitObject_NoTran_TranCommitReadLock.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("AIT01", "1000", "$(1)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void PerfProfile01_I_AIT01_ImplicitObject_NoTran_TranCommitWriteLock()
	{
		setTestName("AIT01_ImplicitObject_NoTran_TranCommitWriteLock");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.PerfProfile01Servers.Server_AIT01_ImplicitObject.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.PerfProfile01Clients.Client_ImplicitObject_NoTran_TranCommitWriteLock.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("AIT01", "1000", "$(1)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void PerfProfile01_I_AIT01_ImplicitObject_NoTran_TranRollbackNullOper()
	{
		setTestName("AIT01_ImplicitObject_NoTran_TranRollbackNullOper");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.PerfProfile01Servers.Server_AIT01_ImplicitObject.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.PerfProfile01Clients.Client_ImplicitObject_NoTran_TranRollbackNullOper.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("AIT01", "1000", "$(1)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void PerfProfile01_I_AIT01_ImplicitObject_NoTran_TranRollbackReadLock()
	{
		setTestName("AIT01_ImplicitObject_NoTran_TranRollbackReadLock");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.PerfProfile01Servers.Server_AIT01_ImplicitObject.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.PerfProfile01Clients.Client_ImplicitObject_NoTran_TranRollbackReadLock.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("AIT01", "1000", "$(1)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void PerfProfile01_I_AIT01_ImplicitObject_NoTran_TranRollbackWriteLock()
	{
		setTestName("AIT01_ImplicitObject_NoTran_TranRollbackWriteLock");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.PerfProfile01Servers.Server_AIT01_ImplicitObject.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.PerfProfile01Clients.Client_ImplicitObject_NoTran_TranRollbackWriteLock.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("AIT01", "1000", "$(1)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void PerfProfile01_I_AIT01_ImplicitObject_TranCommit_NoTranNullOper()
	{
		setTestName("AIT01_ImplicitObject_TranCommit_NoTranNullOper");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.PerfProfile01Servers.Server_AIT01_ImplicitObject.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.PerfProfile01Clients.Client_ImplicitObject_TranCommit_NoTranNullOper.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("AIT01", "1000", "$(1)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void PerfProfile01_I_AIT01_ImplicitObject_TranCommit_NoTranReadLock()
	{
		setTestName("AIT01_ImplicitObject_TranCommit_NoTranReadLock");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.PerfProfile01Servers.Server_AIT01_ImplicitObject.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.PerfProfile01Clients.Client_ImplicitObject_TranCommit_NoTranReadLock.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("AIT01", "1000", "$(1)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void PerfProfile01_I_AIT01_ImplicitObject_TranCommit_NoTranWriteLock()
	{
		setTestName("AIT01_ImplicitObject_TranCommit_NoTranWriteLock");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.PerfProfile01Servers.Server_AIT01_ImplicitObject.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.PerfProfile01Clients.Client_ImplicitObject_TranCommit_NoTranWriteLock.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("AIT01", "1000", "$(1)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void PerfProfile01_I_AIT01_ImplicitObject_TranCommit_TranCommitNullOper()
	{
		setTestName("AIT01_ImplicitObject_TranCommit_TranCommitNullOper");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.PerfProfile01Servers.Server_AIT01_ImplicitObject.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.PerfProfile01Clients.Client_ImplicitObject_TranCommit_TranCommitNullOper.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("AIT01", "1000", "$(1)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void PerfProfile01_I_AIT01_ImplicitObject_TranCommit_TranCommitReadLock()
	{
		setTestName("AIT01_ImplicitObject_TranCommit_TranCommitReadLock");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.PerfProfile01Servers.Server_AIT01_ImplicitObject.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.PerfProfile01Clients.Client_ImplicitObject_TranCommit_TranCommitReadLock.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("AIT01", "1000", "$(1)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void PerfProfile01_I_AIT01_ImplicitObject_TranCommit_TranCommitWriteLock()
	{
		setTestName("AIT01_ImplicitObject_TranCommit_TranCommitWriteLock");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.PerfProfile01Servers.Server_AIT01_ImplicitObject.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.PerfProfile01Clients.Client_ImplicitObject_TranCommit_TranCommitWriteLock.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("AIT01", "1000", "$(1)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void PerfProfile01_I_AIT01_ImplicitObject_TranCommit_TranRollbackNullOper()
	{
		setTestName("AIT01_ImplicitObject_TranCommit_TranRollbackNullOper");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.PerfProfile01Servers.Server_AIT01_ImplicitObject.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.PerfProfile01Clients.Client_ImplicitObject_TranCommit_TranRollbackNullOper.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("AIT01", "1000", "$(1)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void PerfProfile01_I_AIT01_ImplicitObject_TranCommit_TranRollbackReadLock()
	{
		setTestName("AIT01_ImplicitObject_TranCommit_TranRollbackReadLock");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.PerfProfile01Servers.Server_AIT01_ImplicitObject.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.PerfProfile01Clients.Client_ImplicitObject_TranCommit_TranRollbackReadLock.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("AIT01", "1000", "$(1)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void PerfProfile01_I_AIT01_ImplicitObject_TranCommit_TranRollbackWriteLock()
	{
		setTestName("AIT01_ImplicitObject_TranCommit_TranRollbackWriteLock");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.PerfProfile01Servers.Server_AIT01_ImplicitObject.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.PerfProfile01Clients.Client_ImplicitObject_TranCommit_TranRollbackWriteLock.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("AIT01", "1000", "$(1)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void PerfProfile01_I_AIT01_ImplicitObject_TranRollback_NoTranNullOper()
	{
		setTestName("AIT01_ImplicitObject_TranRollback_NoTranNullOper");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.PerfProfile01Servers.Server_AIT01_ImplicitObject.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.PerfProfile01Clients.Client_ImplicitObject_TranRollback_NoTranNullOper.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("AIT01", "1000", "$(1)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void PerfProfile01_I_AIT01_ImplicitObject_TranRollback_NoTranReadLock()
	{
		setTestName("AIT01_ImplicitObject_TranRollback_NoTranReadLock");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.PerfProfile01Servers.Server_AIT01_ImplicitObject.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.PerfProfile01Clients.Client_ImplicitObject_TranRollback_NoTranReadLock.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("AIT01", "1000", "$(1)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void PerfProfile01_I_AIT01_ImplicitObject_TranRollback_NoTranWriteLock()
	{
		setTestName("AIT01_ImplicitObject_TranRollback_NoTranWriteLock");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.PerfProfile01Servers.Server_AIT01_ImplicitObject.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.PerfProfile01Clients.Client_ImplicitObject_TranRollback_NoTranWriteLock.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("AIT01", "1000", "$(1)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void PerfProfile01_I_AIT01_ImplicitObject_TranRollback_TranCommitNullOper()
	{
		setTestName("AIT01_ImplicitObject_TranRollback_TranCommitNullOper");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.PerfProfile01Servers.Server_AIT01_ImplicitObject.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.PerfProfile01Clients.Client_ImplicitObject_TranRollback_TranCommitNullOper.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("AIT01", "1000", "$(1)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void PerfProfile01_I_AIT01_ImplicitObject_TranRollback_TranCommitReadLock()
	{
		setTestName("AIT01_ImplicitObject_TranRollback_TranCommitReadLock");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.PerfProfile01Servers.Server_AIT01_ImplicitObject.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.PerfProfile01Clients.Client_ImplicitObject_TranRollback_TranCommitReadLock.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("AIT01", "1000", "$(1)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void PerfProfile01_I_AIT01_ImplicitObject_TranRollback_TranCommitWriteLock()
	{
		setTestName("AIT01_ImplicitObject_TranRollback_TranCommitWriteLock");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.PerfProfile01Servers.Server_AIT01_ImplicitObject.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.PerfProfile01Clients.Client_ImplicitObject_TranRollback_TranCommitWriteLock.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("AIT01", "1000", "$(1)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void PerfProfile01_I_AIT01_ImplicitObject_TranRollback_TranRollbackNullOper()
	{
		setTestName("AIT01_ImplicitObject_TranRollback_TranRollbackNullOper");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.PerfProfile01Servers.Server_AIT01_ImplicitObject.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.PerfProfile01Clients.Client_ImplicitObject_TranRollback_TranRollbackNullOper.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("AIT01", "1000", "$(1)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void PerfProfile01_I_AIT01_ImplicitObject_TranRollback_TranRollbackReadLock()
	{
		setTestName("AIT01_ImplicitObject_TranRollback_TranRollbackReadLock");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.PerfProfile01Servers.Server_AIT01_ImplicitObject.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.PerfProfile01Clients.Client_ImplicitObject_TranRollback_TranRollbackReadLock.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("AIT01", "1000", "$(1)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void PerfProfile01_I_AIT01_ImplicitObject_TranRollback_TranRollbackWriteLock()
	{
		setTestName("AIT01_ImplicitObject_TranRollback_TranRollbackWriteLock");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.PerfProfile01Servers.Server_AIT01_ImplicitObject.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.PerfProfile01Clients.Client_ImplicitObject_TranRollback_TranRollbackWriteLock.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("AIT01", "1000", "$(1)");
		client0.waitFor();
		server1.terminate();
	}

    // disable and use the jts remote equivalent
    public void PerfProfile01_JTSRemote_PerfTest() {
        String numberOfCalls = System.getProperty("testgroup.jtsremote.perftest.numberOfCalls", "10000");
        String threadCount = System.getProperty("testgroup.jtsremote.perftest.numberOfThreads", "20");
        String batchSize = System.getProperty("testgroup.jtsremote.perftest.batchSize", "100");

        Task server1 = createTask("server1", com.hp.mwtests.ts.jts.remote.servers.GridServer.class, Task.TaskType.EXPECT_READY, 960);
        server1.start("$(1)");

        startAndWaitForClient(com.hp.mwtests.ts.jts.remote.hammer.PerfHammer.class, "$(1)", numberOfCalls, threadCount, batchSize);

        server1.terminate();
    }
}