/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.oasis_open.docs.ws_tx.wsat._2006._06;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.oasis_open.docs.ws_tx.wsat._2006._06 package. 
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

    private final static QName _Aborted_QNAME = new QName("http://docs.oasis-open.org/ws-tx/wsat/2006/06", "Aborted");
    private final static QName _Commit_QNAME = new QName("http://docs.oasis-open.org/ws-tx/wsat/2006/06", "Commit");
    private final static QName _ReadOnly_QNAME = new QName("http://docs.oasis-open.org/ws-tx/wsat/2006/06", "ReadOnly");
    private final static QName _Committed_QNAME = new QName("http://docs.oasis-open.org/ws-tx/wsat/2006/06", "Committed");
    private final static QName _Rollback_QNAME = new QName("http://docs.oasis-open.org/ws-tx/wsat/2006/06", "Rollback");
    private final static QName _Prepare_QNAME = new QName("http://docs.oasis-open.org/ws-tx/wsat/2006/06", "Prepare");
    private final static QName _Prepared_QNAME = new QName("http://docs.oasis-open.org/ws-tx/wsat/2006/06", "Prepared");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.oasis_open.docs.ws_tx.wsat._2006._06
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Notification }
     * 
     */
    public Notification createNotification() {
        return new Notification();
    }

    /**
     * Create an instance of {@link ATAssertion }
     * 
     */
    public ATAssertion createATAssertion() {
        return new ATAssertion();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Notification }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/ws-tx/wsat/2006/06", name = "Aborted")
    public JAXBElement<Notification> createAborted(Notification value) {
        return new JAXBElement<Notification>(_Aborted_QNAME, Notification.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Notification }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/ws-tx/wsat/2006/06", name = "Commit")
    public JAXBElement<Notification> createCommit(Notification value) {
        return new JAXBElement<Notification>(_Commit_QNAME, Notification.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Notification }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/ws-tx/wsat/2006/06", name = "ReadOnly")
    public JAXBElement<Notification> createReadOnly(Notification value) {
        return new JAXBElement<Notification>(_ReadOnly_QNAME, Notification.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Notification }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/ws-tx/wsat/2006/06", name = "Committed")
    public JAXBElement<Notification> createCommitted(Notification value) {
        return new JAXBElement<Notification>(_Committed_QNAME, Notification.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Notification }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/ws-tx/wsat/2006/06", name = "Rollback")
    public JAXBElement<Notification> createRollback(Notification value) {
        return new JAXBElement<Notification>(_Rollback_QNAME, Notification.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Notification }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/ws-tx/wsat/2006/06", name = "Prepare")
    public JAXBElement<Notification> createPrepare(Notification value) {
        return new JAXBElement<Notification>(_Prepare_QNAME, Notification.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Notification }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/ws-tx/wsat/2006/06", name = "Prepared")
    public JAXBElement<Notification> createPrepared(Notification value) {
        return new JAXBElement<Notification>(_Prepared_QNAME, Notification.class, null, value);
    }

}
