/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.jboss.jbossts.qa.ArjunaCore.AbstractRecord.client;

import org.jboss.jbossts.qa.ArjunaCore.Utils.BaseTestClient;

public class WorkerClient002 extends BaseTestClient
{
	public static void main(String[] args)
	{
		WorkerClient002 test = new WorkerClient002(args);
	}

	private WorkerClient002(String[] args)
	{
		super(args);
	}

	public void Test()
	{
		try
		{
			setNumberOfCalls(3);
			setNumberOfResources(2);
			setNumberOfWorkers(1);

			Worker002[] mWorkers = new Worker002[mNumberOfWorkers];
			for (int i = 0; i < mNumberOfWorkers; i++)
			{
				mWorkers[i] = new Worker002(mMaxIteration, mNumberOfResources, i);
				mWorkers[i].start();
			}

			try
			{
				//wait for threads to complete
				for (int i = 0; i < mNumberOfWorkers; i++)
				{
					mWorkers[i].join();
					//check for any exceptions
					if (!mWorkers[i].isCorrect())
					{
						Debug("worker " + i + " has encountered an exception");
						mCorrect = false;
					}
				}
			}
			catch (Exception e)
			{
				mCorrect = false;
				Debug("exception in worker thread ", e);
			}

			for (int i = 0; i < mNumberOfWorkers; i++)
			{
				if (!mWorkers[i].isCorrect())
				{
					mCorrect = false;
					Debug("worker " + i + " has encountered a problem");
					break;
				}
			}

			qaAssert(mCorrect);
		}
		catch (Exception e)
		{
			Fail("Error in WorkerClient002.test() :", e);
		}
	}

}