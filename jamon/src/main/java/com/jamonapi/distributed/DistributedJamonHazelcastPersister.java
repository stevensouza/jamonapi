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

public class DistributedJamonHazelcastPersister implements JamonDataPersister {

    // could be Map if we don't want the instance methods of hazelcast
    private IMap<String, MonitorComposite> jamonDataMap;
    // This should really be an ISet, but ISet doesn't support time-to-live methods
    private IMap<String, Date> instances;
    private HazelcastInstance hazelCast;
    private LocalJamonDataPersister localJamonData = new LocalJamonDataPersister();

    public DistributedJamonHazelcastPersister() {
        hazelCast = Hazelcast.newHazelcastInstance();
    }

    public DistributedJamonHazelcastPersister(HazelcastInstance hazelCast) {
        this.hazelCast = hazelCast;
    }

    @Override
    public Set<String> getInstances() {
        Set<String> allInstances = new TreeSet<String>(localJamonData.getInstances());
        allInstances.addAll(getHazelcastInstances());
        return allInstances;
    }

    @Override
    /** Put jamon data into the hazelcast map */
    public void put() {
        String label = DistributedJamonHazelcastPersister.class.getCanonicalName()+".put()";
        Monitor mon = MonitorFactory.getTimeMonitor(label);
        // only allow 1 process to put at the sametime.
        if (mon.getActive() < 1) {
            mon.start();
            try {
                intitialize();
                String key = getInstance();
                jamonDataMap.set(key, MonitorFactory.getRootMonitor().setInstanceName(key));
                instances.set(key, new Date());
            } catch(Throwable t) {
                MonitorFactory.addException(mon, t);
            } finally {
                mon.stop();
            }
        }
    }


    @Override
    public MonitorComposite get(String key) {
        MonitorComposite monitorComposite = localJamonData.get(key);
        if (monitorComposite == null) {
            String label = DistributedJamonHazelcastPersister.class.getCanonicalName() + ".get()";
            Monitor mon = MonitorFactory.start(label);
            try {
                intitialize();
                // done purely to ensure instance and data live the same amount of time.
                instances.get(key);
                monitorComposite = jamonDataMap.get(key);
            } catch(Throwable t) {
                MonitorFactory.addException(mon, t);
                return localJamonData.get("local");
            } finally {
                mon.stop();
            }
        }

        return monitorComposite;
    }

    @Override
    public void remove(String instanceKey) {
        if (LocalJamonDataPersister.INSTANCE.equalsIgnoreCase(instanceKey)) {
            localJamonData.remove(instanceKey);
            return;
        }

        String label = DistributedJamonHazelcastPersister.class.getCanonicalName() + ".remove()";
        Monitor mon = MonitorFactory.start(label);
        try {
           intitialize();
           instances.remove(instanceKey);
           jamonDataMap.remove(instanceKey);
        } catch(Throwable t) {
           MonitorFactory.addException(mon, t);
        } finally {
           mon.stop();
        }
    }

    @Override
    public String getInstance() {
       try {
           intitialize();
           return hazelCast.getCluster().getLocalMember().toString();
       } catch (Throwable t) {
          MonitorFactory.addException(t);
       }

        return null;
    }


    public void shutDownHazelCast() {
        try {
            intitialize();
            hazelCast.shutdown();
        } catch (Throwable t) {
            MonitorFactory.addException(t);
        }
    }

    // I don't ever want to not display data when there is a hazelcast error.
    private Set<String> getHazelcastInstances() {
        try {
            intitialize();
            return instances.keySet();
        } catch (Throwable e) {
            MonitorFactory.addException(e);
            Set<String> error=new HashSet<String>();
            error.add("HazelcastExceptionThrown");
            return error;
        }
    }

    private void intitialize() {
        if (jamonDataMap == null) {
            jamonDataMap = hazelCast.getMap(MonitorComposite.class.getCanonicalName());
            instances = hazelCast.getMap("com.jamonapi.instances");
        }
    }

}
