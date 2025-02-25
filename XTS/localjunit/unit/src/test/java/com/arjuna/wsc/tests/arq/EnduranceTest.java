/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */


package com.arjuna.wsc.tests.arq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import jakarta.xml.ws.wsaddressing.W3CEndpointReference;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.ws.api.addressing.MAP;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CoordinationContext;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CoordinationContextType;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CreateCoordinationContextResponseType;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CreateCoordinationContextType;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.RegisterType;

import com.arjuna.webservices11.wsaddr.AddressingHelper;
import com.arjuna.webservices11.wsarj.ArjunaContext;
import com.arjuna.webservices11.wscoor.client.ActivationCoordinatorClient;
import com.arjuna.webservices11.wscoor.processors.ActivationCoordinatorProcessor;
import com.arjuna.webservices11.wscoor.processors.RegistrationCoordinatorProcessor;
import com.arjuna.wsc.CannotRegisterException;
import com.arjuna.wsc.InvalidCreateParametersException;
import com.arjuna.wsc.InvalidProtocolException;
import com.arjuna.wsc.InvalidStateException;
import com.arjuna.wsc.tests.TestUtil;
import com.arjuna.wsc.tests.TestUtil11;
import com.arjuna.wsc.tests.WarDeployment;
import com.arjuna.wsc.tests.arq.TestActivationCoordinatorProcessor.CreateCoordinationContextDetails;
import com.arjuna.wsc.tests.arq.TestRegistrationCoordinatorProcessor.RegisterDetails;
import com.arjuna.wsc11.ActivationCoordinator;
import com.arjuna.wsc11.RegistrationCoordinator;

@RunWith(Arquillian.class)
public class EnduranceTest extends BaseWSCTest {

    @Deployment
    public static WebArchive createDeployment() {
        return WarDeployment.getDeployment(
                TestActivationCoordinatorProcessor.class,
                TestRegistrationCoordinatorProcessor.class,
                CreateCoordinationContextDetails.class,
                RegisterDetails.class);
    }

    private ActivationCoordinatorProcessor origActivationCoordinatorProcessor ;
    private RegistrationCoordinatorProcessor origRegistrationCoordinatorProcessor ;

    private TestActivationCoordinatorProcessor testActivationCoordinatorProcessor = new TestActivationCoordinatorProcessor() ;

    private TestRegistrationCoordinatorProcessor testRegistrationCoordinatorProcessor = new TestRegistrationCoordinatorProcessor() ;

    private static final long TEST_DURATION = 30 * 1000;

    @Before
    public void setUp()
            throws Exception
            {
        origActivationCoordinatorProcessor = ActivationCoordinatorProcessor.setCoordinator(testActivationCoordinatorProcessor) ;

        origRegistrationCoordinatorProcessor = RegistrationCoordinatorProcessor.setCoordinator(testRegistrationCoordinatorProcessor) ;
            }

    @Test
    public void testCreateCoordinationContextRequest()
            throws Exception
            {
        long startTime = System.currentTimeMillis();

        int dialogIdentifierNumber = 0;
        while ((System.currentTimeMillis() - startTime) < TEST_DURATION)
        {
            doCreateCoordinationContextRequest(Integer.toString(dialogIdentifierNumber));
            dialogIdentifierNumber++;
        }
            }

    @Test
    public void testCreateCoordinationContextError()
            throws Exception
            {
        long startTime = System.currentTimeMillis();

        int dialogIdentifierNumber = 0;
        while ((System.currentTimeMillis() - startTime) < TEST_DURATION)
        {
            doCreateCoordinationContextError(Integer.toString(dialogIdentifierNumber));
            dialogIdentifierNumber++;
        }
            }

    @Test
    public void testRegisterRequest()
            throws Exception
            {
        long startTime = System.currentTimeMillis();

        int dialogIdentifierNumber = 0;
        while ((System.currentTimeMillis() - startTime) < TEST_DURATION)
        {
            doRegisterRequest(Integer.toString(dialogIdentifierNumber));
            dialogIdentifierNumber++;
        }
            }

    @Test
    public void testRegisterError()
            throws Exception
            {
        long startTime = System.currentTimeMillis();

        int dialogIdentifierNumber = 0;
        while ((System.currentTimeMillis() - startTime) < TEST_DURATION)
        {
            doRegisterError(Integer.toString(dialogIdentifierNumber), dialogIdentifierNumber % 3);
            dialogIdentifierNumber++;
        }
            }

    @Test
    public void testEachInTurn()
            throws Exception
            {
        long startTime = System.currentTimeMillis();

        int count                  = 0;
        int dialogIdentifierNumber = 0;
        while ((System.currentTimeMillis() - startTime) < TEST_DURATION)
        {
            if (count == 0)
                doCreateCoordinationContextRequest(Integer.toString(dialogIdentifierNumber));
            else if (count == 1)
                doCreateCoordinationContextError(Integer.toString(dialogIdentifierNumber));
            else if (count == 2)
                doRegisterRequest(Integer.toString(dialogIdentifierNumber));
            else
                doRegisterError(Integer.toString(dialogIdentifierNumber), (dialogIdentifierNumber / 4) % 4);

            count = (count + 1) % 4;
            dialogIdentifierNumber++;
        }
            }

    public void doCreateCoordinationContextRequest(final String messageId)
            throws Exception
            {
        final String coordinationType = TestUtil.COORDINATION_TYPE ;
        final MAP map = AddressingHelper.createRequestContext(TestUtil11.activationCoordinatorService, messageId) ;
        CreateCoordinationContextResponseType response =
                ActivationCoordinatorClient.getClient().sendCreateCoordination(map, coordinationType, null, null) ;

        final CreateCoordinationContextDetails details = testActivationCoordinatorProcessor.getCreateCoordinationContextDetails(messageId, 10000) ;
        final CreateCoordinationContextType requestCreateCoordinationContext = details.getCreateCoordinationContext() ;
        final MAP requestMap = details.getMAP() ;

        assertEquals(requestMap.getTo(), TestUtil11.activationCoordinatorService);
        assertEquals(requestMap.getMessageID(), messageId);

        assertNull(requestCreateCoordinationContext.getExpires()) ;
        assertNull(requestCreateCoordinationContext.getCurrentContext()) ;
        assertEquals(requestCreateCoordinationContext.getCoordinationType(), coordinationType);

        CoordinationContext context = response.getCoordinationContext();
        assertNotNull(context);
        assertNull(context.getExpires());
        assertEquals(context.getCoordinationType(), coordinationType);
        assertNotNull(context.getIdentifier());
            }

    public void doCreateCoordinationContextError(final String messageId)
            throws Exception
            {
        final String coordinationType = TestUtil.INVALID_CREATE_PARAMETERS_COORDINATION_TYPE;
        try {
            ActivationCoordinator.createCoordinationContext(TestUtil11.activationCoordinatorService, messageId, coordinationType, null, null) ;
        } catch (InvalidCreateParametersException icpe) {
            final CreateCoordinationContextDetails details = testActivationCoordinatorProcessor.getCreateCoordinationContextDetails(messageId, 10000) ;
            final CreateCoordinationContextType requestCreateCoordinationContext = details.getCreateCoordinationContext() ;
            final MAP requestMap = details.getMAP() ;
            assertEquals(requestMap.getTo(), TestUtil11.activationCoordinatorService);
            assertEquals(requestMap.getMessageID(), messageId);

            assertNull(requestCreateCoordinationContext.getExpires()) ;
            assertNull(requestCreateCoordinationContext.getCurrentContext()) ;
            assertEquals(requestCreateCoordinationContext.getCoordinationType(), coordinationType);
            return;
        }
        fail("expected invalid create parameters exception");
            }

    public void doRegisterRequest(final String messageId)
            throws Exception
            {
        final String protocolIdentifier = TestUtil.PROTOCOL_IDENTIFIER ;
        final W3CEndpointReference participantProtocolService = TestUtil11.getProtocolParticipantEndpoint("participant");
        final CoordinationContextType coordinationContext = new CoordinationContextType() ;
        CoordinationContextType.Identifier identifierInstance = new CoordinationContextType.Identifier();
        coordinationContext.setCoordinationType(TestUtil.COORDINATION_TYPE) ;
        coordinationContext.setIdentifier(identifierInstance) ;
        identifierInstance.setValue("identifier");
        coordinationContext.setRegistrationService(TestUtil11.getRegistrationEndpoint(identifierInstance.getValue())) ;

        W3CEndpointReference coordinator = RegistrationCoordinator.register(coordinationContext, messageId, participantProtocolService, protocolIdentifier) ;

        final RegisterDetails details = testRegistrationCoordinatorProcessor.getRegisterDetails(messageId, 10000) ;
        final RegisterType requestRegister = details.getRegister() ;
        final MAP requestMap = details.getMAP() ;
        final ArjunaContext requestArjunaContext = details.getArjunaContext() ;

        assertEquals(requestMap.getTo(), TestUtil11.registrationCoordinatorService);
        assertEquals(requestMap.getMessageID(), messageId);

        assertNotNull(requestArjunaContext) ;
        assertEquals(requestArjunaContext.getInstanceIdentifier().getInstanceIdentifier(), identifierInstance.getValue()) ;

        assertEquals(protocolIdentifier, requestRegister.getProtocolIdentifier()) ;
        assertNotNull(protocolIdentifier, requestRegister.getParticipantProtocolService()) ;

        assertNotNull(coordinator);
            }

    public void doRegisterError(final String messageId, int count)
            throws Exception
            {
        final String protocolIdentifier;
        final W3CEndpointReference participantProtocolService = TestUtil11.getProtocolParticipantEndpoint("participant");
        final CoordinationContextType coordinationContext = new CoordinationContextType() ;
        CoordinationContextType.Identifier identifierInstance = new CoordinationContextType.Identifier();
        coordinationContext.setCoordinationType(TestUtil.COORDINATION_TYPE) ;
        coordinationContext.setIdentifier(identifierInstance) ;
        identifierInstance.setValue("identifier");
        coordinationContext.setRegistrationService(TestUtil11.getRegistrationEndpoint(identifierInstance.getValue())) ;

        W3CEndpointReference coordinator = null;

        switch (count) {
            case 0:
                protocolIdentifier = TestUtil.INVALID_PROTOCOL_PROTOCOL_IDENTIFIER;
                try {
                    coordinator = RegistrationCoordinator.register(coordinationContext, messageId, participantProtocolService, protocolIdentifier) ;
                } catch (InvalidProtocolException ipe) {
                }
                if (coordinator != null) {
                    fail("expected invalid protocol exception");
                }
                break;
            case 1:
                protocolIdentifier = TestUtil.INVALID_STATE_PROTOCOL_IDENTIFIER;
                try {
                    coordinator = RegistrationCoordinator.register(coordinationContext, messageId, participantProtocolService, protocolIdentifier) ;
                } catch (InvalidStateException ise) {
                }
                if (coordinator != null) {
                    fail("expected invalid state exception");
                }
                break;
            case 3:
                protocolIdentifier = TestUtil.NO_ACTIVITY_PROTOCOL_IDENTIFIER;
                try {
                    coordinator = RegistrationCoordinator.register(coordinationContext, messageId, participantProtocolService, protocolIdentifier) ;
                } catch (CannotRegisterException cre) {
                }
                if (coordinator != null) {
                    fail("expected cannot register exception");
                }
                break;
            default:
                protocolIdentifier = TestUtil.ALREADY_REGISTERED_PROTOCOL_IDENTIFIER;
                try {
                    coordinator = RegistrationCoordinator.register(coordinationContext, messageId, participantProtocolService, protocolIdentifier) ;
                } catch (CannotRegisterException cre) {
                }
                if (coordinator != null) {
                    fail("expected cannot register exception");
                }
                break;
        }

        final RegisterDetails details = testRegistrationCoordinatorProcessor.getRegisterDetails(messageId, 10000) ;
        final RegisterType requestRegister = details.getRegister() ;
        final MAP requestMap = details.getMAP() ;
        final ArjunaContext requestArjunaContext = details.getArjunaContext() ;

        assertEquals(requestMap.getTo(), TestUtil11.registrationCoordinatorService);
        assertEquals(requestMap.getMessageID(), messageId);

        assertNotNull(requestArjunaContext) ;
        assertEquals(requestArjunaContext.getInstanceIdentifier().getInstanceIdentifier(), identifierInstance.getValue()); ;

        assertEquals(protocolIdentifier, requestRegister.getProtocolIdentifier()) ;
        assertNotNull(protocolIdentifier, requestRegister.getParticipantProtocolService()) ;
            }

    @After
    public void tearDown()
            throws Exception
            {
        ActivationCoordinatorProcessor.setCoordinator(origActivationCoordinatorProcessor) ;
        origActivationCoordinatorProcessor = null ;
        testActivationCoordinatorProcessor = null ;

        RegistrationCoordinatorProcessor.setCoordinator(origRegistrationCoordinatorProcessor) ;
        origRegistrationCoordinatorProcessor = null ;
        testRegistrationCoordinatorProcessor = null ;
            }
}