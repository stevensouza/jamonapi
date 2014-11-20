package com.jamonapi.jmx;

import javax.management.ObjectName;

/**
 * MXBean that exposes jamon log4j metrics.
 */
public class Log4jMXBeanImp implements Log4jMXBean {
    private static final String UNITS = "log4j";
    public static ObjectName getObjectName() {
       return JmxUtils.getObjectName(Log4jMXBeanImp.class.getPackage().getName() + ":type=current,name=log4j");
    }

    @Override
    public long getTRACE() {
        return getCount("com.jamonapi.log4j.JAMonAppender.TRACE");
    }

    @Override
    public long getDEBUG() {
        return getCount("com.jamonapi.log4j.JAMonAppender.DEBUG");
    }

    @Override
    public long getWARN() {
        return getCount("com.jamonapi.log4j.JAMonAppender.WARN");
    }

    @Override
    public long getINFO() {
        return getCount("com.jamonapi.log4j.JAMonAppender.INFO");
    }

    @Override
    public long getERROR() {
        return getCount("com.jamonapi.log4j.JAMonAppender.ERROR");
    }

    @Override
    public long getFATAL() {
        return getCount("com.jamonapi.log4j.JAMonAppender.FATAL");
    }

    @Override
    public long getTOTAL() {
        return getCount("com.jamonapi.log4j.JAMonAppender.TOTAL");
    }

    private long getCount(String label) {
       return JmxUtils.getCount(label, UNITS);
    }
}
