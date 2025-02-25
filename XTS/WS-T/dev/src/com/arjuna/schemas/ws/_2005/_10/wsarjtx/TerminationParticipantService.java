/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.schemas.ws._2005._10.wsarjtx;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebEndpoint;
import jakarta.xml.ws.WebServiceClient;
import jakarta.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.2-hudson-182-RC1
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "TerminationParticipantService", targetNamespace = "http://schemas.arjuna.com/ws/2005/10/wsarjtx", wsdlLocation = "wsdl/wsarjtx-termination-participant-binding.wsdl")
public class TerminationParticipantService
    extends Service
{

    private final static URL TERMINATIONPARTICIPANTSERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(com.arjuna.schemas.ws._2005._10.wsarjtx.TerminationParticipantService.class.getName());

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = com.arjuna.schemas.ws._2005._10.wsarjtx.TerminationParticipantService.class.getResource("");
            url = new URL(baseUrl, "wsdl/wsarjtx-termination-participant-binding.wsdl");
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'wsdl/wsarjtx-termination-participant-binding.wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        TERMINATIONPARTICIPANTSERVICE_WSDL_LOCATION = url;
    }

    public TerminationParticipantService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public TerminationParticipantService() {
        super(TERMINATIONPARTICIPANTSERVICE_WSDL_LOCATION, new QName("http://schemas.arjuna.com/ws/2005/10/wsarjtx", "TerminationParticipantService"));
    }

    /**
     * 
     * @return
     *     returns TerminationParticipantPortType
     */
    @WebEndpoint(name = "TerminationParticipantPortType")
    public TerminationParticipantPortType getTerminationParticipantPortType() {
        return super.getPort(new QName("http://schemas.arjuna.com/ws/2005/10/wsarjtx", "TerminationParticipantPortType"), TerminationParticipantPortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns TerminationParticipantPortType
     */
    @WebEndpoint(name = "TerminationParticipantPortType")
    public TerminationParticipantPortType getTerminationParticipantPortType(WebServiceFeature... features) {
        return super.getPort(new QName("http://schemas.arjuna.com/ws/2005/10/wsarjtx", "TerminationParticipantPortType"), TerminationParticipantPortType.class, features);
    }

}
