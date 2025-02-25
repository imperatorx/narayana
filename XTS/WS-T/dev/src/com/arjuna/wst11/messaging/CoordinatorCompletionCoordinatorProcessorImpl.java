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
import com.arjuna.webservices11.wsba.CoordinatorCompletionCoordinatorInboundEvents;
import com.arjuna.webservices11.wsba.client.CoordinatorCompletionParticipantClient;
import com.arjuna.webservices11.wsba.processors.CoordinatorCompletionCoordinatorProcessor;
import com.arjuna.webservices11.SoapFault11;
import com.arjuna.webservices11.wscoor.CoordinationConstants;
import com.arjuna.wsc11.messaging.MessageId;
import org.oasis_open.docs.ws_tx.wsba._2006._06.ExceptionType;
import org.oasis_open.docs.ws_tx.wsba._2006._06.NotificationType;
import org.oasis_open.docs.ws_tx.wsba._2006._06.StatusType;
import org.jboss.jbossts.xts.recovery.participant.ba.XTSBARecoveryManager;

/**
 * The Coordinator Completion Coordinator processor.
 * @author kevin
 */
public class CoordinatorCompletionCoordinatorProcessorImpl extends CoordinatorCompletionCoordinatorProcessor
{
    /**
     * The activated object processor.
     */
    private final ActivatedObjectProcessor activatedObjectProcessor = new ActivatedObjectProcessor() ;

    /**
     * Activate the coordinator.
     * @param coordinator The coordinator.
     * @param identifier The identifier.
     */
    public void activateCoordinator(final CoordinatorCompletionCoordinatorInboundEvents coordinator, final String identifier)
    {
        activatedObjectProcessor.activateObject(coordinator, identifier) ;
    }

    /**
     * Deactivate the coordinator.
     * @param coordinator The coordinator.
     */
    public void deactivateCoordinator(final CoordinatorCompletionCoordinatorInboundEvents coordinator)
    {
        activatedObjectProcessor.deactivateObject(coordinator) ;
    }

    /**
     * Locate a coordinator by name.
     * @param identifier The name of the coordinator.
     */
    public CoordinatorCompletionCoordinatorInboundEvents getCoordinator(final String  identifier)
    {
        return (CoordinatorCompletionCoordinatorInboundEvents)activatedObjectProcessor.getObject(identifier);
    }

    /**
     * Get the coordinator associated with the specified identifier.
     * @param instanceIdentifier The coordinator identifier.
     * @return The coordinator or null if not known.
     */
    private CoordinatorCompletionCoordinatorInboundEvents getCoordinator(final InstanceIdentifier instanceIdentifier)
    {
        final String identifier = (instanceIdentifier != null ? instanceIdentifier.getInstanceIdentifier() : null) ;
        return (CoordinatorCompletionCoordinatorInboundEvents)activatedObjectProcessor.getObject(identifier) ;
    }

    /**
     * Cancelled.
     * @param cancelled The canceled notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     */
    public void cancelled(final NotificationType cancelled, final MAP map, final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
        final CoordinatorCompletionCoordinatorInboundEvents coordinator = getCoordinator(instanceIdentifier) ;

        if (coordinator != null)
        {
            try
            {
                coordinator.cancelled(cancelled, map, arjunaContext) ;
            }
            catch (final Throwable th)
            {
                if (WSTLogger.logger.isTraceEnabled())
                {
                    WSTLogger.logger.tracev("Unexpected exception thrown from cancelled:", th) ;
                }
            }
        }
        else if (WSTLogger.logger.isTraceEnabled())
        {
            WSTLogger.logger.tracev("Cancelled called on unknown coordinator: {0}", new Object[] {instanceIdentifier}) ;
        }
    }

    /**
     * Closed.
     * @param closed The closed notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     */
    public void closed(final NotificationType closed, final MAP map, final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
        final CoordinatorCompletionCoordinatorInboundEvents coordinator = getCoordinator(instanceIdentifier) ;

        if (coordinator != null)
        {
            try
            {
                coordinator.closed(closed, map, arjunaContext) ;
            }
            catch (final Throwable th)
            {
                if (WSTLogger.logger.isTraceEnabled())
                {
                    WSTLogger.logger.tracev("Unexpected exception thrown from closed:", th) ;
                }
            }
        }
        else if (WSTLogger.logger.isTraceEnabled())
        {
            WSTLogger.logger.tracev("Closed called on unknown coordinator: {0}", new Object[] {instanceIdentifier}) ;
        }
    }

    /**
     * Compensated.
     * @param compensated The compensated notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     */
    public void compensated(final NotificationType compensated, final MAP map, final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
        final CoordinatorCompletionCoordinatorInboundEvents coordinator = getCoordinator(instanceIdentifier) ;

        if (coordinator != null)
        {
            try
            {
                coordinator.compensated(compensated, map, arjunaContext) ;
            }
            catch (final Throwable th)
            {
                if (WSTLogger.logger.isTraceEnabled())
                {
                    WSTLogger.logger.tracev("Unexpected exception thrown from compensated:", th) ;
                }
            }
        }
        else if (WSTLogger.logger.isTraceEnabled())
        {
            WSTLogger.logger.tracev("Compensated called on unknown coordinator: {0}", new Object[] {instanceIdentifier}) ;
        }
    }

    /**
      * Fail.
      * @param fail The fail exceptionnotification.
      * @param map The addressing context.
      * @param arjunaContext The arjuna context.
      *
      */
     public void fail(final ExceptionType fail, final MAP map, final ArjunaContext arjunaContext)
     {
         final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
         final CoordinatorCompletionCoordinatorInboundEvents coordinator = getCoordinator(instanceIdentifier) ;

         if (coordinator != null)
         {
             try
             {
                 coordinator.fail(fail, map, arjunaContext) ;
             }
             catch (final Throwable th)
             {
                 if (WSTLogger.logger.isTraceEnabled())
                 {
                     WSTLogger.logger.tracev("Unexpected exception thrown from failed:", th) ;
                 }
             }
         } else if (areRecoveryLogEntriesAccountedFor()) {
             // we can respond with a failed as the participant is not pending recovery
             if (WSTLogger.logger.isTraceEnabled())
             {
                 WSTLogger.logger.tracev("Failed called on unknown coordinator: {0}", new Object[] {instanceIdentifier}) ;
             }
             sendFailed(map, arjunaContext) ;
         } else {
             // we must delay responding until we can be sure there is no participant pending recovery
             if (WSTLogger.logger.isTraceEnabled())
             {
                 WSTLogger.logger.tracev("Ignoring fail called on unidentified coordinator until recovery pass is complete: {0}", new Object[] {instanceIdentifier}) ;
             }
         }
     }
    
    /**
     * Completed.
     * @param completed The completed notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     */
    public void completed(final NotificationType completed, final MAP map,
        final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
        final CoordinatorCompletionCoordinatorInboundEvents coordinator = getCoordinator(instanceIdentifier) ;

        if (coordinator != null)
        {
            try
            {
                coordinator.completed(completed, map, arjunaContext) ;
            }
            catch (final Throwable th)
            {
                if (WSTLogger.logger.isTraceEnabled())
                {
                    WSTLogger.logger.tracev("Unexpected exception thrown from completed:", th) ;
                }
            }
        }
        else if (WSTLogger.logger.isTraceEnabled())
        {
            if (areRecoveryLogEntriesAccountedFor()) {
                // this is a resend for a lost participant
                WSTLogger.logger.tracev("Completed called on unknown coordinator: {0}", new Object[] {instanceIdentifier}) ;
            } else {
                // this may be a resend for a participant still pending recovery
                WSTLogger.logger.tracev("Ignoring completed called on unidentified coordinator until recovery pass is complete: {0}", new Object[] {instanceIdentifier}) ;
            }
        }
    }

    /**
     * Exit.
     * @param exit The exit notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     */
    public void exit(final NotificationType exit, final MAP map, final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
        final CoordinatorCompletionCoordinatorInboundEvents coordinator = getCoordinator(instanceIdentifier) ;

        if (coordinator != null)
        {
            try
            {
                coordinator.exit(exit, map, arjunaContext) ;
            }
            catch (final Throwable th)
            {
                if (WSTLogger.logger.isTraceEnabled())
                {
                    WSTLogger.logger.tracev("Unexpected exception thrown from exit:", th) ;
                }
            }
        }
        else
        {
            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev("Exit called on unknown coordinator: {0}", new Object[] {instanceIdentifier}) ;
            }
            sendExited(map, arjunaContext) ;
        }
    }

    /**
     * FaulCannot completet.
     * @param cannotComplete The cannot complete notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     */
    public void cannotComplete(final NotificationType cannotComplete, final MAP map,
        final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
        final CoordinatorCompletionCoordinatorInboundEvents coordinator = getCoordinator(instanceIdentifier) ;

        if (coordinator != null)
        {
            try
            {
                coordinator.cannotComplete(cannotComplete, map, arjunaContext); ;
            }
            catch (final Throwable th)
            {
                if (WSTLogger.logger.isTraceEnabled())
                {
                    WSTLogger.logger.tracev("Unexpected exception thrown from cannotComplete:", th) ;
                }
            }
        }
        else
        {
            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev("cannotComplete called on unknown coordinator: {0}", new Object[] {instanceIdentifier}) ;
            }
            sendNotCompleted(map, arjunaContext) ;
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
        final CoordinatorCompletionCoordinatorInboundEvents coordinator = getCoordinator(instanceIdentifier) ;

        if (coordinator != null)
        {
            try
            {
                coordinator.getStatus(getStatus, map, arjunaContext) ;
            }
            catch (final Throwable th)
            {
                if (WSTLogger.logger.isTraceEnabled())
                {
                    WSTLogger.logger.tracev("Unexpected exception thrown from getStatus:", th) ;
                }
            }
        }
        else if (!areRecoveryLogEntriesAccountedFor())
        {
            // drop the request until we have ensured that there is no recovered coordinator for this id
            
            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev("GetStatus dropped for unknown coordinator completion participant {0} while waiting on recovery scan", new Object[] {instanceIdentifier}) ;
            }
        }
        else
        {
            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev("GetStatus called on unknown coordinator: {0}", new Object[] {instanceIdentifier}) ;
            }
            // send an invalid state fault

            final String messageId = MessageId.getMessageId();
            final MAP faultMAP = AddressingHelper.createFaultContext(map, messageId) ;
            try
            {
                final SoapFault11 soapFault = new SoapFault11(SoapFaultType.FAULT_SENDER, CoordinationConstants.WSCOOR_ERROR_CODE_INVALID_STATE_QNAME,
                        WSTLogger.i18NLogger.get_wst11_messaging_CoordinatorCompletionCoordinatorProcessorImpl_getStatus_4()) ;
                CoordinatorCompletionParticipantClient.getClient().sendSoapFault(soapFault, null, faultMAP, getFaultAction());
            }
            catch (final Throwable th)
            {
                WSTLogger.i18NLogger.info_wst11_messaging_CoordinatorCompletionCoordinatorProcessorImpl_getStatus_3(instanceIdentifier.toString(), th);
            }
        }
    }

    private static String getFaultAction()
    {
        return CoordinationConstants.WSCOOR_ACTION_FAULT;
    }

    /**
     * Status.
     * @param status The status.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     */
    public void status(final StatusType status, final MAP map, final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
        final CoordinatorCompletionCoordinatorInboundEvents coordinator = getCoordinator(instanceIdentifier) ;

        if (coordinator != null)
        {
            try
            {
                coordinator.status(status, map, arjunaContext) ;
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
            WSTLogger.logger.tracev("Status called on unknown coordinator: {0}", new Object[] {instanceIdentifier}) ;
        }
    }

    /**
     * SOAP fault.
     * @param soapFault The SOAP fault.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     */
    public void soapFault(final SoapFault soapFault, final MAP map,
        final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
        final CoordinatorCompletionCoordinatorInboundEvents coordinator = getCoordinator(instanceIdentifier) ;

        if (coordinator != null)
        {
            try
            {
                coordinator.soapFault(soapFault, map, arjunaContext) ;
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
            WSTLogger.logger.tracev("SoapFault called on unknown coordinator: {0}", new Object[] {instanceIdentifier}) ;
        }
    }

    /**
     * Send an exited message.
     *
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     */
    private void sendExited(final MAP map, final ArjunaContext arjunaContext)
    {
        // KEV add check for recovery
        final String messageId = MessageId.getMessageId() ;
        final MAP responseAddressingContext = AddressingHelper.createOneWayResponseContext(map, messageId) ;

        try
        {
            // supply a null endpoint indicating that the port should be configured from the addressing properties!
            CoordinatorCompletionParticipantClient.getClient().sendExited(null, responseAddressingContext, arjunaContext.getInstanceIdentifier()) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev("Unexpected exception while sending Exited", th) ;
            }
        }
    }

    /**
     * Send a faulted message.
     *
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     */
    private void sendFailed(final MAP map, final ArjunaContext arjunaContext)
    {
        // KEV add check for recovery
        final String messageId = MessageId.getMessageId() ;
        final MAP responseAddressingContext = AddressingHelper.createOneWayResponseContext(map, messageId) ;

        try
        {
            // supply null endpoint so that addressing properties are used to deliver message
            CoordinatorCompletionParticipantClient.getClient().sendFailed(null, responseAddressingContext, arjunaContext.getInstanceIdentifier()) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev("Unexpected exception while sending Faulted", th) ;
            }
        }
    }

    /**
     * Send a not completed message.
     *
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     */
    private void sendNotCompleted(final MAP map, final ArjunaContext arjunaContext)
    {
        // KEV add check for recovery
        final String messageId = MessageId.getMessageId() ;
        final MAP responseAddressingContext = AddressingHelper.createOneWayResponseContext(map, messageId) ;

        try
        {
            // supply null endpoint so that addressing properties are used to deliver message
            CoordinatorCompletionParticipantClient.getClient().sendNotCompleted(null, responseAddressingContext, arjunaContext.getInstanceIdentifier()); ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev("Unexpected exception while sending NotCompleted", th) ;
            }
        }
    }

    /**
     * Tests if there may be unknown coordinator entries in the recovery log.
     *
     * @return false if there may be unknown coordinator entries in the recovery log.
     */

    private static boolean areRecoveryLogEntriesAccountedFor()
    {
        return (XTSBARecoveryManager.getRecoveryManager().isCoordinatorRecoveryStarted() &&
                XTSBARecoveryManager.getRecoveryManager().isSubordinateCoordinatorRecoveryStarted());
    }
}