package com.jamonapi.jmx;

import javax.management.ObjectName;
import java.util.Date;

/**
 * Created by stevesouza on 11/21/14.
 */
public class MonitorMXBeanImp implements MonitorMXBean {

    protected final String label;
    protected final String units;
    protected final String name;

    // see MonitorInt for values.  I didn't use those values as it is package private data and I didn't want
    // to change that at this time.
    private static final String LASTVALUE = "lastvalue";
    private static final String MAX = "max";
    private static final String MIN = "min";
    private static final String MAXACTIVE = "maxactive";
    private static final String TOTAL = "total";
    private static final String AVG = "avg";
    private static final String HITS = "hits";
    private static final String STDDEV = "stddev";
    private static final String FIRSTACCESS = "firstaccess";
    private static final String LASTACCESS = "lastaccess";
    private static final String ACTIVE = "active";
    private static final String AVGACTIVE = "avgactive";

    public static MonitorMXBeanImp create(String label, String units, String name) {
        MonitorMXBeanImp bean = null;
        if (name == null || "".equals(name.trim())) {
            bean = new MonitorMXBeanImp(label.trim(), units.trim());
        } else {
            bean = new MonitorMXBeanImp(label.trim(), units.trim(), name.trim());
        }

        return bean;
    }

    public MonitorMXBeanImp(String label, String units) {
        this(label, units, label);
    }

    public MonitorMXBeanImp(String label, String units, String name) {
        this.label = label;
        this.units = units;
        this.name = name;
    }

    public static ObjectName getObjectName(MonitorMXBeanImp beanImp) {
        return JmxUtils.getObjectName(MonitorMXBean.class.getPackage().getName() + ":type=current,name="+beanImp.getName());
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getUnits() {
        return units;
    }

    public String getName() {
        return name;
    }

    @Override
    public double getTotal() {
        return getDouble(TOTAL);
    }

    @Override
    public double getAvg() {
        return getDouble(AVG);
    }

    @Override
    public double getMin() {
        return getDouble(MIN);
    }

    @Override
    public double getMax() {
        return getDouble(MAX);
    }

    @Override
    public double getHits() {
        return getDouble(HITS);
    }

    @Override
    public double getStdDev() {
        return getDouble(STDDEV);
    }

    @Override
    public Date getFirstAccess() {
        return getDate(FIRSTACCESS);
    }

    @Override
    public Date getLastAccess() {
        return getDate(LASTACCESS);
    }

    @Override
    public double getLastValue() {
        return getDouble(LASTVALUE);
    }

    @Override
    public double getActive() {
        return getDouble(ACTIVE);
    }

    @Override
    public double getMaxActive() {
        return getDouble(MAXACTIVE);
    }

    @Override
    public double getAvgActive() {
        return getDouble(AVGACTIVE);
    }

    private double getDouble(String  value) {
        return JmxUtils.getDouble(label, units, value);
    }

    private Date getDate(String  value) {
        return JmxUtils.getDate(label, units, value);
    }
}
