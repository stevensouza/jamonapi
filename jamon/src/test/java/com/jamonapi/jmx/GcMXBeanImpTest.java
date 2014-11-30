package com.jamonapi.jmx;

import com.jamonapi.MonitorFactory;
import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GcInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

public class GcMXBeanImpTest {

    private GcMXBeanImp beanImp = new GcMXBeanImp();
    @Before
    public void setUp() throws Exception {
        MonitorFactory.reset();
    }

    @After
    public void tearDown() throws Exception {
        MonitorFactory.reset();
    }

    @Test
    public void shouldCreateMonitors() {
        // arrange
        GarbageCollectionNotificationInfo gcNotifyInfo = mock(GarbageCollectionNotificationInfo.class);
        GcInfo gcInfo = mock(GcInfo.class);
        MemoryUsage memoryUsage = mock(MemoryUsage.class);

        Map<String, MemoryUsage> memoryUsageAfterGc = new HashMap<String, MemoryUsage>();
        memoryUsageAfterGc.put("PS Old Gen", memoryUsage);
        memoryUsageAfterGc.put("Code Cache", memoryUsage);

        when(gcNotifyInfo.getGcInfo()).thenReturn(gcInfo);
        when(gcInfo.getDuration()).thenReturn(101L);
        when(gcInfo.getId()).thenReturn(201L);
        when(gcInfo.getMemoryUsageAfterGc()).thenReturn(memoryUsageAfterGc);
        when(gcNotifyInfo.getGcName()).thenReturn("mygcname");
        when(gcNotifyInfo.getGcCause()).thenReturn("mygccause");
        when(gcNotifyInfo.getGcAction()).thenReturn("mygcaction");
        when(gcNotifyInfo.getGcName()).thenReturn("mygcname");
        when(memoryUsage.getUsed()).thenReturn(301L);

        // act
        beanImp.monitor(gcNotifyInfo);

        // assert
        assertThat(MonitorFactory.getNumRows()).isEqualTo(3);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.jmx.gc.mygcname.time", "ms.").getLastValue()).isEqualTo(101);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.jmx.gc.mygcname.usedMemory.PS Old Gen", "bytes").getLastValue()).isEqualTo(301);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.jmx.gc.mygcname.usedMemory.Code Cache", "bytes").getLastValue()).isEqualTo(301);
    }
}