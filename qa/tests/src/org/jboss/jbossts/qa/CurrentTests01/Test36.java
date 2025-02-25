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
import org.omg.CosTransactions.Current;
import org.omg.CosTransactions.NoTransaction;

/**
 * Test to see if stop start of orb causes any problems
 */
public class Test36
{
	public static void main(String[] args)
	{
		boolean correct = true;
		try
		{
			ORBInterface.initORB(args, null);
			OAInterface.initOA();

			Current current = OTS.get_current();

			try
			{
				current.commit(true);
				correct = false;
			}
			catch (NoTransaction noTransaction)
			{
			}

			if (!correct)
			{
				System.out.println("Failed");
                return;
            }
		}
		catch (Exception exception)
		{
			System.out.println("Failed");
			System.err.println("Test036.main: " + exception);
			exception.printStackTrace(System.err);
            return;
        }

		try
		{
			OAInterface.shutdownOA();
			ORBInterface.shutdownORB();
		}
		catch (Exception exception)
		{
			System.err.println("Test036.main: " + exception);
			exception.printStackTrace(System.err);
		}

		//now do the test again and see what happens

		try
		{
			ORBInterface.initORB(args, null);
			OAInterface.initOA();

			Current current = OTS.get_current();

			try
			{
				current.commit(true);
				correct = false;
			}
			catch (NoTransaction noTransaction)
			{
			}

			if (correct)
			{
				System.out.println("Passed");
			}
			else
			{
				System.out.println("Failed");
			}
		}
		catch (Exception exception)
		{
			System.out.println("Failed");
			System.err.println("Test036.main: " + exception);
			exception.printStackTrace(System.err);
		}

		try
		{
			OAInterface.shutdownOA();
			ORBInterface.shutdownORB();
		}
		catch (Exception exception)
		{
			System.err.println("Test01.main: " + exception);
			exception.printStackTrace(System.err);
		}
	}
}