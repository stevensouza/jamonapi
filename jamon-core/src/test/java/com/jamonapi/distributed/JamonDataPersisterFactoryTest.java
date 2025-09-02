package com.jamonapi.distributed;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JamonDataPersisterFactoryTest {

    @Test
    public void testGet_WithHazelCast() throws Exception {
        JamonDataPersister jamonDataPersister = JamonDataPersisterFactory.get();
        assertThat(jamonDataPersister).isInstanceOf(HazelcastPersister.class);
        ((HazelcastPersister) jamonDataPersister).shutDownHazelCast();
    }
}
