package com.jamonapi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    private Properties jamonProps;
    private List<JamonListener> listenerList = new ArrayList<JamonListener>();

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
        jamonProps = new Properties(defaults);
        jamonProps.putAll(userProvided);
        addListeners();
        return jamonProps;
    }

    public URL getPropertiesDirectory() {
        return getClass().getClassLoader().getResource(".");
    }

    public List<JamonListener> getListeners() {
        getJamonProperties();
        return listenerList;
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
        defaults.put("jamonDataPersister.label", "");
        defaults.put("jamonDataPersister.label.prefix", "");
        defaults.put("jamonDataPersister.directory", "jamondata");
        defaults.put("jamonListener.type", "value");
        defaults.put("jamonListener.name", "FIFOBuffer");
        defaults.put("jamonListener.size", "50");
        return defaults;
    }

    private void addListeners() {
        int size = Integer.valueOf(jamonProps.getProperty("jamonListener.size"));
        for (int i=0; i<=size; i++) {
            String keyPrefix = getKeyPrefix(i);
            String listener = jamonProps.getProperty(keyPrefix+"key");
            if (listener!=null) {
                listenerList.add(new JamonListener(keyPrefix));
            }
        }
    }

    private String getKeyPrefix(int i) {
        return "jamonListener["+i+"].";
    }

    // Simple value object that holds the values for a listener read in from the properties file
    public class JamonListener {
        private String keyPrefix;

        private JamonListener(String keyPrefix) {
            this.keyPrefix = keyPrefix;
        }

        private String[] split(String keyInfo) {
            String[] key = keyInfo.split(",");
            key[0] = key[0].trim();
            key[1] = key[1].trim();
            return key;
        }

        /** example: com.jamonapi.Exceptions */
        public String getLabel() {
            return split(jamonProps.getProperty(keyPrefix + "key"))[0];
        }

        /** example: Exception */
        public String getUnits() {
            return split(jamonProps.getProperty(keyPrefix + "key"))[1];
        }

        /** example: value, maxactive, ... */
        public String getListenerType() {
            String defaultProp = jamonProps.getProperty("jamonListener.type");
            return jamonProps.getProperty(keyPrefix + "type", defaultProp).trim();
        }

        /** example: FIFOBuffer */
        public String getListenerName() {
            String defaultProp = jamonProps.getProperty("jamonListener.name");
            return jamonProps.getProperty(keyPrefix + "name", defaultProp).trim();
        }
    }

}
