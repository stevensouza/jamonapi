package com.jamonapi.distributed;

import com.jamonapi.JamonPropertiesLoader;

import java.util.Properties;

/**
 * Class that instanciates the JamonDataPersister class.  Note this could be a local implementation or a distributed jamon intrface
 * such as HazelCast.  By default it tries to use HazelCast if it is in the classpath, or else it falls back to the local
 * implementation
 *
 * Created by stevesouza on 7/6/14.
 */
public class JamonDataPersisterFactory {

    private static JamonDataPersisterFactory factory = new JamonDataPersisterFactory();
    private JamonDataPersister jamonDataPersister;
    private String jamonDataPersisterName;
    private Properties jamonProperties;

    private JamonDataPersisterFactory() {
        jamonProperties = new JamonPropertiesLoader().getJamonProperties();
        jamonDataPersisterName = jamonProperties.getProperty("jamonDataPersister");
    }

    public static JamonDataPersister get() {
        if (factory.jamonDataPersister ==null) {
            factory.initialize();
        }
         return factory.jamonDataPersister;
    }

    public static Properties getJamonProperties() {
        return factory.jamonProperties;
    }

    // initialize with HazelCast implementation if you can.  If not use the local implementation.
    private void initialize() {
        jamonDataPersister =  create(jamonDataPersisterName);
        if (jamonDataPersister ==null) {
           jamonDataPersister = new LocalJamonDataPersister();
        }
    }

    private static JamonDataPersister create(String className) {
        try {
            return (JamonDataPersister) Class.forName(className).newInstance();
        } catch (Throwable e) {
        }
        return null;
    }


}
