package com.jamonapi.distributed;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.jamonapi.MonitorComposite;
import com.jamonapi.MonitorFactory;

import java.util.Set;
import java.util.TreeSet;

/**  Class that interacts with HazelCast to save jamon data to it so data from any jvms in the hazelcast cluster
 * can be visible via the jamon web app.  Note in must cases hazelcast exceptions are not bubbled up in this class
 * as I would still like jamon to be availalbe even if HazelCast has issues.  The exceptions and stack traces can be
 * seen in jamon however.
 *
 * Created by stevesouza on 7/6/14.
 */

public class HazelcastPersisterImp implements JamonDataPersister {

    // could be Map if we don't want the instance methods of hazelcast such as delete, and set
    private IMap<String, MonitorComposite> jamonDataMap;
    private HazelcastInstance hazelCast;

    public HazelcastPersisterImp() {
        hazelCast = Hazelcast.newHazelcastInstance();
    }

    public HazelcastPersisterImp(HazelcastInstance hazelCast) {
        this.hazelCast = hazelCast;
    }

    @Override
    public Set<String> getInstances() {
      intitialize();
      return new TreeSet<String>(jamonDataMap.keySet());
    }

    @Override
    /** Put jamon data into the hazelcast map */
    public void put() {
      put(getInstance());
    }

    @Override
    public void put(String key) {
        intitialize();
        jamonDataMap.set(key, MonitorFactory.getRootMonitor().setInstanceName(key));
    }

    @Override
    public MonitorComposite get(String key) {
       intitialize();
       return  jamonDataMap.get(key);
    }

    @Override
    public void remove(String instanceKey) {
       intitialize();
       jamonDataMap.delete(instanceKey);
    }

    @Override
    public String getInstance() {
        intitialize();
        return hazelCast.getCluster().getLocalMember().toString();
    }

    public void shutDownHazelCast() {
        intitialize();
        hazelCast.shutdown();
    }

    private void intitialize() {
        if (jamonDataMap == null) {
            jamonDataMap = hazelCast.getMap(MonitorComposite.class.getCanonicalName());
        }
    }

}
