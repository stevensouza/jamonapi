package com.jamonapi.jmx;

import com.jamonapi.MonKey;
import com.jamonapi.MonKeyImp;
import com.jamonapi.MonitorFactory;
import com.jamonapi.utils.NumberDelta;
import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GcInfo;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import java.lang.management.MemoryUsage;
import java.util.Date;
import java.util.Map;

/**
 * Class that can listen to any jmx notifications. It listens to gc collections from a sun jvm post 1.7 only it seems.
 *
 * http://www.fasterj.com/articles/gcnotifs.shtml
 */
public class GcMXBeanImp implements GcMXBean, NotificationListener {
    private String gcInfoString="";
    private long duration;
    private Date when;
    private static final String PREFIX = GcMXBeanImp.class.getPackage().getName();

    public static ObjectName getObjectName() {
        return JmxUtils.getObjectName( PREFIX + ":type=current,name=GcInfo");
    }

    public void handleNotification(Notification notification, Object handback) {
        String notifyType = notification.getType();
        if (notifyType.equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
            // retrieve the garbage collection notification information
            CompositeData cd = (CompositeData) notification.getUserData();
            GarbageCollectionNotificationInfo gcNotifyInfo = GarbageCollectionNotificationInfo.from(cd);
            monitor(gcNotifyInfo);
        }
    }

    private void monitor(GarbageCollectionNotificationInfo gcNotifyInfo) {
        // http://docs.oracle.com/javase/7/docs/jre/api/management/extension/com/sun/management/GcInfo.html
        // count times fired for each type, duration for each type, and delta between firings for each type.
        GcInfo gcInfo = gcNotifyInfo.getGcInfo();
        duration = gcInfo.getDuration();
        when = new Date();
        String details = toString(gcNotifyInfo);
        String labelPrefix = PREFIX + ".gc." + gcNotifyInfo.getGcName();

        // create jamon gc monitors
        MonKey key = new MonKeyImp(labelPrefix, details, "ms.");
        MonitorFactory.add(key, duration); // ms. duration of gc
        monitorReclaimedMemory(labelPrefix, gcInfo, details);
    }

    /**
     * Loop through different memory pools and determine how much of their memory has been reclaimed (bytes) in this
     * gc firing.   Example data follows:
     *
     *  before: PS Survivor Space=... used = 1589320(1552K)...,
     *  after: PS Survivor Space=... used = 0(0K)...
     *
     *  So a jamon monitor would be created for PS Survivor Space and its value would be 1589320.
     *  Note a positive value means that the pool now consumes less space (i.e. it is the reclaimed memory).
     *  A negative number means the pool now consumes more space than before the gc.
     *
     * @param labelPrefix
     * @param gcInfo
     * @param details
     */
    private void monitorReclaimedMemory(String labelPrefix, GcInfo gcInfo, String details) {
        for (Map.Entry<String, MemoryUsage> entry : gcInfo.getMemoryUsageAfterGc().entrySet()) {
            NumberDelta gcReclaimedMem = new NumberDelta();
            MemoryUsage afterMemoryUsage = entry.getValue();
            MemoryUsage beforeMemoryUsage = gcInfo.getMemoryUsageBeforeGc().get(entry.getKey());
            gcReclaimedMem.setValue(afterMemoryUsage.getUsed()).setValue(beforeMemoryUsage.getUsed());
            MonKey key = new MonKeyImp(labelPrefix+".reclaimedMemory."+entry.getKey(), details, "bytes");
            MonitorFactory.add(key, gcReclaimedMem.getDelta()); // reclaimed memory
        }
    }

    /**
     * Sample returned string:
     *  Name: PS MarkSweep
     *  Cause: System.gc()
     *  Action: end of major GC
     *  Duration: 43
     *  Sequence: 1
     *  When: Fri Nov 28 13:10:31 EST 2014
     *  BeforeGc: {PS Survivor Space=init = 2621440(2560K) used = 2242248(2189K) committed = 2621440(2560K) max = 2621440(2560K), PS Eden Space=init = 17301504(16896K) used = 0(0K) committed = 17301504(16896K) max = 352845824(344576K), PS Old Gen=init = 44564480(43520K) used = 8192(8K) committed = 44564480(43520K) max = 715653120(698880K), Code Cache=init = 2555904(2496K) used = 531712(519K) committed = 2555904(2496K) max = 50331648(49152K), PS Perm Gen=init = 22020096(21504K) used = 7591192(7413K) committed = 22020096(21504K) max = 85983232(83968K)}
     *  AfterGc: {PS Survivor Space=init = 2621440(2560K) used = 0(0K) committed = 2621440(2560K) max = 2621440(2560K), PS Eden Space=init = 17301504(16896K) used = 0(0K) committed = 17301504(16896K) max = 352845824(344576K), PS Old Gen=init = 44564480(43520K) used = 2075448(2026K) committed = 44564480(43520K) max = 715653120(698880K), Code Cache=init = 2555904(2496K) used = 531712(519K) committed = 2555904(2496K) max = 50331648(49152K), PS Perm Gen=init = 22020096(21504K) used = 7589328(7411K) committed = 22020096(21504K) max = 85983232(83968K)}
     *
     * @param gcNotifyInfo
     * @return
     */
    private String toString(GarbageCollectionNotificationInfo gcNotifyInfo) {
        GcInfo gcInfo = gcNotifyInfo.getGcInfo();
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(gcNotifyInfo.getGcName()).append("\n");
        sb.append("Cause: ").append(gcNotifyInfo.getGcCause()).append("\n");
        sb.append("Action: ").append(gcNotifyInfo.getGcAction()).append("\n");
        sb.append("Duration: ").append(gcInfo.getDuration()).append("\n");
        sb.append("Sequence: ").append(gcInfo.getId()).append("\n");
        sb.append("When: ").append(when).append("\n\n");
        sb.append("BeforeGc: ").append(gcInfo.getMemoryUsageBeforeGc()).append("\n\n");
        sb.append("AfterGc: ").append(gcInfo.getMemoryUsageAfterGc()).append("\n");
        gcInfoString = sb.toString();
        return gcInfoString;
    }

    @Override
    public String getGcInfo() {
        return gcInfoString;
    }

    @Override
    public Date getWhen() {
        return when;
    }

    @Override
    public long getDuration() {
        return duration;
    }
}
