/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.hp.mwtests.ts.jts.orbspecific.resources;

import org.omg.CORBA.SystemException;
import org.omg.CosTransactions.Control;
import org.omg.CosTransactions.Coordinator;
import org.omg.CosTransactions.HeuristicCommit;
import org.omg.CosTransactions.HeuristicHazard;
import org.omg.CosTransactions.HeuristicMixed;
import org.omg.CosTransactions.HeuristicRollback;
import org.omg.CosTransactions.Inactive;
import org.omg.CosTransactions.NotPrepared;
import org.omg.CosTransactions.NotSubtransaction;
import org.omg.CosTransactions.SubtransactionAwareResource;
import org.omg.CosTransactions.SubtransactionAwareResourceHelper;
import org.omg.CosTransactions.Unavailable;
import org.omg.CosTransactions.Vote;

import com.arjuna.ats.internal.jts.ORBManager;
import com.arjuna.ats.internal.jts.OTSImpleManager;
import com.arjuna.ats.internal.jts.orbspecific.CurrentImple;
import com.hp.mwtests.ts.jts.utils.ResourceTrace;

public class DemoSubTranResource extends org.omg.CosTransactions.SubtransactionAwareResourcePOA
{
    
    public DemoSubTranResource ()
    {
	ORBManager.getPOA().objectIsReady(this);

	ref = SubtransactionAwareResourceHelper.narrow(ORBManager.getPOA().corbaReference(this));

        trace = new ResourceTrace();

	numSubtransactionsRolledback = 0;
	numSubtransactionsCommitted = 0;
    }

    public SubtransactionAwareResource getReference ()
    {
	return ref;
    }
 
    public void registerResource (boolean registerSubtran) throws Unavailable, Inactive, NotSubtransaction, SystemException
    {
	CurrentImple current = OTSImpleManager.current();
	Control myControl = current.get_control();
	Coordinator coord = myControl.get_coordinator();
	
	if (registerSubtran)
	    coord.register_subtran_aware(ref);
	else
	    coord.register_resource(ref);
	
	System.out.println("Registered DemoSubTranResource");
    }

    public void commit_subtransaction (Coordinator parent) throws SystemException
    {
        numSubtransactionsCommitted++;
	System.out.println("DEMOSUBTRANRESOURCE : COMMIT_SUBTRANSACTION");
    }

    public void rollback_subtransaction () throws SystemException
    {
	System.out.println("DEMOSUBTRANRESOURCE : ROLLBACK_SUBTRANSACTION");
        numSubtransactionsRolledback++;
    }

    public org.omg.CosTransactions.Vote prepare () throws SystemException
    {
	System.out.println("DEMOSUBTRANRESOURCE : PREPARE");

        if (trace.getTrace() == ResourceTrace.ResourceTraceNone)
	    trace.setTrace(ResourceTrace.ResourceTracePrepare);
	else
	    trace.setTrace(ResourceTrace.ResourceTraceUnknown);

	return Vote.VoteCommit;
    }

    public void rollback () throws SystemException, HeuristicCommit, HeuristicMixed, HeuristicHazard
    {
	System.out.println("DEMOSUBTRANRESOURCE : ROLLBACK");

        if (trace.getTrace() == ResourceTrace.ResourceTraceNone)
	    trace.setTrace(ResourceTrace.ResourceTraceRollback);
	else
	{
	    if (trace.getTrace() == ResourceTrace.ResourceTracePrepare)
		trace.setTrace(ResourceTrace.ResourceTracePrepareRollback);
	    else
		trace.setTrace(ResourceTrace.ResourceTraceUnknown);
	}
    }

    public void commit () throws SystemException, NotPrepared, HeuristicRollback, HeuristicMixed, HeuristicHazard
    {
	System.out.println("DEMOSUBTRANRESOURCE : COMMIT");

        if (trace.getTrace() == ResourceTrace.ResourceTracePrepare)
	    trace.setTrace(ResourceTrace.ResourceTracePrepareCommit);
	else
	    trace.setTrace(ResourceTrace.ResourceTraceUnknown);
    }

    public void forget () throws SystemException
    {
	System.out.println("DEMOSUBTRANRESOURCE : FORGET");

        if (trace.getTrace() == ResourceTrace.ResourceTracePrepare)
            trace.setTrace(ResourceTrace.ResourceTracePrepareForget);
        else if (trace.getTrace() == ResourceTrace.ResourceTracePrepareRollback)
            trace.setTrace(ResourceTrace.ResourceTracePrepareRollbackForget);
        else if (trace.getTrace() == ResourceTrace.ResourceTracePrepareCommit)
            trace.setTrace(ResourceTrace.ResourceTracePrepareCommitForget);
        else if (trace.getTrace() == ResourceTrace.ResourceTraceCommitOnePhase)
            trace.setTrace(ResourceTrace.ResourceTraceCommitOnePhaseForget);
        else if (trace.getTrace() == ResourceTrace.ResourceTracePrepareCommitHeurisiticRollback)
            trace.setTrace(ResourceTrace.ResourceTracePrepareCommitHeurisiticRollbackForget);
        else if (trace.getTrace() == ResourceTrace.ResourceTracePrepareHeuristicHazard)
            trace.setTrace(ResourceTrace.ResourceTracePrepareHeuristicHazardForget);
        else
            trace.setTrace(ResourceTrace.ResourceTraceUnknown);
    }

    public void commit_one_phase () throws HeuristicHazard, SystemException
    {
	System.out.println("DEMOSUBTRANRESOURCE : COMMIT_ONE_PHASE");

        if (trace.getTrace() == ResourceTrace.ResourceTraceNone)
            trace.setTrace(ResourceTrace.ResourceTraceCommitOnePhase);
        else
            trace.setTrace(ResourceTrace.ResourceTraceUnknown);
    }

    public int getNumberOfSubtransactionsCommitted()
    {
        return(numSubtransactionsCommitted);
    }

    public int getNumberOfSubtransactionsRolledBack()
    {
        return(numSubtransactionsRolledback);
    }

    public ResourceTrace getResourceTrace()
    {
        return(trace);
    }

    private SubtransactionAwareResource ref;
    private ResourceTrace trace;

    private int numSubtransactionsCommitted;
    private int numSubtransactionsRolledback;
}