/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.jboss.jbossts.qa.junit.testgroup;

import org.jboss.jbossts.qa.junit.*;
import org.junit.*;

// Automatically generated by XML2JUnit
public class TestGroup_rawsubtransactionawareresources01_3 extends TestGroupBase
{
	public String getTestGroupName()
	{
		return "rawsubtransactionawareresources01_3";
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
		task0.perform("$(1)", "$(2)", "$(3)");
		} finally {
			super.tearDown();
		}
	}

	@Test public void RawSubtransactionAwareResources01_3_Test001()
	{
		setTestName("Test001");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server03.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)", "$(2)", "$(3)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Clients3.Client001.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)", "$(3)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void RawSubtransactionAwareResources01_3_Test002()
	{
		setTestName("Test002");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server03.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)", "$(2)", "$(3)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Clients3.Client002.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)", "$(3)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void RawSubtransactionAwareResources01_3_Test003()
	{
		setTestName("Test003");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server03.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)", "$(2)", "$(3)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Clients3.Client003.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)", "$(3)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void RawSubtransactionAwareResources01_3_Test004()
	{
		setTestName("Test004");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server03.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)", "$(2)", "$(3)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Clients3.Client004.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)", "$(3)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void RawSubtransactionAwareResources01_3_Test005()
	{
		setTestName("Test005");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server03.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)", "$(2)", "$(3)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Clients3.Client005.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)", "$(3)");
		client0.waitFor();
		server1.terminate();
	}

	@Test public void RawSubtransactionAwareResources01_3_Test006()
	{
		setTestName("Test006");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server2.start("$(2)", "$(3)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Clients3.Client001.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)", "$(3)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
	}

	@Test public void RawSubtransactionAwareResources01_3_Test007()
	{
		setTestName("Test007");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server2.start("$(2)", "$(3)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Clients3.Client002.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)", "$(3)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
	}

	@Test public void RawSubtransactionAwareResources01_3_Test008()
	{
		setTestName("Test008");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server2.start("$(2)", "$(3)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Clients3.Client003.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)", "$(3)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
	}

	@Test public void RawSubtransactionAwareResources01_3_Test009()
	{
		setTestName("Test009");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server2.start("$(2)", "$(3)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Clients3.Client004.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)", "$(3)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
	}

	@Test public void RawSubtransactionAwareResources01_3_Test010()
	{
		setTestName("Test010");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server2.start("$(2)", "$(3)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Clients3.Client005.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)", "$(3)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
	}

	@Test public void RawSubtransactionAwareResources01_3_Test011()
	{
		setTestName("Test011");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)", "$(2)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server2.start("$(3)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Clients3.Client001.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)", "$(3)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
	}

	@Test public void RawSubtransactionAwareResources01_3_Test012()
	{
		setTestName("Test012");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)", "$(2)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server2.start("$(3)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Clients3.Client002.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)", "$(3)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
	}

	@Test public void RawSubtransactionAwareResources01_3_Test013()
	{
		setTestName("Test013");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)", "$(2)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server2.start("$(3)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Clients3.Client003.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)", "$(3)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
	}

	@Test public void RawSubtransactionAwareResources01_3_Test014()
	{
		setTestName("Test014");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)", "$(2)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server2.start("$(3)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Clients3.Client004.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)", "$(3)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
	}

	@Test public void RawSubtransactionAwareResources01_3_Test015()
	{
		setTestName("Test015");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)", "$(2)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server2.start("$(3)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Clients3.Client005.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)", "$(3)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
	}

	@Test public void RawSubtransactionAwareResources01_3_Test016()
	{
		setTestName("Test016");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)", "$(3)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server2.start("$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Clients3.Client001.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)", "$(3)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
	}

	@Test public void RawSubtransactionAwareResources01_3_Test017()
	{
		setTestName("Test017");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)", "$(3)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server2.start("$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Clients3.Client002.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)", "$(3)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
	}

	@Test public void RawSubtransactionAwareResources01_3_Test018()
	{
		setTestName("Test018");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)", "$(3)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server2.start("$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Clients3.Client003.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)", "$(3)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
	}

	@Test public void RawSubtransactionAwareResources01_3_Test019()
	{
		setTestName("Test019");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)", "$(3)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server2.start("$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Clients3.Client004.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)", "$(3)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
	}

	@Test public void RawSubtransactionAwareResources01_3_Test020()
	{
		setTestName("Test020");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server02.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)", "$(3)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server2.start("$(2)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Clients3.Client005.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)", "$(3)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
	}

	@Test public void RawSubtransactionAwareResources01_3_Test021()
	{
		setTestName("Test021");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server2.start("$(2)");
		Task server3 = createTask("server3", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server3.start("$(3)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Clients3.Client001.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)", "$(3)");
		client0.waitFor();
		server3.terminate();
		server2.terminate();
		server1.terminate();
	}

	@Test public void RawSubtransactionAwareResources01_3_Test022()
	{
		setTestName("Test022");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server2.start("$(2)");
		Task server3 = createTask("server3", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server3.start("$(3)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Clients3.Client002.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)", "$(3)");
		client0.waitFor();
		server3.terminate();
		server2.terminate();
		server1.terminate();
	}

	@Test public void RawSubtransactionAwareResources01_3_Test023()
	{
		setTestName("Test023");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server2.start("$(2)");
		Task server3 = createTask("server3", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server3.start("$(3)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Clients3.Client003.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)", "$(3)");
		client0.waitFor();
		server3.terminate();
		server2.terminate();
		server1.terminate();
	}

	@Test public void RawSubtransactionAwareResources01_3_Test024()
	{
		setTestName("Test024");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server2.start("$(2)");
		Task server3 = createTask("server3", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server3.start("$(3)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Clients3.Client004.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)", "$(3)");
		client0.waitFor();
		server3.terminate();
		server2.terminate();
		server1.terminate();
	}

	@Test public void RawSubtransactionAwareResources01_3_Test025()
	{
		setTestName("Test025");
		Task server1 = createTask("server1", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server2.start("$(2)");
		Task server3 = createTask("server3", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server3.start("$(3)");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.RawSubtransactionAwareResources01Clients3.Client005.class, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)", "$(3)");
		client0.waitFor();
		server3.terminate();
		server2.terminate();
		server1.terminate();
	}

}