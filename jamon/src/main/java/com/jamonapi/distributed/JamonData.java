package com.jamonapi.distributed;

import com.jamonapi.MonitorComposite;

import java.util.Map;
import java.util.Set;

/**
 * Interface that supports saving different jamon data sets in a clustered environment. The key should be
 * a uniquely identifiable jvm instance name (i.e. server) that has meaning to you
 * (for example: hostname:9809 or 111.222.333.4444:9878).  Because each host can have multiple instance names so make
 * sure each jvm is unique.  The value should be JAMon MonitorComposite data (i.e. MonitorFactory.getRootMoitor())
 */
public interface JamonData {

    /**
     *
     * @return Return a map that has the server instances being monitored as the key and the jamon
     * data as the value.
     */
    public Map<String, MonitorComposite> getMap();

    /**
     *
     * @return Return a list of all the jvm server instances being tracked.
     */
    public Set<String> getInstances();


    /**
     *
     * @param instanceKey key that identifies the jvm instance that the JAMon data is for.
     * @param monitorComposite jamon data @
     */
    public void put(String instanceKey, MonitorComposite monitorComposite);

    /**
     *
     * @param instanceKey identifier of jvm server instance being monitored
     * @return jamon data for the server
     */
    public MonitorComposite get(String instanceKey);

}
