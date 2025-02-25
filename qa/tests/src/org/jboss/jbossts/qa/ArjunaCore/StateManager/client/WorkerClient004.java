/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.jboss.jbossts.qa.ArjunaCore.StateManager.client;

import org.jboss.jbossts.qa.ArjunaCore.Utils.BaseTestClient;

public class WorkerClient004 extends BaseTestClient
{
	public static void main(String[] args)
	{
		WorkerClient004 test = new WorkerClient004(args);
	}

	private WorkerClient004(String[] args)
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

			Worker004[] mWorkers = new Worker004[mNumberOfWorkers];
			for (int i = 0; i < mNumberOfWorkers; i++)
			{
				mWorkers[i] = new Worker004(mMaxIteration, mNumberOfResources, i);
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
				Debug("exception in worker thread " + e);
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
			Fail("Error in WorkerClient004.test() :", e);
		}
	}

}