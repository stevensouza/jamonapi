package com.jamonapi.distributed;

import com.jamonapi.MonitorComposite;
import com.jamonapi.MonitorFactory;

import java.util.*;

/**
 * Class that returns the static jamon data for the jvm as returned by MonitorFactory.getRootMonitor()
 *
 */
public class LocalJamonDataPersister implements JamonDataPersister {
    public static final String INSTANCE = "local";
    private final Map<String, Date> instances;

    public LocalJamonDataPersister() {
        instances = new HashMap<String, Date>();
        instances.put(INSTANCE, new Date());
    }

    @Override
    public Set<String> getInstances() {
        return new TreeSet<String>(instances.keySet());
    }

    @Override
    public String getInstance() {
        return INSTANCE;
    }

    /**
     *  This is a noop. The only key supproted is 'local' and the only data supported is MonitorFactory.getRootMonitor()
     */
    @Override
    public void put() {
    }

    /**
     *  This is a noop. The only key supported is 'local' and the only data supported is MonitorFactory.getRootMonitor()
     */
    @Override
    public void put(String instanceKey) {

    }

    @Override
    public MonitorComposite get(String instanceKey) {
        if (INSTANCE.equalsIgnoreCase(instanceKey)) {
            return MonitorFactory.getRootMonitor();
        }

        return null;
    }

    @Override
    public void remove(String instanceKey) {
        if (INSTANCE.equalsIgnoreCase(instanceKey)) {
          MonitorFactory.reset();
        }
    }



}
