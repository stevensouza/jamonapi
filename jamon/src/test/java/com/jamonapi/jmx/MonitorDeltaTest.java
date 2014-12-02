package com.jamonapi.jmx;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class MonitorDeltaTest {

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
        MonitorDelta delta = new MonitorDelta();
        Monitor mon = MonitorFactory.add("label","units", 100).start().stop();
        delta = delta.minus(new MonitorDelta(mon));

        assertThat(delta.getLabel()).isEqualTo(mon.getLabel());
        assertThat(delta.getUnits()).isEqualTo(mon.getUnits());
        assertThat(delta.getTotal()).isEqualTo(mon.getTotal());
        assertThat(delta.getAvg()).isEqualTo(mon.getAvg());
        assertThat(delta.getMin()).isEqualTo(mon.getMin());
        assertThat(delta.getMax()).isEqualTo(mon.getMax());
        assertThat(delta.getHits()).isEqualTo(mon.getHits());
        assertThat(delta.getStdDev()).isEqualTo(mon.getStdDev());
        assertThat(delta.getFirstAccess()).isEqualTo(mon.getFirstAccess());
        assertThat(delta.getLastAccess()).isEqualTo(mon.getLastAccess());
        assertThat(delta.getLastValue()).isEqualTo(mon.getLastValue());
        assertThat(delta.getActive()).isEqualTo(mon.getActive());
        assertThat(delta.getMaxActive()).isEqualTo(mon.getMaxActive());
        assertThat(delta.getAvgActive()).isEqualTo(mon.getAvgActive());
    }

    @Test
    public void testSecondMonitorDeltaCorrect() throws Exception {
        Monitor mon = MonitorFactory.add("label","units", 100).start().stop();
        MonitorDelta prevMonValue = new MonitorDelta(mon);
        MonitorFactory.add("label","units", 150).start().start().start().stop();
        mon = MonitorFactory.add("label","units", 150).start().stop();
        MonitorDelta currentDelta = new MonitorDelta(mon);
        MonitorDelta displayDelta = prevMonValue.minus(currentDelta);

        // remember the following show deltas
        assertThat(displayDelta.getLabel()).isEqualTo(mon.getLabel());
        assertThat(displayDelta.getUnits()).isEqualTo(mon.getUnits());
        assertThat(displayDelta.getTotal()).isEqualTo(300);
        assertThat(displayDelta.getAvg()).isEqualTo(50);
        assertThat(displayDelta.getMin()).isEqualTo(0);
        assertThat(displayDelta.getMax()).isEqualTo(50);
        assertThat(displayDelta.getHits()).isEqualTo(2);
        assertThat(displayDelta.getStdDev()).isEqualTo(mon.getStdDev());
        assertThat(displayDelta.getFirstAccess()).isEqualTo(mon.getFirstAccess());
        assertThat(displayDelta.getLastAccess()).isEqualTo(mon.getLastAccess());
        assertThat(displayDelta.getLastValue()).isEqualTo(150);
        assertThat(displayDelta.getActive()).isEqualTo(2);
        assertThat(displayDelta.getMaxActive()).isEqualTo(2);
        assertThat(displayDelta.getAvgActive()).isBetween(1d,2d);
    }

    @Test
    public void testSecondMonitorDeltaNegativeCorrect() throws Exception {
        Monitor mon = MonitorFactory.add("label","units", 100).start().stop();
        MonitorDelta prevMonValue = new MonitorDelta(mon);
        MonitorFactory.add("label","units", -150).start().stop();
        mon = MonitorFactory.add("label","units", -150).start().stop();
        MonitorDelta currentDelta = new MonitorDelta(mon);
        MonitorDelta displayDelta = prevMonValue.minus(currentDelta);

        // remember the following show deltas
        assertThat(displayDelta.getLabel()).isEqualTo(mon.getLabel());
        assertThat(displayDelta.getUnits()).isEqualTo(mon.getUnits());
        assertThat(displayDelta.getTotal()).isEqualTo(-300);
        assertThat(displayDelta.getAvg()).isEqualTo(-250);
        assertThat(displayDelta.getMin()).isEqualTo(-250);
        assertThat(displayDelta.getMax()).isEqualTo(0);
        assertThat(displayDelta.getHits()).isEqualTo(2);
        assertThat(displayDelta.getStdDev()).isEqualTo(mon.getStdDev());
        assertThat(displayDelta.getFirstAccess()).isEqualTo(mon.getFirstAccess());
        assertThat(displayDelta.getLastAccess()).isEqualTo(mon.getLastAccess());
        assertThat(displayDelta.getLastValue()).isEqualTo(-150);
        assertThat(displayDelta.getActive()).isEqualTo(0);
        assertThat(displayDelta.getMaxActive()).isEqualTo(0);
        assertThat(displayDelta.getAvgActive()).isEqualTo(0);
    }
}