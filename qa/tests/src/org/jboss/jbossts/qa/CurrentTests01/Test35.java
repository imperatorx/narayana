/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */


package org.jboss.jbossts.qa.CurrentTests01;



/*
 * Try to get around the differences between Ansi CPP and
 * K&R cpp with concatenation.
 */




import org.jboss.jbossts.qa.Utils.OAInterface;
import org.jboss.jbossts.qa.Utils.ORBInterface;
import org.jboss.jbossts.qa.Utils.OTS;
import org.omg.CORBA.INVALID_TRANSACTION;
import org.omg.CORBA.TRANSACTION_ROLLEDBACK;

public class Test35
{
	public static void main(String[] args)
	{
		boolean correct = true;
		int interPhaseSleepPeriod;

		if (args.length != 0)
		{
			interPhaseSleepPeriod = Integer.parseInt(args[args.length - 1]);
			interPhaseSleepPeriod = interPhaseSleepPeriod * 1000;
		}
		else
		{
			interPhaseSleepPeriod = 0;
		}

		try
		{
			ORBInterface.initORB(args, null);
			OAInterface.initOA();

			System.err.println("Purpose of test: This test has been introduced to ensure that setting a transaction\n" +
					"timeout of 0 (zero) seconds will remove any existing timeout. The test operates\n" +
					"in two phases;\n\n" +
					"In the first phase, a transaction timeout of 4 secs is set. A transaction is then\n" +
					"started, the thread then immediately goes to sleep for 8 seconds. When the thread\n" +
					"wakes, an attempt is made to commit the transaction. An exception (INVALID_TRANSACTION)\n" +
					"is expected to be thrown at this point as the transaction should have timed out and\n" +
					"been rolled back.\n\n" +
					"In the second phase, a transaction timeout of 0 secs is set. A transaction is then\n" +
					"started, the thread then immediately goes to sleep for 12 seconds. When the thread\n" +
					"wakes up, an attempt is again made to commit the transaction. This time the commit\n" +
					"should work and the test will thus be regarded as having passed. If instead an\n" +
					"exception is thrown because the transaction has timed out, this would indicate that\n" +
					"setting the timeout to 0 did not remove the previous timeout and the test should\n" +
					"therefore be regarded as having failed.\n\n" +
					"An additional pause may be specified in between the above two phases to allow a\n" +
					"manual check to see whether the TX_REAPER_THREAD is thrashing the CPU, as it\n" +
					"has done in the past. To activate this interphase pause, you simply need to pass\n" +
					"an integer as a parameter to the test. The test will then pause for this amount\n" +
					"of seconds between phases one and two.\n\n");

			org.omg.CosTransactions.Current current = OTS.get_current();

			System.err.println("Client: Initiating phase one");

			System.err.println("Client: Setting 4 second timeout");
			current.set_timeout(4);

			System.err.println("Client: Starting transaction then sleeping for 12 seconds");
			current.begin();
			Thread.sleep(12000);

			try
			{

				System.err.println("Client: Trying commit (expect INVALID_TRANSACTION exception)...");
				current.commit(true);
				System.err.println("Client: Commit OK - Warning: Expected INVALID_TRANSACTION or TRANSACTION_ROLLEDBACK exception to be thrown");
				System.err.println("Client: Test should fail");
				correct = false;
			}
			catch (INVALID_TRANSACTION invalidTransaction)
			{
				System.err.println("Client: Caught INVALID_TRANSACTION exception");
			}
            catch(TRANSACTION_ROLLEDBACK transactionRolledback)
            {
                System.out.println("Client: Caught TRANSACTION_ROLLEDBACK exception");
            }
			catch (Exception exception)
			{
				System.err.println("Client: Caught unexpected exception: " + exception);
				exception.printStackTrace(System.err);
				correct = false;
			}

			if (correct)
			{

				if (interPhaseSleepPeriod != 0)
				{
					try
					{
						System.err.println("Client: Inter-phase sleep period of " + (interPhaseSleepPeriod / 1000) + " seconds");
						Thread.sleep(interPhaseSleepPeriod);
					}
					catch (Exception exception)
					{
						System.err.println("Client: Caught unexpected exception: " + exception);
						exception.printStackTrace(System.err);
						correct = false;
					}
				}

				if (correct)
				{

					System.err.println("Client: Initiating phase two");

					System.err.println("Client: Setting 0 second timeout");
					current.set_timeout(0);

					System.err.println("Client: Starting transaction then sleeping for 8 seconds");
					current.begin();
					Thread.sleep(8000);

					try
					{
						System.err.println("Client: Trying commit (expect 'Commit OK')...");
						current.commit(true);
						System.err.println("Client: Commit OK");
						System.err.println("Client: Test should pass");
					}
					catch (INVALID_TRANSACTION invalidTransaction)
					{
						System.err.println("Client: Caught INVALID_TRANSACTION exception - Warning: Expected Commit OK");
						System.err.println("Client: Test should fail");
						correct = false;
					}
					catch (Exception exception)
					{
						System.err.println("Client: Caught unexpected exception: " + exception);
						exception.printStackTrace(System.err);
						correct = false;
					}
				}
			}
		}
		catch (Exception exception)
		{
			System.err.println("Client: Caught unexpected exception: " + exception);
			exception.printStackTrace(System.err);
			correct = false;
		}

		if (correct)
		{
			System.out.println("Passed");
		}
		else
		{
			System.out.println("Failed");
		}

		try
		{
			OAInterface.shutdownOA();
			ORBInterface.shutdownORB();
		}
		catch (Exception exception)
		{
			System.err.println("Client: Caught unexpected exception: " + exception);
			exception.printStackTrace(System.err);
		}
	}
}