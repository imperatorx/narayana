/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.jboss.transaction.wstf.webservices.sc007.participant;

import com.arjuna.wst.Durable2PCParticipant;
import com.arjuna.wst.Prepared;
import com.arjuna.wst.SystemException;
import com.arjuna.wst.Vote;
import com.arjuna.wst.WrongStateException;

/**
 * The durable 2PC participant which fails the first call to commit.
 */
public class CommitFailureDurable2PCParticipant extends ParticipantAdapter implements Durable2PCParticipant
{
    /**
     * The drop commit flag.
     */
    private boolean dropCommit ;
    
    /**
     * Vote to prepare.
     */
    public Vote prepare()
        throws WrongStateException, SystemException
    {
        return new Prepared() ;
    }
    
    public void commit()
        throws WrongStateException, SystemException
    {
        if (!dropCommit)
        {
            dropCommit = true ;
            throw new IllegalStateException("Forced failure of commit") ;
        }
    }
}