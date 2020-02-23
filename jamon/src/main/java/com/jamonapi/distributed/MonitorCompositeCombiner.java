package com.jamonapi.distributed;

import com.jamonapi.*;
import com.jamonapi.utils.Misc;
import com.jamonapi.utils.SerializationUtils;

import java.util.*;

/**
 * Combines multiple MonitorComposite objects into one by getting them from the @link JamonDataPersister.
 *
 * Created by stevesouza on 8/16/14.
 */
public class MonitorCompositeCombiner {
    private JamonDataPersister persister;

    public MonitorCompositeCombiner(JamonDataPersister persister) {
        this.persister = persister;
    }

    /**
     * Combine MonitorComposites returned by each of the instanceKeys into 1 MonitorComposite.
     *
     * @param instanceKeys
     * @return MonitorComposite
     */
    public MonitorComposite get(String... instanceKeys) {
        return append(getMonitorComposites(instanceKeys));
    }

    public MonitorComposite aggregate(String... instanceKeys) {
        return aggregate(getMonitorComposites(instanceKeys));
    }

    private List<MonitorComposite> getMonitorComposites(String[] instanceKeys) {
        List<MonitorComposite> monitorCompositeList = new ArrayList<MonitorComposite>();
        for (int i=0;i<instanceKeys.length;i++) {
            MonitorComposite monitorComposite = persister.get(instanceKeys[i]);
            if (monitorComposite!=null) {
                monitorCompositeList.add(monitorComposite);
            }
        }
        return monitorCompositeList;
    }


    /**
     * Combine multiple MonitorComposites into 1 MonitorComposite.
     *
     * @param monitorCompositeList
     * @return MonitorComposite
     */
    public static MonitorComposite append(Collection<MonitorComposite> monitorCompositeList) {
        Date previousDate = null;
        Date finalDate = null; // assign the date of all the results as the most recent of all monitorComposite dates
        Iterator<MonitorComposite> iter = monitorCompositeList.iterator();
        // note 2 lists are used instead of a Map so if 2 instanceNames are the same (say 'local') each of them can be
        // retained.
        List<MonitorComposite> monitorCompositeResultsList = new ArrayList<MonitorComposite>();
        List<String> instanceNameList = new ArrayList<String>();

        while (iter.hasNext()) {
            MonitorComposite mc = iter.next();
            instanceNameList.add(mc.getInstanceName());
            monitorCompositeResultsList.add(mc);
            if (previousDate == null || mc.getDateCreated().after(previousDate)) {
                finalDate = mc.getDateCreated();
            }
            previousDate = mc.getDateCreated();
        }

        MonitorComposite mc = new MonitorCompositeIterator(monitorCompositeResultsList).toMonitorComposite().setDateCreated(finalDate);
        return mc.setInstanceName(Misc.getAsString(instanceNameList));
    }

    public static MonitorComposite aggregate(Collection<MonitorComposite> monitorCompositeList) {
        FactoryEnabled factory = new FactoryEnabled();
        MonitorComposite mc = append(monitorCompositeList);
        Monitor[] monitors = mc.getMonitors();
        // 1) iterate data creating monitors and and setup listeners
        // 2) loop through a second time and merge monitor values and populate listeners (maybe done in same method)
        for (Monitor monitor : monitors) {
            MonKey key = SerializationUtils.deepCopy(monitor.getMonKey());
            key.setInstanceName("aggregate");
//            factory.getMonitor(key).add(1);
            merge(monitor, factory.getMonitor(key));
            //monitor.getMonKey().clone();
            // iterator for append sets instancename....monitor.getMonKey().setInstanceName("aggregate");
        }
        return factory.getRootMonitor();
    }


        /**
         * Remove any of the MonitorComposites associated with the key.  This data could be in memory,  on HazelCast
         * or in a file for example.
         *
         * @param instanceKey
         */
    public void remove(String... instanceKey) {
        for (int i=0;i<instanceKey.length;i++) {
            persister.remove(instanceKey[i]);
        }
    }


    public static Monitor merge(Monitor from, Monitor to) {
        to.setHits(to.getHits()+from.getHits());
        to.setTotal(to.getTotal()+from.getTotal());
        to.setMin(Math.min(to.getMin(), from.getMin()));
        to.setMax(Math.max(to.getMax(),  from.getMax()));
        to.setMaxActive(Math.max(to.getMaxActive(), from.getMaxActive()));
        to.setActive(to.getActive()+from.getActive());
        // to.setTotalActive();//?
        // to.globalactive....
        to.setFirstAccess(Misc.min(to.getFirstAccess(), from.getFirstAccess()));// ?
        to.setLastAccess(Misc.max(to.getLastAccess(), from.getLastAccess()));//?
        to.setLastValue(from.getLastValue()); // last access date more recent use that value
        to.getAvgActive();//?
        to.getAvgGlobalActive();//?
        to.getAvgPrimaryActive();//?
        to.setPrimary(from.isPrimary());//?
        return to;
    }


}
