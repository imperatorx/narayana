/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package org.jboss.narayana.compensations.internal;

import com.arjuna.mw.wscf.protocols.ProtocolRegistry;
import org.jboss.narayana.compensations.internal.local.LocalBAController;
import org.jboss.narayana.compensations.internal.remote.RemoteBAController;

/**
 * @author paul.robinson@redhat.com 19/04/2014
 */
public class BAControllerFactory {

    public static BAController getInstance() {

        BAController remoteBAController = getRemoteInstance();
        if (remoteBAController.isBARunning()) {
            return remoteBAController;
        }
        BAController localBAController = getLocalInstance();
        if (localBAController.isBARunning()) {
            return localBAController;
        }
        return localBAController;
    }

    public static BAController getRemoteInstance() {

        return new RemoteBAController();
    }

    public static BAController getLocalInstance() {

        ProtocolRegistry.sharedManager().initialise();
        return new LocalBAController();
    }

    public static boolean isLocalTransactionRunning() {

        if (getRemoteInstance().isBARunning()) {
            return false;
        }
        BAController localBAController = getLocalInstance();
        return localBAController.isBARunning();
    }

    public static boolean isRemoteTransactionRunning() {

        return getRemoteInstance().isBARunning();
    }

}
