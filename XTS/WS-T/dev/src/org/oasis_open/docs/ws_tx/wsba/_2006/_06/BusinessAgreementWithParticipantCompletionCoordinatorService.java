/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.oasis_open.docs.ws_tx.wsba._2006._06;

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
@WebServiceClient(name = "BusinessAgreementWithParticipantCompletionCoordinatorService", targetNamespace = "http://docs.oasis-open.org/ws-tx/wsba/2006/06", wsdlLocation = "wsdl/wsba-participant-completion-coordinator-binding.wsdl")
public class BusinessAgreementWithParticipantCompletionCoordinatorService
    extends Service
{

    private final static URL BUSINESSAGREEMENTWITHPARTICIPANTCOMPLETIONCOORDINATORSERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(org.oasis_open.docs.ws_tx.wsba._2006._06.BusinessAgreementWithParticipantCompletionCoordinatorService.class.getName());

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = org.oasis_open.docs.ws_tx.wsba._2006._06.BusinessAgreementWithParticipantCompletionCoordinatorService.class.getResource("");
            url = new URL(baseUrl, "wsdl/wsba-participant-completion-coordinator-binding.wsdl");
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'wsdl/wsba-participant-completion-coordinator-binding.wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        BUSINESSAGREEMENTWITHPARTICIPANTCOMPLETIONCOORDINATORSERVICE_WSDL_LOCATION = url;
    }

    public BusinessAgreementWithParticipantCompletionCoordinatorService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public BusinessAgreementWithParticipantCompletionCoordinatorService() {
        super(BUSINESSAGREEMENTWITHPARTICIPANTCOMPLETIONCOORDINATORSERVICE_WSDL_LOCATION, new QName("http://docs.oasis-open.org/ws-tx/wsba/2006/06", "BusinessAgreementWithParticipantCompletionCoordinatorService"));
    }

    /**
     * 
     * @return
     *     returns BusinessAgreementWithParticipantCompletionCoordinatorPortType
     */
    @WebEndpoint(name = "BusinessAgreementWithParticipantCompletionCoordinatorPortType")
    public BusinessAgreementWithParticipantCompletionCoordinatorPortType getBusinessAgreementWithParticipantCompletionCoordinatorPortType() {
        return super.getPort(new QName("http://docs.oasis-open.org/ws-tx/wsba/2006/06", "BusinessAgreementWithParticipantCompletionCoordinatorPortType"), BusinessAgreementWithParticipantCompletionCoordinatorPortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns BusinessAgreementWithParticipantCompletionCoordinatorPortType
     */
    @WebEndpoint(name = "BusinessAgreementWithParticipantCompletionCoordinatorPortType")
    public BusinessAgreementWithParticipantCompletionCoordinatorPortType getBusinessAgreementWithParticipantCompletionCoordinatorPortType(WebServiceFeature... features) {
        return super.getPort(new QName("http://docs.oasis-open.org/ws-tx/wsba/2006/06", "BusinessAgreementWithParticipantCompletionCoordinatorPortType"), BusinessAgreementWithParticipantCompletionCoordinatorPortType.class, features);
    }

}
