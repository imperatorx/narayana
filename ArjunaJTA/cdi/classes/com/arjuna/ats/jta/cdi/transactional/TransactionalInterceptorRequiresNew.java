/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */


package com.arjuna.ats.jta.cdi.transactional;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.transaction.Transaction;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.Transactional;

/**
 * @author paul.robinson@redhat.com 25/05/2013
 */

@Interceptor
@Transactional(Transactional.TxType.REQUIRES_NEW)
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 200)
public class TransactionalInterceptorRequiresNew extends TransactionalInterceptorBase {
    public TransactionalInterceptorRequiresNew() {
        super(false);
    }

    @AroundInvoke
    public Object intercept(InvocationContext ic) throws Exception {
        return super.intercept(ic);
    }

    @Override
    protected Object doIntercept(TransactionManager tm, Transaction tx, InvocationContext ic) throws Exception {
        if (tx != null) {
            tm.suspend();
            return invokeInOurTx(ic, tm, () -> tm.resume(tx));
        } else {
            return invokeInOurTx(ic, tm);
        }
    }
}