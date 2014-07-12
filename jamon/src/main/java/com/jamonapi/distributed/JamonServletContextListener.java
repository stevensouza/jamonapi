package com.jamonapi.distributed;

import com.jamonapi.distributed.DistributedJamonTimerTask;
import com.jamonapi.distributed.JamonData;
import com.jamonapi.distributed.JamonDataFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Timer;

/**
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
            Timer timer = new Timer();
            int refreshRate = getRefreshRate(context);
            // use refreshRate for 1st value:  when to start, and how long to wait until next one.
            timer.scheduleAtFixedRate(saveTask, refreshRate, refreshRate);
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
