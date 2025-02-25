/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */


package com.jboss.transaction.wstf.webservices.sc007.generated;

import jakarta.jws.Oneway;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.xml.ws.RequestWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.2-hudson-182-RC1
 * Generated source version: 2.0
 * 
 */
@WebService(name = "InitiatorPortType", targetNamespace = "http://www.wstf.org/sc007")
public interface InitiatorPortType {


    /**
     * 
     */
    @WebMethod(operationName = "Response", action = "http://www.wstf.org/docs/scenarios/sc007/Response")
    @Oneway
    @RequestWrapper(localName = "Response", targetNamespace = "http://www.wstf.org/sc007", className = "com.jboss.transaction.wstf.webservices.sc007.generated.TestMessageType")
    public void response();

}