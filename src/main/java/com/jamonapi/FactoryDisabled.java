package com.jamonapi;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * Factory that returns null monitors when JAMon is disabled.  Method are noops.
 * Any methods that return Monitors return NullMonitors (noop monitors).
 */

public final class FactoryDisabled implements MonitorFactoryInterface {
    private Monitor nullMon=new NullMonitor();
    private MonitorComposite compositeMon=new MonitorComposite();
    private MonitorFactoryInterface factoryEnabled;

    /** Creates a new instance of FactoryDisabled */
    public FactoryDisabled(MonitorFactoryInterface factoryEnabled) {
        this.factoryEnabled=factoryEnabled;
    }

    public Monitor add(MonKey key, double value) {
        return nullMon;
    }

    public Monitor add(String label, String units, double value) {
        return nullMon;
    }

    public Monitor start() {
        return nullMon;
    }

    public Monitor start(MonKey key) {
        return nullMon;
    }

    public Monitor start(String label) {
        return nullMon;
    }

    public Monitor startNano(String label) {
        return nullMon;
    }

    public Monitor startNano(MonKey key) {
        return nullMon;
    }
    public Monitor startPrimary(MonKey key) {
        return nullMon;
    }

    public Monitor startPrimary(String label) {
        return nullMon;
    }

    public Monitor getMonitor(MonKey key) {
        return nullMon;
    }

    public Monitor getMonitor(String label, String units) {
        return nullMon;
    }

    public Monitor getTimeMonitor(MonKey key) {
        return nullMon;
    }

    public Monitor getTimeMonitor(String label) {
        return nullMon;
    }

    public void remove(MonKey key) {
    }

    public void remove(String label, String units) {
    }

    public boolean exists(MonKey key) {
        return false;
    }
    public boolean exists(String label, String units) {
        return false;
    }

    public MonitorComposite getComposite(String units) {
        return compositeMon;
    }

    public MonitorComposite getRootMonitor() {
        return compositeMon;
    }

    public int getNumRows() {
        return 0;
    }

    public String[] getRangeHeader() {
        return factoryEnabled.getRangeHeader();
    }

    public Object[][] getRangeNames() {
        return factoryEnabled.getRangeNames();
    }

    public String getVersion() {
        return VERSION;
    }

    public void setMap(java.util.Map map) {
    }

    public void setRangeDefault(String key, RangeHolder rangeHolder) {
    }

    public void reset() {
    }

    public void enableGlobalActive(boolean enable) {
    }

    public boolean isGlobalActiveEnabled() {
        return false;
    }

    public Monitor getMonitor() {
        return nullMon;
    }

    public Iterator iterator() {
        return Collections.EMPTY_LIST.iterator();
    }

    public Map getMap() {
        return Collections.EMPTY_MAP;
    }

    public void enableActivityTracking(boolean enable) {
    }

    public boolean isActivityTrackingEnabled() {
        return false;
    }

    public int getMaxNumMonitors() {
        return 0;
    }

    public void setMaxNumMonitors(int maxMonitors) {
    }

    public void enableTotalKeySizeTracking() {
    }

    public void disableTotalKeySizeTracking() {
    }

    public long getTotalKeySize() {
        return 0;
    }

    public boolean isTotalKeySizeTrackingEnabled() {
        return false;
    }

    public int getMaxSqlSize() {
        return 0;
    }

    public void setMaxSqlSize(int size) {

    }

}
