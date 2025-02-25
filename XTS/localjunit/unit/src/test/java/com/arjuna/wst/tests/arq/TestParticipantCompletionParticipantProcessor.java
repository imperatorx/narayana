/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.wst.tests.arq;

import java.util.HashMap;
import java.util.Map;

import com.arjuna.webservices11.wsarj.ArjunaContext;
import com.arjuna.webservices11.wsba.ParticipantCompletionParticipantInboundEvents;
import com.arjuna.webservices11.wsba.processors.ParticipantCompletionParticipantProcessor;
import org.jboss.ws.api.addressing.MAP;
import com.arjuna.webservices.SoapFault;
import org.oasis_open.docs.ws_tx.wsba._2006._06.NotificationType;
import org.oasis_open.docs.ws_tx.wsba._2006._06.StatusType;

public class TestParticipantCompletionParticipantProcessor extends ParticipantCompletionParticipantProcessor
{
    private Map<String,ParticipantCompletionParticipantDetails> messageIdMap = new HashMap<>() ;

    public ParticipantCompletionParticipantDetails getParticipantCompletionParticipantDetails(final String messageId, final long timeout)
    {
        final long endTime = System.currentTimeMillis() + timeout ;
        synchronized(messageIdMap)
        {
            long now = System.currentTimeMillis() ;
            while(now < endTime)
            {
                final ParticipantCompletionParticipantDetails details = (ParticipantCompletionParticipantDetails)messageIdMap.remove(messageId) ;
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
            final ParticipantCompletionParticipantDetails details = (ParticipantCompletionParticipantDetails)messageIdMap.remove(messageId) ;
            if (details != null)
            {
                return details ;
            }
        }
        throw new NullPointerException("Timeout occurred waiting for id: " + messageId) ;
    }

    /**
     * Activate the participant.
     *
     * @param participant The participant.
     * @param identifier  The identifier.
     */
    public void activateParticipant(ParticipantCompletionParticipantInboundEvents participant, String identifier) {
    }

    /**
     * Deactivate the participant.
     *
     * @param participant The participant.
     */
    public void deactivateParticipant(ParticipantCompletionParticipantInboundEvents participant) {
    }

    /**
     * Check whether a participant with the given id is currently active
     *
     * @param identifier The identifier.
     */
    public boolean isActive(String identifier) {
        return false;
    }

    public void cancel(NotificationType cancel, MAP map, ArjunaContext arjunaContext)
    {
        final String messageId = map.getMessageID() ;
        final ParticipantCompletionParticipantDetails details = new ParticipantCompletionParticipantDetails(map, arjunaContext) ;
        details.setCancel(true) ;

        synchronized(messageIdMap)
        {
            messageIdMap.put(messageId, details) ;
            messageIdMap.notifyAll() ;
        }
    }

    public void close(NotificationType close, MAP map, ArjunaContext arjunaContext)
    {
        final String messageId = map.getMessageID() ;
        final ParticipantCompletionParticipantDetails details = new ParticipantCompletionParticipantDetails(map, arjunaContext) ;
        details.setClose(true) ;

        synchronized(messageIdMap)
        {
            messageIdMap.put(messageId, details) ;
            messageIdMap.notifyAll() ;
        }
    }

    public void compensate(NotificationType compensate, MAP map, ArjunaContext arjunaContext)
    {
        final String messageId = map.getMessageID() ;
        final ParticipantCompletionParticipantDetails details = new ParticipantCompletionParticipantDetails(map, arjunaContext) ;
        details.setCompensate(true) ;

        synchronized(messageIdMap)
        {
            messageIdMap.put(messageId, details) ;
            messageIdMap.notifyAll() ;
        }
    }

    /**
     * Exited.
     *
     * @param exited            The exited notification.
     * @param map The addressing context.
     * @param arjunaContext     The arjuna context.
     */
    public void exited(NotificationType exited, MAP map, ArjunaContext arjunaContext) {
        final String messageId = map.getMessageID() ;
        final ParticipantCompletionParticipantDetails details = new ParticipantCompletionParticipantDetails(map, arjunaContext) ;
        details.setExited(true); ;

        synchronized(messageIdMap)
        {
            messageIdMap.put(messageId, details) ;
            messageIdMap.notifyAll() ;
        }
    }

    /**
     * Not Completed.
     *
     * @param notCompleted         The not completed notification.
     * @param map The addressing context.
     * @param arjunaContext        The arjuna context.
     */
    public void notCompleted(NotificationType notCompleted, MAP map, ArjunaContext arjunaContext) {
        final String messageId = map.getMessageID() ;
        final ParticipantCompletionParticipantDetails details = new ParticipantCompletionParticipantDetails(map, arjunaContext) ;
        details.setNotCompleted(true); ;

        synchronized(messageIdMap)
        {
            messageIdMap.put(messageId, details) ;
            messageIdMap.notifyAll() ;
        }
    }

    /**
     * Faulted.
     *
     * @param faulted           The faulted notification.
     * @param map The addressing context.
     * @param arjunaContext     The arjuna context.
     */
    public void failed(NotificationType faulted, MAP map, ArjunaContext arjunaContext) {
        final String messageId = map.getMessageID() ;
        final ParticipantCompletionParticipantDetails details = new ParticipantCompletionParticipantDetails(map, arjunaContext) ;
        details.setFaulted(true); ;

        synchronized(messageIdMap)
        {
            messageIdMap.put(messageId, details) ;
            messageIdMap.notifyAll() ;
        }
    }

    public void getStatus(NotificationType getStatus, MAP map, ArjunaContext arjunaContext)
    {
        final String messageId = map.getMessageID() ;
        final ParticipantCompletionParticipantDetails details = new ParticipantCompletionParticipantDetails(map, arjunaContext) ;
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
        final ParticipantCompletionParticipantDetails details = new ParticipantCompletionParticipantDetails(map, arjunaContext) ;
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
        final ParticipantCompletionParticipantDetails details = new ParticipantCompletionParticipantDetails(map, arjunaContext) ;
        details.setSoapFault(soapFault) ;

        synchronized(messageIdMap)
        {
            messageIdMap.put(messageId, details) ;
            messageIdMap.notifyAll() ;
        }
        details.setSoapFault(soapFault);
    }

    public static class ParticipantCompletionParticipantDetails
    {
        private final MAP map ;
        private final ArjunaContext arjunaContext ;
        private boolean cancel ;
        private boolean close ;
        private boolean compensate ;
        private boolean getStatus ;
        private boolean faulted;
        private boolean exited;
        private boolean notCompleted;
        private StatusType status;
        private SoapFault soapFault;

        ParticipantCompletionParticipantDetails(final MAP map, final ArjunaContext arjunaContext)
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

        public boolean hasCancel()
        {
            return cancel ;
        }

        void setCancel(final boolean cancel)
        {
            this.cancel = cancel ;
        }

        public boolean hasClose()
        {
            return close ;
        }

        void setClose(final boolean close)
        {
            this.close = close ;
        }

        public boolean hasCompensate()
        {
            return compensate ;
        }

        void setCompensate(final boolean compensate)
        {
            this.compensate = compensate ;
        }

        public boolean hasGetStatus()
        {
            return getStatus ;
        }

        void setGetStatus(final boolean getStatus)
        {
            this.getStatus = getStatus ;
        }

        public boolean hasFaulted() {
            return faulted;
        }

        public void setFaulted(boolean faulted) {
            this.faulted = faulted;
        }

        public boolean hasExited() {
            return exited;
        }

        public void setExited(boolean exited) {
            this.exited = exited;
        }

        public boolean hasNotCompleted() {
            return notCompleted;
        }

        public void setNotCompleted(boolean notCompleted) {
            this.notCompleted = notCompleted;
        }

        public StatusType hasStatus() {
            return status;
        }
        public void setStatus(StatusType status) {
            this.status = status;
        }

        public SoapFault getSoapFault() {
            return soapFault;
        }

        public void setSoapFault(SoapFault soapFault) {
            this.soapFault = soapFault;
        }
    }
}