/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.hp.mwtests.ts.jts.orbspecific.resources;

import java.io.IOException;

import org.omg.CORBA.IntHolder;
import org.omg.CORBA.SystemException;

import com.arjuna.ats.arjuna.ObjectType;
import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputObjectState;
import com.arjuna.ats.jts.extensions.AtomicTransaction;
import com.arjuna.ats.txoj.Lock;
import com.arjuna.ats.txoj.LockManager;
import com.arjuna.ats.txoj.LockMode;
import com.arjuna.ats.txoj.LockResult;

public class StackImple extends LockManager implements com.hp.mwtests.ts.jts.TestModule.stackOperations
{

    public StackImple ()
    {
	super (ObjectType.ANDPERSISTENT);

	top = 0;

	for (int i = 0; i < ARRAY_SIZE; i++)
	    array[i] = 0;

	AtomicTransaction A = new AtomicTransaction();

	try
	{
	    A.begin();

	    if (setlock(new Lock(LockMode.WRITE), 0) == LockResult.GRANTED)
		A.commit(false);
	    else
		A.rollback();
	}
	catch (Exception e1)
	{
	    System.err.println(e1);

	    try
	    {
		A.rollback();
	    }
	    catch (Exception e2)
	    {
		System.err.println(e2);
	    }

	    System.exit(1);
	}
    }

    public StackImple (Uid uid)
    {
	super(uid);
	
	top = 0;

	for (int i = 0; i < ARRAY_SIZE; i++)
	    array[i] = 0;

	AtomicTransaction A = new AtomicTransaction();

	try
	{
	    A.begin();

	    if (setlock(new Lock(LockMode.WRITE), 0) == LockResult.GRANTED)
		A.commit(false);
	    else
		A.rollback();
	}
	catch (Exception e1)
	{
	    System.err.println(e1);

	    try
	    {
		A.rollback();
	    }
	    catch (Exception e2)
	    {
		System.err.println(e2);
	    }

	    System.exit(1);
	}	
    }

    public void finalize () throws Throwable
    {
	super.terminate();
	super.finalize();
    }

    public int push (int val) throws SystemException
    {
	AtomicTransaction A = new AtomicTransaction();
	int res = 0;

	try
	{
	    A.begin();

	    if (setlock(new Lock(LockMode.WRITE), 0) == LockResult.GRANTED)
	    {
		if (top < ARRAY_SIZE)
		{
		    array[top] = val;
		    top++;
		}
		else
		    res = -1;

		if (res == 0)
		{
		    A.commit(false);
		}
		else
		    A.rollback();
	    }
	    else
		A.rollback();
	}
	catch (Exception e1)
	{
	    try
	    {
		A.rollback();
	    }
	    catch (Exception e2)
	    {
		System.err.println(e2);
	    }

	    res = -1;
	}

	return res;
    }

    public int pop (IntHolder val) throws SystemException
    {
	AtomicTransaction A = new AtomicTransaction();
	int res = 0;

	try
	{
	    A.begin();

	    if (setlock(new Lock(LockMode.WRITE), 0) == LockResult.GRANTED)
	    {
		if (top > 0)
		{
		    top--;
		    val.value = array[top];
		}
		else
		    res = -1;

		if (res == 0)
		{
		    A.commit(false);
		}
		else
		    A.rollback();
	    }
	    else
	    {
		A.rollback();
	    }
	}
	catch (Exception e1)
	{
	    try
	    {
		A.rollback();
	    }
	    catch (Exception e2)
	    {
		System.err.println(e2);
	    }

	    res = -1;
	}
	
	return res;
    }

    public void printStack () throws SystemException
    {
	AtomicTransaction A = new AtomicTransaction();

	try
	{
	    A.begin();
    
	    if (setlock(new Lock(LockMode.WRITE), 0) == LockResult.GRANTED)
	    {
		if (top > 0)
		{
		    System.out.println("\nContents of stack:");
	
		    for (int i = 0; i < top; i++)
			System.out.println("\t"+array[i]);
		}
		else
		    System.out.println("\nStack is empty.");

		A.commit(false);
	    }
	    else
	    {
		System.out.println("printStack: could not set WRITE lock.");
	
		A.rollback();
	    }
	}
	catch (Exception e1)
	{
	    try
	    {
		A.rollback();
	    }
	    catch (Exception e2)
	    {
		System.err.println(e2);
	    }
	}
    }

    public boolean save_state (OutputObjectState objectState, int ot)
    {
	if (!super.save_state(objectState, ot))
	    return false;
	
	try
	{
	    objectState.packInt(top);

	    for (int i = 0; i < top; i++)
	    {
		objectState.packInt(array[i]);
	    }

	    return true;
	}
	catch (IOException e)
	{
	    return false;
	}
    }

    public boolean restore_state (InputObjectState objectState, int ot)
    {
	if (!super.restore_state(objectState, ot))
	    return false;

	try
	{
	    top = objectState.unpackInt();

	    for (int j = 0; j < ARRAY_SIZE; j++)
		array[j] = 0;
    
	    for (int i = 0; i < top; i++)
	    {
		array[i] = objectState.unpackInt();
	    }

	    return true;
	}
	catch (IOException e)
	{
	    return false;
	}
    }

    public String type ()
    {
	return "/StateManager/LockManager/StackImple";
    }

    public static final int ARRAY_SIZE = 10;

    private int[] array = new int[ARRAY_SIZE];
    private int top;

}