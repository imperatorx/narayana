/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.ats.arjuna.coordinator;

import java.io.PrintWriter;

/**
 * The various state changes that a transaction can go through.
 *
 * @author Mark Little (mark@arjuna.com)
 * @version $Id: ActionStatus.java 2342 2006-03-30 13:06:17Z  $
 * @since JTS 1.0.
 */

public class ActionStatus
{

    /*
     * WARNING: do not re-order these values! Here be dragons.
     */

    public static final int RUNNING = 0;
    public static final int PREPARING = 1;
    public static final int ABORTING = 2;
    public static final int ABORT_ONLY = 3;
    public static final int ABORTED = 4;
    public static final int PREPARED = 5;
    public static final int COMMITTING = 6;
    public static final int COMMITTED = 7;
    public static final int CREATED = 8;
    public static final int INVALID = 9;
    public static final int CLEANUP = 10;
    public static final int H_ROLLBACK = 11;
    public static final int H_COMMIT = 12;
    public static final int H_MIXED = 13;
    public static final int H_HAZARD = 14;
    public static final int DISABLED = 15;
    public static final int NO_ACTION = 16;

    /**
     * @return <code>String</code> representation of the status.
     */

    public static String stringForm (int res)
    {
	switch (res)
	{
	case RUNNING:
	    return "ActionStatus.RUNNING";
	case PREPARING:
	    return "ActionStatus.PREPARING";
	case ABORTING:
	    return "ActionStatus.ABORTING";
	case ABORT_ONLY:
	    return "ActionStatus.ABORT_ONLY";
	case ABORTED:
	    return "ActionStatus.ABORTED";
	case PREPARED:
	    return "ActionStatus.PREPARED";
	case COMMITTING:
	    return "ActionStatus.COMMITTING";
	case COMMITTED:
	    return "ActionStatus.COMMITTED";
	case CREATED:
	    return "ActionStatus.CREATED";
	case INVALID:
	    return "ActionStatus.INVALID";
	case CLEANUP:
	    return "ActionStatus.CLEANUP";
	case H_ROLLBACK:
	    return "ActionStatus.H_ROLLBACK";
	case H_COMMIT:
	    return "ActionStatus.H_COMMIT";
	case H_MIXED:
	    return "ActionStatus.H_MIXED";
	case H_HAZARD:
	    return "ActionStatus.H_HAZARD";
	case DISABLED:
	    return "ActionStatus.DISABLED";
	case NO_ACTION:
	    return "ActionStatus.NO_ACTION";
	default:
	    return "Unknown";
	}
    }

    /**
     * Print the status on the specified <code>PrintWriter</code>.
     */

    public static void print (PrintWriter strm, int res)
    {
	strm.print(ActionStatus.stringForm(res));
    }

}