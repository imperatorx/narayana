/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */


package org.jboss.jbossts.qa.AITResources01Clients;



/*
 * Try to get around the differences between Ansi CPP and
 * K&R cpp with concatenation.
 */




import org.jboss.jbossts.qa.AITResources01.*;
import org.jboss.jbossts.qa.Utils.OAInterface;
import org.jboss.jbossts.qa.Utils.ORBInterface;
import org.jboss.jbossts.qa.Utils.ServerIORStore;
import org.omg.CORBA.IntHolder;

public class Client14
{
	public static void main(String[] args)
	{
		try
		{
			ORBInterface.initORB(args, null);
			OAInterface.initOA();

			String counterIOR = ServerIORStore.loadIOR(args[args.length - 3]);
			Counter counter = CounterHelper.narrow(ORBInterface.orb().string_to_object(counterIOR));

			int numberOfWorkers = Integer.parseInt(args[args.length - 2]);
			int numberOfCalls = Integer.parseInt(args[args.length - 1]);

			Worker[] workers = new Worker[numberOfWorkers];

			for (int index = 0; index < workers.length; index++)
			{
				workers[index] = new Worker(numberOfCalls, counter);
			}

			for (int index = 0; index < workers.length; index++)
			{
				workers[index].start();
			}

			boolean correct = true;

			for (int index = 0; index < workers.length; index++)
			{
				workers[index].join();
				correct = correct && workers[index].isCorrect();
			}

			IntHolder value = new IntHolder();
			counter.get(value);
			correct = correct && (value.value == (numberOfWorkers * numberOfCalls));

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

	private static class Worker extends Thread
	{
		public Worker(int numberOfCalls, Counter counter)
		{
			_numberOfCalls = numberOfCalls;
			_counter = counter;
		}

		public void run()
		{
			try
			{
				int index = 0;
				while (index < _numberOfCalls)
				{
					try
					{
						_counter.increase();
						index++;
					}
					catch (InvocationException invocationException)
					{
					}
				}
			}
			catch (Exception exception)
			{
				System.err.println("Client14.Worker.run: " + exception);
				exception.printStackTrace(System.err);
				_correct = false;
			}
		}

		public boolean isCorrect()
		{
			return _correct;
		}

		private boolean _correct = true;
		private int _numberOfCalls;
		private Counter _counter = null;
	}
}