/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.ats.txoj.lockstore;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputObjectState;
import com.arjuna.ats.internal.txoj.Implementations;
import com.arjuna.ats.txoj.exceptions.LockStoreException;

/**
 * The lock store interface is the application's route to using a specific lock
 * store implementation. The interface dynamically binds to an implementation of
 * the right type.
 * 
 * @author Mark Little (mark@arjuna.com)
 * @version $Id: LockStore.java 2342 2006-03-30 13:06:17Z $
 * @since JTS 1.0.
 */

public abstract class LockStore
{
    public abstract InputObjectState read_state (Uid u, String tName)
            throws LockStoreException;

    /**
     * Remove the state from the lock store.
     */

    public abstract boolean remove_state (Uid u, String tname);

    /**
     * Write the state to the lock store.
     */

    public abstract boolean write_committed (Uid u, String tName,
            OutputObjectState state);

    protected LockStore ()
    {       
    }

    static
    {
        /*
         * Make sure the possible implementations are in the inventory.
         * Otherwise this is going to be a very short ride!
         */

        if (!Implementations.added())
            Implementations.initialise();
    }

}