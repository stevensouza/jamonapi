package com.jamonapi;

import com.jamonapi.utils.Misc;

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

    public static final Properties PROPS = new JamonPropertiesLoader().getJamonProperties();

    private String fileName;
    private Properties jamonProps;
    private List<JamonListenerProperty> listenerList;
    private List<String> jamonMxBeanList;

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
        if (jamonProps==null) {
            initialize();
        }
        return jamonProps;
    }

    void initialize() {
        // note precedence is -D properties, then from the file, then defaults.
        Properties defaults = getDefaults();
        Properties userProvided = propertyLoader(fileName);
        replaceWithCommandLineProps(userProvided, defaults);
        jamonProps = new Properties(defaults);
        jamonProps.putAll(userProvided);
    }

    public URL getPropertiesDirectory() {
        return getClass().getClassLoader().getResource(".");
    }

    public List<JamonListenerProperty> getListeners() {
        if (jamonProps==null) {
            initialize();
        }
        if (listenerList==null) {
            addListeners();
        }
        return listenerList;
    }

    public List<String> getMxBeans() {
        if (jamonProps==null) {
            initialize();
        }
        if (jamonMxBeanList==null) {
            addJamonMxBeans();
        }

        if (jamonMxBeanList.isEmpty()) {
            loadDefaultJamonMxBeans();
        }

        return jamonMxBeanList;
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
        defaults.put("jamonDataPersister", "com.jamonapi.distributed.HazelcastFilePersister");
        defaults.put("jamonDataPersister.label", "");
        defaults.put("jamonDataPersister.label.prefix", "");
        defaults.put("jamonDataPersister.directory", "jamondata");
        defaults.put("jamonListener.type", "value");
        defaults.put("jamonListener.name", "FIFOBuffer");
        defaults.put("jamonListener.size", "50");
        defaults.put("jamonJmxBean.size", "50");
        defaults.put("monitorCompositeCombiner.defaultFifoBufferSize", "100");
        return defaults;
    }

    // These defaults are only used if NO jamon jmx beans are listed in the properties file.  If even
    // 1 is created in the properties file then none of these are created.  This allows a user to override
    // these defaults for example if they don't use 'delete' statements.
    private void loadDefaultJamonMxBeans() {
        jamonMxBeanList.add("com.jamonapi.http.JAMonJettyHandlerNew.request.allPages, ms., com.jamonapi.http.JAMonTomcatValve.request.allPages, ms., Jamon.HttpPageRequests");
        jamonMxBeanList.add("MonProxy-SQL-Type: All, ms., Jamon.Sql.All");
        jamonMxBeanList.add("MonProxy-SQL-Type: select, ms., Jamon.Sql.Select");
        jamonMxBeanList.add("MonProxy-SQL-Type: update, ms., Jamon.Sql.Update");
        jamonMxBeanList.add("MonProxy-SQL-Type: delete, ms., Jamon.Sql.Delete");
        jamonMxBeanList.add("MonProxy-SQL-Type: insert, ms., Jamon.Sql.Insert");
    }

    private void addListeners() {
        listenerList = new ArrayList<JamonListenerProperty>();
        int size = Integer.valueOf(jamonProps.getProperty("jamonListener.size"));
        for (int i = 0; i <= size; i++) {
           String keyPrefix = getKeyPrefix("jamonListener", i);
           String listener = jamonProps.getProperty(keyPrefix + "key");
           if (listener != null) {
              listenerList.add(new JamonListenerProperty(keyPrefix));
           }
        }
    }

    private void addJamonMxBeans() {
        jamonMxBeanList = new ArrayList<String>();
        int size = Integer.valueOf(jamonProps.getProperty("jamonJmxBean.size"));
        for (int i = 0; i <= size; i++) {
            String keyPrefix = getKeyPrefix("jamonJmxBean", i);
            String propertyValue = jamonProps.getProperty(keyPrefix + "key");
            if (propertyValue != null) {
                jamonMxBeanList.add(propertyValue);
            }
        }
    }

    /**
     *
     * @param key example: mykey
     * @param i example: 0
     * @return something like: mykey[0]
     */
    private String getKeyPrefix(String key, int i) {
        return key+"["+i+"].";
    }


    // Simple value object that holds the values for a listener read in from the properties file
    public class JamonListenerProperty {
        protected String keyPrefix;

        protected JamonListenerProperty(String keyPrefix) {
            this.keyPrefix = keyPrefix;
        }

        protected String[] split(String listenerPropertyValue) {
            return Misc.trim(listenerPropertyValue.split(","));
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
