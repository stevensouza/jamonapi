package com.jamonapi;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;


public class TimeMon2Test {

    @Test
    public void testTiming() throws InterruptedException {
        TimeMon2 mon=new TimeMon2();
        mon.start();
        Thread.sleep(50);
        mon.stop();
        assertThat(mon.getHits()).isEqualTo(1);
        // can't say for sure how long the timer should have been set for, but should be close to the sleep time.
        assertThat(mon.getTotal()).isGreaterThan(40);
        assertThat(mon.getAvg()).isGreaterThan(40);
        assertThat(mon.getLastValue()).isGreaterThan(40);
        assertThat(mon.getMin()).isGreaterThan(40);
        assertThat(mon.getMax()).isGreaterThan(40);
        assertThat(mon.getUnits()).isEqualTo("ms.");
        assertThat(mon.getActive()).isEqualTo(0);
    }

}
