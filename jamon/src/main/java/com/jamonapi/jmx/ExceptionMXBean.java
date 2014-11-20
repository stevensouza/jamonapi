package com.jamonapi.jmx;

/**
 * Created by stevesouza on 11/19/14.
 */
public interface ExceptionMXBean {
    public static final String NAME = "sandbox.jamonapi:name=Log4j";

    public String getMostRecentException();
    public long getExceptionCount();
}
