/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package com.arjuna.webservices11.wsba.client;

import com.arjuna.webservices11.util.PrivilegedServiceFactory;
import com.arjuna.webservices11.util.PrivilegedServiceHelper;
import com.arjuna.webservices11.wsaddr.AddressingHelper;
import org.jboss.ws.api.addressing.MAP;
import org.oasis_open.docs.ws_tx.wsba._2006._06.*;

import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.soap.AddressingFeature;
import jakarta.xml.ws.wsaddressing.W3CEndpointReference;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: adinn
 * Date: Oct 7, 2007
 * Time: 3:14:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class WSBAClient
{
    // TODO -- do we really need a thread local here or can we just use one service?
    /**
     *  thread local which maintains a per thread participant completion coordinator service instance
     */
    private static ThreadLocal<BusinessAgreementWithParticipantCompletionCoordinatorService> participantCompletionCoordinatorService = new ThreadLocal<BusinessAgreementWithParticipantCompletionCoordinatorService>();

    /**
     *  thread local which maintains a per thread participant completion participant service instance
     */
    private static ThreadLocal<BusinessAgreementWithParticipantCompletionParticipantService> participantCompletionParticipantService = new ThreadLocal<BusinessAgreementWithParticipantCompletionParticipantService>();

    /**
     *  thread local which maintains a per thread coordinator completion coordinator service instance
     */
    private static ThreadLocal<BusinessAgreementWithCoordinatorCompletionCoordinatorService> coordinatorCompletionCoordinatorService = new ThreadLocal<BusinessAgreementWithCoordinatorCompletionCoordinatorService>();

    /**
     *  thread local which maintains a per thread coordinator completion participant service instance
     */
    private static ThreadLocal<BusinessAgreementWithCoordinatorCompletionParticipantService> coordinatorCompletionParticipantService = new ThreadLocal<BusinessAgreementWithCoordinatorCompletionParticipantService>();

    /**
     * fetch a participant completion coordinator service unique to the current thread
     * @return
     */
    private static synchronized BusinessAgreementWithParticipantCompletionCoordinatorService getParticipantCompletionCoordinatorService()
    {
        if (participantCompletionCoordinatorService.get() == null) {
            participantCompletionCoordinatorService.set(PrivilegedServiceFactory.getInstance(
                    BusinessAgreementWithParticipantCompletionCoordinatorService.class).getService());
        }
        return participantCompletionCoordinatorService.get();
    }

    /**
     * fetch a participant completion participant service unique to the current thread
     * @return
     */
    private static synchronized BusinessAgreementWithParticipantCompletionParticipantService getParticipantCompletionParticipantService()
    {
        if (participantCompletionParticipantService.get() == null) {
            participantCompletionParticipantService.set(PrivilegedServiceFactory.getInstance(
                    BusinessAgreementWithParticipantCompletionParticipantService.class).getService());
        }
        return participantCompletionParticipantService.get();
    }

    /**
     * fetch a coordinator completion coordinator service unique to the current thread
     * @return
     */
    private static synchronized BusinessAgreementWithCoordinatorCompletionCoordinatorService getCoordinatorCompletionCoordinatorService()
    {
        if (coordinatorCompletionCoordinatorService.get() == null) {
            coordinatorCompletionCoordinatorService.set(PrivilegedServiceFactory.getInstance(
                    BusinessAgreementWithCoordinatorCompletionCoordinatorService.class).getService());
        }
        return coordinatorCompletionCoordinatorService.get();
    }

    /**
     * fetch a coordinator completion participant service unique to the current thread
     * @return
     */
    private static synchronized BusinessAgreementWithCoordinatorCompletionParticipantService getCoordinatorCompletionParticipantService()
    {
        if (coordinatorCompletionParticipantService.get() == null) {
            coordinatorCompletionParticipantService.set(PrivilegedServiceFactory.getInstance(
                    BusinessAgreementWithCoordinatorCompletionParticipantService.class).getService());
        }
        return coordinatorCompletionParticipantService.get();
    }

    // get ports where we HAVE an endpoint to create the port from

    public static BusinessAgreementWithParticipantCompletionCoordinatorPortType getParticipantCompletionCoordinatorPort(W3CEndpointReference endpointReference,
                                                         String action,
                                                         MAP map)
    {
        final BusinessAgreementWithParticipantCompletionCoordinatorService service =
                getParticipantCompletionCoordinatorService();
        final BusinessAgreementWithParticipantCompletionCoordinatorPortType port =
                PrivilegedServiceHelper.getInstance().getPort(service, endpointReference,
                        BusinessAgreementWithParticipantCompletionCoordinatorPortType.class,
                        new AddressingFeature(true, true));
        BindingProvider bindingProvider = (BindingProvider)port;

        configureEndpointPort(bindingProvider, action, map);

        return port;
    }

    public static BusinessAgreementWithParticipantCompletionParticipantPortType getParticipantCompletionParticipantPort(W3CEndpointReference endpointReference,
                                                         String action,
                                                         MAP map)
    {
        final BusinessAgreementWithParticipantCompletionParticipantService service =
                getParticipantCompletionParticipantService();
        final BusinessAgreementWithParticipantCompletionParticipantPortType port =
                PrivilegedServiceHelper.getInstance().getPort(service, endpointReference,
                        BusinessAgreementWithParticipantCompletionParticipantPortType.class,
                        new AddressingFeature(true, true));
        BindingProvider bindingProvider = (BindingProvider)port;

        configureEndpointPort(bindingProvider, action, map);

        return port;
    }

    public static BusinessAgreementWithCoordinatorCompletionCoordinatorPortType getCoordinatorCompletionCoordinatorPort(W3CEndpointReference endpointReference,
                                                                             String action,
                                                                             MAP map)
    {
        final BusinessAgreementWithCoordinatorCompletionCoordinatorService service =
                getCoordinatorCompletionCoordinatorService();
        final BusinessAgreementWithCoordinatorCompletionCoordinatorPortType port =
                PrivilegedServiceHelper.getInstance().getPort(service, endpointReference,
                        BusinessAgreementWithCoordinatorCompletionCoordinatorPortType.class,
                        new AddressingFeature(true, true));
        BindingProvider bindingProvider = (BindingProvider)port;

        configureEndpointPort(bindingProvider, action, map);

        return port;
    }

    public static BusinessAgreementWithCoordinatorCompletionParticipantPortType getCoordinatorCompletionParticipantPort(W3CEndpointReference endpointReference,
                                                                         String action,
                                                                         MAP map)
    {
        final BusinessAgreementWithCoordinatorCompletionParticipantService service =
                getCoordinatorCompletionParticipantService();
        final BusinessAgreementWithCoordinatorCompletionParticipantPortType port =
                PrivilegedServiceHelper.getInstance().getPort(service, endpointReference,
                        BusinessAgreementWithCoordinatorCompletionParticipantPortType.class,
                        new AddressingFeature(true, true));
        BindingProvider bindingProvider = (BindingProvider)port;

        configureEndpointPort(bindingProvider, action, map);

        return port;
    }

    // get ports where we have NO endpoint to create the port from

    public static BusinessAgreementWithParticipantCompletionCoordinatorPortType
    getParticipantCompletionCoordinatorPort(String action,
                                            MAP map)
    {
        final BusinessAgreementWithParticipantCompletionCoordinatorService service =
                getParticipantCompletionCoordinatorService();
        final BusinessAgreementWithParticipantCompletionCoordinatorPortType port =
                PrivilegedServiceHelper.getInstance().getPort(service,
                        BusinessAgreementWithParticipantCompletionCoordinatorPortType.class,
                        new AddressingFeature(true, true));
        BindingProvider bindingProvider = (BindingProvider)port;

        configurePort(bindingProvider, action, map);

        return port;
    }
    
    public static BusinessAgreementWithParticipantCompletionParticipantPortType
    getParticipantCompletionParticipantPort(String action, MAP map)
    {
        final BusinessAgreementWithParticipantCompletionParticipantService service =
                getParticipantCompletionParticipantService();
        final BusinessAgreementWithParticipantCompletionParticipantPortType port =
                PrivilegedServiceHelper.getInstance().getPort(service,
                        BusinessAgreementWithParticipantCompletionParticipantPortType.class,
                        new AddressingFeature(true, true));
        BindingProvider bindingProvider = (BindingProvider)port;

        configurePort(bindingProvider, action, map);

        return port;
    }

    public static BusinessAgreementWithCoordinatorCompletionParticipantPortType
    getCoordinatorCompletionParticipantPort(String action, MAP map)
    {
        final BusinessAgreementWithCoordinatorCompletionParticipantService service =
                getCoordinatorCompletionParticipantService();
        final BusinessAgreementWithCoordinatorCompletionParticipantPortType port =
                PrivilegedServiceHelper.getInstance().getPort(service,
                        BusinessAgreementWithCoordinatorCompletionParticipantPortType.class,
                        new AddressingFeature(true, true));
        BindingProvider bindingProvider = (BindingProvider)port;

        configurePort(bindingProvider, action, map);

        return port;
    }

    public static BusinessAgreementWithCoordinatorCompletionCoordinatorPortType
    getCoordinatorCompletionCoordinatorPort(String action, MAP map)
    {
        final BusinessAgreementWithCoordinatorCompletionCoordinatorService service =
                getCoordinatorCompletionCoordinatorService();
        final BusinessAgreementWithCoordinatorCompletionCoordinatorPortType port =
                PrivilegedServiceHelper.getInstance().getPort(service,
                        BusinessAgreementWithCoordinatorCompletionCoordinatorPortType.class,
                        new AddressingFeature(true, true));
        BindingProvider bindingProvider = (BindingProvider)port;

        configurePort(bindingProvider, action, map);

        return port;

    }

    private static void configureEndpointPort(BindingProvider bindingProvider, String action, MAP map)
    {
        /*
         * we no longer have to add the JaxWS WSAddressingClientHandler because we can specify the WSAddressing feature
        List<Handler> customHandlerChain = new ArrayList<Handler>();
		customHandlerChain.add(new WSAddressingClientHandler());
		bindingProvider.getBinding().setHandlerChain(customHandlerChain);
         */
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        MAP requestMap = AddressingHelper.outboundMap(requestContext);
        map.setAction(action);
        AddressingHelper.installCallerProperties(map, requestMap);
        AddressingHelper.configureRequestContext(requestContext, requestMap.getTo(), action);
    }

    private static void configurePort(BindingProvider bindingProvider, String action, MAP map)
    {
        /*
         * we no longer have to add the JaxWS WSAddressingClientHandler because we can specify the WSAddressing feature
        List<Handler> customHandlerChain = new ArrayList<Handler>();
		customHandlerChain.add(new WSAddressingClientHandler());
		bindingProvider.getBinding().setHandlerChain(customHandlerChain);
         */
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        map.setAction(action);
        AddressingHelper.configureRequestContext(requestContext, map, map.getTo(), action);
    }
}
