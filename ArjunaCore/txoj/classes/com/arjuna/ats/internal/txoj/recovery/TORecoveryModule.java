/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.ats.internal.txoj.recovery;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.exceptions.ObjectStoreException;
import com.arjuna.ats.arjuna.objectstore.ObjectStoreAPI;
import com.arjuna.ats.arjuna.objectstore.StateStatus;
import com.arjuna.ats.arjuna.objectstore.StoreManager;
import com.arjuna.ats.arjuna.recovery.RecoveryModule;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.internal.arjuna.common.UidHelper;
import com.arjuna.ats.txoj.logging.txojLogger;

/**
 * This class is a plug-in module for the recovery manager. This class is
 * responsible for the recovery of Transactional Objects (aka AIT objects),
 * i.e., objects that derive from LockManager and StateManager.
 */

public class TORecoveryModule implements RecoveryModule
{

    /**
     * Create the module to scan in the default location for object states. Any
     * modifications to locations must occur in the properties file.
     */

    @SuppressWarnings("unchecked")
    public TORecoveryModule()
    {
        if (txojLogger.logger.isDebugEnabled()) {
            txojLogger.logger.debug("TORecoveryModule created");
        }

        /*
         * Where are TO's stored. Default.
         */

        _objectStore = StoreManager.getTxOJStore();
    }

    public void periodicWorkFirstPass ()
    {
        if(txojLogger.logger.isDebugEnabled()) {
            txojLogger.logger.debug("TORecoveryModule - first pass");
        }

        // Build a hashtable of uncommitted transactional objects
        _uncommittedTOTable = new Hashtable();

        try
        {
            InputObjectState types = new InputObjectState();

            // find all the types of transactional object (in this ObjectStore)
            if (_objectStore.allTypes(types))
            {
                String theName = null;

                try
                {
                    boolean endOfList = false;

                    while (!endOfList)
                    {
                        // extract a type
                        theName = types.unpackString();

                        if (theName.compareTo("") == 0)
                            endOfList = true;
                        else
                        {
                            InputObjectState uids = new InputObjectState();

                            // find the uids of anything with an uncommitted
                            // entry in the object store
                            if (_objectStore.allObjUids(theName, uids,
                                    StateStatus.OS_UNCOMMITTED))
                            {
                                Uid theUid = null;

                                try
                                {
                                    boolean endOfUids = false;

                                    while (!endOfUids)
                                    {
                                        // extract a uid
                                        theUid = UidHelper.unpackFrom(uids);

                                        if (theUid.equals(Uid.nullUid()))
                                            endOfUids = true;
                                        else
                                        {
                                            String newTypeString = new String(
                                                    theName);
                                            Uid newUid = new Uid(theUid);
                                            
                                            _uncommittedTOTable.put(newUid,newTypeString);
                                            
                                            if (txojLogger.logger.isDebugEnabled()) {
                                                txojLogger.logger.debug("TO currently uncommitted "+newUid+" is a "+newTypeString);
                                            }
                                        }
                                    }
                                }
                                catch (Exception e)
                                {
                                    // end of uids!
                                }
                            }
                        }
                    }
                }
                catch (IOException ex)
                {
                    // nothing there.
                }
                catch (Exception e)
                {
                    txojLogger.i18NLogger.warn_recovery_TORecoveryModule_5(e);
                }
            }
        }
        catch (Exception e)
        {
            txojLogger.i18NLogger.warn_recovery_TORecoveryModule_5(e);
        }

    }

    public void periodicWorkSecondPass ()
    {
        if(txojLogger.logger.isDebugEnabled()) {
            txojLogger.logger.debug("TORecoveryModule - second pass");
        }

        Enumeration uncommittedObjects = _uncommittedTOTable.keys();

        while (uncommittedObjects.hasMoreElements())
        {
            Uid objUid = (Uid) uncommittedObjects.nextElement();
            String objType = (String) _uncommittedTOTable.get(objUid);

            try
            {
                if (_objectStore.currentState(objUid, objType) == StateStatus.OS_UNCOMMITTED)
                {
                    recoverObject(objUid, objType);
                }
                else
                {
                    if (txojLogger.logger.isDebugEnabled()) {
                        txojLogger.logger.debug("Object ("+objUid+", "+objType+") is no longer uncommitted.");
                    }
                }
            }
            catch (ObjectStoreException ose)
            {
                if (txojLogger.logger.isDebugEnabled()) {
                    txojLogger.logger.debug("Object ("+objUid+", "+objType+") no longer exists.");
                }
            }
        }
    }

    /**
     * Set-up routine.
     */

    protected void initialise ()
    {
        if (txojLogger.logger.isDebugEnabled()) {
            txojLogger.logger.debug("TORecoveryModule.initialise()");
        }
    }

    private final void recoverObject (Uid objUid, String objType)
    {
        if (txojLogger.logger.isDebugEnabled()) {
            txojLogger.logger.debug("TORecoveryModule.recoverObject(" + objUid + ", "
                    + objType + ")");
        }

        /*
         * Get a shell of the TO and find out which transaction it was that got
         * it uncommitted.
         */

        RecoveredTransactionalObject recoveredTO = new RecoveredTransactionalObject(
                objUid, objType,_objectStore);

        /*
         * Tell it to replayPhase2, in whatever way it does (in fact it won't do
         * anything unless it determines the transaction rolled back).
         */

        recoveredTO.replayPhase2();
    }

    private Hashtable _uncommittedTOTable;

    private static ObjectStoreAPI _objectStore = null;

}