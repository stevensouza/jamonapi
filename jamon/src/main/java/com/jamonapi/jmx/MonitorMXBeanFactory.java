package com.jamonapi.jmx;

import javax.management.ObjectName;

/**
 * Created by stevesouza on 12/3/14.
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
            bean = new MonitorDeltaMXBeanImp(label.trim(), units.trim(), name.trim());
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
