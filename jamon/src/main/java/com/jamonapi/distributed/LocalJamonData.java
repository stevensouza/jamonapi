package com.jamonapi.distributed;

import com.jamonapi.MonitorComposite;
import com.jamonapi.MonitorFactory;
import com.jamonapi.utils.Misc;

import java.util.*;

/**
 * Class that returns the static jamon data for the jvm as returned by MonitorFactory.getRootMonitor()
 *
 */
public class LocalJamonData implements JamonData {
    public static final String INSTANCE = "local";
    private final Map<String, Date> instances;

    public LocalJamonData() {
        instances = new HashMap<String, Date>();
        instances.put(INSTANCE, new Date());
    }

    @Override
    public Set<String> getInstances() {
        return instances.keySet();
    }

    /**
     *  This is a noop. The only key supproted is 'local' and the only data supported is MonitorFactory.getRootMonitor()
     */
    @Override
    public void put() {
    }

    @Override
    public MonitorComposite getMonitors(String instanceKey) {
        if (INSTANCE.equalsIgnoreCase(instanceKey)) {
            return MonitorFactory.getRootMonitor();
        }

        return null;
    }

}
