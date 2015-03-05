package com.jamonapi.distributed;

import com.jamonapi.JamonPropertiesLoader;
import com.jamonapi.MonitorFactory;
import com.jamonapi.jmx.JmxUtils;
import com.jamonapi.utils.LocaleContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Properties;

/**
 * A timer is executed when the web container starts up.  The timer saves jamon data every N minutes per a config
 * property in the jamonapi.properties file.
 *
 * Created by stevesouza on 7/6/14.
 */
public class JamonServletContextListener implements ServletContextListener  {

    private static final int MINUTES = 60*1000;

    //Run this before web application is started
    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        if (context != null) {
        	
        	String jamonPropertiesLocation = context.getInitParameter("jamonPropertiesLocation");
        	context.log("Initialize the JamonServletContextListener, jamonPropertiesLocation: " + jamonPropertiesLocation);
        	
        	JamonPropertiesLoader loader = null;
        	if (jamonPropertiesLocation != null) {
        		loader = new JamonPropertiesLoader(jamonPropertiesLocation);
        	}
        	else {
            	loader = new JamonPropertiesLoader();
        	}
        	
            addListeners(loader);
            addJmxBeans();
            JamonDataPersisterTimerTask saveTask = getDistributedJamonTimerTask(loader);
            int refreshRate = getRefreshRate(loader);
            saveTask.schedule(refreshRate);
        }
    }

    private void addListeners(JamonPropertiesLoader loader) {
        MonitorFactory.addListeners(loader.getListeners());
    }

    private void addJmxBeans() {
       JmxUtils.registerMbeans();
    }

    JamonDataPersisterTimerTask getDistributedJamonTimerTask(final JamonPropertiesLoader loader) {
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
        LocaleContext.reset();
        JmxUtils.unregisterMbeans();
    }

}
