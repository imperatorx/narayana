/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.mw.wst11.service;

import com.arjuna.mw.wst11.common.CoordinationContextHelper;
import com.arjuna.mw.wst.common.SOAPUtil;
import com.arjuna.mw.wst.TxContext;
import com.arjuna.mw.wst11.*;
import com.arjuna.mw.wstx.logging.wstxLogger;
import com.arjuna.webservices11.wsat.AtomicTransactionConstants;
import com.arjuna.webservices11.wsba.BusinessActivityConstants;
import com.arjuna.webservices11.wscoor.CoordinationConstants;
import com.arjuna.mwlabs.wst11.at.context.TxContextImple;
import com.arjuna.mwlabs.wst11.at.SubordinateImporter;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CoordinationContextType;

import jakarta.xml.soap.*;
import java.util.Iterator;

/**
 * Common base class for classes used to perform
 * WS-Transaction context manipulation on SOAP messages.
 *
 */
class JaxBaseHeaderContextProcessor
{
    /**
     * Handle the request.
     * @param soapMessage The current message context.
     */
    protected boolean handleInboundMessage(final SOAPMessage soapMessage)
    {
        return handleInboundMessage(soapMessage, false);
    }

    /**
     * Handle the request.
     * @param soapMessage The current message context.
     * @param installSubordinateTx true if a subordinate transaction should be interposed and false
     * if the handler should just resume the incoming transaction. currently only works for AT
     * transactions but will eventually be extended to work for BA transactions too.
     */
    protected boolean handleInboundMessage(final SOAPMessage soapMessage, boolean installSubordinateTx)
    {
        if (soapMessage != null)
        {
            try
            {
                final SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope() ;
                final SOAPHeader soapHeader = soapEnvelope.getHeader() ;
                final SOAPHeaderElement soapHeaderElement = getHeaderElement(soapHeader, CoordinationConstants.WSCOOR_NAMESPACE, CoordinationConstants.WSCOOR_ELEMENT_COORDINATION_CONTEXT) ;

                if (soapHeaderElement != null)
                {
                    final CoordinationContextType cc = CoordinationContextHelper.deserialise(soapHeaderElement) ;
                    final String coordinationType = cc.getCoordinationType();
                    if (AtomicTransactionConstants.WSAT_PROTOCOL.equals(coordinationType))
                    {
                        clearMustUnderstand(soapHeader, soapHeaderElement) ;
                        TxContext txContext = new TxContextImple(cc) ;
                        if (installSubordinateTx) {
                            txContext = SubordinateImporter.importContext(cc);
                        }
                        TransactionManagerFactory.transactionManager().resume(txContext);
                    }
                    else if (BusinessActivityConstants.WSBA_PROTOCOL_ATOMIC_OUTCOME.equals(coordinationType))
                    {
                        // interposition is not yet implemented for business activities
                        clearMustUnderstand(soapHeader, soapHeaderElement) ;
                        TxContext txContext = new com.arjuna.mwlabs.wst11.ba.context.TxContextImple(cc);
                        if (installSubordinateTx) {
                            txContext = com.arjuna.mwlabs.wst11.ba.SubordinateImporter.importContext(cc);
                        }
                        BusinessActivityManagerFactory.businessActivityManager().resume(txContext) ;
                    }
                    else {
                        wstxLogger.i18NLogger.warn_mw_wst11_service_JaxHC11P_2("com.arjuna.mw.wst11.service.JaxBaseHeaderContextProcessor.handleRequest(MessageContext context)", coordinationType);
                    }
                }
            }
            catch (final Throwable th) {
                wstxLogger.i18NLogger.warn_mw_wst11_service_JaxHC11P_1("com.arjuna.mw.wst11.service.JaxBaseHeaderContextProcessor.handleRequest(MessageContext context)", th);
            }
        }
        return true ;
    }

    /**
     * Suspend the current transaction.
     */
    protected void suspendTransaction()
    {
        try
        {
            /*
             * There should either be an Atomic Transaction *or* a Business Activity
             * associated with the thread.
             */
            final TransactionManager transactionManager = TransactionManagerFactory.transactionManager() ;
            final BusinessActivityManager businessActivityManager = BusinessActivityManagerFactory.businessActivityManager() ;

            if (transactionManager != null)
            {
                transactionManager.suspend() ;
            }

            if (businessActivityManager != null)
            {
                businessActivityManager.suspend() ;
            }
        }
        catch (final Throwable th) {
            wstxLogger.i18NLogger.warn_mw_wst11_service_JaxHC11P_1("com.arjuna.mw.wst11.service.JaxBaseHeaderContextProcessor.suspendTransaction()", th);
        }
    }

    /**
     * Clear the soap MustUnderstand.
     * @param soapHeader The SOAP header.
     * @param soapHeaderElement The SOAP header element.
     */
    private void clearMustUnderstand(final SOAPHeader soapHeader, final SOAPHeaderElement soapHeaderElement)
            throws SOAPException
    {
        final Name headerName = soapHeader.getElementName() ;

        final SOAPFactory factory = SOAPFactory.newInstance() ;
        final Name attributeName = factory.createName("mustUnderstand", headerName.getPrefix(), headerName.getURI()) ;

        soapHeaderElement.removeAttribute(attributeName) ;
    }

    private SOAPHeaderElement getHeaderElement(final SOAPHeader soapHeader, final String uri, final String name)
            throws SOAPException
    {
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