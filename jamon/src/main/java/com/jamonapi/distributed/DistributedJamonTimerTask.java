package com.jamonapi.distributed;

import java.util.Timer;
import java.util.TimerTask;

/**
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

    public Timer schedule(int refreshRateInMs) {
        Timer timer = new Timer(DistributedJamonTimerTask.class.getSimpleName()+"-saveJamonData");
        // use refreshRate for 1st value:  when to start, and how long to wait until next one.
        timer.scheduleAtFixedRate(this, refreshRateInMs, refreshRateInMs);
        return timer;
    }
}
