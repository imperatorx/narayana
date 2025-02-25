/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.jboss.transaction.wstf.webservices.sc007.sei;

import com.jboss.transaction.wstf.webservices.sc007.processors.InitiatorProcessor;
import com.arjuna.webservices11.SoapFault11;
import com.arjuna.webservices11.wsaddr.AddressingHelper;
import org.jboss.ws.api.addressing.MAP;
import org.xmlsoap.schemas.soap.envelope.Fault;

import jakarta.xml.ws.Action;
import jakarta.xml.ws.RequestWrapper;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.soap.Addressing;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.annotation.Resource;

/**
 * Implementor class for OASIS WS-Interop 1.1 Initiator Service
 */
import jakarta.jws.Oneway;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.jws.WebParam;
import jakarta.jws.soap.SOAPBinding;

/**
 * Implementation class for WSTX 1.1 AT Interop Test Initiator service
 */
@WebService(name = "InitiatorPortType",
        targetNamespace = "http://www.wstf.org/sc007",
        // wsdlLocation="/WEB-INF/wsdl/sc007.wsdl",
        portName = "sc007InitiatorPort",
        serviceName="sc007Service")
@Addressing(required=true)
public class InitiatorPortTypeImpl // implements InitiatorPortType, SoapFaultPortType
{

    /**
     * injected resource providing access to WSA addressing properties
     */
    @Resource
    private WebServiceContext webServiceCtx;

    /**
     *
     */
    @WebMethod(operationName = "Response", action = "http://www.wstf.org/docs/scenarios/sc007/Response")
    @Oneway
    @Action(input="http://www.wstf.org/docs/scenarios/sc007/Response")
    @RequestWrapper(localName = "Response", targetNamespace = "http://www.wstf.org/sc007", className = "com.jboss.transaction.wstf.webservices.sc007.generated.TestMessageType")
    public void response()
    {
        MessageContext ctx = webServiceCtx.getMessageContext();
        MAP inboundMap = AddressingHelper.inboundMap(ctx);

        InitiatorProcessor.getInitiator().handleResponse(inboundMap) ;
    }

    @WebMethod(operationName = "SoapFault", action = "http://www.wstf.org/docs/scenarios/sc007/SoapFault")
    @Oneway
    @Action(input="http://www.wstf.org/docs/scenarios/sc007/SoapFault")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    public void soapFault(
            @WebParam(name = "Fault", targetNamespace = "http://schemas.xmlsoap.org/soap/envelope/", partName = "parameters")
            Fault fault)
    {
        MessageContext ctx = webServiceCtx.getMessageContext();
        MAP inboundMap = AddressingHelper.inboundMap(ctx);

        SoapFault11 soapFaultInternal = SoapFault11.fromFault(fault);
        InitiatorProcessor.getInitiator().handleSoapFault(soapFaultInternal, inboundMap) ;
    }

}