package com.jamonapi.distributed;

import java.util.Date;
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
        System.out.println("saving jamon data now: "+new Date());
    }
}
