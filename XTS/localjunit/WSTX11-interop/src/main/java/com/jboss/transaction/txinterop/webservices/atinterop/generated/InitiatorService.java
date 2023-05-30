/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */


package com.jboss.transaction.txinterop.webservices.atinterop.generated;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebEndpoint;
import jakarta.xml.ws.WebServiceClient;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.2-hudson-182-RC1
 * Generated source version: 2.0
 * 
 */
@WebServiceClient(name = "InitiatorService", targetNamespace = "http://fabrikam123.com", wsdlLocation = "wsdl/interopat-initiator-binding.wsdl")
public class InitiatorService
    extends Service
{

    private final static URL INITIATORSERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(com.jboss.transaction.txinterop.webservices.atinterop.generated.InitiatorService.class.getName());

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = com.jboss.transaction.txinterop.webservices.atinterop.generated.InitiatorService.class.getResource(".");
            url = new URL(baseUrl, "wsdl/interopat-initiator-binding.wsdl");
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'wsdl/interopat-initiator-binding.wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        INITIATORSERVICE_WSDL_LOCATION = url;
    }

    public InitiatorService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public InitiatorService() {
        super(INITIATORSERVICE_WSDL_LOCATION, new QName("http://fabrikam123.com", "InitiatorService"));
    }

    /**
     * 
     * @return
     *     returns InitiatorPortType
     */
    @WebEndpoint(name = "InitiatorPortType")
    public InitiatorPortType getInitiatorPortType() {
        return super.getPort(new QName("http://fabrikam123.com", "InitiatorPortType"), InitiatorPortType.class);
    }

}