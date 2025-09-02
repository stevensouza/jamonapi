package com.jamonapi.jmx;

import com.jamonapi.utils.NumberDelta;

import javax.management.ObjectName;

/**
 * Implementation that tracks exceptions deltas/changes between calls of the number of exceptions caught by jamon.
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
