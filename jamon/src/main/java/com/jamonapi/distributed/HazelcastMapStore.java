package com.jamonapi.distributed;

import com.hazelcast.core.MapStore;
import com.jamonapi.JamonPropertiesLoader;
import com.jamonapi.utils.FileUtils;
import com.jamonapi.utils.SerializationUtils;

import java.io.*;
import java.util.*;

/**
 * HazelCast class that you implement to persist your map data.  This is used to persist jamon MonitorComposite data
 * as well as the instances map.  This allows the jamon data to be viewable in between boots.
 *
 * @deprecated This class currently won't save the map properly.
 * Created by stevesouza on 7/23/14.
 */
public class HazelcastMapStore implements MapStore<String, Serializable> {

    private static final String FILE_EXT = ".ser";

    private String mapName="jamonapi";
    private JamonPropertiesLoader jamonPropertiesLoader = new JamonPropertiesLoader();


    public HazelcastMapStore() {

    }

    public HazelcastMapStore(String mapName) {
        this.mapName = mapName;
    }

    /** Saves the key and value.  For example serverName, and MonitorComposite for jamon
     *
     * @param key
     * @param value
     */
    @Override
    public void store(String key, Serializable value) {
        try {
            createDirectory();
            OutputStream outputStream = FileUtils.getOutputStream(getFileName(key));
            SerializationUtils.serialize(value, outputStream);
        } catch (IOException e) {
            throw new RuntimeException("HazelCast exception while trying to save jamondata", e);
        }
    }

    /** Store all of the data in the map using the key as a file name and the value as the file contents.  Each row in
     * the map is a different file
     *
     * @param map
     */
    @Override
    public void storeAll(Map<String, Serializable> map) {
        for (Map.Entry<String, Serializable> entry : map.entrySet()) {
            store(entry.getKey(), entry.getValue());
        }
    }

    /** Delete the file with the name of the passed in key
     *
     * @param key fileName
     */
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


    /** Read the fileName associated with the key into a HashMap
     *
     * @param key fileName
     * @return
     */
    @Override
    public Serializable load(String key) {
        try {
            String fileName = getFileName(key);
            if (FileUtils.exists(fileName)) {
                InputStream inputStream = FileUtils.getInputStream(fileName);
                Serializable serializable = SerializationUtils.deserialize(inputStream);
                return serializable;
            } else {
                return null;
            }
        } catch (Throwable e) {
            throw new RuntimeException("HazelCast exception while trying to load jamondata", e);
        }
    }

    /** Take a list of files and load them all into a Map with each key being a different file (for example
     * MonitorComposite data for each server)
     *
     * @param keys
     * @return
     */
    @Override
    public Map<String, Serializable> loadAll(Collection<String> keys) {
        Map<String, Serializable> map = new HashMap<String, Serializable>();
        for (String fileName : keys) {
            map.put(fileName, load(fileName));
        }
        return map;
    }

    /** Read file names from a directory
     *
     * @return fileNames
     */
    @Override
    public Set<String> loadAllKeys() {
        File[] files = FileUtils.listFiles(getDirectoryName(), FILE_EXT);
        if(files == null || files.length == 0) {
            return new HashSet<String>();
        }
        Set<String> keys = removeFileExtenstion(files);
        return keys;
    }

    /**
     * Create the directory where jamon data will be saved (if it already exists this is a noop)
     */
    private void createDirectory() {
        String dirName = getDirectoryName();
        if (!FileUtils.exists(dirName)) {
          FileUtils.mkdirs(dirName);
        }
    }

    /**
     *
     * @return  Directory where jamon data is stored
     */
    protected String getDirectoryName() {
        String rootDir  = jamonPropertiesLoader.getJamonProperties().getProperty("jamonDataPersister.directory");
        return rootDir+File.separator+mapName+File.separator;
    }

    /**
     *
     * @param key
     * @return Take the key and turn it into a file name
     */
    protected String getFileName(String key) {
      return getDirectoryName()+key+".ser";
    }

    /**
     *
     * @param files
     * @return Remove the file extenstion from each file in the array.
     */
    static Set<String> removeFileExtenstion(File[] files) {
        Set<String> keys = new HashSet<String>();
        for (File file : files) {
           keys.add(file.getName().replace(FILE_EXT, ""));
        }
        return keys;
    }
}
