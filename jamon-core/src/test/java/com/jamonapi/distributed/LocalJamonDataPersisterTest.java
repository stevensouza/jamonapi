package com.jamonapi.distributed;

import com.jamonapi.MonitorComposite;
import com.jamonapi.MonitorFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalJamonDataPersisterTest {

    private LocalJamonDataPersister localJamonData;
    @Before
    public void setUp() {
        localJamonData = new LocalJamonDataPersister();
        MonitorFactory.start("mytestmonitor").stop();
    }

    @After
    public void cleanUp() {
        MonitorFactory.reset();
    }

    @Test
    public void testGetInstances() throws Exception {
        assertThat(localJamonData.getInstances()).containsOnly(localJamonData.getInstance());
    }

    @Test
    public void testPut() throws Exception {
        localJamonData.put();
        assertThat(localJamonData.getInstances()).hasSize(1);
        assertThat(localJamonData.get(LocalJamonDataPersister.INSTANCE).getNumRows()).isEqualTo(2);
    }

    @Test
    public void testGet() throws Exception {
        MonitorComposite monitorComposite = localJamonData.get(LocalJamonDataPersister.INSTANCE);
        assertThat(monitorComposite.getReport()).isEqualTo(MonitorFactory.getRootMonitor().getReport());
    }

    @Test
    public void testGet_WithInvalidArg() throws Exception {
        MonitorComposite monitorComposite = localJamonData.get("i_do_not_exist_instance");
        assertThat(monitorComposite).isNull();
    }

    @Test
    public void testRemove() throws Exception {
        localJamonData.put();
        localJamonData.remove(LocalJamonDataPersister.INSTANCE);
        MonitorComposite monitorComposite = localJamonData.get(LocalJamonDataPersister.INSTANCE);
        assertThat(monitorComposite.getNumRows()).isEqualTo(1);
    }
}
