package com.jamonapi.distributed;

import com.apple.eio.FileManager;
import com.hazelcast.core.MapStore;
import com.jamonapi.JamonPropertiesLoader;
import com.jamonapi.MonitorComposite;
import com.jamonapi.utils.FileUtils;
import com.jamonapi.utils.SerializationUtils;

import java.io.*;
import java.util.*;

/**
 * HazelCast class that you implement to persist your map data.  This is used to persist jamon MonitorComposite data
 * as well as the instances map.  This allows the jamon data to be viewable in between boots.
 *
 * Created by stevesouza on 7/23/14.
 */
public class HazelcastMapStore implements MapStore<String, Serializable> {

    private static final String FILE_EXT = ".ser";

    private String mapName="jamonapi";
    private JamonPropertiesLoader jamonPropertiesLoader = new JamonPropertiesLoader();


    public HazelcastMapStore() {

    }
    public HazelcastMapStore(String mapName) {
        System.out.println("mapName="+mapName);
        this.mapName = mapName;
    }

    @Override
    public void store(String key, Serializable value) {
        try {
            createDirectory();
            OutputStream outputStream = FileUtils.getOutputStream(getFileName(key));
            SerializationUtils.serialize(value, outputStream);
            System.out.println("mapName="+mapName+" store");
        } catch (IOException e) {
            throw new RuntimeException("HazelCast exception while trying to save jamondata", e);
        }
    }

    @Override
    public void storeAll(Map<String, Serializable> map) {
        for(Map.Entry<String, Serializable> entry : map.entrySet()) {
            store(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void delete(String key) {
        FileUtils.delete(getFileName(key));
    }

    @Override
    public void deleteAll(Collection<String> keys) {
       for (String key : keys) {
           delete(key);
       }
    }

    @Override
    public Serializable load(String key) {
        try {
            InputStream inputStream = FileUtils.getInputStream(getFileName(key));
            Serializable serializable = SerializationUtils.deserialize(inputStream);
            System.out.println("mapName="+mapName+" load: "+serializable);

            return serializable;
        } catch (Throwable e) {
            throw new RuntimeException("HazelCast exception while trying to load jamondata", e);
        }
    }

    @Override
    public Map<String, Serializable> loadAll(Collection<String> keys) {
        Map<String, Serializable> map = new HashMap<String, Serializable>();
        for (String fileName : keys) {
            map.put(fileName, load(fileName));
        }
        return map;
    }

    @Override
    public Set<String> loadAllKeys() {
        File[] files = FileUtils.listFiles(getDirectoryName(), FILE_EXT);
        if(files == null || files.length == 0) {
            return new HashSet<String>();
        }
        Set<String> keys = replaceFileExtenstion(files);
        System.out.println("mapName="+mapName+" keys: "+keys);
        return keys;
    }

    private void createDirectory() {
        String dirName = getDirectoryName();
        if (!FileUtils.exists(dirName)) {
          FileUtils.mkdirs(dirName);
        }
    }

    protected String getDirectoryName() {
        String rootDir  = jamonPropertiesLoader.getJamonProperties().getProperty("jamonDataPersister.directory");
        return rootDir+File.separator+mapName+File.separator;
    }

    protected String getFileName(String key) {
      return getDirectoryName()+key+".ser";
    }

    static Set<String> replaceFileExtenstion(File[] files) {
        Set<String> keys = new HashSet<String>();
        for (File file : files) {
           keys.add(file.getName().replace(FILE_EXT, ""));
        }
        return keys;
    }
}
