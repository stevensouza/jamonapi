package com.jamonapi.distributed;

import com.jamonapi.MonitorComposite;

import java.util.Set;

/**
 * Interface that supports saving different jamon data sets. The key should be
 * a uniquely identifiable jvm instance name (i.e. server) that has meaning to you
 * (for example: hostname:9809 or 111.222.333.4444:9878, myapp_jetty_production).
 * Because each host can have multiple instance names so make  sure each jvm is unique.
 * The value saved is JAMon's MonitorComposite data (i.e. MonitorFactory.getRootMonitor())
 */
public interface JamonDataPersister {

    /**
     *
     * @return Return a list of all the jvm server instances being tracked.
     */
    public Set<String> getInstances();

    /** get this instance name.  i.e. the key that identifies this host, application or jvm */
    public String getInstance();

    /**
     * Puts the default data (MonitorFactory.getRootMonitor()) in with the default key (For example 'local')
     */
    public void put();

    /**
     * Puts the default data (MonitorFactory.getRootMonitor()) in with the specified key (For example 'jetty-local')
     */
    public void put(String instanceKey);

    /**
     *
     * @param instanceKey identifier of jvm server instance being monitored
     * @return jamon data for the server
     */
    public MonitorComposite get(String instanceKey);

    /**
     * Remove the monitoring data.  Depending on the implementation this could remove the data from memory or a
     * data store.
     *
     * @param instanceKey
     */
    public void remove(String instanceKey);

}
