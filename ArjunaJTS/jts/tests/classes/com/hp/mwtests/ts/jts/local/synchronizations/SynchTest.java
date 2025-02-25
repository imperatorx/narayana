/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.hp.mwtests.ts.jts.local.synchronizations;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TRANSACTION_ROLLEDBACK;
import org.omg.CORBA.UserException;
import org.omg.CosTransactions.Control;
import org.omg.CosTransactions.Coordinator;
import org.omg.CosTransactions.Status;

import com.hp.mwtests.ts.jts.utils.ServerORB;
import com.arjuna.ats.jts.OTSManager;
import com.arjuna.orbportability.ORB;
import com.arjuna.orbportability.RootOA;
import com.hp.mwtests.ts.jts.orbspecific.resources.demosync;
import com.hp.mwtests.ts.jts.orbspecific.resources.tranobject_i;

public class SynchTest
{
    @Test
    public void test()
    {
        org.omg.CosTransactions.Status status = Status.StatusUnknown;
        tranobject_i localObject = null;
        demosync sync = null;
        ORB myORB = null;
        RootOA myOA = null;

        try
        {
            ServerORB orb = new ServerORB();

            myORB = orb.getORB();
            myOA = orb.getOA();

            Control myControl = null;
            org.omg.CosTransactions.Current current = OTSManager.get_current();
            Coordinator coord = null;

            sync = new demosync();
            localObject = new tranobject_i();

            current.begin();

            myControl = current.get_control();

            coord = myControl.get_coordinator();

            coord.register_resource(localObject.getReference());
            coord.register_synchronization(sync.getReference());

            try
            {
                current.commit(true);
            }
            catch (TRANSACTION_ROLLEDBACK  e1)
            {
                System.out.println("Transaction rolledback");
            }

            try
            {
                status = coord.get_status();
            }
            catch (SystemException ex)
            {
                // assume reference no longer valid!

                status = Status.StatusUnknown;
            }
        }
        catch (UserException e1)
        {
            fail("Caught UserException: "+e1);
            e1.printStackTrace();
        }
        catch (SystemException e2)
        {
            fail("Caught SystemException: " +e2);
            e2.printStackTrace();
        }

        System.out.print("Final action status: "+com.arjuna.ats.jts.utils.Utility.stringStatus(status));
        System.out.println("\nTest completed successfully.");

        myOA.shutdownObject(sync);
        myOA.shutdownObject(localObject);

        myOA.destroy();
        myORB.shutdown();
    }
    
    public static void main (String[] args)
    {
        SynchTest obj = new SynchTest();
        
        obj.test();
    }
}