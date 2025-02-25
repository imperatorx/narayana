/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package com.arjuna.wstx.tests.arq.ba;

import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.jbossts.xts.bytemanSupport.BMScript;
import org.jboss.jbossts.xts.bytemanSupport.participantCompletion.ParticipantCompletionCoordinatorCloseBeforeCompletedRules;
import org.jboss.jbossts.xts.bytemanSupport.participantCompletion.ParticipantCompletionCoordinatorRules;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.arjuna.mw.wst11.BusinessActivityManager;
import com.arjuna.mw.wst11.UserBusinessActivity;
import com.arjuna.wstx.tests.arq.WarDeployment;
import com.arjuna.wstx.tests.common.DemoBusinessParticipant;

@RunWith(Arquillian.class)
public class CompensateTest {

    @Deployment
    public static WebArchive createDeployment() {
        return WarDeployment.getDeployment(
                DemoBusinessParticipant.class,
                ParticipantCompletionCoordinatorRules.class);
    }

    @BeforeClass()
    public static void submitBytemanScript() throws Exception {
        BMScript.submit(ParticipantCompletionCoordinatorRules.RESOURCE_PATH);
    }

    @AfterClass()
    public static void removeBytemanScript() {
        BMScript.remove(ParticipantCompletionCoordinatorRules.RESOURCE_PATH);
    }

    @Test
    public void testCompensate()
            throws Exception
            {
        ParticipantCompletionCoordinatorRules.setParticipantCount(1);

        UserBusinessActivity uba = UserBusinessActivity.getUserBusinessActivity();
        BusinessActivityManager bam = BusinessActivityManager.getBusinessActivityManager();
        com.arjuna.wst11.BAParticipantManager bpm = null;
        String participantId = "1236";
        DemoBusinessParticipant p = new DemoBusinessParticipant(DemoBusinessParticipant.COMPENSATE, participantId);
        try {
            uba.begin();

            bpm = bam.enlistForBusinessAgreementWithParticipantCompletion(p, participantId);

            bpm.completed();
        } catch (Exception eouter) {
            try {
                uba.cancel();
            } catch(Exception einner) {
            }
            throw eouter;
        }

        uba.cancel();

        assertTrue(p.passed());
            }
}
