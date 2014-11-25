package com.jamonapi.jmx;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class MonitorMXBeanImpTest {
    private static final String LABEL = "hello_world";
    private MonitorMXBeanImp bean = new MonitorMXBeanImp(LABEL, "units");

    @Before
    public void setUp() throws Exception {
        MonitorFactory.reset();
        Monitor monitor = MonitorFactory.add(LABEL, "units", 5);
        monitor.start();
        MonitorFactory.add(LABEL, "units", 15).start().stop();
        monitor.stop();
    }

    @After
    public void tearDown() throws Exception {
        MonitorFactory.reset();
    }

    @Test
    public void testGetLabel() throws Exception {
        assertThat(bean.getLabel()).isEqualTo(LABEL);
    }

    @Test
    public void testGetUnits() throws Exception {
        assertThat(bean.getUnits()).isEqualTo("units");
    }

    @Test
    public void testGetName() throws Exception {
        assertThat(bean.getName()).isEqualTo(LABEL);
    }

    @Test
    public void testGetTotal() throws Exception {
        assertThat(bean.getTotal()).isEqualTo(20);
    }

    @Test
    public void testGetAvg() throws Exception {
        assertThat(bean.getAvg()).isEqualTo(10);
    }

    @Test
    public void testGetMin() throws Exception {
        assertThat(bean.getMin()).isEqualTo(5);
    }

    @Test
    public void testGetMin_StartedNotStopped() throws Exception {
        MonitorMXBeanImp monBean = new MonitorMXBeanImp("any_label", "ms.");
        MonitorFactory.start("any_label");
        assertThat(monBean.getMin()).isEqualTo(Double.MAX_VALUE);
    }

    @Test
    public void testGetMax() throws Exception {
        assertThat(bean.getMax()).isEqualTo(15);
    }

    @Test
    public void testGetHits() throws Exception {
        assertThat(bean.getHits()).isEqualTo(2);
    }

    @Test
    public void testGetStdDev() throws Exception {
        assertThat(bean.getStdDev()).isBetween(7.0,8.0);
    }

    @Test
    public void testGetFirstAccess() throws Exception {
        assertThat(bean.getFirstAccess()).isBeforeOrEqualsTo(new Date());
    }

    @Test
    public void testGetLastAccess() throws Exception {
        assertThat(bean.getLastAccess()).isBeforeOrEqualsTo(new Date());
    }

    @Test
    public void testGetLastValue() throws Exception {
        assertThat(bean.getLastValue()).isEqualTo(15);
    }

    @Test
    public void testGetActive() throws Exception {
        assertThat(bean.getActive()).isEqualTo(0);
    }

    @Test
    public void testGetMaxActive() throws Exception {
        assertThat(bean.getMaxActive()).isEqualTo(2);
    }

    @Test
    public void testGetAvgActive() throws Exception {
        assertThat(bean.getAvgActive()).isEqualTo(1.5);
    }
}