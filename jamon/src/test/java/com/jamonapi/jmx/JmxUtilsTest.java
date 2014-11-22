package com.jamonapi.jmx;

import com.jamonapi.MonitorFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.management.JMX;
import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.lang.reflect.UndeclaredThrowableException;

import static org.assertj.core.api.Assertions.assertThat;

public class JmxUtilsTest {
    private MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void shouldEqual0IfNotExist() throws Exception  {
        assertThat(JmxUtils.getCount("I_DO_NOT_EXIT","ms.")).isEqualTo(0);
    }

    @Test
    public void shouldExist() throws Exception {
        MonitorFactory.start("I_EXIST").stop();
        MonitorFactory.start("I_EXIST").stop();
        assertThat(JmxUtils.getCount("I_EXIST", "ms.")).isEqualTo(2);
    }

    @Test
    public void testRegisterMbeans() throws Exception {
        JmxUtils.registerMbeans();
        Log4jMXBean log4jProxy = JMX.newMXBeanProxy(mBeanServer, Log4jMXBeanImp.getObjectName(), Log4jMXBean.class);
        assertThat(log4jProxy.getDebug()).isEqualTo(0);
        JmxUtils.unregisterMbeans();
    }

    @Test(expected = UndeclaredThrowableException.class)
    public void testUnregisterMbeans() throws Exception {
        JmxUtils.registerMbeans();
        JmxUtils.unregisterMbeans();
        Log4jMXBean log4jProxy = JMX.newMXBeanProxy(mBeanServer, Log4jMXBeanImp.getObjectName(), Log4jMXBean.class);
        log4jProxy.getDebug();
    }

}