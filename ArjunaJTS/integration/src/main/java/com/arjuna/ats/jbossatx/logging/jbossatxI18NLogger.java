/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.ats.jbossatx.logging;

import org.jboss.logging.annotations.*;
import static org.jboss.logging.Logger.Level.*;
import static org.jboss.logging.annotations.Message.Format.*;

import jakarta.transaction.Transaction;

/**
 * i18n log messages for the atsintegration module.
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com) 2010-06
 */
@MessageLogger(projectCode = "ARJUNA")
public interface jbossatxI18NLogger {

    /*
        Message IDs are unique and non-recyclable.
        Don't change the purpose of existing messages.
          (tweak the message text or params for clarification if you like).
        Allocate new messages by following instructions at the bottom of the file.
     */

    @Message(id = 32001, value = "createConnection got exception", format = MESSAGE_FORMAT)
	@LogMessage(level = ERROR)
	public void error_jta_AppServerJDBCXARecovery_createconnectionproblem(@Cause() Throwable arg0);

	@Message(id = 32002, value = "createDataSource got exception during getXADataSource call", format = MESSAGE_FORMAT)
	@LogMessage(level = ERROR)
	public void error_jta_AppServerJDBCXARecovery_createproblem(@Cause() Throwable arg0);

	@Message(id = 32003, value = "InstanceNotFound. Datasource {0} not deployed, or wrong name?", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_jta_AppServerJDBCXARecovery_notfound(String arg0);

	@Message(id = 32004, value = "createDataSource {0} got exception", format = MESSAGE_FORMAT)
	@LogMessage(level = ERROR)
	public void error_jta_AppServerJDBCXARecovery_problem(String arg0, @Cause() Throwable arg1);

	@Message(id = 32005, value = "Unexpected exception occurred", format = MESSAGE_FORMAT)
	@LogMessage(level = ERROR)
	public void error_jta_PropagationContextManager_exception(@Cause() Throwable arg0);

	@Message(id = 32006, value = "unknown Tx PropagationContext", format = MESSAGE_FORMAT)
	@LogMessage(level = ERROR)
	public void error_jta_PropagationContextManager_unknownctx();

	@Message(id = 32007, value = "getCurrentTransaction() failed", format = MESSAGE_FORMAT)
	@LogMessage(level = ERROR)
	public void error_jts_InboundTransactionCurrentImple_exception(@Cause() Throwable arg0);

	@Message(id = 32008, value = "unknown Tx PropagationContext", format = MESSAGE_FORMAT)
	@LogMessage(level = ERROR)
	public void error_jts_PropagationContextManager_unknownctx();

	@Message(id = 32009, value = "Unexpected exception occurred", format = MESSAGE_FORMAT)
	@LogMessage(level = ERROR)
	public void error_jts_PropagationContextManager_exception(@Cause() Throwable arg0);

	@Message(id = 32010, value = "JBossTS Recovery Service (tag: {0}) - JBoss Inc.", format = MESSAGE_FORMAT)
	@LogMessage(level = INFO)
	public void info_jta_RecoveryManagerService_create(String arg0);

	@Message(id = 32011, value = "No suitable recovery module in which to register XAResourceRecovery instance", format = MESSAGE_FORMAT)
	public String get_jta_RecoveryManagerService_norecoverymodule();

	@Message(id = 32012, value = "No recovery system in which to register XAResourceRecovery instance", format = MESSAGE_FORMAT)
	public String get_jta_RecoveryManagerService_norecoverysystem();

	@Message(id = 32013, value = "Starting transaction recovery manager", format = MESSAGE_FORMAT)
	@LogMessage(level = INFO)
	public void info_jta_RecoveryManagerService_start();

	@Message(id = 32014, value = "Stopping transaction recovery manager", format = MESSAGE_FORMAT)
	@LogMessage(level = INFO)
	public void info_jta_RecoveryManagerService_stop();

	@Message(id = 32015, value = "Transaction has or will rollback.", format = MESSAGE_FORMAT)
	public String get_jta_TransactionManagerDelegate_getTimeLeftBeforeTransactionTimeout_1();

	@Message(id = 32016, value = "Unexpected error retrieving transaction status", format = MESSAGE_FORMAT)
	public String get_jta_TransactionManagerDelegate_getTimeLeftBeforeTransactionTimeout_2();

	@Message(id = 32017, value = "JBossTS Transaction Service ({0} version - tag: {1}) - JBoss Inc.", format = MESSAGE_FORMAT)
	@LogMessage(level = INFO)
	public void info_jta_TransactionManagerService_create(String arg0, String arg1);

	@Message(id = 32018, value = "Destroying TransactionManagerService", format = MESSAGE_FORMAT)
	@LogMessage(level = INFO)
	public void info_jta_TransactionManagerService_destroy();

	@Message(id = 32019, value = "XAExceptionFormatters are not supported by the JBossTS Transaction Service - this warning can safely be ignored", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_jta_TransactionManagerService_noformatter();

	@Message(id = 32020, value = "Transaction is completing!", format = MESSAGE_FORMAT)
	public String get_jta_jca_completing();

	@Message(id = 32021, value = "Transaction is inactive!", format = MESSAGE_FORMAT)
	public String get_jta_jca_inactive();

	@Message(id = 32022, value = "Unexpected error!", format = MESSAGE_FORMAT)
	public String get_jta_jca_unknown();

	@Message(id = 32023, value = "Work not registered!", format = MESSAGE_FORMAT)
	public String get_jta_jca_unknownwork();

	@Message(id = 32024, value = "<init> JTS transaction recovery manager", format = MESSAGE_FORMAT)
	@LogMessage(level = INFO)
	public void info_jts_RecoveryManagerService_init();

	@Message(id = 32025, value = "Transaction has or will rollback.", format = MESSAGE_FORMAT)
	public String get_jts_TransactionManagerDelegate_getTimeLeftBeforeTransactionTimeout_1();

	@Message(id = 32026, value = "Unexpected error retrieving transaction status", format = MESSAGE_FORMAT)
	public String get_jts_TransactionManagerDelegate_getTimeLeftBeforeTransactionTimeout_2();

	@Message(id = 32027, value = "Problem encountered while trying to register transaction manager with ORB!", format = MESSAGE_FORMAT)
	public String get_jts_TransactionManagerService_failed();

	@Message(id = 32028, value = "registering transaction manager", format = MESSAGE_FORMAT)
	@LogMessage(level = INFO)
	public void info_jts_TransactionManagerService_start();

	@Message(id = 32029, value = "Transaction is completing!", format = MESSAGE_FORMAT)
	public String get_jts_jca_completing();

	@Message(id = 32030, value = "Transaction is inactive!", format = MESSAGE_FORMAT)
	public String get_jts_jca_inactive();

	@Message(id = 32031, value = "Unexpected error!", format = MESSAGE_FORMAT)
	public String get_jts_jca_unknown();

	@Message(id = 32032, value = "Work not registered!", format = MESSAGE_FORMAT)
	public String get_jts_jca_unknownwork();


    @Message(id = 32033, value = "AppServerJDBCXARecovery should no longer be used. See jira.jboss.org/browse/JBTM-756", format = MESSAGE_FORMAT)
    @LogMessage(level = WARN)
    public void warn_AppServerJDBCXARecovery_deprecation();

	@Message(id = 32034, value = "Suspending transaction recovery manager", format = MESSAGE_FORMAT)
	@LogMessage(level = INFO)
	void info_jta_RecoveryManagerService_suspend();

	@Message(id = 32035, value = "Resuming transaction recovery manager", format = MESSAGE_FORMAT)
	@LogMessage(level = INFO)
	void info_jta_RecoveryManagerService_resume();

	@Message(id = 32036, value = "Unsupported transaction type. Transaction type is {0}", format = MESSAGE_FORMAT)
	public String get_unsupported_transaction_type(Class<? extends Transaction> transactionClass);

	@Message(id = 32037, value = "Transaction listeners are disabled and should not be used. If you need them they can be enabled via -D{0}=true", format = MESSAGE_FORMAT)
	public String get_transaction_listeners_disabled(String transactionListenersPropertyName);

	@Message(id = 32038, value = "Invalid transaction local resource [{0}] associated with key {1}.", format = MESSAGE_FORMAT)
	public String get_invalid_transaction_local_resource(Object localResource, String key);

	@Message(id = 32039, value = "Cannot lock a TransactionLocal after the Transaction [{0}] has ended", format = MESSAGE_FORMAT)
	public String get_cannot_lock_transactionlocal(Transaction txn);

	@Message(id = 32040, value = "Cannot store value in a TransactionLocal after the Transaction [{0}] has ended", format = MESSAGE_FORMAT)
	public String get_cannot_store_transactionlocal(Transaction txn);
	
	@Message(id = 32041, value = "Unlock called from wrong thread.  Locking thread: {0}, current thread: {1}", format = MESSAGE_FORMAT)
	public String get_cannot_store_transactionlocal(Thread lockingThread, Thread currentThread);
	
    /*
        Allocate new messages directly above this notice.
          - id: use the next id number in numeric sequence. Don't reuse ids.
          The first two digits of the id(XXyyy) denote the module
            all message in this file should have the same prefix.
          - value: default (English) version of the log message.
          - level: according to severity semantics defined at http://docspace.corp.redhat.com/docs/DOC-30217
          Debug and trace don't get i18n. Everything else MUST be i18n.
          By convention methods with String return type have prefix get_,
            all others are log methods and have prefix <level>_
     */
}