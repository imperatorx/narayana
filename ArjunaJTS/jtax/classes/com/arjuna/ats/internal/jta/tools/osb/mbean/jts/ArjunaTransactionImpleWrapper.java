/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.ats.internal.jta.tools.osb.mbean.jts;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.coordinator.AbstractRecord;
import com.arjuna.ats.arjuna.coordinator.BasicAction;
import com.arjuna.ats.arjuna.coordinator.RecordList;
import com.arjuna.ats.arjuna.tools.osb.mbean.*;
import com.arjuna.ats.internal.jts.orbspecific.coordinator.ArjunaTransactionImple;

/**
 * MBean wrapper for exposing the lists maintained by a JTS transaction
 *
 * @see com.arjuna.ats.internal.jts.orbspecific.coordinator.ArjunaTransactionImple
 *
 * @author Mike Musgrove
 */
/**
 * @deprecated as of 5.0.5.Final In a subsequent release we will change packages names in order to 
 * provide a better separation between public and internal classes.
 */
@Deprecated // in order to provide a better separation between public and internal classes.
public class ArjunaTransactionImpleWrapper extends ArjunaTransactionImple implements ActionBeanWrapperInterface {
    UidWrapper wrapper;
    ActionBean action;
    boolean activated;

    public ArjunaTransactionImpleWrapper () {
        this(Uid.nullUid());
    }

    public ArjunaTransactionImpleWrapper (Uid uid) {
        super(uid);
    }
    public ArjunaTransactionImpleWrapper (ActionBean action, UidWrapper w) {
        super(w.getUid());
        this.wrapper = w;
        this.action = action;
    }

    public boolean activate() {
        if (!activated)
            activated = super.activate();

        return activated;
    }

    public String type () {
        String name = UidWrapper.getRecordWrapperTypeName();

        if (name != null)
            return name;

        return super.type();
    }

    public void doUpdateState() {
        updateState();
    }

    public Uid getUid(AbstractRecord rec) {
        return rec.order();
    }

    public void register() {

    }

    public void unregister() {
        
    }

    public RecordList getRecords(ParticipantStatus type) {
        switch (type) {
            default:
            case PREPARED: return preparedList;
            case FAILED: return failedList;
            case HEURISTIC: return heuristicList;
            case PENDING: return pendingList;
            case READONLY: return readonlyList;
        }
    }

    public StringBuilder toString(String prefix, StringBuilder sb) {
        prefix += '\t';
        return sb.append('\n').append(prefix).append(get_uid());
    }

    public BasicAction getAction() {
        return null;
    }

    public void clearHeuristicDecision(int newDecision) {
        if (super.heuristicList.size() == 0)
            setHeuristicDecision(newDecision);
    }

    @Override
    public void remove(LogRecordWrapper logRecordWrapper) {
        if (logRecordWrapper.removeFromList(getRecords(logRecordWrapper.getListType()))) {
            doUpdateState(); // rewrite the list
        }
    }
}