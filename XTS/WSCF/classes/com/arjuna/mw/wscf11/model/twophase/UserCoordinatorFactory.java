/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.mw.wscf11.model.twophase;

import com.arjuna.mw.wscf.model.twophase.hls.TwoPhaseHLS;
import com.arjuna.mw.wscf.model.twophase.api.UserCoordinator;

import com.arjuna.mw.wsas.exceptions.SystemException;

import com.arjuna.mw.wscf.exceptions.ProtocolNotRegisteredException;
import com.arjuna.mw.wscf.protocols.ProtocolManager;
import com.arjuna.mw.wscf.protocols.ProtocolRegistry;

import java.util.HashMap;

/**
 * The factory which returns the UserCoordinator implementation to use.
 *
 * @author Mark Little (mark.little@arjuna.com)
 * @version $Id: UserCoordinatorFactory.java,v 1.6 2005/05/19 12:13:25 nmcl Exp $
 * @since 1.0.
 */

public class UserCoordinatorFactory
{

    /**
     * @exception com.arjuna.mw.wscf.exceptions.ProtocolNotRegisteredException Thrown if the default
     * protocol is not available.
     *
     * @return the CoordinatorManager implementation to use. The default
     * coordination protocol is used (two-phase commit) with its
     * associated implementation.
     *
     */

    public static UserCoordinator userCoordinator () throws ProtocolNotRegisteredException, SystemException
    {
        return userCoordinator("TwoPhase11HLS");
    }

    /**
     * Obtain a reference to a coordinator that implements the specified
     * protocol.
     *
     * @param protocol The XML definition of the type of
     * coordination protocol required.
     *
     * @exception com.arjuna.mw.wscf.exceptions.ProtocolNotRegisteredException Thrown if the requested
     * protocol is not available.
     *
     * @return the CoordinatorManager implementation to use.
     */

    public static UserCoordinator userCoordinator (String protocol) throws ProtocolNotRegisteredException, SystemException
    {
        try
        {
            TwoPhaseHLS coordHLS;

            synchronized (_implementations)
            {
                coordHLS = (TwoPhaseHLS) _implementations.get(protocol);

                if (coordHLS == null)
                {
                    coordHLS = (TwoPhaseHLS) _protocolManager.getProtocolImplementation(protocol);

                    _implementations.put(protocol, coordHLS);
                }

                return coordHLS.userCoordinator();
            }
        }
        catch (ProtocolNotRegisteredException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new SystemException(ex.toString());
        }
    }

    private static ProtocolManager _protocolManager = ProtocolRegistry.sharedManager();
    private static HashMap         _implementations = new HashMap();

}