package com.jamonapi.jmx;

import com.jamonapi.Monitor;

import java.util.Date;

/**
* Simple struct like class to measure changes in 2 Monitors i.e. Deltas
*/
class MonitorDelta {

    private double total;
    private double hits;
    private double avg;
    private double min;
    private double max;
    private double stdDev;
    private Date firstAccess;
    private Date lastAccess;
    private double lastValue;
    private double active;
    private double maxActive;
    private double avgActive;

    public MonitorDelta() {
    }

    public MonitorDelta(Monitor mon) {
        hits = mon.getHits();
        total = mon.getTotal();
        avg = mon.getAvg();
        min = mon.getMin();
        max = mon.getMax();
        stdDev = mon.getStdDev();
        firstAccess = mon.getFirstAccess();
        lastAccess = mon.getLastAccess();
        lastValue = mon.getLastValue();
        active = mon.getActive();
        maxActive = mon.getMaxActive();
        avgActive = mon.getAvgActive();
    }

    /**
     * Take a previous monitors values and subtract the passed in values from it.  If the monitors are of the same
     * concept then change will be measured.
     *
     * @param values
     * @return
     */
    public MonitorDelta minus(MonitorDelta values) {
        MonitorDelta delta = new MonitorDelta();
        delta.hits = hits - values.getHits();
        delta.total = total - values.getTotal();
        delta.avg = avg - values.getAvg();
        delta.min = min - values.getMin();
        delta.max = max - values.getMax();
        delta.stdDev = stdDev - values.getStdDev();
        delta.firstAccess = values.getFirstAccess();
        delta.lastAccess = values.getLastAccess();
        delta.lastValue = values.getLastValue();
        delta.active = active - values.getActive();
        delta.maxActive = maxActive - values.getMaxActive();
        delta.avgActive = avgActive - values.getAvgActive();
        return delta;
    }

    public double getTotal() {
        return total;
    }

    public double getHits() {
        return hits;
    }

    public double getAvg() {
        return avg;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getStdDev() {
        return stdDev;
    }

    public Date getFirstAccess() {
        return firstAccess;
    }

    public Date getLastAccess() {
        return lastAccess;
    }

    public double getLastValue() {
        return lastValue;
    }

    public double getActive() {
        return active;
    }

    public double getMaxActive() {
        return maxActive;
    }

    public double getAvgActive() {
        return avgActive;
    }

}
