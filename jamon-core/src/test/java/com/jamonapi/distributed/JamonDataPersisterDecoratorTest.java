package com.jamonapi.distributed;

import com.jamonapi.MonitorComposite;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


public class JamonDataPersisterDecoratorTest {

    private LocalJamonDataPersister localPersister = mock(LocalJamonDataPersister.class);
    private JamonDataPersister persister = mock(JamonDataPersister.class);
    private JamonDataPersisterDecorator persisterDecorator = new JamonDataPersisterDecorator(persister, localPersister);
    private MonitorComposite monitorComposite = mock(MonitorComposite.class);

    @Test
    public void testGetInstances() throws Exception {
        Set<String> localInstances = new HashSet<String>();
        localInstances.add("local1");
        localInstances.add("local2");

        Set<String> persisterInstances = new HashSet<String>();
        persisterInstances.add("local3");
        persisterInstances.add("local4");

        when(localPersister.getInstances()).thenReturn(localInstances);
        when(persister.getInstances()).thenReturn(persisterInstances);

        Set<String> answer = new HashSet<String>();
        answer.addAll(localInstances);
        answer.addAll(persisterInstances);
        assertThat(persisterDecorator.getInstances()).containsOnly(answer.toArray(new String[0]));
    }

    @Test
    public void testPut() throws Exception {
        when(persister.getInstance()).thenReturn("any_instance_name");
        persisterDecorator.put();
        verify(localPersister, times(1)).put();
        verify(persister, times(1)).put("any_instance_name");
    }

    @Test
    public void testGetFromLocal() throws Exception {
        when(localPersister.get(anyString())).thenReturn(monitorComposite);
        MonitorComposite mc = persisterDecorator.get("local");
        verify(localPersister, times(1)).get(anyString());
        verify(persister, times(0)).get(anyString());
        assertThat(mc).isEqualTo(monitorComposite);
    }

    @Test
    public void testGetFromPersister() throws Exception {
        when(localPersister.get(anyString())).thenReturn(null);
        when(persister.get(anyString())).thenReturn(monitorComposite);
        MonitorComposite mc = persisterDecorator.get("instancename");
        verify(localPersister, times(1)).get(anyString());
        verify(persister, times(1)).get(anyString());
        assertThat(mc).isEqualTo(monitorComposite);
    }

    @Test
    public void testRemove() throws Exception {
        persisterDecorator.remove("instancename");
        verify(localPersister, times(1)).remove(anyString());
        verify(persister, times(1)).remove(anyString());
    }

    @Test
    public void testGetInstance() throws Exception {
        when(persister.getInstance()).thenReturn("instancename");
        assertThat(persisterDecorator.getInstance()).isEqualTo("instancename");
    }

    @Test
    public void testGetJamonDataPersister() throws Exception {
        assertThat(persisterDecorator.getJamonDataPersister()).isEqualTo(persister);
    }
}
