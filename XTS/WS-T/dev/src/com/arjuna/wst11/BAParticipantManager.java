/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.wst11;

import com.arjuna.wst.WrongStateException;
import com.arjuna.wst.UnknownTransactionException;
import com.arjuna.wst.SystemException;

import javax.xml.namespace.QName;

/**
 * When a Business Activity participant registers with a BA transaction it
 * gets a reference to the transaction coordinator that lets it callback
 * into the transaction and drive the state-transition.
 *
 * One instance of this per transaction.
 *
 * @author Mark Little (mark.little@arjuna.com)
 * @version $Id: BAParticipantManager.java,v 1.5 2004/09/09 08:48:33 kconner Exp $
 * @since XTS 1.0.
 */

public interface BAParticipantManager
{

    /**
     * The participant has exited the business activity. The participant
     * uses this to inform the coordinator that is has left the activity.
     * It will not be informed when (and how) the business activity terminates.
     */

    public void exit () throws WrongStateException, UnknownTransactionException, SystemException;

    /**
     * The participant has completed it works, but wishes to continue in
     * the business activity, so that it will eventually be told when (and how)
     * the activity terminates. The participant may later be asked to
     * compensate for the work it has done.
     */

    public void completed () throws WrongStateException, UnknownTransactionException, SystemException;

    /**
     * Cannot complete.
     */

    public void cannotComplete () throws WrongStateException, UnknownTransactionException, SystemException;

    /**
     * Fault.
     */

    public void fail (final QName exceptionIdentifier) throws SystemException;
}