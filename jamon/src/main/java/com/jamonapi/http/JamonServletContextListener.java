package com.jamonapi.http;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by stevesouza on 7/6/14.
 */
public class JamonServletContextListener implements ServletContextListener  {

    //Run this before web application is started
    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("ServletContextListener started steve: "+event);

        ServletContext context = event.getServletContext();
        if (context != null) {
            String refreshRate = context.getInitParameter("distributedDataRefreshRate");
            // create timer
            String timeToLive = context.getInitParameter("distributedDataTimeToLive");
            System.out.println("*** it worked: refreshRate="+refreshRate+", timeToLive="+timeToLive);
        }
    }

        @Override
        public void contextDestroyed(ServletContextEvent event) {

            System.out.println("ServletContextListener destroyed steve "+event);


        }



}
