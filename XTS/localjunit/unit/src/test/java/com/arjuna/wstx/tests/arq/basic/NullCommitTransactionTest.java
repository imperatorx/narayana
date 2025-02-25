/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package com.arjuna.wstx.tests.arq.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.arjuna.mw.wst11.UserTransaction;
import com.arjuna.wstx.tests.arq.WarDeployment;

@RunWith(Arquillian.class)
public class NullCommitTransactionTest {

    @Deployment
    public static WebArchive createDeployment() {
        return WarDeployment.getDeployment();
    }

    @Test
    public void testNullCommitTransaction()
            throws Exception
            {
        UserTransaction ut = UserTransaction.getUserTransaction();

        ut.begin();

        ut.commit();
            }
}
