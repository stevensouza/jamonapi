package com.jamonapi;

/**
 * FrequencyDist's are what Ranges are made of.  They are buckets of aggregate stats
 * within the monitor.  For example a FrequencyDist for ms. may be 0-10 ms., 20-40 ms.
 * etc.  They have end points, and all FrequencyDists within a range equate to
 * the entire range of possible values for a monitor.
 *
 */

public interface FrequencyDist extends MonitorInt {
    /** Get the end value of the FrequencyDist.*/
    public double getEndValue();

}
