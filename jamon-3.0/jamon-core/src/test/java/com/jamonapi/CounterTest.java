package com.jamonapi;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CounterTest {

    @Test
    public void testSetCount() {
        Counter counter=new Counter();

        assertThat(counter.getCount()).isEqualTo(0);

        counter.setCount(150);
        assertThat(counter.getCount()).isEqualTo(150);

        counter.setCount(-150);
        assertThat(counter.getCount()).isEqualTo(-150);
    }

    @Test
    public void testIncrementDecrement() {
        Counter counter=new Counter();

        counter.decrement();
        assertThat(counter.getCount()).isEqualTo(-1);

        counter.increment();
        assertThat(counter.getCount()).isEqualTo(0);

        counter.increment();
        assertThat(counter.getCount()).isEqualTo(1);

        assertThat(counter.incrementAndReturn()).isEqualTo(2);
    }


    @Test
    public void testEnableDisable() {
        Counter counter=new Counter();

        counter.enable(false);

        counter.increment();
        assertThat(counter.getCount()).isEqualTo(0);

        counter.decrement();
        counter.decrement();
        counter.decrement();
        assertThat(counter.getCount()).isEqualTo(0);

        counter.setCount(100);
        assertThat(counter.getCount()).isEqualTo(0);

        counter.enable(true);
        assertThat(counter.incrementAndReturn()).isEqualTo(1);
        counter.decrement();
        assertThat(counter.getCount()).isEqualTo(0);
    }

}
