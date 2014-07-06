package com.jamonapi.distributed;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.fest.assertions.api.Assertions.assertThat;

public class JamonDataFactoryTest {

//    @Test
//    public void testGet_WithLocal() throws Exception {
//        JamonDataFactory factory = new JamonDataFactory();
//        JamonData jamonData = factory.get();
//        assertThat(jamonData).isInstanceOf(LocalJamonData.class);
//        System.out.println(jamonData);
//    }

    @Test
    public void testGet_WithHazelCast() throws Exception {
        JamonDataFactory factory = new JamonDataFactory();
        JamonData jamonData = factory.get();
        assertThat(jamonData).isInstanceOf(DistributedJamonHazelcast.class);
        System.out.println(jamonData);
        ((DistributedJamonHazelcast)jamonData).shutDownHazelCast();
    }
}