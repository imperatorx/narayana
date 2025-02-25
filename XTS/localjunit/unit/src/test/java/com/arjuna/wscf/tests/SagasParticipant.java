/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.wscf.tests;

import java.io.IOException;

import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputObjectState;
import com.arjuna.mw.wsas.exceptions.SystemException;
import com.arjuna.mw.wsas.exceptions.WrongStateException;
import com.arjuna.mw.wscf.exceptions.InvalidParticipantException;
import com.arjuna.mw.wscf.model.sagas.exceptions.CompensateFailedException;
import com.arjuna.mw.wscf.model.sagas.participants.ParticipantWithComplete;

/**
 * @author Mark Little (mark.little@arjuna.com)
 * @version $Id: TwoPhaseParticipant.java,v 1.3 2005/01/15 21:21:06 kconner Exp $
 * @since 1.0.
 */

public class SagasParticipant implements ParticipantWithComplete
{
    public SagasParticipant(String id)
    {
	_id = id;
    }

    public void close() throws InvalidParticipantException, WrongStateException, SystemException {
        System.out.println("SagasParticipant.close");
    }

    public void cancel () throws InvalidParticipantException, InvalidParticipantException, WrongStateException, SystemException
    {
        System.out.println("SagasParticipant.cancel");
    }

    public void compensate() throws CompensateFailedException, InvalidParticipantException, WrongStateException, SystemException
    {
        System.out.println("SagasParticipant.compensate");
    }

    public void forget() throws InvalidParticipantException, WrongStateException, SystemException {
        System.out.println("SagasParticipant.forget");
    }

    public void complete() throws InvalidParticipantException, WrongStateException, SystemException {
        System.out.println("SagasParticipant.complete");
    }
    public String id () throws SystemException
    {
	return _id;
    }

    public boolean save_state(OutputObjectState os)
    {
        try {
            os.packString(_id);
        } catch (IOException ioe) {
            return false;
        }
        return true ;
    }

    public boolean restore_state(InputObjectState os)
    {
        try {
            _id = os.unpackString();
        } catch (IOException e) {
            return false;
        }
        return true ;
    }

    private String _id;
}