/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.mw.wst11.client;

import java.util.Iterator;

import jakarta.xml.soap.Name;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPHeader;
import jakarta.xml.soap.SOAPHeaderElement;
import jakarta.xml.soap.SOAPMessage;

import org.oasis_open.docs.ws_tx.wscoor._2006._06.CoordinationContextType;

import com.arjuna.mw.wsc11.context.Context;
import com.arjuna.mw.wst.TxContext;
import com.arjuna.mw.wst.common.SOAPUtil;
import com.arjuna.mw.wst11.BusinessActivityManagerFactory;
import com.arjuna.mw.wst11.TransactionManager;
import com.arjuna.mw.wst11.TransactionManagerFactory;
import com.arjuna.mw.wst11.common.CoordinationContextHelper;
import com.arjuna.mw.wstx.logging.wstxLogger;
import com.arjuna.mwlabs.wst11.at.context.TxContextImple;
import com.arjuna.webservices11.wsat.AtomicTransactionConstants;
import com.arjuna.webservices11.wsba.BusinessActivityConstants;
import com.arjuna.webservices11.wscoor.CoordinationConstants;

/**
 * Common base class for classes used to perform
 * WS-Transaction context manipulation on SOAP messages.
 *
 */
class JaxBaseHeaderContextProcessor
{
    /**
     * Handle the request.
     *
     * @param soapMessage The current message context.
     */
    public boolean handleOutboundMessage(final SOAPMessage soapMessage)
    {
        return handleOutboundMessage(soapMessage, true);
    }

    /**
     * Handle the request.
     *
     * @param soapMessage The current message context.
     * @param mustUnderstand Value of MustUnderstand attribute.
     * @return whether the message was handled
     */
    public boolean handleOutboundMessage(final SOAPMessage soapMessage, boolean mustUnderstand)
    {
        if (soapMessage == null)
        {
            return true ;
        }

        try
        {
            /*
             * There should either be an Atomic Transaction *or* a Business Activity
             * associated with the thread.
             */
            final TransactionManager transactionManager = TransactionManagerFactory.transactionManager() ;
            final com.arjuna.mw.wst11.BusinessActivityManager businessActivityManager = BusinessActivityManagerFactory.businessActivityManager() ;

            final Context atContext ;
            if (transactionManager != null)
            {
                final TxContextImple txContext =
                    (TxContextImple)transactionManager.currentTransaction() ;
                atContext = (txContext == null ? null : txContext.context()) ;
            }
            else
            {
                atContext = null ;
            }

            final Context baContext ;
            if (businessActivityManager != null)
            {
                final com.arjuna.mwlabs.wst11.ba.context.TxContextImple txContext =
                    (com.arjuna.mwlabs.wst11.ba.context.TxContextImple)businessActivityManager.currentTransaction() ;
                baContext = (txContext == null ? null : txContext.context()) ;
            }
            else
            {
                baContext = null ;
            }

            final CoordinationContextType coordinationContext ;
            if (baContext != null)
            {
                coordinationContext = baContext.getCoordinationContext() ;
            }
            else if (atContext != null)
            {
                coordinationContext = atContext.getCoordinationContext() ;
            }
            else
            {
                coordinationContext = null ;
            }

            if (coordinationContext != null)
            {
                final SOAPEnvelope env = soapMessage.getSOAPPart().getEnvelope() ;
                SOAPHeader header = env.getHeader() ;
                if (header == null)
                {
                    header = env.addHeader() ;
                }
                final Name name = env.createName(CoordinationConstants.WSCOOR_ELEMENT_COORDINATION_CONTEXT, CoordinationConstants.WSCOOR_PREFIX, CoordinationConstants.WSCOOR_NAMESPACE) ;
                final SOAPHeaderElement headerElement = header.addHeaderElement(name) ;
                headerElement.addNamespaceDeclaration(CoordinationConstants.WSCOOR_PREFIX, CoordinationConstants.WSCOOR_NAMESPACE) ;
                headerElement.setMustUnderstand(mustUnderstand) ;
                CoordinationContextHelper.serialise(coordinationContext, headerElement) ;
            }
        }
        catch (final Throwable th) {
            wstxLogger.i18NLogger.warn_mw_wst11_client_JaxHC11P_1("com.arjuna.mw.wst11.client.JaxBaseHeaderContextProcessor.handleRequest()", th);
        }

        return true ;
    }

    /**
     * Resume the current transaction.
     *
     */

    protected void resumeTransaction(final SOAPMessage soapMessage)
    {
        if (soapMessage != null)
        {
            try
            {
                final SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope() ;
                final SOAPHeaderElement soapHeaderElement = getHeaderElement(soapEnvelope, CoordinationConstants.WSCOOR_NAMESPACE, CoordinationConstants.WSCOOR_ELEMENT_COORDINATION_CONTEXT) ;

                if (soapHeaderElement != null)
                {
                    final CoordinationContextType cc = CoordinationContextHelper.deserialise(soapHeaderElement) ;
                    if (cc != null)
                    {
                        final String coordinationType = cc.getCoordinationType() ;
                        if (AtomicTransactionConstants.WSAT_PROTOCOL.equals(coordinationType))
                        {
                            final TxContext txContext = new TxContextImple(cc) ;
                            TransactionManagerFactory.transactionManager().resume(txContext) ;
                        }
                        else if (BusinessActivityConstants.WSBA_PROTOCOL_ATOMIC_OUTCOME.equals(coordinationType))
                        {
                            final TxContext txContext = new com.arjuna.mwlabs.wst11.ba.context.TxContextImple(cc);
                            BusinessActivityManagerFactory.businessActivityManager().resume(txContext) ;
                        }
                        else {
                            wstxLogger.i18NLogger.warn_mw_wst11_client_JaxHC11P_2("com.arjuna.mw.wst11.client.JaxBaseHeaderContextProcessor.resumeTransaction()", coordinationType);
                        }
                    }
                }
            }
            catch (final Throwable th) {
                wstxLogger.i18NLogger.warn_mw_wst11_client_JaxHC11P_1("com.arjuna.mw.wst11.client.JaxBaseHeaderContextProcessor.resumeTransaction()", th);
            }
        }
    }

    /**
     * Retrieve the first header matching the uri and name.
     * @param soapEnvelope The soap envelope containing the header.
     * @param uri The uri of the header element.
     * @param name The name of the header element.
     * @return The header element or null if not found.
     */
    private SOAPHeaderElement getHeaderElement(final SOAPEnvelope soapEnvelope, final String uri, final String name)
        throws SOAPException
    {
        final SOAPHeader soapHeader = soapEnvelope.getHeader() ;
        if (soapHeader != null)
        {
            final Iterator headerIter = SOAPUtil.getChildElements(soapHeader) ;
            while(headerIter.hasNext())
            {
                final SOAPHeaderElement current = (SOAPHeaderElement)headerIter.next() ;
                final Name currentName = current.getElementName() ;
                if ((currentName != null) &&
                    match(name, currentName.getLocalName()) &&
                    match(uri, currentName.getURI()))
                {
                    return current ;
                }
            }
        }
        return null ;
    }

    /**
     * Do the two references match?
     * @param lhs The first reference.
     * @param rhs The second reference.
     * @return true if the references are both null or if they are equal.
     */
    private boolean match(final Object lhs, final Object rhs)
    {
        if (lhs == null)
        {
            return (rhs == null) ;
        }
        else
        {
            return lhs.equals(rhs) ;
        }
    }
}