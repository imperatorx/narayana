/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.wstx.tests.common;

import com.arjuna.wst.*;

/**
 * @author Mark Little (mark.little@arjuna.com)
 * @version $Id: FailureParticipant.java,v 1.5 2004/09/09 08:48:40 kconner Exp $
 * @since 1.0.
 */

public class FailureParticipant implements Durable2PCParticipant
{

    public static final int FAIL_IN_PREPARE = 0;
    public static final int FAIL_IN_ROLLBACK = 1;
    public static final int FAIL_IN_COMMIT = 2;
    public static final int FAIL_IN_ONE_PHASE = 3;
    
    public static final int WRONG_STATE = 20;
    public static final int SYSTEM = 21;
    public static final int NONE = 22;
    
    public FailureParticipant (int failurePoint, int failureType)
    {
	_failurePoint = failurePoint;
	_failureType = failureType;
    _prepared = false;
    _resolved = false;
    }

    public final boolean passed ()
    {
	return _passed;
    }
    
    public final boolean prepared ()
    {
	return _prepared;
    }

    public final boolean resolved ()
    {
	return _resolved;
    }

    public Vote prepare () throws WrongStateException, SystemException
    {
	System.out.println("FailureParticipant.prepare");
	
    _prepared = true;
	if (_failurePoint == FAIL_IN_PREPARE)
	{
	    generateException();
	    
	    return new Aborted();
	}
	else
	    return new Prepared();
    }

    public void commit () throws WrongStateException, SystemException
    {
	System.out.println("FailureParticipant.commit");

    _resolved = true;
	if (_failurePoint == FAIL_IN_COMMIT)
	    generateException();

	if (_failurePoint == FAIL_IN_PREPARE)
	    _passed = false;
    }

    public void rollback () throws WrongStateException, SystemException
    {
    _resolved = true;
	System.out.println("FailureParticipant.rollback");
	
	if (_failurePoint == FAIL_IN_ROLLBACK)
	    generateException();
	
	if (_failurePoint == FAIL_IN_PREPARE)
	    _passed = true;
    }

    public void commitOnePhase () throws WrongStateException, SystemException
    {
    _resolved = true;
	System.out.println("FailureParticipant.commitOnePhase");

	if (_failurePoint == FAIL_IN_ONE_PHASE)
	    generateException();
	
	_passed = true;
    }

    public void unknown () throws SystemException
    {
    }

    public void error () throws SystemException
    {
    }

    private void generateException () throws WrongStateException, SystemException
    {
	switch (_failureType)
	{
	case WRONG_STATE:
	    throw new WrongStateException();
	case SYSTEM:
	    throw new SystemException();
	default:
	    break;
	}
    }
    
    private int     _failurePoint;
    private int     _failureType;
    private boolean _passed;
    private boolean _prepared;
    private boolean _resolved;

}