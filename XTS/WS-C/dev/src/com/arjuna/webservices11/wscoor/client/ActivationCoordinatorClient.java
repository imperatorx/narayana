/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.webservices11.wscoor.client;

import com.arjuna.webservices.SoapFault;
import com.arjuna.webservices11.wscoor.CoordinationConstants;
import org.jboss.ws.api.addressing.MAP;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.*;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * The Client side of the Activation Coordinator.
 * @author kevin
 */
public class ActivationCoordinatorClient
{
    /**
     * The client singleton.
     */
    private static final ActivationCoordinatorClient CLIENT = new ActivationCoordinatorClient() ;

    /**
     * Construct the activation coordinator client.
     */
    private ActivationCoordinatorClient()
    {
    }
    
    /**
     * Send a create coordination request.
     * @param map addressing context initialised with to and message ID.
     * @param coordinationType The type of the coordination.
     * @param expires The expiry interval of the context.
     * @param currentContext The current coordination context.
     * @throws SoapFault For any errors.
     * @throws IOException for any transport errors.
     */
    public CreateCoordinationContextResponseType
    sendCreateCoordination(final MAP map,
        final String coordinationType, final Expires expires, final CoordinationContext currentContext)
        throws SoapFault, IOException
    {
        final CreateCoordinationContextType request = new CreateCoordinationContextType() ;
        request.setCoordinationType(coordinationType) ;
        request.setExpires(expires) ;
        if (currentContext != null) {
            // structurally a CreateCoordinationContextType.CurrentContext and a CoordinationContext are the same i.e.
            // they are a CoordinationContextType extended with an Any list. but the schema does not use one to define
            // the other so, until we can generate them as the same type we have to interconvert here (and elsewhere)

            CreateCoordinationContextType.CurrentContext current = new CreateCoordinationContextType.CurrentContext();
            current.setCoordinationType(currentContext.getCoordinationType());
            current.setExpires(currentContext.getExpires());
            current.setIdentifier(currentContext.getIdentifier());
            current.setRegistrationService(currentContext.getRegistrationService());
            current.getAny().addAll(currentContext.getAny());
            request.setCurrentContext(current);
        } else {
            request.setCurrentContext(null) ;
        }

        // get proxy with required message id and end point address
        final ActivationPortType port = WSCOORClient.getActivationPort(map, CoordinationConstants.WSCOOR_ACTION_CREATE_COORDINATION_CONTEXT);

        // invoke remote method
        return createCoordinationContextOperation(port, request);
    }

    private CreateCoordinationContextResponseType createCoordinationContextOperation(final ActivationPortType port,
            final CreateCoordinationContextType request) {

        if (System.getSecurityManager() == null) {
            return port.createCoordinationContextOperation(request);
        }

        return AccessController.doPrivileged(new PrivilegedAction<CreateCoordinationContextResponseType>() {
            @Override
            public CreateCoordinationContextResponseType run() {
                return port.createCoordinationContextOperation(request);
            }
        });
    }

    /**
     * Get the Activation Coordinator client singleton.
     * @return The Activation Coordinator client singleton.
     */
    public static ActivationCoordinatorClient getClient()
    {
        return CLIENT ;
    }
}