/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.hp.mwtests.ts.arjuna.abstractrecords;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.arjuna.ats.arjuna.ObjectType;
import com.arjuna.ats.arjuna.coordinator.RecordType;
import com.arjuna.ats.arjuna.coordinator.TwoPhaseOutcome;
import com.arjuna.ats.arjuna.objectstore.ParticipantStore;
import com.arjuna.ats.arjuna.objectstore.StateType;
import com.arjuna.ats.arjuna.objectstore.StoreManager;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputObjectState;
import com.arjuna.ats.internal.arjuna.abstractrecords.DisposeRecord;
import com.arjuna.ats.internal.arjuna.abstractrecords.PersistenceRecord;
import com.hp.mwtests.ts.arjuna.resources.ExtendedObject;

public class DisposeRecordUnitTest
{
    @Test
    public void test ()
    {
        ParticipantStore store = StoreManager.setupStore(null, StateType.OS_UNSHARED);
        
        DisposeRecord cr = new DisposeRecord(store, new ExtendedObject());
        
        assertFalse(cr.propagateOnAbort());
        assertTrue(cr.propagateOnCommit());
        assertEquals(cr.typeIs(), RecordType.DISPOSE);
        
        assertTrue(cr.type() != null);
        assertEquals(cr.doSave(), true);

        assertEquals(cr.nestedPrepare(), TwoPhaseOutcome.PREPARE_OK);
        assertEquals(cr.nestedAbort(), TwoPhaseOutcome.FINISH_OK);

        cr = new DisposeRecord(store, new ExtendedObject());
        
        assertEquals(cr.nestedPrepare(), TwoPhaseOutcome.PREPARE_OK);
        assertEquals(cr.nestedCommit(), TwoPhaseOutcome.FINISH_OK);
        
        cr = new DisposeRecord(store, new ExtendedObject());
        
        assertEquals(cr.topLevelPrepare(), TwoPhaseOutcome.PREPARE_OK);
        assertEquals(cr.topLevelAbort(), TwoPhaseOutcome.FINISH_OK);
 
        cr = new DisposeRecord(store, new ExtendedObject());
        
        assertEquals(cr.topLevelPrepare(), TwoPhaseOutcome.PREPARE_OK);
        assertEquals(cr.topLevelCommit(), TwoPhaseOutcome.FINISH_OK);
        
        cr = new DisposeRecord();
        
        assertFalse(cr.shouldAdd(new PersistenceRecord()));
        assertFalse(cr.shouldAlter(new PersistenceRecord()));
        assertFalse(cr.shouldMerge(new PersistenceRecord()));
        assertFalse(cr.shouldReplace(new PersistenceRecord()));
        
        assertFalse(cr.save_state(new OutputObjectState(), ObjectType.ANDPERSISTENT));
        assertFalse(cr.restore_state(new InputObjectState(), ObjectType.ANDPERSISTENT));
    }
}