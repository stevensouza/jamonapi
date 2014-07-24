package com.jamonapi.utils;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.fest.assertions.api.Assertions.assertThat;

public class FileUtilsTest {

    @Test
    public void testMakeValidFileName() throws Exception {
        String fileName = FileUtils.makeValidFileName("himom.txt");
        assertThat(fileName).isEqualTo("himom.txt");

        fileName = FileUtils.makeValidFileName("himom  .txt");
        assertThat(fileName).isEqualTo("himom--.txt");

        fileName = FileUtils.makeValidFileName("himom7__  .txt");
        assertThat(fileName).isEqualTo("himom7__--.txt");

        fileName = FileUtils.makeValidFileName("himom7__  a[](*&^%$%^.txt.bak(");
        assertThat(fileName).isEqualTo("himom7__--a----------.txt.bak-");

        fileName = FileUtils.makeValidFileName("jetty-Member [172.16.16.109]:5701 this");
        assertThat(fileName).isEqualTo("jetty-Member--172.16.16.109--5701-this");
    }
}