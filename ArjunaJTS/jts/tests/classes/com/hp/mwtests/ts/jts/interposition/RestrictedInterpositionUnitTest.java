/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.hp.mwtests.ts.jts.interposition;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.omg.CosTransactions.PropagationContext;

import com.arjuna.ats.internal.jts.OTSImpleManager;
import com.arjuna.ats.internal.jts.interposition.resources.restricted.RestrictedInterposition;
import com.arjuna.ats.internal.jts.interposition.resources.restricted.RestrictedInterpositionCreator;
import com.arjuna.ats.internal.jts.orbspecific.ControlImple;
import com.hp.mwtests.ts.jts.resources.TestBase;

public class RestrictedInterpositionUnitTest extends TestBase
{
    @Test
    public void test () throws Exception
    {
        RestrictedInterposition inter = new RestrictedInterposition();
        
        OTSImpleManager.current().begin();
        OTSImpleManager.current().begin();
        
        PropagationContext ctx = OTSImpleManager.current().get_control().get_coordinator().get_txcontext();
        
        ControlImple cont = inter.setupHierarchy(ctx);
        
        RestrictedInterpositionCreator creator = new RestrictedInterpositionCreator();
        
        assertTrue(creator.recreateLocal(ctx) != null);       
        assertTrue(creator.recreate(ctx) != null);
        
        OTSImpleManager.current().rollback();
        OTSImpleManager.current().rollback();
    }
}