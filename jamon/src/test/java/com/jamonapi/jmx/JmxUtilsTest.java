package com.jamonapi.jmx;

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
    private List<JamonJmxBeanProperty> beanPropertyList;

    @Before
    public void setUp() throws Exception {
        MonitorFactory.reset();
        JamonJmxBeanProperty property = mock(JamonJmxBeanProperty.class);
        when(property.getLabel()).thenReturn("I_EXIST");
        when(property.getUnits()).thenReturn("ms.");
        beanPropertyList = new ArrayList<JamonJmxBeanProperty>();
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
    public void getDateNoExist() throws Exception  {
        assertThat(JmxUtils.getDate("NO_EXIST","ms.","lastaccess")).isNull();
        assertThat(JmxUtils.getDate(beanPropertyList,"lastaccess")).isNull();
    }

    @Test
    public void getDouble() throws Exception  {
        MonitorFactory.start("I_EXIST").stop();
        assertThat(JmxUtils.getDouble("I_EXIST", "ms.", "hits")).isEqualTo(1);
        assertThat(JmxUtils.getDouble(beanPropertyList,"hits")).isEqualTo(1);
    }

    @Test
    public void getDoubleNoExist() throws Exception  {
        assertThat(JmxUtils.getDouble("NO_EXIST", "ms.", "hits")).isEqualTo(0.0);
        assertThat(JmxUtils.getDouble(beanPropertyList,"hits")).isEqualTo(0.0);
    }

    @Test
    public void getCount() throws Exception  {
        MonitorFactory.start("I_EXIST").stop();
        assertThat(JmxUtils.getCount("I_EXIST", "ms.")).isEqualTo(1);
        assertThat(JmxUtils.getCount(beanPropertyList)).isEqualTo(1);
    }

    @Test
    public void getMonitorWhenOnlyFirstExists() {
        JamonJmxBeanProperty property = mock(JamonJmxBeanProperty.class);
        when(property.getLabel()).thenReturn("I_EXIST");
        when(property.getUnits()).thenReturn("ms.");
        List<JamonJmxBeanProperty> list = new ArrayList<JamonJmxBeanProperty>();
        list.add(property);
        property = mock(JamonJmxBeanProperty.class);
        when(property.getLabel()).thenReturn("NO_EXIST");
        when(property.getUnits()).thenReturn("ms.");
        list.add(property);

        MonitorFactory.start("I_EXIST").stop();
        MonitorFactory.start("I_EXIST").stop();
        MonitorFactory.start("I_EXIST").stop();

        assertThat(JmxUtils.getValue(list, "label", "MY_DEFAULT")).isEqualTo("I_EXIST");
        assertThat(JmxUtils.getValue(list, "hits", "MY_DEFAULT")).isEqualTo(3.0);
    }

    @Test
    public void getMonitorWhenOnlySecondExists() {
        JamonJmxBeanProperty property = mock(JamonJmxBeanProperty.class);
        when(property.getLabel()).thenReturn("NO_EXIST");
        when(property.getUnits()).thenReturn("ms.");
        List<JamonJmxBeanProperty> list = new ArrayList<JamonJmxBeanProperty>();
        list.add(property);
        property = mock(JamonJmxBeanProperty.class);
        when(property.getLabel()).thenReturn("I_EXIST");
        when(property.getUnits()).thenReturn("ms.");
        list.add(property);

        MonitorFactory.start("I_EXIST").stop();
        MonitorFactory.start("I_EXIST").stop();
        MonitorFactory.start("I_EXIST").stop();

        assertThat(JmxUtils.getValue(list, "label", "MY_DEFAULT")).isEqualTo("I_EXIST");
        assertThat(JmxUtils.getValue(list, "hits", "MY_DEFAULT")).isEqualTo(3.0);
    }

    @Test
    public void getMonitorWhenBothExists() {
        JamonJmxBeanProperty property = mock(JamonJmxBeanProperty.class);
        when(property.getLabel()).thenReturn("I_EXIST1");
        when(property.getUnits()).thenReturn("ms.");
        List<JamonJmxBeanProperty> list = new ArrayList<JamonJmxBeanProperty>();
        list.add(property);
        property = mock(JamonJmxBeanProperty.class);
        when(property.getLabel()).thenReturn("I_EXIST2");
        when(property.getUnits()).thenReturn("ms.");
        list.add(property);

        MonitorFactory.start("I_EXIST1").stop();
        MonitorFactory.start("I_EXIST1").stop();
        MonitorFactory.start("I_EXIST1").stop();
        MonitorFactory.start("I_EXIST2").stop();

        assertThat(JmxUtils.getValue(list, "label", "MY_DEFAULT")).isEqualTo("I_EXIST1");
        assertThat(JmxUtils.getValue(list, "hits", "MY_DEFAULT")).isEqualTo(3.0);
    }

    @Test
    public void getCountNoExist() throws Exception  {
        assertThat(JmxUtils.getCount("NO_EXIST", "ms.")).isEqualTo(0);
        assertThat(JmxUtils.getCount(beanPropertyList)).isEqualTo(0);
    }
    @Test
    public void getMonitor() throws Exception  {
        MonitorFactory.start("I_EXIST").stop();
        assertThat(JmxUtils.getMonitor(beanPropertyList).getLabel()).isEqualTo("I_EXIST");
        assertThat(JmxUtils.getMonitor(beanPropertyList).getUnits()).isEqualTo("ms.");
    }

    @Test
    public void getValueNoExist() {
        assertThat(JmxUtils.getValue(beanPropertyList, "label", "MY_DEFAULT")).isEqualTo("MY_DEFAULT");
    }

    @Test
    public void getValueExist() {
        MonitorFactory.start("I_EXIST").stop();
        assertThat(JmxUtils.getValue(beanPropertyList, "label", "MY_DEFAULT")).isEqualTo("I_EXIST");
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