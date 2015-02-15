package com.jamonapi.jmx;

import com.jamonapi.JamonPropertiesLoader;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.*;

/**
 * Class that creates and destroys jamon jmx monitors.  It also provides some utility functions used in the other
 * jamon jmx classes.
 */
 public class JmxUtils {
    private static final Double DOUBLE_ZERO=0.0;

    /**
     * Return the first monitor that exists in the passed in list.
     *
     * @param jmxBeanProperties
     * @return The found monitor based on label, and units or null if none is found
     */
    static Monitor getMonitor(List<JamonJmxBeanProperty> jmxBeanProperties) {
        for (JamonJmxBeanProperty jmxBeanProperty: jmxBeanProperties) {
            if (MonitorFactory.exists(jmxBeanProperty.getLabel(), jmxBeanProperty.getUnits())) {
                return MonitorFactory.getMonitor(jmxBeanProperty.getLabel(), jmxBeanProperty.getUnits());
            }
        }

        return null;
    }

    /**
     * Return the value  of the first passed in monitor that exists otherwise return 0
     *
     * @param jmxBeanProperties List of jmx bean properties to check for the passed in value.
     * @param value string representing the metric to return i.e. avg, hits etc.
     * @param defaultValue value to return if the passed in parameter is not found.

     * @return the metric associated with the first monitor found.
     */
    static Object getValue(List<JamonJmxBeanProperty> jmxBeanProperties, String value, Object defaultValue) {
        Monitor mon = getMonitor(jmxBeanProperties);
        if (mon==null) {
            return defaultValue;
        }

        Object retValue = mon.getValue(value);
        if (retValue==null) {
            return defaultValue;
        }
        return retValue;
    }

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
     * Return hits/count of the first passed in monitor that exists otherwise return 0
     *
     * @param jmxBeanProperties jamon label
     * @return count
     */
    static long getCount(List<JamonJmxBeanProperty> jmxBeanProperties) {
        return (long) getDouble(jmxBeanProperties, Monitor.HITS);
    }

    /**
     * Return the value of the passed in monitor if it exists otherwise return 0
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
     * Return the value of the first passed in monitor that exists otherwise return 0.0
     *
     * @param jmxBeanProperties List of jmx bean properties to check for the passed in value.
     * @param value string representing the metric to return i.e. avg, hits etc.
     * @return the metric
     */
    static double getDouble(List<JamonJmxBeanProperty> jmxBeanProperties, String value) {
        return (Double) getValue(jmxBeanProperties, value, DOUBLE_ZERO);
    }


    /**
     *
     * @param label jamon label
     * @param units jamon units
     * @param value string representing the date metric to return lastaccess, firstaccess.
     * @return The date associated with the jamon monitor
     */
    static Date getDate(String label, String units, String value) {
        if (MonitorFactory.exists(label, units)) {
            Monitor mon = MonitorFactory.getMonitor(label, units);
            return  (Date) mon.getValue(value);
        }

        return null;
    }

    /**
     *
     * @param jmxBeanProperties list of jamon properties to check for the associated date
     * @param value string representing the date metric to return lastaccess, firstaccess.
     * @return The date associated with the first jamon monitor that exist or else a null if none exist.
     */
    static Date getDate(List<JamonJmxBeanProperty> jmxBeanProperties, String value) {
        return (Date) getValue(jmxBeanProperties, value, null);
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
        try {
            mBeanServer.registerMBean(new Log4jMXBeanImp(), Log4jMXBeanImp.getObjectName());
            mBeanServer.registerMBean(new ExceptionMXBeanImp(), ExceptionMXBeanImp.getObjectName());
            mBeanServer.registerMBean(new ExceptionDeltaMXBeanImp(), ExceptionDeltaMXBeanImp.getObjectName());
            mBeanServer.registerMBean(new Log4jDeltaMXBeanImp(), Log4jDeltaMXBeanImp.getObjectName());
            mBeanServer.registerMBean(new JamonMXBeanImp(), JamonMXBeanImp.getObjectName());
            mBeanServer.registerMBean(new HttpStatusMXBeanImp(), HttpStatusMXBeanImp.getObjectName());
            mBeanServer.registerMBean(new HttpStatusDeltaMXBeanImp(), HttpStatusDeltaMXBeanImp.getObjectName());

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
        List<String> jamonJmxBeanProperties = loader.getMxBeans();
        Iterator<String> iter = jamonJmxBeanProperties.iterator();

        // register both the mxbean and the delta mxbean that displays diffs from when the bean was last called.
        while (iter.hasNext()) {
            String beanInfo = iter.next();

            MonitorMXBean mXbean = MonitorMXBeanFactory.create(beanInfo);
            mBeanServer.registerMBean(mXbean, MonitorMXBeanFactory.getObjectName(mXbean));

            MonitorMXBean  mXbeanDelta = MonitorMXBeanFactory.createDelta(beanInfo);
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
            mBeanServer.unregisterMBean(HttpStatusMXBeanImp.getObjectName());
            mBeanServer.unregisterMBean(HttpStatusDeltaMXBeanImp.getObjectName());
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
        List<String> jamonJmxBeanProperties = loader.getMxBeans();
        Iterator<String> iter = jamonJmxBeanProperties.iterator();

        while (iter.hasNext()) {
            String beanInfo = iter.next();

            MonitorMXBean mXbean = MonitorMXBeanFactory.create(beanInfo);
            mBeanServer.unregisterMBean(MonitorMXBeanFactory.getObjectName(mXbean));

            MonitorMXBean  mXbeanDelta = MonitorMXBeanFactory.createDelta(beanInfo);
            mBeanServer.unregisterMBean(MonitorMXBeanFactory.getDeltaObjectName(mXbeanDelta));
        }
    }

    /**
     * Get a list of the garbage collector mbeans.  This is used primarily to regsiter jamon to listen for gc notifications.
     * Note if the GarbageCollector mbean doesn't exist an empty collection will be returned.
     *
     * @param mBeanServer
     * @return Set of ObjectNames for gc jmx objects
     * @throws Exception
     */
    public static Set<ObjectName> getGarbageCollectionMbeans(MBeanServer mBeanServer) throws Exception {
        return queryMBeans(mBeanServer, "type=GarbageCollector");
    }

    static Set<ObjectName> queryMBeans(MBeanServer mBeanServer, String containsStr) {
        Set<ObjectName> mbeans = mBeanServer.queryNames(null, null);
        Set<ObjectName> gcMbeans = new HashSet<ObjectName>();
        for (ObjectName objectInstance : mbeans) {
            if (objectInstance.toString().contains(containsStr)) {
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
          unregisterAllMXBeans(mBeanServer);
        } catch (Throwable e) {
            // fail silently
        }
    }

    private static void unregisterAllMXBeans(MBeanServer mBeanServer) throws MBeanRegistrationException, InstanceNotFoundException {
         Set<ObjectName> gcMbeans = queryMBeans(mBeanServer, "Jamon.Gc.");
         for (ObjectName name : gcMbeans) {
            mBeanServer.unregisterMBean(name);
         }
    }

}