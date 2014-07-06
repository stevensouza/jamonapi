package com.jamonapi.distributed;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.jamonapi.MonitorComposite;
import com.jamonapi.MonitorFactory;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by stevesouza on 7/6/14.
 */

/** NOTE THIS CLASS NEEDS TO SUPPORT local too for performance reasons */
// need to make this something like 7/6/14
//    MonitorComposite mc = MonitorFactory.getRootComposite();
///   mc = mc.getComposite(rangeName);
//    MonitorComposite mc=MonitorFactory.getComposite(rangeName);
// see    public MonitorComposite getComposite(String units) {
//  return new MonitorComposite(getMonitors(units));
//   }
    // monitor.hasListeners() being wrong
//Time too live
//        Timer period

public class DistributedJamonHazelcast implements JamonData {

    // could be Map if we don't want the instance methods of hazelcast
    private IMap<String, MonitorComposite> jamonDataMap;
    // This should really be an ISet, but ISet doesn't support time-to-live methods
    private IMap<String, Date> instances;
    private HazelcastInstance hazelCast;
    private LocalJamonData localJamonData = new LocalJamonData();

    public DistributedJamonHazelcast() {
        this(Hazelcast.newHazelcastInstance());
    }

    public DistributedJamonHazelcast(HazelcastInstance hazelCast) {
        this.hazelCast = hazelCast;
        jamonDataMap = hazelCast.getMap(MonitorComposite.class.getCanonicalName());
        instances = hazelCast.getMap("com.jamonapi.instances");
    }

    @Override
    public IMap<String, MonitorComposite> getMap() {
        return jamonDataMap;
    }

    @Override
    public Set<String> getInstances() {
        Set<String> allInstances = localJamonData.getInstances();
        allInstances.addAll(instances.keySet());
        return allInstances;
    }
//
//    @Override
//    public void put(String key, MonitorComposite monitorComposite) {
//        jamonDataMap.set(key, monitorComposite, 1, TimeUnit.HOURS);
//        instances.set(key, key, 1, TimeUnit.HOURS);
//    }
//
//    @Override
//    public void put(MonitorComposite monitorComposite) {
//
//    }

    @Override
    public void put() {
        String key = getInstance();
        jamonDataMap.set(key, MonitorFactory.getRootMonitor(), 1, TimeUnit.HOURS);
        instances.set(key, new Date(), 1, TimeUnit.HOURS);
    }

    /** NOTE THIS CLASS NEEDS TO SUPPORT local too for performance reasons */
    // need to make this something like 7/6/14
//    MonitorComposite mc = MonitorFactory.getRootComposite();
///   mc = mc.getComposite(rangeName);
//    MonitorComposite mc=MonitorFactory.getComposite(rangeName);
    // see    public MonitorComposite getComposite(String units) {
    //  return new MonitorComposite(getMonitors(units));
    //   }
    @Override
    public MonitorComposite get(String key) {
        MonitorComposite monitorComposite = localJamonData.get(key);
        if (monitorComposite==null) {
            monitorComposite = jamonDataMap.get(key);
        }

        System.out.println("***** hazelcast stats for jamonmap: "+jamonDataMap.getLocalMapStats());

        return monitorComposite;
    }

    private String getInstance() {
        return hazelCast.getCluster().getLocalMember().toString();
    }

    public void shutDownHazelCast() {
        hazelCast.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        DistributedJamonHazelcast driver = new DistributedJamonHazelcast();
        String nodeName = driver.getInstance();
        int i=0;
        while (true) {
            i++;
            String label = (args.length==0) ? "jamon-hazelcast" : args[0];
            MonitorFactory.add(label + "-" + i, "count", i);
            TimeUnit.SECONDS.sleep(1);
            if (i%10==0) {
                driver.put();
                MonitorComposite composite =  driver.get(nodeName);
                System.out.println("****distributed mapsize: " + driver.getMap().size() + ", MonitorComposite rows: " + composite.getNumRows());
                System.out.println("**** cluster members: " + driver.hazelCast.getCluster().getMembers());
                System.out.println("****"+driver.hazelCast.getCluster().getLocalMember().toString());
                System.out.println("****"+driver.hazelCast.getName());
                System.out.println(driver.jamonDataMap.getLocalMapStats());

            }
        }
    }
}
