/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.jboss.jbossts.qa.ArjunaCore.AbstractRecord.CrashRecovery.client;

import org.jboss.jbossts.qa.ArjunaCore.AbstractRecord.CrashRecovery.impl.CrashAbstractRecord02;
import org.jboss.jbossts.qa.ArjunaCore.AbstractRecord.CrashRecovery.impl.CrashService02;
import org.jboss.jbossts.qa.ArjunaCore.AbstractRecord.CrashRecovery.impl.RecoveryTransaction;
import org.jboss.jbossts.qa.ArjunaCore.Utils.BaseTestClient;

import com.arjuna.ats.arjuna.coordinator.RecordType;
import com.arjuna.ats.arjuna.coordinator.abstractrecord.RecordTypeManager;
import com.arjuna.ats.arjuna.coordinator.abstractrecord.RecordTypeMap;

class CrashAbstractRecordMap implements RecordTypeMap
{
    @SuppressWarnings("unchecked")
    public Class getRecordClass ()
    {
        return CrashAbstractRecord02.class;
    }
    
    public int getType ()
    {
        return RecordType.USER_DEF_FIRST1;
    }
}

public class Client001 extends BaseTestClient
{
	public static void main(String[] args)
	{
	       RecordTypeManager.manager().add(new CrashAbstractRecordMap());
	            
		@SuppressWarnings("unused")
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
			setNumberOfCalls(5);
			setNumberOfResources(4);
			setCrashPoint(3);
			setCrashType(2);
			setUniquePrefix(1);

			CrashService02 mService = new CrashService02(mNumberOfResources, mCrashPoint, mCrashType);

			//start transaction	to check all is ok.
			startTx();
			mService.setupOper(getUniquePrefix());
			mService.doWork(mMaxIteration);
			commit();

			for (int ii = 0; ii < mNumberOfResources; ii++)
			{
				mService.mAbstractRecordList[ii].resetValue();
			}

			RecoveryTransaction tx = new RecoveryTransaction(mAtom.get_uid());

			tx.doCommit();

			try
			{
				for (int i = 0; i < mNumberOfResources; i++)
				{
					if (mService.mAbstractRecordList[i].getValue() != mMaxIteration * mNumberOfResources)
					{
						Debug("Error checking resource " + i + " value  = " + mService.mAbstractRecordList[i].getValue());
						mCorrect = false;

						qaAssert(false);
					}
				}
			}
			catch (Exception e)
			{
				Fail("Exception whilst checking resource", e);
			}

			qaAssert(mCorrect);
		}
		catch (Exception e)
		{
			Fail("Error in Client001.test() :", e);
		}
	}

}