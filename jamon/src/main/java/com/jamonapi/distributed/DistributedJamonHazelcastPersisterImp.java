package com.jamonapi.distributed;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorComposite;
import com.jamonapi.MonitorFactory;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**  Class that interacts with HazelCast to save jamon data to it so data from any jvms in the hazelcast cluster
 * can be visible via the jamon web app.  Note in must cases hazelcast exceptions are not bubbled up in this class
 * as I would still like jamon to be availalbe even if HazelCast has issues.  The exceptions and stack traces can be
 * seen in jamon however.
 *
 * Created by stevesouza on 7/6/14.
 */

public class DistributedJamonHazelcastPersisterImp implements JamonDataPersister {

    // could be Map if we don't want the instance methods of hazelcast
    private IMap<String, MonitorComposite> jamonDataMap;
    // This should really be an ISet, but ISet doesn't support time-to-live methods
    private IMap<String, Date> instances;
    private HazelcastInstance hazelCast;
    private LocalJamonDataPersister localJamonData = new LocalJamonDataPersister();

    public DistributedJamonHazelcastPersisterImp() {
        hazelCast = Hazelcast.newHazelcastInstance();
    }

    public DistributedJamonHazelcastPersisterImp(HazelcastInstance hazelCast) {
        this.hazelCast = hazelCast;
    }

    @Override
    public Set<String> getInstances() {
      // I don't ever want to not display data when there is a hazelcast error.
      intitialize();
      return instances.keySet();
    }

    @Override
    /** Put jamon data into the hazelcast map */
    public void put() {
      intitialize();
      String key = getInstance();
      jamonDataMap.set(key, MonitorFactory.getRootMonitor().setInstanceName(key));
      instances.set(key, new Date());
    }


    @Override
    public MonitorComposite get(String key) {
       intitialize();
       // done purely to ensure instance and data live the same amount of time.
       instances.get(key);
       return  jamonDataMap.get(key);
    }

    @Override
    public void remove(String instanceKey) {
       intitialize();
       instances.remove(instanceKey);
       jamonDataMap.remove(instanceKey);
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
            instances = hazelCast.getMap("com.jamonapi.instances");
        }
    }

}
