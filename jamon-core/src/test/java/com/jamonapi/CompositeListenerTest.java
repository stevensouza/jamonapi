package com.jamonapi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CompositeListenerTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void shouldAllowMultipleCompositeListenersWithSameName() throws Exception {
        CompositeListener listener = new CompositeListener();
        listener.addListener(new CompositeListener("listener1"));
        listener.addListener(new CompositeListener("listener1"));

        assertThat(listener.getNumListeners()).isEqualTo(2);
    }

    @Test
    public void shouldAllowOneListenerWithSameName() throws Exception {
        JAMonListener listener1  = mock(JAMonListener.class);
        when(listener1.getName()).thenReturn("listener1");

        JAMonListener listener2  = mock(JAMonListener.class);
        when(listener2.getName()).thenReturn("listener1");

        CompositeListener compositeListener = new CompositeListener();
        compositeListener.addListener(listener1);
        compositeListener.addListener(listener2);

        assertThat(compositeListener.getNumListeners()).isEqualTo(1);
        assertThat(compositeListener.getListener("listener1")).isEqualTo(listener1);
        assertThat(compositeListener.getListener(0)).isEqualTo(listener1);
    }

    @Test
    public void shouldAllowMultipleListenersWithSameNameWithComposites() throws Exception {
        JAMonListener listener1  = mock(JAMonListener.class);
        when(listener1.getName()).thenReturn("listener");

        JAMonListener listener2  = mock(JAMonListener.class);
        when(listener2.getName()).thenReturn("listener");

        JAMonListener listener3  = mock(JAMonListener.class);
        when(listener3.getName()).thenReturn("listener3");

        CompositeListener listener = new CompositeListener();
        listener.addListener(listener1);
        CompositeListener compositeListener2 = new CompositeListener();
        compositeListener2.addListener(listener2);
        compositeListener2.addListener(listener3);
        listener.addListener(compositeListener2);

        assertThat(listener.getNumListeners()).isEqualTo(2);
    }
}