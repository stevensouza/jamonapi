package com.jamonapi.distributed;

import com.jamonapi.MonitorComposite;
import com.jamonapi.MonitorFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Class that returns the static jamon data for the jvm as returned by MonitorFactory.getRootMonitor()
 *
 */
public class LocalJamonData implements JamonData {
    public static final String INSTANCE = "local";
    private final Map<String, MonitorComposite> jamonData;
    private final Set<String> instances;

    public LocalJamonData() {
        jamonData = new HashMap<String, MonitorComposite>();
        instances = new TreeSet<String>();
        instances.add(INSTANCE);
    }
    @Override
    public Map<String, MonitorComposite> getMap() {
        jamonData.put(INSTANCE, MonitorFactory.getRootMonitor());
        return jamonData;
    }

    @Override
    public Set<String> getInstances() {
        return instances;
    }

    /**
     *  This is a noop. The only key supproted is 'local' and the only data supported is MonitorFactory.getRootMonitor()
     *
     * @param instanceKey key that identifies the jvm instance that the JAMon data is for.
     * @param monitorComposite jamon data @
     */
//    @Override
//    public void put(String instanceKey, MonitorComposite monitorComposite) {
//    }

    /**
     *  This is a noop. The only key supproted is 'local' and the only data supported is MonitorFactory.getRootMonitor()
     *
     * @param monitorComposite
     */
//    @Override
//    public void put(MonitorComposite monitorComposite) {
//
//    }


    /**
     *  This is a noop. The only key supproted is 'local' and the only data supported is MonitorFactory.getRootMonitor()

     */
    @Override
    public void put() {

    }

    @Override
    public MonitorComposite get(String instanceKey) {
        if (INSTANCE.equalsIgnoreCase(instanceKey)) {
            return MonitorFactory.getRootMonitor();
        }

        return null;
    }
}
