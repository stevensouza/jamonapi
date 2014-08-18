package com.jamonapi.distributed;

import com.jamonapi.MonitorComposite;
import com.jamonapi.MonitorCompositeIterator;
import com.jamonapi.utils.Misc;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        Map<String, MonitorComposite> map = new HashMap<String, MonitorComposite>();
        Date previousDate = null;
        Date finalDate = null; // assign the date of all the results as the most recent of all monitorComposite dates
        for (int i=0;i<instanceKeys.length;i++) {
            MonitorComposite mc = persister.get(instanceKeys[i]);
            map.put(instanceKeys[i], mc);
            if (previousDate == null || mc.getDateCreated().after(previousDate)) {
                finalDate = mc.getDateCreated();
            }
            previousDate = mc.getDateCreated();
        }

        MonitorComposite mc = new MonitorCompositeIterator(map.values()).toMonitorComposite().setDateCreated(finalDate);
        return mc.setInstanceName(Misc.getAsString(instanceKeys));
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

}
