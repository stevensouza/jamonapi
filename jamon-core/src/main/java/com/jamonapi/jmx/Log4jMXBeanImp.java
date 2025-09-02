package com.jamonapi.jmx;

import javax.management.ObjectName;

/**
 * MXBean that exposes jamon log4j metrics. Note jamon log4j tracking must be enabled. It tracks counts
 * for each of the log levels (DEBUG, INFO,...)
 *
 */
public class Log4jMXBeanImp implements Log4jMXBean {
    public static ObjectName getObjectName() {
       return JmxUtils.getObjectName(Log4jMXBeanImp.class.getPackage().getName() + ":type=current,name=Log4j");
    }

    @Override
    public long getTrace() {
        return getCount("com.jamonapi.log4j.JAMonAppender.TRACE");
    }

    @Override
    public long getDebug() {
        return getCount("com.jamonapi.log4j.JAMonAppender.DEBUG");
    }

    @Override
    public long getWarn() {
        return getCount("com.jamonapi.log4j.JAMonAppender.WARN");
    }

    @Override
    public long getInfo() {
        return getCount("com.jamonapi.log4j.JAMonAppender.INFO");
    }

    @Override
    public long getError() {
        return getCount("com.jamonapi.log4j.JAMonAppender.ERROR");
    }

    @Override
    public long getFatal() {
        return getCount("com.jamonapi.log4j.JAMonAppender.FATAL");
    }

    @Override
    public long getTotal() {
        return getCount("com.jamonapi.log4j.JAMonAppender.TOTAL");
    }

    private long getCount(String label) {
       return JmxUtils.getCount(label, UNITS);
    }
}
