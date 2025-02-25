/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.webservices11.wsarj;

import org.w3c.dom.Element;

import jakarta.xml.soap.Name;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFactory;
import jakarta.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import com.arjuna.webservices.wsarj.ArjunaConstants;
import org.jboss.ws.api.addressing.MAPEndpoint;

/**
 * Representation of an InstanceIdentifier element.
 * @author kevin
 */
public class InstanceIdentifier
{
    /**
     * The instance identifier.
     */
    private String instanceIdentifier ;

    /**
      * Default constructor.
      */
     public InstanceIdentifier()
     {
     }

    /**
     * Construct an instance identifier with the specific identifier
     * @param instanceIdentifier The instance identifier.
     */

    public InstanceIdentifier(final String instanceIdentifier)
    {
        this.instanceIdentifier = instanceIdentifier ;
    }

    /**
     * Set the instance identifier of this element.
     * @param instanceIdentifier The instance identifier of the element.
     */
    public void setInstanceIdentifier(final String instanceIdentifier)
    {
        this.instanceIdentifier = instanceIdentifier ;
    }

    /**
     * Get the instance identifier of this element.
     * @return The instance identifier of the element or null if not set.
     */
    public String getInstanceIdentifier()
    {
        return instanceIdentifier ;
    }

    /**
     * Is the configuration of this element valid?
     * @return true if valid, false otherwise.
     */
    public boolean isValid()
    {
        return (instanceIdentifier != null) && (instanceIdentifier.trim().length() > 0);
    }

    /**
     * Get a string representation of this instance identifier.
     * @return the string representation.
     */
    public String toString()
    {
        return (instanceIdentifier != null ? instanceIdentifier : "") ;
    }

     /**
     * Set the identifier on a W3C endpoint reference under construction.
     * @param builder The endpoint reference builder.
     * @param identifier The identifier.
     */
    public static void setEndpointInstanceIdentifier(final W3CEndpointReferenceBuilder builder, final String identifier)
    {
        builder.referenceParameter(createInstanceIdentifierElement(identifier));
    }
    
    /**
     * Set the identifier on a W3C endpoint reference under construction.
     * @param builder The endpoint reference builder.
     * @param instanceIdentifier The identifier.
     */
    public static void setEndpointInstanceIdentifier(final W3CEndpointReferenceBuilder builder, final InstanceIdentifier instanceIdentifier)
    {
        builder.referenceParameter(createInstanceIdentifierElement(instanceIdentifier.getInstanceIdentifier())) ;
    }

    /**
    * Set the identifier on a WS Addressing endpoint reference under construction.
    * @param epReference The WS Addressing endpoint reference.
    * @param instanceIdentifier The identifier.
    */
   public static void setEndpointInstanceIdentifier(final MAPEndpoint epReference, final InstanceIdentifier instanceIdentifier)
   {
       setEndpointInstanceIdentifier(epReference, instanceIdentifier.getInstanceIdentifier());
   }

    /**
    * Set the identifier on a WS Addressing endpoint reference under construction.
    * @param epReference The WS Addressing endpoint reference.
    * @param instanceIdentifier The identifier string.
    */
   public static void setEndpointInstanceIdentifier(final MAPEndpoint epReference, final String instanceIdentifier)
   {
       epReference.addReferenceParameter(createInstanceIdentifierElement(instanceIdentifier));
   }

    /**
     * a soap factory used to construct SOAPElement instances representing InstanceIdentifier instances
     */
    private static SOAPFactory factory = createSoapFactory();

    /**
     * a name for the WSArj Instance element
     */
    private static Name WSARJ_ELEMENT_INSTANCE_NAME;

    /**
     * Create a SOAPElement representing an InstanceIdentifier
     * @param instanceIdentifier the identifier string of the InstanceIdentifier being represented
     * @return a SOAPElement with the InstancreIdentifier QName as its element tag and a text node containing the
     * suppliedidentifier string as its value
     */

    public static Element createInstanceIdentifierElement(final String instanceIdentifier)
    {
        try {
            SOAPElement element = factory.createElement(WSARJ_ELEMENT_INSTANCE_NAME);
            element.addNamespaceDeclaration(ArjunaConstants.WSARJ_PREFIX, ArjunaConstants.WSARJ_NAMESPACE);
            element.addTextNode(instanceIdentifier);
            return element;
        } catch (SOAPException se) {
            // TODO log error here (should never happen)
            return null;
        }
    }

    private static SOAPFactory createSoapFactory()
    {
        try {
            SOAPFactory factory = SOAPFactory.newInstance();
            Name name = factory.createName(ArjunaConstants.WSARJ_ELEMENT_INSTANCE_IDENTIFIER,
                    ArjunaConstants.WSARJ_PREFIX,
                    ArjunaConstants.WSARJ_NAMESPACE);
            WSARJ_ELEMENT_INSTANCE_NAME = name;
            return factory;
        } catch (SOAPException e) {
            // TODO log error here (should never happen)
        }
        return null;
    }
}