/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.jboss.jbossts.qa.ArjunaCore.LockManager.client;

import org.jboss.jbossts.qa.ArjunaCore.LockManager.impl.TXBasicLockRecord;
import org.jboss.jbossts.qa.ArjunaCore.Utils.BaseTestClient;

public class Client002 extends BaseTestClient
{
	public static void main(String[] args)
	{
		Client002 test = new Client002(args);
	}

	private Client002(String[] args)
	{
		super(args);
	}

	public void Test()
	{
		try
		{
			setNumberOfCalls(2);
			setNumberOfResources(1);

			TXBasicLockRecord[] mLockRecordList = new TXBasicLockRecord[mNumberOfResources];
			int[] expectedValue = new int[mNumberOfResources];
			//set up abstract records
			for (int i = 0; i < mNumberOfResources; i++)
			{
				mLockRecordList[i] = new TXBasicLockRecord();
				expectedValue[i] = 0;
			}

			createTx();
			try
			{
				//start transaction
				begin();
				//add abstract record
				for (int j = 0; j < mNumberOfResources; j++)
				{
					for (int i = 0; i < mMaxIteration; i++)
					{
						expectedValue[j] += mLockRecordList[j].increase();
					}
				}
				//comit transaction
				commit();
			}
			catch (Exception e)
			{
				Debug("exception in first transaction ", e);
				abort();
				mCorrect = false;
			}

			if (mCorrect)
			{
				//start new AtomicAction
				createTx();
				try
				{
					begin();
					for (int j = 0; j < mNumberOfResources; j++)
					{
						for (int i = 0; i < mMaxIteration; i++)
						{
							mLockRecordList[j].increase();
						}
					}
					//abort transaction
					abort();
				}
				catch (Exception e)
				{
					Debug("exception in first transaction ", e);
					abort();
					mCorrect = false;
				}
			}

			//check final values
			for (int i = 0; i < mNumberOfResources; i++)
			{
				//first test to see if increases have been run
				if (mLockRecordList[i].getValue() != expectedValue[i])
				{
					Debug("whilst checking the " + i + " resource the getvalue was: " + mLockRecordList[i].getValue() + " and we expected: " + expectedValue[i]);
					mCorrect = false;
					break;
				}
			}

			qaAssert(mCorrect);
		}
		catch (Exception e)
		{
			Fail("Error in Client002.test() :", e);
		}
	}

}