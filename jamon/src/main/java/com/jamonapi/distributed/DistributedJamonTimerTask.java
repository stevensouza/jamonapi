package com.jamonapi.distributed;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Task that starts a thread that will call the JamonData put method on a timer.  The put method persists jamon data
 * (MonitorComposite which is serializable).  The JamonServletContextListener automatically starts this thread for
 * web applications.
 *
 * Created by stevesouza on 7/7/14.
 */
public class DistributedJamonTimerTask extends TimerTask {
    private final JamonData jamonData;

    public DistributedJamonTimerTask(JamonData jamonData) {
        this.jamonData = jamonData;
    }
    @Override
    public void run() {
        jamonData.put();
    }

    /** Start thread that will save jamon data (MonitorComposite). 
     *
     * @param refreshRateInMs frequency save should be executed.
     * @return The scheduled timer.
     */
    public Timer schedule(int refreshRateInMs) {
        Timer timer = new Timer(DistributedJamonTimerTask.class.getSimpleName()+"-saveJamonData");
        // use refreshRate for 1st value:  when to start, and how long to wait until next one.
        timer.scheduleAtFixedRate(this, refreshRateInMs, refreshRateInMs);
        return timer;
    }
}
