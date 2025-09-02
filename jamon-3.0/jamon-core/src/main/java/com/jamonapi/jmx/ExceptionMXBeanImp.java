package com.jamonapi.jmx;

import com.jamonapi.JAMonBufferListener;
import com.jamonapi.JAMonListener;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import javax.management.ObjectName;
import java.util.Date;

/**
 * Track exceptions caught by jamon.
 */
public class ExceptionMXBeanImp implements ExceptionMXBean {
    private static final int STACKTRACE_INDEX = 1;
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

    @Override
    public Date getWhen() {
        return JmxUtils.getDate(LABEL, UNITS, Monitor.LASTACCESS);
    }

    private static String getMostRecentStacktrace(JAMonBufferListener listener) {
        Object[][] stackTraces = listener.getDetailData().getData();
        int mostRecent = stackTraces.length-1;
        return stackTraces[mostRecent][STACKTRACE_INDEX].toString();
    }
}
