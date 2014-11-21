package com.jamonapi.jmx;

/**
 * MxBean that counts how many times each of the various log4j logging methods is called.
 */
public interface Log4jMXBean {
    public long getTRACE();
    public long getDEBUG();
    public long getWARN();
    public long getINFO();
    public long getERROR();
    public long getFATAL();
    public long getTOTAL();
}
