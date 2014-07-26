package com.jamonapi.distributed;

import com.jamonapi.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;

public class HazelcastMapStoreTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();


    @Test
    public void testReplaceFileExtenstion() throws Exception {
        File[] files = new File[2];
        files[0] = folder.newFile("file1.ser");
        files[1] = folder.newFile("file2.ser");
        Set<String> fileSet = HazelcastMapStore.removeFileExtenstion(files);
        assertThat(fileSet).hasSize(2);
        assertThat(fileSet).doesNotContain("file1.ser", "file2.ser");
        assertThat(fileSet).contains("file1", "file2");
    }


    /** persist monitors in map to file system and read them to make sure they are as expected */
    @Test
    public void testPersist() {
        HazelcastMapStore mapStore = new HazelcastMapStore() {
            // override to put files in temp file directory.
            protected String getDirectoryName() {
                return folder.getRoot()+File.separator;
            }
        };

        Map<String, Serializable> map = getJamonDataMap();
        // save jamon data to file system
        mapStore.storeAll(map);
        // ensure keys can be read from file system
        assertThat(mapStore.loadAllKeys()).containsOnly("jvm1", "jvm2");

        // load map containing jamon data from file system
        map = mapStore.loadAll(mapStore.loadAllKeys());
        assertThat(map.keySet()).containsOnly("jvm1", "jvm2");

        // ensure the jamon data read from file system matches expected values.
        MonitorComposite mc = (MonitorComposite) map.get("jvm1");
        assertThat(mc.getNumRows()).isEqualTo(2);
        assertThat(mc.exists(new MonKeyImp("helloworld1", "ms."))).isTrue();
        assertThat(mc.exists(new MonKeyImp("helloworld2", "ms."))).isTrue();

        mc = (MonitorComposite) map.get("jvm2");
        assertThat(mc.getNumRows()).isEqualTo(3);
        assertThat(mc.exists(new MonKeyImp("goodbyeworld1", "ms."))).isTrue();
        assertThat(mc.exists(new MonKeyImp("goodbyeworld2", "ms."))).isTrue();
        assertThat(mc.exists(new MonKeyImp("goodbyeworld3", "ms."))).isTrue();

        mapStore.delete("jvm1");
        assertThat(mapStore.loadAllKeys()).containsOnly("jvm2");
    }

    // mimic populating a map  with jamon data from 2 different jvm instances.
    private Map<String, Serializable> getJamonDataMap() {
        Map<String, Serializable> map = new HashMap<String, Serializable>();
        FactoryEnabled factory = new FactoryEnabled();
        factory.start("helloworld1").stop();
        factory.start("helloworld2").stop();
        map.put("jvm1", factory.getRootMonitor());

        factory = new FactoryEnabled();
        factory.start("goodbyeworld1").stop();
        factory.start("goodbyeworld2").stop();
        factory.start("goodbyeworld3").stop();
        map.put("jvm2", factory.getRootMonitor());
        return map;
    }

}