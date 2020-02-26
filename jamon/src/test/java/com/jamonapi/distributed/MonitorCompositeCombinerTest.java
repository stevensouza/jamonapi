package com.jamonapi.distributed;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorComposite;
import com.jamonapi.MonitorFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
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
        // hello, world, com.jamonapi.Exceptions
        MonitorComposite mc1 = MonitorFactory.getRootMonitor().copy();

        MonitorFactory.start("hi").stop();
        // hi,hello, world, com.jamonapi.Exceptions
        MonitorComposite mc2 = MonitorFactory.getRootMonitor().copy();

        MonitorFactory.start("again").stop();
        // again,hi,hello, world, com.jamonapi.Exceptions
        MonitorComposite mc3 = MonitorFactory.getRootMonitor().copy();

        when(persister.get("local1")).thenReturn(mc1);
        when(persister.get("local2")).thenReturn(mc2);
        when(persister.get("local3")).thenReturn(mc3);

        MonitorComposite monitorComposite = mcc.get("local1", "local2", "local3");
        assertThat(monitorComposite.getNumRows()).isEqualTo(12);
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
        MonitorComposite monitorComposite = new MonitorCompositeCombiner(null).append(list);
        assertThat(monitorComposite.getNumRows()).isEqualTo(12);
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

    @Test
    public void merge() {
        Monitor from = MonitorFactory.getMonitor();
        Monitor to = MonitorFactory.getMonitor();
//        Date lastAccess = new Date(System.currentTimeMillis()-1000000); // ? fails
        Date firstAccess = new Date();
        Date lastAccess = new Date(System.currentTimeMillis() + 1000000);

        to.add(10);
        from.add(20);
        from.add(30);

        from.setActive(5); // this monitor
        to.setActive(1);

        from.setTotalActive(20); // all monitors, note getAvgActive()=totalActive/hits
        to.setTotalActive(10);

        from.setPrimary(true);

        from.setMaxActive(11); // this monitors max active
        from.getMaxActive();

        from.setFirstAccess(lastAccess);
        from.setLastAccess(lastAccess);
        to.setFirstAccess(firstAccess);
        to.setLastAccess(firstAccess);

        new MonitorCompositeCombiner(null).merge(from, to);

        assertThat(to.getHits()).isEqualTo(3);
        assertThat(to.getTotal()).isEqualTo(60);
        assertThat(to.getLastValue()).isEqualTo(30); // test when other value is last????
        assertThat(to.getMin()).isEqualTo(10);
        assertThat(to.getMax()).isEqualTo(30);
        assertThat(to.getActive()).isEqualTo(6);
        assertThat(to.getMaxActive()).isEqualTo(11);
        assertThat(to.getAvgActive()).isEqualTo(10);
        assertThat(to.getFirstAccess()).isEqualTo(firstAccess);
        assertThat(to.getLastAccess()).isEqualTo(lastAccess);
        assertTrue(to.isEnabled());
        assertTrue(to.isPrimary());
        MonitorCompositeCombiner.StdDev stdDev = (MonitorCompositeCombiner.StdDev) to.getMonKey().getDetails();
        assertThat(stdDev.getAvgStdDev()).isBetween(7.07, 7.08); // 7.0710678118654755
    }

}
