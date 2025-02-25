/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */


package org.jboss.jbossts.qa.PerfProfile01Clients;



/*
 * Try to get around the differences between Ansi CPP and
 * K&R cpp with concatenation.
 */




import org.jboss.jbossts.qa.PerfProfile01.*;
import org.jboss.jbossts.qa.Utils.OAInterface;
import org.jboss.jbossts.qa.Utils.ORBInterface;
import org.jboss.jbossts.qa.Utils.PerformanceProfileStore;
import org.jboss.jbossts.qa.Utils.ServerIORStore;

import java.util.Date;

public class Client_ImplicitObject_NoTran_NoTranNullOper
{
	public static void main(String[] args)
	{
		try
		{
			ORBInterface.initORB(args, null);
			OAInterface.initOA();

			String prefix = args[args.length - 3];
			int numberOfCalls = Integer.parseInt(args[args.length - 2]);
			String implicitObjectIOR = ServerIORStore.loadIOR(args[args.length - 1]);

			ImplicitObject implicitObject = ImplicitObjectHelper.narrow(ORBInterface.orb().string_to_object(implicitObjectIOR));

			boolean correct = true;

			Date start = new Date();

			for (int index = 0; index < numberOfCalls; index++)
			{
				implicitObject.no_tran_nulloper();
			}

			Date end = new Date();

			float operationDuration = ((float) (end.getTime() - start.getTime())) / ((float) numberOfCalls);

			System.err.println("Operation duration       : " + operationDuration + "ms");
			System.err.println("Test duration            : " + (end.getTime() - start.getTime()) + "ms");

			correct = PerformanceProfileStore.checkPerformance(prefix + "_ImplicitObject_NoTran_NoTranNullOper", operationDuration);

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
			System.err.println("Client_ImplicitObject_NoTran_NoTranNullOper.main: " + exception);
			exception.printStackTrace(System.err);
		}

		try
		{
			OAInterface.shutdownOA();
			ORBInterface.shutdownORB();
		}
		catch (Exception exception)
		{
			System.err.println("Client_ImplicitObject_NoTran_NoTranNullOper.main: " + exception);
			exception.printStackTrace(System.err);
		}
	}
}