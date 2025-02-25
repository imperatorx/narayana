/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.ats.jbossatx.jts;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;
import jakarta.transaction.RollbackException;
import jakarta.transaction.Status;
import jakarta.transaction.SystemException;

import com.arjuna.ats.internal.jta.transaction.jts.TransactionManagerImple;
import com.arjuna.ats.jbossatx.BaseTransactionManagerDelegate;
import com.arjuna.ats.jbossatx.logging.jbossatxLogger;

public class TransactionManagerDelegate extends BaseTransactionManagerDelegate implements ObjectFactory
{
    /**
     * The transaction manager.
     */
    private static final TransactionManagerImple TRANSACTION_MANAGER = new TransactionManagerImple() ;

    /**
     * Construct the delegate with the appropriate transaction manager
     */
    public TransactionManagerDelegate()
    {
        super(getTransactionManager());
    }

    /**
     * Get the transaction timeout.
     *
     * @return the timeout in seconds associated with this thread
     * @throws SystemException for any error
     */
    public int getTransactionTimeout()
        throws SystemException
    {
        return getTransactionManager().getTimeout() ;
    }

    /**
     * Get the time left before transaction timeout
     *
     * @param errorRollback throw an error if the transaction is marked for rollback
     * @return the remaining in the current transaction or -1
     * if there is no transaction
     * @throws RollbackException if the transaction is marked for rollback and
     * errorRollback is true
     */
    public long getTimeLeftBeforeTransactionTimeout(boolean errorRollback)
        throws RollbackException
    {
        // see JBAS-5081, JBTM-371 and http://www.jboss.com/index.html?module=bb&op=viewtopic&t=132128

        try
    	{
            switch(getStatus())
            {
                case Status.STATUS_MARKED_ROLLBACK:
                case Status.STATUS_ROLLEDBACK:
                case Status.STATUS_ROLLING_BACK:
                    if(errorRollback) {
                        throw new RollbackException(jbossatxLogger.i18NLogger.get_jts_TransactionManagerDelegate_getTimeLeftBeforeTransactionTimeout_1());
                    }
                    break;
                case Status.STATUS_COMMITTED:
                case Status.STATUS_COMMITTING:
                case Status.STATUS_UNKNOWN:
                    throw new IllegalStateException();  // would be better to use a checked exception,
                    // but RollbackException does not make sense and the API does not allow any other.
                    // also need to clarify if we should throw an exception at all if !errorRollback?
                case Status.STATUS_ACTIVE:
                case Status.STATUS_PREPARED:
                case Status.STATUS_PREPARING:
                    com.arjuna.ats.jta.transaction.Transaction tx = (com.arjuna.ats.jta.transaction.Transaction)getTransaction();
                    if(tx != null) {
                            return tx.getRemainingTimeoutMills();
                    } else {
                        return 0;
                    }
                case Status.STATUS_NO_TRANSACTION:
                default:
                    break;
            }
    	}
    	catch (final SystemException se)
    	{
    		RollbackException rollbackException = new RollbackException(
                    jbossatxLogger.i18NLogger.get_jts_TransactionManagerDelegate_getTimeLeftBeforeTransactionTimeout_2()) ;
            rollbackException.initCause(se);
            throw rollbackException;
    	}
        return -1 ;
    }

    /**
     * Get the transaction manager from the factory.
     * @param initObj The initialisation object.
     * @param relativeName The instance name relative to the context.
     * @param namingContext The naming context for the instance.
     * @param env The environment.
     */
    public Object getObjectInstance(final Object initObj,
           final Name relativeName, final Context namingContext,
           final Hashtable env)
        throws Exception
    {
        return this ;
    }

    /**
     * Get the transaction manager.
     * @return The transaction manager.
     */
    private static TransactionManagerImple getTransactionManager()
    {
        return TRANSACTION_MANAGER ;
    }
}