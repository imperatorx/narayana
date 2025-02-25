/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.jboss.transaction.txinterop.interop.states;

import com.arjuna.webservices11.wsba.BusinessActivityConstants;


/**
 * A conversation state for waiting on participant completed.
 */
public class BAInteropParticipantCompletedState extends BaseState
{
    /**
     * The last action.
     */
    private final String lastAction ;
    /**
     * The participant completed flag.
     */
    private boolean participantCompleted ;
    
    /**
     * Construct the participant completed test.
     * @param lastAction The last action.
     */
    public BAInteropParticipantCompletedState(final String lastAction)
    {
	this.lastAction = lastAction ;
    }
    
    /**
     * Handle the next action in the sequence.
     * @param action The SOAP action.
     * @param identifier The identifier associated with the endpoint.
     * @return true if the message should be dropped, false otherwise.
     */
    public synchronized boolean handleAction(final String action, final String identifier)
    {
        if (BusinessActivityConstants.WSBA_ACTION_COMPLETED.equals(action))
        {
            participantCompleted = true ;
            notifyAll() ;
        }
        else if (participantCompleted && lastAction.equals(action))
        {
            success() ;
        }
        return false ;
    }
    
    /**
     * Wait for the participant to complete.
     * @param timeout The timeout.
     * @return true if the participant has completed, false otherwise.
     */
    public boolean waitForParticipantCompleted(final long timeout)
    {
	final long endTime = System.currentTimeMillis() + timeout ;
	final boolean result ;
	synchronized(this)
	{
	    while(!participantCompleted)
	    {
		final long currentTimeout = endTime - System.currentTimeMillis() ;
		if (currentTimeout <= 0)
		{
		    break ;
		}
                try
                {
                    wait(currentTimeout) ;
                }
                catch (final InterruptedException ie) {}
	    }
	    
	    result = participantCompleted ;
	}
	
	if (result)
	{
	    // If it is completd then wait to allow processing of message.
	    try
	    {
		Thread.sleep(2000) ;
	    }
	    catch (final InterruptedException ie) {}
	}
	return result ;
    }
}