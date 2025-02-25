/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.hp.mwtests.ts.arjuna.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Test;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.state.InputBuffer;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputBuffer;
import com.arjuna.ats.arjuna.state.OutputObjectState;
import com.arjuna.ats.internal.arjuna.common.UidHelper;

public class IOStateUnitTest
{
    @Test
    public void testIOObjectBuffer() throws Exception
    {
        PrintWriter pw = new PrintWriter(new StringWriter());
        OutputBuffer obuff = new OutputBuffer(1024);
        
        obuff.print(pw);
        
        assertTrue(obuff.toString() != null);
        
        OutputBuffer tobuff = new OutputBuffer(obuff);
        
        assertTrue(tobuff.valid());
        
        InputBuffer ibuff = new InputBuffer();
        
        ibuff.print(pw);
        
        InputBuffer tibuff = new InputBuffer(ibuff);
        
        assertEquals(tibuff.valid(), false);
    }
    
    @Test
    public void testIOObjectState() throws Exception
    {
        OutputObjectState oos = new OutputObjectState(new Uid(), "");
        
        oos.packBoolean(true);
        oos.packByte((byte) 0);
        oos.packChar('a');
        oos.packDouble(1.0);
        oos.packFloat((float) 1.0);
        oos.packInt(1);
        oos.packLong(1234);
        oos.packShort((short) 10);
        oos.packString("test");
        
        assertTrue(oos.valid());
        
        PrintWriter pw = new PrintWriter(new StringWriter());
        
        oos.print(pw);
        
        assertTrue(oos.length() != 0);
        assertTrue(oos.notempty());
        assertTrue(oos.stateUid() != Uid.nullUid());
        assertTrue(oos.buffer() != null);
        assertTrue(oos.size() > 0);
        assertTrue(oos.type() != null);
        
        OutputObjectState temp = new OutputObjectState(oos);

        assertTrue(temp.toString() != null);
        
        InputObjectState ios = new InputObjectState(oos);

        assertTrue(ios.buffer() != null);
        assertTrue(ios.length() > 0);
        assertTrue(ios.notempty());
        assertTrue(ios.size() > 0);
        assertTrue(ios.stateUid() != Uid.nullUid());
        
        assertTrue(ios.valid());
        
        ios.print(pw);
        
        InputObjectState is = new InputObjectState(ios);

        assertTrue(is.toString() != null);
        
        assertTrue(ios.unpackBoolean());
        assertEquals(ios.unpackByte(), (byte) 0);
        assertEquals(ios.unpackChar(), 'a');
        assertTrue(ios.unpackDouble() == 1.0);
        assertTrue(ios.unpackFloat() == (float) 1.0);
        assertEquals(ios.unpackInt(), 1);
        assertEquals(ios.unpackLong(), 1234);
        assertEquals(ios.unpackShort(), (short) 10);
        assertEquals(ios.unpackString(), "test");
        
        InputObjectState c = new InputObjectState();
        OutputObjectState buff = new OutputObjectState();
        OutputObjectState o = new OutputObjectState();
        Uid u = new Uid();
        
        buff.packString("foobar");
        UidHelper.packInto(u, buff);

        buff.packInto(o);
        
        InputBuffer ibuff = new InputBuffer(o.buffer());
        
        c.copy(ios);
        ios.unpackFrom(ibuff);
        ios.copyFrom(new OutputObjectState());
        
        assertTrue(ios.toString() != null);
        
        oos.reset();
        oos.rewrite();
        
        oos.packInto(new OutputObjectState());
        oos.copy(new OutputObjectState());
        
        assertTrue(oos.toString() != null);
        
        temp = new OutputObjectState(new Uid(), "", null);
        
        assertFalse(temp.valid());
    }
}