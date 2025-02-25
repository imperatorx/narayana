/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.wsc.tests.arq;

import java.util.HashMap;
import java.util.Map;

import jakarta.xml.soap.SOAPFactory;
import jakarta.xml.soap.SOAPFault;
import jakarta.xml.ws.ProtocolException;
import jakarta.xml.ws.soap.SOAPFaultException;
import jakarta.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import org.jboss.ws.api.addressing.MAP;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CoordinationContext;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CoordinationContextType;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CreateCoordinationContextResponseType;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CreateCoordinationContextType;

import com.arjuna.webservices.SoapFaultType;
import com.arjuna.webservices11.wscoor.CoordinationConstants;
import com.arjuna.webservices11.wscoor.processors.ActivationCoordinatorProcessor;
import com.arjuna.wsc.tests.TestUtil;
import com.arjuna.wsc.tests.TestUtil11;

public class TestActivationCoordinatorProcessor extends
        ActivationCoordinatorProcessor
{
    private Map<String, CreateCoordinationContextDetails> messageIdMap = new HashMap<String, CreateCoordinationContextDetails>() ;

    public CreateCoordinationContextResponseType createCoordinationContext(final CreateCoordinationContextType createCoordinationContext,
        final MAP map, boolean isSecure)
    {
        final String messageId = map.getMessageID() ;
        synchronized(messageIdMap)
        {
            messageIdMap.put(messageId, new CreateCoordinationContextDetails(createCoordinationContext, map)) ;
            messageIdMap.notifyAll() ;
        }
        String coordinationType = createCoordinationContext.getCoordinationType();
        if (TestUtil.INVALID_CREATE_PARAMETERS_COORDINATION_TYPE.equals(coordinationType)) {
            try {
                SOAPFactory factory = SOAPFactory.newInstance();
                SOAPFault soapFault = factory.createFault(SoapFaultType.FAULT_SENDER.getValue(), CoordinationConstants.WSCOOR_ERROR_CODE_INVALID_PARAMETERS_QNAME);
                soapFault.addDetail().addDetailEntry(CoordinationConstants.WSCOOR_ERROR_CODE_INVALID_PARAMETERS_QNAME).addTextNode("Invalid create parameters");
                throw new SOAPFaultException(soapFault);
            } catch (Throwable th) {
                throw new ProtocolException(th);
            }
        }
        
        // we have to return a value so lets cook one up

        CreateCoordinationContextResponseType createCoordinationContextResponseType = new CreateCoordinationContextResponseType();
        CoordinationContext coordinationContext = new CoordinationContext();
        coordinationContext.setCoordinationType(coordinationType);
        coordinationContext.setExpires(createCoordinationContext.getExpires());
        String identifier = nextIdentifier();
        CoordinationContextType.Identifier identifierInstance = new CoordinationContextType.Identifier();
        identifierInstance.setValue(identifier);
        coordinationContext.setIdentifier(identifierInstance);
        W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
        builder.serviceName(CoordinationConstants.REGISTRATION_SERVICE_QNAME);
        builder.endpointName(CoordinationConstants.REGISTRATION_ENDPOINT_QNAME);
        builder.address(TestUtil.PROTOCOL_COORDINATOR_SERVICE);
        builder.build();
        coordinationContext.setRegistrationService(TestUtil11.getRegistrationEndpoint(identifier));
        createCoordinationContextResponseType.setCoordinationContext(coordinationContext);

        return createCoordinationContextResponseType;
    }

    public CreateCoordinationContextDetails getCreateCoordinationContextDetails(final String messageId, long timeout)
    {
        final long endTime = System.currentTimeMillis() + timeout ;
        synchronized(messageIdMap)
        {
            long now = System.currentTimeMillis() ;
            while(now < endTime)
            {
                final CreateCoordinationContextDetails details = (CreateCoordinationContextDetails)messageIdMap.remove(messageId) ;
                if (details != null)
                {
                    return details ;
                }
                try
                {
                    messageIdMap.wait(endTime - now) ;
                }
                catch (final InterruptedException ie) {} // ignore
                now = System.currentTimeMillis() ;
            }
            final CreateCoordinationContextDetails details = (CreateCoordinationContextDetails)messageIdMap.remove(messageId) ;
            if (details != null)
            {
                return details ;
            }
        }
        throw new NullPointerException("Timeout occurred waiting for id: " + messageId) ;
    }

    public static class CreateCoordinationContextDetails
    {
        private final CreateCoordinationContextType createCoordinationContext ;
        private final MAP map ;

        CreateCoordinationContextDetails(final CreateCoordinationContextType createCoordinationContext,
            final MAP map)
        {
            this.createCoordinationContext = createCoordinationContext ;
            this.map = map ;
        }

        public CreateCoordinationContextType getCreateCoordinationContext()
        {
            return createCoordinationContext ;
        }

        public MAP getMAP()
        {
            return map ;
        }
    }

    private static int nextIdentifier = 0;

    private synchronized String nextIdentifier()
    {
        int value = nextIdentifier++;

        return Integer.toString(value);
    }
}