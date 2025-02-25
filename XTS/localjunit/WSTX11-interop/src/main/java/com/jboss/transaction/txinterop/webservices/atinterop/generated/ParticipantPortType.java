/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */


package com.jboss.transaction.txinterop.webservices.atinterop.generated;

import jakarta.jws.Oneway;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.xml.ws.RequestWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.2-hudson-182-RC1
 * Generated source version: 2.0
 * 
 */
@WebService(name = "ParticipantPortType", targetNamespace = "http://fabrikam123.com")
public interface ParticipantPortType {


    /**
     * 
     * @param parameters
     */
    @WebMethod(operationName = "CompletionCommit", action = "http://fabrikam123.com/CompletionCommit")
    @Oneway
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    public void completionCommit(
        @WebParam(name = "CompletionCommit", targetNamespace = "http://fabrikam123.com", partName = "parameters")
        String parameters);

    /**
     * 
     * @param parameters
     */
    @WebMethod(operationName = "CompletionRollback", action = "http://fabrikam123.com/CompletionRollback")
    @Oneway
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    public void completionRollback(
        @WebParam(name = "CompletionRollback", targetNamespace = "http://fabrikam123.com", partName = "parameters")
        String parameters);

    /**
     * 
     */
    @WebMethod(operationName = "Commit", action = "http://fabrikam123.com/Commit")
    @Oneway
    @RequestWrapper(localName = "Commit", targetNamespace = "http://fabrikam123.com", className = "com.jboss.transaction.txinterop.webservices.atinterop.generated.TestMessageType")
    public void commit();

    /**
     * 
     */
    @WebMethod(operationName = "Rollback", action = "http://fabrikam123.com/Rollback")
    @Oneway
    @RequestWrapper(localName = "Rollback", targetNamespace = "http://fabrikam123.com", className = "com.jboss.transaction.txinterop.webservices.atinterop.generated.TestMessageType")
    public void rollback();

    /**
     * 
     */
    @WebMethod(operationName = "Phase2Rollback", action = "http://fabrikam123.com/Phase2Rollback")
    @Oneway
    @RequestWrapper(localName = "Phase2Rollback", targetNamespace = "http://fabrikam123.com", className = "com.jboss.transaction.txinterop.webservices.atinterop.generated.TestMessageType")
    public void phase2Rollback();

    /**
     * 
     */
    @WebMethod(operationName = "Readonly", action = "http://fabrikam123.com/Readonly")
    @Oneway
    @RequestWrapper(localName = "Readonly", targetNamespace = "http://fabrikam123.com", className = "com.jboss.transaction.txinterop.webservices.atinterop.generated.TestMessageType")
    public void readonly();

    /**
     * 
     */
    @WebMethod(operationName = "VolatileAndDurable", action = "http://fabrikam123.com/VolatileAndDurable")
    @Oneway
    @RequestWrapper(localName = "VolatileAndDurable", targetNamespace = "http://fabrikam123.com", className = "com.jboss.transaction.txinterop.webservices.atinterop.generated.TestMessageType")
    public void volatileAndDurable();

    /**
     * 
     */
    @WebMethod(operationName = "EarlyReadonly", action = "http://fabrikam123.com/EarlyReadonly")
    @Oneway
    @RequestWrapper(localName = "EarlyReadonly", targetNamespace = "http://fabrikam123.com", className = "com.jboss.transaction.txinterop.webservices.atinterop.generated.TestMessageType")
    public void earlyReadonly();

    /**
     * 
     */
    @WebMethod(operationName = "EarlyAborted", action = "http://fabrikam123.com/EarlyAborted")
    @Oneway
    @RequestWrapper(localName = "EarlyAborted", targetNamespace = "http://fabrikam123.com", className = "com.jboss.transaction.txinterop.webservices.atinterop.generated.TestMessageType")
    public void earlyAborted();

    /**
     * 
     */
    @WebMethod(operationName = "ReplayCommit", action = "http://fabrikam123.com/ReplayCommit")
    @Oneway
    @RequestWrapper(localName = "ReplayCommit", targetNamespace = "http://fabrikam123.com", className = "com.jboss.transaction.txinterop.webservices.atinterop.generated.TestMessageType")
    public void replayCommit();

    /**
     * 
     */
    @WebMethod(operationName = "RetryPreparedCommit", action = "http://fabrikam123.com/RetryPreparedCommit")
    @Oneway
    @RequestWrapper(localName = "RetryPreparedCommit", targetNamespace = "http://fabrikam123.com", className = "com.jboss.transaction.txinterop.webservices.atinterop.generated.TestMessageType")
    public void retryPreparedCommit();

    /**
     * 
     */
    @WebMethod(operationName = "RetryPreparedAbort", action = "http://fabrikam123.com/RetryPreparedAbort")
    @Oneway
    @RequestWrapper(localName = "RetryPreparedAbort", targetNamespace = "http://fabrikam123.com", className = "com.jboss.transaction.txinterop.webservices.atinterop.generated.TestMessageType")
    public void retryPreparedAbort();

    /**
     * 
     */
    @WebMethod(operationName = "RetryCommit", action = "http://fabrikam123.com/RetryCommit")
    @Oneway
    @RequestWrapper(localName = "RetryCommit", targetNamespace = "http://fabrikam123.com", className = "com.jboss.transaction.txinterop.webservices.atinterop.generated.TestMessageType")
    public void retryCommit();

    /**
     * 
     */
    @WebMethod(operationName = "PreparedAfterTimeout", action = "http://fabrikam123.com/PreparedAfterTimeout")
    @Oneway
    @RequestWrapper(localName = "PreparedAfterTimeout", targetNamespace = "http://fabrikam123.com", className = "com.jboss.transaction.txinterop.webservices.atinterop.generated.TestMessageType")
    public void preparedAfterTimeout();

    /**
     * 
     */
    @WebMethod(operationName = "LostCommitted", action = "http://fabrikam123.com/LostCommitted")
    @Oneway
    @RequestWrapper(localName = "LostCommitted", targetNamespace = "http://fabrikam123.com", className = "com.jboss.transaction.txinterop.webservices.atinterop.generated.TestMessageType")
    public void lostCommitted();

}