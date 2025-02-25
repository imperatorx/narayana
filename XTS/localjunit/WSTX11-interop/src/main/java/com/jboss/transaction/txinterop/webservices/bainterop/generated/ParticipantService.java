/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */


package com.jboss.transaction.txinterop.webservices.bainterop.generated;

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
@WebServiceClient(name = "ParticipantService", targetNamespace = "http://fabrikam123.com/wsba", wsdlLocation = "wsdl/interopba-participant-binding.wsdl")
public class ParticipantService
    extends Service
{

    private final static URL PARTICIPANTSERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(com.jboss.transaction.txinterop.webservices.bainterop.generated.ParticipantService.class.getName());

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = com.jboss.transaction.txinterop.webservices.bainterop.generated.ParticipantService.class.getResource(".");
            url = new URL(baseUrl, "wsdl/interopba-participant-binding.wsdl");
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'wsdl/interopba-participant-binding.wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        PARTICIPANTSERVICE_WSDL_LOCATION = url;
    }

    public ParticipantService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ParticipantService() {
        super(PARTICIPANTSERVICE_WSDL_LOCATION, new QName("http://fabrikam123.com/wsba", "ParticipantService"));
    }

    /**
     * 
     * @return
     *     returns ParticipantPortType
     */
    @WebEndpoint(name = "ParticipantPortType")
    public ParticipantPortType getParticipantPortType() {
        return super.getPort(new QName("http://fabrikam123.com/wsba", "ParticipantPortType"), ParticipantPortType.class);
    }

}