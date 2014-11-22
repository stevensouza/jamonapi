package com.jamonapi.jmx;

/**
 * MxBean that counts how many times each of the various log4j logging methods is called.
 */
public interface Log4jMXBean {
    public long getTrace();
    public long getDebug();
    public long getWarn();
    public long getInfo();
    public long getError();
    public long getFatal();
    public long getTotal();
}
