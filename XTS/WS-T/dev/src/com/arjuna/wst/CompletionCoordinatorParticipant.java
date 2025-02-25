/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.wst;

/**
 */

public interface CompletionCoordinatorParticipant
{

    public void commit () throws TransactionRolledBackException, UnknownTransactionException, SystemException;
    
    public void rollback () throws UnknownTransactionException, SystemException;
    
}