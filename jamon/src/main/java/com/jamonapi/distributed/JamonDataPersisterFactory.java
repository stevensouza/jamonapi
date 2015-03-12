package com.jamonapi.distributed;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jamonapi.JamonPropertiesLoader;

/**
 * Class that instanciates the JamonDataPersister class.  Note this could be a local implementation or a distributed implementation
 * such as HazelCast.  By default it tries to use HazelCast if the HazelCast jar is in the classpath,
 * or else it falls back to the local implementation
 *
 * Created by stevesouza on 7/6/14.
 */
public class JamonDataPersisterFactory {
	
	private static final Log LOGGER = LogFactory.getLog(JamonDataPersisterFactory.class);

    private static JamonDataPersisterFactory factory = new JamonDataPersisterFactory();
    private JamonDataPersister jamonDataPersister;
    private String jamonDataPersisterName;
    private Properties jamonProperties;

    private JamonDataPersisterFactory() {
    }

    public static JamonDataPersister get() {
        if (factory.jamonDataPersister == null) {
            factory.initialize();
        }
         return factory.jamonDataPersister;
    }

    public static Properties getJamonProperties() {
    	
    	if (factory.jamonProperties == null) {
    		LOGGER.info("The factory.jamonProperties are not assigned, create default properties.");
    		factory.jamonProperties = new JamonPropertiesLoader().getJamonProperties();
    	}
    	
        return factory.jamonProperties;
    }
    
    public static void setJamonProperties(final Properties jamonProperties) {
    	LOGGER.info("Set the jamonProperties: " + jamonProperties);
        factory.jamonProperties = jamonProperties;
    }

    // initialize with HazelCast implementation if you can.  If not use the local implementation.
    private void initialize() {
    	if (jamonProperties == null) {
    		LOGGER.info("Initialize: No jamonProperties available. Create default jamonProperties.");
    		jamonProperties = new JamonPropertiesLoader().getJamonProperties();
    	}

    	jamonDataPersisterName = jamonProperties.getProperty("jamonDataPersister");
    	
		LOGGER.warn("Initialize: jamonDataPersister with className: " + jamonDataPersisterName);
        jamonDataPersister =  create(jamonDataPersisterName, jamonProperties);
        if (jamonDataPersister ==null) {
           jamonDataPersister = new LocalJamonFilePersister(jamonProperties);
        }
    }

    private static JamonDataPersister create(String className, final Properties jamonProperties) {
        try {
        	JamonDataPersister instance = null;
        	if (jamonProperties == null) {
        		LOGGER.info("Create new instance of " + className + " without properties.");
        		instance = (JamonDataPersister) Class.forName(className).newInstance();
        	}
        	else {
        		LOGGER.info("Create new instance of " + className + " with provided properties.");
        		instance = (JamonDataPersister) Class.forName(className).getDeclaredConstructor(Properties.class).newInstance(jamonProperties);
        	}
            return instance;
        } catch (Throwable e) {
        }
        return null;
    }

}
