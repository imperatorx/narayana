/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.wst11.messaging.engines;

import com.arjuna.webservices.SoapFault;
import com.arjuna.webservices.SoapFaultType;
import com.arjuna.webservices.logging.WSTLogger;
import com.arjuna.webservices.util.TransportTimer;
import com.arjuna.webservices11.wsaddr.AddressingHelper;
import org.jboss.ws.api.addressing.MAP;
import com.arjuna.webservices11.wsarj.ArjunaContext;
import com.arjuna.webservices11.wsarj.InstanceIdentifier;
import com.arjuna.webservices11.wsba.CoordinatorCompletionCoordinatorInboundEvents;
import com.arjuna.webservices11.wsba.State;
import com.arjuna.webservices11.wsba.processors.CoordinatorCompletionCoordinatorProcessor;
import com.arjuna.webservices11.wsba.client.CoordinatorCompletionParticipantClient;
import com.arjuna.webservices11.SoapFault11;
import com.arjuna.webservices11.wscoor.CoordinationConstants;
import com.arjuna.wsc11.messaging.MessageId;
import com.arjuna.wst11.BAParticipantManager;
import org.oasis_open.docs.ws_tx.wsba._2006._06.ExceptionType;
import org.oasis_open.docs.ws_tx.wsba._2006._06.NotificationType;
import org.oasis_open.docs.ws_tx.wsba._2006._06.StatusType;

import javax.xml.namespace.QName;
import jakarta.xml.ws.wsaddressing.W3CEndpointReference;

/**
 * The coordinator completion coordinator state engine
 * @author kevin
 */
public class CoordinatorCompletionCoordinatorEngine implements CoordinatorCompletionCoordinatorInboundEvents
{
    /**
     * The coordinator id.
     */
    private final String id ;
    /**
     * The instance identifier.
     */
    private final InstanceIdentifier instanceIdentifier ;
    /**
     * The participant endpoint reference.
     */
    private final W3CEndpointReference participant ;
    /**
     * The associated coordinator
     */
    private BAParticipantManager coordinator ;
    /**
     * The current state.
     */
    private State state;
    /**
     * The failure state which preceded state ended during close/cancel or null if no failure occurred.
     */
    private State failureState;
    /**
     * The flag indicating that this coordinator has been recovered from the log.
     */
    private boolean recovered ;

    /**
     * Construct the initial engine for the coordinator.
     * @param id The coordinator id.
     * @param participant The participant endpoint reference.
     */
    public CoordinatorCompletionCoordinatorEngine(final String id, final W3CEndpointReference participant)
    {
        this(id, participant, State.STATE_ACTIVE, false) ;
    }

    /**
     * Construct the engine for the coordinator in a specified state and register it.
     * @param id The coordinator id.
     * @param participant The participant endpoint reference.
     * @param state The initial state.
     */
    public CoordinatorCompletionCoordinatorEngine(final String id, final W3CEndpointReference participant,
        final State state, final boolean recovered)
    {
        this.id = id ;
        this.instanceIdentifier = new InstanceIdentifier(id) ;
        this.participant = participant ;
        this.state = state ;
        this.failureState = null;
        this.recovered = recovered;
        // unrecovered participants are always activated
        // we only need to reactivate recovered participants which were successfully COMPLETED or which began
        // CLOSING. any others will only have been saved because of a heuristic outcome. we can safely drop
        // them since we implement presumed abort.
        if (!recovered || state == State.STATE_COMPLETED || state == State.STATE_CLOSING) {
            CoordinatorCompletionCoordinatorProcessor.getProcessor().activateCoordinator(this, id) ;
        }
    }

    /**
     * Set the coordinator
     * @param coordinator
     */
    public void setCoordinator(final BAParticipantManager coordinator)
    {
        this.coordinator = coordinator ;
    }

    /**
     * Handle the cancelled event.
     * @param cancelled The cancelled notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * Active -&gt; Active (invalid state)
     * Canceling-Active -&gt; Ended
     * Canceling-Completing -&gt; Ended
     * Completing -&gt; Completing (invalid state)
     * Completed -&gt; Completed (invalid state)
     * Closing -&gt; Closing (invalid state)
     * Compensating -&gt; Compensating (invalid state)
     * Failing-Active -&gt; Failing-Active (invalid state)
     * Failing-Canceling -&gt; Failing-Canceling (invalid state)
     * Failing-Completing -&gt; Failing-Completing (invalid state)
     * Failing-Compensating -&gt; Failing-Compensating (invalid state)
     * NotCompleting -&gt; NotCompleting (invalid state)
     * Exiting -&gt; Exiting (invalid state)
     * Ended -&gt; Ended
     */
    public void cancelled(final NotificationType cancelled, final MAP map, final ArjunaContext arjunaContext)
    {
        final State current ;
        synchronized(this)
        {
            current = state ;
            if ((current == State.STATE_CANCELING) || (current == State.STATE_CANCELING_ACTIVE) ||
                (current == State.STATE_CANCELING_COMPLETING))
            {
                ended() ;
            }
        }
    }

    /**
     * Handle the closed event.
     * @param closed The closed notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * Active -&gt; Active (invalid state)
     * Canceling-Active -&gt; Canceling-Active (invalid state)
     * Canceling-Completing -&gt; Canceling-Completing (invalid state)
     * Completing -&gt; Completing (invalid state)
     * Completed -&gt; Completed (invalid state)
     * Closing -&gt; Ended
     * Compensating -&gt; Compensating (invalid state)
     * Failing-Active -&gt; Failing-Active (invalid state)
     * Failing-Canceling -&gt; Failing-Canceling (invalid state)
     * Failing-Completing -&gt; Failing-Completing (invalid state)
     * Failing-Compensating -&gt; Failing-Compensating (invalid state)
     * NotCompleting -&gt; NotCompleting (invalid state)
     * Exiting -&gt; Exiting (invalid state)
     * Ended -&gt; Ended
     */
    public void closed(final NotificationType closed, final MAP map, final ArjunaContext arjunaContext)
    {
        final State current ;
        synchronized(this)
        {
            current = state ;
            if (current == State.STATE_CLOSING)
            {
                ended() ;
            }
        }
    }

    /**
     * Handle the compensated event.
     * @param compensated The compensated notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * Active -&gt; Active (invalid state)
     * Canceling-Active -&gt; Canceling-Active (invalid state)
     * Canceling-Completing -&gt; Canceling-Completing (invalid state)
     * Completing -&gt; Completing (invalid state)
     * Completed -&gt; Completed (invalid state)
     * Closing -&gt; Closing (invalid state)
     * Compensating -&gt; Ended
     * Failing-Active -&gt; Failing-Active (invalid state)
     * Failing-Canceling -&gt; Failing-Canceling (invalid state)
     * Failing-Completing -&gt; Failing-Completing (invalid state)
     * Failing-Compensating -&gt; Failing-Compensating (invalid state)
     * NotCompleting -&gt; NotCompleting (invalid state)
     * Exiting -&gt; Exiting (invalid state)
     * Ended -&gt; Ended
     */
    public void compensated(final NotificationType compensated, final MAP map, final ArjunaContext arjunaContext)
    {
        final State current ;
        synchronized(this)
        {
            current = state ;
            if (current == State.STATE_COMPENSATING)
            {
                ended() ;
            }
        }
    }

    /**
     * Handle the completed event.
     * @param completed The completed notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * Active -&gt; Active (invalid state)
     * Canceling-Active -&gt; Canceling-Active (invalid state)
     * Canceling-Completing -&gt; Canceling-Completing (invalid state)
     * Completing -&gt; Completing (invalid state)
     * Completed -&gt; Completed (invalid state)
     * Closing -&gt; Closing (invalid state)
     * Compensating -&gt; Ended
     * Failing-Active -&gt; Failing-Active (invalid state)
     * Failing-Canceling -&gt; Failing-Canceling (invalid state)
     * Failing-Completing -&gt; Failing-Completing (invalid state)
     * Failing-Compensating -&gt; Failing-Compensating (invalid state)
     * NotCompleting -&gt; NotCompleting (invalid state)
     * Exiting -&gt; Exiting (invalid state)
     * Ended -&gt; Ended
     */
    public void completed(final NotificationType completed, final MAP map,
        final ArjunaContext arjunaContext)
    {
        final State current ;
        synchronized(this)
        {
            current = state ;
            if ((current == State.STATE_CANCELING_COMPLETING) || (current == State.STATE_COMPLETING))
            {
                changeState(State.STATE_COMPLETED) ;
            }
            else if (current == State.STATE_ACTIVE)
            {
        	ended() ;
            }
        }

        if (current == State.STATE_CLOSING)
        {
            sendClose() ;
        }
        else if (current == State.STATE_COMPENSATING)
        {
            sendCompensate() ;
        }
        else if (current == State.STATE_ACTIVE)
        {
            sendInvalidStateFault() ;
        }
    }

    /**
     * Handle the exit event.
     * @param exit The exit notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * Active -&gt; Exiting
     * Canceling -&gt; Exiting
     * Canceling-Active -&gt; Exiting
     * Canceling-Completing -&gt; Exiting
     * Completing -&gt; Exiting
     * Completed -&gt; Completed (invalid state)
     * Closing -&gt; Closing (invalid state)
     * Compensating -&gt; Compensating (invalid state)
     * Faulting -&gt; Faulting (invalid state)
     * Faulting-Active -&gt; Faulting (invalid state)
     * Faulting-Compensating -&gt; Faulting (invalid state)
     * Exiting -&gt; Exiting
     * Ended -&gt; Ended (resend Exited)
     */
    public void exit(final NotificationType exit, final MAP map, final ArjunaContext arjunaContext)
    {
        final State current ;
        synchronized(this)
        {
            current = state ;
            if ((current == State.STATE_ACTIVE) || (current == State.STATE_CANCELING_ACTIVE) ||
        	(current == State.STATE_CANCELING_COMPLETING) || (current == State.STATE_COMPLETING))
            {
                changeState(State.STATE_EXITING) ;
            }
        }

        if ((current == State.STATE_ACTIVE) || (current == State.STATE_CANCELING_ACTIVE) ||
            (current == State.STATE_CANCELING_COMPLETING) || (current == State.STATE_COMPLETING))
        {
            executeExit() ;
        }
        else if (current == State.STATE_ENDED)
        {
            sendExited() ;
        }
    }

    /**
     * Handle the fail event.
     * @param fail The fail exception.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * Active -&gt; Failing-Active
     * Canceling-Active -&gt; Failing-Canceling
     * Canceling-Completing -&gt; Failing-Canceling
     * Completing -&gt; Failing-Completing
     * Completed -&gt; Completed (invalid state)
     * Closing -&gt; Closing (invalid state)
     * Compensating -&gt; Failing-Compensating
     * Failing-Active -&gt; Failing-Active
     * Failing-Canceling -&gt; Failing-Canceling
     * Failing-Completing -&gt; Failing-Completing
     * Failing-Compensating -&gt; Failing-Compensating
     * NotCompleting -&gt; NotCompleting (invalid state)
     * Exiting -&gt; Exiting (invalid state)
     * Ended -&gt; Ended (resend Failed)
     *
     * In fact we only execute the transition to FAILING_ACTIVE and in this case we send a message to the
     * coordinator by calling executeFail. This propagates the failure back thorugh the activityy hierarchy
     * to the relevant participant and also marks the acivity as ABORT_ONLY.
     *
     * In the other failure cases we do not change to a FAILING_XXX state but instead go straight to ENDED
     * and save the failing state in a field failureState. In these cases there will be a coordinator
     * close/cancel/compensate thread waiting on the change to state FAILING_XXX. The change to FAILING_XXX
     * will wake it up and, if the state is still FAILING_XXX, return a fault to the coordinator, However,
     * the failing thread also sends a failed response and then call ended. This means the state might be
     * transitioned to ENDED before the coordinator thread is scheduled. So, we have to avoid this race by
     * going straight to ENDED and saving a failureState which the coordinator thread can check.
     *
     * The failureState also avoids another race condition for these (non-ACTIVE) cases. It means we don't have
     * to send a message to the coordinator to notify the failure. We would need to do this after the state
     * change as we need to exclude threads handling resent messages. However, the waiting coordinator thread
     * is woken by the state change and so it might complete and remove the activity before the message is sent
     * causing a NoSuchActivity exception in this thread. Settign the  failureState ensures that the failure is
     * detected cleanly by any waiting coordinator thread.
     *
     * Fortuitously, this also avoids problems during recovery. During recovery we have no link to our
     * coordinator available since there is no activity hierarchy in the current context. So, communicating
     * failures via the failureState is the only way to ensure that the recovreed coordinator sees a failure.
     * There is a further wrinkle here too. If a recovered coordinator times out waiting for a response we need
     * to leave the engine in place when we ditch the recovered coordinator and then reestablish a link to it
     * next time we recreate the coordinator. We cannot afford to miss a failure during this interval but the]
     * engine must transition to ENDED after handling the failure. Saving the failure state ensures that the
     * next time the coordinator calls cancel, compensate or close it receives a fault indicating a failure
     * rather than just detecting that the pariticpant  has ended.
     */
    public void fail(final ExceptionType fail, final MAP map,
        final ArjunaContext arjunaContext)
    {
        final State current ;
        synchronized(this)
        {
            current = state ;
            if (current == State.STATE_ACTIVE)
            {
                changeState(State.STATE_FAILING_ACTIVE) ;
            }
            else if ((current == State.STATE_CANCELING_ACTIVE) || (current == State.STATE_CANCELING_COMPLETING))
            {
                failureState = State.STATE_FAILING_CANCELING;
                ended();
            }
            else if (current == State.STATE_COMPLETING)
            {
                failureState = State.STATE_FAILING_COMPLETING;
                ended();
            }
            else if (current == State.STATE_COMPENSATING)
            {
                failureState = State.STATE_FAILING_COMPENSATING;
                ended();
            }
        }

        if (current == State.STATE_ACTIVE)
        {
            executeFail(fail.getExceptionIdentifier()) ;
        }
        else if ((current == State.STATE_CANCELING_ACTIVE) || (current == State.STATE_CANCELING_COMPLETING) ||
                (current == State.STATE_COMPLETING) || (current == State.STATE_COMPENSATING) ||
                (current == State.STATE_ENDED))
        {
            sendFailed() ;
        }
    }

    /**
     * Handle the cannot complete event.
     * @param cannotComplete The cannotComplete exception.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * Active -&gt; NotComleting
     * Canceling-Active -&gt; NotCompleting
     * Canceling-Completing -&gt; NotCompleting
     * Completing -&gt; NotCompleting
     * Completed -&gt; Completed (invalid state)
     * Closing -&gt; Closing (invalid state)
     * Compensating -&gt; Compensating (invalid state)
     * Failing-Active -&gt; Failing-Active (invalid state)
     * Failing-Canceling -&gt; Failing-Canceling (invalid state)
     * Failing-Completing -&gt; Failing-Completing (invalid state)
     * Failing-Compensating -&gt; Failing-Compensating (invalid state)
     * NotCompleting -&gt; NotCompleting
     * Exiting -&gt; Exiting (invalid state)
     * Ended -&gt; Ended (resend NotCompleted)
     */
    public void cannotComplete(final NotificationType cannotComplete, final MAP map,
        final ArjunaContext arjunaContext)
    {
        final State current ;
        synchronized(this)
        {
            current = state ;
            if ((current == State.STATE_ACTIVE) ||
                    (current == State.STATE_CANCELING_ACTIVE) ||
                    (current == State.STATE_CANCELING_COMPLETING))
            {
                changeState(State.STATE_NOT_COMPLETING) ;
            }
            else if (current == State.STATE_COMPLETING)
            {
                // ending now avoids a race condition similar to the one for a fail message
                // we set a failure state which ensures that any waiting coordinator detects
                // that the complete has failed
                failureState = State.STATE_NOT_COMPLETING;
                ended();
            }
        }

        if ((current == State.STATE_ACTIVE) ||
                (current == State.STATE_CANCELING_ACTIVE) ||
                (current == State.STATE_CANCELING_COMPLETING))
        {
            // we need to make sure the coordinator marks the participant as not completing
            executeCannotComplete() ;
        }
        else if ((current == State.STATE_COMPLETING) || (current == State.STATE_ENDED))
        {
            sendNotCompleted() ;
        }
    }

    /**
     * Handle the getStatus event.
     * @param getStatus The getStatus notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     */
    public void getStatus(final NotificationType getStatus, final MAP map, final ArjunaContext arjunaContext)
    {
        final State current ;
        synchronized(this)
        {
            current = state ;
        }
        sendStatus(current) ;
    }

    /**
     * Handle the status event.
     * @param status The status.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     */
    public void status(final StatusType status, final MAP map, final ArjunaContext arjunaContext)
    {
        // KEV - implement
    }

    /**
     * Handle the get status event.
     * @return The state.
     */
    public synchronized State getStatus()
    {
        return state ;
    }

    /**
     * Handle the cancel event.
     * @return The state.
     *
     * Active -&gt; Canceling-Active
     * Canceling-Active -&gt; Canceling-Active
     * Canceling-Completing -&gt; Canceling-Completing
     * Completing -&gt; Canceling-Completing
     * Completed -&gt; Completed (invalid state)
     * Closing -&gt; Closing (invalid state)
     * Compensating -&gt; Compensating (invalid state)
     * Failing-Active -&gt; Failing-Active (invalid state)
     * Failing-Canceling -&gt; Failing-Canceling (invalid state)
     * Failing-Completing -&gt; Failing-Completing (invalid state)
     * Failing-Compensating -&gt; Failing-Compensating (invalid state)
     * NotCompleting -&gt; NotCompleting (invalid state)
     * Exiting -&gt; Exiting (invalid state)
     * Ended -&gt; Ended (invalid state)
     */
    public State cancel()
    {
        State current ;
        synchronized(this)
        {
            current = state ;
            if (current == State.STATE_ACTIVE)
            {
                changeState(State.STATE_CANCELING_ACTIVE) ;
            }
            else if (current == State.STATE_COMPLETING)
            {
                changeState(State.STATE_CANCELING_COMPLETING) ;
            }
        }

        if (current == State.STATE_ACTIVE)
        {
            sendCancel() ;
            current = waitForState(State.STATE_CANCELING_ACTIVE, TransportTimer.getTransportTimeout()) ;
        }
        else if (current == State.STATE_COMPLETING)
        {
            sendCancel() ;
            current = waitForState(State.STATE_CANCELING_COMPLETING, TransportTimer.getTransportTimeout()) ;
        }

        // if we reached ended via a failure then make sure we return the failure state so that
        // the coordinator sees the failure

        if (current == State.STATE_ENDED && failureState != null) {
            return failureState;
        }

        return current ;
    }

    /**
     * Handle the compensate event.
     * @return The state.
     *
     * Active -&gt; Active (invalid state)
     * Canceling-Active -&gt; Canceling-Active (invalid state)
     * Canceling-Completing -&gt; Canceling-Completing (invalid state)
     * Completing -&gt; Completing (invalid state)
     * Completed -&gt; Compensating
     * Closing -&gt; Closing (invalid state)
     * Compensating -&gt; Compensating
     * Failing-Active -&gt; Failing-Active (invalid state)
     * Failing-Canceling -&gt; Failing-Canceling (invalid state)
     * Failing-Completing -&gt; Failing-Completing (invalid state)
     * Failing-Compensating -&gt; Failing-Compensating (invalid state)
     * NotCompleting -&gt; NotCompleting (invalid state)
     * Exiting -&gt; Exiting (invalid state)
     * Ended -&gt; Ended (invalid state)
     */
    public State compensate()
    {
        State current ;
        synchronized(this)
        {
            current = state ;
            if (current == State.STATE_COMPLETED)
            {
                changeState(State.STATE_COMPENSATING) ;
            }
        }

        if (current == State.STATE_COMPLETED)
        {
            sendCompensate() ;
            waitForState(State.STATE_COMPENSATING, TransportTimer.getTransportTimeout()) ;
        }

        synchronized(this) {
            if (state != State.STATE_COMPENSATING) {
                // if this is a recovered participant then ended will not have
                // deactivated the entry so that this (recovery) thread can
                // detect it and update its log entry. so we need to deactivate
                // the entry here.

                if (recovered) {
                    CoordinatorCompletionCoordinatorProcessor.getProcessor().deactivateCoordinator(this) ;
                }

                if (state == State.STATE_ENDED && failureState != null) {
                    return failureState;
                }

                return state;
            }  else {
                // timeout -- leave participant in place as this TX will get retried later
                return State.STATE_COMPENSATING;
            }
        }
    }

    /**
     * Handle the complete event.
     * @return The state.
     *
     * Active -&gt; Completing
     * Canceling-Active -&gt; Canceling-Active (invalid state)
     * Canceling-Completing -&gt; Canceling-Completing (invalid state)
     * Completing -&gt; Completing
     * Completed -&gt; Completed (invalid state)
     * Closing -&gt; Closing (invalid state)
     * Compensating -&gt; Compensating (invalid state)
     * Failing-Active -&gt; Failing-Active (invalid state)
     * Failing-Canceling -&gt; Failing-Canceling (invalid state)
     * Failing-Completing -&gt; Failing-Completing (invalid state)
     * Failing-Compensating -&gt; Failing-Compensating (invalid state)
     * NotCompleting -&gt; NotCompleting (invalid state)
     * Exiting -&gt; Exiting (invalid state)
     * Ended -&gt; Ended (invalid state)
     */
    public State complete()
    {
        final State current ;
        synchronized(this)
        {
            current = state ;
            if (current == State.STATE_ACTIVE)
            {
                changeState(State.STATE_COMPLETING) ;
            }
        }

        if (current == State.STATE_ACTIVE)
        {
            sendComplete() ;
            waitForState(State.STATE_COMPLETING, TransportTimer.getTransportTimeout()) ;
        }

        synchronized(this) {
            if (state == State.STATE_ENDED && failureState != null) {
                return failureState;
            }

            return state;
        }
    }

    /**
     * Handle the close event.
     * @return The state.
     *
     * Active -&gt; Active (invalid state)
     * Canceling-Active -&gt; Canceling-Active (invalid state)
     * Canceling-Completing -&gt; Canceling-Completing (invalid state)
     * Completing -&gt; Completing (invalid state)
     * Completed -&gt; Closing
     * Closing -&gt; Closing
     * Compensating -&gt; Compensating (invalid state)
     * Failing-Active -&gt; Failing-Active (invalid state)
     * Failing-Canceling -&gt; Failing-Canceling (invalid state)
     * Failing-Completing -&gt; Failing-Completing (invalid state)
     * Failing-Compensating -&gt; Failing-Compensating (invalid state)
     * NotCompleting -&gt; NotCompleting (invalid state)
     * Exiting -&gt; Exiting (invalid state)
     * Ended -&gt; Ended (invalid state)
     */
    public State close()
    {
        State current ;
        synchronized(this)
        {
            current = state ;
            if (current == State.STATE_COMPLETED)
            {
                changeState(State.STATE_CLOSING) ;
            }
        }

        if (current == State.STATE_COMPLETED)
        {
            sendClose() ;
            waitForState(State.STATE_CLOSING, TransportTimer.getTransportTimeout()) ;
        }

        synchronized(this) {
            if (state != State.STATE_CLOSING) {
                // if this is a recovered participant then ended will not have
                // deactivated the entry so that this (recovery) thread can
                // detect it and update its log entry. so we need to deactivate
                // the entry here.

                if (recovered) {
                    CoordinatorCompletionCoordinatorProcessor.getProcessor().deactivateCoordinator(this) ;
                }

                if (state == State.STATE_ENDED && failureState != null) {
                    return failureState;
                }

                return state;
            }  else {
                // timeout -- leave participant in place as this TX will get retried later
                return State.STATE_CLOSING;
            }
        }
    }

    /**
     * Handle the soap fault event.
     * @param soapFault The soap fault.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     */
    public void soapFault(final SoapFault soapFault, final MAP map, final ArjunaContext arjunaContext)
    {
        ended() ;
        try
        {
            if (coordinator != null) {
                coordinator.fail(soapFault.getSubcode()) ;
            }
        }
        catch (final Throwable th) {} // ignore
    }

    /**
     * Send the close message.
     *
     */
    private void sendClose()
    {
        final MAP map = createContext() ;
        try
        {
            CoordinatorCompletionParticipantClient.getClient().sendClose(participant, map, instanceIdentifier) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev("Unexpected exception while sending Close", th) ;
            }
        }
    }

    /**
     * Send the compensate message.
     *
     */
    private void sendCompensate()
    {
        final MAP map = createContext() ;
        try
        {
            CoordinatorCompletionParticipantClient.getClient().sendCompensate(participant, map, instanceIdentifier) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev("Unexpected exception while sending Compensate", th) ;
            }
        }
    }

    /**
     * Send the complete message.
     *
     */
    private void sendComplete()
    {
        final MAP map = createContext() ;
        try
        {
            CoordinatorCompletionParticipantClient.getClient().sendComplete(participant, map, instanceIdentifier) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev("Unexpected exception while sending Complete", th) ;
            }
        }
    }

    /**
     * Send the cancel message.
     *
     */
    private void sendCancel()
    {
        final MAP map = createContext() ;
        try
        {
            CoordinatorCompletionParticipantClient.getClient().sendCancel(participant, map, instanceIdentifier) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev("Unexpected exception while sending Cancel", th) ;
            }
        }
    }

    /**
     * Send the exited message.
     *
     */
    private void sendExited()
    {
        final MAP map = createContext() ;
        try
        {
            CoordinatorCompletionParticipantClient.getClient().sendExited(participant, map, instanceIdentifier) ;
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
     * Send the failed message.
     *
     */
    private void sendFailed()
    {
        final MAP map = createContext() ;
        try
        {
            CoordinatorCompletionParticipantClient.getClient().sendFailed(participant, map, instanceIdentifier) ;
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
     * Send the not completed message.
     *
     */
    private void sendNotCompleted()
    {
        final MAP map = createContext() ;
        try
        {
            CoordinatorCompletionParticipantClient.getClient().sendNotCompleted(participant, map, instanceIdentifier) ;
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
     * Send the status message.
     * @param state The state.
     *
     */
    private void sendStatus(final State state)
    {
        final MAP map = createContext() ;
        try
        {
            CoordinatorCompletionParticipantClient.getClient().sendStatus(participant, map, instanceIdentifier, state.getValue()) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev("Unexpected exception while sending Status", th) ;
            }
        }
    }

    /**
     * Send the invalid state fault message.
     *
     */
    private void sendInvalidStateFault()
    {
        final MAP map = createContext() ;
        try
        {
            final SoapFault11 soapFault = new SoapFault11(SoapFaultType.FAULT_SENDER, CoordinationConstants.WSCOOR_ERROR_CODE_INVALID_STATE_QNAME,
                    WSTLogger.i18NLogger.get_wst11_messaging_engines_CoordinatorCompletionCoordinatorEngine_sendInvalidStateFault_2()) ;
            CoordinatorCompletionParticipantClient.getClient().sendSoapFault(soapFault, participant, map, getFaultAction()) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev("Unexpected exception while sending InvalidStateFault", th) ;
            }
        }
    }

    /**
     * Get the coordinator id.
     * @return The coordinator id.
     */
    public String getId()
    {
        return id ;
    }

    /**
     * Get the participant endpoint reference
     * @return The participant endpoint reference
     */
    public W3CEndpointReference getParticipant()
    {
        return participant ;
    }

    /**
     * Get the associated coordinator.
     * @return The associated coordinator.
     */
    public BAParticipantManager getCoordinator()
    {
        return coordinator ;
    }

    /**
     * check whether this participant's details have been recovered from the log
     * @return true if the participant is recovered otherwise false
     */
    public boolean isRecovered()
    {
        return recovered;
    }
    
    /**
     * Change the state and notify any listeners.
     * @param state The new state.
     */
    private synchronized void changeState(final State state)
    {
        if (this.state != state)
        {
            this.state = state ;
            notifyAll() ;
        }
    }

    /**
     * Wait for the state to change from the specified state.
     * @param origState The original state.
     * @param delay The maximum time to wait for (in milliseconds).
     * @return The current state.
     */
    private State waitForState(final State origState, final long delay)
    {
        final long end = System.currentTimeMillis() + delay ;
        synchronized(this)
        {
            while(state == origState)
            {
                final long remaining = end - System.currentTimeMillis() ;
                if (remaining <= 0)
                {
                    break ;
                }
                try
                {
                    wait(remaining) ;
                }
                catch (final InterruptedException ie) {} // ignore
            }
            return state ;
        }
    }

    /**
     * Execute the exit transition.
     *
     */
    private void executeExit()
    {
        try
        {
            coordinator.exit() ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev("Unexpected exception from coordinator exit", th) ;
            }
            return ;
        }
        sendExited() ;
        ended() ;
    }

    /**
     * Executing the fail transition.
     *
     * @throws com.arjuna.webservices.SoapFault for SOAP errors.
     * @throws java.io.IOException for transport errors.
     *
     */
    private void executeFail(final QName exceptionIdentifier)
    {
        try
        {
            coordinator.fail(exceptionIdentifier) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev("Unexpected exception from coordinator fail", th) ;
            }
            return ;
        }
        sendFailed() ;
        ended() ;
    }

    /**
     * Executing the cannot complete transition.
     *
     * @throws com.arjuna.webservices.SoapFault for SOAP errors.
     * @throws java.io.IOException for transport errors.
     *
     */
    private void executeCannotComplete()
    {
        try
        {
            coordinator.cannotComplete() ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev("Unexpected exception from coordinator cannotComplete", th) ;
            }
            return ;
        }
        sendNotCompleted() ;
        ended() ;
    }

    /**
     * End the current coordinator.
     */
    private void ended()
    {
        changeState(State.STATE_ENDED) ;
        // participants which have not been recovered from the log can be deactivated now.

        // participants which have been recovered are left for the recovery thread to deactivate.
        // this is because the recovery thread may have timed out waiting for a response to
        // a close/cancel message and gone on to complete its scan and suspend. the next scan
        // will detect this activated participant and note that it has completed. if a crash
        // happens in between the recovery thread can safely recreate and reactivate the
        // participant and resend the commit since the commit/committed exchange is idempotent.

        if (!recovered) {
            CoordinatorCompletionCoordinatorProcessor.getProcessor().deactivateCoordinator(this) ;
        }
    }

    /**
     * Create a context for the outgoing message.
     * @return The addressing context.
     */
    private MAP createContext()
    {
        final String messageId = MessageId.getMessageId() ;

        return AddressingHelper.createNotificationContext(messageId) ;
    }

    private static String getFaultAction()
    {
        return CoordinationConstants.WSCOOR_ACTION_FAULT;
    }
}