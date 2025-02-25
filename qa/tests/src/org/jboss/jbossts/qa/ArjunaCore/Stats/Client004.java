/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.jboss.jbossts.qa.ArjunaCore.Stats;

import com.arjuna.ats.arjuna.coordinator.TxStats;
import org.jboss.jbossts.qa.ArjunaCore.AbstractRecord.impl.Service02;
import org.jboss.jbossts.qa.ArjunaCore.Utils.BaseTestClient;

public class Client004 extends BaseTestClient
{
	public static void main(String[] args)
	{
		Client004 test = new Client004(args);
	}

	private Client004(String[] args)
	{
		super(args);
	}

	public void Test()
	{
		try
		{
			setNumberOfCalls(2);
			setNumberOfResources(1);

			TxStats mStats = TxStats.getInstance();
			int expectedCommitted = 0, expectedRolledback = 0, expectedNested = 0, expectedTx = 0;

			for (int j = 0; j < mNumberOfResources; j++)
			{
				//start transaction
				startTx();
				Service02 mService = new Service02(mNumberOfResources);
				mService.dowork(mMaxIteration);
				expectedCommitted += mMaxIteration / 2;
				expectedRolledback += mMaxIteration / 2;
				expectedNested += mMaxIteration;
				expectedTx += mMaxIteration + 1;
				if (j % 2 == 0)
				{
					commit();
					expectedCommitted++;
				}
				else
				{
					abort();
					expectedRolledback++;
				}
			}

			System.err.println("Number of resources = " + mNumberOfResources);
			System.err.println("Number of iterations = " + mMaxIteration);

			//test what the final stat values are
			if (mStats.getNumberOfAbortedTransactions() != expectedRolledback)
			{
				Debug("error in number of aborted transactions: " + mStats.getNumberOfAbortedTransactions() + " expected = " + expectedRolledback);
				mCorrect = false;
			}

			if (mStats.getNumberOfCommittedTransactions() != expectedCommitted)
			{
				Debug("error in number of commited transactions: " + mStats.getNumberOfCommittedTransactions() + " expected = " + expectedCommitted);
				mCorrect = false;
			}

			if (mStats.getNumberOfNestedTransactions() != expectedNested)
			{
				Debug("error in number of nested transactions: " + mStats.getNumberOfNestedTransactions() + " expected = " + expectedNested);
				mCorrect = false;
			}

			if (mStats.getNumberOfTransactions() != expectedTx)
			{
				Debug("error in number of transactions: " + mStats.getNumberOfTransactions() + " expected = " + expectedTx);
				mCorrect = false;
			}

			qaAssert(mCorrect);
		}
		catch (Exception e)
		{
			Fail("Error in Client004.test() :", e);
		}
	}

}