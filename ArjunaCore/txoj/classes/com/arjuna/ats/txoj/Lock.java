/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.ats.txoj;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.arjuna.ats.arjuna.ObjectType;
import com.arjuna.ats.arjuna.StateManager;
import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.coordinator.ActionHierarchy;
import com.arjuna.ats.arjuna.coordinator.BasicAction;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputObjectState;
import com.arjuna.ats.arjuna.utils.Utility;
import com.arjuna.ats.txoj.logging.txojLogger;

/**
 * Instances of this class (or derived user classes) are used when trying to set
 * a lock. The default implementation provides a single-write/multiple-reader
 * policy. However, by overridding the appropriate methods, other, type-specific
 * concurrency control locks can be implemented.
 * 
 * @author Mark Little (mark@arjuna.com)
 * @version $Id: Lock.java 2342 2006-03-30 13:06:17Z $
 * @since JTS 1.0.
 */

public class Lock extends StateManager
{

    /**
     * Create a new lock.
     */

    public Lock()
    {
        super(ObjectType.NEITHER);

        currentStatus = LockStatus.LOCKFREE;
        nextLock = null;
        lMode = LockMode.WRITE;
        owners = new ActionHierarchy(0);
    }

    /**
     * Create a new Lock object and initialise it. Mode is based upon argument.
     * The value of BasicAction.Current determines the values of the remainder
     * of the fields. If there is no action running the owner field is set to be
     * the application uid created when the application starts.
     */

    public Lock(int lm)
    {
        super(ObjectType.NEITHER);

        if (txojLogger.logger.isTraceEnabled()) {
            txojLogger.logger.trace("Lock::Lock(" + lm
                    + ")");
        }

        currentStatus = LockStatus.LOCKFREE;
        nextLock = null;
        lMode = lm;
        owners = new ActionHierarchy(0);

        BasicAction curr = BasicAction.Current();

        if (curr == null)
        {
            int currentPid = Utility.getpid(); // ::getpid();
            ActionHierarchy ah = new ActionHierarchy(1); /* max depth of 1 */

            if (applicUid == null)
            {
                applicUid = new Uid();
            }

            if (applicPid != currentPid)
            {
                /*
                 * Process id change probably due to a fork(). Get new pid and
                 * generate a new Applic_Uid
                 */

                applicPid = currentPid;
                applicUid = new Uid();
            }

            ah.add(applicUid);
            owners.copy(ah);
        }
        else
        {
            owners.copy(curr.getHierarchy());
        }
    }

    /**
     * This is used when re-initialising a Lock after retrieval from the object
     * store.
     */

    public Lock(Uid storeUid)
    {
        super(storeUid, ObjectType.NEITHER);

        if (txojLogger.logger.isTraceEnabled()) {
            txojLogger.logger.trace("Lock::Lock("
                    + storeUid + ")");
        }

        currentStatus = LockStatus.LOCKFREE;
        nextLock = null;
        lMode = LockMode.WRITE;
        owners = new ActionHierarchy(0);
    }

    /**
     * General clean up as Lock is deleted.
     */

    public void finalize () throws Throwable
    {
        if (txojLogger.logger.isTraceEnabled()) {
            txojLogger.logger.trace("Lock.finalize()");
        }

        super.terminate();
    }

    /*
     * Public utility operations. Most are sufficiently simple as to be self
     * explanatory!
     */

    /**
     * @return the mode this lock is currently in, e.g.,
     *         <code>LockMode.READ</code>.
     */

    public final int getLockMode ()
    {
        if (txojLogger.logger.isTraceEnabled()) {
            txojLogger.logger.trace("Lock::getLockMode()");
        }

        return lMode;
    }

    /**
     * @return the identity of the lock's current owner (the transaction id).
     */

    public final Uid getCurrentOwner ()
    {
        if (txojLogger.logger.isTraceEnabled()) {
            txojLogger.logger.trace("Lock::getCurrentOwner()");
        }

        return owners.getDeepestActionUid();
    }

    /**
     * @return the transaction hierarchy associated with this lock.
     */

    public final ActionHierarchy getAllOwners ()
    {
        if (txojLogger.logger.isTraceEnabled()) {
            txojLogger.logger.trace("Lock::getAllOwners()");
        }

        return owners;
    }

    /**
     * @return the lock's current status.
     */

    public final int getCurrentStatus ()
    {
        if (txojLogger.logger.isTraceEnabled()) {
            txojLogger.logger.trace("Lock::getCurrentStatus()");
        }

        return currentStatus;
    }

    /**
     * Change the transaction hierarchy associated with the lock to that
     * provided.
     */

    public final void changeHierarchy (ActionHierarchy newOwner)
    {
        if (txojLogger.logger.isTraceEnabled()) {
            txojLogger.logger.trace("Lock::getCurrentOwner()");
        }

        owners.copy(newOwner);

        if (currentStatus == LockStatus.LOCKFREE)
            currentStatus = LockStatus.LOCKHELD;
    }

    /**
     * Propagate the lock.
     */

    public final void propagate ()
    {
        if (txojLogger.logger.isTraceEnabled()) {
            txojLogger.logger.trace("Lock::propagate()");
        }

        owners.forgetDeepest();

        currentStatus = LockStatus.LOCKRETAINED;
    }

    /**
     * Does this lock imply a modification of the object it is applied to? For
     * example, a READ lock would return false, but a WRITE lock would return
     * true.
     * 
     * @return <code>true</code> if this lock implies the object's state will be
     *         modified, <code>false</code> otherwise.
     */

    public boolean modifiesObject ()
    {
        if (txojLogger.logger.isTraceEnabled()) {
            txojLogger.logger.trace("Lock::modifiesObject()");
        }

        return ((lMode == LockMode.WRITE) ? true : false);
    }

    /**
     * Implementation of Lock conflict check. Returns TRUE if there is conflict
     * FALSE otherwise. Does not take account of relationship in the atomic
     * action hierarchy since this is a function of LockManager.
     * 
     * @return <code>true</code> if this lock conflicts with the parameter,
     *         <code>false</code> otherwise.
     */

    public boolean conflictsWith (Lock otherLock)
    {
        if (txojLogger.logger.isTraceEnabled()) {
            txojLogger.logger.trace("Lock::conflictsWith(" + otherLock + ")\n" + "\tLock 1:\n"
                    + this + "\n" + "\tLock 2:\n" + otherLock);
        }

        if (!(getCurrentOwner().equals(otherLock.getCurrentOwner())))
        {
            switch (lMode)
            {
            case LockMode.WRITE:
                return true; /* WRITE conflicts always */
            case LockMode.READ:
                if (otherLock.getLockMode() != LockMode.READ)
                    return true;
                break;
            }
        }

        return false; /* no conflict between these locks */
    }

    /**
     * Overrides Object.equals()
     */

    public boolean equals (Object otherLock)
    {
        if (otherLock instanceof Lock)
            return equals((Lock) otherLock);
        else
            return false;
    }

    /**
     * Are the two locks equal?
     * 
     * @return <code>true</code> if the locks are equal, <code>false</code>
     *         otherwise.
     */

    public boolean equals (Lock otherLock)
    {
        if (txojLogger.logger.isTraceEnabled()) {
            txojLogger.logger.trace("Lock::equals("
                    + otherLock + ")\n" + "\tLock 1:\n" + this + "\n"
                    + "\tLock 2:\n" + otherLock);
        }

        if (this == otherLock)
            return true;

        if ((lMode == otherLock.lMode) && (owners.equals(otherLock.owners))
                && (currentStatus == otherLock.currentStatus))
        {
            return true;
        }

        return false;
    }

    /**
     * Overrides Object.toString()
     */

    public String toString ()
    {
        StringWriter strm = new StringWriter();

        strm.write("Lock object : \n");
        strm.write("\ttype : "+type()+ "\n");
        strm.write("\tunique id is : " + get_uid() + "\n");

        strm.write("\tcurrent_status : "
                + LockStatus.printString(currentStatus));

        strm.write("\n\tMode : " + LockMode.stringForm(lMode));

        strm.write("\n\tOwner List : \n");
        owners.print(new PrintWriter(strm));

        return strm.toString();
    }

    /**
     * functions inherited from StateManager
     */

    public void print (PrintWriter strm)
    {
        strm.print(toString());
    }

    /**
     * Carefully restore the state of a Lock.
     * 
     * @return <code>true</code> if successful, <code>false</code> otherwise.
     */

    public boolean restore_state (InputObjectState os, int ot)
    {
        if (txojLogger.logger.isTraceEnabled()) {
            txojLogger.logger.trace("Lock::restore_state(" + os + ", " + ot + ")");
        }

        ActionHierarchy ah = new ActionHierarchy(0);

        try
        {
            currentStatus = os.unpackInt();
            lMode = os.unpackInt();
            ah.unpack(os);
            owners = ah;

            return true;
        }
        catch (IOException e)
        {
            return false;
        }
    }

    /**
     * Save the state of a lock object.
     * 
     * @return <code>true</code> if successful, <code>false</code> otherwise.
     */

    public boolean save_state (OutputObjectState os, int ot)
    {
        if (txojLogger.logger.isTraceEnabled()) {
            txojLogger.logger.trace("Lock::save_state("
                    + os + ", " + ot + ")");
        }

        try
        {
            os.packInt(currentStatus);
            os.packInt(lMode);
            owners.pack(os);

            return os.valid();
        }
        catch (IOException e)
        {
            return false;
        }
    }

    /**
     * Overrides StateManager.type()
     */

    public String type ()
    {
        return "/StateManager/Lock";
    }

    /**
     * Get the next lock in the chain.
     */

    protected Lock getLink ()
    {
        return nextLock;
    }

    /**
     * Set the next lock in the chain.
     */

    protected void setLink (Lock pointTo)
    {
        nextLock = pointTo;
    }

    private int currentStatus;/* Current status of lock */

    private Lock nextLock;

    private int lMode; /* Typically READ or WRITE */

    private ActionHierarchy owners; /* Uid of owner action (faked if none) */

    private static Uid applicUid = null; /* In case lock set outside AA */

    private static int applicPid = com.arjuna.ats.arjuna.utils.Utility.getpid(); /*
                                                                                  * process
                                                                                  * id
                                                                                  */

}