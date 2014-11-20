package com.jamonapi.jmx;

/**
 * Created by stevesouza on 11/19/14.
 */
public interface Log4jMXBean {
    public static final String NAME = "sandbox.jamonapi:name=Log4j";

    public long getTRACE();
    public long getDEBUG();
    public long getWARN();
    public long getINFO();
    public long getERROR();
    public long getFATAL();
    public long getTOTAL();
}
