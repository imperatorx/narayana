/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */


package org.jboss.jbossts.qa.CrashRecovery05Clients1;



/*
 * Try to get around the differences between Ansi CPP and
 * K&R cpp with concatenation.
 */




import org.jboss.jbossts.qa.CrashRecovery05.CheckBehavior;
import org.jboss.jbossts.qa.CrashRecovery05.ResourceTrace;

public class Client02a
{
	public static void main(String[] args)
	{
        ClientAfterCrash afterCrash = new ClientAfterCrash(Client02a.class.getSimpleName());

		try
		{
			afterCrash.initOrb(args);

			CheckBehavior[] checkBehaviors = new CheckBehavior[1];
			checkBehaviors[0] = new CheckBehavior();
			checkBehaviors[0].allow_done = false;
			checkBehaviors[0].allow_returned_prepared = false;
			checkBehaviors[0].allow_returned_committing = true;
			checkBehaviors[0].allow_returned_committed = true;
			checkBehaviors[0].allow_returned_rolledback = true;
			checkBehaviors[0].allow_raised_not_prepared = false;

            afterCrash.serviceSetup(checkBehaviors);

            afterCrash.waitForRecovery();

            afterCrash.checkResourceTrace(ResourceTrace.ResourceTraceCommit, ResourceTrace.ResourceTraceRollback);

            afterCrash.reportStatus();
		}
		catch (Exception exception)
		{
            afterCrash.reportException(exception);
		}
        finally {
            afterCrash.shutdownOrb();
        }
	}
}