package com.jamonapi.jmx;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.management.ObjectName;
import java.util.List;

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

    @Test
    public void testIsEven() throws Exception {
        assertThat(MonitorMXBeanFactory.isEven(0)).isTrue();
        assertThat(MonitorMXBeanFactory.isEven(1)).isFalse();
        assertThat(MonitorMXBeanFactory.isEven(2)).isTrue();
        assertThat(MonitorMXBeanFactory.isEven(3)).isFalse();
        assertThat(MonitorMXBeanFactory.isEven(4)).isTrue();
    }

    @Test(expected = RuntimeException.class)
    public void testIllegalPropertiesNull() throws Exception {
        MonitorMXBeanFactory.getJmxBeanProperties(null);
    }

    @Test(expected = RuntimeException.class)
    public void testIllegalPropertiesEmpty() throws Exception {
        MonitorMXBeanFactory.getJmxBeanProperties(" ");
    }

    @Test(expected = RuntimeException.class)
    public void testIllegalPropertiesNoUnits() throws Exception {
        MonitorMXBeanFactory.getJmxBeanProperties("Must have units too (i.e. at least 2 args)");
    }

    @Test
    public void testPropertiesWith2Args() throws Exception {
       List<JamonJmxBeanProperty> properties = MonitorMXBeanFactory.getJmxBeanProperties(" mylabel , myunits ");
        assertThat(properties).hasSize(1);
        JamonJmxBeanProperty property = properties.get(0);
        assertThat(property.getLabel()).isEqualTo("mylabel");
        assertThat(property.getUnits()).isEqualTo("myunits");
        assertThat(property.getName()).isEqualTo("mylabel");
    }

    @Test
    public void testPropertiesWith3Args() throws Exception {
        List<JamonJmxBeanProperty> properties = MonitorMXBeanFactory.getJmxBeanProperties(" mylabel , myunits, myname ");
        assertThat(properties).hasSize(1);
        JamonJmxBeanProperty property = properties.get(0);
        assertThat(property.getLabel()).isEqualTo("mylabel");
        assertThat(property.getUnits()).isEqualTo("myunits");
        assertThat(property.getName()).isEqualTo("myname");
    }

    @Test
         public void testPropertiesWith4Args() throws Exception {
        // even args test doesn't have a name so uses first one as name.
        List<JamonJmxBeanProperty> properties = MonitorMXBeanFactory.getJmxBeanProperties(" mylabel , myunits , mylabel2, myunits2 ");
        assertThat(properties).hasSize(2);
        JamonJmxBeanProperty property = properties.get(0);
        assertThat(property.getLabel()).isEqualTo("mylabel");
        assertThat(property.getUnits()).isEqualTo("myunits");
        assertThat(property.getName()).isEqualTo("mylabel");
        property = properties.get(1);
        assertThat(property.getLabel()).isEqualTo("mylabel2");
        assertThat(property.getUnits()).isEqualTo("myunits2");
        assertThat(property.getName()).isEqualTo("mylabel");
    }

    @Test
    public void testPropertiesWith5Args() throws Exception {
        // even args test doesn't have a name so uses first one as name.
        List<JamonJmxBeanProperty> properties = MonitorMXBeanFactory.getJmxBeanProperties(" mylabel , myunits , mylabel2, myunits2 , myname2");
        assertThat(properties).hasSize(2);
        JamonJmxBeanProperty property = properties.get(0);
        assertThat(property.getLabel()).isEqualTo("mylabel");
        assertThat(property.getUnits()).isEqualTo("myunits");
        assertThat(property.getName()).isEqualTo("myname2");
        property = properties.get(1);
        assertThat(property.getLabel()).isEqualTo("mylabel2");
        assertThat(property.getUnits()).isEqualTo("myunits2");
        assertThat(property.getName()).isEqualTo("myname2");
    }
}