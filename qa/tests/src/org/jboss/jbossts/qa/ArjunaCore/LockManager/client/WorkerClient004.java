/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.jboss.jbossts.qa.ArjunaCore.LockManager.client;

import org.jboss.jbossts.qa.ArjunaCore.LockManager.impl.TXBasicLockRecord;
import org.jboss.jbossts.qa.ArjunaCore.LockManager.impl.TXBasicLockRecord2;
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

		if (args.length > 0 && args[0].equals("-newlock"))
		{
			System.out.println("Creating a lock per attempt");
			_newLock = true;
		}
		else
		{
			System.out.println("Creating a single lock for all attempts");
		}
	}

	public void Test()
	{
		try
		{
			setNumberOfCalls(3);
			setNumberOfResources(2);
			setNumberOfWorkers(1);

			//set up lockmanager records
			TXBasicLockRecord[] mLockRecordList = _newLock ? new TXBasicLockRecord[mNumberOfResources] : new TXBasicLockRecord2[mNumberOfResources];
			int[] expectedValue = new int[mNumberOfResources];

			for (int i = 0; i < mNumberOfResources; i++)
			{
				mLockRecordList[i] = _newLock ? new TXBasicLockRecord() : new TXBasicLockRecord2();
				expectedValue[i] = 0;
			}

			Worker004[] mWorkers = new Worker004[mNumberOfWorkers];
			for (int i = 0; i < mNumberOfWorkers; i++)
			{
				mWorkers[i] = new Worker004(mMaxIteration, mNumberOfResources, mLockRecordList, i);
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

					int[] workersExpectedValue = mWorkers[i].getExpectedValues();
					for (int j = 0; j < workersExpectedValue.length; j++)
					{
						expectedValue[j] += workersExpectedValue[j];
					}
				}
			}
			catch (Exception e)
			{
				mCorrect = false;
				Debug("exception in worker thread " + e);
			}

			//now check final values
			for (int i = 0; i < mNumberOfResources; i++)
			{
				int endValue = mLockRecordList[i].getValue();
				double result = Math.abs(endValue - expectedValue[i]) / (double) expectedValue[i];
				if (result > mPercent)
				{
					Debug("resource " + i + " final value is incorrect: value =" + mLockRecordList[i].getValue() + " we expected = " + expectedValue[i] + " does not fall within the " + (mPercent * 100) + "% margin");
					mCorrect = false;
				}
			}

			qaAssert(mCorrect);
		}
		catch (Exception e)
		{
			Fail("Error in WorkerClient004.test() :", e);
		}
	}

	private boolean _newLock = false;
}