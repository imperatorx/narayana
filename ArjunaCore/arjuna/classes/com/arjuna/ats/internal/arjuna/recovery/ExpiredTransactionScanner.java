/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */


package com.arjuna.ats.internal.arjuna.recovery;

import java.util.Hashtable;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.exceptions.ObjectStoreException;
import com.arjuna.ats.arjuna.logging.tsLogger;
import com.arjuna.ats.arjuna.objectstore.RecoveryStore;
import com.arjuna.ats.arjuna.objectstore.StoreManager;
import com.arjuna.ats.arjuna.recovery.ExpiryScanner;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputObjectState;
import com.arjuna.ats.internal.arjuna.common.UidHelper;

/**
 * This class is a plug-in module for the recovery manager. This class is
 * responsible for the removing transaction status manager items that are too
 * old.
 */

public class ExpiredTransactionScanner implements ExpiryScanner
{
	public ExpiredTransactionScanner(String typeName, String movedTypeName)
	{
		_recoveryStore = StoreManager.getRecoveryStore();
		_typeName = typeName;
		_movedTypeName = movedTypeName;
	}

	/**
	 * This is called periodically by the RecoveryManager
	 */
	public void scan()
	{
		boolean initialScan = false;

		if (_scanM == null)
		{
			_scanM = new Hashtable();
			initialScan = true;
		}

		try
		{
			InputObjectState uids = new InputObjectState();

			// take a snapshot of the log

			if (_recoveryStore.allObjUids(_typeName, uids))
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

						if (initialScan)
							_scanM.put(newUid, newUid);
						else
						{
							if (!_scanM.contains(newUid))
							{
								if (_scanN == null)
									_scanN = new Hashtable();

								_scanN.put(newUid, newUid);
							}
							else
							// log is present in this iteration, so move it
							{
								tsLogger.i18NLogger.info_recovery_ExpiredTransactionScanner_4(newUid);

								try
								{
								    moveEntry(newUid);
								}
								catch (Exception ex)
								{
                                    tsLogger.i18NLogger.warn_recovery_ExpiredTransactionScanner_2(newUid, ex);

									_scanN.put(newUid, newUid);
								}
							}
						}
					}
				}

				if (_scanN != null)
				{
					_scanM = _scanN;
					_scanN = null;
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
		return true;
	}

	public boolean moveEntry (Uid newUid) throws ObjectStoreException
	{
	    InputObjectState state = _recoveryStore.read_committed(newUid, _typeName);
	    boolean res = false;
	    
	    if (state != null) // just in case recovery
	        // kicked-in
	    {
	        boolean moved = _recoveryStore.write_committed(newUid, _movedTypeName, new OutputObjectState(state));

	        if (!moved) {
                tsLogger.logger.debugf("Removing old transaction status manager item %s", newUid);
                }
	        else {
	            res = _recoveryStore.remove_committed(newUid, _typeName);
                    tsLogger.i18NLogger.warn_recovery_ExpiredTransactionStatusManagerScanner_6(newUid);
                }

	    }
          
	    return res;
	}
	
	private String _typeName;

	private String _movedTypeName;

	private RecoveryStore _recoveryStore;

	private Hashtable _scanM = null;

	private Hashtable _scanN = null;

}