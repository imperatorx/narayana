/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.oasis_open.docs.ws_tx.wscoor._2006._06;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebEndpoint;
import jakarta.xml.ws.WebServiceClient;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "CoordinationFaultService", targetNamespace = "http://docs.oasis-open.org/ws-tx/wscoor/2006/06", wsdlLocation = "wsdl/wscoor-fault-binding.wsdl")
public class CoordinationFaultService
    extends Service
{

    private final static URL COORDINATIONFAULTSERVICE_WSDL_LOCATION;
    private final static WebServiceException COORDINATIONFAULTSERVICE_EXCEPTION;
    private final static QName COORDINATIONFAULTSERVICE_QNAME = new QName("http://docs.oasis-open.org/ws-tx/wscoor/2006/06", "CoordinationFaultService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("wsdl/wscoor-fault-binding.wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        COORDINATIONFAULTSERVICE_WSDL_LOCATION = url;
        COORDINATIONFAULTSERVICE_EXCEPTION = e;
    }

    public CoordinationFaultService() {
        super(__getWsdlLocation(), COORDINATIONFAULTSERVICE_QNAME);
    }

    public CoordinationFaultService(WebServiceFeature... features) {
        super(__getWsdlLocation(), COORDINATIONFAULTSERVICE_QNAME, features);
    }

    public CoordinationFaultService(URL wsdlLocation) {
        super(wsdlLocation, COORDINATIONFAULTSERVICE_QNAME);
    }

    public CoordinationFaultService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, COORDINATIONFAULTSERVICE_QNAME, features);
    }

    public CoordinationFaultService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public CoordinationFaultService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns CoordinationFaultPortType
     */
    @WebEndpoint(name = "CoordinationFaultPortType")
    public CoordinationFaultPortType getCoordinationFaultPortType() {
        return super.getPort(new QName("http://docs.oasis-open.org/ws-tx/wscoor/2006/06", "CoordinationFaultPortType"), CoordinationFaultPortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns CoordinationFaultPortType
     */
    @WebEndpoint(name = "CoordinationFaultPortType")
    public CoordinationFaultPortType getCoordinationFaultPortType(WebServiceFeature... features) {
        return super.getPort(new QName("http://docs.oasis-open.org/ws-tx/wscoor/2006/06", "CoordinationFaultPortType"), CoordinationFaultPortType.class, features);
    }

    private static URL __getWsdlLocation() {
        if (COORDINATIONFAULTSERVICE_EXCEPTION!= null) {
            throw COORDINATIONFAULTSERVICE_EXCEPTION;
        }
        return COORDINATIONFAULTSERVICE_WSDL_LOCATION;
    }

}
