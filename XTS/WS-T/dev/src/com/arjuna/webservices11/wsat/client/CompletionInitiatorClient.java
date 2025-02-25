/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package com.arjuna.webservices11.wsat.client;

import com.arjuna.webservices.SoapFault;
import com.arjuna.webservices11.util.PrivilegedMapBuilderFactory;
import com.arjuna.webservices11.util.PrivilegedServiceRegistryFactory;
import com.arjuna.webservices11.wsarj.InstanceIdentifier;
import com.arjuna.webservices11.wsat.AtomicTransactionConstants;
import com.arjuna.webservices11.ServiceRegistry;
import com.arjuna.webservices11.SoapFault11;
import com.arjuna.webservices11.wsaddr.AddressingHelper;
import com.arjuna.webservices11.wsaddr.NativeEndpointReference;
import com.arjuna.webservices11.wsaddr.EndpointHelper;
import org.jboss.ws.api.addressing.MAPEndpoint;
import org.jboss.ws.api.addressing.MAPBuilder;
import org.jboss.ws.api.addressing.MAP;
import org.oasis_open.docs.ws_tx.wsat._2006._06.CompletionInitiatorPortType;
import org.oasis_open.docs.ws_tx.wsat._2006._06.Notification;
import org.xmlsoap.schemas.soap.envelope.Fault;

import jakarta.xml.ws.wsaddressing.W3CEndpointReference;
import java.io.IOException;

/**
 * The Client side of the Completion Initiator.
 * @author kevin
 */
public class CompletionInitiatorClient
{
    /**
     * The client singleton.
     */
    private static final CompletionInitiatorClient CLIENT = new CompletionInitiatorClient() ;

    /**
     * The committed action.
     */
    private String committedAction = null;
    /**
     * The aborted action.
     */
    private String abortedAction = null;
    /**
     * The fault action.
     */
    private String faultAction = null;

    /**
     * The completion coordinator URI for replies.
     */
    private MAPEndpoint completionCoordinator ;

    /**
     * The completion coordinator URI for secure replies.
     */
    private MAPEndpoint secureCompletionCoordinator ;

    /**
     * Construct the completion initiator client.
     */
    private CompletionInitiatorClient()
    {
        final MAPBuilder builder = PrivilegedMapBuilderFactory.getInstance().getBuilderInstance();
        committedAction = AtomicTransactionConstants.WSAT_ACTION_COMMITTED;
        abortedAction = AtomicTransactionConstants.WSAT_ACTION_ABORTED;
        faultAction = AtomicTransactionConstants.WSAT_ACTION_FAULT;

        final ServiceRegistry serviceRegistry = PrivilegedServiceRegistryFactory.getInstance().getServiceRegistry();
        final String completionCoordinatorURIString =
                serviceRegistry.getServiceURI(AtomicTransactionConstants.COMPLETION_COORDINATOR_SERVICE_NAME, false) ;
        final String secureCompletionCoordinatorURIString =
                serviceRegistry.getServiceURI(AtomicTransactionConstants.COMPLETION_COORDINATOR_SERVICE_NAME, true) ;
        completionCoordinator = builder.newEndpoint(completionCoordinatorURIString);
        secureCompletionCoordinator = builder.newEndpoint(secureCompletionCoordinatorURIString);
    }

    /**
     * Send a committed request.
     * @param map addressing context initialised with to and message ID.
     * @param identifier The identifier of the initiator.
     * @throws com.arjuna.webservices.SoapFault For any errors.
     * @throws java.io.IOException for any transport errors.
     */
    public void sendCommitted(final W3CEndpointReference participant, final MAP map, final InstanceIdentifier identifier)
        throws SoapFault, IOException
    {
        MAPEndpoint coordinator = getCompletionCoordinator(participant);
        AddressingHelper.installFaultTo(map, coordinator, identifier);
        CompletionInitiatorPortType port = getPort(participant, map, committedAction);
        Notification commited = new Notification();

        port.committedOperation(commited);
    }

    /**
     * Send an aborted request.
     * @param map addressing context initialised with to and message ID.
     * @param identifier The identifier of the initiator.
     * @throws com.arjuna.webservices.SoapFault For any errors.
     * @throws java.io.IOException for any transport errors.
     */
    public void sendAborted(final W3CEndpointReference participant, final MAP map, final InstanceIdentifier identifier)
        throws SoapFault, IOException
    {
        MAPEndpoint coordinator = getCompletionCoordinator(participant);
        AddressingHelper.installFaultTo(map, coordinator, identifier);
        CompletionInitiatorPortType port = getPort(participant, map, abortedAction);
        Notification aborted = new Notification();

        port.abortedOperation(aborted);
    }

    /**
     * Send a fault.
     * @param map The addressing context.
     * @param soapFault The SOAP fault.
     * @param identifier The arjuna instance identifier.
     * @throws com.arjuna.webservices.SoapFault For any errors.
     * @throws java.io.IOException for any transport errors.
     */
    public void sendSoapFault(final W3CEndpointReference participant, final MAP map, final SoapFault soapFault, final InstanceIdentifier identifier)
        throws SoapFault, IOException
    {
        CompletionInitiatorPortType port = getPort(participant, map, faultAction);
        // convert fault to the wire format and dispatch it to the initiator
        soapFault.setAction(faultAction) ;
        Fault fault = ((SoapFault11)soapFault).toFault();
        port.soapFault(fault);
    }

    /**
     * Send a fault for an unknown participant.
     * @param map The addressing context.
     * @param soapFault The SOAP fault.
     * @param identifier The arjuna instance identifier.
     * @throws com.arjuna.webservices.SoapFault For any errors.
     * @throws java.io.IOException for any transport errors.
     */
    public void sendSoapFault(final MAP map, final SoapFault soapFault, final InstanceIdentifier identifier)
        throws SoapFault, IOException
    {
        CompletionInitiatorPortType port = getPort(null, map, faultAction);
        // convert fault to the wire format and dispatch it to the initiator
        soapFault.setAction(faultAction) ;
        Fault fault = ((SoapFault11)soapFault).toFault();
        port.soapFault(fault);
    }

    /**
     * return a completion coordinator endpoint appropriate to the type of completion initiator
     * @param participant
     * @return either the secure terminaton participant endpoint or the non-secure endpoint
     */
    MAPEndpoint getCompletionCoordinator(W3CEndpointReference participant)
    {
        NativeEndpointReference nativeRef = EndpointHelper.transform(NativeEndpointReference.class, participant);
        String address = nativeRef.getAddress();
        if (address.startsWith("https")) {
            return secureCompletionCoordinator;
        } else {
            return completionCoordinator;
        }
    }

    /**
     * Get the Completion Coordinator client singleton.
     * @return The Completion Coordinator client singleton.
     */
    public static CompletionInitiatorClient getClient()
    {
        return CLIENT;
    }

    /**
     * obtain a port from the completion participant endpoint configured with the instance identifier handler and the supplied
     * addressing properties supplemented with the given action
     * @param participant
     * @param map
     * @param action
     * @return
     */
    private CompletionInitiatorPortType getPort(final W3CEndpointReference participant,
                                                final MAP map,
                                                final String action)
    {
        AddressingHelper.installNoneReplyTo(map);
        if (participant ==  null) {
            return WSATClient.getCompletionInitiatorPort(action, map);
        } else {
            return WSATClient.getCompletionInitiatorPort(participant, action, map);
        }
    }
}
