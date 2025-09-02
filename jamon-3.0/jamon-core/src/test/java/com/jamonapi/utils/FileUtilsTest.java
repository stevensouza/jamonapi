package com.jamonapi.utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class FileUtilsTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

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

    @Test
    public void testMkdirs1() {
        String dir = folder.getRoot()+File.separator+"dir1";
        boolean success = FileUtils.mkdirs(dir);
        assertThat(success).isTrue();
        assertThat(new File(dir)).exists();
    }

    @Test
    public void testMkdirs2() {
        String dir = folder.getRoot()+ File.separator+"dir2"+File.separator+"dir3";
        boolean success = FileUtils.mkdirs(dir);
        assertThat(success).isTrue();
        assertThat(new File(dir)).exists();
    }

    @Test
    public void testNotExistsFile() {
        assertThat(FileUtils.exists("IDoNotExist.txt")).isFalse();
    }

    @Test
    public void testNotExistsDir() {
        assertThat(FileUtils.exists(File.separator+"IDoNotExist"+File.separator+"dir")).isFalse();
    }

    @Test
    public void testExistsDir() {
        String dir = folder.getRoot()+ File.separator+"dir1";
        boolean success = FileUtils.mkdirs(dir);
        assertThat(success).isTrue();
        assertThat(FileUtils.exists(dir)).isTrue();
    }

    @Test
    public void testExistsFile() throws IOException {
        String file = folder.getRoot()+ File.separator+"file.txt";
        folder.newFile("file.txt");
        assertThat(FileUtils.exists(file)).isTrue();
    }

    @Test
    public void testDeleteFile() throws IOException {
        String fileName = folder.getRoot()+ File.separator+"file.txt";
        File file = folder.newFile("file.txt");
        boolean deleted = FileUtils.delete(fileName);
        assertThat(deleted).isTrue();
        assertThat(file.exists()).isFalse();
    }

    @Test
    public void testDeleteDir() throws IOException {
        String fileName = folder.getRoot()+ File.separator+"dir1";
        File file = folder.newFolder("dir1");
        boolean deleted = FileUtils.delete(fileName);
        assertThat(deleted).isTrue();
        assertThat(file.exists()).isFalse();
    }

    @Test
    public void testListFiles() throws IOException {
        folder.newFile("file1.txt");
        folder.newFile("file2.txt");
        folder.newFile("txt.csv");
        System.out.println(folder.getRoot().getCanonicalPath());
        File[] files = FileUtils.listFiles(folder.getRoot().getCanonicalPath(), ".txt");
        assertThat(files.length).isEqualTo(2);
    }

    @Test
    public void testListNoFiles() throws IOException {;
        File[] files = FileUtils.listFiles(folder.getRoot().getCanonicalPath(), ".txt");
        assertThat(files.length).isEqualTo(0);
    }

}
