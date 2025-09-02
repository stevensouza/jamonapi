package com.jamonapi.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NumberDeltaTest {

    private NumberDelta delta=new NumberDelta();
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testDefaultDelta() throws Exception {
        assertThat(delta.getDelta()).isEqualTo(0);
    }

    @Test
    public void testWithValue1() throws Exception {
        delta.setValue(1);
        assertThat(delta.getDelta()).isEqualTo(1);
    }

    @Test
    public void testWith2Numbers() throws Exception {
        delta.setValue(1).setValue(101);
        assertThat(delta.getDelta()).isEqualTo(100);
    }

    @Test
    public void testWith3Numbers() throws Exception {
        delta.setValue(1);
        delta.setValue(101);
        delta.setValue(1001);
        assertThat(delta.getDelta()).isEqualTo(900);
    }
    @Test
    public void testWithNegative() throws Exception {
        delta.setValue(-100);
        delta.setValue(100);
        assertThat(delta.getDelta()).isEqualTo(200);
    }

    @Test
    public void testWithNegative2() throws Exception {
        delta.setValue(100);
        delta.setValue(-100);
        assertThat(delta.getDelta()).isEqualTo(-200);
    }
}
