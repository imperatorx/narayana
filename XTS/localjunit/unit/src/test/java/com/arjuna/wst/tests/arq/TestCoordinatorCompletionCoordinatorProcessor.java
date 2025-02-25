/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.wst.tests.arq;

import java.util.HashMap;
import java.util.Map;

import com.arjuna.webservices11.wsarj.ArjunaContext;
import com.arjuna.webservices11.wsba.CoordinatorCompletionCoordinatorInboundEvents;
import com.arjuna.webservices11.wsba.processors.CoordinatorCompletionCoordinatorProcessor;
import org.jboss.ws.api.addressing.MAP;
import com.arjuna.webservices.SoapFault;
import org.oasis_open.docs.ws_tx.wsba._2006._06.ExceptionType;
import org.oasis_open.docs.ws_tx.wsba._2006._06.NotificationType;
import org.oasis_open.docs.ws_tx.wsba._2006._06.StatusType;

public class TestCoordinatorCompletionCoordinatorProcessor extends CoordinatorCompletionCoordinatorProcessor
{
    private Map<String,CoordinatorCompletionCoordinatorDetails> messageIdMap = new HashMap<>() ;

    public CoordinatorCompletionCoordinatorDetails getCoordinatorCompletionCoordinatorDetails(final String messageId, final long timeout)
    {
        final long endTime = System.currentTimeMillis() + timeout ;
        synchronized(messageIdMap)
        {
            long now = System.currentTimeMillis() ;
            while(now < endTime)
            {
                final CoordinatorCompletionCoordinatorDetails details = (CoordinatorCompletionCoordinatorDetails)messageIdMap.remove(messageId) ;
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
            final CoordinatorCompletionCoordinatorDetails details = (CoordinatorCompletionCoordinatorDetails)messageIdMap.remove(messageId) ;
            if (details != null)
            {
                return details ;
            }
        }
        throw new NullPointerException("Timeout occurred waiting for id: " + messageId) ;
    }

    /**
     * Activate the coordinator.
     *
     * @param coordinator The coordinator.
     * @param identifier       The identifier.
     */
    public void activateCoordinator(CoordinatorCompletionCoordinatorInboundEvents coordinator, String identifier) {
    }

    /**
     * Deactivate the coordinator.
     *
     * @param coordinator The coordinator.
     */
    public void deactivateCoordinator(CoordinatorCompletionCoordinatorInboundEvents coordinator) {
    }

    public CoordinatorCompletionCoordinatorInboundEvents getCoordinator(String identifier) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Cancelled.
     *
     * @param cancelled         The cancelled notification.
     * @param map The addressing context.
     * @param arjunaContext     The arjuna context.
     */
    public void cancelled(NotificationType cancelled, MAP map, ArjunaContext arjunaContext) {
        final String messageId = map.getMessageID() ;
        final CoordinatorCompletionCoordinatorDetails details = new CoordinatorCompletionCoordinatorDetails(map, arjunaContext) ;
        details.setCancelled(true); ;

        synchronized(messageIdMap)
        {
            messageIdMap.put(messageId, details) ;
            messageIdMap.notifyAll() ;
        }
    }

    /**
     * Closed.
     *
     * @param closed            The closed notification.
     * @param map The addressing context.
     * @param arjunaContext     The arjuna context.
     */
    public void closed(NotificationType closed, MAP map, ArjunaContext arjunaContext) {
        final String messageId = map.getMessageID() ;
        final CoordinatorCompletionCoordinatorDetails details = new CoordinatorCompletionCoordinatorDetails(map, arjunaContext) ;
        details.setClosed(true); ;

        synchronized(messageIdMap)
        {
            messageIdMap.put(messageId, details) ;
            messageIdMap.notifyAll() ;
        }
    }

    /**
     * Compensated.
     *
     * @param compensated       The compensated notification.
     * @param map The addressing context.
     * @param arjunaContext     The arjuna context.
     */
    public void compensated(NotificationType compensated, MAP map, ArjunaContext arjunaContext) {
        final String messageId = map.getMessageID() ;
        final CoordinatorCompletionCoordinatorDetails details = new CoordinatorCompletionCoordinatorDetails(map, arjunaContext) ;
        details.setCompensated(true); ;

        synchronized(messageIdMap)
        {
            messageIdMap.put(messageId, details) ;
            messageIdMap.notifyAll() ;
        }
    }

    /**
     * Completed.
     *
     * @param completed         The completed notification.
     * @param map The addressing context.
     * @param arjunaContext     The arjuna context.
     */
    public void completed(NotificationType completed, MAP map, ArjunaContext arjunaContext) {
        final String messageId = map.getMessageID() ;
        final CoordinatorCompletionCoordinatorDetails details = new CoordinatorCompletionCoordinatorDetails(map, arjunaContext) ;
        details.setCompleted(true); ;

        synchronized(messageIdMap)
        {
            messageIdMap.put(messageId, details) ;
            messageIdMap.notifyAll() ;
        }
    }

    public void exit(NotificationType exit, MAP map, ArjunaContext arjunaContext)
    {
        final String messageId = map.getMessageID() ;
        final CoordinatorCompletionCoordinatorDetails details = new CoordinatorCompletionCoordinatorDetails(map, arjunaContext) ;
        details.setExit(true) ;

        synchronized(messageIdMap)
        {
            messageIdMap.put(messageId, details) ;
            messageIdMap.notifyAll() ;
        }
    }

    /**
     * Cannot complete.
     *
     * @param cannotComplete       The cannot complete notification.
     * @param map The addressing context.
     * @param arjunaContext        The arjuna context.
     */
    public void cannotComplete(NotificationType cannotComplete, MAP map, ArjunaContext arjunaContext) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Fault.
     *
     * @param fault             The fault exception.
     * @param map The addressing context.
     * @param arjunaContext     The arjuna context.
     */
    public void fail(ExceptionType fault, MAP map, ArjunaContext arjunaContext) {
        final String messageId = map.getMessageID() ;
        final CoordinatorCompletionCoordinatorDetails details = new CoordinatorCompletionCoordinatorDetails(map, arjunaContext) ;
        details.setFail(fault); ;

        synchronized(messageIdMap)
        {
            messageIdMap.put(messageId, details) ;
            messageIdMap.notifyAll() ;
        }
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void getStatus(NotificationType getStatus, MAP map, ArjunaContext arjunaContext)
    {
        final String messageId = map.getMessageID() ;
        final CoordinatorCompletionCoordinatorDetails details = new CoordinatorCompletionCoordinatorDetails(map, arjunaContext) ;
        details.setGetStatus(true) ;

        synchronized(messageIdMap)
        {
            messageIdMap.put(messageId, details) ;
            messageIdMap.notifyAll() ;
        }
    }

    /**
     * Status.
     *
     * @param status            The status.
     * @param map The addressing context.
     * @param arjunaContext     The arjuna context.
     */
    public void status(StatusType status, MAP map, ArjunaContext arjunaContext) {
        final String messageId = map.getMessageID() ;
        final CoordinatorCompletionCoordinatorDetails details = new CoordinatorCompletionCoordinatorDetails(map, arjunaContext) ;
        details.setStatus(status); ;

        synchronized(messageIdMap)
        {
            messageIdMap.put(messageId, details) ;
            messageIdMap.notifyAll() ;
        }
    }

    /**
     * SOAP fault.
     *
     * @param soapFault         The SOAP fault.
     * @param map The addressing context.
     * @param arjunaContext     The arjuna context.
     */
    public void soapFault(SoapFault soapFault, MAP map, ArjunaContext arjunaContext) {
        final String messageId = map.getMessageID() ;
        final CoordinatorCompletionCoordinatorDetails details = new CoordinatorCompletionCoordinatorDetails(map, arjunaContext) ;
        details.setSoapFault(soapFault); ;

        synchronized(messageIdMap)
        {
            messageIdMap.put(messageId, details) ;
            messageIdMap.notifyAll() ;
        }
    }

    public static class CoordinatorCompletionCoordinatorDetails
    {
        private final MAP map ;
        private final ArjunaContext arjunaContext ;
        private boolean closed ;
        private boolean cancelled ;
        private boolean compensated ;
        private ExceptionType fail;
        private boolean completed ;
        private boolean cannotComplete ;
        private StatusType status ;
        private SoapFault soapFault ;
        private boolean exit ;
        private boolean getStatus ;

        CoordinatorCompletionCoordinatorDetails(final MAP map, final ArjunaContext arjunaContext)
        {
            this.map = map ;
            this.arjunaContext = arjunaContext ;
        }

        public MAP getMAP()
        {
            return map ;
        }

        public ArjunaContext getArjunaContext()
        {
            return arjunaContext ;
        }

        public boolean hasExit()
        {
            return exit ;
        }

        void setExit(final boolean exit)
        {
            this.exit = exit ;
        }

        public boolean hasGetStatus()
        {
            return getStatus ;
        }

        void setGetStatus(final boolean getStatus)
        {
            this.getStatus = getStatus ;
        }
        public boolean hasClosed() {
            return closed;
        }

        public void setClosed(boolean closed) {
            this.closed = closed;
        }

        public boolean hasCancelled() {
            return cancelled;
        }

        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }

        public boolean hasCompensated() {
            return compensated;
        }

        public void setCompensated(boolean compensated) {
            this.compensated = compensated;
        }

        public ExceptionType hasFail() {
            return fail;
        }

        public void setFail(ExceptionType fail) {
            this.fail = fail;
        }

        public boolean hasCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        public boolean hasCAnnotComplete() {
            return cannotComplete;
        }

        public void setCannotComplete(boolean cannotComplete) {
            this.cannotComplete = cannotComplete;
        }

        public StatusType hasStatus() {
            return status;
        }

        public void setStatus(StatusType status) {
            this.status = status;
        }

        public SoapFault hasSoapFault() {
            return soapFault;
        }

        public void setSoapFault(SoapFault soapFault) {
            this.soapFault = soapFault;
        }
    }
}