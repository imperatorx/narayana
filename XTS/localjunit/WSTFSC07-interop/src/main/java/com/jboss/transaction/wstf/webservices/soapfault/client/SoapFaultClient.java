/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.jboss.transaction.wstf.webservices.soapfault.client;

import com.arjuna.webservices11.SoapFault11;
import com.arjuna.webservices11.wsaddr.AddressingHelper;
import org.jboss.jbossts.xts.soapfault.SoapFaultPortType;
import org.jboss.ws.api.addressing.MAP;
import org.xmlsoap.schemas.soap.envelope.Fault;

import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.soap.AddressingFeature;
import jakarta.xml.ws.wsaddressing.W3CEndpointReference;
import java.io.IOException;
import java.util.Map;

/**
 * Base client.
 * @author kevin
 */
public class SoapFaultClient
{
    /**
     * Send a fault.
     * @param soapFault The SOAP fault.
     * @param map addressing context initialised with to and message ID.
     * @param action The action URI for the request.
     * @throws com.arjuna.webservices.SoapFault For any errors.
     * @throws java.io.IOException for any transport errors.
     */
    public static void sendSoapFault(final SoapFault11 soapFault,
                                     final MAP map,
                                     final String action)
        throws SoapFault11, IOException
    {
        if (action != null)
        {
            soapFault.setAction(action) ;
        }

        final SoapFaultPortType faultPort = getSoapFaultPort(map, action);
        Fault fault = soapFault.toFault();
        faultPort.soapFault(fault);
    }

    /**
     * fetch a coordinator activation service unique to the current thread
     * @return
     */
    private static synchronized org.jboss.jbossts.xts.soapfault.SoapFaultService getSoapFaultService()
    {
        if (soapFaultService.get() == null) {
            // we don't supply wsdl on the client side -- we want this client to address the various
            // different versions of the service which bind the fault WebMethod using different
            // soap actions. the annotations on the service and port supply all the info needed
            // to create the service and port on the client side.
            // soapFaultService.set(new SoapFaultService(null, new QName("http://jbossts.jboss.org/xts/soapfault", "SoapFaultService")));
            soapFaultService.set(new org.jboss.jbossts.xts.soapfault.SoapFaultService());
        }
        return soapFaultService.get();
    }

    private static org.jboss.jbossts.xts.soapfault.SoapFaultPortType getSoapFaultPort(final MAP map,
                                                      final String action)
    {
        org.jboss.jbossts.xts.soapfault.SoapFaultService service = getSoapFaultService();
        org.jboss.jbossts.xts.soapfault.SoapFaultPortType port = service.getPort(org.jboss.jbossts.xts.soapfault.SoapFaultPortType.class, new AddressingFeature(true, true));
        BindingProvider bindingProvider = (BindingProvider)port;
        String to = map.getTo();
        /*
         * we no longer have to add the JaxWS WSAddressingClientHandler because we can specify the WSAddressing feature
        List<Handler> customHandlerChain = new ArrayList<Handler>();
		customHandlerChain.add(new WSAddressingClientHandler());
        bindingProvider.getBinding().setHandlerChain(customHandlerChain);
         */

        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        if (action != null) {
            map.setAction(action);
        }
        AddressingHelper.configureRequestContext(requestContext, map, to, action);

        return port;
    }

    private static final ThreadLocal<org.jboss.jbossts.xts.soapfault.SoapFaultService> soapFaultService = new ThreadLocal<org.jboss.jbossts.xts.soapfault.SoapFaultService>();
}