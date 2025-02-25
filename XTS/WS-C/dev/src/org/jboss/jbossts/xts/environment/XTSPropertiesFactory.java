/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package org.jboss.jbossts.xts.environment;

import com.arjuna.common.util.propertyservice.PropertiesFactory;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: adinn
 * Date: Aug 25, 2010
 * Time: 2:45:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class XTSPropertiesFactory
{
    private static Properties defaultProperties = null;

    /**
     * Returns the systems default properties, as read from the configuration file.
     * @return the configuration Properties
     */
    public static synchronized Properties getDefaultProperties() {
        try {
            initDefaultProperties("org.jboss.jbossts.xts.propertiesFile");
        } catch (Exception e) {
            defaultProperties = new Properties(System.getProperties());
        }

        return defaultProperties;
    }

    /**
     * Returns the systems default properties, as read from the configuration file.
     */
    public static synchronized void setDefaultProperties(Properties properties) {
        if(defaultProperties == null) {
            defaultProperties = properties;
        }
    }

    private static void initDefaultProperties(String fileNamePropertyKey)
    {
        if(defaultProperties != null) {
            return;
        }

        // This is where the properties loading takes place. The algorithm is as follows:

        // If the specified fileNamePropertyKey exists as a key is the system properties, take the value of that property as
        // the location of the module's properties file. This allows file location to be overriden easily.
        String propertyFileName = System.getProperty(fileNamePropertyKey);

        // If the system property is not set, try to load the build time properties. Build time properties
        // are not the module properties! These are optional and so loading may fail. That's not considered an error.
        // If the properties file name is defined by the build time properties, use that.
        // (In JBossTS it mostly does exist - the build scripts put build time properties into the .jars manifest file.)
        if (propertyFileName == null) {
            propertyFileName = "xts-properties.xml";
        }

        // use the TS properties factory but supply the XTS property file name
        // we also need to pass the class loader in case we try to load
        // the file as a resource. this makes sure the resource load is performed
        // relative to the XTS deployment

        defaultProperties = PropertiesFactory.getPropertiesFromFile(propertyFileName, XTSPropertiesFactory.class.getClassLoader());
    }
}
