package com.jamonapi.jmx;

/**
 * MxBean that counts how many times each of the various log4j logging methods is called (i.e.
 * TRACE, DEBUG, INFO, ERROR, ...
 */
public interface Log4jMXBean {
    static final String UNITS = "log4j";
    public long getTrace();
    public long getDebug();
    public long getWarn();
    public long getInfo();
    public long getError();
    public long getFatal();
    public long getTotal();
}
