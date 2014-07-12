package com.jamonapi.distributed;

import com.jamonapi.MonitorComposite;
import com.jamonapi.MonitorFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class LocalJamonDataTest {

    private LocalJamonData localJamonData;
    @Before
    public void setUp() {
        localJamonData = new LocalJamonData();
        MonitorFactory.start("mytestmonitor").stop();
    }

    @After
    public void cleanUp() {
        MonitorFactory.reset();
    }

    @Test
    public void testGetInstances() throws Exception {
        assertThat(localJamonData.getInstances().size()).isEqualTo(1);
        assertThat(localJamonData.getInstances().contains(LocalJamonData.INSTANCE));
    }

    @Test
    public void testPut() throws Exception {
        localJamonData.put();
        assertThat(localJamonData.getMonitors(LocalJamonData.INSTANCE).getNumRows()).isEqualTo(1);
        assertThat(localJamonData.getInstances().size()).isEqualTo(1);
    }

    @Test
    public void testGet() throws Exception {
        MonitorComposite monitorComposite = localJamonData.getMonitors(LocalJamonData.INSTANCE);
        assertThat(monitorComposite.getReport()).isEqualTo(MonitorFactory.getRootMonitor().getReport());
    }

    @Test
    public void testGet_WithInvalidArg() throws Exception {
        MonitorComposite monitorComposite = localJamonData.getMonitors("i_do_not_exist_instance");
        assertThat(monitorComposite).isNull();
    }
}