package com.jamonapi.jmx;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MonitorMXBeanImpTest {
    private static final Date NULL_DATE = new Date(0);
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
    public void testGetMin_StartedNotStopped() throws Exception {
        MonitorMXBeanImp monBean = new MonitorMXBeanImp("any_label", "ms.");
        MonitorFactory.start("any_label");
        assertThat(monBean.getMin()).isEqualTo(Double.MAX_VALUE);
    }

    @Test
    public void testBasicBean() throws Exception {
        assertThat(bean.getLabel()).isEqualTo(LABEL);
        assertThat(bean.getUnits()).isEqualTo("units");
        assertThat(bean.getName()).isEqualTo(LABEL);
        assertThat(bean.getTotal()).isEqualTo(20);
        assertThat(bean.getAvg()).isEqualTo(10);
        assertThat(bean.getMin()).isEqualTo(5);
        assertThat(bean.getMax()).isEqualTo(15);
        assertThat(bean.getHits()).isEqualTo(2);
        assertThat(bean.getStdDev()).isBetween(7.0,8.0);
        assertThat(bean.getFirstAccess()).isBeforeOrEqualsTo(new Date());
        assertThat(bean.getLastAccess()).isBeforeOrEqualsTo(new Date());
        assertThat(bean.getLastValue()).isEqualTo(15);
        assertThat(bean.getActive()).isEqualTo(0);
        assertThat(bean.getMaxActive()).isEqualTo(2);
        assertThat(bean.getAvgActive()).isEqualTo(1.5);
    }

    @Test
    public void testCreate() {
        MonitorMXBean bean = MonitorMXBeanFactory.create(LABEL, "units", null);
        assertThat(bean.getLabel()).isEqualTo(LABEL);
        assertThat(bean.getUnits()).isEqualTo("units");
        assertThat(bean.getName()).isEqualTo(LABEL);

        bean = MonitorMXBeanFactory.create(LABEL, "units", "   ");
        assertThat(bean.getLabel()).isEqualTo(LABEL);
        assertThat(bean.getUnits()).isEqualTo("units");
        assertThat(bean.getName()).isEqualTo(LABEL);

        bean = MonitorMXBeanFactory.create(LABEL, "units", "");
        assertThat(bean.getLabel()).isEqualTo(LABEL);
        assertThat(bean.getUnits()).isEqualTo("units");
        assertThat(bean.getName()).isEqualTo(LABEL);

        bean = MonitorMXBeanFactory.create(LABEL, "units", "name");
        assertThat(bean.getLabel()).isEqualTo(LABEL);
        assertThat(bean.getUnits()).isEqualTo("units");
        assertThat(bean.getName()).isEqualTo("name");
    }

    @Test
    public void testBeanCreatedFromJamonJmxBeanProperty() {
        List<JamonJmxBeanProperty> jmxProperties = new ArrayList<JamonJmxBeanProperty>();
        jmxProperties.add(new JamonJmxBeanPropertyDefault("NO_EXIST","units", "myname1"));
        jmxProperties.add(new JamonJmxBeanPropertyDefault(LABEL,"units", "myname2"));
        MonitorMXBean bean = new MonitorMXBeanImp(jmxProperties);

        // should get data from LABEL monitor as NO_EXIST doesn't have a monitor
        assertThat(bean.getLabel()).isEqualTo(LABEL);
        assertThat(bean.getUnits()).isEqualTo("units");
        assertThat(bean.getName()).isEqualTo("myname1");
        assertThat(bean.getTotal()).isEqualTo(20);
        assertThat(bean.getAvg()).isEqualTo(10);
        assertThat(bean.getMin()).isEqualTo(5);
        assertThat(bean.getMax()).isEqualTo(15);
        assertThat(bean.getHits()).isEqualTo(2);
        assertThat(bean.getStdDev()).isBetween(7.0,8.0);
        assertThat(bean.getFirstAccess()).isBeforeOrEqualsTo(new Date());
        assertThat(bean.getLastAccess()).isBeforeOrEqualsTo(new Date());
        assertThat(bean.getLastValue()).isEqualTo(15);
        assertThat(bean.getActive()).isEqualTo(0);
        assertThat(bean.getMaxActive()).isEqualTo(2);
        assertThat(bean.getAvgActive()).isEqualTo(1.5);
    }

    @Test
    public void testMonitorNoExistFromProps() throws Exception {
        List<JamonJmxBeanProperty> jmxProperties = new ArrayList<JamonJmxBeanProperty>();
        jmxProperties.add(new JamonJmxBeanPropertyDefault("NO_EXIST","units", "name"));
        jmxProperties.add(new JamonJmxBeanPropertyDefault("NO_EXIST2","units", "myname2"));
        MonitorMXBean bean = new MonitorMXBeanImp(jmxProperties);

        assertThat(bean.getLabel()).isEqualTo("NO_EXIST");
        assertThat(bean.getUnits()).isEqualTo("units");
        assertThat(bean.getName()).isEqualTo("name");
        assertThat(bean.getTotal()).isEqualTo(0);
        assertThat(bean.getAvg()).isEqualTo(0);
        assertThat(bean.getMin()).isEqualTo(0);
        assertThat(bean.getMax()).isEqualTo(0);
        assertThat(bean.getHits()).isEqualTo(0);
        assertThat(bean.getStdDev()).isEqualTo(0);
        assertThat(bean.getFirstAccess()).isNull();
        assertThat(bean.getLastAccess()).isNull();
        assertThat(bean.getLastValue()).isEqualTo(0);
        assertThat(bean.getActive()).isEqualTo(0);
        assertThat(bean.getMaxActive()).isEqualTo(0);
        assertThat(bean.getAvgActive()).isEqualTo(0);
    }
}