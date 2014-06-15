package com.jamonapi;

/** The most basic of timing Monitors using nanosecond timing.
 * 
 * <p>Sample call:
 *  <pre>{@code
 *  BasicTimingMonitor mon=new BasicNanoTimingMonitor();
 *  mon.start();
 *  ...code being monitored...
 *  mon.stop();
 *  }</pre>
 *  </p>
 **/

final public class BasicNanoTimingMonitor {
    // Note. this class does not implement the Monitor interface and is not used in the
    // rest of the monitor framework. However it can be used if performance comparable
    // to simple times to currentTimeMillis() are required.
    private long startTime;

    public void start() {
        startTime=System.nanoTime();
    }

    public long stop() {
        return System.nanoTime()-startTime;
    }
}

