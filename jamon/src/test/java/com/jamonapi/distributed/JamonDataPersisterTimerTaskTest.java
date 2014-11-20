package com.jamonapi.distributed;

import org.junit.Test;

import java.util.Timer;

import static org.mockito.Mockito.*;

public class JamonDataPersisterTimerTaskTest {

    private JamonDataPersister jamonDataPersister = mock(JamonDataPersister.class);

    @Test
    public void testTimer() throws InterruptedException {
        JamonDataPersisterTimerTask task = new JamonDataPersisterTimerTask(jamonDataPersister);
        Timer timer = task.schedule(100);
        Thread.sleep(1000);
        // not sure that it would always be exact so giving the amount of times
        // it is called a range.
        verify(jamonDataPersister, atLeast(8)).put();
        verify(jamonDataPersister, atMost(13)).put();
        timer.cancel();
    }


}
