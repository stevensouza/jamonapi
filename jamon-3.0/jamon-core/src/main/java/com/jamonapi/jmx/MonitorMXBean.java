package com.jamonapi.jmx;

import java.util.Date;

/**
 * Interface for configurable jamon jmx beans.  Essentially it exposes the values of a monitor.
 */
public interface MonitorMXBean {
        public double getTotal();
        public double getAvg();
        public double getMin();
        public double getMax();
        public double getHits();
        public double getStdDev();
        public Date getFirstAccess();
        public Date getLastAccess();
        public double getLastValue();
        public double getActive();
        public double getMaxActive();
        public double getAvgActive();
        public String getLabel();
        public String getUnits();
        public String getName();
}
