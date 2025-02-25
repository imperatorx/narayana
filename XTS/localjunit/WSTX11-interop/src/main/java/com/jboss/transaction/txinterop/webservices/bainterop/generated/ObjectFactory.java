/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */


package com.jboss.transaction.txinterop.webservices.bainterop.generated;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.jboss.transaction.txinterop.webservices.bainterop.generated package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ParticipantCompleteClose_QNAME = new QName("http://fabrikam123.com/wsba", "ParticipantCompleteClose");
    private final static QName _MixedOutcome_QNAME = new QName("http://fabrikam123.com/wsba", "MixedOutcome");
    private final static QName _Response_QNAME = new QName("http://fabrikam123.com/wsba", "Response");
    private final static QName _Fail_QNAME = new QName("http://fabrikam123.com/wsba", "Fail");
    private final static QName _UnsolicitedComplete_QNAME = new QName("http://fabrikam123.com/wsba", "UnsolicitedComplete");
    private final static QName _CannotComplete_QNAME = new QName("http://fabrikam123.com/wsba", "CannotComplete");
    private final static QName _Compensate_QNAME = new QName("http://fabrikam123.com/wsba", "Compensate");
    private final static QName _Exit_QNAME = new QName("http://fabrikam123.com/wsba", "Exit");
    private final static QName _CoordinatorCompleteClose_QNAME = new QName("http://fabrikam123.com/wsba", "CoordinatorCompleteClose");
    private final static QName _MessageLossAndRecovery_QNAME = new QName("http://fabrikam123.com/wsba", "MessageLossAndRecovery");
    private final static QName _ParticipantCancelCompletedRace_QNAME = new QName("http://fabrikam123.com/wsba", "ParticipantCancelCompletedRace");
    private final static QName _Cancel_QNAME = new QName("http://fabrikam123.com/wsba", "Cancel");
    private final static QName _CompensationFail_QNAME = new QName("http://fabrikam123.com/wsba", "CompensationFail");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.jboss.transaction.txinterop.webservices.bainterop.generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TestMessageType }
     * 
     */
    public TestMessageType createTestMessageType() {
        return new TestMessageType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com/wsba", name = "ParticipantCompleteClose")
    public JAXBElement<TestMessageType> createParticipantCompleteClose(TestMessageType value) {
        return new JAXBElement<TestMessageType>(_ParticipantCompleteClose_QNAME, TestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com/wsba", name = "MixedOutcome")
    public JAXBElement<TestMessageType> createMixedOutcome(TestMessageType value) {
        return new JAXBElement<TestMessageType>(_MixedOutcome_QNAME, TestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com/wsba", name = "Response")
    public JAXBElement<TestMessageType> createResponse(TestMessageType value) {
        return new JAXBElement<TestMessageType>(_Response_QNAME, TestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com/wsba", name = "Fail")
    public JAXBElement<TestMessageType> createFail(TestMessageType value) {
        return new JAXBElement<TestMessageType>(_Fail_QNAME, TestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com/wsba", name = "UnsolicitedComplete")
    public JAXBElement<TestMessageType> createUnsolicitedComplete(TestMessageType value) {
        return new JAXBElement<TestMessageType>(_UnsolicitedComplete_QNAME, TestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com/wsba", name = "CannotComplete")
    public JAXBElement<TestMessageType> createCannotComplete(TestMessageType value) {
        return new JAXBElement<TestMessageType>(_CannotComplete_QNAME, TestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com/wsba", name = "Compensate")
    public JAXBElement<TestMessageType> createCompensate(TestMessageType value) {
        return new JAXBElement<TestMessageType>(_Compensate_QNAME, TestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com/wsba", name = "Exit")
    public JAXBElement<TestMessageType> createExit(TestMessageType value) {
        return new JAXBElement<TestMessageType>(_Exit_QNAME, TestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com/wsba", name = "CoordinatorCompleteClose")
    public JAXBElement<TestMessageType> createCoordinatorCompleteClose(TestMessageType value) {
        return new JAXBElement<TestMessageType>(_CoordinatorCompleteClose_QNAME, TestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com/wsba", name = "MessageLossAndRecovery")
    public JAXBElement<TestMessageType> createMessageLossAndRecovery(TestMessageType value) {
        return new JAXBElement<TestMessageType>(_MessageLossAndRecovery_QNAME, TestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com/wsba", name = "ParticipantCancelCompletedRace")
    public JAXBElement<TestMessageType> createParticipantCancelCompletedRace(TestMessageType value) {
        return new JAXBElement<TestMessageType>(_ParticipantCancelCompletedRace_QNAME, TestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com/wsba", name = "Cancel")
    public JAXBElement<TestMessageType> createCancel(TestMessageType value) {
        return new JAXBElement<TestMessageType>(_Cancel_QNAME, TestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com/wsba", name = "CompensationFail")
    public JAXBElement<TestMessageType> createCompensationFail(TestMessageType value) {
        return new JAXBElement<TestMessageType>(_CompensationFail_QNAME, TestMessageType.class, null, value);
    }

}