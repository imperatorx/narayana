/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.oasis_open.docs.ws_tx.wscoor._2006._06;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.oasis_open.docs.ws_tx.wscoor._2006._06 package. 
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

    private final static QName _Register_QNAME = new QName("http://docs.oasis-open.org/ws-tx/wscoor/2006/06", "Register");
    private final static QName _RegisterResponse_QNAME = new QName("http://docs.oasis-open.org/ws-tx/wscoor/2006/06", "RegisterResponse");
    private final static QName _CreateCoordinationContext_QNAME = new QName("http://docs.oasis-open.org/ws-tx/wscoor/2006/06", "CreateCoordinationContext");
    private final static QName _CreateCoordinationContextResponse_QNAME = new QName("http://docs.oasis-open.org/ws-tx/wscoor/2006/06", "CreateCoordinationContextResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.oasis_open.docs.ws_tx.wscoor._2006._06
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Expires }
     * 
     */
    public Expires createExpires() {
        return new Expires();
    }

    /**
     * Create an instance of {@link CreateCoordinationContextType.CurrentContext }
     * 
     */
    public CreateCoordinationContextType.CurrentContext createCreateCoordinationContextTypeCurrentContext() {
        return new CreateCoordinationContextType.CurrentContext();
    }

    /**
     * Create an instance of {@link CoordinationContextType.Identifier }
     * 
     */
    public CoordinationContextType.Identifier createCoordinationContextTypeIdentifier() {
        return new CoordinationContextType.Identifier();
    }

    /**
     * Create an instance of {@link RegisterResponseType }
     * 
     */
    public RegisterResponseType createRegisterResponseType() {
        return new RegisterResponseType();
    }

    /**
     * Create an instance of {@link CoordinationContext }
     * 
     */
    public CoordinationContext createCoordinationContext() {
        return new CoordinationContext();
    }

    /**
     * Create an instance of {@link CreateCoordinationContextType }
     * 
     */
    public CreateCoordinationContextType createCreateCoordinationContextType() {
        return new CreateCoordinationContextType();
    }

    /**
     * Create an instance of {@link CreateCoordinationContextResponseType }
     * 
     */
    public CreateCoordinationContextResponseType createCreateCoordinationContextResponseType() {
        return new CreateCoordinationContextResponseType();
    }

    /**
     * Create an instance of {@link RegisterType }
     * 
     */
    public RegisterType createRegisterType() {
        return new RegisterType();
    }

    /**
     * Create an instance of {@link CoordinationContextType }
     * 
     */
    public CoordinationContextType createCoordinationContextType() {
        return new CoordinationContextType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegisterType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/ws-tx/wscoor/2006/06", name = "Register")
    public JAXBElement<RegisterType> createRegister(RegisterType value) {
        return new JAXBElement<RegisterType>(_Register_QNAME, RegisterType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegisterResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/ws-tx/wscoor/2006/06", name = "RegisterResponse")
    public JAXBElement<RegisterResponseType> createRegisterResponse(RegisterResponseType value) {
        return new JAXBElement<RegisterResponseType>(_RegisterResponse_QNAME, RegisterResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateCoordinationContextType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/ws-tx/wscoor/2006/06", name = "CreateCoordinationContext")
    public JAXBElement<CreateCoordinationContextType> createCreateCoordinationContext(CreateCoordinationContextType value) {
        return new JAXBElement<CreateCoordinationContextType>(_CreateCoordinationContext_QNAME, CreateCoordinationContextType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateCoordinationContextResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/ws-tx/wscoor/2006/06", name = "CreateCoordinationContextResponse")
    public JAXBElement<CreateCoordinationContextResponseType> createCreateCoordinationContextResponse(CreateCoordinationContextResponseType value) {
        return new JAXBElement<CreateCoordinationContextResponseType>(_CreateCoordinationContextResponse_QNAME, CreateCoordinationContextResponseType.class, null, value);
    }

}
