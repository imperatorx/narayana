/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.hp.mwtests.ts.jts.orbspecific.resources;

import org.omg.CORBA.SystemException;
import org.omg.CORBA.UNKNOWN;
import org.omg.CosTransactions.Synchronization;
import org.omg.CosTransactions.SynchronizationHelper;

import com.arjuna.ats.internal.jts.ORBManager;
import com.arjuna.ats.jts.utils.Utility;

public class demosync extends org.omg.CosTransactions.SynchronizationPOA
{
    
    public demosync ()
    {
	this(true);
    }

    public demosync (boolean errors)
    {
	ORBManager.getPOA().objectIsReady(this);
	
	ref = SynchronizationHelper.narrow(ORBManager.getPOA().corbaReference(this));

	_errors = errors;
    }

    public Synchronization getReference ()
    {
	return ref;
    }
 
    public void before_completion () throws SystemException
    {
	if (_errors)
	{
	    System.out.println("DEMOSYNC : BEFORE_COMPLETION");
	    System.out.println("Synchronization throwing exception.");
	
	    throw new UNKNOWN();
	}
    }

    public void after_completion (org.omg.CosTransactions.Status status) throws SystemException
    {
	if (_errors)
	{
	    System.out.println("DEMOSYNC : AFTER_COMPLETION ( "+Utility.stringStatus(status)+" )");

	    System.out.println("Synchronization throwing exception.");
	
	    throw new UNKNOWN(); // should not cause any affect!
	}
    }

    private Synchronization ref;
    private boolean _errors;
    
}