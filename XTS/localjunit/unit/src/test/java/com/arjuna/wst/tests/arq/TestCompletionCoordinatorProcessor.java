/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.wst.tests.arq;

import java.util.HashMap;
import java.util.Map;

import com.arjuna.webservices11.wsarj.ArjunaContext;
import com.arjuna.webservices11.wsat.processors.CompletionCoordinatorProcessor;
import org.jboss.ws.api.addressing.MAP;
import com.arjuna.wst11.CompletionCoordinatorParticipant;
import org.oasis_open.docs.ws_tx.wsat._2006._06.Notification;

public class TestCompletionCoordinatorProcessor extends CompletionCoordinatorProcessor
{
    private Map<String,CompletionCoordinatorDetails> messageIdMap = new HashMap<>() ;

    public CompletionCoordinatorDetails getCompletionCoordinatorDetails(final String messageId, final long timeout)
    {
        final long endTime = System.currentTimeMillis() + timeout ;
        synchronized(messageIdMap)
        {
            long now = System.currentTimeMillis() ;
            while(now < endTime)
            {
                final CompletionCoordinatorDetails details = (CompletionCoordinatorDetails)messageIdMap.remove(messageId) ;
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
            final CompletionCoordinatorDetails details = (CompletionCoordinatorDetails)messageIdMap.remove(messageId) ;
            if (details != null)
            {
                return details ;
            }
        }
        throw new NullPointerException("Timeout occurred waiting for id: " + messageId) ;
    }

    /**
     * Commit.
     * @param commit The commit notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     */
    public void commit(final Notification commit, final MAP map,
        final ArjunaContext arjunaContext)
    {
        final String messageId = map.getMessageID();
        final CompletionCoordinatorDetails details = new CompletionCoordinatorDetails(map, arjunaContext) ;
        details.setCommit(true) ;

        synchronized(messageIdMap)
        {
            messageIdMap.put(messageId, details) ;
            messageIdMap.notifyAll() ;
        }
    }

    /**
     * Rollback.
     * @param rollback The rollback notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     */
    public void rollback(final Notification rollback, final MAP map,
        final ArjunaContext arjunaContext)
    {
        final String messageId = map.getMessageID() ;
        final CompletionCoordinatorDetails details = new CompletionCoordinatorDetails(map, arjunaContext) ;
        details.setRollback(true) ;

        synchronized(messageIdMap)
        {
            messageIdMap.put(messageId, details) ;
            messageIdMap.notifyAll() ;
        }
    }

    /**
     * Activate the participant.
     * @param participant The participant.
     * @param identifier The identifier.
     */
    public void activateParticipant(final CompletionCoordinatorParticipant participant, final String identifier)
    {
    }

    /**
     * Deactivate the participant.
     * @param participant The participant.
     */
    public void deactivateParticipant(final CompletionCoordinatorParticipant participant)
    {
    }

    public static class CompletionCoordinatorDetails
    {
        private final MAP map ;
        private final ArjunaContext arjunaContext ;
        private boolean commit ;
        private boolean rollback ;

        CompletionCoordinatorDetails(final MAP map, final ArjunaContext arjunaContext)
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

        public boolean hasCommit()
        {
            return commit ;
        }

        void setCommit(final boolean commit)
        {
            this.commit = commit ;
        }

        public boolean hasRollback()
        {
            return rollback ;
        }

        void setRollback(final boolean rollback)
        {
            this.rollback = rollback ;
        }
    }
}