/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.ats.internal.jta.recovery.arjunacore;

import java.io.IOException;

import javax.transaction.xa.Xid;

import com.arjuna.ats.arjuna.AtomicAction;
import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.coordinator.RecordType;
import com.arjuna.ats.arjuna.exceptions.ObjectStoreException;
import com.arjuna.ats.arjuna.logging.tsLogger;
import com.arjuna.ats.arjuna.objectstore.StoreManager;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.internal.arjuna.Header;
import com.arjuna.ats.internal.jta.resources.arjunacore.CommitMarkableResourceRecord;
import com.arjuna.ats.jta.xa.XidImple;

public class RecoverConnectableAtomicAction extends AtomicAction {
	public static final String ATOMIC_ACTION_TYPE = new AtomicAction().type();
	public static final String CONNECTABLE_ATOMIC_ACTION_TYPE = ATOMIC_ACTION_TYPE + "Connectable";

	private String jndiName;
	private String recoveringAs;
	private Xid xid;
	private boolean hasCompleted;
    private boolean wasCommitted;

	public RecoverConnectableAtomicAction(String type, Uid rcvUid, InputObjectState os)
			throws ObjectStoreException, IOException {
		super(rcvUid);
		this.recoveringAs = type;
		
		// Unpack BasicAction::save_state preamble
		Header hdr = new Header();
		unpackHeader(os, hdr);
		os.unpackBoolean(); // FYI pastFirstParticipant

		// Take a look at the first record type
		int record_type = os.unpackInt();
		if (record_type == RecordType.COMMITMARKABLERESOURCE) {
			// Its one we are interested in
			jndiName = os.unpackString();
			xid = XidImple.unpack(os);
			hasCompleted = os.unpackBoolean();
            if (hasCompleted) {
                wasCommitted = os.unpackBoolean();
            }
		}
	}

	@Override
	public String type() {
		return recoveringAs;
	}

	public boolean containsIncompleteCommitMarkableResourceRecord() {
		return jndiName != null && !hasCompleted;
	}

	public String getCommitMarkableResourceJndiName() {
		return jndiName;
	}

	public Xid getXid() {
		return xid;
	}

	public void updateCommitMarkableResourceRecord(boolean committed) {
		activate();
		CommitMarkableResourceRecord peekFront = (CommitMarkableResourceRecord) preparedList
				.peekFront();
		peekFront.updateOutcome(committed);
		if (tsLogger.logger.isTraceEnabled()) {
		    tsLogger.logger.trace("Moving " + get_uid() + " to an AAR so it won't get processed this time");
		}
		deactivate();
	}

    public boolean wasConfirmedCommitted() {
        return wasCommitted;
    }
}