/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.schemas.ws._2005._10.wsarjtx;

import jakarta.jws.Oneway;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.xml.bind.annotation.XmlSeeAlso;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.2-hudson-182-RC1
 * Generated source version: 2.1
 * 
 */
@WebService(name = "TerminationCoordinatorPortType", targetNamespace = "http://schemas.arjuna.com/ws/2005/10/wsarjtx")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface TerminationCoordinatorPortType {


    /**
     * 
     * @param parameters
     */
    @WebMethod(operationName = "CompleteOperation", action = "http://schemas.arjuna.com/ws/2005/10/wsarjtx/Complete")
    @Oneway
    public void completeOperation(
        @WebParam(name = "Complete", targetNamespace = "http://schemas.arjuna.com/ws/2005/10/wsarjtx", partName = "parameters")
        NotificationType parameters);

    /**
     * 
     * @param parameters
     */
    @WebMethod(operationName = "CloseOperation", action = "http://schemas.arjuna.com/ws/2005/10/wsarjtx/Close")
    @Oneway
    public void closeOperation(
        @WebParam(name = "Close", targetNamespace = "http://schemas.arjuna.com/ws/2005/10/wsarjtx", partName = "parameters")
        NotificationType parameters);

    /**
     * 
     * @param parameters
     */
    @WebMethod(operationName = "CancelOperation", action = "http://schemas.arjuna.com/ws/2005/10/wsarjtx/Cancel")
    @Oneway
    public void cancelOperation(
        @WebParam(name = "Cancel", targetNamespace = "http://schemas.arjuna.com/ws/2005/10/wsarjtx", partName = "parameters")
        NotificationType parameters);

}
