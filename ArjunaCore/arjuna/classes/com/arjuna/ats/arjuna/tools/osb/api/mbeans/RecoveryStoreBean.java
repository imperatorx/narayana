/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.ats.arjuna.tools.osb.api.mbeans;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.exceptions.ObjectStoreException;
import com.arjuna.ats.arjuna.objectstore.RecoveryStore;
import com.arjuna.ats.arjuna.objectstore.StoreManager;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.tools.osb.api.proxy.StoreManagerProxy;

/**
 * implementation of the JMX interface to the JBossTS recovery store
 */
public class RecoveryStoreBean extends TxLogBean implements RecoveryStoreBeanMBean {
	private RecoveryStore rs;

    /**
     * Construct an MBean corresponding to the default recovery store in this JVM
     */
	public RecoveryStoreBean() {
        super(StoreManager.getRecoveryStore());
		rs = (RecoveryStore) getStore();
	}
    /**
     * Construct an MBean corresponding to the given store
     * @param rs the RecoveryStore that is wrapped by this MBean
     */
    public RecoveryStoreBean(RecoveryStore rs) {
        super(rs);
        this.rs = rs;
    }

    @Override
    protected ObjectName getMBeanName() {
        try {
            return new ObjectName(StoreManagerProxy.RECOVERY_BEAN_NAME);
        } catch (MalformedObjectNameException e) {
            System.out.println("Error creating object name: " + e.getMessage());
            return null;
        }
    }

	// RecoveryStore interface implementation

	public ObjectStateWrapper allObjUids(String type, int m) throws ObjectStoreException {
        InputObjectState ios = new InputObjectState();
		boolean ok = rs.allObjUids (type, ios, m);
        return new ObjectStateWrapper(ios, ok);
	}

	public ObjectStateWrapper allObjUids(String type) throws ObjectStoreException {
        InputObjectState ios = new InputObjectState();
		boolean ok = rs.allObjUids (type, ios);
        return new ObjectStateWrapper(ios, ok);
	}

	public ObjectStateWrapper allTypes() throws ObjectStoreException {
        InputObjectState ios = new InputObjectState();
        boolean ok = rs.allTypes(ios);
		return new ObjectStateWrapper(ios, ok);
	}

	public int currentState (Uid u, String tn) throws ObjectStoreException {
		return rs.currentState (u, tn);
	}

	public boolean hide_state (Uid u, String tn) throws ObjectStoreException {
		return rs.hide_state (u, tn);
	}

	public boolean reveal_state (Uid u, String tn) throws ObjectStoreException {
		return rs.reveal_state (u, tn);
	}

	public ObjectStateWrapper read_committed (Uid u, String tn) throws ObjectStoreException {
		InputObjectState ios = rs.read_committed (u, tn);
        return new ObjectStateWrapper(ios);
	}

	public boolean isType (Uid u, String tn, int st) throws ObjectStoreException {
		return rs.isType (u, tn, st);
	}
}