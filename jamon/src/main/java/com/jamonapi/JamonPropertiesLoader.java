package com.jamonapi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Load jamon properties.  The order of loading is to look in the file named jamonapi.properties in the classpath.
 * Next look for system properties passed to the command line: (-DdistributedDataRefreshRateInMinutes=10).
 * These take precedence over the file.  If properties aren't in the file or passed in via the command line
 * then use defaults.
 *
 * Created by stevesouza on 7/13/14.
 */
public class JamonPropertiesLoader {

    private String fileName;

    public JamonPropertiesLoader() {
        this("jamonapi.properties");
    }

    JamonPropertiesLoader(String fileName) {
        this.fileName = fileName;
    }

    /** Using logic documented in the class comments load jamon properties.  Note it can't fail as in the worst case
     * it loads defaults.
     * @return
     * @throws IOException
     */
    public Properties getJamonProperties() {
        // note precedence is -D properties, then from the file, then defaults.
        Properties defaults = getDefaults();
        Properties userProvided = propertyLoader(fileName);
        replaceWithCommandLineProps(userProvided, defaults);
        Properties jamonProps = new Properties(defaults);
        jamonProps.putAll(userProvided);
        return jamonProps;
    }

    public URL getPropertiesDirectory() {
        return getClass().getClassLoader().getResource(".");
    }

    private  Properties propertyLoader(String fileName)  {
        Properties properties = new Properties();
        InputStream input = null;
        try {
            input = getClass().getClassLoader().getResourceAsStream(fileName);
            if (input!=null) {
                properties.load(input);
            }
        } catch (Throwable t) {
            // want to ignore exception and proceed with loading with CLI props or defaults.
        } finally{
            close(input);
        }

        return properties;
    }

    void close(InputStream input) {
        try {
          if (input!=null) {
            input.close();
           }
        } catch (Throwable t) {

        }
    }

    private  void replaceWithCommandLineProps(Properties properties, Properties defaults) {
        for (Object key : defaults.keySet()) {
            String value = System.getProperty(key.toString());
            if (value != null) {
                properties.put(key, value);
            }
        }

    }

    Properties getDefaults() {
        Properties defaults = new Properties();
        defaults.put("distributedDataRefreshRateInMinutes", "5");
        defaults.put("jamonDataPersister", "com.jamonapi.distributed.DistributedJamonHazelcastPersister");
        return defaults;
    }

}
