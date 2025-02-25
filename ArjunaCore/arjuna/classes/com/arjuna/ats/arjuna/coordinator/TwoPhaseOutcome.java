/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.ats.arjuna.coordinator;

import java.io.PrintWriter;

/*
 * If Java had proper reference parameter passing and/or allowed
 * the wrappers for basic types to modify the contents, then we
 * would not have to do this! This class should only be an "enum".
 */

/**
 * The outcomes which can be generated when a transaction
 * attempts to prepare/commit/rollback.
 *
 * @author Mark Little (mark@arjuna.com)
 * @version $Id: TwoPhaseOutcome.java 2342 2006-03-30 13:06:17Z  $
 * @since JTS 1.0.
 */

// TODO this needs extending so that one-phase can return rollback directly.

public class TwoPhaseOutcome
{

    /*
     * WARNING!!
     * Do not re-order this list.
     */
    
    public static final int PREPARE_OK = 0;  // prepared OK
    public static final int PREPARE_NOTOK = 1; // did not prepare so force roll back
    public static final int PREPARE_READONLY = 2; // only used to read the state, so no need for second phase
    public static final int HEURISTIC_ROLLBACK = 3; // after prepare decided to roll back without waiting for coordinator
    public static final int HEURISTIC_COMMIT = 4;  // after prepare decided to commit without waiting for coordinator
    public static final int HEURISTIC_MIXED = 5;  // after prepare some sub-participants committed and some rolled back without waiting for coordinator
    public static final int HEURISTIC_HAZARD = 6;  // after prepare some sub-participants committed, some rolled back and some we don't know
    public static final int FINISH_OK = 7;  // the second phase completed ok
    public static final int FINISH_ERROR = 8;  // there was a failure during the second phase and we should retry later (not necessarily a heuristic)
    public static final int NOT_PREPARED = 9;  // participant told to do second phase operation when it hadn't seen the first phase
    public static final int ONE_PHASE_ERROR = 10;  // WARNING this has different meanings depending upon nested or top-level usage.
    public static final int INVALID_TRANSACTION = 11;  // invalid!
    public static final int PREPARE_ONE_PHASE_COMMITTED = 12;  // dynamic one-phase commit optimisation during prepare

    public TwoPhaseOutcome (int outcome)
    {
	_outcome = outcome;
    }

    public void setOutcome (int outcome)
    {
	_outcome = outcome;
    }

    public int getOutcome ()
    {
	return _outcome;
    }

    /**
     * @return <code>String</code> representation of the status.
     */

    public static String stringForm (int res)
    {
	switch (res)
	{
	case PREPARE_OK:
	    return "TwoPhaseOutcome.PREPARE_OK";
	case PREPARE_NOTOK:
	    return "TwoPhaseOutcome.PREPARE_NOTOK";
	case PREPARE_READONLY:
	    return "TwoPhaseOutcome.PREPARE_READONLY";
	case HEURISTIC_ROLLBACK:
	    return "TwoPhaseOutcome.HEURISTIC_ROLLBACK";
	case HEURISTIC_COMMIT:
	    return "TwoPhaseOutcome.HEURISTIC_COMMIT";
	case HEURISTIC_MIXED:
	    return "TwoPhaseOutcome.HEURISTIC_MIXED";
	case HEURISTIC_HAZARD:
	    return "TwoPhaseOutcome.HEURISTIC_HAZARD";
	case FINISH_OK:
	    return "TwoPhaseOutcome.FINISH_OK";
	case FINISH_ERROR:
	    return "TwoPhaseOutcome.FINISH_ERROR";
	case NOT_PREPARED:
	    return "TwoPhaseOutcome.NOT_PREPARED";
	case ONE_PHASE_ERROR:
	    return "TwoPhaseOutcome.ONE_PHASE_ERROR";
	case INVALID_TRANSACTION:
	    return "TwoPhaseOutcome.INVALID_TRANSACTION";
	case PREPARE_ONE_PHASE_COMMITTED:
	    return "TwoPhaseOutcome.PREPARE_ONE_PHASE_COMMITTED";
	default:
	    return "Unknown";
	}
    }
    
    public static void print (PrintWriter strm, int res)
    {
	strm.print(TwoPhaseOutcome.stringForm(res));
	strm.flush();
    }

    private int _outcome;
	
}