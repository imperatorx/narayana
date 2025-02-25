/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.jboss.jbossts.qa.junit.testgroup;

import org.jboss.jbossts.qa.junit.*;
import org.junit.*;

public class TestGroup_txcore extends TestGroupBase
{
	@Test public void TX_Statistics_Test001()
	{
        startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Stats.Client001.class, "$(CALLS)", "1");
	}

	@Test public void TX_Statistics_Test002()
	{
        startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Stats.Client001.class, "$(CALLS)", "2");
	}

	@Test public void TX_Statistics_Test003()
	{
        startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Stats.Client001.class, "$(CALLS)", "3");
	}

	@Test public void TX_Statistics_Test004()
	{
		startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Stats.Client001.class, "$(CALLS)", "4");
	}


	@Test public void TX_Statistics_Test005()
	{
		startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Stats.Client002.class, "$(CALLS)", "1");
	}

	@Test public void TX_Statistics_Test006()
	{
		startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Stats.Client002.class, "$(CALLS)", "2");
	}

	@Test public void TX_Statistics_Test007()
	{
		startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Stats.Client002.class, "$(CALLS)", "3");
	}

	@Test public void TX_Statistics_Test008()
	{
		startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Stats.Client002.class, "$(CALLS)", "4");
	}


	@Test public void TX_Statistics_Test009()
	{
		startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Stats.Client003.class, "$(CALLS)", "1");
	}

	@Test public void TX_Statistics_Test010()
	{
		startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Stats.Client003.class, "$(CALLS)", "2");
	}

	@Test public void TX_Statistics_Test011()
	{
		startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Stats.Client003.class, "$(CALLS)", "3");
	}

	@Test public void TX_Statistics_Test012()
	{
		startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Stats.Client003.class, "$(CALLS)", "4");
	}


	@Test public void TX_Statistics_Test013()
	{
		startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Stats.Client004.class, "$(CALLS)", "1");
	}

	@Test public void TX_Statistics_Test014()
	{
		startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Stats.Client004.class, "$(CALLS)", "2");
	}

	@Test public void TX_Statistics_Test015()
	{
		startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Stats.Client004.class, "$(CALLS)", "3");
	}

	@Test public void TX_Statistics_Test016()
	{
        startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Stats.Client004.class, "$(CALLS)", "4");
	}

    
	@Test public void Uid_Test001()
	{
        startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Common.UidTest.class, "1", "100");
	}

	@Test public void Uid_Test002()
	{
        startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Common.UidTest.class, "1", "1000");
	}

	@Test public void Uid_Test003()
	{
        startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Common.UidTest.class, "1", "10000");
	}

	@Test public void Uid_Test004()
	{
        startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Common.UidTest.class, "10", "100");
	}

	@Test public void Uid_Test005()
	{
        startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Common.UidTest.class, "10", "800");
	}

	@Test public void Uid_Test006()
	{
        startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Common.UidTest.class, "10", "1000");
	}

	@Test public void Uid_Test007()
	{
        startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Common.UidTest.class, "20", "100");
	}

	@Test public void Uid_Test008()
	{
        startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Common.UidTest.class, "20", "1000");
	}

	@Test public void Uid_Test009()
	{
        startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Common.UidTest.class, "20", "2000");
	}

	@Test public void Uid_Test010()
	{
        startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Common.UidTest.class, "25", "100");
	}

	@Test public void Uid_Test011()
	{
        startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Common.UidTest.class, "28", "100");
	}

	@Test public void Uid_Test012()
	{
        startAndWaitForClient(org.jboss.jbossts.qa.ArjunaCore.Common.UidTest.class, "30", "50");
	}

}