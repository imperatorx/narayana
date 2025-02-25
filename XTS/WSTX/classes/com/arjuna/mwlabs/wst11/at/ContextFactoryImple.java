/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.mwlabs.wst11.at;

import com.arjuna.mw.wsas.exceptions.NoActivityException;
import com.arjuna.mw.wsas.exceptions.SystemException;
import com.arjuna.mw.wscf11.model.twophase.CoordinatorManagerFactory;
import com.arjuna.mw.wscf.model.twophase.api.CoordinatorManager;
import com.arjuna.mw.wstx.logging.wstxLogger;
import com.arjuna.mwlabs.wscf.coordinator.LocalFactory;
import com.arjuna.mwlabs.wscf.model.twophase.arjunacore.ATCoordinator;
import com.arjuna.mwlabs.wscf.model.twophase.arjunacore.CoordinatorControl;
import com.arjuna.mwlabs.wscf.model.twophase.arjunacore.CoordinatorServiceImple;
import com.arjuna.mwlabs.wscf.model.twophase.arjunacore.subordinate.SubordinateATCoordinator;
import com.arjuna.mwlabs.wscf.utils.ContextProvider;
import com.arjuna.mwlabs.wst11.at.context.ArjunaContextImple;
import com.arjuna.mwlabs.wst11.at.participants.CleanupSynchronization;
import com.arjuna.webservices11.util.PrivilegedServiceRegistryFactory;
import com.arjuna.webservices11.wsat.AtomicTransactionConstants;
import com.arjuna.webservices11.wsat.processors.ParticipantProcessor;
import com.arjuna.webservices11.wsarj.InstanceIdentifier;
import com.arjuna.webservices11.wscoor.CoordinationConstants;
import com.arjuna.webservices11.ServiceRegistry;
import com.arjuna.wsc11.ContextFactory;
import com.arjuna.wsc11.ContextFactoryMapper;
import com.arjuna.wsc11.RegistrationCoordinator;
import com.arjuna.wsc11.messaging.MessageId;
import com.arjuna.wsc.InvalidCreateParametersException;
import com.arjuna.wsc.InvalidProtocolException;
import com.arjuna.wst.Volatile2PCParticipant;
import com.arjuna.wst.Durable2PCParticipant;
import com.arjuna.wst11.messaging.engines.ParticipantEngine;
import com.arjuna.wst11.stub.SubordinateVolatile2PCStub;
import com.arjuna.wst11.stub.SubordinateDurable2PCStub;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CoordinationContext;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CoordinationContextType;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.Expires;

import jakarta.xml.ws.wsaddressing.W3CEndpointReference;
import jakarta.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;
import javax.xml.namespace.QName;

@ContextProvider(coordinationType = ArjunaContextImple.coordinationType,
        serviceType = ArjunaContextImple.serviceType,
        contextImplementation = ArjunaContextImple.class)
public class ContextFactoryImple implements ContextFactory, LocalFactory
{
	public ContextFactoryImple()
	{
		try
		{
			_coordManager = CoordinatorManagerFactory.coordinatorManager();

            _theRegistrar = new RegistrarImple();

            // install the factory for the mapper to locate

            ContextFactoryMapper.getMapper().addContextFactory(ArjunaContextImple.coordinationType, this);
        }
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * Called when a context factory is added to a context factory mapper. This
	 * method will be called multiple times if the context factory is added to
	 * multiple context factory mappers or to the same context mapper with
	 * different protocol identifiers.
	 *
	 * @param coordinationTypeURI
	 *            the coordination type uri
	 */
	public void install(final String coordinationTypeURI)
	{
	}

	/**
	 * Creates a coordination context.
	 *
	 * @param coordinationTypeURI
	 *            the coordination type uri
	 * @param expires
	 *            the expire date/time for the returned context, can be null
	 * @param currentContext
	 *            the current context, can be null
	 *
	 * @return the created coordination context
	 *
	 * @throws com.arjuna.wsc.InvalidCreateParametersException
	 *             if a parameter passed is invalid this activity identifier.
	 *
	 */

	public CoordinationContext create (final String coordinationTypeURI, final Long expires,
            final CoordinationContextType currentContext, final boolean isSecure)
			throws InvalidCreateParametersException
	{
		if (coordinationTypeURI.equals(AtomicTransactionConstants.WSAT_PROTOCOL))
		{
			try
			{
                if (currentContext == null) {
				// make sure no transaction is currently associated

				_coordManager.suspend();

				final int timeout ;
                if (expires == null)
                {
                    timeout = 0 ;
                }
                else
                {
                    final long timeoutVal = expires.longValue() ;
                    timeout = (timeoutVal > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)timeoutVal) ;
                }

				_coordManager.begin(ArjunaContextImple.serviceType, timeout);

                final ArjunaContextImple arjunaContext = ArjunaContextImple.getContext() ;
                final ServiceRegistry serviceRegistry = PrivilegedServiceRegistryFactory.getInstance().getServiceRegistry();
                final String registrationCoordinatorURI = serviceRegistry.getServiceURI(CoordinationConstants.REGISTRATION_SERVICE_NAME, isSecure) ;

                final CoordinationContext coordinationContext = new CoordinationContext() ;
                coordinationContext.setCoordinationType(coordinationTypeURI);
                CoordinationContextType.Identifier identifier = new CoordinationContextType.Identifier();
                identifier.setValue("urn:"+arjunaContext.getTransactionIdentifier());
                coordinationContext.setIdentifier(identifier) ;
                final int transactionExpires = arjunaContext.getTransactionExpires();
                if (transactionExpires > 0)
                {
                    Expires expiresInstance = new Expires();
                    expiresInstance.setValue(transactionExpires);
                    coordinationContext.setExpires(expiresInstance);
                }
                W3CEndpointReference registrationCoordinator = getRegistrationCoordinator(registrationCoordinatorURI, arjunaContext);
                coordinationContext.setRegistrationService(registrationCoordinator) ;

				/*
				 * Now add the registrar for this specific coordinator to the
				 * mapper.
				 */

				_coordManager.enlistSynchronization(new CleanupSynchronization(_coordManager.identifier().toString(), _theRegistrar));

				/*
				 * TODO Uughh! This does a suspend for us! Left over from original
				 * WS-AS stuff.
				 *
				 * TODO
				 * REFACTOR, REFACTOR, REFACTOR.
				 */

				_theRegistrar.associate();

				return coordinationContext;
                } else {
                    // we need to create a subordinate transaction and register it as both a durable and volatile
                    // participant with the registration service defined in the current context

                    SubordinateATCoordinator subTx = (SubordinateATCoordinator) createSubordinate();
                    // hmm, need to create wrappers here as the subTx is in WSCF which only knows
                    // about WSAS and WS-C and the participant is in WS-T
                    String vtppid = subTx.getVolatile2PhaseId();
                    String dtppid = subTx.getDurable2PhaseId();
                    Volatile2PCParticipant vtpp = new SubordinateVolatile2PCStub(subTx);
                    Durable2PCParticipant dtpp = new SubordinateDurable2PCStub(subTx);
                    final String messageId = MessageId.getMessageId() ;
                    W3CEndpointReference participant;
                    W3CEndpointReference coordinator;
                    participant= getParticipant(vtppid, isSecure);
                    coordinator = RegistrationCoordinator.register(currentContext, messageId, participant, AtomicTransactionConstants.WSAT_SUB_PROTOCOL_VOLATILE_2PC) ;
                    ParticipantProcessor.getProcessor().activateParticipant(new ParticipantEngine(vtpp, vtppid, coordinator), vtppid) ;
                    participant= getParticipant(dtppid, isSecure);
                    coordinator = RegistrationCoordinator.register(currentContext, messageId, participant, AtomicTransactionConstants.WSAT_SUB_PROTOCOL_DURABLE_2PC) ;
                    ParticipantProcessor.getProcessor().activateParticipant(new ParticipantEngine(dtpp, dtppid, coordinator), dtppid) ;

                    // ok now create the context
                    final ServiceRegistry serviceRegistry = PrivilegedServiceRegistryFactory.getInstance().getServiceRegistry();
                    final String registrationCoordinatorURI = serviceRegistry.getServiceURI(CoordinationConstants.REGISTRATION_SERVICE_NAME, isSecure) ;

                    final CoordinationContext coordinationContext = new CoordinationContext() ;
                    coordinationContext.setCoordinationType(coordinationTypeURI);
                    CoordinationContextType.Identifier identifier = new CoordinationContextType.Identifier();
                    String txId = subTx.get_uid().stringForm();
                    identifier.setValue("urn:" + txId);
                    coordinationContext.setIdentifier(identifier) ;
                    Expires expiresInstance = currentContext.getExpires();
                    final long transactionExpires = (expiresInstance != null ? expiresInstance.getValue() : 0);
                    if (transactionExpires > 0)
                    {
                        expiresInstance = new Expires();
                        expiresInstance.setValue(transactionExpires);
                        coordinationContext.setExpires(expiresInstance);
                    }
                    W3CEndpointReference registrationCoordinator = getRegistrationCoordinator(registrationCoordinatorURI, txId);
                    coordinationContext.setRegistrationService(registrationCoordinator) ;

                    // now associate the tx id with the sub transaction

                    _theRegistrar.associate(subTx);
                    return coordinationContext;
                }
			}
			catch (NoActivityException ex)
			{
				ex.printStackTrace();

				throw new InvalidCreateParametersException();
			}
			catch (SystemException ex)
			{
				ex.printStackTrace();
            }
			catch (com.arjuna.mw.wsas.exceptions.WrongStateException ex)
			{
				ex.printStackTrace();

				throw new InvalidCreateParametersException();
			}
			catch (Exception ex)
			{
				// TODO handle properly

				ex.printStackTrace();
			}
		}
		else {
            wstxLogger.i18NLogger.warn_mwlabs_wst_at_Context11FactoryImple_1(AtomicTransactionConstants.WSAT_PROTOCOL, coordinationTypeURI);

            throw new InvalidCreateParametersException(
                    wstxLogger.i18NLogger.get_mwlabs_wst_at_Context11FactoryImple_3()
                            + " < "
                            + AtomicTransactionConstants.WSAT_PROTOCOL
                            + ", "
                            + coordinationTypeURI + " >");
        }

		return null;
	}

    /**
     * class used to return data required to manage a bridged to subordinate transaction
     */
    public class BridgeTxData
    {
        public CoordinationContext context;
        public SubordinateATCoordinator coordinator;
        public String identifier;
    }

    /**
     * create a bridged to subordinate WS-AT 1.1 transaction, associate it with the registrar and create and return
     * a coordination context for it. n.b. this is a private, behind-the-scenes method for use by the JTA-AT
     * transaction bridge code.
     * @param subordinateType a unique string which groups subordinates for the benefit of their parent tx/app and
     * allows them to be identified and retrieved as a group during recovery
     * @param expires the timeout for the bridged to AT transaction
     * @param isSecure true if the registration cooridnator URL should use a secure address, otherwise false.
     * @return a coordination context for the bridged to transaction
     */
    public BridgeTxData createBridgedTransaction (String subordinateType, final Long expires, final boolean isSecure)
    {
        // we must have a type and it must not be the AT-AT subordinate type
        if (subordinateType == null || SubordinateATCoordinator.SUBORDINATE_TX_TYPE_AT_AT.equals(subordinateType)) {
            return null;
        }
        // we need to create a subordinate transaction and register it as both a durable and volatile
        // participant with the registration service defined in the current context

        SubordinateATCoordinator subTx = null;
        try {
            subTx = (SubordinateATCoordinator) createSubordinate(subordinateType);
        } catch (NoActivityException e) {
            // will not happen
            return null;
        } catch (InvalidProtocolException e) {
            // will not happen
            return null;
        } catch (SystemException e) {
            // may happen
            return null;
        }

        // ok now create the context

        final ServiceRegistry serviceRegistry = PrivilegedServiceRegistryFactory.getInstance().getServiceRegistry();
        final String registrationCoordinatorURI = serviceRegistry.getServiceURI(CoordinationConstants.REGISTRATION_SERVICE_NAME, isSecure) ;

        final CoordinationContext coordinationContext = new CoordinationContext() ;
        coordinationContext.setCoordinationType(AtomicTransactionConstants.WSAT_PROTOCOL);
        CoordinationContextType.Identifier identifier = new CoordinationContextType.Identifier();
        String txId = subTx.get_uid().stringForm();
        identifier.setValue("urn:" + txId);
        coordinationContext.setIdentifier(identifier) ;
        if (expires != null && expires.longValue() > 0)
        {
            Expires expiresInstance = new Expires();
            expiresInstance.setValue(expires);
            coordinationContext.setExpires(expiresInstance);
        }
        W3CEndpointReference registrationCoordinator = getRegistrationCoordinator(registrationCoordinatorURI, txId);
        coordinationContext.setRegistrationService(registrationCoordinator) ;

        // now associate the tx id with the sub transaction

        try {
            _theRegistrar.associate(subTx);
        } catch (Exception e) {
            // will not happen
        }
        BridgeTxData bridgeTxData = new BridgeTxData();
        bridgeTxData.context = coordinationContext;
        bridgeTxData.coordinator = subTx;
        bridgeTxData.identifier = txId;

        return bridgeTxData;
    }

    private W3CEndpointReference getParticipant(final String id, final boolean isSecure)
    {
        final QName serviceName = AtomicTransactionConstants.PARTICIPANT_SERVICE_QNAME;
        final QName endpointName = AtomicTransactionConstants.PARTICIPANT_PORT_QNAME;
        final ServiceRegistry serviceRegistry = PrivilegedServiceRegistryFactory.getInstance().getServiceRegistry();
        final String address = serviceRegistry.getServiceURI(AtomicTransactionConstants.PARTICIPANT_SERVICE_NAME, isSecure);
        W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
        builder.serviceName(serviceName);
        builder.endpointName(endpointName);
        builder.address(address);
        InstanceIdentifier.setEndpointInstanceIdentifier(builder, id);
        return builder.build();
    }

    private static W3CEndpointReference getRegistrationCoordinator(String registrationCoordinatorURI, ArjunaContextImple arjunaContext)
    {
        String identifier = arjunaContext.getTransactionIdentifier();
        return getRegistrationCoordinator(registrationCoordinatorURI, identifier);
    }

    private static W3CEndpointReference getRegistrationCoordinator(String registrationCoordinatorURI, String identifier)
    {
        final W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
        builder.serviceName(CoordinationConstants.REGISTRATION_SERVICE_QNAME);
        builder.endpointName(CoordinationConstants.REGISTRATION_ENDPOINT_QNAME);
        // strictly we shouldn't need to set the address because we are in the same web app as the
        // coordinator but we have to as the W3CEndpointReference implementation is incomplete
        builder.address(registrationCoordinatorURI);
        InstanceIdentifier.setEndpointInstanceIdentifier(builder, identifier);
        W3CEndpointReference registrationCoordinator = builder.build();
        return registrationCoordinator;
    }

    /**
	 * Called when a context factory is removed from a context factory mapper.
	 * This method will be called multiple times if the context factory is
	 * removed from multiple context factory mappers or from the same context
	 * factory mapper with different coordination type uris.
	 *
	 * @param coordinationTypeURI
	 *            the coordination type uri
	 */

	public void uninstall (final String coordinationTypeURI)
	{
		// we don't use this as one implementation is registered per type
	}

    public final Object createSubordinate () throws NoActivityException, InvalidProtocolException, SystemException
    {
        return createSubordinate(SubordinateATCoordinator.SUBORDINATE_TX_TYPE_AT_AT);
    }
    
	public final Object createSubordinate (String subordinateType) throws NoActivityException, InvalidProtocolException, SystemException
	{
		try
		{
			CoordinatorServiceImple coordManager = (CoordinatorServiceImple) _coordManager;
			CoordinatorControl theControl = coordManager.coordinatorControl();
			ATCoordinator subordinateTransaction = theControl.createSubordinate(subordinateType);

			/*
			 * Now add the registrar for this specific coordinator to the
			 * mapper.
			 */

			subordinateTransaction.enlistSynchronization(new CleanupSynchronization(subordinateTransaction.get_uid().stringForm(), _theRegistrar));

			_theRegistrar.associate(subordinateTransaction);

			return subordinateTransaction;
		}
		catch (Exception ex)
		{
			throw new SystemException(ex.toString());
		}
	}

	public final RegistrarImple registrar ()
	{
		return _theRegistrar;
	}

	private CoordinatorManager _coordManager;
	private RegistrarImple _theRegistrar;

}