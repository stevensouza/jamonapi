package com.jamonapi.jmx;

import com.jamonapi.MonitorFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MonitorMsMXBeanImpTest {
    private static final String LABEL="timer";
    private static final String UNITS="ms.";
    private  MonitorMsMXBeanImp bean;

    @Before
    public void setUp() throws Exception {
        MonitorFactory.reset();
        bean = (MonitorMsMXBeanImp) MonitorMXBeanFactory.create(LABEL, UNITS, LABEL);
        assertThat(bean.getAvg()).isEqualTo(0);
    }

    @After
    public void tearDown() throws Exception {
        MonitorFactory.reset();
    }

    @Test
    public void testNoExist() throws Exception {
        MonitorMsMXBeanImp bean = new MonitorMsMXBeanImp("noexist", "units");
        assertThat(bean.getAvg()).isEqualTo(0);
        assertThat(bean.get_Count01_0_10ms()).isEqualTo(0);
    }

    @Test
    public void testNotTimeMonitor() throws Exception {
        MonitorFactory.add("nottimemonitor", "units", 100);
        MonitorMsMXBeanImp bean = new MonitorMsMXBeanImp("nottimemonitor", "units");
        assertThat(bean.get_Count00_LessThan_0ms()).isEqualTo(0);
        assertThat(bean.getAvg()).isEqualTo(100);
    }

    @Test
    public void testGet_Count00_LessThan_0ms() throws Exception {
        assertThat(bean.get_Count00_LessThan_0ms()).isEqualTo(0);
        MonitorFactory.getTimeMonitor(LABEL).add(-100);
        assertThat(bean.get_Count00_LessThan_0ms()).isEqualTo(1);
        assertThat(bean.getAvg()).isEqualTo(-100);
    }

    @Test
    public void testGet_Count01_0_10ms() throws Exception {
        assertThat(bean.get_Count01_0_10ms()).isEqualTo(0);
        MonitorFactory.getTimeMonitor(LABEL).add(5);
        assertThat(bean.get_Count01_0_10ms()).isEqualTo(1);
    }

    @Test
    public void testGet_Count02_10_20ms() throws Exception {
        assertThat(bean.get_Count02_10_20ms()).isEqualTo(0);
        MonitorFactory.getTimeMonitor(LABEL).add(15);
        assertThat(bean.get_Count02_10_20ms()).isEqualTo(1);
    }

    @Test
    public void testGet_Count03_20_40ms() throws Exception {
        assertThat(bean.get_Count03_20_40ms()).isEqualTo(0);
        MonitorFactory.getTimeMonitor(LABEL).add(30);
        assertThat(bean.get_Count03_20_40ms()).isEqualTo(1);
    }

    @Test
    public void testGet_Count04_40_80ms() throws Exception {
        assertThat(bean.get_Count04_40_80ms()).isEqualTo(0);
        MonitorFactory.getTimeMonitor(LABEL).add(45);
        assertThat(bean.get_Count04_40_80ms()).isEqualTo(1);
    }

    @Test
    public void testGet_Count05_80_160ms() throws Exception {
        assertThat(bean.get_Count05_80_160ms()).isEqualTo(0);
        MonitorFactory.getTimeMonitor(LABEL).add(100);
        assertThat(bean.get_Count05_80_160ms()).isEqualTo(1);
    }

    @Test
    public void testGet_Count06_160_320ms() throws Exception {
        assertThat(bean.get_Count06_160_320ms()).isEqualTo(0);
        MonitorFactory.getTimeMonitor(LABEL).add(200);
        assertThat(bean.get_Count06_160_320ms()).isEqualTo(1);
    }

    @Test
    public void testGet_Count07_320_640ms() throws Exception {
        assertThat(bean.get_Count07_320_640ms()).isEqualTo(0);
        MonitorFactory.getTimeMonitor(LABEL).add(400);
        assertThat(bean.get_Count07_320_640ms()).isEqualTo(1);
    }

    @Test
    public void testGet_Count08_640_1280ms() throws Exception {
        assertThat(bean.get_Count08_640_1280ms()).isEqualTo(0);
        MonitorFactory.getTimeMonitor(LABEL).add(800);
        assertThat(bean.get_Count08_640_1280ms()).isEqualTo(1);
    }

    @Test
    public void testGet_Count09_1280_2560ms() throws Exception {
        assertThat(bean.get_Count09_1280_2560ms()).isEqualTo(0);
        MonitorFactory.getTimeMonitor(LABEL).add(2000);
        assertThat(bean.get_Count09_1280_2560ms()).isEqualTo(1);
    }

    @Test
    public void testGet_Count10_2560_5120ms() throws Exception {
        assertThat(bean.get_Count10_2560_5120ms()).isEqualTo(0);
        MonitorFactory.getTimeMonitor(LABEL).add(3000);
        assertThat(bean.get_Count10_2560_5120ms()).isEqualTo(1);
    }

    @Test
    public void testGet_Count11_5120_10240ms() throws Exception {
        assertThat(bean.get_Count11_5120_10240ms()).isEqualTo(0);
        MonitorFactory.getTimeMonitor(LABEL).add(6000);
        assertThat(bean.get_Count11_5120_10240ms()).isEqualTo(1);
    }

    @Test
    public void testGet_Count12_10240_20480ms() throws Exception {
        assertThat(bean.get_Count12_10240_20480ms()).isEqualTo(0);
        MonitorFactory.getTimeMonitor(LABEL).add(12000);
        assertThat(bean.get_Count12_10240_20480ms()).isEqualTo(1);
    }

    @Test
    public void testGet_Count13_GreaterThan_20480ms() throws Exception {
        assertThat(bean.get_Count13_GreaterThan_20480ms()).isEqualTo(0);
        MonitorFactory.getTimeMonitor(LABEL).add(24000);
        assertThat(bean.get_Count13_GreaterThan_20480ms()).isEqualTo(1);
    }
}