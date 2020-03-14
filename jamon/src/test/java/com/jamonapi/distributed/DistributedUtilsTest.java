package com.jamonapi.distributed;

import com.jamonapi.*;
import com.jamonapi.utils.BufferList;
import com.jamonapi.utils.Log4jUtils;
import com.jamonapi.utils.MiscTest;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DistributedUtilsTest {

    private static final int DETAILS_INDEX = 2;

    @Before
    public void setUp() {
        MonitorFactory.reset();
    }

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
        assertTrue(to.hasListener("value", DistributedUtils.getBufferName("FIFOBuffer")));
        assertTrue(to.hasListener("min", DistributedUtils.getBufferName("minFIFOBuffer")));
        assertTrue(to.hasListener("max", DistributedUtils.getBufferName("FIFOBuffer")));
        assertTrue(to.hasListener("maxactive", DistributedUtils.getBufferName("FIFOBuffer")));
        List<DistributedUtils.ListenerInfo> list = DistributedUtils.getAllListeners(to);
        assertThat(list.size()).isEqualTo(5);

        // check that the 'to' monitor has the proper number of rows in its buffer
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

    @Test
    public void testChangeInstanceName() throws Exception {
        final String NEW_INSTANCE_NAME = "newInstanceName";
        Log4jUtils.logWithLog4j("log4j2.xml");

        Monitor helloMon = MonitorFactory.getMonitor("hello", "ms.");
        helloMon.addListener("value", JAMonListenerFactory.get("FIFOBuffer"));
        MonitorFactory.start("hello").stop();
        MonitorFactory.start("world").stop();
        MonitorFactory.add("page", "counter", 1);
        MonitorComposite monitorComposite = MonitorFactory.getRootMonitor();
        MonitorComposite answer = DistributedUtils.changeInstanceName(NEW_INSTANCE_NAME, monitorComposite);

        assertThat(answer.getNumRows()).isEqualTo(monitorComposite.getNumRows());
        List<Monitor> list = new MonitorCompositeIterator(Arrays.asList(answer)).toList();
        assertThat(MiscTest.instanceNames(list)).containsOnly(NEW_INSTANCE_NAME);

        assertInstanceName(NEW_INSTANCE_NAME,answer.getMonitor(new MonKeyImp("com.jamonapi.log4j.JAMonAppender.TRACE","log4j")));
        assertInstanceName(NEW_INSTANCE_NAME,answer.getMonitor(new MonKeyImp("com.jamonapi.log4j.JAMonAppender.DEBUG","log4j")));
        assertInstanceName(NEW_INSTANCE_NAME,answer.getMonitor(new MonKeyImp("com.jamonapi.log4j.JAMonAppender.INFO","log4j")));
        assertInstanceName(NEW_INSTANCE_NAME,answer.getMonitor(new MonKeyImp("com.jamonapi.log4j.JAMonAppender.WARN","log4j")));
        assertInstanceName(NEW_INSTANCE_NAME,answer.getMonitor(new MonKeyImp("com.jamonapi.log4j.JAMonAppender.ERROR","log4j")));
        assertInstanceName(NEW_INSTANCE_NAME,answer.getMonitor(new MonKeyImp("com.jamonapi.log4j.JAMonAppender.FATAL","log4j")));
        assertInstanceName(NEW_INSTANCE_NAME,answer.getMonitor(new MonKeyImp("com.jamonapi.log4j.JAMonAppender.TOTAL","log4j")));
    }

    @Test
    public void getFifoFirstIndexToBeAdded() {
        // test all combinations of combinedFifoBufferSize(>,=,<)sourceBufferSize with 1,2,3 instances
        // sourceBufferSize of 5 has index values 0,1,2,3,4

        // combinedFifoBufferSize>sourceBufferSize
        assertThat(DistributedUtils.getFifoFirstIndexToBeAdded(10,5, 1)).isEqualTo(0);
        assertThat(DistributedUtils.getFifoFirstIndexToBeAdded(10,5, 2)).isEqualTo(0);
        assertThat(DistributedUtils.getFifoFirstIndexToBeAdded(10,5, 3)).isEqualTo(2); // index 2 gets 3 values from each buffer for a total of 9

        // combinedFifoBufferSize<sourceBufferSize
        assertThat(DistributedUtils.getFifoFirstIndexToBeAdded(5,10, 1)).isEqualTo(5);
        assertThat(DistributedUtils.getFifoFirstIndexToBeAdded(5,10, 2)).isEqualTo(8);
        assertThat(DistributedUtils.getFifoFirstIndexToBeAdded(5,10, 3)).isEqualTo(9);// last values index

        // combinedFifoBufferSize==sourceBufferSize
        assertThat(DistributedUtils.getFifoFirstIndexToBeAdded(10,10, 1)).isEqualTo(0);
        assertThat(DistributedUtils.getFifoFirstIndexToBeAdded(10,10, 2)).isEqualTo(5);
        assertThat(DistributedUtils.getFifoFirstIndexToBeAdded(10,10, 3)).isEqualTo(7);
    }


    @Test
    public void copyJamonBufferListenerData_lowData() {
        // less data in 'from' buffers (2 of 50 rows) than in 'to' buffer (250 rows)
        Monitor from1 = MonitorFactory.getMonitor("label1", "count");
        JAMonBufferListener listener1 = (JAMonBufferListener) JAMonListenerFactory.get("FIFOBuffer");
        from1.addListener("value", listener1);

        Monitor from2 = MonitorFactory.getMonitor("label2", "count");
        JAMonBufferListener listener2 = (JAMonBufferListener) JAMonListenerFactory.get("FIFOBuffer");
        from2.addListener("value", listener2);

        for (int i=0;i<1000;i++) {
            MonitorFactory.add(new MonKeyImp("label1", i, "count"), i);
            MonitorFactory.add(new MonKeyImp("label2", i, "count"), i);
        }
        Monitor to = MonitorFactory.getMonitor();
        // from buffer size=50
        DistributedUtils.copyJamonBufferListenerData(from1, to, 2);
        DistributedUtils.copyJamonBufferListenerData(from2, to, 2);

        JAMonBufferListener toListener = (JAMonBufferListener) to.getListenerType("value").getListener(DistributedUtils.getBufferName("FIFOBuffer"));
        // so there should be 100 rows in 'to' buffer for the last rows i.e first 50 rows
        // would have values 50..99 followed by another 50 rows of values 50..99
        assertThat(toListener.getRowCount()).isEqualTo(100);
        Object details = ((Object[])toListener.getBufferList().getCollection().get(49))[DETAILS_INDEX];
        assertThat(details).isEqualTo(999.0);
        details = ((Object[])toListener.getBufferList().getCollection().get(99))[DETAILS_INDEX];
        assertThat(details).isEqualTo(999.0);
    }

    @Test
    public void copyJamonBufferListenerData_highData() {
        // less data in 'from' buffers (2 of 1000 rows) than in 'to' buffer (250 rows)
        Monitor from1 = MonitorFactory.getMonitor("label1", "count");
        JAMonBufferListener listener1 = (JAMonBufferListener) JAMonListenerFactory.get("FIFOBuffer");
        listener1.getBufferList().setBufferSize(1000);
        from1.addListener("value", listener1);

        Monitor from2 = MonitorFactory.getMonitor("label2", "count");
        JAMonBufferListener listener2 = (JAMonBufferListener) JAMonListenerFactory.get("FIFOBuffer");
        listener2.getBufferList().setBufferSize(1000);
        from2.addListener("value", listener2);

        for (int i=0;i<1000;i++) {
            MonitorFactory.add(new MonKeyImp("label1", i, "count"), i);
            MonitorFactory.add(new MonKeyImp("label2", i, "count"), i);
        }
        Monitor to = MonitorFactory.getMonitor();
        // from buffer size=50
        DistributedUtils.copyJamonBufferListenerData(from1, to, 2);
        DistributedUtils.copyJamonBufferListenerData(from2, to, 2);

        JAMonBufferListener toListener = (JAMonBufferListener) to.getListenerType("value").getListener(DistributedUtils.getBufferName("FIFOBuffer"));
        // so there should be 250 rows in 'to' buffer for the last 125 rows in each 'from' buffer
        assertThat(toListener.getRowCount()).isEqualTo(250);
        Object details = ((Object[])toListener.getBufferList().getCollection().get(124))[DETAILS_INDEX];
        assertThat(details).isEqualTo(999.0);
        details = ((Object[])toListener.getBufferList().getCollection().get(249))[DETAILS_INDEX];
        assertThat(details).isEqualTo(999.0);
    }

    private void assertBufferListeners(Monitor to, String listenerType, String listenerName, int expectedRows) {
        JAMonBufferListener jaMonBufferListener = (JAMonBufferListener) to.getListenerType(listenerType).getListener(DistributedUtils.getBufferName(listenerName));
        BufferList bufferList = jaMonBufferListener.getBufferList();
        assertThat(bufferList.getBufferSize()).isEqualTo(DistributedUtils.COMBINED_FIFO_BUFFER_SIZE);
        assertThat(bufferList.getRowCount()).isEqualTo(expectedRows);
        if (expectedRows > 0) {
            Object[][] data = bufferList.getDetailData().getData();
            assertThat(data[0][0].toString()).isEqualTo("local");
        }
    }

    private void assertInstanceName(String expectedInstanceName, Monitor monitor) {
        assertThat(expectedInstanceName).isEqualTo(monitor.getMonKey().getInstanceName());
        JAMonBufferListener log4jBufferListener = (JAMonBufferListener) monitor.getListenerType("value")
                .getListener("FIFOBuffer");
        log4jBufferListener.getBufferList().getCollection().stream().forEach(obj -> {
            JAMonDetailValue value =  ((JAMonDetailValue)obj);
            assertThat(expectedInstanceName).isEqualTo(value.getMonKey().getInstanceName());
        });
    }
}