package com.jamonapi.jmx;

import com.jamonapi.JAMonListenerFactory;
import com.jamonapi.MonKeyImp;
import com.jamonapi.MonitorFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ExceptionMXBeanImpTest {
    ExceptionMXBeanImp bean = new ExceptionMXBeanImp();


    @Before
    public void setUp() throws Exception {
        MonitorFactory.reset();
    }

    @After
    public void tearDown() throws Exception {
        MonitorFactory.reset();
    }

    @Test
    public void testNoExceptionsException() throws Exception {
        assertThat(bean.getMostRecentException()).isEqualTo("No exceptions have been thrown");
        assertThat(bean.getExceptionCount()).isEqualTo(0);
    }

    @Test
    public void testNoListener() throws Exception {
        MonitorFactory.add(ExceptionMXBean.LABEL, ExceptionMXBean.UNITS, 1);
        assertThat(bean.getMostRecentException()).isEqualTo("Exception Stacktrace tracking is not enabled.");
    }

    @Test
    public void testStactraceWithListener() throws Exception {
        Throwable exception = new Exception("hello world");
        MonitorFactory.getMonitor(ExceptionMXBean.LABEL, ExceptionMXBean.UNITS).addListener("value", JAMonListenerFactory.get("FIFOBuffer"));
        MonitorFactory.add(new MonKeyImp(ExceptionMXBean.LABEL, exception, ExceptionMXBean.UNITS), 1);
        assertThat(bean.getMostRecentException()).startsWith("java.lang.Exception: hello world");
    }

    @Test
    public void testGetExceptionCount() throws Exception {
        MonitorFactory.add(ExceptionMXBean.LABEL, ExceptionMXBean.UNITS, 1);
        MonitorFactory.add(ExceptionMXBean.LABEL, ExceptionMXBean.UNITS, 1);
        assertThat(bean.getExceptionCount()).isEqualTo(2);
    }

    @Test
    public void testGetExceptionDate() throws Exception {
        MonitorFactory.add(ExceptionMXBean.LABEL, ExceptionMXBean.UNITS, 1);
        assertThat(bean.getWhen()).isEqualTo(MonitorFactory.getMonitor(ExceptionMXBean.LABEL, ExceptionMXBean.UNITS).getLastAccess());
    }
}