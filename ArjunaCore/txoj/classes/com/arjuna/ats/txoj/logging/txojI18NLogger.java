/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.ats.txoj.logging;

import static org.jboss.logging.Logger.Level.WARN;
import static org.jboss.logging.annotations.Message.Format.MESSAGE_FORMAT;

import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.txoj.LockManager;

/**
 * i18n log messages for the txoj module.
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com) 2010-06
 */
@MessageLogger(projectCode = "ARJUNA")
public interface txojI18NLogger {

    /*
        Message IDs are unique and non-recyclable.
        Don't change the purpose of existing messages.
          (tweak the message text or params for clarification if you like).
        Allocate new messages by following instructions at the bottom of the file.
     */

    @Message(id = 15001, value = "LockManagerFriend.getLink", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_lmf1(@Cause() Throwable arg0);

	@Message(id = 15002, value = "LockManagerFriend.setLink", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_lmf2(@Cause() Throwable arg0);

//	@Message(id = 15003, value = "RecoveredTransactionalObject created for {0}", format = MESSAGE_FORMAT)
//	@LogMessage(level = WARN)
//	public void warn_recovery_RecoveredTransactionalObject_1(String arg0);

	@Message(id = 15004, value = "Object store exception on committing {0}", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_recovery_RecoveredTransactionalObject_10(Uid arg0, @Cause() Throwable arg1);

//	@Message(id = 15005, value = "TO held by transaction {0}", format = MESSAGE_FORMAT)
//	@LogMessage(level = WARN)
//	public void warn_recovery_RecoveredTransactionalObject_2(String arg0);

//	@Message(id = 15006, value = "transaction status {0}", format = MESSAGE_FORMAT)
//	@LogMessage(level = WARN)
//	public void warn_recovery_RecoveredTransactionalObject_3(String arg0);

//	@Message(id = 15007, value = "transaction Status from original application {0} and inactive: {1}", format = MESSAGE_FORMAT)
//	@LogMessage(level = WARN)
//	public void warn_recovery_RecoveredTransactionalObject_4(String arg0, String arg1);

//	@Message(id = 15008, value = "RecoveredTransactionalObject.replayPhase2 - cannot find/no holding transaction", format = MESSAGE_FORMAT)
//	@LogMessage(level = WARN)
//	public void warn_recovery_RecoveredTransactionalObject_5();

	@Message(id = 15009, value = "RecoveredTransactionalObject tried to access object store", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_recovery_RecoveredTransactionalObject_6(@Cause() Throwable arg0);

//	@Message(id = 15010, value = "RecoveredTransactionalObject::findHoldingTransaction - uid is {0}", format = MESSAGE_FORMAT)
//	@LogMessage(level = WARN)
//	public void warn_recovery_RecoveredTransactionalObject_7(String arg0);

	@Message(id = 15011, value = "RecoveredTransactionalObject::findHoldingTransaction - exception", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_recovery_RecoveredTransactionalObject_8(@Cause() Throwable arg0);

	@Message(id = 15012, value = "Object store exception on removing uncommitted state: {0}", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_recovery_RecoveredTransactionalObject_9(Uid arg0, @Cause() Throwable arg1);

//	@Message(id = 15013, value = "TORecoveryModule created", format = MESSAGE_FORMAT)
//	@LogMessage(level = WARN)
//	public void warn_recovery_TORecoveryModule_1();

//	@Message(id = 15014, value = "TORecoveryModule created with {0}", format = MESSAGE_FORMAT)
//	@LogMessage(level = WARN)
//	public void warn_recovery_TORecoveryModule_2(String arg0);

//	@Message(id = 15015, value = "TORecoveryModule - first pass", format = MESSAGE_FORMAT)
//	@LogMessage(level = INFO)
//	public void info_recovery_TORecoveryModule_3();

//	@Message(id = 15016, value = "TO currently uncommitted {0} is a {1}", format = MESSAGE_FORMAT)
//	@LogMessage(level = WARN)
//	public void warn_recovery_TORecoveryModule_4(String arg0, String arg1);

	@Message(id = 15017, value = "TORecoveryModule: searching for TOs:", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_recovery_TORecoveryModule_5(@Cause() Throwable arg0);

//	@Message(id = 15018, value = "TORecoveryModule - second pass", format = MESSAGE_FORMAT)
//	@LogMessage(level = INFO)
//	public void info_recovery_TORecoveryModule_6();

//	@Message(id = 15019, value = "TORecoveryModule.periodicWork(): Object ({0}, {1}) is no longer uncommitted.", format = MESSAGE_FORMAT)
//	@LogMessage(level = WARN)
//	public void warn_recovery_TORecoveryModule_7(String arg0, String arg1);

//	@Message(id = 15020, value = "TORecoveryModule.periodicWork(): Object ({0}, {1}) no longer exists.", format = MESSAGE_FORMAT)
//	@LogMessage(level = WARN)
//	public void warn_recovery_TORecoveryModule_8(String arg0, String arg1);

//	@Message(id = 15021, value = "TORecoveryModule - could not create ObjectStore instance!", format = MESSAGE_FORMAT)
//	public String get_recovery_TORecoveryModule_osproblem();

	@Message(id = 15022, value = "CadaverLockRecord::nestedAbort - no Current!", format = MESSAGE_FORMAT)
	public String get_CadaverLockRecord_1();

	@Message(id = 15023, value = "CadaverLockRecord::nestedCommit - no Current!", format = MESSAGE_FORMAT)
	public String get_CadaverLockRecord_2();

	@Message(id = 15024, value = "CadaverLockRecord::topLevelAbort - no Current!", format = MESSAGE_FORMAT)
	public String get_CadaverLockRecord_3();

	@Message(id = 15025, value = "CadaverLockRecord::topLevelCommit - no Current!", format = MESSAGE_FORMAT)
	public String get_CadaverLockRecord_4();

	@Message(id = 15026, value = "LockManager: lock propagation failed", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_LockManager_1();

	@Message(id = 15027, value = "LockManager::unloadState() failed to remove empty lock state for object {0} of type {1}", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_LockManager_10(Uid arg0, String arg1);

	@Message(id = 15028, value = "LockManager.unloadState - could not save lock state: {0}", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_LockManager_11(String arg0);

	@Message(id = 15029, value = "LockManager::unloadState() failed to write new state for object {0} of type {1}", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_LockManager_12(Uid arg0, String arg1);

	@Message(id = 15030, value = "LockManager::unloadState() failed to pack up new state for object {0} of type {1}", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_LockManager_13(Uid arg0, String arg1);

	@Message(id = 15031, value = "LockManager::setlock() no lock!", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_LockManager_2();

	@Message(id = 15032, value = "LockManager::setlock() cannot find action hierarchy", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_LockManager_3();

	@Message(id = 15033, value = "LockManager::setlock() cannot load existing lock states", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_LockManager_4();

	@Message(id = 15034, value = "LockManager::setlock() cannot activate object", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_LockManager_5();

	@Message(id = 15035, value = "LockManager::setlock() cannot save new lock states", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_LockManager_6();

	@Message(id = 15036, value = "Lockmanager::releaselock() could not load old lock states", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_LockManager_7();

	@Message(id = 15037, value = "Lockmanager::releaselock() could not unload new lock states", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_LockManager_8();

	@Message(id = 15038, value = "LockRecord::set_value() called illegally", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_LockRecord_1();

	@Message(id = 15039, value = "LockRecord - release failed for action {0}", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_LockRecord_2(Uid arg0);

	@Message(id = 15040, value = "LockRecord::nestedAbort - no current action", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_LockRecord_3();

	@Message(id = 15041, value = "LockRecord::nestedCommit - no current action", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_LockRecord_4();

	@Message(id = 15042, value = "LockRecord - release failed for action {0}", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_LockRecord_5(Uid arg0);

	@Message(id = 15043, value = "LockRecord::topLevelCommit - no current action", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_LockRecord_6();

	@Message(id = 15044, value = "Invocation of LockRecord::restore_state for {0} inappropriate - ignored for {1}", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_LockRecord_7(String arg0, Uid arg1);

	@Message(id = 15050, value = "OptimisticLockRecord.topLevelPrepare state check failed for {0} will force rollback.", format = MESSAGE_FORMAT)
        @LogMessage(level = WARN)
        public void warn_OptimisticLockRecord_1(LockManager man);
	
	@Message(id = 15051, value = "OptimisticLockRecord.topLevelCommit state check failed for {0} will force rollback.", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_OptimisticLockRecord_2(LockManager man);

	@Message(id = 15052, value = "LockManager::initialise() could not initialise lock store", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_LockManager_14(@Cause() Throwable arg0);

    /*
        Allocate new messages directly above this notice.
          - id: use the next id number in sequence. Don't reuse ids.
          The first two digits of the id(XXyyy) denote the module
            all message in this file should have the same prefix.
          - value: default (English) version of the log message.
          - level: according to severity semantics defined at http://docspace.corp.redhat.com/docs/DOC-30217
          Debug and trace don't get i18n. Everything else MUST be i18n.
          By convention methods with String return type have prefix get_,
            all others are log methods and have prefix <level>_
    */
}