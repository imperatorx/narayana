/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.hp.mwtests.ts.arjuna.abstractrecords;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.junit.Test;

import com.arjuna.ats.arjuna.ObjectType;
import com.arjuna.ats.arjuna.common.arjPropertyManager;
import com.arjuna.ats.arjuna.coordinator.RecordType;
import com.arjuna.ats.arjuna.coordinator.TwoPhaseOutcome;
import com.arjuna.ats.arjuna.objectstore.ParticipantStore;
import com.arjuna.ats.arjuna.objectstore.StateType;
import com.arjuna.ats.arjuna.objectstore.StoreManager;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputObjectState;
import com.arjuna.ats.internal.arjuna.abstractrecords.PersistenceRecord;
import com.hp.mwtests.ts.arjuna.resources.ExtendedObject;

public class PersistenceRecordUnitTest
{
    @Test
    public void test ()
    {
        ParticipantStore store = StoreManager.setupStore(null, StateType.OS_UNSHARED);
        
        PersistenceRecord cr = new PersistenceRecord(new OutputObjectState(), store, new ExtendedObject());
        
        arjPropertyManager.getCoordinatorEnvironmentBean().setClassicPrepare(true);
        
        assertFalse(cr.propagateOnAbort());
        assertTrue(cr.propagateOnCommit());
        assertEquals(cr.typeIs(), RecordType.PERSISTENCE);
        
        assertTrue(cr.type() != null);
        assertEquals(cr.doSave(), true);

        assertEquals(cr.topLevelPrepare(), TwoPhaseOutcome.PREPARE_OK);
        assertEquals(cr.topLevelAbort(), TwoPhaseOutcome.FINISH_ERROR);
 
        cr = new PersistenceRecord(new OutputObjectState(), store, new ExtendedObject());
        
        assertEquals(cr.topLevelPrepare(), TwoPhaseOutcome.PREPARE_OK);
        assertEquals(cr.topLevelCommit(), TwoPhaseOutcome.FINISH_OK);

        cr.print(new PrintWriter(new ByteArrayOutputStream()));
        
        OutputObjectState os = new OutputObjectState();
        
        assertTrue(cr.save_state(os, ObjectType.ANDPERSISTENT));
        assertTrue(cr.restore_state(new InputObjectState(os), ObjectType.ANDPERSISTENT));
        
        assertEquals(cr.topLevelCleanup(), TwoPhaseOutcome.FINISH_OK);
    }
}