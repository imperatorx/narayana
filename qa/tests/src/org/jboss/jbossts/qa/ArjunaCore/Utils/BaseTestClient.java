/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

/*
 * Created by IntelliJ IDEA.
 * User: peter craddock
 * Date: 24-May-02
 * Time: 11:56:12
 */
package org.jboss.jbossts.qa.ArjunaCore.Utils;

import com.arjuna.ats.arjuna.AtomicAction;
import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.coordinator.AbstractRecord;
import org.jboss.jbossts.qa.Utils.BaseTestCase;
import org.jboss.jbossts.qa.Utils.JVMStats;

public class BaseTestClient extends BaseTestCase
{
	public BaseTestClient()
	{
		super();
	}

	public BaseTestClient(String[] args)
	{
		super(args);
		Test();
	}

	public void Test()
	{
	}

	public void startTx()
			throws Exception
	{
		mAtom = new AtomicAction();
		mAtom.begin();
	}

	public void createTx()
			throws Exception
	{
		mAtom = new AtomicAction();
	}

	public void createTx(Uid uid)
			throws Exception
	{
		mAtom = new AtomicAction(uid);
	}

	public void begin()
			throws Exception
	{
		mAtom.begin();
	}

	public int commit()
			throws Exception
	{
		return commit(true);
	}

	public int commit(boolean flag)
			throws Exception
	{
		return mAtom.commit(flag);
	}

	//extra methods because we are changing the return type of the base class
	public int intCommit()
			throws Exception
	{
		return intCommit(true);
	}

	public int intCommit(boolean flag)
			throws Exception
	{
		return mAtom.commit(flag);
	}

	public void abort()
			throws Exception
	{
		mAtom.abort();
	}

	public void add(AbstractRecord record)
			throws Exception
	{
		mAtom.add(record);
	}

	public void setNumberOfCalls(int relative_position)
	{
		mMaxIteration = getIntRelArg(relative_position);
	}

	public void setNumberOfResources(int relative_position)
	{
		mNumberOfResources = getIntRelArg(relative_position);
	}

	public void setNumberOfWorkers(int relative_position)
	{
		mNumberOfWorkers = getIntRelArg(relative_position);
	}

	public void setCrashPoint(int relative_position)
	{
		mCrashPoint = getIntRelArg(relative_position);
	}

	public void setCrashType(int relative_position)
	{
		mCrashType = getIntRelArg(relative_position);
	}

	public void getFirstReading()
	{
		clientMemory0 = (int) JVMStats.getMemory();
        System.out.println("clientMemory0: "+clientMemory0);
    }

	public void getSecondReading()
	{
		clientMemory1 = (int) JVMStats.getMemory();
        System.out.println("clientMemory1: "+clientMemory1);
	}

	public void qaMemoryAssert()
	{
		float clientMemoryIncrease = ((float) (clientMemory1 - clientMemory0)) / ((float) clientMemory0);

		Debug("Client memory increase threshold : " + (float) (100.0 * clientIncreaseThreshold) + "%");
		Debug("Client percentage memory increase: " + (float) (100.0 * clientMemoryIncrease) + "%");
		Debug("Client memory increase per call  : " + (clientMemory1 - clientMemory0) / mMaxIteration);

		if (clientMemoryIncrease > clientIncreaseThreshold)
		{
			Debug("Memory increase too high");
            mCorrect = false;
		}

		qaAssert(mCorrect);
	}

	public void setUniquePrefix(int relative_position)
	{
		uniquePrefix = getRelativeArg(relative_position);
	}

	public String getUniquePrefix()
	{
		return uniquePrefix;
	}

	public String getResourceName(String text)
	{
		return getUniquePrefix() + text;
	}

	public void startStopWatch()
	{
		_stopWatch = System.currentTimeMillis();
	}

	public long stopStopWatch()
	{
		long endTime = System.currentTimeMillis();

		return endTime - _stopWatch;
	}

	private long _stopWatch = 0;
	private String uniquePrefix = "";
	public int mMaxIteration = 0; //default value
	public int mNumberOfResources = 0;//default value
	public int mNumberOfWorkers = 0;//default value
	public int mCrashPoint = 1;//default value
	public int mCrashType = 0;//default value

	public AtomicAction mAtom = null;
	public boolean mCorrect = true;

	//value used with statemanager tests
	public double mPercent = 0.4;//default value
}