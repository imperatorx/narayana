/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.mwlabs.wst11.ba.remote;

import com.arjuna.mw.wst11.UserBusinessActivity;
import com.arjuna.mw.wstx.logging.wstxLogger;
import com.arjuna.mw.wsc11.context.Context;
import com.arjuna.mw.wst.TxContext;
import com.arjuna.mwlabs.wst11.ba.ContextImple;
import com.arjuna.mwlabs.wst11.ba.context.TxContextImple;
import com.arjuna.mwlabs.wst.ba.remote.ContextManager;
import com.arjuna.webservices11.util.PrivilegedServiceRegistryFactory;
import com.arjuna.webservices11.wsba.BusinessActivityConstants;
import com.arjuna.webservices11.wsarjtx.ArjunaTX11Constants;
import com.arjuna.webservices.SoapFault;
import com.arjuna.webservices11.wsarj.InstanceIdentifier;
import com.arjuna.webservices11.wscoor.CoordinationConstants;
import com.arjuna.webservices11.ServiceRegistry;
import com.arjuna.wsc11.ActivationCoordinator;
import com.arjuna.wsc.InvalidCreateParametersException;
import com.arjuna.wsc11.RegistrationCoordinator;
import com.arjuna.wsc11.messaging.MessageId;
import com.arjuna.wst.SystemException;
import com.arjuna.wst.TransactionRolledBackException;
import com.arjuna.wst.UnknownTransactionException;
import com.arjuna.wst.WrongStateException;
import com.arjuna.wst11.stub.BusinessActivityTerminatorStub;
import org.jboss.jbossts.xts.environment.XTSPropertyManager;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CoordinationContextType;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CoordinationContext;

import javax.xml.namespace.QName;
import jakarta.xml.ws.wsaddressing.W3CEndpointReference;
import jakarta.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

/**
 * This is the interface that allows transactions to be started and terminated.
 * The messaging layer converts the Commit, Rollback and Notify messages into
 * calls on this.
 *
 */

public class UserBusinessActivityImple extends UserBusinessActivity
{
    public UserBusinessActivityImple()
    {
        try
        {
            _activationCoordinatorService = XTSPropertyManager.getWSCEnvironmentBean().getCoordinatorURL11();

            /*
             * If the coordinator URL hasn't been specified via the
             * configuration file then assume we are using a locally registered
             * implementation.
             */

            if (_activationCoordinatorService == null)
            {
                final ServiceRegistry serviceRegistry = PrivilegedServiceRegistryFactory.getInstance().getServiceRegistry();
                _activationCoordinatorService = serviceRegistry.getServiceURI(CoordinationConstants.ACTIVATION_SERVICE_NAME) ;
            }
        }
        catch (Exception ex)
        {
            // TODO

            ex.printStackTrace();
        }
        _userSubordinateBusinessActivity = new UserSubordinateBusinessActivityImple();
    }

    public UserBusinessActivity getUserSubordinateBusinessActivity() {
        return _userSubordinateBusinessActivity;
    }

    public void begin () throws WrongStateException, SystemException
    {
    	begin(0);
    }

    public void begin (int timeout) throws WrongStateException, SystemException
    {
    	try
    	{
    	    if (_ctxManager.currentTransaction() != null)
        		throw new WrongStateException();

    	    Context ctx = startTransaction(timeout, null);

    	    _ctxManager.resume(new TxContextImple(ctx));
    	}
    	catch (InvalidCreateParametersException ex)
    	{
    	    tidyup();

    	    throw new SystemException(ex.toString());
    	}
    	catch (UnknownTransactionException ex)
    	{
    	    tidyup();

    	    throw new SystemException(ex.toString());
    	}
    	catch (SystemException ex)
    	{
    	    tidyup();

    	    throw ex;
    	}
    }

    public void close () throws TransactionRolledBackException, UnknownTransactionException, SystemException, WrongStateException
    {
    	TxContextImple ctx = null;

    	try
    	{
    	    ctx = (TxContextImple) _ctxManager.suspend();
            if (ctx == null) {
                throw new WrongStateException();
            }

            final String id = ctx.identifier() ;
            final W3CEndpointReference terminatorCoordinator = getTerminationCoordinator(ctx) ;

    	    BusinessActivityTerminatorStub terminatorStub = new BusinessActivityTerminatorStub(id, terminatorCoordinator);

    	    terminatorStub.close();
    	}
    	catch (SystemException ex)
    	{
    	    throw ex;
    	}
    	catch (TransactionRolledBackException ex)
    	{
    	    throw ex;
    	}
        catch (WrongStateException ex)
        {
            throw ex;
        }
    	catch (UnknownTransactionException ex)
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
    	    tidyup();
    	}
    }

    public void cancel () throws UnknownTransactionException, SystemException, WrongStateException
    {
    	TxContextImple ctx = null;

    	try
    	{
            ctx = (TxContextImple) _ctxManager.suspend();
            if (ctx == null) {
                throw new WrongStateException();
            }

            final String id = ctx.identifier() ;
            final W3CEndpointReference terminatorCoordinator = getTerminationCoordinator(ctx) ;

            BusinessActivityTerminatorStub terminatorStub = new BusinessActivityTerminatorStub(id, terminatorCoordinator);

    	    terminatorStub.cancel();
    	}
    	catch (SystemException ex)
    	{
    	    throw ex;
    	}
        catch (WrongStateException ex)
        {
            throw ex;
        }
    	catch (UnknownTransactionException ex)
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
    	    tidyup();
    	}
    }

    public void complete () throws UnknownTransactionException, SystemException, WrongStateException
    {
    	try
    	{
            final TxContextImple ctx = ((TxContextImple) _ctxManager.currentTransaction()) ;
            if (ctx == null) {
                throw new WrongStateException();
            }
            final String id = ctx.identifier() ;
            final W3CEndpointReference terminatorCoordinator = getTerminationCoordinator(ctx) ;

            BusinessActivityTerminatorStub terminatorStub = new BusinessActivityTerminatorStub(id, terminatorCoordinator);

    	    terminatorStub.complete();
    	}
    	catch (SystemException ex)
    	{
    	    throw ex;
    	}
    	catch (UnknownTransactionException ex)
    	{
    	    throw ex;
    	}
        catch (WrongStateException ex)
        {
            throw ex;
        }
    	catch (Exception ex)
    	{
    	    throw new SystemException(ex.toString());
    	}
    }

    public String transactionIdentifier ()
    {
    	try
    	{
    	    return _ctxManager.currentTransaction().toString();
    	}
    	catch (SystemException ex)
    	{
    	    return "Unknown";
    	}
    	catch (NullPointerException ex)
    	{
    	    return "Unknown";
    	}
    }

    public String toString ()
    {
    	return transactionIdentifier();
    }

    public void beginSubordinate(int timeout) throws WrongStateException, SystemException
    {
        try
        {
            TxContext current = _ctxManager.currentTransaction();
            if ((current == null) || !(current instanceof TxContextImple))
                throw new WrongStateException();

            TxContextImple currentImple = (TxContextImple) current;
            Context ctx = startTransaction(timeout, currentImple);

            _ctxManager.resume(new TxContextImple(ctx));
            // n.b. we don't enlist the subordinate transaction for completion
            // that ensures that any attempt to commit or rollback will fail
        }
        catch (com.arjuna.wsc.InvalidCreateParametersException ex)
        {
            tidyup();

            throw new SystemException(ex.toString());
        }
        catch (com.arjuna.wst.UnknownTransactionException ex)
        {
            tidyup();

            throw new SystemException(ex.toString());
        }
        catch (SystemException ex)
        {
            tidyup();

            throw ex;
        }
    }

    /**
     * fetch the coordination context type stashed in the current BA context implememtation
     * and use it to construct an instance of the coordination context extension type we need to
     * send down the wire to the activation coordinator
     * @param current the current AT context implememtation
     * @return an instance of the coordination context extension type
     */
    private CoordinationContext getContext(TxContextImple current)
    {
        CoordinationContextType contextType = getContextType(current);
        CoordinationContext context = new CoordinationContext();
        context.setCoordinationType(contextType.getCoordinationType());
        context.setExpires(contextType.getExpires());
        context.setIdentifier(contextType.getIdentifier());
        context.setRegistrationService(contextType.getRegistrationService());

        return context;
    }

    /**
     * fetch the coordination context type stashed in the current BA context implememtation
     * @param current the current AT context implememtation
     * @return the coordination context type stashed in the current AT context implememtation
     */
    private CoordinationContextType getContextType(TxContextImple current)
    {
        ContextImple contextImple = (ContextImple)current.context();
        return contextImple.getCoordinationContext();
    }

    private final Context startTransaction (int timeout, TxContextImple current) throws InvalidCreateParametersException, SystemException
    {
        try
        {
            final Long expires = (timeout > 0 ? (long) timeout : null) ;
            final String messageId = MessageId.getMessageId() ;
            final CoordinationContext currentContext = (current != null ? getContext(current) : null);
            final CoordinationContextType coordinationContext = ActivationCoordinator.createCoordinationContext(
                    _activationCoordinatorService, messageId, BusinessActivityConstants.WSBA_PROTOCOL_ATOMIC_OUTCOME, expires, currentContext) ;
            if (coordinationContext == null)
            {
                throw new SystemException(
                        wstxLogger.i18NLogger.get_mwlabs_wst11_ba_remote_UserBusinessActivityImple_2());
            }
            return new ContextImple(coordinationContext) ;
        }
        catch (final InvalidCreateParametersException icpe)
        {
            throw icpe ;
        }
        catch (final SoapFault sf)
        {
            throw new SystemException(sf.getMessage()) ;
        }
        catch (final Exception ex)
        {
            throw new SystemException(ex.toString());
        }
	}

    private W3CEndpointReference getTerminationCoordinator(final TxContextImple ctx)
        throws SystemException
    {
        final CoordinationContextType coordinationContext = ctx.context().getCoordinationContext() ;
        final String messageId = MessageId.getMessageId() ;
        try
        {
            return RegistrationCoordinator.register(coordinationContext, messageId,
                getParticipantProtocolService(ctx.identifier(), ctx.isSecure()), com.arjuna.webservices.wsarjtx.ArjunaTXConstants.WSARJTX_PROTOCOL_TERMINATION) ;
        }
        catch (final Throwable th)
        {
            throw new SystemException(wstxLogger.i18NLogger.get_mwlabs_wst11_ba_remote_UserBusinessActivityImple_3());
        }
    }

    private W3CEndpointReference getParticipantProtocolService(final String id, boolean isSecure)
    {
        // final SoapRegistry soapRegistry = SoapRegistry.getRegistry() ;
        // final String serviceURI = soapRegistry.getServiceURI(ArjunaTX11Constants.SERVICE_TERMINATION_PARTICIPANT) ;
        final QName serviceId = ArjunaTX11Constants.TERMINATION_PARTICIPANT_SERVICE_QNAME;
        final QName endpointId = ArjunaTX11Constants.TERMINATION_PARTICIPANT_PORT_QNAME;
        final ServiceRegistry serviceRegistry = PrivilegedServiceRegistryFactory.getInstance().getServiceRegistry();
        final String address = serviceRegistry.getServiceURI(ArjunaTX11Constants.TERMINATION_PARTICIPANT_SERVICE_NAME, isSecure);
        W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
        builder.serviceName(serviceId);
        builder.endpointName(endpointId);
        builder.address(address);
        InstanceIdentifier.setEndpointInstanceIdentifier(builder, id) ;
        return builder.build();
    }

    private final void tidyup ()
    {
    	try
    	{
    	    _ctxManager.suspend();
    	}
    	catch (Exception ex)
    	{
    	    ex.printStackTrace();
    	}
    }

    private ContextManager _ctxManager = new ContextManager();
    private String _activationCoordinatorService;
    private UserSubordinateBusinessActivityImple _userSubordinateBusinessActivity;
}