/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.mw.wsas.activity;

import com.arjuna.mw.wsas.context.Context;

import com.arjuna.mw.wsas.completionstatus.CompletionStatus;

import com.arjuna.mw.wsas.exceptions.SystemException;

/**
 * An HLS is registered with activities to be informed of their lifecycle
 * and to augment the basic notion of what an activity is.
 *
 * Currently each HLS is registered globally so that all activities on
 * all threads know about them. However, we may want to have finer
 * granularity such that an HLS may only be registered with a specific
 * thread.
 *
 * @author Mark Little (mark.little@arjuna.com)
 * @version $Id: HLS.java,v 1.2 2005/05/19 12:13:16 nmcl Exp $
 * @since 1.0.
 */

public interface HLS
{

    /**
     * An activity has begun and is active on the current thread.
     *
     * @exception SystemException Thrown if an error occurs. Any error
     * will cause the activity to be failed.
     */

    public void begun () throws SystemException;

    /**
     * The current activity is completing with the specified completion status.
     *
     * @param cs The completion status to use.
     *
     * @exception SystemException Thrown if an error occurs. Any error
     * will cause the activity to be failed.
     *
     * @return The result of terminating the relationship of this HLS and
     * the current activity.
     */

    /*
     * How do we deal with the case where an HLS has seen the activity to
     * be completing successfully then another sets it to fail? Unless we
     * introduce a multi-phase completion protocol this will always be
     * a problem.
     */

    public Outcome complete (CompletionStatus cs) throws SystemException;

    /**
     * The activity is being suspended, but is still active on the current
     * thread.
     */

    public void suspended () throws SystemException;

    /**
     * The activity has been resumed on the current thread and is active on
     * that thread.
     */

    public void resumed () throws SystemException;

    /**
     * The activity has completed and is still active on the current
     * thread.
     */

    public void completed () throws SystemException;

    /**
     * The HLS name.
     */

    public String identity () throws SystemException;

    /**
     * The activity service maintains a priority ordered list of HLS
     * implementations. If an HLS wishes to be ordered based on priority
     * then it can return a non-negative value: the higher the value,
     * the higher the priority and hence the earlier in the list of HLSes
     * it will appear (and be used in).
     *
     * @return a positive value for the priority for this HLS, or zero/negative
     * if the order is not important.
     */

    public int priority () throws SystemException;

    /**
     * Return the context augmentation for this HLS, if any on the current
     * activity, i.e., the activity active on the current thread.
     *
     * @return a context object or null if no augmentation is necessary.
     */

    public Context context () throws SystemException;
    
}