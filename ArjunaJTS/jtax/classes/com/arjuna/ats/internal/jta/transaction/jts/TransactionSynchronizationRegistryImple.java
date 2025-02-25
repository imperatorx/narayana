/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.ats.internal.jta.transaction.jts;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;
import jakarta.transaction.RollbackException;
import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.SystemException;
import jakarta.transaction.Transaction;
import jakarta.transaction.TransactionSynchronizationRegistry;

import com.arjuna.ats.internal.jta.resources.jts.orbspecific.JTAInterposedSynchronizationImple;
import com.arjuna.ats.internal.jta.utils.jtaxLogger;

/**
 * Implementation of the TransactionSynchronizationRegistry interface, in line with the JTA 1.1 specification.
 *
 * @author jonathan.halliday@jboss.com
 */
public class TransactionSynchronizationRegistryImple implements TransactionSynchronizationRegistry, Serializable, ObjectFactory
{

    // This Imple is stateless and just delegates the work down to the transaction manager.
    // It's Serilizable so it can be shoved into the app server JNDI.

    /*
         * http://java.sun.com/javaee/5/docs/api/javax/transaction/TransactionSynchronizationRegistry.html
         * http://jcp.org/aboutJava/communityprocess/maintenance/jsr907/907ChangeLog.html
         */

    // Return an opaque object to represent the transaction bound to the current thread at the time this method is called.

    private static final long serialVersionUID = 1L;

    // cached for performance. Note: must set tm config before instantiating a TSRImple instance.
    private transient jakarta.transaction.TransactionManager tm = com.arjuna.ats.jta.TransactionManager.transactionManager();

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
    }

    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable environment) throws Exception
    {
        return this;
    }

    public Object getTransactionKey()
    {
        if (jtaxLogger.logger.isTraceEnabled()) {
            jtaxLogger.logger.trace("TransactionSynchronizationRegistryImple.getTransactionKey");
        }

        TransactionImple transactionImple = null;
        try
        {
            transactionImple = (TransactionImple)tm.getTransaction();
        }
        catch (SystemException e)
        {
            throw new RuntimeException(jtaxLogger.i18NLogger.get_jtax_transaction_jts_systemexception(), e);
        }

        if (transactionImple == null) {
            return null;
        } else {
            return transactionImple.get_uid();
        }
    }

    // Add or replace an object in the Map of resources being managed for the transaction bound to the current thread at the time this method is called.
    public void putResource(Object key, Object value)
    {
        if (jtaxLogger.logger.isTraceEnabled()) {
            jtaxLogger.logger.trace("TransactionSynchronizationRegistryImple.putResource");
        }

        if(key ==  null)
        {
            throw new NullPointerException();
        }

        TransactionImple transactionImple = getTransactionImple();
        transactionImple.putTxLocalResource(key, value);
    }

    // Get an object from the Map of resources being managed for the transaction bound to the current thread at the time this method is called.
    public Object getResource(Object key)
    {
        if (jtaxLogger.logger.isTraceEnabled()) {
            jtaxLogger.logger.trace("TransactionSynchronizationRegistryImple.getResource");
        }

        if(key ==  null)
        {
            throw new NullPointerException();
        }

        TransactionImple transactionImple = getTransactionImple();
        return transactionImple.getTxLocalResource(key);
    }

    // Register a Synchronization instance with special ordering semantics.
    public void registerInterposedSynchronization(Synchronization synchronization)
    {
        if (jtaxLogger.logger.isTraceEnabled()) {
            jtaxLogger.logger.trace("TransactionSynchronizationRegistryImple.registerInterposedSynchronization - Class: " + synchronization.getClass() + " HashCode: " + synchronization.hashCode() + " toString: " + synchronization);
        }

        TransactionImple transactionImple = getTransactionImple();

        try
        {
            transactionImple.registerSynchronizationImple(new JTAInterposedSynchronizationImple(synchronization));
        }
        catch (RollbackException e)
        {
            throw new com.arjuna.ats.jta.exceptions.RollbackException(jtaxLogger.i18NLogger.get_jtax_transaction_jts_syncrollbackexception(), e);
        }
        catch (SystemException e)
        {
            throw new RuntimeException(jtaxLogger.i18NLogger.get_jtax_transaction_jts_systemexception(), e);
        }
    }

    // Return the status of the transaction bound to the current thread at the time this method is called.
    public int getTransactionStatus()
    {
        try
        {
            return tm.getStatus();
        }
        catch(SystemException e)
        {
            throw new RuntimeException(jtaxLogger.i18NLogger.get_jtax_transaction_jts_systemexception(), e);
        }

    }

    // Set the rollbackOnly status of the transaction bound to the current thread at the time this method is called.
    public void setRollbackOnly()
    {
        if (jtaxLogger.logger.isTraceEnabled()) {
            jtaxLogger.logger.trace("TransactionSynchronizationRegistryImple.setRollbackOnly");
        }

        try
        {
            Transaction transaction = tm.getTransaction();

            if(transaction == null)
            {
                throw new IllegalStateException();
            }

            tm.setRollbackOnly();
        }
        catch (SystemException e)
        {
            throw new RuntimeException(jtaxLogger.i18NLogger.get_jtax_transaction_jts_systemexception(), e);
        }
    }

    // Get the rollbackOnly status of the transaction bound to the current thread at the time this method is called.
    public boolean getRollbackOnly()
    {
        if (jtaxLogger.logger.isTraceEnabled()) {
            jtaxLogger.logger.trace("TransactionSynchronizationRegistryImple.getRollbackOnly");
        }

        TransactionImple transactionImple = getTransactionImple();

        if(transactionImple == null) {
            throw new IllegalStateException();
        }

        try
        {
            return (transactionImple.getStatus() == Status.STATUS_MARKED_ROLLBACK);
        }
        catch (SystemException e)
        {
            throw new RuntimeException(jtaxLogger.i18NLogger.get_jtax_transaction_jts_systemexception(), e);
        }
    }

    private TransactionImple getTransactionImple() throws IllegalStateException
    {
        TransactionImple transactionImple = null;
        try
        {
            transactionImple = (TransactionImple)tm.getTransaction();
        }
        catch (SystemException e)
        {
            throw new RuntimeException(jtaxLogger.i18NLogger.get_jtax_transaction_jts_systemexception(), e);
        }

        try {
            if (transactionImple == null
                    || (transactionImple.getStatus() != Status.STATUS_ACTIVE && transactionImple.getStatus() != Status.STATUS_MARKED_ROLLBACK)) {
                throw new IllegalStateException("No transaction is running");
            }
        } catch (SystemException e) {
            throw new IllegalStateException("Could not get the status of a transaction");
        }

        return transactionImple;
    }
}