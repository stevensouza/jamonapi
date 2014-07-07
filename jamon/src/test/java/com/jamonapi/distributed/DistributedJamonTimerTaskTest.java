package com.jamonapi.distributed;

import org.junit.Test;

import java.util.Timer;

import static org.junit.Assert.*;

public class DistributedJamonTimerTaskTest {

    @Test
    public void testTimer() throws InterruptedException {
        DistributedJamonTimerTask task = new DistributedJamonTimerTask(new LocalJamonData());
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 500);
        Thread.sleep(5000);
      //  timer.cancel();
    }

    @Test
    public void testTimer2() throws InterruptedException {
        System.out.println("in new method");
        Thread.sleep(5000);

    }
    }