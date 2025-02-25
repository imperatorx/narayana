/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.webservices11.wsarjtx.sei;

import com.arjuna.schemas.ws._2005._10.wsarjtx.NotificationType;
import com.arjuna.schemas.ws._2005._10.wsarjtx.TerminationParticipantPortType;
import com.arjuna.services.framework.task.Task;
import com.arjuna.services.framework.task.TaskManager;
import com.arjuna.webservices.SoapFault;
import com.arjuna.webservices11.SoapFault11;
import org.jboss.ws.api.addressing.MAP;
import com.arjuna.webservices11.wsaddr.AddressingHelper;
import com.arjuna.webservices11.wsarj.ArjunaContext;
import com.arjuna.webservices11.wsarjtx.processors.TerminationParticipantProcessor;
import org.xmlsoap.schemas.soap.envelope.Fault;

import jakarta.annotation.Resource;
import jakarta.jws.*;
import jakarta.jws.soap.SOAPBinding;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.soap.Addressing;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.1-b03-
 * Generated source version: 2.0
 *
 */
@WebService(name = "TerminationParticipantPortType", targetNamespace = "http://schemas.arjuna.com/ws/2005/10/wsarjtx",
        // wsdlLocation = "/WEB-INF/wsdl/wsarjtx-termination-participant-binding.wsdl",
        serviceName = "TerminationParticipantService",
        portName = "TerminationParticipantPortType"
)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@HandlerChain(file="/ws-t_handlers.xml")
@Addressing(required=true)
public class TerminationParticipantPortTypeImpl implements TerminationParticipantPortType
{

    @Resource
     private WebServiceContext webServiceCtx;

    /**
     *
     * @param parameters
     */
    @WebMethod(operationName = "CompletedOperation", action = "http://schemas.arjuna.com/ws/2005/10/wsarjtx/Completed")
    @Oneway
    public void completedOperation(
        @WebParam(name = "Completed", targetNamespace = "http://schemas.arjuna.com/ws/2005/10/wsarjtx", partName = "parameters")
        NotificationType parameters)
    {
        MessageContext ctx = webServiceCtx.getMessageContext();
        final NotificationType completed = parameters;
        final MAP inboundMap = AddressingHelper.inboundMap(ctx);
        final ArjunaContext arjunaContext = ArjunaContext.getCurrentContext(ctx);

        TaskManager.getManager().queueTask(new Task() {
            public void executeTask() {
                TerminationParticipantProcessor.getProcessor().handleCompleted(completed, inboundMap, arjunaContext);
            }
        }) ;
    }

    /**
     *
     * @param parameters
     */
    @WebMethod(operationName = "ClosedOperation", action = "http://schemas.arjuna.com/ws/2005/10/wsarjtx/Closed")
    @Oneway
    public void closedOperation(
        @WebParam(name = "Closed", targetNamespace = "http://schemas.arjuna.com/ws/2005/10/wsarjtx", partName = "parameters")
        NotificationType parameters)
    {
        MessageContext ctx = webServiceCtx.getMessageContext();
        final NotificationType closed = parameters;
        final MAP inboundMap = AddressingHelper.inboundMap(ctx);
        final ArjunaContext arjunaContext = ArjunaContext.getCurrentContext(ctx);

        TaskManager.getManager().queueTask(new Task() {
            public void executeTask() {
                TerminationParticipantProcessor.getProcessor().handleClosed(closed, inboundMap, arjunaContext);
            }
        }) ;
    }

    /**
     *
     * @param parameters
     */
    @WebMethod(operationName = "CancelledOperation", action = "http://schemas.arjuna.com/ws/2005/10/wsarjtx/Cancelled")
    @Oneway
    public void cancelledOperation(
        @WebParam(name = "Cancelled", targetNamespace = "http://schemas.arjuna.com/ws/2005/10/wsarjtx", partName = "parameters")
        NotificationType parameters)
    {
        MessageContext ctx = webServiceCtx.getMessageContext();
        final NotificationType cancelled = parameters;
        final MAP inboundMap = AddressingHelper.inboundMap(ctx);
        final ArjunaContext arjunaContext = ArjunaContext.getCurrentContext(ctx);

        TaskManager.getManager().queueTask(new Task() {
            public void executeTask() {
                TerminationParticipantProcessor.getProcessor().handleCancelled(cancelled, inboundMap, arjunaContext);
            }
        }) ;
    }

    /**
     *
     * @param parameters
     */
    @WebMethod(operationName = "FaultedOperation", action = "http://schemas.arjuna.com/ws/2005/10/wsarjtx/Faulted")
    @Oneway
    public void faultedOperation(
        @WebParam(name = "Faulted", targetNamespace = "http://schemas.arjuna.com/ws/2005/10/wsarjtx", partName = "parameters")
        NotificationType parameters)
    {
        MessageContext ctx = webServiceCtx.getMessageContext();
        final NotificationType faulted = parameters;
        final MAP inboundMap = AddressingHelper.inboundMap(ctx);
        final ArjunaContext arjunaContext = ArjunaContext.getCurrentContext(ctx);

        TaskManager.getManager().queueTask(new Task() {
            public void executeTask() {
                TerminationParticipantProcessor.getProcessor().handleFaulted(faulted, inboundMap, arjunaContext);
            }
        }) ;
    }

    /**
     *
     * @param parameters
     *
     */
    @WebMethod(operationName = "FaultOperation", action = "http://schemas.arjuna.com/ws/2005/10/wsarjtx/Fault")
    @Oneway
    public void faultOperation(
        @WebParam(name = "Fault", targetNamespace = "http://schemas.xmlsoap.org/soap/envelope/", partName = "parameters")
        Fault parameters)
    {
        MessageContext ctx = webServiceCtx.getMessageContext();
        final MAP inboundMap = AddressingHelper.inboundMap(ctx);
        final ArjunaContext arjunaContext = ArjunaContext.getCurrentContext(ctx);
        final SoapFault soapFault = SoapFault11.fromFault(parameters);
    
        TaskManager.getManager().queueTask(new Task() {
            public void executeTask() {
                TerminationParticipantProcessor.getProcessor().handleSoapFault(soapFault, inboundMap, arjunaContext);
            }
        }) ;
    }

}
