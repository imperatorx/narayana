/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.mwlabs.wsas.activity;

public class ReaperElement implements Comparable<ReaperElement>
{

    /*
     * Currently, once created the reaper object and thread stay around
     * forever.
     * We could destroy both once the list of transactions is null. Depends
     * upon the relative cost of recreating them over keeping them around.
     */

    public ReaperElement (ActivityImple act, int timeout)
    {
	_activity = act;
	_timeout = timeout;

	/*
	 * Given a timeout period in seconds, calculate its absolute value
	 * from the current time of day in milliseconds.
	 */
	
	_absoluteTimeout = timeout*1000 + System.currentTimeMillis();
    }

    public ActivityImple _activity;
    public long          _absoluteTimeout;
    public int           _timeout;

    public int compareTo(ReaperElement o)
    {
        if (this == o) {
            return 0;
        }

        long otherAbsoluteTimeout = o._absoluteTimeout;
        if (_absoluteTimeout < otherAbsoluteTimeout) {
            return -1;
        } else if (_absoluteTimeout > otherAbsoluteTimeout) {
            return 1;
        } else {
            // enforce law of trichotomy
            int hashcode = this.hashCode();
            int otherHashcode = o.hashCode();
            if (hashcode < otherHashcode) {
                return -1;
            } else if (hashcode > otherHashcode) {
                return 1;
            } else {
                // should not happen (often :-)
                return 0;
            }
        }
    }
}