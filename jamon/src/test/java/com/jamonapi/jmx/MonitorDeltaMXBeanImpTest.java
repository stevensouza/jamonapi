package com.jamonapi.jmx;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class MonitorDeltaMXBeanImpTest {

    @Before
    public void setUp() throws Exception {
        MonitorFactory.reset();
    }

    @After
    public void tearDown() throws Exception {
        MonitorFactory.reset();
    }

    @Test
    public void testFirstMonitorDeltaCorrect() throws Exception {
        MonitorDeltaMXBeanImp bean = MonitorDeltaMXBeanImp.create("label", "units", "label");
        Monitor mon = MonitorFactory.add("label","units", 100).start().stop();

        assertThat(bean.getLabel()).isEqualTo(mon.getLabel());
        assertThat(bean.getUnits()).isEqualTo(mon.getUnits());
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
        MonitorDeltaMXBeanImp bean = MonitorDeltaMXBeanImp.create("label", "units", "label");
        Monitor mon = MonitorFactory.add("label","units", 100).start().stop();

        // call to reset to current state as the baseline
        resetToCurrent(bean);

        MonitorFactory.add("label","units", 150).start().start().start().stop();
        mon = MonitorFactory.add("label","units", 150).start().stop();

        // remember the following show deltas
        assertThat(bean.getLabel()).isEqualTo(mon.getLabel());
        assertThat(bean.getUnits()).isEqualTo(mon.getUnits());
        assertThat(bean.getTotal()).isEqualTo(300);
        assertThat(bean.getAvg()).isEqualTo(150);
        assertThat(bean.getMin()).isEqualTo(0);
        assertThat(bean.getMax()).isEqualTo(50);
        assertThat(bean.getHits()).isEqualTo(2);
        assertThat(bean.getStdDev()).isEqualTo(mon.getStdDev());
        assertThat(bean.getFirstAccess()).isEqualTo(mon.getFirstAccess());
        assertThat(bean.getLastAccess()).isEqualTo(mon.getLastAccess());
        assertThat(bean.getLastValue()).isEqualTo(150);
        assertThat(bean.getActive()).isEqualTo(2);
        assertThat(bean.getMaxActive()).isEqualTo(2);
        assertThat(bean.getAvgActive()).isBetween(1d,2d);
    }



    @Test
    public void testSecondMonitorDeltaNegativeCorrect() throws Exception {
        MonitorDeltaMXBeanImp bean = MonitorDeltaMXBeanImp.create("label", "units", "label");
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
        assertThat(bean.getLastValue()).isEqualTo(-150);
        assertThat(bean.getActive()).isEqualTo(0);
        assertThat(bean.getMaxActive()).isEqualTo(0);
        assertThat(bean.getAvgActive()).isEqualTo(0);
    }

    // resets the deltas to be based on the current monitor
    private void resetToCurrent(MonitorDeltaMXBeanImp bean) {
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