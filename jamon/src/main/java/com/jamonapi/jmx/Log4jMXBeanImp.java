package com.jamonapi.jmx;

/**
 * Created by stevesouza on 11/19/14.
 */
public class Log4jMXBeanImp implements Log4jMXBean {
    private static final String UNITS = "log4j";

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
       return Utils.getCount(label, UNITS);
    }
}
