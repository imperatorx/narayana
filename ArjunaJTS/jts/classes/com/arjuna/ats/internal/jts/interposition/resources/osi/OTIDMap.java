/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.ats.internal.jts.interposition.resources.osi;

import java.util.Enumeration;
import java.util.Hashtable;

import org.omg.CosTransactions.otid_t;

import com.arjuna.ats.arjuna.common.Uid;

/*
 * Class which maintains a mapping of otid to Uid.
 * It automatically updates itself when it sees a
 * new Uid, and is pruned when transactions terminate.
 */

class OTIDWrapper
{
    
    public OTIDWrapper (otid_t otid)
    {
	_otid = otid;
	_uid = new Uid();
    }

    public Uid get_uid ()
    {
	return _uid;
    }

    public otid_t get_otid ()
    {
	return _otid;
    }

    private otid_t _otid;
    private Uid _uid;
 
};
    
public class OTIDMap
{

    public static synchronized Uid find (otid_t otid)
    {
	OTIDWrapper element = null;
	
	if (!_otids.isEmpty())
	{
	    Enumeration e = _otids.elements();

	    while (e.hasMoreElements())
	    {
		element = (OTIDWrapper) e.nextElement();

		if (OTIDMap.same(element.get_otid(), otid))
		    return element.get_uid();
	    }
	}
	
	/*
	 * Got here, so must be new otid.
	 */

	element = new OTIDWrapper(otid);
    
	_otids.put(element.get_uid(), element);

	return element.get_uid();
    }

    public static synchronized boolean remove (Uid uid)
    {
	OTIDWrapper wrapper = (OTIDWrapper) _otids.remove(uid);

	if (wrapper != null)
	{
	    wrapper = null;

	    return true;
	}
	else
	    return false;
    }

    /*
     * Only called from synchronized methods.
     */
    
    private static boolean same (otid_t otid1, otid_t otid2)
    {
	if ((otid1.formatID == otid2.formatID) &&
	    (otid1.bqual_length == otid2.bqual_length))
	{
	    for (int i = 0; i < otid1.bqual_length; i++)
	    {
		if (otid1.tid[i] != otid2.tid[i])
		    return false;
	    }

	    /*
	     * Got here, so must be equal!
	     */
	    
	    return true;
	}
	else
	    return false;
    }

    private static Hashtable _otids = new Hashtable();
 
}