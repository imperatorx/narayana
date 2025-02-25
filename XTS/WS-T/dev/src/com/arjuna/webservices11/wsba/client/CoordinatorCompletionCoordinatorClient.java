/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.webservices11.wsba.client;

import com.arjuna.webservices11.SoapFault11;
import com.arjuna.webservices11.util.PrivilegedMapBuilderFactory;
import com.arjuna.webservices11.util.PrivilegedServiceRegistryFactory;
import com.arjuna.webservices11.wsba.BusinessActivityConstants;
import com.arjuna.webservices.SoapFault;
import com.arjuna.webservices11.wsarj.InstanceIdentifier;
import com.arjuna.webservices11.ServiceRegistry;
import com.arjuna.webservices11.wsaddr.AddressingHelper;
import com.arjuna.webservices11.wsaddr.NativeEndpointReference;
import com.arjuna.webservices11.wsaddr.EndpointHelper;
import org.jboss.ws.api.addressing.MAPEndpoint;
import org.oasis_open.docs.ws_tx.wsba._2006._06.BusinessAgreementWithCoordinatorCompletionCoordinatorPortType;
import org.oasis_open.docs.ws_tx.wsba._2006._06.ExceptionType;
import org.oasis_open.docs.ws_tx.wsba._2006._06.NotificationType;
import org.oasis_open.docs.ws_tx.wsba._2006._06.StatusType;
import org.jboss.ws.api.addressing.MAP;
import org.jboss.ws.api.addressing.MAPBuilder;
import org.xmlsoap.schemas.soap.envelope.Fault;

import javax.xml.namespace.QName;
import jakarta.xml.ws.wsaddressing.W3CEndpointReference;
import java.io.IOException;

/**
 * The Client side of the Coordinator Completion Coordinator.
 * @author kevin
 */
public class CoordinatorCompletionCoordinatorClient
{
    /**
     * The client singleton.
     */
    private static final CoordinatorCompletionCoordinatorClient CLIENT = new CoordinatorCompletionCoordinatorClient() ;

    /**
     * The completed action.
     */
    private String completedAction = null;
    /**
     * The fault action.
     */
    private String failAction = null;
    /**
     * The compensated action.
     */
    private String compensatedAction = null;
    /**
     * The closed action.
     */
    private String closedAction = null;
    /**
     * The cancelled action.
     */
    private String cancelledAction = null;
    /**
     * The exit action.
     */
    private String cannotCompleteAction = null;
    /**
     * The exit action.
     */
    private String exitAction = null;
    /**
     * The get status action.
     */
    private String getStatusAction = null;
    /**
     * The status action.
     */
    private String statusAction = null;

    /**
     * The coordinator completion participant URI for replies.
     */
    private MAPEndpoint coordinatorCompletionParticipant = null;

    /**
     * The coordinator completion participant URI for replies.
     */
    private MAPEndpoint secureCoordinatorCompletionParticipant = null;

    /**
     * Construct the participant completion coordinator client.
     */
    private CoordinatorCompletionCoordinatorClient()
    {
        final MAPBuilder builder = PrivilegedMapBuilderFactory.getInstance().getBuilderInstance();
        completedAction = BusinessActivityConstants.WSBA_ACTION_COMPLETED;
        failAction = BusinessActivityConstants.WSBA_ACTION_FAIL;
        compensatedAction = BusinessActivityConstants.WSBA_ACTION_COMPENSATED;
        closedAction = BusinessActivityConstants.WSBA_ACTION_CLOSED;
        cancelledAction = BusinessActivityConstants.WSBA_ACTION_CANCELLED;
        exitAction = BusinessActivityConstants.WSBA_ACTION_EXIT;
        cannotCompleteAction = BusinessActivityConstants.WSBA_ACTION_CANNOT_COMPLETE;
        getStatusAction = BusinessActivityConstants.WSBA_ACTION_GET_STATUS;
        statusAction = BusinessActivityConstants.WSBA_ACTION_STATUS;

        final ServiceRegistry serviceRegistry = PrivilegedServiceRegistryFactory.getInstance().getServiceRegistry();
        final String coordinatorCompletionParticipantURIString =
                serviceRegistry.getServiceURI(BusinessActivityConstants.COORDINATOR_COMPLETION_PARTICIPANT_SERVICE_NAME, false) ;
        final String secureCoordinatorCompletionParticipantURIString =
                serviceRegistry.getServiceURI(BusinessActivityConstants.COORDINATOR_COMPLETION_PARTICIPANT_SERVICE_NAME, true) ;
        coordinatorCompletionParticipant = builder.newEndpoint(coordinatorCompletionParticipantURIString);
        secureCoordinatorCompletionParticipant = builder.newEndpoint(secureCoordinatorCompletionParticipantURIString);
    }

    /**
     * Send a completed request.
     * @param map addressing context initialised with to and message ID.
     * @param identifier The identifier of the initiator.
     * @throws com.arjuna.webservices.SoapFault For any errors.
     * @throws java.io.IOException for any transport errors.
     */
    public void sendCompleted(W3CEndpointReference endpoint, final MAP map, final InstanceIdentifier identifier)
        throws SoapFault, IOException
    {
        MAPEndpoint participant = getParticipant(endpoint, map);
        AddressingHelper.installFromFaultTo(map, participant, identifier);
        BusinessAgreementWithCoordinatorCompletionCoordinatorPortType port;
        port = getPort(endpoint, map, completedAction);
        NotificationType completed = new NotificationType();

        port.completedOperation(completed);
    }

    /**                                                                 Address
     * Send a fail request.
     * @param map addressing context initialised with to and message ID.
     * @param identifier The identifier of the initiator.
     * @throws com.arjuna.webservices.SoapFault For any errors.
     * @throws java.io.IOException for any transport errors.
     */
    public void sendFail(W3CEndpointReference endpoint, final MAP map, final InstanceIdentifier identifier,
        final QName exceptionIdentifier)
        throws SoapFault, IOException
    {
        MAPEndpoint participant = getParticipant(endpoint, map);
        AddressingHelper.installFromFaultTo(map, participant, identifier);
        BusinessAgreementWithCoordinatorCompletionCoordinatorPortType port;
        port = getPort(endpoint, map, failAction);
        ExceptionType fail = new ExceptionType();
        fail.setExceptionIdentifier(exceptionIdentifier);

        port.failOperation(fail);
    }

    /**
     * Send a compensated request.
     * @param map addressing context initialised with to and message ID.
     * @param identifier The identifier of the initiator.
     * @throws com.arjuna.webservices.SoapFault For any errors.
     * @throws java.io.IOException for any transport errors.
     */
    public void sendCompensated(W3CEndpointReference endpoint, final MAP map, final InstanceIdentifier identifier)
        throws SoapFault, IOException
    {
        MAPEndpoint participant = getParticipant(endpoint, map);
        AddressingHelper.installFaultTo(map, participant, identifier);
        BusinessAgreementWithCoordinatorCompletionCoordinatorPortType port;
        port = getPort(endpoint, map, compensatedAction);
        NotificationType compensated = new NotificationType();

        port.compensatedOperation(compensated);
    }

    /**
     * Send a closed request.
     * @param map addressing context initialised with to and message ID.
     * @param identifier The identifier of the initiator.
     * @throws com.arjuna.webservices.SoapFault For any errors.
     * @throws java.io.IOException for any transport errors.
     */
    public void sendClosed(W3CEndpointReference endpoint, final MAP map, final InstanceIdentifier identifier)
        throws SoapFault, IOException
    {
        MAPEndpoint participant = getParticipant(endpoint, map);
        AddressingHelper.installFaultTo(map, participant, identifier);
        BusinessAgreementWithCoordinatorCompletionCoordinatorPortType port;
        port = getPort(endpoint, map, closedAction);
        NotificationType closed = new NotificationType();

        port.closedOperation(closed);
    }

    /**
     * Send a cancelled request.
     * @param map addressing context initialised with to and message ID.
     * @param identifier The identifier of the initiator.
     * @throws com.arjuna.webservices.SoapFault For any errors.
     * @throws java.io.IOException for any transport errors.
     */
    public void sendCancelled(W3CEndpointReference endpoint, final MAP map, final InstanceIdentifier identifier)
        throws SoapFault, IOException
    {
        MAPEndpoint participant = getParticipant(endpoint, map);
        AddressingHelper.installFaultTo(map, participant, identifier);
        BusinessAgreementWithCoordinatorCompletionCoordinatorPortType port;
        port = getPort(endpoint, map, cancelledAction);
        NotificationType camcelled = new NotificationType();

        port.canceledOperation(camcelled);
    }

    /**
     * Send an exit request.
     * @param map addressing context initialised with to and message ID.
     * @param identifier The identifier of the initiator.
     * @throws com.arjuna.webservices.SoapFault For any errors.
     * @throws java.io.IOException for any transport errors.
     */
    public void sendExit(W3CEndpointReference endpoint, final MAP map, final InstanceIdentifier identifier)
        throws SoapFault, IOException
    {
        MAPEndpoint participant = getParticipant(endpoint, map);
        AddressingHelper.installFromFaultTo(map, participant, identifier);
        BusinessAgreementWithCoordinatorCompletionCoordinatorPortType port;
        port = getPort(endpoint, map, exitAction);
        NotificationType exited = new NotificationType();

        port.exitOperation(exited);
    }

    /**
     * Send a cannot complete request.
     * @param map addressing context initialised with to and message ID.
     * @param identifier The identifier of the initiator.
     * @throws com.arjuna.webservices.SoapFault For any errors.
     * @throws java.io.IOException for any transport errors.
     */
    public void sendCannotComplete(W3CEndpointReference endpoint, final MAP map, final InstanceIdentifier identifier)
        throws SoapFault, IOException
    {
        MAPEndpoint participant = getParticipant(endpoint, map);
        AddressingHelper.installFromFaultTo(map, participant, identifier);
        BusinessAgreementWithCoordinatorCompletionCoordinatorPortType port;
        port = getPort(endpoint, map, cannotCompleteAction);
        NotificationType cannotComplete = new NotificationType();

        port.cannotComplete(cannotComplete);
    }

    /**
     * Send a get status request.
     * @param map addressing context initialised with to and message ID.
     * @param identifier The identifier of the initiator.
     * @throws com.arjuna.webservices.SoapFault For any errors.
     * @throws java.io.IOException for any transport errors.
     */
    public void sendGetStatus(W3CEndpointReference endpoint, final MAP map, final InstanceIdentifier identifier)
        throws SoapFault, IOException
    {
        MAPEndpoint participant = getParticipant(endpoint, map);
        AddressingHelper.installFromFaultTo(map, participant, identifier);
        BusinessAgreementWithCoordinatorCompletionCoordinatorPortType port;
        port = getPort(endpoint, map, getStatusAction);
        NotificationType getStatus = new NotificationType();

        port.getStatusOperation(getStatus);
    }

    /**
     * Send a status request.
     * @param map addressing context initialised with to and message ID.
     * @param identifier The identifier of the initiator.
     * @throws com.arjuna.webservices.SoapFault For any errors.
     * @throws java.io.IOException for any transport errors.
     */
    public void sendStatus(W3CEndpointReference endpoint, final MAP map, final InstanceIdentifier identifier,
        final QName state)
        throws SoapFault, IOException
    {
        MAPEndpoint participant = getParticipant(endpoint, map);
        AddressingHelper.installFromFaultTo(map, participant, identifier);
        BusinessAgreementWithCoordinatorCompletionCoordinatorPortType port;
        port = getPort(endpoint, map, statusAction);
        StatusType status = new StatusType();
        status.setState(state);

        port.statusOperation(status);
    }

    /**
     * send a soap fault
     * @param soapFault the fault to be sent
     * @param endpoint the endpoint to send the fault to
     * @param map addressing context to be used to send the fault
     * @param faultAction the action to associate with the message
     */
    public void sendSoapFault(SoapFault11 soapFault, W3CEndpointReference endpoint, MAP map, String faultAction)
            throws SoapFault, IOException
    {
        AddressingHelper.installNoneReplyTo(map);
        map.setAction(faultAction);
        BusinessAgreementWithCoordinatorCompletionCoordinatorPortType port;
        port = getPort(endpoint, map, faultAction);
        Fault fault = ((SoapFault11)soapFault).toFault();
        port.soapFault(fault);
    }
    /**
     * return a participant endpoint appropriate to the type of coordinator
     * @param endpoint
     * @return either the secure participant endpoint or the non-secure endpoint
     */
    MAPEndpoint getParticipant(W3CEndpointReference endpoint, MAP map)
    {
        String address;
        if (endpoint != null) {
            NativeEndpointReference nativeRef = EndpointHelper.transform(NativeEndpointReference.class, endpoint);
            address = nativeRef.getAddress();
        } else {
            address = map.getTo();
        }

        if (address.startsWith("https")) {
            return secureCoordinatorCompletionParticipant;
        } else {
            return coordinatorCompletionParticipant;
        }
    }

    /**
     * Get the Completion Coordinator client singleton.
     * @return The Completion Coordinator client singleton.
     */
    public static CoordinatorCompletionCoordinatorClient getClient()
    {
        return CLIENT ;
    }

    /**
     * obtain a port from the coordinator endpoint configured with the instance identifier handler and the supplied
     * addressing properties supplemented with the given action
     * @param participant
     * @param map
     * @param action
     * @return
     */
    private BusinessAgreementWithCoordinatorCompletionCoordinatorPortType
    getPort(final W3CEndpointReference participant, final MAP map, final String action)
    {
        AddressingHelper.installNoneReplyTo(map);
        if (participant != null) {
            return WSBAClient.getCoordinatorCompletionCoordinatorPort(participant, action, map);
        } else {
            return WSBAClient.getCoordinatorCompletionCoordinatorPort(action, map);
        }
    }
}