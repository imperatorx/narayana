/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.jboss.jbossts.qa.ArjunaCore.Stats;

import com.arjuna.ats.arjuna.coordinator.TxStats;
import org.jboss.jbossts.qa.ArjunaCore.AbstractRecord.impl.Service02;
import org.jboss.jbossts.qa.ArjunaCore.Utils.BaseTestClient;

public class Client003 extends BaseTestClient
{
	public static void main(String[] args)
	{
		Client003 test = new Client003(args);
	}

	private Client003(String[] args)
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

			Service02 mService = new Service02(mNumberOfResources);
			mService.dowork(mMaxIteration * 2);

			if (mStats.getNumberOfAbortedTransactions() != mMaxIteration)
			{
				Debug("error in number of aborted transactions: " + mStats.getNumberOfAbortedTransactions());
				mCorrect = false;
			}

			if (mStats.getNumberOfCommittedTransactions() != mMaxIteration)
			{
				Debug("error in number of commited transactions: " + mStats.getNumberOfCommittedTransactions());
				mCorrect = false;
			}

			if (mStats.getNumberOfNestedTransactions() != 0)
			{
				Debug("error in number of nested transactions: " + mStats.getNumberOfNestedTransactions());
				mCorrect = false;
			}

			if (mStats.getNumberOfTransactions() != mMaxIteration * 2)
			{
				Debug("error in number of transactions: " + mStats.getNumberOfTransactions());
				mCorrect = false;
			}

			qaAssert(mCorrect);
		}
		catch (Exception e)
		{
			Fail("Error in Client003.test() :", e);
		}
	}

}