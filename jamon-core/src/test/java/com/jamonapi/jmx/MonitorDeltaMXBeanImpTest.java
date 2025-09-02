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

public class MonitorDeltaMXBeanImpTest {

    private static final Date NULL_DATE = new Date(0);

    @Before
    public void setUp() throws Exception {
        MonitorFactory.reset();
    }

    @After
    public void tearDown() throws Exception {
        MonitorFactory.reset();
    }

    @Test
    public void testMonitorNoExist() throws Exception {
        MonitorMXBean bean = MonitorMXBeanFactory.createDelta("NO_EXIST", "units", "name");

        assertThat(bean.getLabel()).isEqualTo("NO_EXIST");
        assertThat(bean.getUnits()).isEqualTo("units");
        assertThat(bean.getName()).isEqualTo("name");
        assertThat(bean.getTotal()).isEqualTo(0);
        assertThat(bean.getAvg()).isEqualTo(0);
        assertThat(bean.getMin()).isEqualTo(0);
        assertThat(bean.getMax()).isEqualTo(0);
        assertThat(bean.getHits()).isEqualTo(0);
        assertThat(bean.getStdDev()).isEqualTo(0);
        assertThat(bean.getFirstAccess()).isEqualTo(NULL_DATE);
        assertThat(bean.getLastAccess()).isEqualTo(NULL_DATE);
        assertThat(bean.getLastValue()).isEqualTo(0);
        assertThat(bean.getActive()).isEqualTo(0);
        assertThat(bean.getMaxActive()).isEqualTo(0);
        assertThat(bean.getAvgActive()).isEqualTo(0);
    }

    @Test
    public void testMonitorNoExistFromProps() throws Exception {
        List<JamonJmxBeanProperty> jmxProperties = new ArrayList<JamonJmxBeanProperty>();
        jmxProperties.add(new JamonJmxBeanPropertyDefault("NO_EXIST","units", "name"));
        jmxProperties.add(new JamonJmxBeanPropertyDefault("NO_EXIST2","units", "myname2"));
        MonitorMXBean bean = new MonitorDeltaMXBeanImp(jmxProperties);

        assertThat(bean.getLabel()).isEqualTo("NO_EXIST");
        assertThat(bean.getUnits()).isEqualTo("units");
        assertThat(bean.getName()).isEqualTo("name");
        assertThat(bean.getTotal()).isEqualTo(0);
        assertThat(bean.getAvg()).isEqualTo(0);
        assertThat(bean.getMin()).isEqualTo(0);
        assertThat(bean.getMax()).isEqualTo(0);
        assertThat(bean.getHits()).isEqualTo(0);
        assertThat(bean.getStdDev()).isEqualTo(0);
        assertThat(bean.getFirstAccess()).isEqualTo(NULL_DATE);
        assertThat(bean.getLastAccess()).isEqualTo(NULL_DATE);
        assertThat(bean.getLastValue()).isEqualTo(0);
        assertThat(bean.getActive()).isEqualTo(0);
        assertThat(bean.getMaxActive()).isEqualTo(0);
        assertThat(bean.getAvgActive()).isEqualTo(0);
    }

    @Test
    public void testFirstMonitorDeltaCorrect() throws Exception {
        MonitorMXBean bean = MonitorMXBeanFactory.createDelta("label", "units", "name");
        Monitor mon = MonitorFactory.add("label","units", 100).start().stop();

        assertThat(bean.getLabel()).isEqualTo(mon.getLabel());
        assertThat(bean.getUnits()).isEqualTo(mon.getUnits());
        assertThat(bean.getName()).isEqualTo("name");
        assertThat(bean.getTotal()).isEqualTo(mon.getTotal());
        assertThat(bean.getAvg()).isEqualTo(mon.getAvg());
        assertThat(bean.getMin()).isEqualTo(mon.getMin());
        assertThat(bean.getMax()).isEqualTo(mon.getMax());
        assertThat(bean.getHits()).isEqualTo(mon.getHits());
        assertThat(bean.getStdDev()).isEqualTo(mon.getStdDev());
        assertThat(bean.getFirstAccess()).isEqualTo(mon.getFirstAccess());
        assertThat(bean.getLastAccess()).isEqualTo(mon.getLastAccess());
        assertThat(bean.getLastValue()).isEqualTo(mon.getLastValue());
        assertThat(bean.getActive()).isEqualTo(mon.getActive());
        assertThat(bean.getMaxActive()).isEqualTo(mon.getMaxActive());
        assertThat(bean.getAvgActive()).isEqualTo(mon.getAvgActive());
    }

    @Test
    public void testSecondMonitorDeltaCorrect() throws Exception {
        MonitorMXBean bean = MonitorMXBeanFactory.createDelta("label", "units", "name");
        MonitorFactory.add("label","units", 100).start().stop();

        // call to reset to current state as the baseline
        resetToCurrent(bean);

        MonitorFactory.add("label","units", 150).start().start().start().stop();
        Monitor mon = MonitorFactory.add("label","units", 150).start().stop();

        // remember the following show deltas
        assertThat(bean.getLabel()).isEqualTo(mon.getLabel());
        assertThat(bean.getUnits()).isEqualTo(mon.getUnits());
        assertThat(bean.getName()).isEqualTo("name");
        assertThat(bean.getTotal()).isEqualTo(300);
        assertThat(bean.getAvg()).isEqualTo(150);
        assertThat(bean.getMin()).isEqualTo(0);
        assertThat(bean.getMax()).isEqualTo(50);
        assertThat(bean.getHits()).isEqualTo(2);
        assertThat(bean.getStdDev()).isEqualTo(mon.getStdDev());
        assertThat(bean.getFirstAccess()).isEqualTo(mon.getFirstAccess());
        assertThat(bean.getLastAccess()).isEqualTo(mon.getLastAccess());
        assertThat(bean.getLastValue()).isEqualTo(50);
        assertThat(bean.getActive()).isEqualTo(2);
        assertThat(bean.getMaxActive()).isEqualTo(2);
        assertThat(bean.getAvgActive()).isBetween(1d,2d);
    }

    @Test
    public void testSecondMonitorDeltaCorrectFromProperties() throws Exception {
        List<JamonJmxBeanProperty> jmxProperties = new ArrayList<JamonJmxBeanProperty>();
        jmxProperties.add(new JamonJmxBeanPropertyDefault("NO_EXIST","units", "name"));
        jmxProperties.add(new JamonJmxBeanPropertyDefault("label","units", "myname2"));
        MonitorMXBean bean = new MonitorDeltaMXBeanImp(jmxProperties);

        MonitorFactory.add("label","units", 100).start().stop();

        // call to reset to current state as the baseline
        resetToCurrent(bean);

        MonitorFactory.add("label","units", 150).start().start().start().stop();
        Monitor mon = MonitorFactory.add("label","units", 150).start().stop();

        // remember the following show deltas
        assertThat(bean.getLabel()).isEqualTo(mon.getLabel());
        assertThat(bean.getUnits()).isEqualTo(mon.getUnits());
        assertThat(bean.getName()).isEqualTo("name");
        assertThat(bean.getTotal()).isEqualTo(300);
        assertThat(bean.getAvg()).isEqualTo(150);
        assertThat(bean.getMin()).isEqualTo(0);
        assertThat(bean.getMax()).isEqualTo(50);
        assertThat(bean.getHits()).isEqualTo(2);
        assertThat(bean.getStdDev()).isEqualTo(mon.getStdDev());
        assertThat(bean.getFirstAccess()).isEqualTo(mon.getFirstAccess());
        assertThat(bean.getLastAccess()).isEqualTo(mon.getLastAccess());
        assertThat(bean.getLastValue()).isEqualTo(50);
        assertThat(bean.getActive()).isEqualTo(2);
        assertThat(bean.getMaxActive()).isEqualTo(2);
        assertThat(bean.getAvgActive()).isBetween(1d,2d);
    }


    @Test
    public void testSecondMonitorDeltaNegativeCorrect() throws Exception {
        MonitorMXBean bean = MonitorMXBeanFactory.createDelta("label", "units", "label");
        Monitor mon = MonitorFactory.add("label","units", 100).start().stop();
        resetToCurrent(bean);
        MonitorFactory.add("label","units", -150).start().stop();
        mon = MonitorFactory.add("label","units", -150).start().stop();

        // remember the following show deltas
        assertThat(bean.getLabel()).isEqualTo(mon.getLabel());
        assertThat(bean.getUnits()).isEqualTo(mon.getUnits());
        assertThat(bean.getTotal()).isEqualTo(-300);
        assertThat(bean.getAvg()).isEqualTo(-150);
        assertThat(bean.getMin()).isEqualTo(-250);
        assertThat(bean.getMax()).isEqualTo(0);
        assertThat(bean.getHits()).isEqualTo(2);
        assertThat(bean.getStdDev()).isEqualTo(mon.getStdDev());
        assertThat(bean.getFirstAccess()).isEqualTo(mon.getFirstAccess());
        assertThat(bean.getLastAccess()).isEqualTo(mon.getLastAccess());
        assertThat(bean.getLastValue()).isEqualTo(-250);
        assertThat(bean.getActive()).isEqualTo(0);
        assertThat(bean.getMaxActive()).isEqualTo(0);
        assertThat(bean.getAvgActive()).isEqualTo(0);
    }

    // resets the deltas to be based on the current monitor
    private void resetToCurrent(MonitorMXBean bean) {
        bean.getTotal();
        bean.getAvg();
        bean.getMin();
        bean.getMax();
        bean.getHits();
        bean.getStdDev();
        bean.getFirstAccess();
        bean.getLastAccess();
        bean.getLastValue();
        bean.getActive();
        bean.getMaxActive();
        bean.getAvgActive();
    }
}