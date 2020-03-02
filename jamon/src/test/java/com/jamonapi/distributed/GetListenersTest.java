package com.jamonapi.distributed;

import com.jamonapi.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

public class GetListenersTest {


    @Test
    public void getAllNoListeners() {
        GetListeners getListeners = new GetListeners();
        FactoryEnabled factory = new FactoryEnabled();
        Monitor mon = factory.getMonitor("mymon", "units");
        assertThat(getListeners.getAll(mon).size()).isEqualTo(0);
    }

    @Test
    public void getAllWithValueListener() {
        GetListeners getListeners = new GetListeners();
        FactoryEnabled factory = new FactoryEnabled();
        Monitor mon = factory.getMonitor("mymon", "units");
        JAMonListener listener = JAMonListenerFactory.get("FIFOBuffer");
        mon.addListener("value", listener);

        List<GetListeners.ListenerInfo> list = getListeners.getAll(mon);
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).getListener()).isEqualTo(listener);
    }

    @Test
    public void getAllWithValueCompositeListener() {
        GetListeners getListeners = new GetListeners();
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

        List<GetListeners.ListenerInfo> list = getListeners.getAll(mon);
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
        GetListeners getListeners = new GetListeners();
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

        List<GetListeners.ListenerInfo> list = getListeners.getAll(mon);
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


}