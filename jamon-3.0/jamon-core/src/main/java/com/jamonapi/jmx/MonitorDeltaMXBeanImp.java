package com.jamonapi.jmx;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.jamonapi.NullMonitor;

import java.util.Date;
import java.util.List;

/**
 * Calculate the delta since the last time the methods were called for a given jamon monitor key.
 * Note any of the delta objects only reset numbers for the specific method being called.
 * This works out ok if the use case is to call all methods at the same time as would happen in jconsole.
 * In this case all methods are called and so all are reset at the same time.  If only 1 method is called
 * then the deltas could get out of sync.
 */
public class MonitorDeltaMXBeanImp extends MonitorMXBeanImp {
    private static Monitor NULL_MONITOR = new NullMonitor();

    private MonitorDelta prevTotal = new MonitorDelta();
    private MonitorDelta prevAvg = new MonitorDelta();
    private MonitorDelta prevMin = new MonitorDelta();
    private MonitorDelta prevMax = new MonitorDelta();
    private MonitorDelta prevHits = new MonitorDelta();
    private MonitorDelta prevStdDev = new MonitorDelta();
    private MonitorDelta prevFistAccess = new MonitorDelta();
    private MonitorDelta prevLastAccess = new MonitorDelta();
    private MonitorDelta prevLastValue = new MonitorDelta();
    private MonitorDelta prevActive = new MonitorDelta();
    private MonitorDelta prevMaxActive = new MonitorDelta();
    private MonitorDelta prevAvgActive = new MonitorDelta();

    private MonitorDelta prevMon;

    public MonitorDeltaMXBeanImp(List<JamonJmxBeanProperty> jmxProperties) {
        super(jmxProperties);
    }
    public MonitorDeltaMXBeanImp(String label, String units) {
        this(label, units, label);
    }

    public MonitorDeltaMXBeanImp(String label, String units, String name) {
        super(label, units, name);
    }

    private Monitor getMon() {
        Monitor mon = JmxUtils.getMonitor(jmxProperties);
        if (mon==null) {
            return NULL_MONITOR;
        }

        return mon;
    }

    // return the value to be displayed via jmx/jconsole
    private MonitorDelta getDisplayDelta(MonitorDelta previousMon) {
        // Subtact the previous monitor from the current one. Note that the only value that truly has the delta is
        // displayDelta.  currentMon and previousMon are simply value wrappers for a regular monitor.
        MonitorDelta currentMon = new MonitorDelta(getMon());
        MonitorDelta displayDelta = currentMon.delta(previousMon);
        prevMon = currentMon;
        return displayDelta;
    }

    @Override
    public double getTotal() {
        double value = getDisplayDelta(prevTotal).getTotal();
        prevTotal = prevMon;
        return value;
    }

    @Override
    public double getAvg() {
        double value =  getDisplayDelta(prevAvg).getAvg();
        prevAvg = prevMon;
        return value;
    }

    @Override
    public double getMin() {
        double value =  getDisplayDelta(prevMin).getMin();
        prevMin = prevMon;
        return value;
    }

    @Override
    public double getMax() {
        double value =  getDisplayDelta(prevMax).getMax();
        prevMax = prevMon;
        return value;
    }

    @Override
    public double getHits() {
        double value =  getDisplayDelta(prevHits).getHits();
        prevHits = prevMon;
        return value;
    }

    @Override
    public double getStdDev() {
        double value =  getDisplayDelta(prevStdDev).getStdDev();
        prevStdDev = prevMon;
        return value;
    }

    @Override
    public Date getFirstAccess() {
        Date value = getDisplayDelta(prevFistAccess).getFirstAccess();
        prevFistAccess = prevMon;
        return value;
    }

    @Override
    public Date getLastAccess() {
        Date value = getDisplayDelta(prevLastAccess).getLastAccess();
        prevLastAccess = prevMon;
        return value;
    }

    @Override
    public double getLastValue() {
        double value =  getDisplayDelta(prevLastValue).getLastValue();
        prevLastValue = prevMon;
        return value;
    }

    @Override
    public double getActive() {
        double value =  getDisplayDelta(prevActive).getActive();
        prevActive = prevMon;
        return value;
    }

    @Override
    public double getMaxActive() {
        double value =  getDisplayDelta(prevMaxActive).getMaxActive();
        prevMaxActive = prevMon;
        return value;
    }

    @Override
    public double getAvgActive() {
        double value =  getDisplayDelta(prevAvgActive).getAvgActive();
        prevAvgActive = prevMon;
        return value;
    }

}
