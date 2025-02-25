/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.jboss.jbossts.qa.ArjunaCore.StateManager.impl;

import com.arjuna.ats.arjuna.ObjectType;
import com.arjuna.ats.arjuna.StateManager;
import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.coordinator.BasicAction;
import com.arjuna.ats.arjuna.coordinator.RecordType;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputObjectState;
import org.jboss.jbossts.qa.ArjunaCore.Utils.qautil;

/**
 * Simple record used to test AtomicAction
 */
public class BasicStateRecord extends StateManager
{
	/**
	 * This constructor will be the default and will not make the object
	 * persistent allowing the test to run quicker.
	 */
	public BasicStateRecord()
	{
		super();
		qautil.qadebug("starting construction");
		activate();
		modified();
		deactivate();
		qautil.qadebug("ending construction");
	}

	/**
	 * This constructor will be used with the crashrecovery group
	 * of tests to ensure the objects state has been persisted to
	 * disk.
	 */
	public BasicStateRecord(int id)
	{
		super(ObjectType.ANDPERSISTENT);

		qautil.qadebug("starting construction");
		activate();
		modified();
		deactivate();
		mId = id;
		qautil.qadebug("ending construction");
	}

	/**
	 * This constructor will be used to recreate an object after a
	 * crash has occured.
	 */
	public BasicStateRecord(Uid oldId)
	{
		super(oldId, ObjectType.ANDPERSISTENT);

		qautil.qadebug("starting construction");
		activate();
		deactivate();

		qautil.qadebug("ending construction");
	}

	public int typeIs()
	{
		return RecordType.USER_DEF_FIRST0;
	}

	/**
	 * My methods to test abstract record is being processed correctly by the transaction
	 * manager.
	 */
	public void increase()
	{

		qautil.qadebug("start increase");
		activate();
		modified();
		mValue++;
		if (BasicAction.Current() == null)
		{
			deactivate();
		}
		qautil.qadebug("end increase");
	}

	/**
	 * Get value should realy use activate etc we will look into this.
	 */
	public int getValue()
	{
		return mValue;
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

	public boolean restore_state(InputObjectState objectState, int objectType)
	{

		qautil.qadebug("restore state called");
		super.restore_state(objectState, objectType);
		try
		{
			mValue = objectState.unpackInt();
			qautil.qadebug("value restored = " + mValue);
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
		return "/StateManager/BasicStateRecord";
	}

	private int mValue = 0;
	private int mId = 0;
}