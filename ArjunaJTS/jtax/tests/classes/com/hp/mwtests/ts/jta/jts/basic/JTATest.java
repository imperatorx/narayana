/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.hp.mwtests.ts.jta.jts.basic;

import static org.junit.Assert.fail;

import javax.transaction.xa.XAResource;

import org.junit.Test;

import com.arjuna.ats.internal.jts.ORBManager;
import com.arjuna.ats.jta.common.jtaPropertyManager;
import com.arjuna.orbportability.OA;
import com.arjuna.orbportability.ORB;
import com.arjuna.orbportability.RootOA;
import com.hp.mwtests.ts.jta.jts.common.XACreator;

public class JTATest
{
    @Test
    public void test() throws Exception
    {
	ORB myORB = null;
	RootOA myOA = null;

	    myORB = ORB.getInstance("test");
	    myOA = OA.getRootOA(myORB);
	    
	    myORB.initORB(new String[] {}, null);
	    myOA.initOA();

	    ORBManager.setORB(myORB);
	    ORBManager.setPOA(myOA);

	String xaResource = "com.hp.mwtests.ts.jta.common.DummyCreator";
	String connectionString = null;
	boolean tmCommit = true;

        jtaPropertyManager.getJTAEnvironmentBean().setTransactionManagerClassName(com.arjuna.ats.internal.jta.transaction.jts.TransactionManagerImple.class.getName());
        jtaPropertyManager.getJTAEnvironmentBean().setUserTransactionClassName(com.arjuna.ats.internal.jta.transaction.jts.UserTransactionImple.class.getName());

	/*
	 * We should have a reference to a factory object (see JTA
	 * specification). However, for simplicity we will ignore this.
	 */
	
	try
	{
	    XACreator creator = (XACreator) Thread.currentThread().getContextClassLoader().loadClass(xaResource).newInstance();
	    XAResource theResource = creator.create(connectionString, true);

	    if (theResource == null)
	    {
    		fail("Error - creator "+xaResource+" returned null resource.");
	    }

	    jakarta.transaction.TransactionManager tm = com.arjuna.ats.jta.TransactionManager.transactionManager();

	    if (tm != null)
	    {
		System.out.println("Starting top-level transaction.");
		
		tm.begin();
	    
		jakarta.transaction.Transaction theTransaction = tm.getTransaction();

		if (theTransaction != null)
		{
		    System.out.println("\nTrying to register resource with transaction.");
		    
		    if (!theTransaction.enlistResource(theResource))
		    {
			tm.rollback();
                fail("Error - could not enlist resource in transaction!");
		    }
		    else
			System.out.println("\nResource enlisted successfully.");
		    /*
		     * XA does not support subtransactions.
		     * By default we ignore any attempts to create such
		     * transactions. Appropriate settings can be made which
		     * will cause currently running transactions to also
		     * rollback, if required.
		     */
		    
		    System.out.println("\nTrying to start another transaction - should fail!");

		    try
		    {
			tm.begin();

			fail("Error - transaction started!");
		    }
		    catch (Exception e)
		    {
			System.out.println("Transaction did not begin: "+e);
		    }
		    
		    /*
		     * Do some work and decide whether to commit or rollback.
		     * (Assume commit for example.)
		     */

		    com.hp.mwtests.ts.jta.jts.common.Synchronization s = new com.hp.mwtests.ts.jta.jts.common.Synchronization();

		    tm.getTransaction().registerSynchronization(s);
		    
		    System.out.println("\nCommitting transaction.");

		    if (tmCommit)
			System.out.println("Using transaction manager.\n");
		    else
			System.out.println("Using transaction.\n");
		    
		    if (tmCommit)
			tm.commit();
		    else
			tm.getTransaction().commit();
		}
		else
		{
		    tm.rollback();
            fail("Error - could not get transaction!");
		}

		System.out.println("\nTest completed successfully.");
	    }
	    else
		System.err.println("Error - could not get transaction manager!");
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

	myOA.destroy();
	myORB.shutdown();
    }

}