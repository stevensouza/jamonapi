package com.jamonapi.jmx;

import com.jamonapi.JAMonBufferListener;
import com.jamonapi.JAMonDetailValue;
import com.jamonapi.JAMonListener;
import com.jamonapi.MonitorFactory;

import javax.management.ObjectName;

/**
 * Created by stevesouza on 11/19/14.
 */
public class ExceptionMXBeanImp implements ExceptionMXBean {
    private static final String LABEL = "com.jamonapi.Exceptions";
    private static final String UNITS = "Exception";

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

        JAMonBufferListener bufferListener = (JAMonBufferListener) listener;
        int lastElement = bufferListener.getRowCount()-1;
        return ((JAMonDetailValue)bufferListener.getBufferList().getCollection().get(lastElement)).toArray()[0].toString();
    }

    @Override
    public long getExceptionCount() {
        return JmxUtils.getCount(LABEL, UNITS);
    }
}
