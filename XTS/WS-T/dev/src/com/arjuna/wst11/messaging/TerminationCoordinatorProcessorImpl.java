/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.wst11.messaging;

import com.arjuna.schemas.ws._2005._10.wsarjtx.NotificationType;
import com.arjuna.webservices.SoapFault;
import com.arjuna.webservices.SoapFaultType;
import com.arjuna.webservices.base.processors.ActivatedObjectProcessor;
import com.arjuna.webservices.logging.WSTLogger;
import com.arjuna.webservices.wsarjtx.ArjunaTXConstants;
import com.arjuna.webservices11.SoapFault11;
import com.arjuna.webservices11.wsaddr.AddressingHelper;
import org.jboss.ws.api.addressing.MAP;
import com.arjuna.webservices11.wsarj.ArjunaContext;
import com.arjuna.webservices11.wsarj.InstanceIdentifier;
import com.arjuna.webservices11.wsarjtx.client.TerminationParticipantClient;
import com.arjuna.webservices11.wsarjtx.processors.TerminationCoordinatorProcessor;
import com.arjuna.wsc11.messaging.MessageId;
import com.arjuna.wst.FaultedException;
import com.arjuna.wst.SystemException;
import com.arjuna.wst.TransactionRolledBackException;
import com.arjuna.wst.UnknownTransactionException;
import com.arjuna.wst11.BusinessActivityTerminator;

import jakarta.xml.ws.wsaddressing.W3CEndpointReference;

/**
 * The Terminator Participant processor.
 * @author kevin
 *
 */
public class TerminationCoordinatorProcessorImpl extends TerminationCoordinatorProcessor
{
    /**
     * The activated object processor.
     */
    private final ActivatedObjectProcessor activatedObjectProcessor = new ActivatedObjectProcessor() ;

    /**
     * Activate the participant.
     * @param participant The participant.
     * @param identifier The identifier.
     */
    public void activateParticipant(final BusinessActivityTerminator participant, final String identifier)
    {
        activatedObjectProcessor.activateObject(participant, identifier) ;
    }

    /**
     * Deactivate the participant.
     * @param participant The participant.
     */
    public void deactivateParticipant(final BusinessActivityTerminator participant)
    {
        activatedObjectProcessor.deactivateObject(participant) ;
    }

   /**
     * Get the participant with the specified identifier.
     * @param instanceIdentifier The participant identifier.
     * @return The participant or null if not known.
     */
    public BusinessActivityTerminator getParticipant(final InstanceIdentifier instanceIdentifier)
    {
        final String identifier = (instanceIdentifier != null ? instanceIdentifier.getInstanceIdentifier() : null) ;
        return (com.arjuna.wst11.BusinessActivityTerminator)activatedObjectProcessor.getObject(identifier) ;
    }

    /**
     * Cancel.
     * @param cancel The cancel notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     */
    public void cancel(final NotificationType cancel, final MAP map,
        final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
        final BusinessActivityTerminator participant = getParticipant(instanceIdentifier) ;

        try
        {
            if (participant != null)
            {
                W3CEndpointReference endpoint = participant.getEndpoint();

                final String messageId = MessageId.getMessageId() ;
                try
                {
                    participant.cancel() ;
                }
                catch (final FaultedException fe)
                {
                    final MAP responseMAP = AddressingHelper.createNotificationContext(messageId) ;
                    TerminationParticipantClient.getClient().sendFaulted(endpoint, responseMAP, instanceIdentifier) ;
                }
                catch (final UnknownTransactionException ute)
                {
                    final MAP faultMAP = AddressingHelper.createFaultContext(map, messageId) ;
                    final SoapFault soapFault = new SoapFault11(SoapFaultType.FAULT_SENDER, ArjunaTXConstants.UNKNOWNTRANSACTION_ERROR_CODE_QNAME,
                            WSTLogger.i18NLogger.get_wst11_messaging_TerminationCoordinatorProcessorImpl_1()) ;
                    TerminationParticipantClient.getClient().sendSoapFault(endpoint, faultMAP, soapFault, instanceIdentifier) ;
                    return ;
                }
                catch (final SystemException se)
                {
                    final MAP faultMAP = AddressingHelper.createFaultContext(map, messageId) ;
                    final String message = WSTLogger.i18NLogger.get_wst11_messaging_TerminationCoordinatorProcessorImpl_2();
                    final SoapFault soapFault = new SoapFault11(SoapFaultType.FAULT_SENDER, ArjunaTXConstants.UNKNOWNERROR_ERROR_CODE_QNAME, message) ;
                    TerminationParticipantClient.getClient().sendSoapFault(endpoint, faultMAP, soapFault, instanceIdentifier) ;
                    return ;
                }
                catch (final Throwable th)
                {
                    if (WSTLogger.logger.isTraceEnabled())
                    {
                        WSTLogger.logger.tracev("Unexpected exception thrown from cancel:", th) ;
                    }
                    final MAP faultMAP = AddressingHelper.createFaultContext(map, messageId) ;
                    final SoapFault soapFault = new SoapFault11(th) ;
                    TerminationParticipantClient.getClient().sendSoapFault(endpoint, faultMAP, soapFault, instanceIdentifier) ;
                    return ;
                }
                final MAP responseMAP = AddressingHelper.createNotificationContext(messageId) ;
                TerminationParticipantClient.getClient().sendCancelled(endpoint, responseMAP, instanceIdentifier) ;
            }
            else
            {
                if (WSTLogger.logger.isTraceEnabled())
                {
                    WSTLogger.logger.tracev("Cancel called on unknown participant: {0}", new Object[] {instanceIdentifier}) ;
                }
                final MAP faultMAP =
                        AddressingHelper.createFaultContext(map, MessageId.getMessageId()) ;
                final SoapFault soapFault = new SoapFault11(SoapFaultType.FAULT_SENDER, ArjunaTXConstants.UNKNOWNTRANSACTION_ERROR_CODE_QNAME,
                        WSTLogger.i18NLogger.get_wst11_messaging_TerminationCoordinatorProcessorImpl_5()) ;
                 TerminationParticipantClient.getClient().sendSoapFault(soapFault, faultMAP, instanceIdentifier) ;
            }
        }
        catch (Throwable throwable)
        {
            throwable.printStackTrace(System.err);
        }
    }

    /**
     * Close.
     * @param close The close notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     */
    public void close(final NotificationType close, final MAP map,
        final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
        final BusinessActivityTerminator participant = getParticipant(instanceIdentifier) ;

        try
        {
            if (participant != null)
            {
                W3CEndpointReference endpoint = participant.getEndpoint();

                final String messageId = MessageId.getMessageId() ;
                try
                {
                    participant.close() ;
                }
                catch (final UnknownTransactionException ute)
                {
                    final MAP faultMAP = AddressingHelper.createFaultContext(map, messageId) ;
                    final SoapFault soapFault = new SoapFault11(SoapFaultType.FAULT_SENDER, ArjunaTXConstants.UNKNOWNTRANSACTION_ERROR_CODE_QNAME,
                            WSTLogger.i18NLogger.get_wst11_messaging_TerminationCoordinatorProcessorImpl_6()) ;
                    TerminationParticipantClient.getClient().sendSoapFault(endpoint, faultMAP, soapFault, instanceIdentifier) ;
                    return ;
                }
                catch (final TransactionRolledBackException trbe)
                {
                    final MAP faultMAP = AddressingHelper.createFaultContext(map, messageId) ;
                    final SoapFault soapFault = new SoapFault11(SoapFaultType.FAULT_SENDER, ArjunaTXConstants.TRANSACTIONROLLEDBACK_ERROR_CODE_QNAME,
                            WSTLogger.i18NLogger.get_wst11_messaging_TerminationCoordinatorProcessorImpl_7()) ;
                    TerminationParticipantClient.getClient().sendSoapFault(endpoint, faultMAP, soapFault, instanceIdentifier) ;
                    return ;
                }
                catch (final SystemException se)
                {
                    final MAP faultMAP = AddressingHelper.createFaultContext(map, messageId) ;
                    final String message = WSTLogger.i18NLogger.get_wst11_messaging_TerminationCoordinatorProcessorImpl_8();
                    final SoapFault soapFault = new SoapFault11(SoapFaultType.FAULT_SENDER, ArjunaTXConstants.UNKNOWNERROR_ERROR_CODE_QNAME, message) ;
                    TerminationParticipantClient.getClient().sendSoapFault(endpoint, faultMAP, soapFault, instanceIdentifier) ;
                    return ;
                }
                catch (final Throwable th)
                {
                    if (WSTLogger.logger.isTraceEnabled())
                    {
                        WSTLogger.logger.tracev("Unexpected exception thrown from close:", th) ;
                    }
                    final MAP faultMAP = AddressingHelper.createFaultContext(map, MessageId.getMessageId()) ;
                    final SoapFault soapFault = new SoapFault11(th) ;
                    TerminationParticipantClient.getClient().sendSoapFault(endpoint, faultMAP, soapFault, instanceIdentifier) ;
                    return ;
                }
                final MAP responseMAP = AddressingHelper.createNotificationContext(messageId) ;
                TerminationParticipantClient.getClient().sendClosed(endpoint, responseMAP, instanceIdentifier) ;
            }
            else
            {
                if (WSTLogger.logger.isTraceEnabled())
                {
                    WSTLogger.logger.tracev("Close called on unknown participant: {0}", new Object[] {instanceIdentifier}) ;
                }
                final MAP faultMAP =
                        AddressingHelper.createFaultContext(map, MessageId.getMessageId()) ;
                final SoapFault soapFault = new SoapFault11(SoapFaultType.FAULT_SENDER, ArjunaTXConstants.UNKNOWNTRANSACTION_ERROR_CODE_QNAME,
                        WSTLogger.i18NLogger.get_wst11_messaging_TerminationCoordinatorProcessorImpl_11()) ;
                TerminationParticipantClient.getClient().sendSoapFault(soapFault, faultMAP, instanceIdentifier) ;
            }
        }
        catch (Throwable throwable)
        {
            throwable.printStackTrace(System.err);
        }
    }

    /**
     * Complete.
     * @param complete The complete notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     */
    public void complete(final NotificationType complete, final MAP map,
        final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
        final BusinessActivityTerminator participant = getParticipant(instanceIdentifier) ;

        try
        {
            if (participant != null)
            {
                W3CEndpointReference endpoint = participant.getEndpoint();

                final String messageId = MessageId.getMessageId() ;
                try
                {
                    participant.complete() ;
                }
                catch (final FaultedException fe)
                {
                    final MAP responseMAP = AddressingHelper.createNotificationContext(messageId) ;
                    TerminationParticipantClient.getClient().sendFaulted(endpoint, responseMAP, instanceIdentifier) ;
                }
                catch (final UnknownTransactionException ute)
                {
                    final MAP faultMAP = AddressingHelper.createFaultContext(map, messageId) ;
                    final SoapFault soapFault = new SoapFault11(SoapFaultType.FAULT_SENDER, ArjunaTXConstants.UNKNOWNTRANSACTION_ERROR_CODE_QNAME,
                            WSTLogger.i18NLogger.get_wst11_messaging_TerminationCoordinatorProcessorImpl_12()) ;
                    TerminationParticipantClient.getClient().sendSoapFault(endpoint, faultMAP, soapFault, instanceIdentifier) ;
                    return ;
                }
                catch (final SystemException se)
                {
                    final MAP faultMAP = AddressingHelper.createFaultContext(map, messageId) ;
                    final String message = WSTLogger.i18NLogger.get_wst11_messaging_TerminationCoordinatorProcessorImpl_13();
                    final SoapFault soapFault = new SoapFault11(SoapFaultType.FAULT_SENDER, ArjunaTXConstants.UNKNOWNERROR_ERROR_CODE_QNAME, message) ;
                    TerminationParticipantClient.getClient().sendSoapFault(endpoint, faultMAP, soapFault, instanceIdentifier) ;
                    return ;
                }
                catch (final Throwable th)
                {
                    if (WSTLogger.logger.isTraceEnabled())
                    {
                        WSTLogger.logger.tracev("Unexpected exception thrown from complete:", th) ;
                    }
                    final MAP faultMAP = AddressingHelper.createFaultContext(map, messageId) ;
                    final SoapFault soapFault = new SoapFault11(th) ;
                    TerminationParticipantClient.getClient().sendSoapFault(endpoint, faultMAP, soapFault, instanceIdentifier) ;
                    return ;
                }
                final MAP responseMAP = AddressingHelper.createNotificationContext(messageId) ;
                TerminationParticipantClient.getClient().sendCompleted(endpoint, responseMAP, instanceIdentifier) ;
            }
            else
            {
                if (WSTLogger.logger.isTraceEnabled())
                {
                    WSTLogger.logger.tracev("Complete called on unknown participant: {0}", new Object[] {instanceIdentifier}) ;
                }
                final MAP faultMAP =
                        AddressingHelper.createFaultContext(map, MessageId.getMessageId()) ;
                final SoapFault soapFault =
                        new SoapFault11(SoapFaultType.FAULT_SENDER, ArjunaTXConstants.UNKNOWNTRANSACTION_ERROR_CODE_QNAME,
                                WSTLogger.i18NLogger.get_wst11_messaging_TerminationCoordinatorProcessorImpl_16()) ;
                TerminationParticipantClient.getClient().sendSoapFault(soapFault, faultMAP, instanceIdentifier) ;
            }
        }
        catch (Throwable throwable)
        {
            throwable.printStackTrace(System.err);
        }
    }

    /**
     * handle a soap fault sent by the participant.
     *
     * kev's code just prints a log message?
     *
     * @param soapFault The soap fault
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     */
    public void soapFault(final SoapFault soapFault, final MAP map,
        final ArjunaContext arjunaContext)
    {
        // in this case all we do is log a message

        if (WSTLogger.logger.isTraceEnabled())
        {
            WSTLogger.logger.tracev("Service {0} received unexpected fault: {1}",
                    new Object[] {ArjunaTXConstants.SERVICE_TERMINATION_COORDINATOR, soapFault}) ;
        }
    }
}