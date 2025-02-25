/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.jboss.transaction.txinterop.webservices.bainterop.participant;

import com.arjuna.wst.FaultedException;
import com.arjuna.wst.SystemException;
import com.arjuna.wst.WrongStateException;

public class FailedCompensateParticipant extends CompletedParticipant
{
    public void compensate()
    	throws FaultedException, WrongStateException, SystemException
    {
	throw new FaultedException() ;
    }
}