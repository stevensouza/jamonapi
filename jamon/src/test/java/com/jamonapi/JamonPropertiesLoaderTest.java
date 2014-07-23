package com.jamonapi;

import org.junit.Test;

import java.util.Properties;

import static org.fest.assertions.api.Assertions.assertThat;

// also try to test by putting arguments in the command line.  They should take precedence over the config file:
// -DdistributedDataRefreshRateInMinutes=20 -DjamonDataPersister=MYPERSISTER
public class JamonPropertiesLoaderTest {

    @Test
    public void shouldUseDefaults() {
        JamonPropertiesLoader loader = new JamonPropertiesLoader("I_DO_NOT_EXIT.properties");
        Properties props = loader.getJamonProperties();
        assertThat(props.getProperty("distributedDataRefreshRateInMinutes")).isEqualTo("5");
        assertThat(props.getProperty("jamonDataPersister")).isEqualTo("com.jamonapi.distributed.DistributedJamonHazelcastPersister");
        assertThat(props.getProperty("jamonDataPersister.label")).isEqualTo("");
        assertThat(props.getProperty("jamonDataPersister.label.prefix")).isEqualTo("");
    }

    @Test
    public void shouldUseFile() {
        JamonPropertiesLoader loader = new JamonPropertiesLoader("jamonapi2.properties");
        Properties props = loader.getJamonProperties();
        assertThat(props.getProperty("distributedDataRefreshRateInMinutes")).isEqualTo("2");
        assertThat(props.getProperty("jamonDataPersister")).isEqualTo("com.jamonapi.distributed.DistributedJamonHazelcastPersister2");
        assertThat(props.getProperty("jamonDataPersister.label")).isEqualTo("myapplication name");
        assertThat(props.getProperty("jamonDataPersister.label.prefix")).isEqualTo("myprefix:");
    }


    @Test
    public void configDirectory() {
        JamonPropertiesLoader loader = new JamonPropertiesLoader("jamonapi2.properties");
        assertThat(loader.getPropertiesDirectory().toString()).contains("file:/");
    }
}