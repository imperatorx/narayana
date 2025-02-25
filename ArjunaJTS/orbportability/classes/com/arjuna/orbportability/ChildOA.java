/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.orbportability;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.SystemException;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;

import com.arjuna.orbportability.event.EventManager;
import com.arjuna.orbportability.logging.opLogger;


/**
 * A class which represents a child OA
 *
 * @author Richard Begg (richard_begg@hp.com)
 */
public class ChildOA extends OA
{
    /**
     * Constructs a child OA for a given ORB with a given name wrapping a given POA
     *
     * @param orb The ORB this OA is a part of
     * @param oaName The name of this OA
     * @param thisPOA The POA this class is a wrapper for
     */
    ChildOA (com.arjuna.orbportability.ORB orb, String oaName, POA thisPOA)
    {
        super(orb,oaName,thisPOA);
    }

    /**
     * Set the Root POA represented by this ChildOA class
     *
     * @return True - if the root POA was successfully altered
     */
public synchronized boolean setRootPoa (POA thePOA)
    {
        if (!_oa.initialised())
        {
            _oa.rootPoa(thePOA);

            return true;
        }
        else
            return false;
    }

public org.omg.CORBA.Object corbaReference (Servant obj)
    {
        org.omg.CORBA.Object objRef = null;

        if (_oa.initialised())
        {
            try
            {
                objRef = corbaReference(obj, _oa.poa(_oaName));
            }
            catch (Exception e)
            {
                objRef = null;
            }
        }

        return objRef;
    }

public boolean objectIsReady (Servant obj, byte[] id) throws SystemException
    {
        if (opLogger.logger.isTraceEnabled()) {
            opLogger.logger.trace("ChildOA::objectIsReady (Servant, byte[], " + _oaName + ")");
        }

        try
        {
            _oa.poa(_oaName).activate_object_with_id(id, obj);
        }
        catch (Exception e)
        {
            opLogger.i18NLogger.warn_OA_exceptioncaughtforobj("objectIsReady", obj.toString(), e);

            return false;
        }

        return true;
    }

public boolean objectIsReady (Servant obj) throws SystemException
    {
        if (opLogger.logger.isTraceEnabled()) {
            opLogger.logger.trace("OA::objectIsReady (Servant)");
        }

        boolean result = true;

        try
        {
            boolean invalidPOA = false;

            if (_oa.poa(_oaName) != null)
                _oa.poa(_oaName).activate_object(obj);
            else
                invalidPOA = true;

            if (invalidPOA)
            {
                opLogger.i18NLogger.warn_OA_invalidpoa("objectIsReady", "rootPOA");

                result = false;
            }

            EventManager.getManager().connected(corbaReference(obj));
        }
        catch (Exception e)
        {
            opLogger.i18NLogger.warn_OA_exceptioncaughtforobj("objectIsReady", obj.toString(), e);

            result = false;
        }

        return result;
    }

public boolean shutdownObject (org.omg.CORBA.Object obj)
    {
        if (opLogger.logger.isTraceEnabled()) {
            opLogger.logger.trace("ChildOA::shutdownObject ()");
        }

        boolean result = true;

        try
        {
            EventManager.getManager().disconnected(obj);

            boolean invalidPOA = false;
            if (_oa.poa(_oaName) != null)
                _oa.poa(_oaName).deactivate_object(_oa.poa(_oaName).reference_to_id(obj));
            else
                invalidPOA = true;

            if (invalidPOA)
            {
                opLogger.i18NLogger.warn_OA_invalidpoa("objectIsReady", "childPOA");

                result = false;
            }
        }
        catch (Exception e)
        {
            opLogger.i18NLogger.warn_OA_caughtexception("objectIsReady", e);

            result = false;
        }

        return result;
    }

public boolean shutdownObject (Servant obj)
    {
        if (opLogger.logger.isTraceEnabled()) {
            opLogger.logger.trace("ChildOA::shutdownObject (Servant)");
        }

        boolean result = true;

        try
        {
            boolean invalidPOA = false;
            if (_oa.poa(_oaName) != null)
                _oa.poa(_oaName).deactivate_object(_oa.poa(_oaName).servant_to_id(obj));
            else
                invalidPOA = true;

            if (invalidPOA)
            {
                opLogger.i18NLogger.warn_OA_invalidpoa("shutdownObject", "childPOA");

                result = false;
            }
        }
	catch (NullPointerException ex)
	{
	    /*
	     * Ignore this as it means some other thread/process was sharing
	     * the POA and shut it down itself.
	     */
	}
        catch (Exception e)
        {
            opLogger.i18NLogger.warn_OA_caughtexception("shutdownObject", e);

            result = false;
        }

        return result;
    }

public synchronized void destroy() throws SystemException
    {
        if (opLogger.logger.isTraceEnabled()) {
            opLogger.logger.trace("OA::destroyPOA ()");
        }

        if (_oaName == null)
            throw new BAD_PARAM();

        _oa.destroyPOA(_oaName);
    }
}