/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.jboss.jbossts.qa.ArjunaCore.LockManager.client;

import org.jboss.jbossts.qa.ArjunaCore.AbstractRecord.CrashRecovery.impl.CrashAbstractRecord;
import org.jboss.jbossts.qa.ArjunaCore.LockManager.impl.BasicLockRecord;
import org.jboss.jbossts.qa.ArjunaCore.Utils.BaseTestClient;

public class Client005 extends BaseTestClient
{
	public static void main(String[] args)
	{
		Client005 test = new Client005(args);
	}

	private Client005(String[] args)
	{
		super(args);
	}

	public void Test()
	{
		try
		{
			setNumberOfCalls(2);
			setNumberOfResources(1);

			BasicLockRecord[] mLockRecordList = new BasicLockRecord[mNumberOfResources];
			int[] expectedValue = new int[mNumberOfResources];

			//set up abstract records
			for (int i = 0; i < mNumberOfResources; i++)
			{
				mLockRecordList[i] = new BasicLockRecord();
				expectedValue[i] = 0;
			}

			startTx();
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

			//now create abstract record that will cause rollback
			CrashAbstractRecord mCrashObject = new CrashAbstractRecord(3, 1);

			//start new AtomicAction
			startTx();
			add(mCrashObject);
			for (int j = 0; j < mNumberOfResources; j++)
			{
				for (int i = 0; i < mMaxIteration; i++)
				{
					expectedValue[j] += mLockRecordList[j].increase();
				}
			}
			//abort transaction
			commit();

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
			Fail("Error in Client005.test() :", e);
		}
	}

}