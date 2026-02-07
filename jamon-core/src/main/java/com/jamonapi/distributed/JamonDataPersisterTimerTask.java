package com.jamonapi.distributed;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Task that starts a thread that will call the JamonDataPersister put method on a timer.  The put method persists jamon data
 * (MonitorComposite which is serializable).  The JamonServletContextListener automatically starts this thread for
 * web applications.
 *
 * Created by stevesouza on 7/7/14.
 */
public class JamonDataPersisterTimerTask extends TimerTask {
    private final JamonDataPersister jamonDataPersister;

    public JamonDataPersisterTimerTask(JamonDataPersister jamonDataPersister) {
        this.jamonDataPersister = jamonDataPersister;
    }

    /** This method saves the jamon data.  Save is used loosely.  It can do anything it wants.  For example the
     * HazelCast implementation sends the data to the HazelCast cluster, but it is not saved to disk.  Other
     * implementations could save to other clustered environments such as Reddis, or Hadoop.  The data saved
     * could replace previously saved data or make additional copies (for example append)
     */
    @Override
    public void run() {
        jamonDataPersister.put();
    }

    /** Start thread that will save jamon data (MonitorComposite).
     *
     * @param refreshRateInMs frequency save should be executed.
     * @return The scheduled timer.
     */
    public Timer schedule(int refreshRateInMs) {
        Timer timer = new Timer(JamonDataPersisterTimerTask.class.getSimpleName()+"-saveJamonData");
        // use refreshRate for 1st value:  when to start, and how long to wait until next one.
        timer.scheduleAtFixedRate(this, refreshRateInMs, refreshRateInMs);
        return timer;
    }
}
