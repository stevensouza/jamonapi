package com.jamonapi.distributed;

import com.jamonapi.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.jamonapi.distributed.MonitorCompositeCombiner.AGGREGATED_MONITOR_LABEL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class MonitorCompositeCombinerTest {
    private static int NOT_APPLICABLE = -1;

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
        assertThat(to.getStdDev()).isNaN(); // standard deviation isn't calculated when aggrregating instances. would have to redesign some things to get this done.
    }

    @Test
    public void mergeFromIsLastAccess() {
        Monitor from = MonitorFactory.getMonitor();
        Monitor to = MonitorFactory.getMonitor();
        Date lastAccess = new Date(System.currentTimeMillis() + 1000000); // ? fails
        Date firstAccess = new Date();

        to.add(10);
        from.add(20);

        from.setFirstAccess(lastAccess);
        from.setLastAccess(lastAccess);
        to.setFirstAccess(firstAccess);
        to.setLastAccess(firstAccess);

        new MonitorCompositeCombiner(null).merge(from, to);

        assertThat(to.getLastValue()).isEqualTo(20); // test when other value is last????
        assertThat(to.getFirstAccess()).isEqualTo(firstAccess);
        assertThat(to.getLastAccess()).isEqualTo(lastAccess);
    }

    @Test
    public void mergeToIsLastAccess() {
        Monitor from = MonitorFactory.getMonitor();
        Monitor to = MonitorFactory.getMonitor();
        Date lastAccess = new Date(System.currentTimeMillis() + 1000000);
        Date firstAccess = new Date();
        Date secondAccess = new Date(System.currentTimeMillis() + 50000);

        to.add(10);
        from.add(20);

        from.setFirstAccess(firstAccess);
        from.setLastAccess(firstAccess);
        to.setFirstAccess(secondAccess);
        to.setLastAccess(lastAccess);

        new MonitorCompositeCombiner(null).merge(from, to);

        assertThat(to.getLastValue()).isEqualTo(10); // test when other value is last????
        assertThat(to.getFirstAccess()).isEqualTo(firstAccess);
        assertThat(to.getLastAccess()).isEqualTo(lastAccess);
    }

    @Test
    public void aggregate() {
        MonKey key = new MonKeyImp("instance.label1", "count");
        key.setInstanceName("first");
        Monitor mon = MonitorFactory.getMonitor(key);
        mon.addListener("value", JAMonListenerFactory.get("FIFOBuffer"));
        MonitorFactory.add(key, 10);

        MonitorFactory.add("instance1.label2", "bytes", 20);
        MonitorComposite mc1 = MonitorFactory.getRootMonitor();
        MonitorFactory.reset();

        key = new MonKeyImp("instance.label1", "count");
        key.setInstanceName("second");
        mon = MonitorFactory.getMonitor(key);
        mon.addListener("value", JAMonListenerFactory.get("FIFOBuffer"));
        MonitorFactory.add(key, 40);
        MonitorFactory.add("instance2.label1", "bytes", 30);
        MonitorComposite mc2 = MonitorFactory.getRootMonitor();
        MonitorFactory.reset();

        JamonDataPersister persister = mock(JamonDataPersister.class);
        when(persister.get("instance1")).thenReturn(mc1);
        when(persister.get("instance2")).thenReturn(mc2);
        String[] instances = {"instance1", "instance2"};

        MonitorCompositeCombiner monitorCompositeCombiner = new MonitorCompositeCombiner(persister);
        MonitorComposite aggregate = monitorCompositeCombiner.aggregate(instances);

        assertThat(aggregate.getNumRows()).isEqualTo(4);
        assertThat(aggregate.getInstanceName()).isEqualTo(MonitorCompositeCombiner.AGGREGATED_INSTANCENAME);
        assertTrue(aggregate.hasListeners());

        isExpected(aggregate.getMonitor(key), 2, 50, true);
        isExpected(aggregate.getMonitor(new MonKeyImp("instance1.label2", "bytes")), 1, 20, true);
        isExpected(aggregate.getMonitor(new MonKeyImp("instance2.label1", "bytes")), 1, 30, true);
        isExpected(aggregate.getMonitor(new MonKeyImp("com.jamonapi.Exceptions", "Exception")), 0, 0, true);
        assertThat(MonitorFactory.getMonitor(new MonKeyImp(AGGREGATED_MONITOR_LABEL, "ms.")).getHits()).isEqualTo(2);
        assertTrue(MonitorFactory.getMonitor(new MonKeyImp(AGGREGATED_MONITOR_LABEL, "ms.")).hasListeners());
        mon = aggregate.getMonitor(key);
        assertTrue(mon.hasListener("value", DistributedUtils.getBufferName("FIFOBuffer")));
        JAMonBufferListener jaMonBufferListener = (JAMonBufferListener) mon.getListenerType("value").getListener(DistributedUtils.getBufferName("FIFOBuffer"));
        assertThat(jaMonBufferListener.getRowCount()).isEqualTo(2);

        // instanceName of returned data should only be instance1, and instance2
        final int INSTANCE_NAME = 0;
        List<Object[]> data = Arrays.asList(jaMonBufferListener.getBufferList().getDetailData().getData());
        Set<String> set = data.stream().map(arr->arr[INSTANCE_NAME].toString()).collect(Collectors.toSet());
        assertThat(set).containsOnly("first","second");
    }

    private void isExpected(Monitor mon, int hits, double total, boolean bufferListenerExists) {
        assertThat(mon.getMonKey().getInstanceName()).isEqualTo(MonitorCompositeCombiner.AGGREGATED_INSTANCENAME);
        assertThat(mon.getHits()).isEqualTo(hits);

        if (total != NOT_APPLICABLE) {
            assertThat(mon.getTotal()).isEqualTo(total);
        }

        if (bufferListenerExists) {
            assertTrue(mon.getListenerType("value").hasListener(MonitorCompositeCombiner.SUMMARY_LISTENER));
        }
    }

}
