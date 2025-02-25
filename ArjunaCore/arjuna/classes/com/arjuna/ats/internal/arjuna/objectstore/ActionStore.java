/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.ats.internal.arjuna.objectstore;

import com.arjuna.ats.arjuna.common.ObjectStoreEnvironmentBean;
import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.exceptions.ObjectStoreException;
import com.arjuna.ats.arjuna.logging.tsLogger;
import com.arjuna.ats.arjuna.objectstore.StateStatus;
import com.arjuna.ats.arjuna.objectstore.StateType;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputObjectState;

/**
 * The basic transaction log implementation. Uses the no file-level locking
 * implementation of the file system store since only a single user (the
 * coordinator) can ever be manipulating the action's state.
 *
 * @author Mark Little (mark@arjuna.com)
 * @version $Id: ActionStore.java 2342 2006-03-30 13:06:17Z $
 * @since JTS 1.0.
 */

public class ActionStore extends ShadowNoFileLockStore
{
    /**
     * @return current state of object. Assumes that genPathName allocates
     *         enough extra space to allow extra chars to be added. Action
     *         stores only store committed objects
     */

    public int currentState (Uid objUid, String tName)
            throws ObjectStoreException
    {
        int theState = StateStatus.OS_UNKNOWN;

        String path = genPathName(objUid, tName, StateType.OS_ORIGINAL);

        if (exists(path))
            theState = StateStatus.OS_COMMITTED;

        path = null;

        if (tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("ActionStore.currentState("+objUid+", "+tName+") - returning "+
                    StateStatus.stateStatusString(theState));
        }

        return theState;
    }

    /**
     * Commit a previous write_state operation which was made with the SHADOW
     * StateType argument. This is achieved by renaming the shadow and removing
     * the hidden version.
     */

    public boolean commit_state (Uid objUid, String tName)
            throws ObjectStoreException
    {
        if (tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("ActionStore.commit_state(" + objUid + ", " + tName + ")");
        }

        boolean result = false;

        if (currentState(objUid, tName) == StateStatus.OS_COMMITTED)
            result = true;

        return result;
    }

    public boolean hide_state (Uid u, String tn) throws ObjectStoreException
    {
        if (tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("ActionStore.hide_state(" + u + ", " + tn + ")");
        }

        return false;
    }

    public boolean reveal_state (Uid u, String tn) throws ObjectStoreException
    {
        if (tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("ActionStore.reveal_state(" + u + ", " + tn + ")");
        }

        return false;
    }

    public InputObjectState read_committed (Uid storeUid, String tName)
            throws ObjectStoreException
    {
        if (tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("ActionStore.read_committed(" + storeUid + ", " + tName
                    + ")");
        }

        return super.read_committed(storeUid, tName);
    }

    public InputObjectState read_uncommitted (Uid u, String tn)
            throws ObjectStoreException
    {
        if (tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("ActionStore.read_uncommitted(" + u + ", " + tn + ")");
        }

        return null;
    }

    public boolean remove_committed (Uid storeUid, String tName)
            throws ObjectStoreException
    {
        if (tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("ActionStore.remove_committed(" + storeUid + ", " + tName
                    + ")");
        }

        return super.remove_committed(storeUid, tName);
    }

    public boolean remove_uncommitted (Uid u, String tn)
            throws ObjectStoreException
    {
        if (tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("ActionStore.remove_uncommitted(" + u + ", " + tn + ")");
        }

        return false;
    }

    public boolean write_committed (Uid storeUid, String tName,
                                    OutputObjectState state) throws ObjectStoreException
    {
        if (tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("ActionStore.write_committed(" + storeUid + ", " + tName
                    + ")");
        }

        return super.write_committed(storeUid, tName, state);
    }

    public boolean write_uncommitted (Uid u, String tn, OutputObjectState s)
            throws ObjectStoreException
    {
        if (tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("ActionStore.write_uncommitted(" + u + ", " + tn + ", " + s
                    + ")");
        }

        return false;
    }

    public ActionStore(ObjectStoreEnvironmentBean objectStoreEnvironmentBean) throws ObjectStoreException
    {
        super(objectStoreEnvironmentBean);

        // overrides parents use of isObjectStoreSync
        doSync = objectStoreEnvironmentBean.isTransactionSync();
    }
}