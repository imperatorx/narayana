/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */


package org.jboss.jbossts.xts.recovery.coordinator.ba;

import com.arjuna.ats.arjuna.coordinator.RecordType;
import com.arjuna.ats.arjuna.coordinator.abstractrecord.RecordTypeManager;
import com.arjuna.ats.arjuna.coordinator.abstractrecord.RecordTypeMap;
import com.arjuna.mwlabs.wscf.model.sagas.arjunacore.ParticipantRecord;

/**
 * Module specific class that is responsible for adding any implementations
 * to the inventory.
 *
 * @author Mark Little (mark@arjuna.com)
 * @version $Id: Implementations.java 2342 2006-03-30 13:06:17Z  $
 * @since JTS 1.0.
 */

class ParticipantRecordMap implements RecordTypeMap
{
    @SuppressWarnings("unchecked")
    public Class getRecordClass ()
    {
        return ParticipantRecord.class;
    }
    
    public int getType ()
    {
        return RecordType.XTS_WSBA_RECORD;
    }
}

public class Implementations {

    static boolean _added = false;

    public static synchronized void install ()
    {
        if (!_added)
        {
            RecordTypeManager.manager().add(new ParticipantRecordMap());
            
            _added = true;
        }
    }

    public static synchronized void uninstall()
    {
    }

    private Implementations()
    {
    }
}