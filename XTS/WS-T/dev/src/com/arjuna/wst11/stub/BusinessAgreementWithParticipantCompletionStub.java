/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.wst11.stub;

import java.io.StringWriter;
import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import jakarta.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.transform.stream.StreamSource;

import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputObjectState;
import com.arjuna.webservices.logging.WSTLogger;
import com.arjuna.webservices.soap.SoapUtils;
import com.arjuna.webservices11.wsba.State;
import com.arjuna.webservices11.wsba.processors.ParticipantCompletionCoordinatorProcessor;
import com.arjuna.webservices11.util.StreamHelper;
import com.arjuna.wst.BusinessAgreementWithParticipantCompletionParticipant;
import com.arjuna.wst.FaultedException;
import com.arjuna.wst.PersistableParticipant;
import com.arjuna.wst.SystemException;
import com.arjuna.wst.WrongStateException;
import com.arjuna.wst11.messaging.engines.ParticipantCompletionCoordinatorEngine;

public class BusinessAgreementWithParticipantCompletionStub implements BusinessAgreementWithParticipantCompletionParticipant, PersistableParticipant
{
    private static final QName QNAME_BAPCWS_PARTICIPANT = new QName("bapcwsParticipant") ;

    private ParticipantCompletionCoordinatorEngine participant ;

    public BusinessAgreementWithParticipantCompletionStub(final ParticipantCompletionCoordinatorEngine participant)
        throws Exception
    {
        this.participant = participant ;
    }

    /**
     * constructor for use during recovery
     */
    public BusinessAgreementWithParticipantCompletionStub()
    {
        this.participant = null ;
    }

    public synchronized void close ()
        throws WrongStateException, SystemException
    {
        /*
         * Active -> illegal state
         * Canceling -> illegal state
         * Completed -> illegal state
         * Closing -> no response
         * Compensating -> illegal state
         * Faulting -> illegal state
         * Faulting-Active -> illegal state
         * Faulting-Compensating -> illegal state
         * Exiting -> illegal state
         * Ended -> ended
         */
        final State state = participant.close() ;

        if (state == State.STATE_CLOSING)
        {
            throw new SystemException() ;
        }
        else if (state != State.STATE_ENDED)
        {
            throw new WrongStateException() ;
        }
    }

    public synchronized void cancel ()
        throws FaultedException, WrongStateException, SystemException
    {
        /*
         * Active -> illegal state
         * Canceling -> no response
         * Completed -> illegal state
         * Closing -> illegal state
         * Compensating -> illegal state
         * Faulting -> illegal state
         * Faulting-Active -> illegal state
         * Faulting-Compensating -> illegal state
         * Exiting -> illegal state
         * Ended -> ended
         */
        final State state = participant.cancel() ;

        if (state == State.STATE_COMPLETED)
        {
            //Complete arrived, whilst the coordinator was trying to abort.
            compensate();
        }
        else if (state == State.STATE_CANCELING)
        {
            throw new SystemException() ;
        }
        else if (state == State.STATE_FAILING_CANCELING)
        {
            throw new FaultedException() ;
        }
        else if (state != State.STATE_ENDED)
        {
            throw new WrongStateException() ;
        }
    }

    public synchronized void compensate ()
        throws FaultedException, WrongStateException, SystemException
    {
        /*
         * Active -> illegal state
         * Canceling -> illegal state
         * Completed -> illegal state
         * Closing -> illegal state
         * Compensating -> no answer
         * Faulting -> illegal state
         * Faulting-Active -> illegal state
         * Faulting-Compensating -> fault
         * Exiting -> illegal state
         * Ended -> ended
         */
        final State state = participant.compensate() ;
        if (state == State.STATE_COMPENSATING)
        {
            throw new SystemException() ;
        }
        else if (state == State.STATE_FAILING_COMPENSATING)
        {
            throw new FaultedException() ;
        }
        else if (state != State.STATE_ENDED)
        {
            throw new WrongStateException() ;
        }
    }

    public String status ()
        throws SystemException
    {
        final State state = participant.getStatus() ;
        return (state == null ? null : state.getValue().getLocalPart()) ;
    }

    public void unknown ()
        throws SystemException
    {
        error() ;
    }

    public synchronized void error ()
        throws SystemException
    {
        participant.cancel() ;
    }

    public boolean saveState(final OutputObjectState oos)
    {
        try
        {
            oos.packString(participant.getId()) ;

            // n.b. just use toString() for the endpoint -- it uses the writeTo() method which calls a suitable marshaller
            final StringWriter sw = new StringWriter() ;
            final XMLStreamWriter writer = SoapUtils.getXMLStreamWriter(sw) ;
            StreamHelper.writeStartElement(writer, QNAME_BAPCWS_PARTICIPANT) ;
            String eprefText = participant.getParticipant().toString();
            writer.writeCData(eprefText);
            StreamHelper.writeEndElement(writer, null, null) ;
            writer.close() ;

            oos.packString(sw.toString()) ;

            final State state = participant.getStatus();
            final QName stateName = state.getValue();
            final String ns = stateName.getNamespaceURI();
            final String localPart = stateName.getLocalPart();
            final String prefix = stateName.getPrefix();
            oos.packString(ns != null ? ns : "");
            oos.packString(localPart != null ? localPart : "");
            oos.packString(prefix != null ? prefix : "");

            return true ;
        }
        catch (final Throwable th)
        {
            WSTLogger.i18NLogger.error_wst11_stub_BusinessAgreementWithParticipantCompletionStub_2(th);
            return false ;
        }
    }

    public boolean restoreState(final InputObjectState ios)
    {
        try
        {
            final String id = ios.unpackString() ;
            final String eprValue = ios.unpackString() ;

            // this should successfully reverse the save process
            final XMLStreamReader reader = SoapUtils.getXMLStreamReader(new StringReader(eprValue)) ;
            StreamHelper.checkNextStartTag(reader, QNAME_BAPCWS_PARTICIPANT) ;
            String eprefText = reader.getElementText();
            StreamSource source = new StreamSource(new StringReader(eprefText));
            final W3CEndpointReference endpointReference = new W3CEndpointReference(source);

            String ns = ios.unpackString();
            final String localPart = ios.unpackString();
            String prefix = ios.unpackString();
            if ("".equals(ns)) {
                ns = null;
            }
            if ("".equals(prefix)) {
                prefix = null;
            }

            QName statename = new QName(ns, localPart, prefix);
            State state = State.toState11(statename);

            // if we already have an engine from a previous recovery scan or because
            // we had a heuristic outcome then reuse it with luck it will have been committed
            // or aborted between the last scan and this one
            // note that whatever happens it will not have been removed from the table
            // because it is marked as recovered
            participant = (ParticipantCompletionCoordinatorEngine) ParticipantCompletionCoordinatorProcessor.getProcessor().getCoordinator(id);
            if (participant == null) {
                participant = new ParticipantCompletionCoordinatorEngine(id, endpointReference, state, true);
            }
            return true ;
        }
        catch (final Throwable th)
        {
            WSTLogger.i18NLogger.error_wst11_stub_BusinessAgreementWithParticipantCompletionStub_3(th);
            return false ;
        }
    }
}