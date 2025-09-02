package com.jamonapi.jmx;

import com.jamonapi.utils.NumberDelta;

import javax.management.ObjectName;

/**
 * MXBean that exposes jamon log4j metrics deltas.  It tracks counts in between invocations for the log4j log
 * levels such as DEBUG, WARN,FATAL, ERROR,...
 *
 */
public class Log4jDeltaMXBeanImp extends Log4jMXBeanImp {
    private NumberDelta traceDelta = new NumberDelta();
    private NumberDelta debugDelta = new NumberDelta();
    private NumberDelta warnDelta = new NumberDelta();
    private NumberDelta infoDelta = new NumberDelta();
    private NumberDelta errorDelta = new NumberDelta();
    private NumberDelta fatalDelta = new NumberDelta();
    private NumberDelta totalDelta = new NumberDelta();

    public static ObjectName getObjectName() {
       return JmxUtils.getObjectName(Log4jDeltaMXBeanImp.class.getPackage().getName() + ":type=delta,name=Log4j");
    }

    @Override
    public long getTrace() {
        long count = super.getTrace();
        return (long) traceDelta.setValue(count).getDelta();
    }

    @Override
    public long getDebug() {
        long count = super.getDebug();
        return (long) debugDelta.setValue(count).getDelta();
    }

    @Override
    public long getWarn() {
        long count = super.getWarn();
        return (long) warnDelta.setValue(count).getDelta();
    }

    @Override
    public long getInfo() {
        long count = super.getInfo();
        return (long) infoDelta.setValue(count).getDelta();
    }

    @Override
    public long getError() {
        long count = super.getError();
        return (long) errorDelta.setValue(count).getDelta();
    }

    @Override
    public long getFatal() {
        long count = super.getFatal();
        return (long) fatalDelta.setValue(count).getDelta();
    }

    @Override
    public long getTotal() {
        long count = super.getTotal();
        return (long) totalDelta.setValue(count).getDelta();
    }

}
