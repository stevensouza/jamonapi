package com.jamonapi.jmx;

import com.jamonapi.utils.NumberDelta;

import javax.management.ObjectName;

/**
 * MXBean that exposes jamon httpStatus metrics deltas.  It tracks counts in between invocations for the httpStatus
 *  1xx, 2xx, 3xx, 4xx and 5xx
 *
 */
public class HttpStatusDeltaMXBeanImp extends HttpStatusMXBeanImp {
    private NumberDelta status1xx = new NumberDelta();
    private NumberDelta status2xx = new NumberDelta();
    private NumberDelta status3xx = new NumberDelta();
    private NumberDelta status4xx = new NumberDelta();
    private NumberDelta status5xx = new NumberDelta();

    public static ObjectName getObjectName() {
       return JmxUtils.getObjectName(HttpStatusDeltaMXBeanImp.class.getPackage().getName() + ":type=delta,name=HttpStatus");
    }


    @Override
    public long get1xx() {
        long count = super.get1xx();
        return (long) status1xx.setValue(count).getDelta();    }

    @Override
    public long get2xx() {
        long count = super.get2xx();
        return (long) status2xx.setValue(count).getDelta();    }

    @Override
    public long get3xx() {
        long count = super.get3xx();
        return (long) status3xx.setValue(count).getDelta();    }

    @Override
    public long get4xx() {
        long count = super.get4xx();
        return (long) status4xx.setValue(count).getDelta();    }

    @Override
    public long get5xx() {
        long count = super.get5xx();
        return (long) status5xx.setValue(count).getDelta();    }
}
