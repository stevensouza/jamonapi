package com.jamonapi.jmx;

import com.jamonapi.FrequencyDist;
import com.jamonapi.Monitor;
import com.jamonapi.Range;

import java.util.List;

/**
 * MxBean used for ms. time monitors as it also includes counts for each range.  Note it will work with any
 * monitor (for example bytes) however the ranges will have no meaning and so are set to 0.
 */
public class MonitorMsMXBeanImp extends MonitorMXBeanImp implements MonitorMsMXBean {

    public MonitorMsMXBeanImp(List<JamonJmxBeanProperty> jmxProperties) {
        super(jmxProperties);
    }

    public MonitorMsMXBeanImp(String label, String units) {
        super(label, units);
    }

    public MonitorMsMXBeanImp(String label, String units, String name) {
        super(label, units, name);
    }

    // get hits/count for each range such as 0_10ms. 10_20ms. etc.
    private long getRangeCount(int i) {
        Monitor mon = JmxUtils.getMonitor(jmxProperties);
        if (mon==null) {
            return 0;
        }

        Range range = mon.getRange();
        if (range==null) {
            return 0;
        }

        FrequencyDist[] dists = range.getFrequencyDists();
        if (dists==null) {
            return 0;
        }
        return (long) dists[i].getHits();
    }

    @Override
    public long get_Count00_LessThan_0ms() {
        return getRangeCount(0);
    }

    @Override
    public long get_Count01_0_10ms() {
        return getRangeCount(1);
    }

    @Override
    public long get_Count02_10_20ms() {
        return getRangeCount(2);
    }

    @Override
    public long get_Count03_20_40ms() {
        return getRangeCount(3);
    }

    @Override
    public long get_Count04_40_80ms() {
        return getRangeCount(4);
    }

    @Override
    public long get_Count05_80_160ms() {
        return getRangeCount(5);
    }

    @Override
    public long get_Count06_160_320ms() {
        return getRangeCount(6);
    }

    @Override
    public long get_Count07_320_640ms() {
        return getRangeCount(7);
    }

    @Override
    public long get_Count08_640_1280ms() {
        return getRangeCount(8);
    }

    @Override
    public long get_Count09_1280_2560ms() {
        return getRangeCount(9);
    }

    @Override
    public long get_Count10_2560_5120ms() {
        return getRangeCount(10);
    }

    @Override
    public long get_Count11_5120_10240ms() {
        return getRangeCount(11);
    }

    @Override
    public long get_Count12_10240_20480ms() {
        return getRangeCount(12);
    }

    @Override
    public long get_Count13_GreaterThan_20480ms() {
        return getRangeCount(13);
    }

}
