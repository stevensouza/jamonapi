
package com.jamonapi;


/**
 * <p>Monitor that tracks execution time in nanoseconds. There are 1,000,000 nanoseconds in a millisecond </p>
 *
 * <p>Note due to the fact that when start is called it resets the startTime instance
 * variable and different threads can call start() before one of the threads calls
 * stop this object when used BY ITSELF would not be thread safe.  However, when
 * not reused i.e. when TimeMon's are always taken from MonitorFactory it is threadsafe.</p>
 *
 * <p>I didn't attempt to make this thread safe as even if it was having two threads
 * subsequently call start, start before a stop would reset the startTime and so
 * make one of the callers time off.</p>
 *
 * <p>Note this class is a thin wrapper that adds time capabilities to the basic Monitor
 * class </p>
 */

class TimeMonNano extends TimeMon {
    private static final long NANOSECS_PER_MILLISEC=1000000;
    private static final long serialVersionUID = 278L;

    public TimeMonNano(MonKey key, MonInternals monData) {
        super(key, monData);
    }

    @Override
    public Monitor start() {
        super.start();
        startTime=System.nanoTime();

        return this;
    }

    @Override
    public Monitor stop() {
        long endTime=System.nanoTime();
        setAccessStats(endTime/NANOSECS_PER_MILLISEC);// saves some cycles by not recalculating time for time monitors
        add(endTime-startTime);// accrue status
        super.stop();// decrement active.
        return this;
    }

}
