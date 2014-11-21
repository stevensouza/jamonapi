package com.jamonapi.jmx;

/**
 * MxBean that tracks the count of the number of exceptions thrown.  It also shows the full stacktrace of the
 * most recent exception thrown.
 */
public interface ExceptionMXBean {
    /**
     * Get the stacktrace in string format of the most recently thrown exception.
     * @return stacktrace
     */
    public String getMostRecentException();

    /**
     * Get the total count of exceptions thrown.
     *
     * @return number of exceptions thrown
     */
    public long getExceptionCount();
}
