/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.jboss.jbossts.qa.ArjunaCore.StateManager.client;

import org.jboss.jbossts.qa.ArjunaCore.StateManager.impl.TXBasicStateRecord;
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

			TXBasicStateRecord[] mStateRecordList = new TXBasicStateRecord[mNumberOfResources];
			//set up abstract records
			for (int i = 0; i < mNumberOfResources; i++)
			{
				mStateRecordList[i] = new TXBasicStateRecord();
			}

			createTx();
			try
			{
				//start transaction
				begin();
				//dont add anything here we will do this in the increase
				for (int j = 0; j < mNumberOfResources; j++)
				{
					for (int i = 0; i < mMaxIteration; i++)
					{
						mStateRecordList[j].increase();
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
							mStateRecordList[j].increase();
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
				if (mStateRecordList[i].getValue() != mMaxIteration)
				{
					Debug("whilst checking the " + i + " resource the getvalue was: " + mStateRecordList[i].getValue() + " and we expected: " + mMaxIteration);
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