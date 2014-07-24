package com.jamonapi.distributed;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Set;

import static org.junit.Assert.*;
import static org.fest.assertions.api.Assertions.assertThat;

public class HazelcastMapStoreTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testReplaceFileExtenstion() throws Exception {
        File[] files = new File[2];
        files[0] = folder.newFile("file1.ser");
        files[1] = folder.newFile("file2.ser");
        Set<String> fileSet = HazelcastMapStore.replaceFileExtenstion(files);
        assertThat(fileSet).hasSize(2);
        assertThat(fileSet).doesNotContain("file1.ser", "file2.ser");
        assertThat(fileSet).contains("file1", "file2");
    }
}