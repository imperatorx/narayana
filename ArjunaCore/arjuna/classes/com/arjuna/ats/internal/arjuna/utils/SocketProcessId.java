/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.ats.internal.arjuna.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import com.arjuna.ats.arjuna.common.arjPropertyManager;
import com.arjuna.ats.arjuna.exceptions.FatalError;
import com.arjuna.ats.arjuna.logging.tsLogger;
import com.arjuna.ats.arjuna.utils.Utility;

/**
 * Obtains a unique value to represent the process id via sockets and
 * ports.
 *
 * @author Mark Little (mark_little@hp.com)
 * @version $Id: SocketProcessId.java 2342 2006-03-30 13:06:17Z  $
 * @since HPTS 3.0.
 */

public class SocketProcessId implements com.arjuna.ats.arjuna.utils.Process
{
    public SocketProcessId()
    {
        int port = arjPropertyManager.getCoreEnvironmentBean().getSocketProcessIdPort();
        int maxPorts = arjPropertyManager.getCoreEnvironmentBean().getSocketProcessIdMaxPorts();

        int maxPort;

        if (maxPorts <= 1)
        {
            maxPort = port;
        }
        else if (Utility.MAX_PORT - maxPorts < port)
        {
            maxPort = Utility.MAX_PORT;
        }
        else
        {
            maxPort = port + maxPorts;
        }

        do {
            _theSocket = createSocket(port);
        } while (_theSocket == null && ++port < maxPort);

        _thePort = ((_theSocket == null) ? -1 : _theSocket.getLocalPort());

        if (_thePort == -1) {
            throw new FatalError(tsLogger.i18NLogger.get_utils_SocketProcessId_2());
        }
    }

    /**
     * @return the process id. This had better be unique between processes
     * on the same machine. If not we're in trouble!
     */
    public int getpid ()
    {
    	return _thePort;
    }

    private static ServerSocket createSocket(int port)
    {
        try
        {
            return new ServerSocket(port, 0, InetAddress.getByName(null));
        }
        catch (IOException e)
        {
            return null;
        }
    }

    private final int _thePort;
    private ServerSocket _theSocket;
}