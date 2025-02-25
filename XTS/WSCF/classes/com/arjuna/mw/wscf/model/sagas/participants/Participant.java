/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.mw.wscf.model.sagas.participants;

import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputObjectState;
import com.arjuna.mw.wscf.model.sagas.exceptions.*;

import com.arjuna.mw.wscf.exceptions.*;

import com.arjuna.mw.wsas.exceptions.SystemException;
import com.arjuna.mw.wsas.exceptions.WrongStateException;

/**
 * This is the interface that all two-phase aware participants must define.
 *
 * @author Mark Little (mark.little@arjuna.com)
 * @version $Id: Participant.java,v 1.4 2005/05/19 12:13:24 nmcl Exp $
 * @since 1.0.
 */

public interface Participant
{

    /**
     * Confirm the participant at top-level.
     *
     * @exception InvalidParticipantException Thrown if the participant identity is invalid
     *            (e.g., refers to an unknown participant.)
     * @exception WrongStateException Thrown if the state of the participant is such that
     *            it cannot confirm.
     * @exception SystemException Thrown if some other error occurred.
     */

    public void close () throws InvalidParticipantException, WrongStateException, SystemException;

    /**
     * Cancel the participant at top-level.
     *
     * @exception InvalidParticipantException Thrown if the participant identity is invalid
     *            (e.g., refers to an unknown participant.)
     * @exception WrongStateException Thrown if the state of the participant is such that
     *            it cannot cancel.
     * @exception SystemException Thrown if some other error occurred.
     */

    public void cancel () throws InvalidParticipantException, WrongStateException, SystemException;

    /**
     * Compensate the participant.
     *
     * @exception InvalidParticipantException Thrown if the participant identity is invalid
     *            (e.g., refers to an unknown participant.)
     * @exception WrongStateException Thrown if the state of the participant is such that
     *            it cannot cancel.
     * @exception SystemException Thrown if some other error occurred.
     */

    public void compensate () throws CompensateFailedException, InvalidParticipantException, WrongStateException, SystemException;

    /**
     * Inform the participant that is can forget the heuristic result.
     *
     * @exception InvalidParticipantException Thrown if the participant identity is invalid.
     * @exception WrongStateException Thrown if the participant is in an invalid state.
     * @exception SystemException Thrown in the event of a general fault.
     */

    public void forget () throws InvalidParticipantException, WrongStateException, SystemException;

    public String id () throws SystemException;
    
    public boolean save_state (OutputObjectState os);
    public boolean restore_state (InputObjectState os);
    
}