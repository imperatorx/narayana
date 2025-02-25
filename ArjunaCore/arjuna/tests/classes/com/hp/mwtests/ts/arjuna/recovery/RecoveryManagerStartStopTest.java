/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */


package com.hp.mwtests.ts.arjuna.recovery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.byteman.contrib.bmunit.BMScript;
import org.jboss.byteman.contrib.bmunit.BMUnitRunner;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.arjuna.ats.arjuna.common.recoveryPropertyManager;
import com.arjuna.ats.arjuna.recovery.RecoveryManager;

/**
 * test to ensure that the recovery manager cleans up all its threads when terminated
 */

@RunWith(BMUnitRunner.class)
@BMScript("recovery")
public class RecoveryManagerStartStopTest
{
    @Before
    public void enableSocketBasedRecovery()
    {
        recoveryPropertyManager.getRecoveryEnvironmentBean().setRecoveryListener(true);
    }

    @Test
    public void testStartStop() throws Exception
    {
        recoveryPropertyManager.getRecoveryEnvironmentBean().setRecoveryPort(4712);

        // check how many threads there are running

        ThreadGroup thg = Thread.currentThread().getThreadGroup();
        int activeCount = thg.activeCount();

        dumpThreadGroup(thg, "Before recovery manager create");

        RecoveryManager.delayRecoveryManagerThread();
        RecoveryManager manager = RecoveryManager.manager(RecoveryManager.INDIRECT_MANAGEMENT);

        dumpThreadGroup(thg, "Before recovery manager initialize");

        manager.initialize();

        dumpThreadGroup(thg, "Before recovery manager start periodic recovery thread");

        manager.startRecoveryManagerThread();

        dumpThreadGroup(thg, "Before recovery manager client create");

        // Thread.sleep(1000);

        // we need to open several connections to the recovery manager listener service and then
        // ensure they get closed down

        addRecoveryClient();
        addRecoveryClient();

        dumpThreadGroup(thg, "Before recovery manager terminate");

        manager.terminate();

        // ensure the client threads get killed

        ensureRecoveryClientsTerminated();

        dumpThreadGroup(thg, "After recovery manager terminate");

        int newActiveCount = thg.activeCount();

        assertEquals(activeCount, newActiveCount);
    }

    private void ensureRecoveryClientsTerminated()
    {
        // check that any threads added to talk to the recovery listener get their sockets closed

        for (RecoveryManagerStartStopTestThread client : clients) {
            try {
                client.join();
            } catch (InterruptedException e) {
                // do nothing
            }
            assertFalse(client.failed());
        }
    }

    private void addRecoveryClient()
    {
        // open a connection to the recovery listener service in a new thread and ensure that the
        // thread is terminated by having its socket closed.

        RecoveryManagerStartStopTestThread client = new RecoveryManagerStartStopTestThread();
        clients.add(client);
        client.start();
        client.ensureStarted();
    }

    private void dumpThreadGroup(ThreadGroup thg, String header)
    {
        int activeCount = thg.activeCount();
        Thread[] threads = new Thread[activeCount];
        int reported = thg.enumerate(threads);

        System.out.println(header);
        System.out.println("Thread count == " + activeCount);
        for (int i = 0; i < reported; i++) {
            System.out.println("Thread[" + i + "] == " + threads[i].getName());
        }

        System.out.flush();
    }

    private List<RecoveryManagerStartStopTestThread> clients = new ArrayList<RecoveryManagerStartStopTestThread>();

    private static class RecoveryManagerStartStopTestThread extends Thread
    {
        private boolean failed = true;
        private boolean started = false;
        private boolean stopped = false;

        public RecoveryManagerStartStopTestThread()
        {
            super("Recovery Listener Client");
        }

        public boolean failed()
        {
            return failed;
        }

        public void run()
        {
            BufferedReader fromServer = null;
            Socket connectorSocket = null;
            // get a socket connected to the listener
            // don't write anything just sit on a read until the socket is closed
            try {
                String host;
                int port;

                host = InetAddress.getLocalHost().getHostName();
                
                port = recoveryPropertyManager.getRecoveryEnvironmentBean().getRecoveryPort();

                System.out.println("client attempting to connect to host " + host + " port " + port);
                System.out.flush();

                try
                {
                    connectorSocket = new Socket(host, port);
                }
                catch (final Exception ex)
                {
                    // in case local host name bind fails (e.g., on Mac OS)
                    System.out.println("caught exception " + ex.getMessage() + " trying IPv4 loopback connection instead");
                    try {
                        connectorSocket = new Socket("localhost", port);
                    } catch (IOException e) {
                        System.out.println("caught exception " + ex.getMessage() + " trying IPv6 loopback connection instead");
                        connectorSocket = new Socket("::1", port);
                    }
                }

                System.out.println("connected!!!");
                System.out.flush();

                fromServer = new BufferedReader(new InputStreamReader(connectorSocket.getInputStream()));
            } catch (Exception e) {

                System.out.println("Failed to set up listener input stream!!!");
                e.printStackTrace();
                System.out.flush();

                return;
            } finally {
                notifyStarted();
            }

            try {
                String result = fromServer.readLine();
                if (result == null || result.equals("")) {
                    System.out.println("Recovery Listener Client got empty string from readline() as expected");
                    System.out.flush();
                    failed = false;
                }
            } catch (SocketException e) {
                if (!connectorSocket.isClosed()) {
                    try {
                        connectorSocket.close();
                    } catch (IOException e1) {
                        // ignore
                    }
                }
                System.out.println("Recovery Listener Client got socket exception as expected");
                e.printStackTrace();
                System.out.flush();
                failed = false;
            } catch (IOException e) {
                if (!connectorSocket.isClosed()) {
                    System.out.println("Recovery Listener Client got non socket IO exception without socket being closed");
                    try {
                        connectorSocket.close();
                    } catch (IOException e1) {
                        // ignore
                    }
                } else {
                    System.out.println("Recovery Listener Client got IO exception under readline() as expected");
                    failed = false;
                }
                e.printStackTrace();
                System.out.flush();
            } catch (Exception e) {
                System.out.println("Recovery Listener Client got non IO exception");
                e.printStackTrace();
                System.out.flush();
            }
        }

        public synchronized void notifyStarted()
        {
            started = true;
            notify();
        }

        public synchronized void ensureStarted() {
            while (!started) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
    }
}