/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.wst11.messaging;

import com.arjuna.schemas.ws._2005._10.wsarjtx.NotificationType;
import com.arjuna.webservices.SoapFaultType;
import com.arjuna.webservices.base.processors.ActivatedObjectProcessor;
import com.arjuna.webservices.logging.WSTLogger;
import com.arjuna.webservices.wsarjtx.ArjunaTXConstants;
import com.arjuna.webservices11.wsarj.ArjunaContext;
import com.arjuna.webservices11.wsarj.InstanceIdentifier;
import com.arjuna.webservices11.wsarjtx.processors.TerminationCoordinatorProcessor;
import com.arjuna.webservices11.wsarjtx.processors.TerminationCoordinatorRPCProcessor;
import com.arjuna.wsc11.messaging.MessageId;
import com.arjuna.wst.FaultedException;
import com.arjuna.wst.SystemException;
import com.arjuna.wst.TransactionRolledBackException;
import com.arjuna.wst.UnknownTransactionException;
import com.arjuna.wst11.BusinessActivityTerminator;
import org.jboss.ws.api.addressing.MAP;

import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFactory;
import jakarta.xml.soap.SOAPFault;
import jakarta.xml.ws.ProtocolException;
import jakarta.xml.ws.soap.SOAPFaultException;
import jakarta.xml.ws.wsaddressing.W3CEndpointReference;

/**
 * The Terminator Participant processor.
 * @author kevin
 *
 */
public class TerminationCoordinatorRPCProcessorImpl extends TerminationCoordinatorRPCProcessor
{
    /**
     * Cancel.
     * @param cancel The cancel notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     */
    public void cancel(final NotificationType cancel, final MAP map, final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
        final BusinessActivityTerminator participant = TerminationCoordinatorProcessor.getProcessor().getParticipant(instanceIdentifier) ;

        try
        {
            if (participant != null)
            {
                try
                {
                    participant.cancel() ;
                }
                catch (FaultedException fm)
                {
                    SOAPFactory factory = SOAPFactory.newInstance();
                    SOAPFault soapFault = factory.createFault(SoapFaultType.FAULT_RECEIVER.getValue(), ArjunaTXConstants.FAULTED_ERROR_CODE_QNAME);
                    throw new SOAPFaultException(soapFault);
                }
                catch (final UnknownTransactionException ute)
                {
                    SOAPFactory factory = SOAPFactory.newInstance();
                    SOAPFault soapFault = factory.createFault(SoapFaultType.FAULT_SENDER.getValue(), ArjunaTXConstants.UNKNOWNTRANSACTION_ERROR_CODE_QNAME);
                    soapFault.addDetail().addDetailEntry(ArjunaTXConstants.UNKNOWNTRANSACTION_ERROR_CODE_QNAME).addTextNode(WSTLogger.i18NLogger.get_wst11_messaging_TerminationCoordinatorProcessorImpl_1());
                    throw new SOAPFaultException(soapFault);
                }
                catch (final SystemException se)
                {
                    SOAPFactory factory = SOAPFactory.newInstance();
                    SOAPFault soapFault = factory.createFault(SoapFaultType.FAULT_RECEIVER.getValue(), ArjunaTXConstants.UNKNOWNERROR_ERROR_CODE_QNAME);
                    soapFault.addDetail().addDetailEntry(ArjunaTXConstants.UNKNOWNERROR_ERROR_CODE_QNAME).addTextNode(WSTLogger.i18NLogger.get_wst11_messaging_TerminationCoordinatorProcessorImpl_2());
                    throw new SOAPFaultException(soapFault);
                }
                catch (final Throwable th)
                {
                    if (WSTLogger.logger.isTraceEnabled())
                    {
                        WSTLogger.logger.tracev("Unexpected exception thrown from close:", th) ;
                    }
                    SOAPFactory factory = SOAPFactory.newInstance();
                    SOAPFault soapFault = factory.createFault(SoapFaultType.FAULT_RECEIVER.getValue(), ArjunaTXConstants.UNKNOWNERROR_ERROR_CODE_QNAME);
                    soapFault.addDetail().addDetailEntry(ArjunaTXConstants.UNKNOWNERROR_ERROR_CODE_QNAME).addTextNode(WSTLogger.i18NLogger.get_wst11_messaging_TerminationCoordinatorProcessorImpl_2());
                    throw new SOAPFaultException(soapFault);
                }
            }
            else
            {
                if (WSTLogger.logger.isTraceEnabled())
                {
                    WSTLogger.logger.tracev("Cancel called on unknown participant: {0}", new Object[] {instanceIdentifier}) ;
                }
                SOAPFactory factory = SOAPFactory.newInstance();
                SOAPFault soapFault = factory.createFault(SoapFaultType.FAULT_SENDER.getValue(), ArjunaTXConstants.UNKNOWNTRANSACTION_ERROR_CODE_QNAME);
                soapFault.addDetail().addDetailEntry(ArjunaTXConstants.UNKNOWNTRANSACTION_ERROR_CODE_QNAME).addTextNode(WSTLogger.i18NLogger.get_wst11_messaging_TerminationCoordinatorProcessorImpl_5());
                throw new SOAPFaultException(soapFault);
            }
        }
        catch (SOAPException se)
        {
            se.printStackTrace(System.err);
            throw new ProtocolException(se);
        }
    }

    /**
     * Close.
     * @param close The close notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     */
    public void close(final NotificationType close, final MAP map, final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
        final BusinessActivityTerminator participant = TerminationCoordinatorProcessor.getProcessor().getParticipant(instanceIdentifier) ;

        try
        {
            if (participant != null)
            {
                try
                {
                    participant.close() ;
                }
                catch (final UnknownTransactionException ute)
                {
                    SOAPFactory factory = SOAPFactory.newInstance();
                    SOAPFault soapFault = factory.createFault(SoapFaultType.FAULT_SENDER.getValue(), ArjunaTXConstants.UNKNOWNTRANSACTION_ERROR_CODE_QNAME);
                    soapFault.addDetail().addDetailEntry(ArjunaTXConstants.UNKNOWNTRANSACTION_ERROR_CODE_QNAME).addTextNode(WSTLogger.i18NLogger.get_wst11_messaging_TerminationCoordinatorProcessorImpl_6());
                    throw new SOAPFaultException(soapFault);
                }
                catch (final TransactionRolledBackException trbe)
                {
                    SOAPFactory factory = SOAPFactory.newInstance();
                    SOAPFault soapFault = factory.createFault(SoapFaultType.FAULT_SENDER.getValue(), ArjunaTXConstants.UNKNOWNTRANSACTION_ERROR_CODE_QNAME);
                    soapFault.addDetail().addDetailEntry(ArjunaTXConstants.TRANSACTIONROLLEDBACK_ERROR_CODE_QNAME).addTextNode(WSTLogger.i18NLogger.get_wst11_messaging_TerminationCoordinatorProcessorImpl_7());
                    throw new SOAPFaultException(soapFault);
                }
                catch (final SystemException se)
                {
                    SOAPFactory factory = SOAPFactory.newInstance();
                    SOAPFault soapFault = factory.createFault(SoapFaultType.FAULT_RECEIVER.getValue(), ArjunaTXConstants.UNKNOWNERROR_ERROR_CODE_QNAME);
                    soapFault.addDetail().addDetailEntry(ArjunaTXConstants.UNKNOWNERROR_ERROR_CODE_QNAME).addTextNode(WSTLogger.i18NLogger.get_wst11_messaging_TerminationCoordinatorProcessorImpl_8());
                    throw new SOAPFaultException(soapFault);
                }
                catch (final Throwable th)
                {
                    if (WSTLogger.logger.isTraceEnabled())
                    {
                        WSTLogger.logger.tracev("Unexpected exception thrown from close:", th) ;
                    }
                    SOAPFactory factory = SOAPFactory.newInstance();
                    SOAPFault soapFault = factory.createFault(SoapFaultType.FAULT_RECEIVER.getValue(), ArjunaTXConstants.UNKNOWNERROR_ERROR_CODE_QNAME);
                    soapFault.addDetail().addDetailEntry(ArjunaTXConstants.UNKNOWNERROR_ERROR_CODE_QNAME).addTextNode(WSTLogger.i18NLogger.get_wst11_messaging_TerminationCoordinatorProcessorImpl_8());
                    throw new SOAPFaultException(soapFault);
                }
            }
            else
            {
                if (WSTLogger.logger.isTraceEnabled())
                {
                    WSTLogger.logger.tracev("Close called on unknown participant: {0}", new Object[] {instanceIdentifier}) ;
                }
                SOAPFactory factory = SOAPFactory.newInstance();
                SOAPFault soapFault = factory.createFault(SoapFaultType.FAULT_SENDER.getValue(), ArjunaTXConstants.UNKNOWNTRANSACTION_ERROR_CODE_QNAME);
                soapFault.addDetail().addDetailEntry(ArjunaTXConstants.UNKNOWNTRANSACTION_ERROR_CODE_QNAME).addTextNode(WSTLogger.i18NLogger.get_wst11_messaging_TerminationCoordinatorProcessorImpl_6());
                throw new SOAPFaultException(soapFault);
            }
        } catch (SOAPException se) {
            se.printStackTrace(System.err);
            throw new ProtocolException(se);
        }
    }

    /**
     * Complete.
     * @param complete The complete notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     */
    public void complete(final NotificationType complete, final MAP map,
        final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
        final BusinessActivityTerminator participant = TerminationCoordinatorProcessor.getProcessor().getParticipant(instanceIdentifier) ;

        try {
            if (participant != null)
            {
                W3CEndpointReference endpoint = participant.getEndpoint();

                final String messageId = MessageId.getMessageId() ;
                try
                {
                    participant.complete() ;
                }
                catch (FaultedException fm)
                {
                    SOAPFactory factory = SOAPFactory.newInstance();
                    SOAPFault soapFault = factory.createFault(SoapFaultType.FAULT_RECEIVER.getValue(), ArjunaTXConstants.FAULTED_ERROR_CODE_QNAME);
                    throw new SOAPFaultException(soapFault);
                }
                catch (final UnknownTransactionException ute)
                {
                    SOAPFactory factory = SOAPFactory.newInstance();
                    SOAPFault soapFault = factory.createFault(SoapFaultType.FAULT_SENDER.getValue(), ArjunaTXConstants.UNKNOWNTRANSACTION_ERROR_CODE_QNAME);
                    soapFault.addDetail().addDetailEntry(ArjunaTXConstants.UNKNOWNTRANSACTION_ERROR_CODE_QNAME).addTextNode(WSTLogger.i18NLogger.get_wst11_messaging_TerminationCoordinatorProcessorImpl_12());
                    throw new SOAPFaultException(soapFault);
                }
                catch (final SystemException se)
                {
                    SOAPFactory factory = SOAPFactory.newInstance();
                    SOAPFault soapFault = factory.createFault(SoapFaultType.FAULT_RECEIVER.getValue(), ArjunaTXConstants.UNKNOWNERROR_ERROR_CODE_QNAME);
                    soapFault.addDetail().addDetailEntry(ArjunaTXConstants.UNKNOWNERROR_ERROR_CODE_QNAME).addTextNode(WSTLogger.i18NLogger.get_wst11_messaging_TerminationCoordinatorProcessorImpl_13());
                    throw new SOAPFaultException(soapFault);
                }
                catch (final Throwable th)
                {
                    if (WSTLogger.logger.isTraceEnabled())
                    {
                        WSTLogger.logger.tracev("Unexpected exception thrown from close:", th) ;
                    }
                    SOAPFactory factory = SOAPFactory.newInstance();
                    SOAPFault soapFault = factory.createFault(SoapFaultType.FAULT_RECEIVER.getValue(), ArjunaTXConstants.UNKNOWNERROR_ERROR_CODE_QNAME);
                    soapFault.addDetail().addDetailEntry(ArjunaTXConstants.UNKNOWNERROR_ERROR_CODE_QNAME).addTextNode(WSTLogger.i18NLogger.get_wst11_messaging_TerminationCoordinatorProcessorImpl_13());
                    throw new SOAPFaultException(soapFault);
                }
            }
            else
            {
                if (WSTLogger.logger.isTraceEnabled())
                {
                    WSTLogger.logger.tracev("Complete called on unknown participant: {0}", new Object[] {instanceIdentifier}) ;
                }
                SOAPFactory factory = SOAPFactory.newInstance();
                SOAPFault soapFault = factory.createFault(SoapFaultType.FAULT_SENDER.getValue(), ArjunaTXConstants.UNKNOWNTRANSACTION_ERROR_CODE_QNAME);
                soapFault.addDetail().addDetailEntry(ArjunaTXConstants.UNKNOWNTRANSACTION_ERROR_CODE_QNAME).addTextNode(WSTLogger.i18NLogger.get_wst11_messaging_TerminationCoordinatorProcessorImpl_12());
                throw new SOAPFaultException(soapFault);
            }
        } catch (SOAPException se) {
            se.printStackTrace(System.err);
            throw new ProtocolException(se);
        }
    }
}