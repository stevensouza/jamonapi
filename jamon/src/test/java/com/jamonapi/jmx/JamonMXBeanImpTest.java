package com.jamonapi.jmx;

import com.jamonapi.MonitorFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JamonMXBeanImpTest {
    private JamonMXBean bean = new JamonMXBeanImp();

    @Before
    public void setUp() throws Exception {
        MonitorFactory.reset();
        MonitorFactory.setEnabled(true);
    }

    @After
    public void tearDown() throws Exception {
        MonitorFactory.reset();
        MonitorFactory.setEnabled(true);
    }

    @Test
    public void testGetVersion() throws Exception {
        assertThat(bean.getVersion()).isEqualTo(MonitorFactory.getVersion());
    }

    @Test
    public void testReset() throws Exception {
        MonitorFactory.add("mymonitor", "units", 100);
        assertThat(MonitorFactory.getNumRows()).isEqualTo(1);
        bean.reset();
        assertThat(MonitorFactory.getNumRows()).isEqualTo(0);
    }

    @Test
    public void testEnableDisable() throws Exception {
        MonitorFactory.add("mymonitor", "units", 100);
        assertThat(bean.getEnabled()).isEqualTo(true);
        assertThat(MonitorFactory.getNumRows()).isEqualTo(1);

        bean.setEnabled(false);
        MonitorFactory.add("mymonitor", "units", 100);
        assertThat(bean.getEnabled()).isEqualTo(false);
        assertThat(MonitorFactory.getNumRows()).isEqualTo(0);

        bean.setEnabled(true);
        MonitorFactory.add("mymonitor", "units", 100);
        assertThat(bean.getEnabled()).isEqualTo(true);
        assertThat(MonitorFactory.getNumRows()).isEqualTo(1);
        assertThat(MonitorFactory.getMonitor("mymonitor", "units").getHits()).isEqualTo(2);

    }
}