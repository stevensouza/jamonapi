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
     * Take the current monitors values and subtract the passed in values from it.  If the monitors are of the same
     * concept then change will be measured.
     *
     * @param previous
     * @return
     */
    public MonitorDelta delta(MonitorDelta previous) {
        MonitorDelta delta = new MonitorDelta();
        delta.label = label;
        delta.units = units;
        delta.hits = hits - previous.getHits();
        delta.total = total - previous.getTotal();
        double currentAverage = (hits>previous.getHits()) ? (total - previous.getTotal())/(hits - previous.getHits()) : 0;
        delta.avg = currentAverage;
        delta.min = min - previous.getMin();
        delta.max = max - previous.getMax();
        delta.stdDev = stdDev - previous.getStdDev();
        delta.firstAccess = firstAccess;
        delta.lastAccess = lastAccess;
        delta.lastValue = lastValue - previous.getLastValue();
        delta.active = active - previous.getActive();
        delta.maxActive = maxActive - previous.getMaxActive();
        delta.avgActive = avgActive - previous.getAvgActive();

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
