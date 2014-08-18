package com.jamonapi.distributed;

import com.jamonapi.MonitorComposite;
import com.jamonapi.MonitorFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class MonitorCompositeCombinerTest {

    @Before
    public void setUp() throws Exception {
        MonitorFactory.reset();
    }

    @After
    public void tearDown() throws Exception {
        MonitorFactory.reset();
    }

    @Test
    public void testGet() throws Exception {
        JamonDataPersister persister = mock(JamonDataPersister.class);
        MonitorCompositeCombiner mcc = new MonitorCompositeCombiner(persister);

        MonitorFactory.start("hello").stop();
        MonitorFactory.start("world").stop();
        MonitorComposite mc1 = MonitorFactory.getRootMonitor().copy();

        MonitorFactory.start("hi").stop();
        MonitorComposite mc2 = MonitorFactory.getRootMonitor().copy();

        MonitorFactory.start("again").stop();
        MonitorComposite mc3 = MonitorFactory.getRootMonitor().copy();

        when(persister.get("local1")).thenReturn(mc1);
        when(persister.get("local2")).thenReturn(mc2);
        when(persister.get("local3")).thenReturn(mc3);

        MonitorComposite monitorComposite = mcc.get("local1", "local2", "local3");
        assertThat(monitorComposite.getNumRows()).isEqualTo(9);
    }

    @Test
    public void testCombine() throws Exception {
        MonitorFactory.start("hello").stop();
        MonitorFactory.start("world").stop();
        MonitorComposite mc1 = MonitorFactory.getRootMonitor().copy();

        MonitorFactory.start("hi").stop();
        MonitorComposite mc2 = MonitorFactory.getRootMonitor().copy();

        MonitorFactory.start("again").stop();
        MonitorComposite mc3 = MonitorFactory.getRootMonitor().copy();

        List<MonitorComposite> list = Arrays.asList(mc1, mc2, mc3);
        MonitorComposite monitorComposite = MonitorCompositeCombiner.combine(list);
        assertThat(monitorComposite.getNumRows()).isEqualTo(9);
    }

    @Test
    public void testRemove() throws Exception {
        JamonDataPersister persister = mock(JamonDataPersister.class);
        MonitorCompositeCombiner mcc = new MonitorCompositeCombiner(persister);
        mcc.remove("local1", "local2", "local3");
        verify(persister).remove("local1");
        verify(persister).remove("local1");
        verify(persister).remove("local1");
        verify(persister, times(3)).remove(anyString());
    }

}