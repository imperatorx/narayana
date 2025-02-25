/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.jboss.jbossts.qa.ArjunaCore.Stats;

import com.arjuna.ats.arjuna.coordinator.TxStats;
import org.jboss.jbossts.qa.ArjunaCore.AbstractRecord.impl.Service01;
import org.jboss.jbossts.qa.ArjunaCore.Utils.BaseTestClient;

public class Client001 extends BaseTestClient
{
	public static void main(String[] args)
	{
		Client001 test = new Client001(args);
	}

	private Client001(String[] args)
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

			startTx();
			//add abstract record
			Service01 mService = new Service01(mNumberOfResources);
			mService.setupOper();
			mService.doWork(mMaxIteration);
			//comit transaction
			commit();

			mService = new Service01(mNumberOfResources);
			//start new AtomicAction
			startTx();
			mService.setupOper();
			mService.doWork(mMaxIteration);
			//abort transaction
			abort();

			if (mStats.getNumberOfAbortedTransactions() != 1)
			{
				Debug("error in number of aborted transactions: " + mStats.getNumberOfAbortedTransactions());
				mCorrect = false;
			}

			if (mStats.getNumberOfCommittedTransactions() != 1)
			{
				Debug("error in number of commited transactions: " + mStats.getNumberOfCommittedTransactions());
				mCorrect = false;
			}

			if (mStats.getNumberOfNestedTransactions() != 0)
			{
				Debug("error in number of nested transactions: " + mStats.getNumberOfNestedTransactions());
				mCorrect = false;
			}

			if (mStats.getNumberOfTransactions() != 2)
			{
				Debug("error in number of transactions: " + mStats.getNumberOfTransactions());
				mCorrect = false;
			}

			qaAssert(mCorrect);
		}
		catch (Exception e)
		{
			Fail("Error in Client001.test() :", e);
		}
	}

}