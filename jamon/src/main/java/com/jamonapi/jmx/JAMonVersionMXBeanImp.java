package com.jamonapi.jmx;

import com.jamonapi.MonitorFactory;

import javax.management.ObjectName;

/**
 * Created by stevesouza on 11/30/14.
 */
public class JAMonVersionMXBeanImp implements JAMonVersionMXBean {

    public static ObjectName getObjectName() {
        return JmxUtils.getObjectName(JAMonVersionMXBeanImp.class.getPackage().getName() + ":type=current,name=JAMonVersion");
    }

    @Override
    public String getVersion() {
        return MonitorFactory.getVersion();
    }
}
