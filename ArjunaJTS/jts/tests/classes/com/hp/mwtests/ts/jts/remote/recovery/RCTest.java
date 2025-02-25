/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.hp.mwtests.ts.jts.remote.recovery;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TRANSACTION_ROLLEDBACK;
import org.omg.CosTransactions.Control;
import org.omg.CosTransactions.Coordinator;
import org.omg.CosTransactions.HeuristicHazard;
import org.omg.CosTransactions.HeuristicMixed;
import org.omg.CosTransactions.NotPrepared;
import org.omg.CosTransactions.RecoveryCoordinator;
import org.omg.CosTransactions.Resource;
import org.omg.CosTransactions.Status;

import com.arjuna.ats.internal.jts.ORBManager;
import com.arjuna.ats.internal.jts.OTSImpleManager;
import com.arjuna.ats.internal.jts.orbspecific.CurrentImple;
import com.arjuna.orbportability.OA;
import com.arjuna.orbportability.ORB;
import com.arjuna.orbportability.RootOA;
import com.hp.mwtests.ts.jts.orbspecific.resources.AtomicResource;

public class RCTest
{
    @Test
    public void test()
    {
        boolean shouldCommit = true;

        boolean passed = false;
        Coordinator coord = null;
        ORB myORB = null;
        RootOA myOA = null;

        try
        {
            myORB = ORB.getInstance("test");
            myOA = OA.getRootOA(myORB);

            myORB.initORB(new String[] {}, null);
            myOA.initOA();

            ORBManager.setORB(myORB);
            ORBManager.setPOA(myOA);

            CurrentImple current = OTSImpleManager.current();
            AtomicResource aImpl = new AtomicResource(shouldCommit);
            Resource atomicObject = aImpl.getReference();

            System.out.println("beginning top-level transaction.");

            current.begin();

            Control myControl = current.get_control();

            assertNotNull( myControl );

            System.out.println("getting coordinator");

            coord = myControl.get_coordinator();

            myControl = null;

            System.out.println("registering resources.");

            RecoveryCoordinator rc = null;

            try
            {
                rc = coord.register_resource(atomicObject);
            }
            catch (Exception ex)
            {
                fail("Failed to register resources: "+ex);
                ex.printStackTrace();
            }

            if (rc == null)
                System.out.println("No recovery coordinator reference.");
            else
            {
                Status s = Status.StatusUnknown;

                try
                {
                    System.out.println("Attempting to use recovery coordinator.");

                    s = rc.replay_completion(atomicObject);
                }
                catch (NotPrepared e)
                {
                    s = Status.StatusActive;
                }
                catch (Exception ex)
                {
                    fail("Caught: "+ex);

                    ex.printStackTrace();
                }

                System.out.println("Got: "+com.arjuna.ats.jts.utils.Utility.stringStatus(s));

                if (s == Status.StatusActive)
                    passed = true;
            }

            System.out.println("committing top-level transaction.");

            if (shouldCommit)
                current.commit(true);
            else
                current.rollback();

            if (rc == null)
                System.out.println("No recovery coordinator reference.");
            else
            {
                Status s = Status.StatusUnknown;

                try
                {
                    System.out.println("Attempting to use recovery coordinator.");

                    s = rc.replay_completion(atomicObject);
                }
                catch (NotPrepared e)
                {
                    s = Status.StatusActive;
                }
                catch (Exception ex)
                {
                    fail("Caught: "+ex);
                }

                System.out.println("Got: "+com.arjuna.ats.jts.utils.Utility.stringStatus(s));

                if (passed && (s == Status.StatusRolledBack))
                    passed = true;
                else
                    passed = false;
            }
        }
        catch (TRANSACTION_ROLLEDBACK  e1)
        {
            System.out.println("\nTransaction RolledBack exception");
        }
        catch (HeuristicMixed e2)
        {
            System.out.println("\nTransaction HeuristicMixed exception");
        }
        catch (HeuristicHazard e3)
        {
            System.out.println("\nTransaction HeuristicHazard exception");
        }
        catch (Exception e4)
        {
            System.out.println("Caught unexpected exception: "+e4);
        }

        System.out.println("Trying to determing final transaction outcome.");

        org.omg.CosTransactions.Status status = Status.StatusUnknown;

        try
        {
            if (coord != null)
            {
                status = coord.get_status();

                coord = null;
            }
            else
                System.out.println("\nCould not determine action status.");
        }
        catch (SystemException ex1)
        {
            // assume invalid reference - tx may have been garbage collected
        }
        catch (Exception e5)
        {
            System.out.println("Caught unexpected exception:" +e5);
        }

        System.out.println("\nFinal action status: "+com.arjuna.ats.jts.utils.Utility.stringStatus(status));

        assertTrue(passed);

        myOA.destroy();
        myORB.shutdown();
    }
}