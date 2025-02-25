/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.jboss.jbossts.qa.junit.testgroup;

import org.jboss.jbossts.qa.junit.*;
import org.junit.*;

public class TestGroup_jdbcresources01_abstract extends TestGroupBase
{
	public String getTestGroupName()
	{
		return "jdbcresources01_abstract";
	}

    public String getDBName1() {
        return "THIS_DB_NAME_MUST_BE_OVERRIDDEN";
    }
    
    public String getDBName2() {
        return "THIS_DB_NAME_MUST_BE_OVERRIDDEN";
    }
        
	@Before public void setUp()
	{
		super.setUp();
		Task setup = createTask("setup", org.jboss.jbossts.qa.Utils.JNDIManager.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup.perform(getDBName1());
	}

	@After public void tearDown()
	{
		try {
		Task task0 = createTask("task0", org.jboss.jbossts.qa.Utils.RemoveServerIORStore.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		task0.perform("$(1)");
		Task cleanup = createTask("cleanup", org.jboss.jbossts.qa.JDBCResources01Cleanups.Cleanup01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		cleanup.perform(getDBName1());
		} finally {
			super.tearDown();
		}
	}

	@Test public void JDBCResources01_abstract_Test001()
	{
		setTestName("Test001");
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)");
		client0.waitFor();
		server1.terminate();
		server0.terminate();
	}

	@Test public void JDBCResources01_abstract_Test002()
	{
		setTestName("Test002");
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)");
		client0.waitFor();
		server1.terminate();
		server0.terminate();
	}

	@Test public void JDBCResources01_abstract_Test003()
	{
		setTestName("Test003");
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client03.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)");
		client0.waitFor();
		server1.terminate();
		server0.terminate();
	}

	@Test public void JDBCResources01_abstract_Test004()
	{
		setTestName("Test004");
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client04.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)");
		client0.waitFor();
		server1.terminate();
		server0.terminate();
	}

	@Test public void JDBCResources01_abstract_Test007()
	{
		setTestName("Test007");
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)");
		client0.waitFor();
		server1.terminate();
		server0.terminate();
	}

	@Test public void JDBCResources01_abstract_Test008()
	{
		setTestName("Test008");
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)");
		client0.waitFor();
		server1.terminate();
		server0.terminate();
	}

	@Test public void JDBCResources01_abstract_Test009()
	{
		setTestName("Test009");
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client03.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)");
		client0.waitFor();
		server1.terminate();
		server0.terminate();
	}

	@Test public void JDBCResources01_abstract_Test010()
	{
		setTestName("Test010");
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client04.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)");
		client0.waitFor();
		server1.terminate();
		server0.terminate();
	}

	@Test public void JDBCResources01_abstract_Test011()
	{
		setTestName("Test011");
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client05.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)");
		client0.waitFor();
		server1.terminate();
		server0.terminate();
	}

	@Test public void JDBCResources01_abstract_Test012()
	{
		setTestName("Test012");
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client06.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)");
		client0.waitFor();
		server1.terminate();
		server0.terminate();
	}

	@Test public void JDBCResources01_abstract_Test013()
	{
		setTestName("Test013");
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.JDBCResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server2.start(getDBName1(), "$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client07.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
		server0.terminate();
	}

	@Test public void JDBCResources01_abstract_Test014()
	{
		setTestName("Test014");
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.JDBCResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server2.start(getDBName1(), "$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client08.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
		server0.terminate();
	}

	@Test public void JDBCResources01_abstract_Test015()
	{
		setTestName("Test015");
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.JDBCResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server2.start(getDBName1(), "$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client09.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
		server0.terminate();
	}

	@Test public void JDBCResources01_abstract_Test016()
	{
		setTestName("Test016");
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.JDBCResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server2.start(getDBName1(), "$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client10.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
		server0.terminate();
	}

	@Test public void JDBCResources01_abstract_Test017()
	{
		setTestName("Test017");
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.JDBCResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server2.start(getDBName1(), "$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client11.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
		server0.terminate();
	}

	@Test public void JDBCResources01_abstract_Test018()
	{
		setTestName("Test018");
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.JDBCResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server2.start(getDBName1(), "$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client07.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
		server0.terminate();
	}

	@Test public void JDBCResources01_abstract_Test019()
	{
		setTestName("Test019");
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.JDBCResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server2.start(getDBName1(), "$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client08.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
		server0.terminate();
	}

	@Test public void JDBCResources01_abstract_Test020()
	{
		setTestName("Test020");
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.JDBCResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server2.start(getDBName1(), "$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client09.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
		server0.terminate();
	}

	@Test public void JDBCResources01_abstract_Test021()
	{
		setTestName("Test021");
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.JDBCResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server2.start(getDBName1(), "$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client10.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
		server0.terminate();
	}

	@Test public void JDBCResources01_abstract_Test022()
	{
		setTestName("Test022");
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.JDBCResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server2.start(getDBName1(), "$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client11.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
		server0.terminate();
	}

	@Test public void JDBCResources01_abstract_Test023()
	{
		setTestName("Test023");
		Task setup2 = createTask("setup2", org.jboss.jbossts.qa.Utils.JNDIManager.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup2.perform(getDBName2());
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task setup3 = createTask("setup3", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup3.perform(getDBName2());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.JDBCResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server2.start(getDBName2(), "$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client12.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
		server0.terminate();
		Task cleanup = createTask("cleanup", org.jboss.jbossts.qa.JDBCResources01Cleanups.Cleanup01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		cleanup.perform(getDBName2());
	}

	@Test public void JDBCResources01_abstract_Test024()
	{
		setTestName("Test024");
		Task setup2 = createTask("setup2", org.jboss.jbossts.qa.Utils.JNDIManager.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup2.perform(getDBName2());
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task setup3 = createTask("setup3", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup3.perform(getDBName2());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.JDBCResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server2.start(getDBName2(), "$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client13.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
		server0.terminate();
		Task cleanup = createTask("cleanup", org.jboss.jbossts.qa.JDBCResources01Cleanups.Cleanup01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
        cleanup.perform(getDBName2());
	}

	@Test public void JDBCResources01_abstract_Test025()
	{
		setTestName("Test025");
		Task setup2 = createTask("setup2", org.jboss.jbossts.qa.Utils.JNDIManager.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup2.perform(getDBName2());
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task setup3 = createTask("setup3", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup3.perform(getDBName2());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.JDBCResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server2.start(getDBName2(), "$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client14.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
		server0.terminate();
		Task cleanup = createTask("cleanup", org.jboss.jbossts.qa.JDBCResources01Cleanups.Cleanup01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
        cleanup.perform(getDBName2());
	}

	@Test public void JDBCResources01_abstract_Test026()
	{
		setTestName("Test026");
		Task setup2 = createTask("setup2", org.jboss.jbossts.qa.Utils.JNDIManager.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup2.perform(getDBName2());
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task setup3 = createTask("setup3", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup3.perform(getDBName2());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.JDBCResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server2.start(getDBName2(), "$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client15.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
		server0.terminate();
		Task cleanup = createTask("cleanup", org.jboss.jbossts.qa.JDBCResources01Cleanups.Cleanup01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
        cleanup.perform(getDBName2());
	}

	@Test public void JDBCResources01_abstract_Test027()
	{
		setTestName("Test027");
		Task setup2 = createTask("setup2", org.jboss.jbossts.qa.Utils.JNDIManager.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup2.perform(getDBName2());
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task setup3 = createTask("setup3", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup3.perform(getDBName2());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.JDBCResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server2.start(getDBName2(), "$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client16.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
		server0.terminate();
		Task cleanup = createTask("cleanup", org.jboss.jbossts.qa.JDBCResources01Cleanups.Cleanup01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
        cleanup.perform(getDBName2());
	}

	@Test public void JDBCResources01_abstract_Test028()
	{
		setTestName("Test028");
		Task setup2 = createTask("setup2", org.jboss.jbossts.qa.Utils.JNDIManager.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup2.perform(getDBName2());
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task setup3 = createTask("setup3", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup3.perform(getDBName2());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.JDBCResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server2.start(getDBName2(), "$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client12.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
		server0.terminate();
		Task cleanup = createTask("cleanup", org.jboss.jbossts.qa.JDBCResources01Cleanups.Cleanup01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
        cleanup.perform(getDBName2());
	}

	@Test public void JDBCResources01_abstract_Test029()
	{
		setTestName("Test029");
		Task setup2 = createTask("setup2", org.jboss.jbossts.qa.Utils.JNDIManager.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup2.perform(getDBName2());
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task setup3 = createTask("setup3", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup3.perform(getDBName2());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.JDBCResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server2.start(getDBName2(), "$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client13.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
		server0.terminate();
		Task cleanup = createTask("cleanup", org.jboss.jbossts.qa.JDBCResources01Cleanups.Cleanup01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
        cleanup.perform(getDBName2());
	}

	@Test public void JDBCResources01_abstract_Test030()
	{
		setTestName("Test030");
		Task setup2 = createTask("setup2", org.jboss.jbossts.qa.Utils.JNDIManager.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup2.perform(getDBName2());
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task setup3 = createTask("setup3", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup3.perform(getDBName2());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.JDBCResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server2.start(getDBName2(), "$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client14.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
		server0.terminate();
		Task cleanup = createTask("cleanup", org.jboss.jbossts.qa.JDBCResources01Cleanups.Cleanup01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
        cleanup.perform(getDBName2());
	}

	@Test public void JDBCResources01_abstract_Test031()
	{
		setTestName("Test031");
		Task setup2 = createTask("setup2", org.jboss.jbossts.qa.Utils.JNDIManager.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup2.perform(getDBName2());
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task setup3 = createTask("setup3", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup3.perform(getDBName2());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.JDBCResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server2.start(getDBName2(), "$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client15.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
		server0.terminate();
		Task cleanup = createTask("cleanup", org.jboss.jbossts.qa.JDBCResources01Cleanups.Cleanup01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
        cleanup.perform(getDBName2());
	}

	@Test public void JDBCResources01_abstract_Test032()
	{
		setTestName("Test032");
		Task setup2 = createTask("setup2", org.jboss.jbossts.qa.Utils.JNDIManager.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup2.perform(getDBName2());
		Task setup1 = createTask("setup1", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup1.perform(getDBName1());
		Task setup3 = createTask("setup3", org.jboss.jbossts.qa.JDBCResources01Setups.Setup02.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		setup3.perform(getDBName2());
		Task server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.JDBCResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start(getDBName1(), "$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.JDBCResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server2.start(getDBName2(), "$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.JDBCResources01Clients.Client16.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
		server0.terminate();
		Task cleanup = createTask("cleanup", org.jboss.jbossts.qa.JDBCResources01Cleanups.Cleanup01.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
        cleanup.perform(getDBName2());
	}

}