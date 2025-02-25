/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.ats.internal.jdbc.drivers;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

import javax.sql.XADataSource;

import com.arjuna.ats.internal.jdbc.DynamicClass;

/**
 * A dynamic class that reads from a properties file and uses the information to
 * instantiate and configure an XADataSource.
 *
 * The properties in the file must be as follows:
 *   xaDataSourceClassName : The name of the driver class that implements XADataSource
 * All other properties in the file are read and a matching setter method called
 * on the XADataSource. This allows for implementations that require non standard configuration.
 * e.g.
 *   serverName=foo
 * results in the method call
 *   setServerName("foo");
 * in accordance with JavaBeans naming conventions.
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com) 2009-05
 */
public class PropertyFileDynamicClass implements DynamicClass
{
    private static final String xaDataSourceClassNameProperty = "xaDataSourceClassName";

    public XADataSource getDataSource(String propertyFileName) throws SQLException {
        // read some system properties and use reflection to load and configure the datasource.

        Properties properties = new Properties();

        FileInputStream propertiesFileInputStream = null;
        try {
            propertiesFileInputStream = new FileInputStream(propertyFileName);
            properties.load(propertiesFileInputStream);
            propertiesFileInputStream.close();
        } catch(IOException e) {
            SQLException sqlException = new SQLException("failed to locate properties file");
            sqlException.initCause(e);
            throw sqlException;
        } finally {
            if(propertiesFileInputStream != null) {
                try {
                    propertiesFileInputStream.close();
                } catch(IOException e) {}
            }
        }

        String xaDataSourceClassName = properties.getProperty(xaDataSourceClassNameProperty);

        XADataSourceReflectionWrapper xaDataSourceReflectionWrapper = new XADataSourceReflectionWrapper(xaDataSourceClassName);

        Enumeration enumeration = properties.propertyNames();
        while(enumeration.hasMoreElements()) {
            String propertyName = (String)enumeration.nextElement();
            if(xaDataSourceClassNameProperty.equals(propertyName)) {
                continue;
            }
            String propertyValue = (String)properties.get(propertyName);
            try {
                xaDataSourceReflectionWrapper.setProperty(propertyName, propertyValue);
            } catch(Exception e) {
                SQLException sqlException = new SQLException("failed to configure XADataSource");
                sqlException.initCause(e);
                throw sqlException;
            }
        }

        return xaDataSourceReflectionWrapper.getWrappedXADataSource();
    }
}