/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.jboss.transaction.txinterop.webservices.atinterop;

import java.io.IOException;

import com.arjuna.webservices.SoapFault;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CoordinationContextType;

/**
 * The interface for the participant stub.
 */
public interface ParticipantStub
{
    /**
     * Send a completion commit request.
     * @param serviceURI The target service URI.
     * @param coordinatorURI The coordinator URI.
     * @throws SoapFault For any errors.
     * @throws IOException for any transport errors.
     */
    public void completionCommit(final String serviceURI, final String coordinatorURI)
        throws SoapFault, IOException ;

    /**
     * Send a completion rollback request.
     * @param serviceURI The target service URI.
     * @param coordinatorURI The coordinator URI.
     * @throws SoapFault For any errors.
     * @throws IOException for any transport errors.
     */
    public void completionRollback(final String serviceURI, final String coordinatorURI)
        throws SoapFault, IOException ;

    /**
     * Send a commit request.
     * @param serviceURI The target service URI.
     * @param coordinationContext The coordination context.
     * @throws SoapFault For any errors.
     * @throws IOException for any transport errors.
     */
    public void commit(final String serviceURI, final CoordinationContextType coordinationContext)
        throws SoapFault, IOException ;

    /**
     * Send a rollback request.
     * @param serviceURI The target service URI.
     * @param coordinationContext The coordination context.
     * @throws SoapFault For any errors.
     * @throws IOException for any transport errors.
     */
    public void rollback(final String serviceURI, final CoordinationContextType coordinationContext)
        throws SoapFault, IOException ;

    /**
     * Send a phase2Rollback request.
     * @param serviceURI The target service URI.
     * @param coordinationContext The coordination context.
     * @throws SoapFault For any errors.
     * @throws IOException for any transport errors.
     */
    public void phase2Rollback(final String serviceURI, final CoordinationContextType coordinationContext)
        throws SoapFault, IOException ;

    /**
     * Send a readonly request.
     * @param serviceURI The target service URI.
     * @param coordinationContext The coordination context.
     * @throws SoapFault For any errors.
     * @throws IOException for any transport errors.
     */
    public void readonly(final String serviceURI, final CoordinationContextType coordinationContext)
        throws SoapFault, IOException ;

    /**
     * Send a volatileAndDurable request.
     * @param serviceURI The target service URI.
     * @param coordinationContext The coordination context.
     * @throws SoapFault For any errors.
     * @throws IOException for any transport errors.
     */
    public void volatileAndDurable(final String serviceURI, final CoordinationContextType coordinationContext)
        throws SoapFault, IOException ;

    /**
     * Send an earlyReadonly request.
     * @param serviceURI The target service URI.
     * @param coordinationContext The coordination context.
     * @throws SoapFault For any errors.
     * @throws IOException for any transport errors.
     */
    public void earlyReadonly(final String serviceURI, final CoordinationContextType coordinationContext)
        throws SoapFault, IOException ;

    /**
     * Send a earlyAborted request.
     * @param serviceURI The target service URI.
     * @param coordinationContext The coordination context.
     * @throws SoapFault For any errors.
     * @throws IOException for any transport errors.
     */
    public void earlyAborted(final String serviceURI, final CoordinationContextType coordinationContext)
        throws SoapFault, IOException ;

    /**
     * Send a replayCommit request.
     * @param serviceURI The target service URI.
     * @param coordinationContext The coordination context.
     * @throws SoapFault For any errors.
     * @throws IOException for any transport errors.
     */
    public void replayCommit(final String serviceURI, final CoordinationContextType coordinationContext)
        throws SoapFault, IOException ;

    /**
     * Send a retryPreparedCommit request.
     * @param serviceURI The target service URI.
     * @param coordinationContext The coordination context.
     * @throws SoapFault For any errors.
     * @throws IOException for any transport errors.
     */
    public void retryPreparedCommit(final String serviceURI, final CoordinationContextType coordinationContext)
        throws SoapFault, IOException ;

    /**
     * Send a retryPreparedAbort request.
     * @param serviceURI The target service URI.
     * @param coordinationContext The coordination context.
     * @throws SoapFault For any errors.
     * @throws IOException for any transport errors.
     */
    public void retryPreparedAbort(final String serviceURI, final CoordinationContextType coordinationContext)
        throws SoapFault, IOException ;

    /**
     * Send a retryCommit request.
     * @param serviceURI The target service URI.
     * @param coordinationContext The coordination context.
     * @throws SoapFault For any errors.
     * @throws IOException for any transport errors.
     */
    public void retryCommit(final String serviceURI, final CoordinationContextType coordinationContext)
        throws SoapFault, IOException ;

    /**
     * Send a preparedAfterTimeout request.
     * @param serviceURI The target service URI.
     * @param coordinationContext The coordination context.
     * @throws SoapFault For any errors.
     * @throws IOException for any transport errors.
     */
    public void preparedAfterTimeout(final String serviceURI, final CoordinationContextType coordinationContext)
        throws SoapFault, IOException ;

    /**
     * Send a lostCommitted request.
     * @param serviceURI The target service URI.
     * @param coordinationContext The coordination context.
     * @throws SoapFault For any errors.
     * @throws IOException for any transport errors.
     */
    public void lostCommitted(final String serviceURI, final CoordinationContextType coordinationContext)
        throws SoapFault, IOException ;
}