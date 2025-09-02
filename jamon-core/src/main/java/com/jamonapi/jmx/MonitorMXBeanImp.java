package com.jamonapi.jmx;

import com.jamonapi.Monitor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Base class for jamon configurable mbeans.  It exposes jamon metrics such as avg, hits, total etc.
 * It also adds the ability to look for the first monitor in a list of label, unit pairs.  This is useful for example
 * when tomcat and jetty have different monitor names, but in jmx we want to call them some common name like 'pageHits'.
 */
public class MonitorMXBeanImp implements MonitorMXBean {
    protected final List<JamonJmxBeanProperty> jmxProperties;
    protected final String label;
    protected final String units;
    protected final String name;


    public MonitorMXBeanImp(List<JamonJmxBeanProperty> jmxProperties) {
        this.label = jmxProperties.get(0).getLabel();
        this.units = jmxProperties.get(0).getUnits();
        this.name =  jmxProperties.get(0).getName();
        this.jmxProperties = jmxProperties;
    }

    public MonitorMXBeanImp(String label, String units) {
        this(label, units, label);
    }

    public MonitorMXBeanImp(String label, String units, String name) {
        this(init(label,units,name));
    }

    private static List<JamonJmxBeanProperty> init(String label, String units, String name) {
        JamonJmxBeanProperty property = new JamonJmxBeanPropertyDefault(label,units,name);
        List<JamonJmxBeanProperty> properties = new ArrayList<JamonJmxBeanProperty>();
        properties.add(property);
        return properties;
    }

    @Override
    public String getLabel() {
        return (String) JmxUtils.getValue(jmxProperties, "label", label);
    }

    @Override
    public String getUnits() {
        return (String) JmxUtils.getValue(jmxProperties, "units", units);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getTotal() {
        return getDouble(Monitor.TOTAL);
    }

    @Override
    public double getAvg() {
        return getDouble(Monitor.AVG);
    }

    @Override
    public double getMin() {
        return getDouble(Monitor.MIN);
    }

    @Override
    public double getMax() {
        return getDouble(Monitor.MAX);
    }

    @Override
    public double getHits() {
        return getDouble(Monitor.HITS);
    }

    @Override
    public double getStdDev() {
        return getDouble(Monitor.STDDEV);
    }

    @Override
    public Date getFirstAccess() {
        return getDate(Monitor.FIRSTACCESS);
    }

    @Override
    public Date getLastAccess() {
        return getDate(Monitor.LASTACCESS);
    }

    @Override
    public double getLastValue() {
        return getDouble(Monitor.LASTVALUE);
    }

    @Override
    public double getActive() {
        return getDouble(Monitor.ACTIVE);
    }

    @Override
    public double getMaxActive() {
        return getDouble(Monitor.MAXACTIVE);
    }

    @Override
    public double getAvgActive() {
        return getDouble(Monitor.AVGACTIVE);
    }

    private double getDouble(String  value) {
        return JmxUtils.getDouble(jmxProperties, value);
    }

    private Date getDate(String  value) {
        return JmxUtils.getDate(jmxProperties, value);
    }
}
