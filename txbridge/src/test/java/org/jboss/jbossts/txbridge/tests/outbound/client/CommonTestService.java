/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package org.jboss.jbossts.txbridge.tests.outbound.client;

import java.util.ArrayList;

public interface CommonTestService {

    void doNothing();

    ArrayList<String> getTwoPhaseCommitInvocations();

    void reset();

}
