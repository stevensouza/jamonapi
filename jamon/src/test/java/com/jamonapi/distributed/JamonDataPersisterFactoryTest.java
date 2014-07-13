package com.jamonapi.distributed;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class JamonDataPersisterFactoryTest {

//    @Test
//    public void testGet_WithLocal() throws Exception {
//        JamonDataPersisterFactory factory = new JamonDataPersisterFactory();
//        JamonDataPersister jamonData = factory.get();
//        assertThat(jamonData).isInstanceOf(LocalJamonDataPersister.class);
//        System.out.println(jamonData);
//    }

    @Test
    public void testGet_WithHazelCast() throws Exception {
        JamonDataPersister jamonDataPersister = JamonDataPersisterFactory.get();
        assertThat(jamonDataPersister).isInstanceOf(DistributedJamonHazelcastPersister.class);
        System.out.println(jamonDataPersister);
        ((DistributedJamonHazelcastPersister) jamonDataPersister).shutDownHazelCast();
    }
}