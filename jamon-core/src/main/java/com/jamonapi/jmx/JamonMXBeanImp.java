package com.jamonapi.jmx;

import com.jamonapi.MonitorFactory;

import javax.management.ObjectName;

/**
 * MXBean that allows you to manage jamon
 */
public class JamonMXBeanImp implements JamonMXBean {

    public static ObjectName getObjectName() {
        return JmxUtils.getObjectName(JamonMXBeanImp.class.getPackage().getName() + ":type=current,name=Jamon");
    }

    @Override
    public String getVersion() {
        return MonitorFactory.getVersion();
    }

    @Override
    public boolean getEnabled() {
        return MonitorFactory.isEnabled();
    }

    @Override
    public void setEnabled(boolean enable) {
        MonitorFactory.setEnabled(enable);
    }

    @Override
    public void reset() {
        MonitorFactory.reset();
    }
}
