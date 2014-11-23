package com.jamonapi.jmx;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Date;

/**
 * Created by stevesouza on 11/19/14.
 */
 public class JmxUtils {

    /**
     * Return hits/count of the passed in monitor if it exists otherwise return 0
     *
     * @param label jamon label
     * @param units jamon units
     * @return count
     */
     static long getCount(String label, String units) {
        if (MonitorFactory.exists(label, units)) {
            Monitor mon = MonitorFactory.getMonitor(label, units);
            return (long) mon.getHits();
        }

        return 0;
    }

    /**
     * Return hits/count of the passed in monitor if it exists otherwise return 0
     *
     * @param label jamon label
     * @param units jamon units
     * @param value string representing the metric to return i.e. avg, hits etc.
     * @return the metric
     */
    static double getDouble(String label, String units, String value) {
        if (MonitorFactory.exists(label, units)) {
            Monitor mon = MonitorFactory.getMonitor(label, units);
            return  (Double) mon.getValue(value);
        }

        return 0.0;
    }

    static Date getDate(String label, String units, String value) {
        if (MonitorFactory.exists(label, units)) {
            Monitor mon = MonitorFactory.getMonitor(label, units);
            return  (Date) mon.getValue(value);
        }

        return null;
    }

    static ObjectName getObjectName(String name) {
        try {
            return new ObjectName(name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     *  register all jamon related mbeans
     */
    public static  void registerMbeans() {
        registerMbeans(ManagementFactory.getPlatformMBeanServer());
    }

     /**
     *  register all jamon related mbeans
     */
    public static  void registerMbeans(MBeanServer mBeanServer) {
        MonitorMXBeanImp mxBeanImp = null;
        try {
            mBeanServer.registerMBean(new Log4jMXBeanImp(), Log4jMXBeanImp.getObjectName());
            mBeanServer.registerMBean(new ExceptionMXBeanImp(), ExceptionMXBeanImp.getObjectName());
            mBeanServer.registerMBean(new ExceptionDeltaMXBeanImp(), ExceptionDeltaMXBeanImp.getObjectName());
            mBeanServer.registerMBean(new Log4jDeltaMXBeanImp(), Log4jDeltaMXBeanImp.getObjectName());

//            mxBeanImp = new MonitorMXBeanImp("mylabel", "myunits");
//            mBeanServer.registerMBean(mxBeanImp, MonitorMXBeanImp.getObjectName(mxBeanImp));
//            mxBeanImp = new MonitorMXBeanImp("com.jamonapi.http.JAMonJettyHandlerNew.request.allPages", "ms.");
//            mBeanServer.registerMBean(mxBeanImp, MonitorMXBeanImp.getObjectName(mxBeanImp));
//            mxBeanImp = new MonitorMXBeanImp("com.jamonapi.http.JAMonJettyHandlerNew.request.allPages", "ms.", "HttpPageRequests");
//            mBeanServer.registerMBean(mxBeanImp, MonitorMXBeanImp.getObjectName(mxBeanImp));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * unRegister all jamon related mbeans
     */
    public static void unregisterMbeans() {
        unregisterMbeans(ManagementFactory.getPlatformMBeanServer());
    }


    /**
     * unRegister all jamon related mbeans
     */
    public static void unregisterMbeans(MBeanServer mBeanServer) {
        try {
            mBeanServer.unregisterMBean(Log4jMXBeanImp.getObjectName());
            mBeanServer.unregisterMBean(ExceptionMXBeanImp.getObjectName());
            mBeanServer.unregisterMBean(ExceptionDeltaMXBeanImp.getObjectName());
            mBeanServer.unregisterMBean(Log4jDeltaMXBeanImp.getObjectName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}