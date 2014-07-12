package com.jamonapi.distributed;

import org.junit.Test;

import java.util.Timer;

import static org.mockito.Mockito.*;

public class DistributedJamonTimerTaskTest {

    private JamonData jamonData = mock(JamonData.class);

    @Test
    public void testTimer() throws InterruptedException {
        DistributedJamonTimerTask task = new DistributedJamonTimerTask(jamonData);
        Timer timer = task.schedule(100);
        Thread.sleep(1000);
        // not sure that it would always be exact so giving the amount of times
        // it is called a range.
        verify(jamonData, atLeast(8)).put();
        verify(jamonData, atMost(13)).put();
        timer.cancel();
    }


}