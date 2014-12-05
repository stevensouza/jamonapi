package com.jamonapi.jmx;

import com.jamonapi.MonKey;
import com.jamonapi.MonKeyImp;
import com.jamonapi.MonitorFactory;
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
 * Class that can listen to any jmx notifications. It listens to gc collections from a sun jvm only and that is
 * only versions at 1.7 and after (unfortunately). JAMon monitors are created for <br>
 *     * gc duration as well as...<br>
 *     * memory used in each of the memory pools after the gc has fired.<br><br>
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

    /** This is called automatically when a gc is fired */
    public void handleNotification(Notification notification, Object handback) {
        String notifyType = notification.getType();
        if (notifyType.equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
            // retrieve the garbage collection notification information
            CompositeData cd = (CompositeData) notification.getUserData();
            GarbageCollectionNotificationInfo gcNotifyInfo = GarbageCollectionNotificationInfo.from(cd);
            monitor(gcNotifyInfo);
        }
    }

    /* This method is visible for testing purposes */
    void monitor(GarbageCollectionNotificationInfo gcNotifyInfo) {
        // http://docs.oracle.com/javase/7/docs/jre/api/management/extension/com/sun/management/GcInfo.html
        // for each type count: times fired, duration, and used memory
        GcInfo gcInfo = gcNotifyInfo.getGcInfo();
        duration = gcInfo.getDuration();
        when = new Date();
        String details = toString(gcNotifyInfo);
        String labelPrefix = PREFIX + ".gc." + gcNotifyInfo.getGcName();

        // create jamon gc monitors
        monitorDuration(labelPrefix, details);
        monitorUsedMemory(labelPrefix, gcInfo, details);
    }

    private void monitorDuration(String labelPrefix, String details) {
        MonKey key = new MonKeyImp(labelPrefix+".time", details, "ms.");
        MonitorFactory.add(key, duration); // ms. duration of gc
    }

    /**
     * Loop through different memory pools and determine how much memory is used (bytes) after this
     * gc firing.   Example data follows:
     *
     *  before: PS Survivor Space=... used = 1589320(1552K)...,
     *  after: PS Survivor Space=... used = 0(0K)...
     *
     * @param labelPrefix
     * @param gcInfo
     * @param details
     */
    private void monitorUsedMemory(String labelPrefix, GcInfo gcInfo, String details) {
        for (Map.Entry<String, MemoryUsage> entry : gcInfo.getMemoryUsageAfterGc().entrySet()) {
            MonKey key = new MonKeyImp(labelPrefix+".usedMemory."+entry.getKey(), details, "bytes");
            MonitorFactory.add(key, entry.getValue().getUsed()); // memory used in pool.
        }
    }

    /**
     * This will be placed in the details part of the MonKey which is used for display in the mondetails.jsp page.
     *
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
