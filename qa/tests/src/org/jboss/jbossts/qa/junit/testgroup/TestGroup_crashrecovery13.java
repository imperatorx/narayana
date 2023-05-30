/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.jboss.jbossts.qa.junit.testgroup;

import org.jboss.jbossts.qa.junit.*;
import org.junit.*;

// Automatically generated by XML2JUnit
public class TestGroup_crashrecovery13 extends TestGroupBase
{
	public String getTestGroupName()
	{
		return "crashrecovery13";
	}


	@Before public void setUp()
	{
		super.setUp();
	}

	@After public void tearDown()
	{
		try {
		} finally {
			super.tearDown();
		}
	}

	@Test public void CrashRecovery13_Test01()
	{
		setTestName("Test01");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.CrashRecovery13Clients.Test01.class, Task.TaskType.EXPECT_PASS_FAIL, 240);
		client0.start("nocrash", "CR13_01.log");
		client0.waitFor();
	}

	@Test public void CrashRecovery13_Test02()
	{
		setTestName("Test02");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.CrashRecovery13Clients.Test02.class, Task.TaskType.EXPECT_PASS_FAIL, 240);
		client0.start("nocrash", "CR13_02.log");
		client0.waitFor();
	}

	@Test public void CrashRecovery13_Test03()
	{
		setTestName("Test03");
		Task client0 = createTask("client0", org.jboss.jbossts.qa.CrashRecovery13Clients.Test03.class, Task.TaskType.EXPECT_PASS_FAIL, 240);
		client0.start("nocrash", "CR13_03.log");
		client0.waitFor();
	}

}