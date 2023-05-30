/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.jboss.jbossts.qa.junit.testgroup;

import org.jboss.jbossts.qa.junit.*;
import org.junit.*;

// Automatically generated by XML2JUnit
public class TestGroup_jdbcresources03_oracle_thin_jndi extends TestGroupBase
{
	public String getTestGroupName()
	{
		return "jdbcresources03_oracle_thin_jndi";
	}

	protected Task server0 = null;

	@Before public void setUp()
	{
		super.setUp();
		Task setup = createTask("setup", org.jboss.jbossts.qa.Utils.JNDIManager.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup.perform("DB_THIN_JNDI");
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources03Setups.Setup01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform("2", "DB_THIN_JNDI");
		server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
	}

	@After public void tearDown()
	{
		try {
			server0.terminate();
		Task task0 = createTask("task0", org.jboss.jbossts.qa.Utils.RemoveServerIORStore.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		task0.perform("$(1)");
		Task cleanup = createTask("cleanup", org.jboss.jbossts.qa.JDBCResources03Cleanups.Cleanup01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		cleanup.perform("DB_THIN_JNDI");
		} finally {
			super.tearDown();
		}
	}

	@Test public void JDBCResources03_Oracle_thin_jndi_Test01()
	{
		setTestName("Test01");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources03Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("DB_THIN_JNDI", "$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources03Clients.Client01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)");
		Task client1 = createTask("client1", org.jboss.jbossts.qa.JDBCResources03Clients.Client01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client1.start("$(1)");
		client0.waitFor();
		client1.waitFor();
		server1.terminate();
	}

	@Test public void JDBCResources03_Oracle_thin_jndi_Test02()
	{
		setTestName("Test02");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources03Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("DB_THIN_JNDI", "$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.JDBCResources03Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server2.start("DB_THIN_JNDI", "$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources03Clients.Client01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)");
		Task client1 = createTask("client1", org.jboss.jbossts.qa.JDBCResources03Clients.Client01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client1.start("$(2)");
		client0.waitFor();
		client1.waitFor();
		server2.terminate();
		server1.terminate();
	}

	@Test public void JDBCResources03_Oracle_thin_jndi_Test03()
	{
		setTestName("Test03");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources03Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("DB_THIN_JNDI", "$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources03Clients.Client01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)");
		Task client1 = createTask("client1", org.jboss.jbossts.qa.JDBCResources03Clients.Client01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client1.start("$(1)");
		client0.waitFor();
		client1.waitFor();
		server1.terminate();
	}

	@Test public void JDBCResources03_Oracle_thin_jndi_Test04()
	{
		setTestName("Test04");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources03Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("DB_THIN_JNDI", "$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.JDBCResources03Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server2.start("DB_THIN_JNDI", "$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources03Clients.Client01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)");
		Task client1 = createTask("client1", org.jboss.jbossts.qa.JDBCResources03Clients.Client01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client1.start("$(2)");
		client0.waitFor();
		client1.waitFor();
		server2.terminate();
		server1.terminate();
	}

	@Test public void JDBCResources03_Oracle_thin_jndi_Test05()
	{
		setTestName("Test05");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources03Servers.Server03.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("DB_THIN_JNDI", "$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources03Clients.Client01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)");
		Task client1 = createTask("client1", org.jboss.jbossts.qa.JDBCResources03Clients.Client01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client1.start("$(1)");
		client0.waitFor();
		client1.waitFor();
		server1.terminate();
	}

	@Test public void JDBCResources03_Oracle_thin_jndi_Test06()
	{
		setTestName("Test06");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources03Servers.Server03.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("DB_THIN_JNDI", "$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.JDBCResources03Servers.Server03.class, Task.TaskType.EXPECT_READY, 480);
		server2.start("DB_THIN_JNDI", "$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources03Clients.Client01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)");
		Task client1 = createTask("client1", org.jboss.jbossts.qa.JDBCResources03Clients.Client01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client1.start("$(2)");
		client0.waitFor();
		client1.waitFor();
		server2.terminate();
		server1.terminate();
	}

}