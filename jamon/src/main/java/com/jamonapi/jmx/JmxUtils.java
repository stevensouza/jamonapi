package com.jamonapi.jmx;

import com.jamonapi.JamonPropertiesLoader;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.*;

/**
 * Class that creates and destroys jamon jmx monitors.  It also provides some utility functions used in the other
 * jamon jmx classes.
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

    /**
     *
     * @param label jamon label
     * @param units jamon units
     * @param value string representing the metric to return i.e. avg, hits etc.
     * @return The date assosiated with the jamon monitor
     */
    static Date getDate(String label, String units, String value) {
        if (MonitorFactory.exists(label, units)) {
            Monitor mon = MonitorFactory.getMonitor(label, units);
            return  (Date) mon.getValue(value);
        }

        return null;
    }

    /**
     * Create a jmx ObjectName out of the passed in String
     * @param name
     * @return ObjectName
     */
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
     *  Register all jamon related mbeans with the passed in MBeanServer
     */
    public static  void registerMbeans(MBeanServer mBeanServer) {
        MonitorMXBeanImp mxBeanImp = null;
        try {
            mBeanServer.registerMBean(new Log4jMXBeanImp(), Log4jMXBeanImp.getObjectName());
            mBeanServer.registerMBean(new ExceptionMXBeanImp(), ExceptionMXBeanImp.getObjectName());
            mBeanServer.registerMBean(new ExceptionDeltaMXBeanImp(), ExceptionDeltaMXBeanImp.getObjectName());
            mBeanServer.registerMBean(new Log4jDeltaMXBeanImp(), Log4jDeltaMXBeanImp.getObjectName());
            mBeanServer.registerMBean(new JamonMXBeanImp(), JamonMXBeanImp.getObjectName());

            // gcMXBean gets notifications from gc events and saves results in jamon.
            registerGcMXBean(mBeanServer);
            registerMbeansFromPropsFile(mBeanServer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Load jamon configurable jmx monitors from the jamon properties file.
     * If there are no configurable monitors taken from the file then a set of default
     * ones will be created (pageHits, sql).
     */
    private static  void registerMbeansFromPropsFile(MBeanServer mBeanServer) throws Exception {
        JamonPropertiesLoader loader = new JamonPropertiesLoader();
        List<JamonPropertiesLoader.JamonJmxBean> jamonJmxBeans = loader.getMxBeans();
        Iterator<JamonPropertiesLoader.JamonJmxBean> iter = jamonJmxBeans.iterator();

        // register both the mxbean and the delta mxbean that displays diffs from when the bean was last called.
        while (iter.hasNext()) {
          JamonPropertiesLoader.JamonJmxBean beanInfo = iter.next();

          MonitorMXBean mXbean = MonitorMXBeanFactory.create(beanInfo.getLabel(), beanInfo.getUnits(), beanInfo.getName());
          mBeanServer.registerMBean(mXbean, MonitorMXBeanFactory.getObjectName(mXbean));

          MonitorMXBean  mXbeanDelta = MonitorMXBeanFactory.createDelta(beanInfo.getLabel(), beanInfo.getUnits(), beanInfo.getName());
          mBeanServer.registerMBean(mXbeanDelta, MonitorMXBeanFactory.getDeltaObjectName(mXbeanDelta));
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
            mBeanServer.unregisterMBean(JamonMXBeanImp.getObjectName());
            unregisterGcMXBean(mBeanServer);
            unregisterMbeansFromPropsFile(mBeanServer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read the properties file and unregister its configurable jamon jmx beans.  Note if the file has changed
     * since the server was brought up the beans unregistered might not match those that were loaded.
     *
     * @param mBeanServer
     * @throws Exception
     */
    private static  void unregisterMbeansFromPropsFile(MBeanServer mBeanServer) throws Exception {
        JamonPropertiesLoader loader = new JamonPropertiesLoader();
        List<JamonPropertiesLoader.JamonJmxBean> jamonJmxBeans = loader.getMxBeans();
        Iterator<JamonPropertiesLoader.JamonJmxBean> iter = jamonJmxBeans.iterator();

        while (iter.hasNext()) {
            JamonPropertiesLoader.JamonJmxBean beanInfo = iter.next();

            MonitorMXBean mXbean = MonitorMXBeanFactory.create(beanInfo.getLabel(), beanInfo.getUnits(), beanInfo.getName());
            mBeanServer.unregisterMBean(MonitorMXBeanFactory.getObjectName(mXbean));

            MonitorMXBean  mXbeanDelta = MonitorMXBeanFactory.createDelta(beanInfo.getLabel(), beanInfo.getUnits(), beanInfo.getName());
            mBeanServer.unregisterMBean(MonitorMXBeanFactory.getDeltaObjectName(mXbeanDelta));
        }
    }

    /**
     * Get a list of the garbage collector mbeans.  This is used primarily to regsiter jamon to listen for gc notifications.
     *
     * @param mBeanServer
     * @return Set of ObjectNames for gc jmx objects
     * @throws Exception
     */
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