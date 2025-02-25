/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */


 
package com.arjuna.ats.internal.arjuna.recovery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import com.arjuna.ats.arjuna.logging.tsLogger;
import com.arjuna.ats.arjuna.recovery.ExtendedRecoveryModule;
import com.arjuna.ats.arjuna.recovery.RecoveryDriver;
import com.arjuna.ats.arjuna.recovery.RecoveryManager;
import com.arjuna.ats.arjuna.recovery.RecoveryModule;
import com.arjuna.ats.arjuna.recovery.Service;

public class WorkerService implements Service
{
    public WorkerService (PeriodicRecovery pr)
    {
	_periodicRecovery = pr;
    }
    
    public void doWork (InputStream is, OutputStream os) throws IOException
    {
	BufferedReader in  = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
	PrintWriter out = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));

	try
	{
	    String request = in.readLine();

        if ("PING".equals(request))
        {
            out.println("PONG");
        }
        else
	    if (RecoveryDriver.isScan(request))
	    {
	    	boolean isAsync = RecoveryDriver.isAsyncScan(request);
	    	boolean anyProblems = false;

            // hmm, we need to synchronize on the periodic recovery object in order to wake it up via notify.
            // but the periodic recovery object has to synchronize on this object and then call notify
            // in order to tell it that the last requested scan has completed. i.e. we have a two way
            // wakeup here. so we have to be careful to avoid a deadlock.

            if (!isAsync) {
                // do this before kicking the periodic recovery thread
                synchronized (this) {
                    doWait = true;
                }
            }
            // now we only need to hold one lock
		_periodicRecovery.wakeUp();

            tsLogger.i18NLogger.info_recovery_WorkerService_3();

		if (!isAsync)
		{
            synchronized (this) {
                if (doWait && _periodicRecovery.getMode() != PeriodicRecovery.Mode.TERMINATED) {
                    // ok, the periodic recovery thread cannot have finished responding to the last scan request
                    // so it is safe to wait. if we delivered the request while the last scan was still going
                    // then it will have been ignored but that is ok.
		    try
		    {
			wait();
		    }
		    catch (Exception ex)
		    {
                if(tsLogger.logger.isTraceEnabled()) {
                    tsLogger.logger.trace("Waiting for recovery scan to complete finished with an exception", ex);
                }
                tsLogger.i18NLogger.info_recovery_WorkerService_4();
		    }
                }
            }
		}

		if (RecoveryDriver.isVerboseScan(request)) {
			for (final RecoveryModule recoveryModule : RecoveryManager.manager().getModules()) {
				if (recoveryModule instanceof ExtendedRecoveryModule) {
					if (!((ExtendedRecoveryModule) recoveryModule).isPeriodicWorkSuccessful()) {
						anyProblems = true;
					}
					break;
				}
			}
		}

		out.println(anyProblems ? "ERROR" : "DONE");
	    }
	    else
		out.println("ERROR");

        out.flush();
	}
	catch (IOException ioe) {
        tsLogger.i18NLogger.warn_recovery_WorkerService_2(ioe);
    }
	catch ( Exception ex ) {
        tsLogger.i18NLogger.warn_recovery_WorkerService_1(ex);
    }
    }

    public synchronized void notifyDone()
    {
	try
	{
	    notifyAll();
        doWait = false;
	}
	catch (Exception ex)
	{
	}
    }

    private PeriodicRecovery _periodicRecovery = null;
    private boolean doWait = false;

}