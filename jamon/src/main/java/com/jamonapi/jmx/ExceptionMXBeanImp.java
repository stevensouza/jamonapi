package com.jamonapi.jmx;

import com.jamonapi.JAMonBufferListener;
import com.jamonapi.JAMonDetailValue;
import com.jamonapi.JAMonListener;
import com.jamonapi.MonitorFactory;
import com.jamonapi.utils.DetailData;
import com.jamonapi.utils.NumberDelta;

import javax.management.ObjectName;

/**
 * Created by stevesouza on 11/19/14.
 */
public class ExceptionMXBeanImp implements ExceptionMXBean {
    private static final int STACKTRACE = 0;

    private NumberDelta delta = new NumberDelta();

    public static ObjectName getObjectName() {
        return JmxUtils.getObjectName(ExceptionMXBean.class.getPackage().getName() + ":type=current,name=Exceptions");
    }

    @Override
    public String getMostRecentException() {
        if (!MonitorFactory.exists(LABEL, UNITS)) {
            return "No exceptions have been thrown";
        }
        if (!MonitorFactory.getMonitor(LABEL, UNITS).hasListener("value", "FIFOBuffer")) {
            return "Exception Stacktrace tracking is not enabled.";
        }
        JAMonListener listener = MonitorFactory.getMonitor(LABEL, UNITS).getListenerType("value").getListener("FIFOBuffer");
        if (!(listener instanceof JAMonBufferListener) || ((JAMonBufferListener) listener).isEmpty()) {
            return "There are no stacktraces";
        }

        return getMostRecentStacktrace((JAMonBufferListener) listener);
    }

    @Override
    public long getExceptionCount() {
        return JmxUtils.getCount(LABEL, UNITS);
    }

    private static String getMostRecentStacktrace(JAMonBufferListener listener) {
        Object[][] stackTraces = listener.getDetailData().getData();
        int mostRecent = stackTraces.length-1;
        return stackTraces[mostRecent][STACKTRACE].toString();
    }
}
