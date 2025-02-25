/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.hp.mwtests.ts.arjuna.resources;

import java.io.IOException;

import com.arjuna.ats.arjuna.coordinator.OnePhaseResource;
import com.arjuna.ats.arjuna.coordinator.TwoPhaseOutcome;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputObjectState;

public class OnePhase implements OnePhaseResource
{

    public static final int PREPARED = 0;
    public static final int COMMITTED = 1;
    public static final int ROLLEDBACK = 2;

    /**
     * Return values from TwoPhaseOutcome to indicate success or failure.
     */

    public int commit()
    {
        if (_status == ROLLEDBACK) {
            return TwoPhaseOutcome.FINISH_ERROR;
        }

        _status = COMMITTED;
        return TwoPhaseOutcome.FINISH_OK;
    }

    /**
     * Return values from TwoPhaseOutcome to indicate success or failure.
     */

    public int rollback()
    {
        if (_status == COMMITTED) {
            return TwoPhaseOutcome.FINISH_ERROR;
        }

        _status = ROLLEDBACK;
        return TwoPhaseOutcome.FINISH_OK;
    }

    public void pack(OutputObjectState os) throws IOException
    {
    }

    public void unpack(InputObjectState os) throws IOException
    {
    }

    public final int status()
    {
        return _status;
    }

    private int _status = PREPARED;

}