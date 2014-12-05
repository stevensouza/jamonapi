package com.jamonapi.jmx;

import javax.management.ObjectName;

/**
 * Factory for creating configurable jamon jmx mbeans.  It will create jmx bean with time ranges for monitors with
 * units 'ms.'. With any other units these ranges won't be added to the mbean.  It can also create delta jmx
 * mbeans.  The user can create their own configurable jmx mbeans by configuring the jamonapi.properties file.
 */
public class MonitorMXBeanFactory {

    public static MonitorMXBean create(String label, String units, String name) {
        MonitorMXBean bean = null;
        if (name == null || "".equals(name.trim())) {
            name = label;
        }

        if ("ms.".equals(units)) {
            bean = new MonitorMsMXBeanImp(label.trim(), units.trim(), name.trim());
        } else {
            bean = new MonitorMXBeanImp(label.trim(), units.trim(), name.trim());
        }

        return bean;
    }

    public static MonitorMXBean createDelta(String label, String units, String name) {
        MonitorMXBean bean = null;
        if (name == null || "".equals(name.trim())) {
            name = label;
        }

        if ("ms.".equals(units)) {
            bean = new MonitorDeltaMsMXBeanImp(label.trim(), units.trim(), name.trim());
        } else {
            bean = new MonitorDeltaMXBeanImp(label.trim(), units.trim(), name.trim());
        }

        return bean;
    }


    public static ObjectName getObjectName(MonitorMXBean beanImp) {
        return JmxUtils.getObjectName(beanImp.getClass().getPackage().getName() + ":type=current,name="+beanImp.getName());
    }

    public static ObjectName getDeltaObjectName(MonitorMXBean beanImp) {
        return JmxUtils.getObjectName(MonitorMXBean.class.getPackage().getName() + ":type=delta,name="+beanImp.getName());
    }

}
