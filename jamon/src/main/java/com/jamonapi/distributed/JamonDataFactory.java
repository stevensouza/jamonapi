package com.jamonapi.distributed;

import com.jamonapi.utils.Misc;

import java.util.Timer;

/**
 * Created by stevesouza on 7/6/14.
 */
public class JamonDataFactory {

    // CHANGE FROM STATIC !!!!
    private static JamonData jamonData;
    private static Timer timer = new Timer();
    public JamonData get() {
        if (jamonData==null) {
            initialize();
        }

       return jamonData;
    }

    private void initialize() {
        jamonData =  create("com.jamonapi.distributed.DistributedJamonHazelcast");
        if (jamonData==null) {
            jamonData = new LocalJamonData();
            DistributedJamonTimerTask saveTask = new DistributedJamonTimerTask(jamonData);
            timer.scheduleAtFixedRate(saveTask, 1000,60000); // start after 1 second, and every minute from there.
          //  timer.cancel();
        }
    }

    private static JamonData create(String className) {
        try {
            return (JamonData) Class.forName(className).newInstance();
        } catch (Throwable e) {
        }
        return null;
    }
}
