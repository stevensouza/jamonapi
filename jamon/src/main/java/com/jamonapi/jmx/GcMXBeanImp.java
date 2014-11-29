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
import java.util.Date;

/**
 * Class that can listen to any jmx notifications.  It is a good place to see how gc notifications work. I also register and
 * send my own notification.  See MyMXBean.setAttrib1(...)
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
        String gcName = gcNotifyInfo.getGcName();

        MonKey key = new MonKeyImp(PREFIX + ".gc." + gcName, details, "ms.");
        MonitorFactory.add(key, gcInfo.getDuration()); // ms. of gc


//        System.out.println("xxxxGarbageCollectionNotificationInfo:");
//        System.out.println("  getGcAction:"+gcNotifyInfo.getGcAction());
//        System.out.println("  getGcCause:"+gcNotifyInfo.getGcCause());
//        System.out.println("  getGcName:"+gcNotifyInfo.getGcName());
//        System.out.println("  gcInfo:");
//
//        // http://docs.oracle.com/javase/7/docs/jre/api/management/extension/com/sun/management/GcInfo.html
//        // count times fired for each type, duration for each type, and delta between firings for each type.
//        System.out.println("    gcInfo.getStartTime:"+gcInfo.getStartTime()); // ms since server started
//        System.out.println("    gcInfo.getEndTime:"+gcInfo.getEndTime());  // ms since server started
//        System.out.println("    gcInfo.getDuration (ms):"+gcInfo.getDuration()); // simple math of above
//        System.out.println("    gcInfo.getId:"+gcInfo.getId()); // number of times this collector has fired since startup
//        // probably for jamon use the following for each of the following maps.  not sure what yet, but used looks good and maybe deltas
//        // http://docs.oracle.com/javase/7/docs/api/java/lang/management/MemoryUsage.html
//        System.out.println("    gcInfo.getMemoryUsageBeforeGc:"+gcInfo.getMemoryUsageBeforeGc()); // Map<String,MemoryUsage>
//        System.out.println("    gcInfo.getMemoryUsageAfterGc:"+gcInfo.getMemoryUsageAfterGc()); // Map<String,MemoryUsage>
//        System.out.println("    gcInfo.values:"+gcInfo.values());

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
