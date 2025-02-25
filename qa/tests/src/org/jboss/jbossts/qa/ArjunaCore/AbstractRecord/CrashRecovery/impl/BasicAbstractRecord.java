/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.jboss.jbossts.qa.ArjunaCore.AbstractRecord.CrashRecovery.impl;

import com.arjuna.ats.arjuna.ObjectType;
import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.coordinator.AbstractRecord;
import com.arjuna.ats.arjuna.coordinator.RecordType;
import com.arjuna.ats.arjuna.coordinator.TwoPhaseOutcome;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputObjectState;
import org.jboss.jbossts.qa.ArjunaCore.Utils.qautil;
import org.jboss.jbossts.qa.Utils.ServerIORStore;

/**
 * Simple record used to test AtomicAction
 */
public class BasicAbstractRecord extends AbstractRecord
{
	public BasicAbstractRecord()
	{
	}

	public BasicAbstractRecord(int id, String uniquePrefix)
	{
		super(new Uid(), "CrashAbstractRecord", ObjectType.ANDPERSISTENT);
		mId = id;

		_uniquePrefix = uniquePrefix;
	}

	public int typeIs()
	{
		return RecordType.USER_DEF_FIRST1;
	}

	public Object value()
	{
		return null;
	}

	// for crash recovery
	public static AbstractRecord create()
	{
		return new BasicAbstractRecord();
	}

	public void setValue(Object object)
	{
	}

	public int topLevelCommit()
	{
		//only do this after first increase has been done
		if (mValue > 1)
		{
			qautil.qadebug("have we processed this");
			try
			{
				ServerIORStore.storeIOR(_uniquePrefix + "resource_" + mId, "restored");
			}
			catch (Exception e)
			{
				qautil.debug("error whilst writing result", e);
			}
		}
		return TwoPhaseOutcome.FINISH_OK;
	}

	public int topLevelAbort()
	{
		return TwoPhaseOutcome.FINISH_OK;
	}

	public int topLevelPrepare()
	{
		return TwoPhaseOutcome.PREPARE_OK;
	}

	public int nestedCommit()
	{
		return TwoPhaseOutcome.FINISH_OK;
	}

	public int nestedAbort()
	{
		return TwoPhaseOutcome.FINISH_OK;
	}

	public int nestedPrepare()
	{
		return TwoPhaseOutcome.PREPARE_OK;
	}

	public void alter(AbstractRecord abstractRecord)
	{
	}

	public void merge(AbstractRecord abstractRecord)
	{
	}

	public boolean shouldAdd(AbstractRecord abstractRecord)
	{
		return false;
	}

	public boolean shouldAlter(AbstractRecord abstractRecord)
	{
		return false;
	}

	public boolean shouldMerge(AbstractRecord abstractRecord)
	{
		return false;
	}

	public boolean shouldReplace(AbstractRecord abstractRecord)
	{
		return false;
	}

	/**
	 * Override method to indicate we want this object to be saved.
	 */
	public boolean doSave()
	{
		return true;
	}

	public boolean save_state(OutputObjectState objectState, int objectType)
	{
		qautil.qadebug("save state called when value = " + mValue);
		super.save_state(objectState, objectType);
		try
		{
			objectState.packInt(mValue);
			return true;
		}
		catch (Exception exception)
		{
			qautil.debug("BasicAbstractRecord.save_state: ", exception);
			return false;
		}
	}

	/**
	 * As this is an abstract record restore state does not function as a ait object
	 * but will be used by the crash recovery engine.
	 */
	public boolean restore_state(InputObjectState objectState, int objectType)
	{
		qautil.qadebug("restore state called");
		try
		{
			ServerIORStore.storeIOR(_uniquePrefix + "resource_" + mId, "restored");
		}
		catch (Exception e)
		{
			qautil.debug("error whilst writing result", e);
		}
		super.restore_state(objectState, objectType);
		try
		{
			mValue = objectState.unpackInt();
			
		        qautil.qadebug("value is "+mValue);
		        
			return true;
		}
		catch (Exception exception)
		{
			qautil.debug("BasicAbstractRecord.restore_state: ", exception);
			return false;
		}
	}

	public String type()
	{
		return "/StateManager/BasicAbstractRecord";
	}

	/**
	 * My methods to test abstract record is being processed correctly by the transaction
	 * manager.
	 */
	public void increase()
	{
		mValue++;
	}

	public int getValue()
	{
		return mValue;
	}

	private String _uniquePrefix = "";
	private int mId = 0;

	private static int mValue = 0;

}