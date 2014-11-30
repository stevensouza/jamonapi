package com.jamonapi.distributed;

import com.jamonapi.JamonPropertiesLoader;
import com.jamonapi.MonitorFactory;
import com.jamonapi.jmx.JmxUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.lang.management.ManagementFactory;
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
            addListeners();
            addJmxBeans();
            JamonDataPersisterTimerTask saveTask = getDistributedJamonTimerTask();
            int refreshRate = getRefreshRate();
            saveTask.schedule(refreshRate);
        }
    }

    private void addListeners() {
        JamonPropertiesLoader loader = new JamonPropertiesLoader();
        MonitorFactory.addListeners(loader.getListeners());
    }

    private void addJmxBeans() {
       JmxUtils.registerMbeans();
    }

    JamonDataPersisterTimerTask getDistributedJamonTimerTask() {
        return new JamonDataPersisterTimerTask(getJamonData());
    }

    JamonDataPersister getJamonData() {
        return JamonDataPersisterFactory.get();
    }


    int getRefreshRate() {
        Properties properties = new JamonPropertiesLoader().getJamonProperties();
        return MINUTES * Integer.valueOf(properties.getProperty("distributedDataRefreshRateInMinutes"));
    }

    @Override
    public void contextDestroyed(ServletContextEvent event)  {
        JmxUtils.unregisterMbeans();
    }

}
