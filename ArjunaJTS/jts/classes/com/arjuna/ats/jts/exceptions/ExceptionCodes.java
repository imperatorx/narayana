/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.ats.jts.exceptions;

/**
 * Various exception values that we use in the OTS in a number of
 * situations, particularly if the ORB does not support some of
 * the standard OTS SystemExceptions. In which case we will throw other
 * SystemExceptions and set the values in them to try to reflect the
 * same type of information we would have conveyed if we had been able to
 * throw the right type of exception.
 *
 * @author Mark Little (mark@arjuna.com)
 * @version $Id: ExceptionCodes.java 2342 2006-03-30 13:06:17Z  $
 * @since JTS 1.0.
 */

public class ExceptionCodes
{
    
    /**
     * The Minor version numbers for some system exceptions
     * we may raise.
     */

    public static final int OTS_GENERAL_BASE = 20000;
    public static final int INACTIVE_TRANSACTION = OTS_GENERAL_BASE+1;

    /**
     * UNKNOWN
     */

    public static final int OTS_UNKNOWN_BASE = 30000;
    public static final int UNKNOWN_EXCEPTION = OTS_UNKNOWN_BASE+1;
    public static final int GETTIMEOUT_FAILED = OTS_UNKNOWN_BASE+2;
    public static final int UNEXPECTED_SYSTEMEXCEPTION = OTS_UNKNOWN_BASE+3;
    public static final int SYNCHRONIZATION_EXCEPTION = OTS_UNKNOWN_BASE+4;

    /**
     * BAD_OPERATION
     */

    public static final int OTS_BAD_OPERATION_BASE = 40000;
    public static final int SERVERAA_COMMIT = OTS_BAD_OPERATION_BASE+1;
    public static final int SERVERAA_PREPARE = OTS_BAD_OPERATION_BASE+2;
    public static final int ATOMICACTION_COMMIT = OTS_BAD_OPERATION_BASE+3;
    public static final int ATOMICACTION_ROLLBACK = OTS_BAD_OPERATION_BASE+4;
    public static final int NO_TRANSACTION = OTS_BAD_OPERATION_BASE+5;
    public static final int HEURISTIC_COMMIT = OTS_BAD_OPERATION_BASE+6;

    /**
     * BAD_PARAM
     */

    public static final int OTS_BAD_PARAM_BASE = 45000;
    public static final int INVALID_TIMEOUT = OTS_BAD_PARAM_BASE+1;
    public static final int BAD_TRANSACTION_CONTEXT = OTS_BAD_PARAM_BASE+2;

    /**
     * TRANSACTION_ROLLEDBACK
     */

    public static final int TRANSACTION_ROLLEDBACK_BASE = 50000;
    public static final int FAILED_TO_COMMIT = TRANSACTION_ROLLEDBACK_BASE+1;
    public static final int ALREADY_ROLLEDBACK = TRANSACTION_ROLLEDBACK_BASE+2;
    public static final int MARKED_ROLLEDBACK = TRANSACTION_ROLLEDBACK_BASE+3;

    /**
     * INVALID_TRANSACTION
     */

    public static final int INVALID_TRANSACTION_BASE = 60000;
    public static final int ALREADY_TERMINATED = INVALID_TRANSACTION_BASE+1;
    public static final int UNKNOWN_INVALID = INVALID_TRANSACTION_BASE+2;
    public static final int ALREADY_BEGUN = INVALID_TRANSACTION_BASE+3;
    public static final int ADD_FAILED = INVALID_TRANSACTION_BASE+4;
    public static final int UNAVAILABLE_COORDINATOR = INVALID_TRANSACTION_BASE+5;
    public static final int UNAVAILABLE_TRANSACTION = INVALID_TRANSACTION_BASE+6;
    public static final int INVALID_ACTION = INVALID_TRANSACTION_BASE+7;

    /**
     * WRONG_TRANSACTION
     */

    public static final int WRONG_TRANSACTION_BASE = 70000;
    public static final int NOT_CURRENT_TRANSACTION = WRONG_TRANSACTION_BASE+1;
    public static final int SERVERAA_NO_CONTROL = WRONG_TRANSACTION_BASE+2;

    /**
     * TRANSACTION_REQUIRED
     */

    public static final int TRANSACTION_REQUIRED_BASE = 80000;
    public static final int NO_TXCONTEXT = TRANSACTION_REQUIRED_BASE+1;
    
}