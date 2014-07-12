package com.jamonapi.distributed;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * A timer is executed when the web container starts up.  The timer saves jamon data every N minutes per a config
 * property in the web.xml file.
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
            DistributedJamonTimerTask saveTask = getDistributedJamonTimerTask();
            int refreshRate = getRefreshRate(context);
            saveTask.schedule(refreshRate);
        }
    }

    DistributedJamonTimerTask getDistributedJamonTimerTask() {
        return new DistributedJamonTimerTask(getJamonData());
    }

    JamonData getJamonData() {
        return JamonDataFactory.get();
    }


    int getRefreshRate(ServletContext context) {
        return MINUTES * Integer.valueOf(context.getInitParameter("distributedDataRefreshRateInMinutes"));
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }



}
