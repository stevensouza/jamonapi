package com.jamonapi.jmx;

import com.jamonapi.JAMonBufferListener;
import com.jamonapi.JAMonListener;
import com.jamonapi.MonitorFactory;
import com.jamonapi.utils.NumberDelta;

import javax.management.ObjectName;

/**
 * Created by stevesouza on 11/19/14.
 */
public class ExceptionDeltaMXBeanImp extends ExceptionMXBeanImp {
    private NumberDelta delta = new NumberDelta();

    public static ObjectName getObjectName() {
        return JmxUtils.getObjectName(ExceptionMXBean.class.getPackage().getName() + ":type=delta,name=Exceptions");
    }

    @Override
    public long getExceptionCount() {
        long count = super.getExceptionCount();
        return (long) delta.setValue(count).getDelta();
    }


}
