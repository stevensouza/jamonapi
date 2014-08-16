package com.jamonapi.distributed;

import com.jamonapi.JamonPropertiesLoader;
import com.jamonapi.Mon;
import com.jamonapi.MonitorComposite;
import com.jamonapi.MonitorFactory;
import com.jamonapi.utils.FileUtils;
import com.jamonapi.utils.SerializationUtils;

import java.io.*;
import java.util.*;

/** Persist/serialize jamon data (MonitorComposite) to a local file.
 *
 * Created by stevesouza on 8/10/14.
 */
public class LocalJamonFilePersister extends LocalJamonDataPersister {
    private static final String FILE_EXT = ".ser";
    private JamonPropertiesLoader jamonPropertiesLoader = new JamonPropertiesLoader();
    private Map<String, String> fileNameMap=new HashMap<String, String>();
    static final String JAMON_FILE_NAME = INSTANCE+"-saved";

    /** Get instances by looking in directory for any saved files and also add local in memory instance */
    @Override
    public Set<String> getInstances() {
        Set<String> keys = new TreeSet<String>();
        keys.addAll(super.getInstances());
        File[] files = FileUtils.listFiles(getDirectoryName(), FILE_EXT);
        if(files == null || files.length == 0) {
            return keys;
        }
        keys.addAll(removeFileExtenstion(files));
        return keys;
    }

    @Override
    public void put() {
        put(JAMON_FILE_NAME);
    }

    @Override
    public void put(String instanceKey) {
        try {
            createDirectory();
            OutputStream outputStream = FileUtils.getOutputStream(getFileName(instanceKey));
            MonitorComposite monitorComposite = MonitorFactory.getRootMonitor().setInstanceName(instanceKey);
            SerializationUtils.serialize(monitorComposite, outputStream);
        } catch (IOException e) {
            throw new RuntimeException("Exception while trying to save jamondata", e);
        }
    }

    @Override
    public MonitorComposite get(String instanceKey) {
        try {
            MonitorComposite monitorComposite = super.get(instanceKey);
            if (monitorComposite!=null) {
                return monitorComposite;
            }

            String fileName = getFileName(instanceKey);
            if (FileUtils.exists(fileName)) {
                InputStream inputStream = FileUtils.getInputStream(fileName);
                monitorComposite = SerializationUtils.deserialize(inputStream);
                return monitorComposite;
            } else {
                return null;
            }
        } catch (Throwable e) {
            throw new RuntimeException("Exception while trying to load jamondata", e);
        }
    }

    @Override
    public void remove(String instanceKey) {
        super.remove(instanceKey);
        String fileName = getFileName(instanceKey);
        if (FileUtils.exists(fileName)) {
            FileUtils.delete(fileName);
        }
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
        if (rootDir.endsWith(File.separator)) {
          return rootDir;
        }
        return rootDir+File.separator;
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
        Set<String> keys = new TreeSet<String>();
        for (File file : files) {
            keys.add(file.getName().replace(FILE_EXT, ""));
        }
        return keys;
    }
}
