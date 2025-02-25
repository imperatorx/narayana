/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.ats.internal.jts.orbspecific.recovery.recoverycoordinators;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.jts.logging.jtsLogger;

/**
 * Holds and manipulates the fields used to identify which branch of which transaction
 * a RecoveryCoordinator is concerned with. Split from {@link GenericRecoveryCoordinator} to
 * allow for default servant mechanism with POA
 *
 * @author Peter Furniss (peter.furniss@arjuna.com)
 * @version $Id: RecoveryCoordinatorId.java 2342 2006-03-30 13:06:17Z  $ 
 *
 */

public class RecoveryCoordinatorId
{
    /* fields have package access - we are close friends with GenericRecoveryCoordinator */
    Uid	      _RCUid;
    Uid	      _actionUid;
    Uid	    _originalProcessUid;
    boolean     _isServerTransaction;

    /**
     * Constructor with separate fields
     */
    RecoveryCoordinatorId (Uid RCUid, Uid actionUid, 
			   Uid processUid, boolean isServerTransaction)
    {
	_RCUid = RCUid;
	_actionUid = actionUid;
	_originalProcessUid = processUid;
	_isServerTransaction = isServerTransaction;
    }
    
    /**
     * Construct a string, to be used somehow in the objectkey (probably)
     * of a RecoveryCoordinator reference. This will be deconstructed in 
     * the reconstruct() which is passed such a string, to remake the
     * necessary RecoveryCoordinator when a replay_completion is received for it.
     *
     * Put here to make it in the same class as the deconstruction
     */
    String makeId()
    {
	String rcObjectKey = null;

	/*
	 * Pack the fields in to the string.
	 * perhaps replace ':' with '-' if required
	 *   (likely to be orb-specific requirement)
	 */

	try
	{
	    StringBuffer stringBuf = new StringBuffer();
	    stringBuf.append(_RCUid.toString());
	    stringBuf.append(_ObjKeyDelimiter);
	    stringBuf.append(_actionUid.toString());
	    stringBuf.append(_ObjKeyDelimiter);
	    stringBuf.append(_originalProcessUid.toString());
	    stringBuf.append(_ObjKeyDelimiter);
	    stringBuf.append(_isServerTransaction);
	    rcObjectKey = stringBuf.toString();

	    if (jtsLogger.logger.isDebugEnabled()) {
            jtsLogger.logger.debug("RecoveryCoordinatorId: created RCkey "+rcObjectKey);
        }
	}
	catch (Exception e) {
        jtsLogger.i18NLogger.warn_recovery_recoverycoordinators_RecoveryCoordinatorId_2(e);
    }
	return rcObjectKey;
    }

    /**
     *  Construct an id from the encoded string
     * @returns null if parse fails 
     */
    public static RecoveryCoordinatorId reconstruct(String encodedRCData)
    {
	if (jtsLogger.logger.isDebugEnabled()) {
        jtsLogger.logger.debug("RecoveryCoordinatorId(" + encodedRCData + ")");
    }
	Uid	      RCUid = null;
	Uid	      actionUid = null;
	Uid	    originalProcessUid = null;
	boolean     isServerTransaction = false;

	boolean ok = (encodedRCData != null);

	if (ok)
	{
	    int index1 = encodedRCData.indexOf(_ObjKeyDelimiter);
	    int index2 = 0;
	    
	    if (index1 != -1)
	    {
		String stringifiedRCUid = encodedRCData.substring(0, index1);
		RCUid = new Uid (stringifiedRCUid);
	    }
	    else
		ok = false;
	    
	    if (ok)
	    {
		index2 = encodedRCData.indexOf(_ObjKeyDelimiter, index1 +1);
		
		if (index2 != -1)
		{
		    String stringifiedTranUid = encodedRCData.substring(index1 +1, index2);
		    actionUid = new Uid (stringifiedTranUid);
		    index1 = index2;
		}
		else
		    ok = false;
	    }
	    
	    if (ok)
	    {
		index2 = encodedRCData.indexOf(_ObjKeyDelimiter, index1 +1);
		
		if (index2 != -1)
		{
		    String stringifiedProcessUid = encodedRCData.substring(index1 +1, index2);
		    originalProcessUid = new Uid (stringifiedProcessUid);
		    index1 = index2;
		}
		else
		    ok = false;
	    }

	    
	    if (ok)
	    {
		String stringifiedIsServerTransaction = encodedRCData.substring(index1 +1);
		isServerTransaction = (Boolean.valueOf(stringifiedIsServerTransaction)).booleanValue();
	    }
	}
	
	if (ok) {
	    return new RecoveryCoordinatorId (RCUid, actionUid,
				 originalProcessUid, isServerTransaction);
	} else {
        jtsLogger.i18NLogger.warn_recovery_recoverycoordinators_RecoveryCoordinatorId_3(encodedRCData);
        return null;
    }
    }

    /**
     * override base toString for clarity
     */
    public String toString()
    {
	return "(" + _RCUid+", "+_actionUid+", " + _originalProcessUid 
			 + (_isServerTransaction ? ", interposed-tx" : ", root-tx" ) + ")" ;

    }
    

    private static final char   _ObjKeyDelimiter = '*';

}