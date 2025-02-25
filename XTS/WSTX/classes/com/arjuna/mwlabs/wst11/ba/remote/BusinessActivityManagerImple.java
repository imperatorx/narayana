/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.mwlabs.wst11.ba.remote;

import com.arjuna.mw.wst11.BusinessActivityManager;
import com.arjuna.mw.wst.TxContext;
import com.arjuna.mwlabs.wst11.ba.context.TxContextImple;
import com.arjuna.mwlabs.wst.ba.remote.ContextManager;
import com.arjuna.webservices11.util.PrivilegedServiceRegistryFactory;
import com.arjuna.webservices11.wsba.BusinessActivityConstants;
import com.arjuna.webservices11.wsba.processors.CoordinatorCompletionParticipantProcessor;
import com.arjuna.webservices11.wsba.processors.ParticipantCompletionParticipantProcessor;
import com.arjuna.webservices.SoapFault;
import com.arjuna.webservices11.wsarj.InstanceIdentifier;
import com.arjuna.webservices11.ServiceRegistry;
import com.arjuna.wsc.CannotRegisterException;
import com.arjuna.wsc11.RegistrationCoordinator;
import com.arjuna.wsc11.messaging.MessageId;
import com.arjuna.wst.*;
import com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine;
import com.arjuna.wst11.messaging.engines.ParticipantCompletionParticipantEngine;
import com.arjuna.wst11.stub.BACoordinatorCompletionParticipantManagerStub;
import com.arjuna.wst11.stub.BAParticipantCompletionParticipantManagerStub;
import com.arjuna.wst11.BAParticipantManager;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CoordinationContextType;

import javax.xml.namespace.QName;
import jakarta.xml.ws.wsaddressing.W3CEndpointReference;
import jakarta.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

/**
 * This is the interface that the core exposes in order to allow different
 * types of participants to be enrolled. The messaging layer continues to
 * work in terms of the registrar, but internally we map to one of these
 * methods.
 */

public class BusinessActivityManagerImple extends BusinessActivityManager
{
    public BusinessActivityManagerImple()
    {
    }

    public BAParticipantManager enlistForBusinessAgreementWithParticipantCompletion (BusinessAgreementWithParticipantCompletionParticipant bap, String id)
        throws WrongStateException, UnknownTransactionException, SystemException
    {
        final QName service = BusinessActivityConstants.PARTICIPANT_COMPLETION_PARTICIPANT_SERVICE_QNAME;
        final QName endpoint = BusinessActivityConstants.PARTICIPANT_COMPLETION_PARTICIPANT_PORT_QNAME;
        try
        {
            boolean isSecure = ((TxContextImple)currentTransaction()).isSecure();
            final ServiceRegistry serviceRegistry = PrivilegedServiceRegistryFactory.getInstance().getServiceRegistry();
            final String address = serviceRegistry.getServiceURI(BusinessActivityConstants.PARTICIPANT_COMPLETION_PARTICIPANT_SERVICE_NAME, isSecure);
            final W3CEndpointReference participant = getParticipant(service, endpoint, address, id) ;
            W3CEndpointReference baPMEndpoint = registerParticipant(participant, BusinessActivityConstants.WSBA_SUB_PROTOCOL_PARTICIPANT_COMPLETION);
            final ParticipantCompletionParticipantEngine engine = new ParticipantCompletionParticipantEngine(id, baPMEndpoint, bap) ;
            ParticipantCompletionParticipantProcessor.getProcessor().activateParticipant(engine, id) ;

            return new BAParticipantCompletionParticipantManagerStub(engine);
    	}
    	catch (com.arjuna.wsc.InvalidProtocolException ex)
    	{
    	    throw new SystemException(ex.toString());
    	}
    	catch (com.arjuna.wsc.InvalidStateException ex)
    	{
    	    throw new WrongStateException();
    	}
    	catch (com.arjuna.wsc.CannotRegisterException ex)
    	{
    	    throw new UnknownTransactionException();
    	}
    	catch (Throwable ex)
    	{
    	    ex.printStackTrace();

    	    throw new SystemException(ex.toString());
    	}
    }

    public com.arjuna.wst11.BAParticipantManager enlistForBusinessAgreementWithCoordinatorCompletion (BusinessAgreementWithCoordinatorCompletionParticipant bawcp, String id)
            throws WrongStateException, UnknownTransactionException, SystemException
    {
        final QName service = BusinessActivityConstants.COORDINATOR_COMPLETION_PARTICIPANT_SERVICE_QNAME;
        final QName endpoint = BusinessActivityConstants.COORDINATOR_COMPLETION_PARTICIPANT_PORT_QNAME;
        boolean isSecure = ((TxContextImple)currentTransaction()).isSecure();
        try
        {
            final ServiceRegistry serviceRegistry = PrivilegedServiceRegistryFactory.getInstance().getServiceRegistry();
            final String address = serviceRegistry.getServiceURI(BusinessActivityConstants.COORDINATOR_COMPLETION_PARTICIPANT_SERVICE_NAME, isSecure);
            final W3CEndpointReference participant = getParticipant(service, endpoint, address, id) ;
    	    W3CEndpointReference baPMEndpoint = registerParticipant(participant, BusinessActivityConstants.WSBA_SUB_PROTOCOL_COORDINATOR_COMPLETION);
            final CoordinatorCompletionParticipantEngine engine = new CoordinatorCompletionParticipantEngine(id, baPMEndpoint, bawcp) ;
            CoordinatorCompletionParticipantProcessor.getProcessor().activateParticipant(engine, id) ;

            return new BACoordinatorCompletionParticipantManagerStub(engine);
    	}
    	catch (com.arjuna.wsc.InvalidProtocolException ex)
    	{
    	    throw new SystemException(ex.toString());
    	}
    	catch (com.arjuna.wsc.InvalidStateException ex)
    	{
    	    throw new WrongStateException();
    	}
    	catch (com.arjuna.wsc.CannotRegisterException ex)
    	{
    	    ex.printStackTrace();

    	    throw new UnknownTransactionException();
    	}
    	catch (Throwable ex)
    	{
    	    ex.printStackTrace();

    	    throw new SystemException(ex.toString());
    	}
    }

    public TxContext suspend () throws SystemException
    {
        return _ctxManager.suspend();
    }

    // resume overwrites. Should we check first a la JTA?

    public void resume (TxContext tx) throws UnknownTransactionException, SystemException
    {
        _ctxManager.resume(tx);
    }

    public TxContext currentTransaction () throws SystemException
    {
        return _ctxManager.currentTransaction();
    }

    private final W3CEndpointReference registerParticipant(final W3CEndpointReference participant, final String protocol)
        throws com.arjuna.wsc.InvalidProtocolException, com.arjuna.wsc.InvalidStateException, com.arjuna.wsc.CannotRegisterException, SystemException
    {
    	TxContextImple currentTx = null;

    	try
    	{
    	    currentTx = (TxContextImple) _ctxManager.currentTransaction();

    	    if (currentTx == null)
        		throw new com.arjuna.wsc.NoActivityException();

            final CoordinationContextType coordinationContext = currentTx.context().getCoordinationContext() ;
            final String messageId = MessageId.getMessageId() ;
            return RegistrationCoordinator.register(coordinationContext, messageId, participant, protocol) ;
    	}
        catch (final SoapFault sf)
        {
            throw new SystemException(sf.getMessage());
        }
        catch (CannotRegisterException ex)
        {
            throw ex;
        }
    	catch (Exception ex)
    	{
    	    ex.printStackTrace();

    	    throw new SystemException(ex.toString());
    	}
    	finally
    	{
    	    try
    	    {
        		if (currentTx != null)
        		    _ctxManager.resume(currentTx);
    	    }
    	    catch (Exception ex)
    	    {
        		ex.printStackTrace();
    	    }
    	}
    }

    private W3CEndpointReference getParticipant(final QName service, final QName endpoint, final String address, final String id)
    {
        final W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
        builder.serviceName(service);
        builder.endpointName(endpoint);
        builder.address(address);
        InstanceIdentifier.setEndpointInstanceIdentifier(builder, id);
        return builder.build();
    }

    private ContextManager _ctxManager = new ContextManager();
}