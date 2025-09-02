package com.jamonapi.jmx;

import java.util.Date;

/**
 * MxBean that gives information about the most recent gc firing.  This could be a major or minor collection.
 */
public interface GcMXBean {
    /**
     * @return String containing detailed information about the most recent gc firing.
     */
    public String getGcInfo();

    /**
     *
     * @return date of the most recent gc firing.
     */
    public Date getWhen();

    /**
     * @return duration of the most recent gc firing in ms.
     */
    public long getDuration();
}
