package com.jamonapi.jmx;

import com.jamonapi.Monitor;

import java.util.Date;

/**
* Simple struct like class to measure changes in 2 Monitors i.e. Deltas
*/
class MonitorDelta {

    private String label;
    private String units;
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
        label = mon.getLabel();
        units = mon.getUnits();
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
     * @param current
     * @return
     */
    public MonitorDelta minus(MonitorDelta current) {
        MonitorDelta delta = new MonitorDelta();
        delta.label = current.getLabel();
        delta.units = current.getUnits();
        delta.hits = current.getHits() - hits;
        delta.total = current.getTotal() - total;
        double currentAverage = (current.getHits()>hits) ? (current.getTotal() - total)/(current.getHits() - hits) : avg;
        delta.avg = currentAverage - avg;
        delta.min = current.getMin() - min;
        delta.max = current.getMax() - max;
        delta.stdDev = current.getStdDev() - stdDev;
        delta.firstAccess = current.getFirstAccess();
        delta.lastAccess = current.getLastAccess();
        delta.lastValue = current.getLastValue();
        delta.active = current.getActive() - active;
        delta.maxActive = current.getMaxActive() - maxActive;
        delta.avgActive = current.getAvgActive() - avgActive;

        return delta;
    }

    public String getLabel() {
        return label;
    }

    public String getUnits() {
        return units;
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
