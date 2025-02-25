/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.jboss.jbossts.xts.servicetests.generated;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.jboss.jbossts.xts.servicetests.generated package. 
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

    private final static QName _Commands_QNAME = new QName("http://jbossts.jboss.org/xts/servicetests/generated", "commands");
    private final static QName _Results_QNAME = new QName("http://jbossts.jboss.org/xts/servicetests/generated", "results");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.jboss.jbossts.xts.servicetests.generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CommandsType }
     * 
     */
    public CommandsType createCommandsType() {
        return new CommandsType();
    }

    /**
     * Create an instance of {@link ResultsType }
     * 
     */
    public ResultsType createResultsType() {
        return new ResultsType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CommandsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://jbossts.jboss.org/xts/servicetests/generated", name = "commands")
    public JAXBElement<CommandsType> createCommands(CommandsType value) {
        return new JAXBElement<CommandsType>(_Commands_QNAME, CommandsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResultsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://jbossts.jboss.org/xts/servicetests/generated", name = "results")
    public JAXBElement<ResultsType> createResults(ResultsType value) {
        return new JAXBElement<ResultsType>(_Results_QNAME, ResultsType.class, null, value);
    }

}
