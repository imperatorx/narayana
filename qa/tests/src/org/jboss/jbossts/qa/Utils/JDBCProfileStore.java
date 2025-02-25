/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */


package org.jboss.jbossts.qa.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.net.UnknownHostException;
import java.util.Properties;

public class JDBCProfileStore {
    private final static String BASE_DIRECTORY_PROPERTY = "jdbcprofilestore.dir";
    private static Properties _profile = null;

    public static int numberOfDrivers(String profileName) throws Exception {
        loadProfile();

        return Integer.parseInt((String) _profile.get(profileName + "_NumberOfDrivers"));
    }

    public static String driver(String profileName, int driverNumber) throws Exception {
        loadProfile();

        return (String) _profile.get(profileName + "_Driver" + driverNumber);
    }

    public static String databaseURL(String profileName) throws Exception {
        loadProfile();

        return (String) _profile.get(profileName + "_DatabaseURL");
    }

    public static String databaseUser(String profileName) throws Exception {
        loadProfile();

        return (String) _profile.get(profileName + "_DatabaseUser");
    }

    public static String databasePassword(String profileName) throws Exception {
        loadProfile();

        return (String) _profile.get(profileName + "_DatabasePassword");
    }

    public static String databaseDynamicClass(String profileName) throws Exception {
        loadProfile();

        return (String) _profile.get(profileName + "_DatabaseDynamicClass");
    }

    // new methods for jndi
    public static String binding(String profileName) throws Exception {
        loadProfile();

        return (String) _profile.get(profileName + "_Binding");
    }

    public static String databaseName(String profileName) throws Exception {
        loadProfile();

        return (String) _profile.get(profileName + "_DatabaseName");
    }

    public static String serviceName(String profileName) throws Exception {
        loadProfile();

        return (String) _profile.get(profileName + "_ServiceName");
    }

    public static String host(String profileName) throws Exception {
        loadProfile();

        return (String) _profile.get(profileName + "_Host");
    }

    public static String port(String profileName) throws Exception {
        loadProfile();

        return (String) _profile.get(profileName + "_Port");
    }

// end of new methods

    // new method to get a single driver
    public static String driver(String profileName) throws Exception {
        loadProfile();

        return (String) _profile.get(profileName + "_Driver");
    }

    /**
     * New method call to get query timeout value
     */
    public static int timeout(String profileName) throws Exception {
        loadProfile();
        //get loaded value or default
        String loadedvalue = _profile.getProperty(profileName + "_Timeout", "20");

        return Integer.parseInt(loadedvalue);
    }

    public static String getTableName(String username, String suffix) throws UnknownHostException {
        // read JBTM-390 before messing with this function.
        // previously this would have been: username + "_" + suffix as in "DROP TABLE " + databaseUser + "_InfoTable");
        String value = username + "_" + getLocalHostNameForTables() + "_" + suffix;
        // in addition to the problems with the valid characters, there are issues with the max length.
        // for oracle it's 30, but we stick a 4 char suffix on indexes, so the table name should not be more than 26
        // in certain cases this may mean we wind up with non-uniq names, which is a pain.
        if (value.length() > 26) {
            value = value.substring(0, 26);
        }
        return value;
    }

    private static String getLocalHostNameForTables() throws UnknownHostException {
        String hostName = java.net.InetAddress.getLocalHost().getHostName();
        hostName = stripHostName(hostName); // strip to local portion, force lower case.
        hostName = hostName.replace("-", "_"); // some db's don't like hyphens in identifiers
        return hostName;
    }

    private static void loadProfile() throws Exception {
        if (_profile == null) {
            String hostName = java.net.InetAddress.getLocalHost().getHostName();
            String baseDir = System.getProperty(BASE_DIRECTORY_PROPERTY);

            if (baseDir == null) {
                throw new Exception(BASE_DIRECTORY_PROPERTY + " property not set - cannot find JDBC test profiles!");
            }

            _profile = new Properties();

            File file = new File(baseDir + File.separator + stripHostName(hostName) + File.separator + "JDBCProfiles");

            if (!file.exists()) {
                // no host specific profile, fallback to a default one
                file = new File(baseDir + File.separator + "default" + File.separator + "JDBCProfiles");
            }

            FileInputStream profileFileInputStream = new FileInputStream(file);
            _profile.load(profileFileInputStream);
            profileFileInputStream.close();
        }
    }

    private static String stripHostName(String hostName) {
        hostName = hostName.toLowerCase();

        if (hostName.indexOf('.') != -1) {
            hostName = hostName.substring(0, hostName.indexOf('.'));
        }

        return hostName;
    }
}