package com.jamonapi;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class BasicNanoTimingMonitorTest {

    @Test
    public void testTiming() throws InterruptedException {
        final int NANO=1000*1000;
        BasicNanoTimingMonitor mon=new BasicNanoTimingMonitor();
        mon.start();
        Thread.sleep(10);
        // can't say for sure how long the timer should have bee set for, but should be close to the sleep time.
        assertThat(mon.stop()).isGreaterThan(5*NANO);
    }

}
