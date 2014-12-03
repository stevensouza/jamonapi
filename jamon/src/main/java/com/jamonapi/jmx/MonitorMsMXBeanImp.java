package com.jamonapi.jmx;

import com.jamonapi.FrequencyDist;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.jamonapi.Range;

import javax.management.ObjectName;

/**
 * MxBean used for ms. time monitors as it also includes counts for each range.  Note it will work with any
 * monitor (for example bytes) however the ranges will have no meaning and so are set to 0.
 */
public class MonitorMsMXBeanImp extends MonitorMXBeanImp implements MonitorMsMXBean {

    public static MonitorMsMXBeanImp create(String label, String units, String name) {
        MonitorMsMXBeanImp bean = null;
        if (name == null || "".equals(name.trim())) {
            bean = new MonitorMsMXBeanImp(label.trim(), units.trim());
        } else {
            bean = new MonitorMsMXBeanImp(label.trim(), units.trim(), name.trim());
        }

        return bean;
    }

    public static ObjectName getObjectName(MonitorMsMXBeanImp beanImp) {
        return JmxUtils.getObjectName(MonitorMXBean.class.getPackage().getName() + ":type=current,name="+beanImp.getName());
    }
    public MonitorMsMXBeanImp(String label, String units) {
        super(label, units);
    }

    public MonitorMsMXBeanImp(String label, String units, String name) {
        super(label, units, name);
    }

    private long getCount(int i) {
        Monitor mon = MonitorFactory.getMonitor(label, units);
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
        return getCount(0);
    }

    @Override
    public long get_Count01_0_10ms() {
        return getCount(1);
    }

    @Override
    public long get_Count02_10_20ms() {
        return getCount(2);
    }

    @Override
    public long get_Count03_20_40ms() {
        return getCount(3);
    }

    @Override
    public long get_Count04_40_80ms() {
        return getCount(4);
    }

    @Override
    public long get_Count05_80_160ms() {
        return getCount(5);
    }

    @Override
    public long get_Count06_160_320ms() {
        return getCount(6);
    }

    @Override
    public long get_Count07_320_640ms() {
        return getCount(7);
    }

    @Override
    public long get_Count08_640_1280ms() {
        return getCount(8);
    }

    @Override
    public long get_Count09_1280_2560ms() {
        return getCount(9);
    }

    @Override
    public long get_Count10_2560_5120ms() {
        return getCount(10);
    }

    @Override
    public long get_Count11_5120_10240ms() {
        return getCount(11);
    }

    @Override
    public long get_Count12_10240_20480ms() {
        return getCount(12);
    }

    @Override
    public long get_Count13_GreaterThan_20480ms() {
        return getCount(13);
    }

}
