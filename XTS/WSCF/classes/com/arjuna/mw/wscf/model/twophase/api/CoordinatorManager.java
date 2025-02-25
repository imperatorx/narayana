/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.mw.wscf.model.twophase.api;

import com.arjuna.mw.wscf.model.twophase.participants.*;
import com.arjuna.mw.wscf.model.twophase.exceptions.*;

import com.arjuna.mw.wscf.exceptions.*;

import com.arjuna.mw.wsas.exceptions.WrongStateException;
import com.arjuna.mw.wsas.exceptions.SystemException;
import com.arjuna.mw.wsas.exceptions.NoActivityException;

/**
 * The CoordinatorManager is the way in which services can enlist
 * participants with the current coordinator.
 *
 * @author Mark Little (mark.little@arjuna.com)
 * @version $Id: CoordinatorManager.java,v 1.3 2005/05/19 12:13:25 nmcl Exp $
 * @since 1.0.
 */

/*
 * TODO
 * 
 * Currently there is no way for users to get direct access to the
 * current coordinator instance (c.f. getting the Transaction from the JTA
 * equivalent interfaces). Do we need to add this? If so, then we should add
 * an interface for the coordinator implementations to implement, so that
 * implementation specific details don't creep into the API.
 */

public interface CoordinatorManager extends UserCoordinator
{

    /**
     * Enrol the specified participant with the coordinator associated with
     * the current thread.
     *
     * @param act The participant.
     *
     * @exception NoActivityException Thrown if there is no activity associated
     * with the current thread.
     * @exception WrongStateException Thrown if the coordinator is not in a
     * state that allows participants to be enrolled.
     * @exception DuplicateParticipantException Thrown if the participant has
     * already been enrolled and the coordination protocol does not support
     * multiple entries.
     * @exception InvalidParticipantException Thrown if the participant is invalid.
     * @exception SystemException Thrown if any other error occurs.
     */

    public void enlistParticipant (Participant act) throws NoActivityException, WrongStateException, DuplicateParticipantException, InvalidParticipantException, SystemException;

    /**
     * Remove the specified participant from the coordinator associated with
     * the current thread.
     *
     * @param act The participant to remove.
     *
     * @exception NoActivityException Thrown if there is no activity associated
     * with the current thread.
     * @exception WrongStateException Thrown if the coordinator is not in a
     * state that allows participants to be removed.
     * @exception InvalidParticipantException Thrown if the participant is invalid.
     * @exception SystemException Thrown if any other error occurs.
     */

    public void delistParticipant (Participant act) throws NoActivityException, InvalidParticipantException, WrongStateException, SystemException;

    /**
     * Enrol the specified synchronization with the coordinator associated with
     * the current thread.
     *
     * @param act The synchronization to add.
     *
     * @exception NoActivityException Thrown if there is no activity associated
     * with the current thread.
     * @exception WrongStateException Thrown if the coordinator is not in a
     * state that allows participants to be enrolled.
     * @exception DuplicateSynchronizationException Thrown if the participant has
     * already been enrolled and the coordination protocol does not support
     * multiple entries.
     * @exception InvalidSynchronizationException Thrown if the participant is invalid.
     * @exception SystemException Thrown if any other error occurs.
     */

    public void enlistSynchronization (Synchronization act) throws NoActivityException, WrongStateException, DuplicateSynchronizationException, InvalidSynchronizationException, SystemException;

    /**
     * Remove the specified synchronization from the coordinator associated
     * with the current thread.
     *
     * @param act The synchronization to remove.
     *
     * @exception NoActivityException Thrown if there is no activity associated
     * with the current thread.
     * @exception WrongStateException Thrown if the coordinator is not in a
     * state that allows participants to be removed.
     * @exception InvalidSynchronizationException Thrown if the participant is invalid.
     * @exception SystemException Thrown if any other error occurs.
     */

    public void delistSynchronization (Synchronization act) throws NoActivityException, InvalidSynchronizationException, WrongStateException, SystemException;

    /**
     * The participant has rolled back. Mark the transaction as rolled back.
     *
     * @param participantId The participant.
     *
     * @exception NoActivityException Thrown if there is no activity associated
     * with the current thread.
     * @exception WrongStateException Thrown if the coordinator is not in a
     * state that allows participants to be removed.
     * @exception InvalidParticipantException Thrown if the participant is invalid.
     * @exception SystemException Thrown if any other error occurs.
     */

    public void participantRolledback (String participantId) throws NoActivityException, InvalidParticipantException, WrongStateException, SystemException;

    /**
     * A participant is readonly. Remove it from the list.
     *
     * @param participantId The participant.
     *
     * @exception NoActivityException Thrown if there is no activity associated
     * with the current thread.
     * @exception InvalidParticipantException Thrown if the participant is invalid.
     * @exception SystemException Thrown if any other error occurs.
     */

    public void participantReadOnly (String participantId) throws NoActivityException, InvalidParticipantException, SystemException;

}