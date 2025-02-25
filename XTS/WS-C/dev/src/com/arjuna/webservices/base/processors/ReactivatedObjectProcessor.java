/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.webservices.base.processors;

/**
 * A specialization of ActivatedObjectProcessor which allows for ghost entries to
 * be left in the table after deletion. A ghost entry cannot be retrieved by a normal
 * getObject(id) which will return null, indicating that no object with the supplied id
 * exists. However, the ghost's presence can be detected using getGhost(id).</p>
 *
 * Ghost entries are used to identify objects which have failed to be terminated due to an
 * unavailable participant or coordinator and so are still present in an unprocessed log record.
 * When recovery processing recreates a participant the recovered instance replaces the
 * ghost entry, ensuring that sbsequent messages update the participant whose recovery is
 * being driven by the coordinator.
 */

public class ReactivatedObjectProcessor extends ActivatedObjectProcessor {

    /**
     * a private object used to identify a ghost entry
     */

    static final private Object tombstone = new Object();

    /**
     * Activate the object.
     *
     * @param object     The object.
     * @param identifier The identifier.
     */
    public synchronized void activateObject(Object object, String identifier) {
        super.activateObject(object, identifier);
    }

    /**
     * Deactivate the object.
     *
     * @param object The object.
     */
    public synchronized void deactivateObject(Object object) {
        deactivateObject(object, false);
    }

    /**
     * Deactivate the object.
     *
     * @param object The object.
     */
    public synchronized void deactivateObject(Object object, boolean leaveGhost) {
        if (leaveGhost) {
            final String identifier = (String)identifierMap.get(object);
            super.deactivateObject(object);
            objectMap.put(identifier, tombstone);
        } else {
            super.deactivateObject(object);
        }
    }

    /**
     * Get the object with the specified identifier.
     *
     * @param identifier The identifier.
     * @return The participant or null if not known.
     */
    public synchronized Object getObject(String identifier) {
        final Object object = super.getObject(identifier);

        if (object == tombstone) {
            return  null;
        }

        return object;
    }

    /**
     * check if there is a ghost entry for this object
     *
     * @param identifier
     * @return true iff there is a ghost entry for this object
     */
    public synchronized boolean getGhost(String identifier)
    {
        if (reactivationProcessingStarted) {
            final Object object = super.getObject(identifier);
            return (object == tombstone);
        } else {
            // until we have been notified of at least one complete recovery scan pass we have
            // to assume that any identifier may have an entry in the log so we return true
            return true;
        }
    }

    /**
     * a global flag which is false at boot and is set to true once a recovery log scan for XTS
     * data has completed
     */
    static boolean reactivationProcessingStarted = false;

    /**
     * notify completion of a recovery log scan for XTS data
     */

    static public void setReactivationProcessingStarted()
    {
        reactivationProcessingStarted = true;
    }
}