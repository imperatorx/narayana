/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.hp.mwtests.ts.txoj.abstactrecords;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.junit.Test;

import com.arjuna.ats.arjuna.AtomicAction;
import com.arjuna.ats.arjuna.ObjectModel;
import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.coordinator.ActionStatus;
import com.arjuna.ats.internal.txoj.abstractrecords.CadaverLockRecord;
import com.arjuna.ats.internal.txoj.abstractrecords.LockRecord;
import com.hp.mwtests.ts.txoj.common.resources.AtomicObject;

public class CadaverUnitTest
{
    @Test
    public void testCommit () throws Exception
    {
	AtomicAction A = new AtomicAction();
	AtomicObject B = new AtomicObject(ObjectModel.MULTIPLE);
	Uid u = B.get_uid();
	
	A.begin();

	B.set(1234);
	 
	A.commit();
	
	A = new AtomicAction();	
	B = new AtomicObject(u, ObjectModel.MULTIPLE);
	
	A.begin();
	
	AtomicAction C = new AtomicAction();
	
	C.begin();
	
	assertEquals(B.get(), 1234);
	
	B.set(5678);
	
	B.terminate();

	C.commit();
	
	assertEquals(A.commit(), ActionStatus.COMMITTED);
    }
    
    @Test
    public void testAbort () throws Exception
    {
        AtomicAction A = new AtomicAction();
        AtomicObject B = new AtomicObject(ObjectModel.MULTIPLE);
        Uid u = B.get_uid();
        
        A.begin();

        B.set(1234);
         
        A.commit();
        
        A = new AtomicAction();
        
        B = new AtomicObject(u, ObjectModel.MULTIPLE);
        
        A.begin();
        
        AtomicAction C = new AtomicAction();
        
        C.begin();
        
        assertEquals(B.get(), 1234);
        
        B.set(5678);
        
        B.terminate();

        C.commit();
        
        assertEquals(A.abort(), ActionStatus.ABORTED);
    }
 
    @Test
    public void testMultipleNestedCommit () throws Exception
    {
        AtomicAction A = new AtomicAction();
        AtomicObject B = new AtomicObject(ObjectModel.MULTIPLE);
        Uid u = B.get_uid();
        
        A.begin();
        
        B.set(1234);
         
        A.commit();
        
        A = new AtomicAction();
        B = new AtomicObject(u, ObjectModel.MULTIPLE);
        
        A.begin();
        
        AtomicAction C = new AtomicAction();
        
        C.begin();
        
        assertEquals(B.get(), 1234);

        B.set(5678);

        B.terminate();

        C.commit();
        
        assertEquals(A.commit(), ActionStatus.COMMITTED);
    }
    
    @Test
    public void testMultipleNestedAbort () throws Exception
    {
        AtomicAction A = new AtomicAction();
        AtomicObject B = new AtomicObject(ObjectModel.MULTIPLE);
        Uid u = B.get_uid();
        
        A.begin();

        B.set(1234);
         
        A.commit();
        
        A = new AtomicAction();
        B = new AtomicObject(u, ObjectModel.MULTIPLE);
        
        A.begin();

        AtomicAction C = new AtomicAction();
        
        C.begin();

        assertEquals(B.get(), 1234);
        
        B.set(5678);

        B.terminate();

        C.abort();
        
        assertEquals(A.commit(), ActionStatus.COMMITTED);
    }
    
    @Test
    public void testBasic () throws Exception
    {
        AtomicAction A = new AtomicAction();
        AtomicObject B = new AtomicObject();
        
        A.begin();
        
        CadaverLockRecord clr = new CadaverLockRecord(null, B, A);
        LockRecord lr = new LockRecord(B, A);
        
        assertTrue(clr.type() != null);
        
        clr.print(new PrintWriter(new ByteArrayOutputStream()));
        
        clr.replace(lr);
        
        A.abort();
    }
}