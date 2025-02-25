/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */


package org.jboss.jbossts.qa.JDBCResources02Clients;

import org.jboss.jbossts.qa.JDBCResources02.*;
import org.jboss.jbossts.qa.Utils.OAInterface;
import org.jboss.jbossts.qa.Utils.ORBInterface;
import org.jboss.jbossts.qa.Utils.OTS;
import org.jboss.jbossts.qa.Utils.ServerIORStore;

public class Client14
{
	public static void main(String[] args)
	{
		try
		{
			ORBInterface.initORB(args, null);
			OAInterface.initOA();

			String infoTableIOR1 = ServerIORStore.loadIOR(args[args.length - 2]);
			InfoTable infoTable1 = InfoTableHelper.narrow(ORBInterface.orb().string_to_object(infoTableIOR1));

			String infoTableIOR2 = ServerIORStore.loadIOR(args[args.length - 1]);
			InfoTable infoTable2 = InfoTableHelper.narrow(ORBInterface.orb().string_to_object(infoTableIOR2));

			boolean correct = true;

			for (int index = 0; index < 10; index++)
			{
				OTS.current().begin();

				String name = "Name_" + index;
				String value = "Value_" + (9 - index);

				try
				{
					infoTable1.update(name, value, OTS.current().get_control());
					infoTable2.update(name, value, OTS.current().get_control());
				}
				catch (Exception exception)
				{
					System.err.println("Client08.main: " + exception);
					exception.printStackTrace(System.err);
					correct = false;
				}

				OTS.current().commit(true);
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
			System.err.println("Client14.main: " + exception);
			exception.printStackTrace(System.err);
		}
		finally
		{
// code change to stop database locking
			try
			{
				if (OTS.current().get_control() != null)
				{
					OTS.current().rollback();
				}
			}
			catch (Exception e)
			{
				System.err.println("Finally has caught exception");
				e.printStackTrace(System.err);
			}
		}

		try
		{
			OAInterface.shutdownOA();
			ORBInterface.shutdownORB();
		}
		catch (Exception exception)
		{
			System.err.println("Client14.main: " + exception);
			exception.printStackTrace(System.err);
		}
	}
}