/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.ats.internal.jts.recovery.contact;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.common.recoveryPropertyManager;
import com.arjuna.ats.arjuna.objectstore.RecoveryStore;
import com.arjuna.ats.arjuna.objectstore.StoreManager;
import com.arjuna.ats.arjuna.recovery.ExpiryScanner;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.internal.arjuna.common.UidHelper;
import com.arjuna.ats.jts.logging.jtsLogger;


/**
 * This class is a plug-in module for the recovery manager.  This
 * class is responsible for the removing contact items that are too old
 */
public class ExpiredContactScanner implements ExpiryScanner
{
    public ExpiredContactScanner ()
    {

	if (jtsLogger.logger.isDebugEnabled()) {
        jtsLogger.logger.debug("ExpiredContactScanner created, with expiry time of "+_expiryTime+" seconds");
    }
	_recoveryStore = StoreManager.getRecoveryStore();
	_itemTypeName = FactoryContactItem.getTypeName();
    
    }

    /**
     * This is called periodically by the RecoveryManager
     */
    public void scan ()
    {

	// calculate the time before which items will be removed
	Date oldestSurviving = new Date( new Date().getTime() - _expiryTime * 1000);

	if (jtsLogger.logger.isDebugEnabled()) {
        jtsLogger.logger.debug("ExpiredContactScanner - scanning to remove items from before "+_timeFormat.format(oldestSurviving));
    }
	try
	{

	    InputObjectState uids = new InputObjectState();
	    
	    // find the uids of all the contact items
	    if (_recoveryStore.allObjUids(_itemTypeName, uids))
	    {
		Uid theUid = null;

		boolean endOfUids = false;

		while (!endOfUids)
		{
		    // extract a uid
		    theUid = UidHelper.unpackFrom(uids);

		    if (theUid.equals(Uid.nullUid()))
			endOfUids = true;
		    else
		    {
			Uid newUid = new Uid(theUid);
			
			FactoryContactItem anItem = FactoryContactItem.recreate(newUid);
			if (anItem != null) 
			{
			    Date timeOfDeath = anItem.getDeadTime();
			    if (timeOfDeath != null && timeOfDeath.before(oldestSurviving)) 
			    {
                    jtsLogger.i18NLogger.info_recovery_ExpiredContactScanner_3(newUid);
				_recoveryStore.remove_committed(newUid, _itemTypeName);
			    }
			}
		    }
		}
	    }
	}
	catch (Exception e)
	{
	    // end of uids!
	}
    }
    
    public boolean toBeUsed()
    {
	return _expiryTime != 0;
    }

    private String	 _itemTypeName;
    private RecoveryStore _recoveryStore;
    private static final int _expiryTime = recoveryPropertyManager.getRecoveryEnvironmentBean()
            .getTransactionStatusManagerExpiryTime() * 60 * 60;
    private static final SimpleDateFormat _timeFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

}