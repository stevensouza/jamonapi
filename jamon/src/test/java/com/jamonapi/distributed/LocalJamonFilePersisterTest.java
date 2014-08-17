package com.jamonapi.distributed;

import com.jamonapi.MonitorComposite;
import com.jamonapi.MonitorFactory;
import com.jamonapi.utils.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;

public class LocalJamonFilePersisterTest {

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();
    private LocalJamonFilePersister persister = new LocalJamonFilePersister() {

        @Override
        protected String getDirectoryName() {
            return tmpFolder.getAbsolutePath()+File.separator;
        }


    };

    private File tmpFolder;

    @Before
    public void setUp() throws Exception {
       tmpFolder = folder.newFolder("jamontestdata");
       for (int i = 0; i<10; i++) {
           MonitorFactory.add("counter-"+i, "count", 1);
       }
    }

    @After
    public void tearDown() throws Exception {
        MonitorFactory.reset();
    }

    @Test
    public void testGetInstance() throws Exception {
        assertThat(persister.getInstance()).isEqualTo("local");
    }

    @Test
    public void testGetInstances() throws Exception {
        Set<String> instances = persister.getInstances();
        assertThat(instances).containsOnly("local");
    }

    @Test
    public void testGetInstancesWithFiles() throws Exception {
        persister.put();
        persister.put("deleteme");
        Set<String> instances = persister.getInstances();
        assertThat(instances).containsOnly("local", LocalJamonFilePersister.JAMON_FILE_NAME, "deleteme");
    }

    @Test
    public void testGetLocal() throws Exception {
        MonitorComposite expected = MonitorFactory.getRootMonitor();
        MonitorComposite local = persister.get("local");
        assertThat(local.getReport()).isEqualTo(expected.getReport());
    }

    @Test
    public void testGetNoExist() throws Exception {
        MonitorComposite local = persister.get("i_do_not_exist");
        assertThat(local).isNull();
    }

    @Test
    public void testPut() throws Exception {
        persister.put();
        MonitorComposite expected = MonitorFactory.getRootMonitor();
        MonitorComposite fileData = persister.get(LocalJamonFilePersister.JAMON_FILE_NAME);
        assertThat(fileData.getInstanceName()).isEqualTo(LocalJamonFilePersister.JAMON_FILE_NAME);
        assertThat(fileData.getReport()).isEqualTo(expected.getReport());
    }

    @Test
    public void testRemoveLocal() throws Exception {
        persister.remove("local");
        assertThat(MonitorFactory.getNumRows()).isEqualTo(0);
    }

    @Test
    public void testRemoveSavedFile() throws Exception {
        persister.put();
        persister.remove(LocalJamonFilePersister.JAMON_FILE_NAME);
        File[] files = FileUtils.listFiles(tmpFolder.getAbsolutePath().toString(), "ser");
        assertThat(files.length).isEqualTo(0);
    }

}