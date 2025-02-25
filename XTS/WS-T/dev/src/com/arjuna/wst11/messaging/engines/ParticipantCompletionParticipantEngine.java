/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.wst11.messaging.engines;

import com.arjuna.webservices.SoapFault;
import com.arjuna.webservices.logging.WSTLogger;
import com.arjuna.webservices.util.TransportTimer;
import com.arjuna.webservices11.wsaddr.AddressingHelper;
import com.arjuna.wst11.ConfirmCompletedParticipant;
import org.jboss.ws.api.addressing.MAP;
import com.arjuna.webservices11.wsarj.ArjunaContext;
import com.arjuna.webservices11.wsarj.InstanceIdentifier;
import com.arjuna.webservices11.wsba.ParticipantCompletionParticipantInboundEvents;
import com.arjuna.webservices11.wsba.State;
import com.arjuna.webservices11.wsba.BusinessActivityConstants;
import com.arjuna.webservices11.wsba.client.ParticipantCompletionCoordinatorClient;
import com.arjuna.webservices11.wsba.processors.ParticipantCompletionParticipantProcessor;
import com.arjuna.webservices11.wscoor.CoordinationConstants;
import com.arjuna.wsc11.messaging.MessageId;
import com.arjuna.wst.BusinessAgreementWithParticipantCompletionParticipant;
import com.arjuna.wst.FaultedException;
import org.oasis_open.docs.ws_tx.wsba._2006._06.NotificationType;
import org.oasis_open.docs.ws_tx.wsba._2006._06.StatusType;
import org.jboss.jbossts.xts11.recovery.participant.ba.BAParticipantRecoveryRecord;
import org.jboss.jbossts.xts.recovery.participant.ba.XTSBARecoveryManager;

import javax.xml.namespace.QName;
import jakarta.xml.ws.wsaddressing.W3CEndpointReference;
import java.util.TimerTask;

/**
 * The participant completion participant state engine
 * @author kevin
 */
public class ParticipantCompletionParticipantEngine implements ParticipantCompletionParticipantInboundEvents
{
    /**
     * The participant id.
     */
    private final String id ;
    /**
     * The instance identifier.
     */
    private final InstanceIdentifier instanceIdentifier ;
    /**
     * The coordinator endpoint reference.
     */
    private final W3CEndpointReference coordinator ;
    /**
     * The associated participant
     */
    private final BusinessAgreementWithParticipantCompletionParticipant participant ;
    /**
     * The current state.
     */
    private State state ;
    /**
     * The associated timer task or null.
     */
    private TimerTask timerTask ;

    /**
     * the time which will elapse before the next message resend. this is incrementally increased
     * until it reaches RESEND_PERIOD_MAX
     */
    private long resendPeriod;

    /**
     * the initial period we will allow between resends.
     */
    private long initialResendPeriod;

    /**
     * the maximum period we will allow between resends. n.b. the coordinator uses the value returned
     * by getTransportTimeout as the limit for how long it waits for a response. however, we can still
     * employ a max resend period in excess of this value. if a message comes in after the coordinator
     * has given up it will catch it on the next retry.
     */
    private long maxResendPeriod;

    /**
     * the amount of time we will wait for a response to a dispatched message
     */
    private long timeout;

    /**
     * true if this participant has been recovered otherwise false
     */
    private boolean recovered;

    /**
     * true if this participant's recovery details have been logged to disk otherwise false
     */
    private boolean persisted;

    /**
     * true if the participant should send getstatus rather than resend a completed message
     */
    private boolean checkStatus;

    /**
     * Construct the initial engine for the participant.
     * @param id The participant id.
     * @param coordinator The coordinator endpoint reference.
     * @param participant The participant.
     */
    public ParticipantCompletionParticipantEngine(final String id, final W3CEndpointReference coordinator,
        final BusinessAgreementWithParticipantCompletionParticipant participant)
    {
        this(id, coordinator, participant, State.STATE_ACTIVE, false) ;
    }

    /**
     * Construct the engine for the participant in a specified state.
     * @param id The participant id.
     * @param coordinator The coordinator endpoint reference.
     * @param participant The participant.
     * @param state The initial state.
     * @param recovered true if the engine has been recovered from th elog otherwise false
     */
    public ParticipantCompletionParticipantEngine(final String id, final W3CEndpointReference coordinator,
        final BusinessAgreementWithParticipantCompletionParticipant participant, final State state, boolean recovered)
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + " constructor. Id: " + id + ", coordinator"
                    + coordinator + ", participant: " + participant + " state: " + state + ", recovered: " + recovered);
        }

        this.id = id ;
        this.instanceIdentifier = new InstanceIdentifier(id) ;
        this.coordinator = coordinator ;
        this.participant = participant ;
        this.state = state ;
        this.recovered = recovered;
        this.persisted = recovered;
        this.initialResendPeriod = TransportTimer.getTransportPeriod();
        this.maxResendPeriod = TransportTimer.getMaximumTransportPeriod();
        this.timeout = TransportTimer.getTransportTimeout();
        this.resendPeriod = initialResendPeriod;
        // we always check the status of a recovered participant and we always start off sending completed
        // if the participant is not recovered
        this.checkStatus = recovered;
    }

    /**
     * Handle the cancel event.
     * @param cancel The cancel notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * Active -&gt; Canceling
     * Canceling -&gt; Canceling
     * Completed -&gt; Completed (resend Completed)
     * Closing -&gt; Closing
     * Compensating -&gt; Compensating
     * Failing-Active -&gt; Failing-Active (resend Fail)
     * Failing-Canceling -&gt; Failing-Canceling (resend Fail)
     * Failing-Compensating -&gt; Failing-Compensating
     * NotCompleting -&gt; NotCompleting (resend CannotComplete)
     * Exiting -&gt; Exiting (resend Exit)
     * Ended -&gt; Ended (resend Cancelled)
     */
    public void cancel(final NotificationType cancel, final MAP map, final ArjunaContext arjunaContext)
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".cancel");
        }

        final State current ;
        synchronized(this)
        {                                      
            current = state ;
            if (current == State.STATE_ACTIVE)
            {
                changeState(State.STATE_CANCELING) ;
            }
        }

        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".cancel. State: " + current);
        }

        if (current == State.STATE_ACTIVE)
        {
            executeCancel() ;
        }
        else if (current == State.STATE_COMPLETED)
        {
            sendCompleted() ;
        }
        else if ((current == State.STATE_FAILING_ACTIVE) || (current == State.STATE_FAILING_CANCELING))
        {
            sendFail(current.getValue()) ;
        }
        else if (current == State.STATE_NOT_COMPLETING)
        {
            sendCannotComplete() ;
        }
        else if (current == State.STATE_EXITING)
        {
            sendExit() ;
        }
        else if (current == State.STATE_ENDED)
        {
            sendCancelled() ;
        }
    }

    /**
     * Handle the close event.
     * @param close The close notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * Active -&gt; Active (invalid state)
     * Canceling -&gt; Canceling (invalid state)
     * Completed -&gt; Closing
     * Closing -&gt; Closing
     * Compensating -&gt; Compensating (invalid state)
     * Failing-Active -&gt; Failing-Active (invalid state)
     * Failing-Canceling -&gt; Failing-Canceling (invalid state)
     * Failing-Compensating -&gt; Failing-Compensating (invalid state)
     * NotCompleting -&gt; NotCompleting (invalid state)
     * Exiting -&gt; Exiting (invalid state)
     * Ended -&gt; Ended (send Closed)
     */
    public void close(final NotificationType close, final MAP map, final ArjunaContext arjunaContext)
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".close");
        }

        final State current ;
        synchronized(this)
        {
            current = state ;
            if (current == State.STATE_COMPLETED)
            {
                changeState(State.STATE_CLOSING) ;
            }
        }

        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".close. State: " + current);
        }

        if (current == State.STATE_COMPLETED)
        {
            if (timerTask != null)
            {
                timerTask.cancel() ;
            }
            executeClose() ;
        }
        else if (current == State.STATE_ENDED)
        {
            sendClosed() ;
        }
    }

    /**
     * Handle the compensate event.
     * @param compensate The compensate notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * Active -&gt; Active (invalid state)
     * Canceling -&gt; Canceling (invalid state)
     * Completed -&gt; Compensating
     * Closing -&gt; Closing (invalid state)
     * Compensating -&gt; Compensating
     * Failing-Active -&gt; Failing-Active (invalid state)
     * Failing-Canceling -&gt; Failing-Canceling (invalid state)
     * Failing-Compensating -&gt; Failing-Compensating (resend Fail)
     * NotCompleting -&gt; NotCompleting (invalid state)
     * Exiting -&gt; Exiting (invalid state)
     * Ended -&gt; Ended (send Compensated)
     */
    public void compensate(final NotificationType compensate, final MAP map, final ArjunaContext arjunaContext)
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".compensate");
        }

        final State current ;
        synchronized(this)
        {
            current = state ;
            if (current == State.STATE_COMPLETED)
            {
                changeState(State.STATE_COMPENSATING) ;
            }
        }

        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".compensate. State: " + current);
        }

        if (current == State.STATE_COMPLETED)
        {
            if (timerTask != null)
            {
                timerTask.cancel() ;
            }
            executeCompensate() ;
        }
        else if (current == State.STATE_FAILING_COMPENSATING)
        {
            sendFail(current.getValue()) ;
        }
        else if (current == State.STATE_ENDED)
        {
            sendCompensated() ;
        }
    }

    /**
     * Handle the exited event.
     * @param exited The exited notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * Active -&gt; Active (invalid state)
     * Canceling -&gt; Canceling (invalid state)
     * Completed -&gt; Completed (invalid state)
     * Closing -&gt; Closing (invalid state)
     * Compensating -&gt; Compensating (invalid state)
     * Failing-Active -&gt; Failing-Active (invalid state)
     * Failing-Canceling -&gt; Failing-Canceling (invalid state)
     * Failing-Compensating -&gt; Failing-Compensating (invalid state)
     * NotCompleting -&gt; NotCompleting (invalid state)
     * Exiting -&gt; Ended
     * Ended -&gt; Ended
     */
    public void exited(final NotificationType exited, final MAP map, final ArjunaContext arjunaContext)
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".exited");
        }

        final State current ;
        synchronized(this)
        {
            current = state ;
            if (current == State.STATE_EXITING)
            {
                ended() ;
            }
        }

        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".exited. State: " + current);
        }
    }

    /**
     * Handle the failed event.
     * @param failed The failed notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * Active -&gt; Active (invalid state)
     * Canceling -&gt; Canceling (invalid state)
     * Completed -&gt; Completed (invalid state)
     * Closing -&gt; Closing (invalid state)
     * Compensating -&gt; Compensating (invalid state)
     * Failing-Active -&gt; Ended
     * Failing-Canceling -&gt; Ended
     * Failing-Compensating -&gt; Ended
     * NotCompleting -&gt; NotCompleting (invalid state)
     * Exiting -&gt; Exiting (invalid state)
     * Ended -&gt; Ended
     */
    public void failed(final NotificationType failed,  final MAP map, final ArjunaContext arjunaContext)
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".failed");
        }

        final State current ;
        boolean deleteRequired = false;
        synchronized(this)
        {
            current = state ;
            if ((current == State.STATE_FAILING_ACTIVE) || (current == State.STATE_FAILING_CANCELING) ||
                (current == State.STATE_FAILING_COMPENSATING))
            {
                deleteRequired = persisted;
            }
        }

        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".failed. State: " + current);
        }

        // if we just ended the participant ensure any log record gets deleted

        if (deleteRequired) {
            if (!XTSBARecoveryManager.getRecoveryManager().deleteParticipantRecoveryRecord(id)) {
                // hmm, could not delete entry -- nothing more we can do than log a message
                WSTLogger.i18NLogger.warn_wst11_messaging_engines_ParticipantCompletionParticipantEngine_failed_1(id);
            } 
        }
        // now the log record has been deleted we can safely end this participant
        if ((current == State.STATE_FAILING_ACTIVE) || (current == State.STATE_FAILING_CANCELING) ||
            (current == State.STATE_FAILING_COMPENSATING))
        {
            ended();
        }
    }

    /**
     * Handle the not completed event.
     * @param notCompleted The notCompleted notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * Active -&gt; Active (invalid state)
     * Canceling -&gt; Canceling (invalid state)
     * Completed -&gt; Completed (invalid state)
     * Closing -&gt; Closing (invalid state)
     * Compensating -&gt; Compensating (invalid state)
     * Failing-Active -&gt; Failing-Active (invalid state)
     * Failing-Canceling -&gt; Failing-Canceling (invalid state)
     * Failing-Compensating -&gt; Failing-Compensating (invalid state)
     * NotCompleting -&gt; Ended
     * Exiting -&gt; Exiting (invalid state)
     * Ended -&gt; Ended
     */
    public void notCompleted(final NotificationType notCompleted, final MAP map, final ArjunaContext arjunaContext)
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".notCompleted");
        }

        final State current ;
        synchronized(this)
        {
            current = state ;
            if (current == State.STATE_NOT_COMPLETING)
            {
        	ended() ;
            }
        }

        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".notCompleted. State: " + current);
        }
    }

    /**
     * Handle the getStatus event.
     * @param getStatus The getStatus notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     */
    public void getStatus(final NotificationType getStatus, final MAP map, final ArjunaContext arjunaContext)
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".getStatus");
        }

	final State current ;
	synchronized(this)
	{
	    current = state ;
	}

        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".getStatus. State: " + current);
        }

	sendStatus(current) ;
    }

    /**
     * Handle the status event.
     * @param status The status type.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     */
    public void status(final StatusType status, final MAP map, final ArjunaContext arjunaContext)
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".status");
        }

        // TODO --  check that the status is actually what we expect

        // revert to sending completed messages and reset the resend period to the initial period
        checkStatus = false;
        updateResendPeriod(false);
    }

    /**
     * Handle the recovery event.
     *
     * Active -&gt; Active (invalid state)
     * Canceling -&gt; Canceling (invalid state)
     * Completed -&gt; Completed (resend completed)
     * Closing -&gt; Closing (invalid state)
     * Compensating -&gt; Compensating (invalid state)
     * Failing-Active -&gt; Failing-Active (invalid state)
     * Failing-Canceling -&gt; Failing-Canceling (invalid state)
     * Failing-Compensating -&gt; Failing-Compensating (invalid state)
     * NotCompleting -&gt; NotCompleting (invalid state)
     * Exiting -&gt; Exiting (invalid state)
     * Ended -&gt; Ended (invalid state)
     */
    public void recovery()
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".recovery");
        }

        final State current ;
        synchronized(this)
        {
            current = state ;
        }

        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".recovery. State: " + current);
        }

        if (current == State.STATE_COMPLETED)
        {
            sendCompleted(true);
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
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".soapFault");
        }

        boolean deleteRequired;
        boolean checkingStatus;
        synchronized(this) {
            deleteRequired = persisted;
            // make sure delete is attempted only once
            persisted = false;
            checkingStatus = (state == State.STATE_COMPLETED && checkStatus);
            ended() ;
        }
        // TODO -- update doc in interface and user guide.
        try
        {
            boolean isInvalidState = soapFault.getSubcode().equals(CoordinationConstants.WSCOOR_ERROR_CODE_INVALID_STATE_QNAME);
            if (checkingStatus && isInvalidState) {
                // coordinator must have died before reaching close so just cancel
                WSTLogger.i18NLogger.warn_wst11_messaging_engines_ParticipantCompletionParticipantEngine_soapFault_2(id);
                participant.compensate();
            } else {
                // hmm, something went wrong -- notify the participant of the error
                WSTLogger.i18NLogger.warn_wst11_messaging_engines_ParticipantCompletionParticipantEngine_soapFault_3(id);
                participant.error();
            }
        }
        catch (final Throwable th) {} // ignore
        // if we just ended the participant ensure any log record gets deleted
        if (deleteRequired) {
            if (!XTSBARecoveryManager.getRecoveryManager().deleteParticipantRecoveryRecord(id)) {
                // hmm, could not delete entry -- nothing more we can do than log a message
                WSTLogger.i18NLogger.warn_wst11_messaging_engines_ParticipantCompletionParticipantEngine_soapFault_1(id);
            }
        }
    }

    /**
     * Handle the completed event.
     *
     * Active -&gt; Completed
     * Canceling -&gt; Canceling (invalid state)
     * Completed -&gt; Completed
     * Closing -&gt; Closing (invalid state)
     * Compensating -&gt; Compensating (invalid state)
     * Failing-Active -&gt; Failing-Active (invalid state)
     * Failing-Canceling -&gt; Failing-Canceling (invalid state)
     * Failing-Compensating -&gt; Failing-Compensating (invalid state)
     * NotCompleting -&gt; NotCompleting (invalid state)
     * Exiting -&gt; Exiting (invalid state)
     * Ended -&gt; Ended (invalid state)
     */
    public State completed()
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".completed");
        }

        State current ;
        boolean failRequired  = false;
        boolean deleteRequired  = false;
        boolean confirm = (participant instanceof ConfirmCompletedParticipant);
        synchronized(this)
        {
            current = state ;

            // we have to do this synchronized so that we don't try writing the participant details twice

            if (current == State.STATE_ACTIVE) {
                // ok we need to write the participant details to disk because it has just completed
                BAParticipantRecoveryRecord recoveryRecord = new BAParticipantRecoveryRecord(id, participant, true, coordinator);

                if (XTSBARecoveryManager.getRecoveryManager().writeParticipantRecoveryRecord(recoveryRecord)) {
                    changeState(State.STATE_COMPLETED);
                    persisted = true;
                    // if necessary notify the client now. n.b. this has to be done synchronized because
                    // if we release the lock then a resent COMPLETE may result in a COMPLETED being
                    // sent back and we cannot allow that until after the confirm
                    if (confirm) {
                        ((ConfirmCompletedParticipant) participant).confirmCompleted(true);
                    }
                } else {
                    // hmm, could not write entry log warning
                    WSTLogger.i18NLogger.warn_wst11_messaging_engines_ParticipantCompletionParticipantEngine_completed_1(id);
                    // we need to fail this transaction
                    failRequired = true;
                }
            }
        }

        // check to see if we need to send a fail or delete the log record before going ahead to complete

        if (failRequired) {
            current = fail(BusinessActivityConstants.WSBA_ELEMENT_FAIL_QNAME);
            // we can safely do this now
            if (confirm) {
                ((ConfirmCompletedParticipant) participant).confirmCompleted(false);
            }
        } else if ((current == State.STATE_ACTIVE) || (current == State.STATE_COMPLETED)) {
            sendCompleted() ;
        }

        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".completed. State: " + current);
        }

        return current ;
    }

    /**
     * Handle the exit event.
     *
     * Active -&gt; Exiting
     * Canceling -&gt; Canceling (invalid state)
     * Completed -&gt; Completed (invalid state)
     * Closing -&gt; Closing (invalid state)
     * Compensating -&gt; Compensating (invalid state)
     * Failing-Active -&gt; Failing-Active (invalid state)
     * Failing-Canceling -&gt; Failing-Canceling (invalid state)
     * Failing-Compensating -&gt; Failing-Compensating (invalid state)
     * NotCompleting -&gt; NotCompleting (invalid state)
     * Exiting -&gt; Exiting
     * Ended -&gt; Ended (invalid state)
     */
    public State exit()
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".completed");
        }

        final State current ;
        synchronized (this)
        {
            current = state ;
            if (current == State.STATE_ACTIVE)
            {
                changeState(State.STATE_EXITING) ;
            }
        }

        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".completed. State: " + current);
        }

        if ((current == State.STATE_ACTIVE) || (current == State.STATE_EXITING))
        {
            sendExit() ;
        }

        return waitForState(State.STATE_EXITING, timeout) ;
    }

    /**
     * Handle the fail event.
     *
     * Active -&gt; Failing-Active
     * Canceling -&gt; Failing-Canceling
     * Completed -&gt; Completed (invalid state)
     * Closing -&gt; Closing (invalid state)
     * Compensating -&gt; Failing-Compensating
     * Failing-Active -&gt; Failing-Active
     * Failing-Canceling -&gt; Failing-Canceling
     * Failing-Compensating -&gt; Failing-Compensating
     * NotCompleting -&gt; NotCompleting (invalid state)
     * Exiting -&gt; Exiting (invalid state)
     * Ended -&gt; Ended (invalid state)
     */
    public State fail(final QName exceptionIdentifier)
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".fail");
        }

        final State current ;
        synchronized (this)
        {
            current = state ;
            if (current == State.STATE_ACTIVE)
            {
                changeState(State.STATE_FAILING_ACTIVE) ;
            }
            else if (current == State.STATE_CANCELING)
            {
        	changeState(State.STATE_FAILING_CANCELING) ;
            }
            else if (current == State.STATE_COMPENSATING)
            {
                changeState(State.STATE_FAILING_COMPENSATING) ;
            }
        }

        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".fail. State: " + current);
        }

        if ((current == State.STATE_ACTIVE) || (current == State.STATE_FAILING_ACTIVE))
        {
            sendFail(exceptionIdentifier) ;
            return waitForState(State.STATE_FAILING_ACTIVE, timeout) ;
        }
        else if ((current == State.STATE_CANCELING) || (current == State.STATE_FAILING_CANCELING))
        {
            sendFail(exceptionIdentifier) ;
            return waitForState(State.STATE_FAILING_CANCELING, timeout) ;
        }
        else if ((current == State.STATE_COMPENSATING) || (current == State.STATE_FAILING_COMPENSATING))
        {
            sendFail(exceptionIdentifier) ;
            return waitForState(State.STATE_FAILING_COMPENSATING, timeout) ;
        }

        return current ;
    }

    /**
     * Handle the cannot complete event.
     *
     * Active -&gt; NotCompleting
     * Canceling -&gt; Canceling (invalid state)
     * Completed -&gt; Completed (invalid state)
     * Closing -&gt; Closing (invalid state)
     * Compensating -&gt; Compensating (invalid state)
     * Failing-Active -&gt; Failing-Active (invalid state)
     * Failing-Canceling -&gt; Failing-Canceling (invalid state)
     * Failing-Compensating -&gt; Failing-Compensating (invalid state)
     * NotCompleting -&gt; NotCompleting
     * Exiting -&gt; Exiting (invalid state)
     * Ended -&gt; Ended (invalid state)
     */
    public State cannotComplete()
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".cannotComplete");
        }

        final State current ;
        synchronized (this)
        {
            current = state ;
            if (current == State.STATE_ACTIVE)
            {
                changeState(State.STATE_NOT_COMPLETING) ;
            }
        }

        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".cannotComplete. State: " + current);
        }

        if ((current == State.STATE_ACTIVE) || (current == State.STATE_NOT_COMPLETING))
        {
            sendCannotComplete() ;
            return waitForState(State.STATE_NOT_COMPLETING, timeout) ;
        }
        return current ;
    }

    /**
     * Handle the comms timeout event.
     *
     * Completed -&gt; Completed (resend Completed)
     */
    private void commsTimeout(TimerTask caller)
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".commsTimeout");
        }

        final State current ;
        synchronized(this)
        {
            if (!timerTask.equals(caller)) {
                // the timer was cancelled but it went off before it could be cancelled

                return;
            }

            current = state ;
        }

        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".commsTimeout. State: " + current);
        }

        if (current == State.STATE_COMPLETED)
        {
            sendCompleted(true) ;
        }
    }

    /**
     * Send the exit message.
     *
     */
    private void sendExit()
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".sendExit. Coordinator: " + coordinator
                    + ", instance identifier: " + instanceIdentifier);
        }

        final MAP map = createContext() ;
        try
        {
            ParticipantCompletionCoordinatorClient.getClient().sendExit(coordinator, map, instanceIdentifier) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev("Unexpected exception while sending Exit", th) ;
            }
        }
    }

    /**
     * Send the completed message
     */

    private void sendCompleted()
    {
        sendCompleted(false);
    }

    /**
     * Send the completed message.
     *
     * @param timedOut true if this is in response to a comms timeout
     */
    private void sendCompleted(boolean timedOut)
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".sendCompleted. Coordinator: " + coordinator
                    + ", instance identifier: " + instanceIdentifier);
        }

        final MAP map = createContext() ;
        try
        {
            // if we are trying to reestablish the participant state then send getStatus otherwise send completed 
            if (timedOut && checkStatus) {
                ParticipantCompletionCoordinatorClient.getClient().sendGetStatus(coordinator, map, instanceIdentifier); ;
            } else {
                ParticipantCompletionCoordinatorClient.getClient().sendCompleted(coordinator, map, instanceIdentifier) ;
            }
        }
        catch (final Throwable th)
        {
            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev("Unexpected exception while sending Completed", th) ;
            }
        }

        // if we timed out the increase the resend period otherwise make sure it is reset to the
        // initial resend period

        updateResendPeriod(timedOut);

        initiateTimer() ;
    }

    private synchronized void updateResendPeriod(boolean timedOut)
    {
        // if we timed out then we multiply the resend period by ~= sqrt(2) up to the maximum
        // if not we make sure it is reset to the initial period

        if (timedOut) {
            if (resendPeriod < maxResendPeriod) {
                long newPeriod  = resendPeriod * 14 / 10;  // approximately doubles every two resends

                if (newPeriod > maxResendPeriod) {
                    newPeriod = maxResendPeriod;
                }
                resendPeriod = newPeriod;
            } else {
                // ok, we hit our maximum period last time -- this time switch to sending getStatus
                checkStatus = true;
            }
        } else {
            if (resendPeriod > initialResendPeriod) {
                resendPeriod = initialResendPeriod;
            }
            // if we were previously checking status we need to revert to sending Completed
            if (checkStatus) {
                checkStatus = false;
            }
        }
    }

    /**
     * Send the fail message.
     * @param message The fail message.
     *
     */
    private void sendFail(final QName message)
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".sendFail. Coordinator: " + coordinator
                    + ", instance identifier: " + instanceIdentifier);
        }

        final MAP map = createContext() ;
        try
        {
            ParticipantCompletionCoordinatorClient.getClient().sendFail(coordinator, map, instanceIdentifier, message) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev("Unexpected exception while sending Fault", th) ;
            }
        }
    }

    /**
     * Send the cancelled message.
     *
     */
    private void sendCancelled()
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".sendCancelled. Coordinator: " + coordinator
                    + ", instance identifier: " + instanceIdentifier);
        }

        final MAP map = createContext() ;
        try
        {
            ParticipantCompletionCoordinatorClient.getClient().sendCancelled(coordinator, map, instanceIdentifier) ;
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
     * Send the closed message.
     *
     */
    private void sendClosed()
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".sendClosed. Coordinator: " + coordinator
                    + ", instance identifier: " + instanceIdentifier);
        }

        final MAP map = createContext() ;
        try
        {
            ParticipantCompletionCoordinatorClient.getClient().sendClosed(coordinator, map, instanceIdentifier) ;
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
     * Send the compensated message.
     *
     */
    private void sendCompensated()
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".sendCompensated. Coordinator: " + coordinator
                    + ", instance identifier: " + instanceIdentifier);
        }

        final MAP map = createContext() ;
        try
        {
            ParticipantCompletionCoordinatorClient.getClient().sendCompensated(coordinator, map, instanceIdentifier) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev("Unexpected exception while sending Compensated", th) ;
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
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".sendStatus. Coordinator: " + coordinator
                    + ", instance identifier: " + instanceIdentifier);
        }

        final MAP map = createContext() ;
        try
        {
            ParticipantCompletionCoordinatorClient.getClient().sendStatus(coordinator, map, instanceIdentifier, state.getValue()) ;
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
     * Send the cannot complete message.
     *
     */
    private void sendCannotComplete()
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".sendCannotComplete. Coordinator: " + coordinator
                    + ", instance identifier: " + instanceIdentifier);
        }

        final MAP map = createContext() ;
        try
        {
            ParticipantCompletionCoordinatorClient.getClient().sendCannotComplete(coordinator, map, instanceIdentifier) ;
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
     * Get the coordinator id.
     * @return The coordinator id.
     */
    public String getId()
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".getId. Id: " + id);
        }

        return id ;
    }

    /**
     * Get the coordinator endpoint reference
     * @return The coordinator endpoint reference
     */
    public W3CEndpointReference getCoordinator()
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".getCoordinator. Coordinator: " + coordinator);
        }

        return coordinator ;
    }

    /**
     * Get the associated participant.
     * @return The associated participant.
     */
    public BusinessAgreementWithParticipantCompletionParticipant getParticipant()
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".getParticipant. Participant: " + participant);
        }

        return participant ;
    }

    /**
     * check whether this participant's details have been recovered from the log
     * @return true if the participant is recovered otherwise false
     */
    public boolean isRecovered()
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".isRecovered. Recovered: " + recovered);
        }

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
     * Execute the cancel transition.
     *
     */
    private void executeCancel()
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".executeCancel. Participant: " + participant);
        }

        try
        {
            participant.cancel() ;
        }
        catch (final FaultedException fe)
        {
            WSTLogger.i18NLogger.warn_messaging_engines_ParticipantCompletionParticipantEngine_executeCancel_1(fe);

            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev(fe, "Faulted exception from participant cancel for WS-BA participant") ;
            }

            // fail here because the participant doesn't want to retry the cancel
            fail(BusinessActivityConstants.WSBA_ELEMENT_FAIL_QNAME);
            return;
        }
        catch (final Throwable th)
        {
            WSTLogger.i18NLogger.warn_messaging_engines_ParticipantCompletionParticipantEngine_executeCancel_2(th);

            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev(th, "Unexpected exception from participant cancel for WS-BA participant") ;
            }

            /*
             * we only get here in from state ACTIVE so if we are stll in state CANCELING then roll back the
             * state allowing a retry of the cancel
             */
            synchronized (this) {
                if (state == State.STATE_CANCELING) {
                    changeState(State.STATE_ACTIVE);
                }
            }
            return ;
        }
        sendCancelled() ;
        ended() ;
    }

    /**
     * Execute the close transition.
     *
     */
    private void executeClose()
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".executeClose. Participant: " + participant);
        }

        try
        {
            participant.close() ;
        }
        catch (final Throwable th)
        {
            WSTLogger.i18NLogger.warn_messaging_engines_ParticipantCompletionParticipantEngine_executeClose_1(th);

            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev(th, "Unexpected exception from participant close for WS-BA participant") ;
            }

            // restore previous state so we can retry the close otherwise we get stuck in state closing forever
            changeState(State.STATE_COMPLETED);

            initiateTimer();
            return ;
        }
        // delete any log record for the participant
        if (persisted) {
            // if we cannot delete the participant record we effectively drop the close message
            // here in the hope that we have better luck next time..
            if (!XTSBARecoveryManager.getRecoveryManager().deleteParticipantRecoveryRecord(id)) {
                // hmm, could not delete entry -- leave it so we can maybe retry later
                WSTLogger.i18NLogger.warn_wst11_messaging_engines_ParticipantCompletionParticipantEngine_executeClose_2(id);
                // restore previous state so we can retry the close otherwise we get stuck in state closing forever

                changeState(State.STATE_COMPLETED);

                initiateTimer();

                return;
            }
        }
        sendClosed() ;
        ended() ;
    }

    /**
     * Execute the compensate transition.
     *
     */
    private void executeCompensate()
    {
        if (WSTLogger.logger.isTraceEnabled()) {
            WSTLogger.logger.trace(getClass().getSimpleName() + ".executeCompensate. Participant: " + participant);
        }

        try
        {
            participant.compensate() ;
        }
        catch (final FaultedException fe)
        {
            WSTLogger.i18NLogger.warn_messaging_engines_ParticipantCompletionParticipantEngine_executeCompensate_1(fe);

            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev(fe, "Faulted exception from participant compensate for WS-BA participant") ;
            }

            // fail here because the participant doesn't want to retry the compensate
            fail(BusinessActivityConstants.WSBA_ELEMENT_FAIL_QNAME);
            return;
        }
        catch (final Throwable th)
        {
            final State current ;
            synchronized (this)
            {
                current = state ;
                if (current == State.STATE_COMPENSATING)
                {
                    changeState(State.STATE_COMPLETED) ;
                }
            }
            if (current == State.STATE_COMPENSATING)
            {
                initiateTimer() ;
            }

            WSTLogger.i18NLogger.warn_messaging_engines_ParticipantCompletionParticipantEngine_executeCompensate_2(th);

            if (WSTLogger.logger.isTraceEnabled())
            {
                WSTLogger.logger.tracev(th, "Unexpected exception from participant compensate for WS-BA participant") ;
            }

            return ;
        }

        final State current ;
        boolean failRequired = false;
        synchronized (this)
        {
            current = state ;
            // need to do this while synchronized so no fail calls can get in on between

            if (current == State.STATE_COMPENSATING)
            {
                if (persisted) {
                    if (!XTSBARecoveryManager.getRecoveryManager().deleteParticipantRecoveryRecord(id)) {
                        // we have to fail since we don't want to run the compensate method again
                        WSTLogger.i18NLogger.warn_wst11_messaging_engines_ParticipantCompletionParticipantEngine_executeCompensate_3(id);
                        failRequired = true;
                        changeState(State.STATE_FAILING_COMPENSATING);
                    }
                }
                // if we did not fail then we can decommission the participant now avoiding any further races
                // we will send the compensate after we exit the synchronized block
                if (!failRequired) {
                    ended();
                }
            }
        }
        if (failRequired) {
            fail(BusinessActivityConstants.WSBA_ELEMENT_FAIL_QNAME);
        } else if (current == State.STATE_COMPENSATING) {
            sendCompensated() ;
        }
    }

    /**
     * End the current participant.
     */
    private void ended()
    {
	changeState(State.STATE_ENDED) ;
        ParticipantCompletionParticipantProcessor.getProcessor().deactivateParticipant(this) ;
    }

    /**
     * Initiate the timer.
     */
    private synchronized void initiateTimer()
    {
        if (timerTask != null)
        {
            timerTask.cancel() ;
        }

        if (state == State.STATE_COMPLETED)
        {
            timerTask = new TimerTask() {
                public void run() {
                    commsTimeout(this) ;
                }
            } ;
            TransportTimer.getTimer().schedule(timerTask, resendPeriod) ;
        }
        else
        {
            timerTask = null ;
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
}