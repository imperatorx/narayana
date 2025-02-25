/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.jboss.transaction.wstf.webservices.sc007;

import com.arjuna.webservices11.wsat.AtomicTransactionConstants;
import com.arjuna.webservices11.wsat.processors.ParticipantProcessor;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CoordinationContextType;
import com.arjuna.webservices11.wsarj.InstanceIdentifier;
import com.arjuna.webservices11.ServiceRegistry;
import com.arjuna.wsc11.ActivationCoordinator;
import com.arjuna.wsc11.RegistrationCoordinator;
import com.arjuna.wsc11.messaging.MessageId;
import com.arjuna.wst.CompletionCoordinatorParticipant;
import com.arjuna.wst.Durable2PCParticipant;
import com.arjuna.wst.Participant;
import com.arjuna.wst.Volatile2PCParticipant;
import com.arjuna.wst11.messaging.engines.ParticipantEngine;
import com.arjuna.wst11.stub.CompletionStub;

import jakarta.xml.ws.wsaddressing.W3CEndpointReference;
import jakarta.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

/**
 * Utility methods
 */
public class InteropUtil
{
    /**
     * Get a coordination context.
     * @param coordinatorURI The coordinator URI.
     * @return The coordination context.
     * @throws Exception for errors.
     */
    public static CoordinationContextType createCoordinationContext(final String coordinatorURI)
        throws Exception
    {
        return ActivationCoordinator.createCoordinationContext(coordinatorURI, MessageId.getMessageId(), AtomicTransactionConstants.WSAT_PROTOCOL, null, null) ;
    }

    /**
     * Register for completion.
     * @param context The coordination context.
     * @return The endpoint for communicating with the coordinator.
     * @throws Exception for errors.
     */
    public static CompletionCoordinatorParticipant registerCompletion(final CoordinationContextType context, final String id)
        throws Exception
    {
        final W3CEndpointReference completionCoordinator = register(context, getCompletionParticipant(id), AtomicTransactionConstants.WSAT_SUB_PROTOCOL_COMPLETION) ;
        return new CompletionStub(id, completionCoordinator);
    }
    
    /**
     * Register a durable participant in the specified coordination context.
     * @param context The coordination context.
     * @param participant The durable 2PC participant
     * @param id The participant id.
     * @return The participant engine.
     * @throws Exception for errors.
     */
    public static ParticipantEngine registerDurable2PC(final CoordinationContextType context, final Durable2PCParticipant participant, final String id)
        throws Exception
    {
        return registerParticipant(context, participant, id, AtomicTransactionConstants.WSAT_SUB_PROTOCOL_DURABLE_2PC) ;
    }
    
    /**
     * Register a volatile participant in the specified coordination context.
     * @param context The coordination context.
     * @param participant The volatile 2PC participant
     * @param id The participant id.
     * @return The participant engine.
     * @throws Exception for errors.
     */
    public static ParticipantEngine registerVolatile2PC(final CoordinationContextType context, final Volatile2PCParticipant participant, final String id)
        throws Exception
    {
        return registerParticipant(context, participant, id, AtomicTransactionConstants.WSAT_SUB_PROTOCOL_VOLATILE_2PC) ;
    }
    
    /**
     * Register the participant in the specified coordination context.
     * @param context The coordination context.
     * @param participant The participant.
     * @param id The participant id.
     * @param protocol The sub protocol to register for.
     * @return The participant engine.
     * @throws Exception for errors.
     */
    private static ParticipantEngine registerParticipant(final CoordinationContextType context, final Participant participant, final String id, final String protocol)
        throws Exception
    {
        final W3CEndpointReference coordinator = RegistrationCoordinator.register(context, MessageId.getMessageId(),
            getParticipant(id), protocol) ;
        final ParticipantEngine engine = new ParticipantEngine(participant, id, coordinator) ;
        ParticipantProcessor.getProcessor().activateParticipant(engine, id) ;
        return engine ;
    }
    
    /**
     * Register for a sub protocol.
     * @param context The coordination context.
     * @param participant The participant EPR.
     * @param protocol The protocol.
     * @return The coordinator endpoint.
     * @throws Exception for errors.
     */
    private static W3CEndpointReference register(final CoordinationContextType context, final W3CEndpointReference participant, final String protocol)
        throws Exception
    {
        return RegistrationCoordinator.register(context, MessageId.getMessageId(), participant, protocol) ;
    }

    /**
     * Get an endpoint reference for the completion initiator service.
     * @param id The completion id.
     * @return The endpoint reference.
     */
    private static W3CEndpointReference getCompletionParticipant(final String id)
    {
        final ServiceRegistry serviceRegistry = ServiceRegistry.getRegistry() ;
        final String serviceURI = serviceRegistry.getServiceURI(AtomicTransactionConstants.COMPLETION_INITIATOR_SERVICE_NAME) ;
        final W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
        builder.serviceName(AtomicTransactionConstants.COMPLETION_INITIATOR_SERVICE_QNAME);
        builder.endpointName(AtomicTransactionConstants.COMPLETION_INITIATOR_PORT_QNAME);
        builder.address(serviceURI);
        InstanceIdentifier.setEndpointInstanceIdentifier(builder, id) ;
        return builder.build();
    }

    /**
     * Get an endpoint reference for the participant service.
     * @param id The participant id.
     * @return The endpoint reference.
     */
    private static W3CEndpointReference getParticipant(final String id)
    {
        final ServiceRegistry serviceRegistry = ServiceRegistry.getRegistry() ;
        final String serviceURI = serviceRegistry.getServiceURI(AtomicTransactionConstants.PARTICIPANT_SERVICE_NAME) ;
        final W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
        builder.serviceName(AtomicTransactionConstants.PARTICIPANT_SERVICE_QNAME);
        builder.endpointName(AtomicTransactionConstants.PARTICIPANT_PORT_QNAME);
        builder.address(serviceURI);
        InstanceIdentifier.setEndpointInstanceIdentifier(builder, id) ;
        return builder.build();
    }
}