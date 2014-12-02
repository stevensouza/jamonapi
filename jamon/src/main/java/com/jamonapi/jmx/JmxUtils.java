package com.jamonapi.jmx;

import com.jamonapi.JamonPropertiesLoader;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import javax.management.MBeanServer;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.*;

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
     *  register all jamon related mbeans except those related to jamon specific monitors taken from jamonapi.properties file
     */
    public static  void registerMbeans(MBeanServer mBeanServer) {
        MonitorMXBeanImp mxBeanImp = null;
        try {
            mBeanServer.registerMBean(new Log4jMXBeanImp(), Log4jMXBeanImp.getObjectName());
            mBeanServer.registerMBean(new ExceptionMXBeanImp(), ExceptionMXBeanImp.getObjectName());
            mBeanServer.registerMBean(new ExceptionDeltaMXBeanImp(), ExceptionDeltaMXBeanImp.getObjectName());
            mBeanServer.registerMBean(new Log4jDeltaMXBeanImp(), Log4jDeltaMXBeanImp.getObjectName());
            mBeanServer.registerMBean(new JAMonVersionMXBeanImp(), JAMonVersionMXBeanImp.getObjectName());

            // gcMXBean gets notificaitons from gc events and saves results in jamon.
            registerGcMXBean(mBeanServer);
            registerMbeansFromPropsFile(mBeanServer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static  void registerMbeansFromPropsFile(MBeanServer mBeanServer) throws Exception {
        JamonPropertiesLoader loader = new JamonPropertiesLoader();
        List<JamonPropertiesLoader.JamonJmxBean> jamonJmxBeans = loader.getMxBeans();
        Iterator<JamonPropertiesLoader.JamonJmxBean> iter = jamonJmxBeans.iterator();

        while (iter.hasNext()) {
          JamonPropertiesLoader.JamonJmxBean beanInfo = iter.next();
          MonitorMXBeanImp mXbean = MonitorMXBeanImp.create(beanInfo.getLabel(), beanInfo.getUnits(), beanInfo.getName());
          mBeanServer.registerMBean(mXbean, MonitorMXBeanImp.getObjectName(mXbean));
          MonitorDeltaMXBeanImp  mXbeanDelta = MonitorDeltaMXBeanImp.create(beanInfo.getLabel(), beanInfo.getUnits(), beanInfo.getName());
          mBeanServer.registerMBean(mXbeanDelta, MonitorDeltaMXBeanImp.getObjectName(mXbeanDelta));
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
            mBeanServer.unregisterMBean(JAMonVersionMXBeanImp.getObjectName());
            unregisterGcMXBean(mBeanServer);
            unregisterMbeansFromPropsFile(mBeanServer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static  void unregisterMbeansFromPropsFile(MBeanServer mBeanServer) throws Exception {
        JamonPropertiesLoader loader = new JamonPropertiesLoader();
        List<JamonPropertiesLoader.JamonJmxBean> jamonJmxBeans = loader.getMxBeans();
        Iterator<JamonPropertiesLoader.JamonJmxBean> iter = jamonJmxBeans.iterator();

        while (iter.hasNext()) {
            JamonPropertiesLoader.JamonJmxBean beanInfo = iter.next();
            MonitorMXBeanImp mXbean = MonitorMXBeanImp.create(beanInfo.getLabel(), beanInfo.getUnits(), beanInfo.getName());
            mBeanServer.unregisterMBean(MonitorMXBeanImp.getObjectName(mXbean));
            MonitorDeltaMXBeanImp  mXbeanDelta = MonitorDeltaMXBeanImp.create(beanInfo.getLabel(), beanInfo.getUnits(), beanInfo.getName());
            mBeanServer.unregisterMBean(MonitorDeltaMXBeanImp.getObjectName(mXbeanDelta));
        }
    }

    public static Set<ObjectName> getGarbageCollectionMbeans(MBeanServer mBeanServer) throws Exception {
        Set<ObjectName> mbeans = mBeanServer.queryNames(null, null);
        Set<ObjectName> gcMbeans = new HashSet<ObjectName>();
        for (ObjectName objectInstance : mbeans) {
            if (objectInstance.toString().contains("type=GarbageCollector")) {
                gcMbeans.add(objectInstance);
            }
        }
        return gcMbeans;
    }

    // Note the garbage collector mxbean was introduced in jdk 1.7 so it may fail for earlier versions of
    // the jdk.  Creating a GcMXBean will throw a class not found exception in these earlier versions.
    // The approach i am taking below is to fail silently as it isn't crucial that gc is monitored.
    private static void registerGcMXBean(MBeanServer mBeanServer)  {
        try {
          GcMXBean gcMXBean = new GcMXBeanImp();
          mBeanServer.registerMBean(gcMXBean, GcMXBeanImp.getObjectName());
          Set<ObjectName> gcMbeans = getGarbageCollectionMbeans(mBeanServer);
          for (ObjectName name : gcMbeans) {
              mBeanServer.addNotificationListener(name, GcMXBeanImp.getObjectName(), null, null);
          }
        } catch (Throwable e) {
            // fail silently
        }
    }

    // same reason as note for registerGcMXBean
    private static void unregisterGcMXBean(MBeanServer mBeanServer)  {
        try {
          Set<ObjectName> gcMbeans = getGarbageCollectionMbeans(mBeanServer);
          for (ObjectName name : gcMbeans) {
             mBeanServer.removeNotificationListener(name, GcMXBeanImp.getObjectName());
          }
          // above must remove before unregistering
          mBeanServer.unregisterMBean(GcMXBeanImp.getObjectName());
        } catch (Throwable e) {
            // fail silently
        }
    }

}