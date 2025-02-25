/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.hp.mwtests.ts.jta.jts.tools;

import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import javax.transaction.xa.XAException;
import jakarta.transaction.Synchronization;
import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

/**
 * Simulate a variety of faults during the various phases of the XA protocol
 *
 * @author Mike Musgrove
 */
/**
 * @deprecated as of 5.0.5.Final In a subsequent release we will change packages names in order to 
 * provide a better separation between public and internal classes.
 */
@Deprecated // in order to provide a better separation between public and internal classes.
public class XAFailureResource implements Synchronization, XAResource, Serializable
{
    private static final Map<String, XAException> xaCodeMap = new HashMap<String, XAException>();

    private XAFailureType _xaFailureType = XAFailureType.NONE;
    private XAFailureMode _xaFailureMode = XAFailureMode.NONE;
    private String[] _args;
    private int _suspend;
    private int _recoveryAttempts = 1;
    private XAException _xaException;
    private int txTimeout = 10;
    private Set<Xid> _xids = new HashSet<Xid>();
    private transient boolean _isPrepared = false; // transient so it doesn't get persisted in the tx store

    static
    {
        init();
    }
    
    public XAFailureResource()
    {
    }

    public XAFailureResource(XAFailureSpec spec)
    {
        this();

        if (spec == null)
            throw new IllegalArgumentException("Invalid XA resource failure injection specification");
        
        setFailureMode(spec.getMode(), spec.getModeArg());
        setFailureType(spec.getType());
        setRecoveryAttempts(spec.getRecoveryArg());
    }

    public void applySpec(String message) throws XAException
    {
        applySpec(message, _isPrepared);
    }

    public void applySpec(String message, boolean prepared) throws XAException
    {
        if (_xaFailureType.equals(XAFailureType.NONE) || _xaFailureMode.equals(XAFailureMode.NONE) || !prepared)
        {
            System.out.println(message + (_isPrepared ? " ... " : " recovery"));
            return; // NB if !_isPrepared then we must have been called from the recovery subsystem
        }

        System.out.println("Applying fault injection with " + _xids.size() + " active branches");
        if (_xaException != null)
        {
            System.out.println(message + " ... xa error: " + _xaException.getMessage());
            throw _xaException;
        }
        else if (_xaFailureMode.equals(XAFailureMode.HALT))
        {
            System.out.println(message + " ... halting");
            Runtime.getRuntime().halt(1);
        }
        else if (_xaFailureMode.equals(XAFailureMode.EXIT))
        {
            System.out.println(message + " ... exiting");
            System.exit(1);
        }
        else if (_xaFailureMode.equals(XAFailureMode.SUSPEND))
        {
            System.out.println(message + " ... suspending for " + _suspend);
            suspend(_suspend);
            System.out.println(message + " ... resuming");
        }
    }

    public String toString()
    {
        return _xaFailureType + ", " + _xaFailureMode + ", " + (_args != null && _args.length != 0 ? _args[0] : "");
    }

    private void suspend(int msecs)
    {
        try
        {
            Thread.sleep(msecs);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public void setFailureMode(XAFailureMode mode, String ... args) throws IllegalArgumentException
    {
        _xaFailureMode = mode;
        _args = args;

        if (args != null && args.length != 0)
        {
            if (_xaFailureMode.equals(XAFailureMode.SUSPEND))
            {
                _suspend = Integer.parseInt(args[0]);
            }
            else if (_xaFailureMode.equals(XAFailureMode.XAEXCEPTION))
            {
                _xaException = xaCodeMap.get(args[0]);

                if (_xaException == null)
                    _xaException = new XAException(XAException.XAER_RMFAIL);
            }
        }
    }

    public void setFailureType(XAFailureType type)
    {
        _xaFailureType = type;
    }

    public XAFailureType getFailureType()
    {
        return _xaFailureType;
    }

    public void setRecoveryAttempts(int _recoveryAttempts)
    {
        this._recoveryAttempts = _recoveryAttempts;
    }

    // Synchronizatons

    public void beforeCompletion()
    {
        if (_xaFailureType.equals(XAFailureType.SYNCH_BEFORE))
            try
            {
                applySpec("Before completion");
            }
            catch (XAException e)
            {
                throw new RuntimeException(e);
            }
    }

    public void afterCompletion(int i)
    {
        if (_xaFailureType.equals(XAFailureType.SYNCH_AFTER))
            try
            {
                applySpec("After completion");
            }
            catch (XAException e)
            {
                throw new RuntimeException(e);
            }
    }

    // XA Interface implementation

    public void commit(Xid xid, boolean b) throws XAException
    {
        if (_xaFailureType.equals(XAFailureType.XARES_COMMIT))
            applySpec("xa commit");

        _isPrepared = false;
        _xids.remove(xid);
    }

    public void rollback(Xid xid) throws XAException
    {
       if (_xaFailureType.equals(XAFailureType.XARES_ROLLBACK))
            applySpec("xa rollback");

        _isPrepared = false;
        _xids.remove(xid);
    }
    
    public void end(Xid xid, int i) throws XAException
    {
        if (_xaFailureType.equals(XAFailureType.XARES_END))
            applySpec("xa end");
    }

    public void forget(Xid xid) throws XAException
    {
        if (_xaFailureType.equals(XAFailureType.XARES_FORGET))
            applySpec("xa forget");

        _isPrepared = false;
        _xids.remove(xid);
    }

    public int getTransactionTimeout() throws XAException
    {
        return txTimeout;
    }

    public boolean isSameRM(XAResource xaResource) throws XAException
    {
        return false;
    }

    public int prepare(Xid xid) throws XAException
    {
        _isPrepared = true;
        
        if (_xaFailureType.equals(XAFailureType.XARES_PREPARE))
            applySpec("xa prepare");

        _xids.add(xid);

        return XA_OK;
    }

    public Xid[] recover(int i) throws XAException
    {
        if (_recoveryAttempts <= 0)
            return _xids.toArray(new Xid[_xids.size()]);

        _recoveryAttempts -= 1;

        if (_xaFailureType.equals(XAFailureType.XARES_RECOVER))
            applySpec("xa recover");

        return new Xid[0];
    }

    public boolean setTransactionTimeout(int txTimeout) throws XAException
    {
        this.txTimeout = txTimeout;
        
        return true;    // set was successfull
    }

    public void start(Xid xid, int i) throws XAException
    {
        _xids.add(xid);

       if (_xaFailureType.equals(XAFailureType.XARES_START))
            applySpec("xa start");
    }

    public String getEISProductName() { return "Test XAResouce";}
    
    public String getEISProductVersion() { return "v666.0";}

    public static XAException getXAExceptionType(String type) {
        return xaCodeMap.get(type);
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    private static void init()
    {
        xaCodeMap.put("XA_HEURCOM", new XAException(XAException.XA_HEURCOM));
        xaCodeMap.put("XA_HEURHAZ", new XAException(XAException.XA_HEURHAZ));
        xaCodeMap.put("XA_HEURMIX", new XAException(XAException.XA_HEURMIX));
        xaCodeMap.put("XA_HEURRB", new XAException(XAException.XA_HEURRB));
        xaCodeMap.put("XA_NOMIGRATE", new XAException(XAException.XA_NOMIGRATE));
        xaCodeMap.put("XA_RBBASE", new XAException(XAException.XA_RBBASE));
        xaCodeMap.put("XA_RBCOMMFAIL", new XAException(XAException.XA_RBCOMMFAIL));
        xaCodeMap.put("XA_RBDEADLOCK", new XAException(XAException.XA_RBDEADLOCK));
        xaCodeMap.put("XA_RBEND", new XAException(XAException.XA_RBEND));
        xaCodeMap.put("XA_RBINTEGRITY", new XAException(XAException.XA_RBINTEGRITY));
        xaCodeMap.put("XA_RBOTHER", new XAException(XAException.XA_RBOTHER));
        xaCodeMap.put("XA_RBPROTO", new XAException(XAException.XA_RBPROTO));
        xaCodeMap.put("XA_RBROLLBACK", new XAException(XAException.XA_RBROLLBACK));
        xaCodeMap.put("XA_RBTIMEOUT", new XAException(XAException.XA_RBTIMEOUT));
        xaCodeMap.put("XA_RBTRANSIENT", new XAException(XAException.XA_RBTRANSIENT));
        xaCodeMap.put("XA_RDONLY", new XAException(XAException.XA_RDONLY));
        xaCodeMap.put("XA_RETRY", new XAException(XAException.XA_RETRY));
        xaCodeMap.put("XAER_ASYNC", new XAException(XAException.XAER_ASYNC));
        xaCodeMap.put("XAER_DUPID", new XAException(XAException.XAER_DUPID));
        xaCodeMap.put("XAER_INVAL", new XAException(XAException.XAER_INVAL));
        xaCodeMap.put("XAER_NOTA", new XAException(XAException.XAER_NOTA));
        xaCodeMap.put("XAER_OUTSIDE", new XAException(XAException.XAER_OUTSIDE));
        xaCodeMap.put("XAER_PROTO", new XAException(XAException.XAER_PROTO));
        xaCodeMap.put("XAER_RMERR", new XAException(XAException.XAER_RMERR));
        xaCodeMap.put("XAER_RMFAIL ", new XAException(XAException.XAER_RMFAIL));
    }

    public boolean isXAResource()
    {
        return _xaFailureType.isXA() || _xaFailureType.equals(XAFailureType.NONE);
    }

    public boolean isSynchronization()
    {
        return _xaFailureType.isSynchronization();
    }

    public boolean isPreCommit()
    {
        return _xaFailureType.isPreCommit();
    }

    public boolean expectException()
    {
        return _xaFailureMode.equals(XAFailureMode.XAEXCEPTION);
    }
}