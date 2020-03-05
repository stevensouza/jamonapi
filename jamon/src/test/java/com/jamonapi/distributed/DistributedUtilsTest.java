package com.jamonapi.distributed;

import com.jamonapi.*;
import com.jamonapi.utils.BufferList;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DistributedUtilsTest {


    @Test
    public void getAllNoListeners() {
        FactoryEnabled factory = new FactoryEnabled();
        Monitor mon = factory.getMonitor("mymon", "units");
        assertThat(DistributedUtils.getAllListeners(mon).size()).isEqualTo(0);
    }

    @Test
    public void getAllWithValueListener() {
        FactoryEnabled factory = new FactoryEnabled();
        Monitor mon = factory.getMonitor("mymon", "units");
        JAMonListener listener = JAMonListenerFactory.get("FIFOBuffer");
        mon.addListener("value", listener);

        List<DistributedUtils.ListenerInfo> list = DistributedUtils.getAllListeners(mon);
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).getListener()).isEqualTo(listener);
    }

    @Test
    public void getAllWithValueCompositeListener() {
        FactoryEnabled factory = new FactoryEnabled();
        Monitor mon = factory.getMonitor("mymon", "units");
        JAMonListener listener1 = JAMonListenerFactory.get("FIFOBuffer");
        JAMonListener listener2 = JAMonListenerFactory.get("NSmallestValueBuffer");
        JAMonListener listener3 = JAMonListenerFactory.get("SharedFIFOBuffer");
        JAMonListener listener4 = JAMonListenerFactory.get("NLargestValueBuffer");

        CompositeListener compositeListener = new CompositeListener();
        compositeListener.addListener(listener2);
        compositeListener.addListener(listener3);

        mon.addListener("value", listener1);
        mon.addListener("value", compositeListener);
        mon.addListener("value", listener4);

        List<DistributedUtils.ListenerInfo> list = DistributedUtils.getAllListeners(mon);
        // note a better check would be for the examples to be the exact expected instances. However, jamon in some cases
        // changes the instance.
        assertThat(list.size()).isEqualTo(4);
        List<String> actual = Arrays.asList(list.get(0).getListener().getName(), list.get(1).getListener().getName(), list.get(2).getListener().getName(), list.get(3).getListener().getName());
        List<String> expected = Arrays.asList(listener1.getName(), listener2.getName(), listener3.getName(), listener4.getName());

        assertThat(actual).isEqualTo(expected);

        String value = "value";
        expected = Arrays.asList(value, value, value, value);
        actual = Arrays.asList(list.get(0).getListenerType(), list.get(1).getListenerType(), list.get(2).getListenerType(), list.get(3).getListenerType());
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void getAllWithAllListeners() {
        FactoryEnabled factory = new FactoryEnabled();
        Monitor mon = factory.getMonitor("mymon", "units");
        JAMonListener listener1 = JAMonListenerFactory.get("FIFOBuffer");
        JAMonListener listener2 = JAMonListenerFactory.get("NSmallestValueBuffer");
        JAMonListener listener3 = JAMonListenerFactory.get("SharedFIFOBuffer");
        JAMonListener listener4 = JAMonListenerFactory.get("NLargestValueBuffer");

        mon.addListener("value", listener1);
        mon.addListener("max", listener2);
        mon.addListener("min", listener3);
        mon.addListener("maxactive", listener4);

        List<DistributedUtils.ListenerInfo> list = DistributedUtils.getAllListeners(mon);
        // note a better check would be for the examples to be the exact expected instances. However, jamon in some cases
        // changes the instance.
        assertThat(list.size()).isEqualTo(4);
        List<String> actual = Arrays.asList(list.get(0).getListener().getName(), list.get(1).getListener().getName(), list.get(2).getListener().getName(), list.get(3).getListener().getName());
        List<String> expected = Arrays.asList(listener1.getName(), listener2.getName(), listener3.getName(), listener4.getName());

        assertThat(actual.size()).isEqualTo(4);
        assertTrue(actual.containsAll(expected));

        expected = Arrays.asList("value", "max", "min", "maxactive");
        actual = Arrays.asList(list.get(0).getListenerType(), list.get(1).getListenerType(), list.get(2).getListenerType(), list.get(3).getListenerType());
        assertThat(actual.size()).isEqualTo(4);
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void copyJamonBufferListenerData_noData() {
        FactoryEnabled factory = new FactoryEnabled();

        Monitor from = factory.getMonitor("from", "count");
        Monitor to = factory.getMonitor("to", "count");

        DistributedUtils.copyJamonBufferListenerData(from, to);
        assertFalse(to.hasListeners());
    }

    @Test
    public void copyJamonBufferListenerData_withListeners() {
        FactoryEnabled factory = new FactoryEnabled();
        JAMonBufferListener fifo = (JAMonBufferListener) JAMonListenerFactory.get("FIFOBuffer");
        JAMonListener minFifo = JAMonListenerFactory.get("FIFOBuffer");
        minFifo.setName("minFIFOBuffer");// to test with a different name
        JAMonListener nLargest = JAMonListenerFactory.get("NLargestValueBuffer");

        Monitor from = factory.getMonitor("from", "count");
        from.addListener("value", fifo.copy());
        from.addListener("min", minFifo);
        from.addListener("max", fifo.copy());
        from.addListener("max", nLargest);
        from.addListener("maxactive", fifo.copy());
        from.add(1);
        from.add(2);

        Monitor to = factory.getMonitor("to", "count");

        DistributedUtils.copyJamonBufferListenerData(from, to);
        // check that listeners were properly created.
        assertTrue(to.hasListeners());
        assertTrue(to.hasListener("value", DistributedUtils.getFifoBufferName("FIFOBuffer")));
        assertTrue(to.hasListener("min", DistributedUtils.getFifoBufferName("minFIFOBuffer")));
        assertTrue(to.hasListener("max", DistributedUtils.getFifoBufferName("FIFOBuffer")));
        assertTrue(to.hasListener("maxactive", DistributedUtils.getFifoBufferName("FIFOBuffer")));
        List<DistributedUtils.ListenerInfo> list = DistributedUtils.getAllListeners(to);
        assertThat(list.size()).isEqualTo(5);

        // check that the 'to' monitor has the proper number of rows in its buffer\
        assertBufferListeners(to, "value", "FIFOBuffer", 2);
        assertBufferListeners(to, "min", "minFIFOBuffer", 1);
        assertBufferListeners(to, "max", "FIFOBuffer", 2);
        assertBufferListeners(to, "maxactive", "FIFOBuffer", 0);

        // max active buffer check
        from = factory.getTimeMonitor("from");
        from.addListener("maxactive", fifo.copy());
        from.start();
        factory.getTimeMonitor("from").start().stop(); // this will allow for a maxactive
        from.stop();
        DistributedUtils.copyJamonBufferListenerData(from, to);
        assertBufferListeners(to, "maxactive", "FIFOBuffer", 1);
    }

    private void assertBufferListeners(Monitor to, String listenerType, String listenerName, int expectedRows) {
        JAMonBufferListener jaMonBufferListener = (JAMonBufferListener) to.getListenerType(listenerType).getListener(DistributedUtils.getFifoBufferName(listenerName));
        BufferList bufferList = jaMonBufferListener.getBufferList();
        assertThat(bufferList.getBufferSize()).isEqualTo(DistributedUtils.DEFAULT_BUFFER_SIZE);
        assertThat(bufferList.getRowCount()).isEqualTo(expectedRows);
    }


}