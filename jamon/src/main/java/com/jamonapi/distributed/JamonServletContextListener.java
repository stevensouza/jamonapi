package com.jamonapi.distributed;

import java.util.Properties;
import java.util.Timer;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jamonapi.JamonPropertiesLoader;
import com.jamonapi.MonitorFactory;
import com.jamonapi.jmx.JmxUtils;
import com.jamonapi.utils.LocaleContext;

/**
 * A timer is executed when the web container starts up.  The timer saves jamon data every N minutes per a config
 * property in the jamonapi.properties file.
 *
 * Created by stevesouza on 7/6/14.
 */
public class JamonServletContextListener implements ServletContextListener  {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JamonServletContextListener.class);

    private static final int MINUTES = 60*1000;
    
    private JamonDataPersisterTimerTask saveTask;
    private Timer saveTimer;
    
    private JamonPropertiesLoader loader;

    //Run this before web application is started
    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        if (context != null) {
        	
        	String jamonPropertiesLocation = context.getInitParameter("jamonPropertiesLocation");
        	context.log("Initialize the JamonServletContextListener, jamonPropertiesLocation: " + jamonPropertiesLocation);

        	LOGGER.info(">> Initialize the JamonServletContextListener, jamonPropertiesLocation: {}", jamonPropertiesLocation);
        	
        	if (jamonPropertiesLocation != null) {
        		loader = new JamonPropertiesLoader(jamonPropertiesLocation);
        		// load the properties
        		Properties props = loader.getJamonProperties();
        		
        		LOGGER.info("Loaded jamonapi properties: {}", props);
        	}
        	else {
        		LOGGER.info("Use default jamonapi properties loader.");
            	loader = new JamonPropertiesLoader();
        	}
        	
            addListeners(loader);
            addJmxBeans(loader);
            
            context.log("Prepare the saveTask.");
            LOGGER.info("Prepare the saveTask.");
            saveTask = getDistributedJamonTimerTask(loader);
            int refreshRate = getRefreshRate(loader);
            saveTimer = saveTask.schedule(refreshRate);
        }
    }
    
    private void addListeners(JamonPropertiesLoader loader) {
        MonitorFactory.addListeners(loader.getListeners());
    }

    private void addJmxBeans(JamonPropertiesLoader loader) {
       JmxUtils.registerMbeans(loader);
    }

    JamonDataPersisterTimerTask getDistributedJamonTimerTask(final JamonPropertiesLoader loader) {
    	LOGGER.info("Create the JamonDataPersisterTimerTask.");
        return new JamonDataPersisterTimerTask(getJamonData(loader));
    }

    JamonDataPersister getJamonData(final JamonPropertiesLoader loader) {
    	// set the properties
    	JamonDataPersisterFactory.setJamonProperties(loader.getJamonProperties());
    	
        return JamonDataPersisterFactory.get();
    }


    int getRefreshRate(JamonPropertiesLoader loader) {
        Properties properties = loader.getJamonProperties();
        return MINUTES * Integer.valueOf(properties.getProperty("distributedDataRefreshRateInMinutes"));
    }

    @Override
    public void contextDestroyed(ServletContextEvent event)  {
		ServletContext context = event.getServletContext();
        if (context != null) {
        	context.log("The context is destroyed.");
        }

    	if (saveTimer != null) {
            if (context != null) {
            	context.log("The context is destroyed, stop the saveTimer: " + saveTimer);
            }
    		try {
    			saveTimer.cancel();
    		}
    		catch (Exception ex) {
    			LOGGER.warn("Stop save timer failed.", ex);
                if (context != null) {
                	context.log("The context is destroyed but stop the saveTimer failed.", ex);
                }
    		}
    	}
    	else {
            if (context != null) {
            	context.log("The context is destroyed, no saveTimer to stop available.");
            }
    	}
        
    	if (saveTask != null) {
            if (context != null) {
            	context.log("The context is destroyed, stop the saveTask: " + saveTask);
            }
    		try {
    			saveTask.cancel();
    		}
    		catch (Exception ex) {
    			LOGGER.warn("Stop save task failed.", ex);
                if (context != null) {
                	context.log("The context is destroyed but stop the saveTask failed.", ex);
                }
    		}
    	}
    	else {
            if (context != null) {
            	context.log("The context is destroyed, no saveTask to stop available.");
            }
    	}
        LocaleContext.reset();
        JmxUtils.unregisterMbeans(loader);
    }

}
