/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */


package com.jboss.transaction.txinterop.webservices.atinterop.generated;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.jboss.transaction.txinterop.webservices.atinterop.generated package. 
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

    private final static QName _Readonly_QNAME = new QName("http://fabrikam123.com", "Readonly");
    private final static QName _Response_QNAME = new QName("http://fabrikam123.com", "Response");
    private final static QName _Commit_QNAME = new QName("http://fabrikam123.com", "Commit");
    private final static QName _VolatileAndDurable_QNAME = new QName("http://fabrikam123.com", "VolatileAndDurable");
    private final static QName _ReplayCommit_QNAME = new QName("http://fabrikam123.com", "ReplayCommit");
    private final static QName _PreparedAfterTimeout_QNAME = new QName("http://fabrikam123.com", "PreparedAfterTimeout");
    private final static QName _Phase2Rollback_QNAME = new QName("http://fabrikam123.com", "Phase2Rollback");
    private final static QName _EarlyAborted_QNAME = new QName("http://fabrikam123.com", "EarlyAborted");
    private final static QName _EarlyReadonly_QNAME = new QName("http://fabrikam123.com", "EarlyReadonly");
    private final static QName _Rollback_QNAME = new QName("http://fabrikam123.com", "Rollback");
    private final static QName _CompletionCommit_QNAME = new QName("http://fabrikam123.com", "CompletionCommit");
    private final static QName _RetryCommit_QNAME = new QName("http://fabrikam123.com", "RetryCommit");
    private final static QName _RetryPreparedCommit_QNAME = new QName("http://fabrikam123.com", "RetryPreparedCommit");
    private final static QName _RetryPreparedAbort_QNAME = new QName("http://fabrikam123.com", "RetryPreparedAbort");
    private final static QName _LostCommitted_QNAME = new QName("http://fabrikam123.com", "LostCommitted");
    private final static QName _CompletionRollback_QNAME = new QName("http://fabrikam123.com", "CompletionRollback");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.jboss.transaction.txinterop.webservices.atinterop.generated
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
    @XmlElementDecl(namespace = "http://fabrikam123.com", name = "Readonly")
    public JAXBElement<TestMessageType> createReadonly(TestMessageType value) {
        return new JAXBElement<TestMessageType>(_Readonly_QNAME, TestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com", name = "Response")
    public JAXBElement<TestMessageType> createResponse(TestMessageType value) {
        return new JAXBElement<TestMessageType>(_Response_QNAME, TestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com", name = "Commit")
    public JAXBElement<TestMessageType> createCommit(TestMessageType value) {
        return new JAXBElement<TestMessageType>(_Commit_QNAME, TestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com", name = "VolatileAndDurable")
    public JAXBElement<TestMessageType> createVolatileAndDurable(TestMessageType value) {
        return new JAXBElement<TestMessageType>(_VolatileAndDurable_QNAME, TestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com", name = "ReplayCommit")
    public JAXBElement<TestMessageType> createReplayCommit(TestMessageType value) {
        return new JAXBElement<TestMessageType>(_ReplayCommit_QNAME, TestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com", name = "PreparedAfterTimeout")
    public JAXBElement<TestMessageType> createPreparedAfterTimeout(TestMessageType value) {
        return new JAXBElement<TestMessageType>(_PreparedAfterTimeout_QNAME, TestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com", name = "Phase2Rollback")
    public JAXBElement<TestMessageType> createPhase2Rollback(TestMessageType value) {
        return new JAXBElement<TestMessageType>(_Phase2Rollback_QNAME, TestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com", name = "EarlyAborted")
    public JAXBElement<TestMessageType> createEarlyAborted(TestMessageType value) {
        return new JAXBElement<TestMessageType>(_EarlyAborted_QNAME, TestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com", name = "EarlyReadonly")
    public JAXBElement<TestMessageType> createEarlyReadonly(TestMessageType value) {
        return new JAXBElement<TestMessageType>(_EarlyReadonly_QNAME, TestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com", name = "Rollback")
    public JAXBElement<TestMessageType> createRollback(TestMessageType value) {
        return new JAXBElement<TestMessageType>(_Rollback_QNAME, TestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com", name = "CompletionCommit")
    public JAXBElement<String> createCompletionCommit(String value) {
        return new JAXBElement<String>(_CompletionCommit_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com", name = "RetryCommit")
    public JAXBElement<TestMessageType> createRetryCommit(TestMessageType value) {
        return new JAXBElement<TestMessageType>(_RetryCommit_QNAME, TestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com", name = "RetryPreparedCommit")
    public JAXBElement<TestMessageType> createRetryPreparedCommit(TestMessageType value) {
        return new JAXBElement<TestMessageType>(_RetryPreparedCommit_QNAME, TestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com", name = "RetryPreparedAbort")
    public JAXBElement<TestMessageType> createRetryPreparedAbort(TestMessageType value) {
        return new JAXBElement<TestMessageType>(_RetryPreparedAbort_QNAME, TestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestMessageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com", name = "LostCommitted")
    public JAXBElement<TestMessageType> createLostCommitted(TestMessageType value) {
        return new JAXBElement<TestMessageType>(_LostCommitted_QNAME, TestMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fabrikam123.com", name = "CompletionRollback")
    public JAXBElement<String> createCompletionRollback(String value) {
        return new JAXBElement<String>(_CompletionRollback_QNAME, String.class, null, value);
    }

}