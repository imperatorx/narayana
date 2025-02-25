/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.jboss.jbossts.qa.performance.servers;

import org.jboss.jbossts.qa.Utils.ServerIORStore;

import com.arjuna.orbportability.ORB;
import com.arjuna.orbportability.OA;
import com.arjuna.ats.jts.OTSManager;

import org.jboss.jbossts.qa.performance.*;

import org.jboss.jbossts.qa.performance.implementations.perftestwork.SingleResourceRegistration;
import org.jboss.jbossts.qa.performance.implementations.perftestwork.DummyPerfTestImplementation;

public class NestedOnePhaseServer
{
    /**
     * The main test method which must assert either a pass or a fail.
     */
    public void run(String[] args)
    {
        try
        {
            String serviceName = args[ args.length - 2 ];
            String serviceName2 = args[ args.length - 1 ];

            /**
             * Retrieve ORB and OA references, intialise them
             * and then set the OTSManager ORB and OA properties
             */
            ORB orb = ORB.getInstance( PerformanceFramework.ORB_INSTANCE_NAME );
            OA oa = OA.getRootOA( orb );

            orb.initORB(args, null);
            oa.initOA(args);

            OTSManager.setORB(orb);
            OTSManager.setPOA(oa);

            SingleResourceRegistration obj1 = new SingleResourceRegistration();
            oa.objectIsReady(obj1);
            PerfTestInterface objRef1 = PerfTestInterfaceHelper.narrow(oa.corbaReference(obj1));

            ServerIORStore.storeIOR(serviceName, orb.orb().object_to_string(objRef1));

            DummyPerfTestImplementation obj2 = new DummyPerfTestImplementation();
            oa.objectIsReady(obj2);
            PerfTestInterface objRef2 = PerfTestInterfaceHelper.narrow(oa.corbaReference(obj2));

            ServerIORStore.storeIOR(serviceName2, orb.orb().object_to_string(objRef2));

            System.out.println("Ready");

            orb.orb().run();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
            System.out.println("Failed");
        }
    }
}