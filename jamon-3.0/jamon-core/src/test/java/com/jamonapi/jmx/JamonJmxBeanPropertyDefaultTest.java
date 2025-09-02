package com.jamonapi.jmx;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JamonJmxBeanPropertyDefaultTest {

    @Test
    public void testInstanceProperlyInitialized() throws Exception {
        JamonJmxBeanProperty property = new JamonJmxBeanPropertyDefault("label", "units", "name");
        assertThat(property.getLabel()).isEqualTo("label");
        assertThat(property.getUnits()).isEqualTo("units");
        assertThat(property.getName()).isEqualTo("name");
    }
}