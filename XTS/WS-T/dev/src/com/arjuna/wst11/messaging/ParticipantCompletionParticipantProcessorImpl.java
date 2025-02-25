/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.wst11.messaging;

import com.arjuna.webservices.SoapFault;
import com.arjuna.webservices.SoapFaultType;
import com.arjuna.webservices.base.processors.ActivatedObjectProcessor;
import com.arjuna.webservices.logging.WSTLogger;
import com.arjuna.webservices11.wsaddr.AddressingHelper;
import org.jboss.ws.api.addressing.MAP;
import com.arjuna.webservices11.wsarj.ArjunaContext;
import com.arjuna.webservices11.wsarj.InstanceIdentifier;
import com.arjuna.webservices11.wsba.ParticipantCompletionParticipantInboundEvents;
import com.arjuna.webservices11.wsba.client.ParticipantCompletionCoordinatorClient;
import com.arjuna.webservices11.wsba.processors.ParticipantCompletionParticipantProcessor;
import com.arjuna.webservices11.SoapFault11;
import com.arjuna.webservices11.wscoor.CoordinationConstants;
import com.arjuna.wsc11.messaging.MessageId;
import org.oasis_open.docs.ws_tx.wsba._2006._06.NotificationType;
import org.oasis_open.docs.ws_tx.wsba._2006._06.StatusType;
import org.jboss.jbossts.xts.recovery.participant.ba.XTSBARecoveryManager;

/**
 * The Participant Completion Participant processor.
 * @author kevin
 */
public class ParticipantCompletionParticipantProcessorImpl extends ParticipantCompletionParticipantProcessor
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
    public void activateParticipant(final ParticipantCompletionParticipantInboundEvents participant, final String identifier)
    {
        activatedObjectProcessor.activateObject(participant, identifier) ;
    }

    /**
     * Deactivate the participant.
     * @param participant The participant.
     */
    public void deactivateParticipant(final ParticipantCompletionParticipantInboundEvents participant)
    {
        activatedObjectProcessor.deactivateObject(participant) ;
    }

    /**
     * Check whether a participant with the given id is currently active
     * @param identifier The identifier.
     */
    public boolean isActive(final String identifier)
    {
        return activatedObjectProcessor.getObject(identifier) != null;
    }

    /**
     * Get the participant with the specified identifier.
     * @param instanceIdentifier The participant identifier.
     * @return The participant or null if not known.
     */
    private ParticipantCompletionParticipantInboundEvents getParticipant(final InstanceIdentifier instanceIdentifier)
    {
        final String identifier = (instanceIdentifier != null ? instanceIdentifier.getInstanceIdentifier() : null) ;
        return (ParticipantCompletionParticipantInboundEvents)activatedObjectProcessor.getObject(identifier) ;
    }

    /**
     * Cancel.
     * @param cancel The cancel notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     */
    public void cancel(final NotificationType cancel, final MAP map, final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;

        /**
         * ensure the BA participant recovery manager is running
         */

        XTSBARecoveryManager recoveryManager = XTSBARecoveryManager.getRecoveryManager();

        if (recoveryManager == null) {
            // log warning and drop this message -- it will be resent
            WSTLogger.i18NLogger.warn_wst11_messaging_ParticipantCompletionParticipantProcessorImpl_cancel_3(instanceIdentifier.toString());

            return;
        }

        final ParticipantCompletionParticipantInboundEvents participant = getParticipant(instanceIdentifier) ;

        if (participant != null)
        {
            try
            {
                participant.cancel(cancel, map, arjunaContext) ;
            }
            catch (final Throwable th)
            {
                if (WSTLogger.logger.isTraceEnabled())
                {
                    WSTLogger.logger.tracev("Unexpected exception thrown from cancel:", th) ;
                }
            }
        }
        else if (!recoveryManager.isParticipantRecoveryStarted()) {
            WSTLogger.i18NLogger.warn_wst11_messaging_ParticipantCompletionParticipantProcessorImpl_cancel_4(instanceIdentifier.toString());
        }
        else if (recoveryManager.findParticipantRecoveryRecord(instanceIdentifier.getInstanceIdentifier()) != null) {
            WSTLogger.i18NLogger.warn_wst11_messaging_ParticipantCompletionParticipantProcessorImpl_cancel_5(instanceIdentifier.toString());
        }
        else
        {
            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev("Cancel called on unknown participant: {0}", new Object[] {instanceIdentifier}) ;
            }
            sendCancelled(map, arjunaContext) ;
        }
    }

    /**
     * Close.
     * @param close The close notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     */
    public void close(final NotificationType close, final MAP map, final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;

        /**
         * ensure the BA participant recovery manager is running
         */

        XTSBARecoveryManager recoveryManager = XTSBARecoveryManager.getRecoveryManager();

        if (recoveryManager == null) {
            // log warning and drop this message -- it will be resent
            WSTLogger.i18NLogger.warn_wst11_messaging_ParticipantCompletionParticipantProcessorImpl_close_3(instanceIdentifier.toString());

            return;
        }

        final ParticipantCompletionParticipantInboundEvents participant = getParticipant(instanceIdentifier) ;

        if (participant != null)
        {
            try
            {
                participant.close(close, map, arjunaContext) ;
            }
            catch (final Throwable th)
            {
                if (WSTLogger.logger.isTraceEnabled())
                {
                    WSTLogger.logger.tracev("Unexpected exception thrown from close:", th) ;
                }
            }
        }
        else if (!recoveryManager.isParticipantRecoveryStarted()) {
            WSTLogger.i18NLogger.warn_wst11_messaging_ParticipantCompletionParticipantProcessorImpl_close_4(instanceIdentifier.toString());
        }
        else if (recoveryManager.findParticipantRecoveryRecord(instanceIdentifier.getInstanceIdentifier()) != null) {
            WSTLogger.i18NLogger.warn_wst11_messaging_ParticipantCompletionParticipantProcessorImpl_close_5(instanceIdentifier.toString());
        }
        else
        {
            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev("Close called on unknown participant: {0}", new Object[] {instanceIdentifier}) ;
            }
            sendClosed(map, arjunaContext) ;
        }
    }

    /**
     * Compensate.
     * @param compensate The compensate notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     */
    public void compensate(final NotificationType compensate, final MAP map, final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;

        /**
         * ensure the BA participant recovery manager is running
         */

        XTSBARecoveryManager recoveryManager = XTSBARecoveryManager.getRecoveryManager();

        if (recoveryManager == null) {
            // log warning and drop this message -- it will be resent
            WSTLogger.i18NLogger.warn_wst11_messaging_ParticipantCompletionParticipantProcessorImpl_compensate_3(instanceIdentifier.toString());

            return;
        }

        final ParticipantCompletionParticipantInboundEvents participant = getParticipant(instanceIdentifier) ;

        if (participant != null)
        {
            try
            {
                participant.compensate(compensate, map, arjunaContext) ;
            }
            catch (final Throwable th)
            {
                if (WSTLogger.logger.isTraceEnabled())
                {
                    WSTLogger.logger.tracev("Unexpected exception thrown from compensate:", th) ;
                }
            }
        }
        else if (!recoveryManager.isParticipantRecoveryStarted()) {
            WSTLogger.i18NLogger.warn_wst11_messaging_ParticipantCompletionParticipantProcessorImpl_compensate_4(instanceIdentifier.toString());
        }
        else if (recoveryManager.findParticipantRecoveryRecord(instanceIdentifier.getInstanceIdentifier()) != null) {
            WSTLogger.i18NLogger.warn_wst11_messaging_ParticipantCompletionParticipantProcessorImpl_compensate_5(instanceIdentifier.toString());
        }
        else
        {
            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev("Compensate called on unknown participant: {0}", new Object[] {instanceIdentifier}) ;
            }
            sendCompensated(map, arjunaContext) ;
        }
    }

    /**
     * Exited.
     * @param exited The exited notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     */
    public void exited(final NotificationType exited, final MAP map, final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
        final ParticipantCompletionParticipantInboundEvents participant = getParticipant(instanceIdentifier) ;

        if (participant != null)
        {
            try
            {
                participant.exited(exited, map, arjunaContext) ;
            }
            catch (final Throwable th)
            {
                if (WSTLogger.logger.isTraceEnabled())
                {
                    WSTLogger.logger.tracev("Unexpected exception thrown from exited:", th) ;
                }
            }
        }
        else if (WSTLogger.logger.isTraceEnabled())
        {
            WSTLogger.logger.tracev("Exited called on unknown participant: {0}", new Object[] {instanceIdentifier}) ;
        }
    }

    /**
     * Not Completed.
     * @param notCompleted The not completed notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     */
    public void notCompleted(final NotificationType notCompleted, final MAP map, final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
        final ParticipantCompletionParticipantInboundEvents participant = getParticipant(instanceIdentifier) ;

        if (participant != null)
        {
            try
            {
                participant.notCompleted(notCompleted, map, arjunaContext) ;
            }
            catch (final Throwable th)
            {
                if (WSTLogger.logger.isTraceEnabled())
                {
                    WSTLogger.logger.tracev("Unexpected exception thrown from notCompleted:", th) ;
                }
            }
        }
        else if (WSTLogger.logger.isTraceEnabled())
        {
            WSTLogger.logger.tracev("Exited called on unknown participant: {0}", new Object[] {instanceIdentifier}) ;
        }
    }

    /**
     * Failed.
     * @param failed The failed notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     */
    public void failed(final NotificationType failed, final MAP map, final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
        final ParticipantCompletionParticipantInboundEvents participant = getParticipant(instanceIdentifier) ;

        if (participant != null)
        {
            try
            {
                participant.failed(failed, map, arjunaContext) ;
            }
            catch (final Throwable th)
            {
                if (WSTLogger.logger.isTraceEnabled())
                {
                    WSTLogger.logger.tracev("Unexpected exception thrown from failed:", th) ;
                }
            }
        }
        else if (WSTLogger.logger.isTraceEnabled())
        {
            WSTLogger.logger.tracev("Failed called on unknown participant: {0}", new Object[] {instanceIdentifier}) ;
        }
    }

    /**
     * Get Status.
     * @param getStatus The get status notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     */
    public void getStatus(final NotificationType getStatus, final MAP map, final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
        final ParticipantCompletionParticipantInboundEvents participant = getParticipant(instanceIdentifier) ;

        if (participant != null)
        {
            try
            {
                participant.getStatus(getStatus, map, arjunaContext) ;
            }
            catch (final Throwable th)
            {
                if (WSTLogger.logger.isTraceEnabled())
                {
                    WSTLogger.logger.tracev("Unexpected exception thrown from getStatus:", th) ;
                }
            }
        }
        else
        {
            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev("GetStatus called on unknown participant: {0}", new Object[] {instanceIdentifier}) ;
            }
            // send an invalid state fault

            final String messageId = MessageId.getMessageId();
            final MAP faultMAP = AddressingHelper.createFaultContext(map, messageId) ;
            try
            {
                final SoapFault11 soapFault = new SoapFault11(SoapFaultType.FAULT_SENDER, CoordinationConstants.WSCOOR_ERROR_CODE_INVALID_STATE_QNAME,
                        WSTLogger.i18NLogger.get_wst11_messaging_ParticipantCompletionParticipantProcessorImpl_getStatus_4()) ;
                ParticipantCompletionCoordinatorClient.getClient().sendSoapFault(soapFault, null, faultMAP, getFaultAction());
            }
            catch (final Throwable th)
            {
                WSTLogger.i18NLogger.info_wst11_messaging_ParticipantCompletionParticipantProcessorImpl_getStatus_3(instanceIdentifier.toString(), th);
            }
        }
    }

    private static String getFaultAction()
    {
        return CoordinationConstants.WSCOOR_ACTION_FAULT;
    }
    /**
     * Status.
     * @param status The status type.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     */
    public void status(final StatusType status, final MAP map, final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
        final ParticipantCompletionParticipantInboundEvents participant = getParticipant(instanceIdentifier) ;

        if (participant != null)
        {
            try
            {
                participant.status(status, map, arjunaContext) ;
            }
            catch (final Throwable th)
            {
                if (WSTLogger.logger.isTraceEnabled())
                {
                    WSTLogger.logger.tracev("Unexpected exception thrown from status:", th) ;
                }
            }
        }
        else if (WSTLogger.logger.isTraceEnabled())
        {
            WSTLogger.logger.tracev("Status called on unknown participant: {0}", new Object[] {instanceIdentifier}) ;
        }
    }

    /**
     * SOAP Fault.
     * @param fault The SOAP fault notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     */
    public void soapFault(final SoapFault fault, final MAP map,
        final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
        final ParticipantCompletionParticipantInboundEvents participant = getParticipant(instanceIdentifier) ;

        if (participant != null)
        {
            try
            {
                participant.soapFault(fault, map, arjunaContext) ;
            }
            catch (final Throwable th)
            {
                if (WSTLogger.logger.isTraceEnabled())
                {
                    WSTLogger.logger.tracev("Unexpected exception thrown from soapFault:", th) ;
                }
            }
        }
        else if (WSTLogger.logger.isTraceEnabled())
        {
            WSTLogger.logger.tracev("SoapFault called on unknown participant: {0}", new Object[] {instanceIdentifier}) ;
        }
    }

    /**
     * Send a cancelled message.
     *
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     */
    private void sendCancelled(final MAP map, final ArjunaContext arjunaContext)
    {
        // KEV add check for recovery
        final String messageId = MessageId.getMessageId() ;
        final MAP responseMAP = AddressingHelper.createOneWayResponseContext(map, messageId) ;
        try
        {
            // supply null endpoint to indicate that addressing properties should be used to route message
            ParticipantCompletionCoordinatorClient.getClient().sendCancelled(null, responseMAP, arjunaContext.getInstanceIdentifier()) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev("Unexpected exception while sending Cancelled", th) ;
            }
        }
    }

    /**
     * Send a closed message.
     *
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     */
    private void sendClosed(final MAP map, final ArjunaContext arjunaContext)
    {
        // KEV add check for recovery
        final String messageId = MessageId.getMessageId() ;
        final MAP responseMAP = AddressingHelper.createOneWayResponseContext(map, messageId) ;
        try
        {
            // supply null endpoint to indicate that addressing properties should be used to route message
            ParticipantCompletionCoordinatorClient.getClient().sendClosed(null, responseMAP, arjunaContext.getInstanceIdentifier()) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev("Unexpected exception while sending Closed", th) ;
            }
        }
    }

    /**
     * Send a compensated message.
     *
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     */
    private void sendCompensated(final MAP map, final ArjunaContext arjunaContext)
    {
        // KEV add check for recovery
        final String messageId = MessageId.getMessageId() ;
        final MAP responseMAP = AddressingHelper.createOneWayResponseContext(map, messageId) ;
        try
        {
            // supply null endpoint to indicate that addressing properties should be used to route message
            ParticipantCompletionCoordinatorClient.getClient().sendCompensated(null, responseMAP, arjunaContext.getInstanceIdentifier()) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev("Unexpected exception while sending Compensated", th) ;
            }
        }
    }
}