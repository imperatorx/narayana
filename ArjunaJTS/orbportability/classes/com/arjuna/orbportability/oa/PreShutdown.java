/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.orbportability.oa;

/**
 * Instances of classes derived from this interface can be registered with
 * the system and do any tidy-up necessary after the ORB has
 * been shutdown.
 *
 * @author Mark Little (mark@arjuna.com)
 * @version $Id: PreShutdown.java 2342 2006-03-30 13:06:17Z  $
 * @since JTS 1.0.
 */

public abstract class PreShutdown extends Shutdown
{
    
public abstract void work ();

protected PreShutdown (String name)
    {
	super(name);
    };

}