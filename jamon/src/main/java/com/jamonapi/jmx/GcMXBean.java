package com.jamonapi.jmx;

import java.util.Date;

/**
 * Created by stevesouza on 11/28/14.
 */
public interface GcMXBean {
    public String getGcInfo();
    public Date getWhen();
    public long getDuration();
}
