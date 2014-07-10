package com.jamonapi.distributed;

import org.junit.Test;

import java.util.Timer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DistributedJamonTimerTaskTest {

    private JamonData jamonData = mock(JamonData.class);

    @Test
    public void testTimer() throws InterruptedException {
        DistributedJamonTimerTask task = new DistributedJamonTimerTask(jamonData);
        task.run();
        verify(jamonData).put();
    }


}