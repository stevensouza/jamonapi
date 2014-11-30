package com.jamonapi.jmx;

import com.jamonapi.MonitorFactory;

import javax.management.ObjectName;

/**
 * MXBean that simply displays jamons version.
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
