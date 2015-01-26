package com.jamonapi.jmx;

import com.jamonapi.JamonPropertiesLoader;
import com.jamonapi.MonitorFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class JmxUtilsTest {
    private MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
    private List<JamonPropertiesLoader.JamonJmxBeanProperty> beanPropertyList;

    @Before
    public void setUp() throws Exception {
        MonitorFactory.reset();
        JamonPropertiesLoader.JamonJmxBeanProperty property = mock(JamonPropertiesLoader.JamonJmxBeanProperty.class);
        when(property.getLabel()).thenReturn("I_EXIST");
        when(property.getUnits()).thenReturn("ms.");
        beanPropertyList = new ArrayList<JamonPropertiesLoader.JamonJmxBeanProperty>();
        beanPropertyList.add(property);
    }

    @After
    public void tearDown() throws Exception {
        MonitorFactory.reset();
    }

    @Test
    public void shouldEqual0IfNotExist() throws Exception  {
        assertThat(JmxUtils.getCount("I_DO_NOT_EXIST","ms.")).isEqualTo(0);
    }

    @Test
    public void shouldExist() throws Exception {
        MonitorFactory.start("I_EXIST").stop();
        MonitorFactory.start("I_EXIST").stop();
        assertThat(JmxUtils.getCount("I_EXIST", "ms.")).isEqualTo(2);
    }

    @Test
    public void getDate() throws Exception  {
        MonitorFactory.start("I_EXIST").stop();
        assertThat(JmxUtils.getDate("I_EXIST","ms.","lastaccess")).isBeforeOrEqualsTo(new Date());
        assertThat(JmxUtils.getDate(beanPropertyList,"lastaccess")).isBeforeOrEqualsTo(new Date());

    }

    @Test
    public void getDouble() throws Exception  {
        MonitorFactory.start("I_EXIST").stop();
        assertThat(JmxUtils.getDouble("I_EXIST","ms.","hits")).isEqualTo(1);
        assertThat(JmxUtils.getDouble(beanPropertyList,"hits")).isEqualTo(1);
    }

    @Test
    public void getCount() throws Exception  {
        MonitorFactory.start("I_EXIST").stop();
        assertThat(JmxUtils.getCount("I_EXIST", "ms.")).isEqualTo(1);
        assertThat(JmxUtils.getCount(beanPropertyList)).isEqualTo(1);
    }


    @Test
    public void testRegisterMbeans() throws Exception {
        JmxUtils.registerMbeans();

        Log4jMXBean log4jProxy = JMX.newMXBeanProxy(mBeanServer, Log4jMXBeanImp.getObjectName(), Log4jMXBean.class);
        assertThat(log4jProxy.getDebug()).isEqualTo(0);

        Log4jMXBean log4jDeltaProxy = JMX.newMXBeanProxy(mBeanServer, Log4jDeltaMXBeanImp.getObjectName(), Log4jMXBean.class);
        assertThat(log4jDeltaProxy.getDebug()).isEqualTo(0);

        ExceptionMXBean exceptionMXBeanImp = JMX.newMXBeanProxy(mBeanServer, ExceptionMXBeanImp.getObjectName(), ExceptionMXBean.class);
        assertThat(exceptionMXBeanImp.getExceptionCount()).isEqualTo(0);

        ExceptionMXBean exceptionMXBeanDeltaImp = JMX.newMXBeanProxy(mBeanServer, ExceptionDeltaMXBeanImp.getObjectName(), ExceptionMXBean.class);
        assertThat(exceptionMXBeanDeltaImp.getExceptionCount()).isEqualTo(0);

        JamonMXBean versionMxBeanImp = JMX.newMXBeanProxy(mBeanServer, JamonMXBeanImp.getObjectName(), JamonMXBean.class);
        assertThat(versionMxBeanImp.getVersion()).isEqualTo(MonitorFactory.getVersion());

        JmxUtils.unregisterMbeans();
    }

    @Test
    public void testQueryMbeans() throws Exception {
        JmxUtils.registerMbeans();

        Set<ObjectName> set = JmxUtils.queryMBeans(mBeanServer, "name");
        assertThat(set).isNotEmpty();
        for (ObjectName obj : set) {
            assertThat(obj.toString()).contains("name");
        }

        JmxUtils.unregisterMbeans();
    }


    @Test
    public void testQueryGcDependentMbeansCreatedAndDestroyed() throws Exception {
        JmxUtils.registerMbeans();
        // gc call
        System.gc();
        Thread.sleep(100);

        Set set = JmxUtils.queryMBeans(mBeanServer, "Jamon.Gc.");
        assertThat(set).isNotEmpty();

        JmxUtils.unregisterMbeans();
        set = JmxUtils.queryMBeans(mBeanServer, "Jamon.Gc.");
        assertThat(set).isEmpty();
    }

    @Test(expected = RuntimeException.class)
    public void testUnregisterMbeans() throws Exception {
        JmxUtils.registerMbeans();
        JmxUtils.unregisterMbeans();
        Log4jMXBean log4jProxy = JMX.newMXBeanProxy(mBeanServer, Log4jMXBeanImp.getObjectName(), Log4jMXBean.class);
        log4jProxy.getDebug();
    }

    @Test
    public void shouldFireGcNotification() throws Exception {
        JmxUtils.registerMbeans();
        System.gc();
        // sleep to ensure notification is called.
        Thread.sleep(3000);
        GcMXBean gcProxy = JMX.newMXBeanProxy(mBeanServer, GcMXBeanImp.getObjectName(), GcMXBean.class);
        assertThat(gcProxy.getGcInfo()).contains("Name","Cause","Action","Duration","Sequence","When","BeforeGc","AfterGc");
        assertThat(gcProxy.getDuration()).isGreaterThan(0);
        JmxUtils.unregisterMbeans();
    }

}