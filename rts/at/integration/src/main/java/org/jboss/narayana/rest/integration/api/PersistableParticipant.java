/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package org.jboss.narayana.rest.integration.api;

/**
 * Participants implementing this interface can write their state to the byte array which is later written to the object
 * store for recovery.
 *
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
public interface PersistableParticipant {

    byte[] getRecoveryState();

}
