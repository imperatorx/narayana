/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.ats.internal.jta.resources.arjunacore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.arjuna.ats.arjuna.coordinator.ExceptionDeferrer;
import com.arjuna.ats.arjuna.coordinator.OnePhaseResource;
import com.arjuna.ats.arjuna.coordinator.TwoPhaseOutcome;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputObjectState;
import com.arjuna.ats.jta.logging.jtaLogger;
import com.arjuna.ats.jta.utils.XAHelper;
import com.arjuna.ats.jta.xa.RecoverableXAConnection;
import com.arjuna.ats.jta.xa.XidImple;
import com.arjuna.common.internal.util.ClassloadingUtility;

/**
 * One Phase resource wrapper for XAResources.
 * @author Kevin Conner (Kevin.Conner@arjuna.com)
 * @version $Id$
 * @since ATS 4.1
  */
public class XAOnePhaseResource implements OnePhaseResource, ExceptionDeferrer
{
    /**
     * The one phase XA resource.
     */
    private XAResource xaResource ;
    /**
     * The recoverable XA connection.
     */
    private RecoverableXAConnection recoverableXAConnection ;
    /**
     * The transaction identified.
     */
    private Xid xid ;
    /**
     * Any XAException that occurs.
     */
    List<Throwable> deferredExceptions;

    /**
     * Default constructor for deserialising resource.
     */
    public XAOnePhaseResource()
    {
    }

    /**
     * Construct the one phase wrapper for the specified resource.
     * @param xaResource The XA resource being wrapped.
     * @param xid The transaction identifier.
     * @param params additional params to pass through.
     */
    public XAOnePhaseResource(final XAResource xaResource, final Xid xid, final Object[] params)
    {
        this.xaResource = xaResource ;
        this.xid = xid ;
        if ((params != null) && (params.length > XAResourceRecord.XACONNECTION))
        {
            final Object param = params[XAResourceRecord.XACONNECTION] ;
            if (param instanceof RecoverableXAConnection)
                recoverableXAConnection = (RecoverableXAConnection)param ;
        }
    }

    /**
     * Commit the one phase resource.
     * @return TwoPhaseOutcome.FINISH_OK, TwoPhaseOutcome.ONE_PHASE_ERROR or TwoPhaseOutcome.FINISH_ERROR
     */
    public int commit()
    {
        boolean doForget = false;

        try
        {
            // TODO we don't do an end here yet we do in 2PC. Check!!

            xaResource.commit(xid, true) ;
            return TwoPhaseOutcome.FINISH_OK ;
        }
        catch (final XAException xae)
        {
            addDeferredThrowable(xae);
            
            if (jtaLogger.logger.isTraceEnabled()) {
                jtaLogger.logger.trace("XAOnePhaseResource.commit(" + xid + ") " + xae.getMessage());
            }
            jtaLogger.i18NLogger.warn_resources_arjunacore_opcerror(XAHelper.xidToString(xid),
                    xaResource.toString(), XAHelper.printXAErrorCode(xae), xae);


            switch (xae.errorCode)
            {
            case XAException.XA_HEURHAZ:
            case XAException.XA_HEURMIX:
                return TwoPhaseOutcome.HEURISTIC_HAZARD;
            case XAException.XA_HEURCOM:
                doForget = true;
                return TwoPhaseOutcome.FINISH_OK;
            case XAException.XA_HEURRB:
                doForget = true;
                return TwoPhaseOutcome.ONE_PHASE_ERROR;
            case XAException.XA_RBROLLBACK:
            case XAException.XA_RBCOMMFAIL:
            case XAException.XA_RBDEADLOCK:
            case XAException.XA_RBINTEGRITY:
            case XAException.XA_RBOTHER:
            case XAException.XA_RBPROTO:
            case XAException.XA_RBTIMEOUT:
            case XAException.XA_RBTRANSIENT:
            case XAException.XAER_RMERR:
                return TwoPhaseOutcome.ONE_PHASE_ERROR;
            case XAException.XAER_NOTA:
                return TwoPhaseOutcome.HEURISTIC_HAZARD; // something committed or rolled back without asking us!
            case XAException.XAER_INVAL: // resource manager failed, did it rollback?
                return TwoPhaseOutcome.HEURISTIC_HAZARD;
            case XAException.XA_RETRY:  // XA does not allow this to be thrown for 1PC!
            case XAException.XAER_PROTO:
                return TwoPhaseOutcome.ONE_PHASE_ERROR; // assume rollback
            case XAException.XAER_RMFAIL:
            default:
                return TwoPhaseOutcome.FINISH_ERROR;  // recovery should retry
            }
        }
        catch (final Throwable ex)
        {
            String xidString = xid == null ? "<  >" : XAHelper.xidToString(xid);
            String resourceString = xaResource == null ? "null XA resource" : xaResource.toString();

            if (jtaLogger.logger.isTraceEnabled()) {
                jtaLogger.logger.trace("XAOnePhaseResource.commit(" + xid + ") " + ex.getMessage());
            }

            jtaLogger.i18NLogger.warn_resources_arjunacore_commitxaerror(xidString, resourceString, "-", ex);

        }
        finally
        {
            try
            {
                if (doForget)
                    xaResource.forget(xid);
            }
            catch (final Throwable ex)
            {
                if (jtaLogger.logger.isTraceEnabled()) {
                    jtaLogger.logger.trace("XAOnePhaseResource.commit(" + xid + ") called forget and got " + ex.getMessage());
                }
            }
        }

        return TwoPhaseOutcome.ONE_PHASE_ERROR; // presume abort.
    }

    /**
     * Commit the one phase resource.
     * @return TwoPhaseOutcome.FINISH_OK or TwoPhaseOutcome.FINISH_ERROR
     */
    public int rollback()
    {
        try
        {
            xaResource.rollback(xid) ;
            return TwoPhaseOutcome.FINISH_OK ;
        }
        catch (final XAException xae)
        {
            addDeferredThrowable(xae);
            jtaLogger.i18NLogger.warn_resources_arjunacore_XAOnePhaseResource_rollbackexception(XAHelper.xidToString(xid), xae);
        }
        catch (final Throwable ex)
        {
            if (jtaLogger.logger.isTraceEnabled()) {
                jtaLogger.logger.trace("XAOnePhaseResource.rollback(" + xid + ") " + ex.getMessage());
            }
        }

        return TwoPhaseOutcome.FINISH_ERROR ;
    }

    /**
     * Pack the state of the resource.
     * @param os The object output state.
     */
    public void pack(final OutputObjectState os)
        throws IOException
    {
        XidImple.pack(os, xid);
        if (recoverableXAConnection != null)
        {
            os.packInt(RecoverableXAConnection.AUTO_RECOVERY);
            os.packString(recoverableXAConnection.getClass().getName());

            recoverableXAConnection.packInto(os);
        }
        else
        {
            os.packInt(RecoverableXAConnection.OBJECT_RECOVERY) ;
            final byte[] data ;
            try
            {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream() ;
                final ObjectOutputStream oos = new ObjectOutputStream(baos) ;
                oos.writeObject(xaResource) ;
                oos.flush() ;
                oos.close() ;
                data = baos.toByteArray() ;
            }
            catch (final IOException ioe)
            {
                final String message = jtaLogger.i18NLogger.get_resources_arjunacore_XAOnePhaseResource_pack();
                IOException ioException = new IOException(message);
                ioException.initCause(ioe);
                throw ioException;
            }
            os.packBytes(data) ;
        }
    }

    /**
     * Unpack the state of the resource.
     * @param is The object input state.
     */
    public void unpack(final InputObjectState is)
        throws IOException
    {
        XidImple.unpack(is) ;
        final int recoveryType = is.unpackInt() ;
        switch(recoveryType)
        {
            case RecoverableXAConnection.AUTO_RECOVERY:
                final String recoverableXAConnectionClassName = is.unpackString() ;

                recoverableXAConnection = ClassloadingUtility.loadAndInstantiateClass(RecoverableXAConnection.class, recoverableXAConnectionClassName, null);
                if(recoverableXAConnection == null) {
                    throw generateUnpackError(new ClassNotFoundException());
                }

                recoverableXAConnection.unpackFrom(is) ;
                try
                {
                    xaResource = recoverableXAConnection.getResource() ;
                }
                catch (final SQLException sqle)
                {
                    throw generateUnpackError(sqle) ;
                }
                break ;
            case RecoverableXAConnection.OBJECT_RECOVERY:
                final byte[] data = is.unpackBytes() ;
                try
                {
                    final ByteArrayInputStream bais = new ByteArrayInputStream(data) ;
                    final ObjectInputStream ois = new ObjectInputStream(bais) ;
                    xaResource = (XAResource)ois.readObject() ;
                }
                catch (final ClassNotFoundException cnfe)
                {
                    throw generateUnpackError(cnfe) ;
                }
                catch (final IOException ioe)
                {
                    throw generateUnpackError(ioe) ;
                }
                catch (final ClassCastException cce)
                {
                    throw generateUnpackError(cce) ;
                }
                break ;
            default:
                final String message = jtaLogger.i18NLogger.get_resources_arjunacore_XAOnePhaseResource_unpackType(Integer.toString(recoveryType));
                throw new IOException(message);
        }
    }

    @Override
    public String toString()
    {
        return "XAOnePhaseResource("+xaResource+")";
    }

    /**
     * Generate the IOException for the corresponding unpack exception.
     * @param ex The exception caught in unpack.
     * @return The corresponding IOException to be thrown.
     */
    private static IOException generateUnpackError(final Exception ex)
    {
        final String message = jtaLogger.i18NLogger.get_resources_arjunacore_XAOnePhaseResource_unpack();
        return new IOException(message, ex) ;
    }
    
    void addDeferredThrowable(Exception e) 
    {
        if (this.deferredExceptions == null)
            this.deferredExceptions = new ArrayList<>();
        this.deferredExceptions.add(e);
    }

   @Override
   public void getDeferredThrowables(List<Throwable> list)
   {
      if (deferredExceptions != null)
          list.addAll(deferredExceptions);
   }
    
}