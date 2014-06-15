package com.jamonapi;

/** The most basic of timing Monitors.  It is very close in performance to making straight calls to System.currentTimeMillis().
 *  This class is used primarily for comparison to the best case performance numbers for all of the other Monitors,
 *  however it may also be used to make timing measurements.
 *
 *  <p>BasicTimingMonitors are not thread safe.  Monitors returned via MonitorFactory are.</p>
 *
 * <p>Sample call:</p>
 * <pre>{@code
 *  BasicTimingMonitor mon=new BasicTimingMonitor();
 *  mon.start();
 *  ...code being monitored...
 *  mon.stop();
 *  }</pre>
 **/

final public class BasicTimingMonitor {
    // Note. this class does not implement the Monitor interface and is not used in the
    // rest of the monitor framework. However it can be used if performance comparable
    // to simple times to currentTimeMillis() are required.
    private long startTime;

    public void start() {
        startTime=System.currentTimeMillis();
    }

    public long stop() {
        return System.currentTimeMillis()-startTime;
    }
}

