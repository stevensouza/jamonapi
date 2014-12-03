package com.jamonapi.jmx;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.management.ObjectName;

import static org.assertj.core.api.Assertions.assertThat;

public class MonitorMXBeanFactoryTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCreateTimeMonitor() throws Exception {
        MonitorMXBean bean = MonitorMXBeanFactory.create("mylabel", "ms.", "myname");
        ObjectName name = MonitorMXBeanFactory.getObjectName(bean);
        assertThat(bean).isExactlyInstanceOf(MonitorMsMXBeanImp.class);
        assertThat(bean.getLabel()).isEqualTo("mylabel");
        assertThat(bean.getUnits()).isEqualTo("ms.");
        assertThat(bean.getName()).isEqualTo("myname");
    }

    @Test
    public void testCreateStandardMonitor() throws Exception {
        MonitorMXBean bean = MonitorMXBeanFactory.create("mylabel", "myunits", "myname");
        assertThat(bean).isExactlyInstanceOf(MonitorMXBeanImp.class);
        assertThat(bean.getLabel()).isEqualTo("mylabel");
        assertThat(bean.getUnits()).isEqualTo("myunits");
        assertThat(bean.getName()).isEqualTo("myname");
    }

    @Test
    public void testCreateWithNullName() throws Exception {
        MonitorMXBean bean = MonitorMXBeanFactory.create("mylabel", "myunits", null);
        assertThat(bean).isExactlyInstanceOf(MonitorMXBeanImp.class);
        assertThat(bean.getLabel()).isEqualTo("mylabel");
        assertThat(bean.getUnits()).isEqualTo("myunits");
        assertThat(bean.getName()).isEqualTo("mylabel");
    }

    @Test
    public void testCreateWithEmptyName() throws Exception {
        MonitorMXBean bean = MonitorMXBeanFactory.create("mylabel", "myunits", "");
        assertThat(bean).isExactlyInstanceOf(MonitorMXBeanImp.class);
        assertThat(bean.getLabel()).isEqualTo("mylabel");
        assertThat(bean.getUnits()).isEqualTo("myunits");
        assertThat(bean.getName()).isEqualTo("mylabel");
    }

    @Test
    public void testCreateStandardDeltaMonitor() throws Exception {
        MonitorMXBean bean = MonitorMXBeanFactory.createDelta("mylabel", "myunits", "myname");
        assertThat(bean).isExactlyInstanceOf(MonitorDeltaMXBeanImp.class);
        assertThat(bean.getLabel()).isEqualTo("mylabel");
        assertThat(bean.getUnits()).isEqualTo("myunits");
        assertThat(bean.getName()).isEqualTo("myname");
    }
}