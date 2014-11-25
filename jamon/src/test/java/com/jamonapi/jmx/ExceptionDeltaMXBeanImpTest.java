package com.jamonapi.jmx;

import com.jamonapi.MonitorFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class ExceptionDeltaMXBeanImpTest {

    private ExceptionDeltaMXBeanImp bean = new ExceptionDeltaMXBeanImp();

    @Before
    public void setUp() throws Exception {
        MonitorFactory.reset();
    }

    @After
    public void tearDown() throws Exception {
        MonitorFactory.reset();
    }

    @Test
    public void testGetExceptionCount() throws Exception {
        MonitorFactory.add(ExceptionMXBean.LABEL, ExceptionMXBean.UNITS, 1);
        MonitorFactory.add(ExceptionMXBean.LABEL, ExceptionMXBean.UNITS, 1);
        assertThat(bean.getExceptionCount()).isEqualTo(2);
        // getExceptionCount returns a delta - the difference - between the count now
        // (3) vs the previous count when getExceptionCount() was called (2), so the
        // following should return 1.
        MonitorFactory.add(ExceptionMXBean.LABEL, ExceptionMXBean.UNITS, 1);
        assertThat(bean.getExceptionCount()).isEqualTo(1);
    }
}