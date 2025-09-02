package com.jamonapi.jmx;

import com.jamonapi.utils.NumberDelta;

import java.util.ArrayList;
import java.util.List;

/**
 * In addition to tracking the normal jamon aggregates this also displays counts for the time ranges for ms. based
 * monitors. i.e. 0_10ms, 10_20ms etc.
 */
public class MonitorDeltaMsMXBeanImp extends MonitorDeltaMXBeanImp implements MonitorMsMXBean {
    private static final int NUM_RANGES=14;
    private NumberDelta[] delta = new NumberDelta[NUM_RANGES];
    private MonitorMsMXBeanImp bean;

    public MonitorDeltaMsMXBeanImp(List<JamonJmxBeanProperty> jmxProperties) {
        super(jmxProperties);
        bean = new MonitorMsMXBeanImp(jmxProperties);
        for (int i=0;i<delta.length;i++) {
            delta[i] = new NumberDelta();
        }
    }
    public MonitorDeltaMsMXBeanImp(String label, String units) {
        this(label, units, label);
    }

    public MonitorDeltaMsMXBeanImp(String label, String units, String name) {
        this(init(label, units, name));
    }

    private static List<JamonJmxBeanProperty> init(String label, String units, String name) {
        List<JamonJmxBeanProperty> properties = new ArrayList<JamonJmxBeanProperty>();
        properties.add(new JamonJmxBeanPropertyDefault(label,units,name));
        return properties;
    }

    @Override
    public long get_Count00_LessThan_0ms() {
        return getDelta(0, bean.get_Count00_LessThan_0ms());
    }

    @Override
    public long get_Count01_0_10ms() {
        return getDelta(1, bean.get_Count01_0_10ms());
    }

    @Override
    public long get_Count02_10_20ms() {
        return getDelta(2, bean.get_Count02_10_20ms());
    }

    @Override
    public long get_Count03_20_40ms() {
        return getDelta(3, bean.get_Count03_20_40ms());
    }

    @Override
    public long get_Count04_40_80ms() {
        return getDelta(4, bean.get_Count04_40_80ms());
    }

    @Override
    public long get_Count05_80_160ms() {
        return getDelta(5, bean.get_Count05_80_160ms());
    }

    @Override
    public long get_Count06_160_320ms() {
        return getDelta(6, bean.get_Count06_160_320ms());
    }

    @Override
    public long get_Count07_320_640ms() {
        return getDelta(7, bean.get_Count07_320_640ms());
    }

    @Override
    public long get_Count08_640_1280ms() {
        return getDelta(8, bean.get_Count08_640_1280ms());
    }

    @Override
    public long get_Count09_1280_2560ms() {
        return getDelta(9, bean.get_Count09_1280_2560ms());
    }

    @Override
    public long get_Count10_2560_5120ms() {
        return getDelta(10, bean.get_Count10_2560_5120ms());
    }

    @Override
    public long get_Count11_5120_10240ms() {
        return getDelta(11, bean.get_Count11_5120_10240ms());
    }

    @Override
    public long get_Count12_10240_20480ms() {
        return getDelta(12, bean.get_Count12_10240_20480ms());
    }

    @Override
    public long get_Count13_GreaterThan_20480ms() {
        return getDelta(13, bean.get_Count13_GreaterThan_20480ms());
    }

    private long getDelta(int i, long count) {
        return (long) delta[i].setValue(count).getDelta();
    }

}
