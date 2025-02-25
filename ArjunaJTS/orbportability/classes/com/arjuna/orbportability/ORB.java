/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.orbportability;

import java.applet.Applet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.omg.CORBA.SystemException;

import com.arjuna.orbportability.common.opPropertyManager;
import com.arjuna.orbportability.internal.utils.PostInitLoader;
import com.arjuna.orbportability.internal.utils.PostSetLoader;
import com.arjuna.orbportability.internal.utils.PreInitLoader;
import com.arjuna.orbportability.logging.opLogger;
import com.arjuna.orbportability.orb.Attribute;
import com.arjuna.orbportability.orb.PostShutdown;
import com.arjuna.orbportability.orb.PreShutdown;

/**
 * An attempt at some ORB portable ways of interacting with the ORB. NOTE:
 * initORB *must* be called if you want to use the pre- and post- initialisation
 * mechanisms.
 * 
 * @author Mark Little (mark@arjuna.com), Richard Begg (richard.begg@arjuna.com)
 * @version $Id: ORB.java 2342 2006-03-30 13:06:17Z $
 * @since JTS 1.0.
 */

public class ORB
{
    /**
     * Initialise the default ORB.
     */

    public synchronized void initORB () throws SystemException
    {
        if (opLogger.logger.isTraceEnabled())
        {
            opLogger.logger.trace("ORB::initORB ()");
        }

        /*
         * Since an ORB can be initialised multiple times we currently allow the
         * initialisation code to be activated multiple times as well. Does this
         * make sense?
         */

        if (!_orb.initialised())
        {
            // null op - just skip it loadProperties(null);

            /**
             * Perform pre-initialisation classes for all ORBs
             */
            PreInitLoader preInit = new PreInitLoader(
                    PreInitLoader.generateORBPropertyName(ORB_INITIALISER_NS),
                    this);

            /**
             * Perform pre-initialisation classes for this ORB only
             */
            preInit = new PreInitLoader(PreInitLoader.generateORBPropertyName(
                    ORB_INITIALISER_NS, _orbName), this);
            preInit = null;

            parseProperties(null, false);

            _orb.init();

            parseProperties(null, true);

            /**
             * Perform post-initialisation classes for all ORBs
             */
            PostInitLoader postInit = new PostInitLoader(
                    PostInitLoader.generateORBPropertyName(ORB_INITIALISER_NS),
                    this);

            /**
             * Perform post-initialisation classes for this ORB only
             */
            postInit = new PostInitLoader(
                    PostInitLoader.generateORBPropertyName(ORB_INITIALISER_NS,
                            _orbName), this);
            postInit = null;
        }
    }

    /**
     * Initialise the ORB.
     */

    public synchronized void initORB (Applet a, Properties p)
            throws SystemException
    {
        if (opLogger.logger.isTraceEnabled())
        {
            opLogger.logger.trace("ORB::initORB (Applet, Properties)");
        }

        if (!_orb.initialised())
        {
            loadProperties(p);

            /**
             * Perform pre-initialisation classes for all ORBs
             */
            PreInitLoader preInit = new PreInitLoader(
                    PreInitLoader.generateORBPropertyName(ORB_INITIALISER_NS),
                    this);

            /**
             * Perform pre-initialisation classes for this ORB only
             */
            preInit = new PreInitLoader(PreInitLoader.generateORBPropertyName(
                    ORB_INITIALISER_NS, _orbName), this);
            preInit = null;

            parseProperties(null, false);

            _orb.init(a, p);

            parseProperties(null, true);

            /**
             * Perform post-initialisation classes for all ORBs
             */
            PostInitLoader postInit = new PostInitLoader(
                    PostInitLoader.generateORBPropertyName(ORB_INITIALISER_NS),
                    this);

            /**
             * Perform post-initialisation classes for this ORB only
             */
            postInit = new PostInitLoader(
                    PostInitLoader.generateORBPropertyName(ORB_INITIALISER_NS,
                            _orbName), this);
            postInit = null;
        }
    }

    /**
     * Initialise the ORB.
     */

    public synchronized void initORB (String[] s, Properties p)
            throws SystemException
    {
        if (opLogger.logger.isTraceEnabled())
        {
            opLogger.logger.trace("ORB::initORB (String[], Properties)");
        }

        if (!_orb.initialised())
        {
            loadProperties(p);

            /**
             * Perform pre-initialisation classes for all ORBs
             */
            PreInitLoader preInit = new PreInitLoader(
                    PreInitLoader.generateORBPropertyName(ORB_INITIALISER_NS),
                    this);

            /**
             * Perform pre-initialisation classes for this ORB only
             */
            preInit = new PreInitLoader(PreInitLoader.generateORBPropertyName(
                    ORB_INITIALISER_NS, _orbName), this);
            preInit = null;

            parseProperties(s, false);

            _orb.init(s, p);

            parseProperties(s, true);

            /**
             * Perform post-initialisation classes for all ORBs
             */
            PostInitLoader postInit = new PostInitLoader(
                    PostInitLoader.generateORBPropertyName(ORB_INITIALISER_NS),
                    this);

            /**
             * Perform post-initialisation classes for this ORB only
             */
            postInit = new PostInitLoader(
                    PostInitLoader.generateORBPropertyName(ORB_INITIALISER_NS,
                            _orbName), this);
            postInit = null;
        }
    }

    public synchronized boolean addAttribute (Attribute p)
    {
        if (opLogger.logger.isTraceEnabled())
        {
            opLogger.logger.trace("ORB::addAttribute (" + p + ")");
        }

        if (_orb.initialised()) // orb already set up!
            return false;

        if (p.postORBInit())
            _postORBInitProperty.put(p, p);
        else
            _preORBInitProperty.put(p, p);

        return true;
    }

    /**
     * Shutdown the ORB asynchronously.
     */
    
    public synchronized void shutdown ()
    {
        shutdown(false);
    }
    
    /**
     * Shutdown the ORB. Define whether this should be sync or async.
     */

    public synchronized void shutdown (boolean waitForCompletion)
    {
        if (opLogger.logger.isTraceEnabled())
        {
            opLogger.logger.trace("ORB::shutdown ()");
        }

        // Ensure destroy is called on the root OA so that any pre/post destroy hooks get called
        // Normally we expect whoever called shutdown to have done this however destroy is
        // safe to call multiple times
        OA.getRootOA(this).destroy();

        /*
         * Do the cleanups first!
         */

        if (!_preORBShutdown.isEmpty())
        {
            Enumeration elements = _preORBShutdown.elements();

            while (elements.hasMoreElements())
            {
                PreShutdown c = (PreShutdown) elements.nextElement();

                if (c != null)
                {
                    if (opLogger.logger.isTraceEnabled())
                    {
                        opLogger.logger.trace("ORB - pre-orb shutdown on "
                                + c.name());
                    }

                    c.work();
                    c = null;
                }
            }

            // _preORBShutdown.clear();
        }

        if (_orb.initialised())
            _orb.shutdown(waitForCompletion);

        if (!_postORBShutdown.isEmpty())
        {
            Enumeration elements = _postORBShutdown.elements();

            while (elements.hasMoreElements())
            {
                PostShutdown c = (PostShutdown) elements.nextElement();

                if (c != null)
                {
                    if (opLogger.logger.isTraceEnabled())
                    {
                        opLogger.logger.trace("ORB - post-orb shutdown on "
                                + c.name());
                    }

                    c.work();
                    c = null;
                }
            }

            // _postORBShutdown.clear();
        }
    _orbMap.remove(_orbName);
    if (_orbShutdownListener != null) {
        _orbShutdownListener.orbShutdown();
    }
    }

    /**
     * Obtain a reference to the current ORB.
     */

    public synchronized org.omg.CORBA.ORB orb ()
    {
        return _orb.orb();
    }

    public synchronized boolean setOrb (org.omg.CORBA.ORB theORB)
    {
        if (!_orb.initialised())
        {
            _orb.orb(theORB);

            /** Perform post-set operations configured for all ORBs **/
            new PostSetLoader(
                    PostSetLoader.generateORBPropertyName(ORB_INITIALISER_NS),
                    this);

            /**
             * Perform post-set operations for this ORB only
             */
            new PostSetLoader(PostSetLoader.generateORBPropertyName(
                    ORB_INITIALISER_NS, _orbName), this);

            return true;
        }
        else
            return false;
    }

    public synchronized void addPreShutdown (PreShutdown c)
    {
        if (opLogger.logger.isTraceEnabled())
        {
            opLogger.logger.trace("ORB::addPreShutdown (" + c + ")");
        }

        _preORBShutdown.put(c, c);
    }

    public synchronized void addPostShutdown (PostShutdown c)
    {
        if (opLogger.logger.isTraceEnabled())
        {
            opLogger.logger.trace("ORB::addPostShutdown (" + c + ")");
        }

        _postORBShutdown.put(c, c);
    }

    public synchronized void destroy () throws SystemException
    {
        if (opLogger.logger.isTraceEnabled())
        {
            opLogger.logger.trace("ORB::destroyORB ()");
        }

        _orb.destroy();
    }

    protected ORB(String orbName)
    {
        _orbName = orbName;
    }

    private void loadProperties (Properties p)
    {
        /**
         * If properties were passed in and the map contains data
         */
        if ((p != null) && (!p.isEmpty()))
        {
            /**
             * For each property passed in the initialiser only set those which
             * are intended for post or pre initialisation routines
             */
            Enumeration properties = p.keys();
            while (properties.hasMoreElements())
            {
                String o = (String) properties.nextElement();

                if (PreInitLoader.isPreInitProperty(o)
                        || PostInitLoader.isPostInitProperty(o))
                {
                    if (opLogger.logger.isTraceEnabled())
                    {
                        opLogger.logger.trace("Adding property '" + o
                                + "' to the ORB portability properties");
                    }

                    synchronized (ORB.class)
                    {
                        Map<String, String> globalProperties = opPropertyManager
                                .getOrbPortabilityEnvironmentBean()
                                .getOrbInitializationProperties();
                        globalProperties.put(o, p.getProperty(o));
                        opPropertyManager.getOrbPortabilityEnvironmentBean()
                                .setOrbInitializationProperties(
                                        globalProperties);
                    }
                }
            }
        }
    }

    private void parseProperties (String[] params, boolean postInit)
    {
        if (opLogger.logger.isTraceEnabled())
        {
            opLogger.logger.trace("ORB::parseProperties (String[], " + postInit
                    + ")");
        }

        Hashtable work = ((postInit) ? _postORBInitProperty
                : _preORBInitProperty);

        if (!work.isEmpty())
        {
            Enumeration elements = work.elements();

            while (elements.hasMoreElements())
            {
                Attribute p = (Attribute) elements.nextElement();

                if (p != null)
                {
                    if (opLogger.logger.isTraceEnabled())
                    {
                        opLogger.logger.trace("Attribute " + p
                                + " initialising.");
                    }

                    p.initialise(params);
                    p = null;
                }
            }

            // work.clear();
        }
    }

    /**
     * Retrieve an ORB instance given a unique name, if an ORB instance with
     * this name doesn't exist then create it.
     * 
     * @param uniqueId
     *            The name of the ORB instance to retrieve.
     * @return The ORB instance refered to by the name given.
     */
    public synchronized static ORB getInstance (String uniqueId)
    {
        /**
         * Try and find this ORB in the hashmap first if its not there then
         * create one and add it
         */
        ORB orb = (ORB) _orbMap.get(uniqueId);

        if (orb == null)
        {
            orb = new ORB(uniqueId);

            _orbMap.put(uniqueId, orb);
        }

        return (orb);
    }

    String getName ()
    {
        return (_orbName);
    }

public void setORBShutdownListener(ORBShutdownListener orbShutdownListener) {
	_orbShutdownListener = orbShutdownListener;
}

    com.arjuna.orbportability.orb.core.ORB _orb = new com.arjuna.orbportability.orb.core.ORB();

    private Hashtable _preORBShutdown = new Hashtable();

    private Hashtable _postORBShutdown = new Hashtable();

    private Hashtable _preORBInitProperty = new Hashtable();

    private Hashtable _postORBInitProperty = new Hashtable();

    private String _orbName = null;

private ORBShutdownListener _orbShutdownListener;

private volatile static HashMap	 _orbMap = new HashMap();

    static final String ORB_INITIALISER_NS = "com.arjuna.orbportability.orb";

}