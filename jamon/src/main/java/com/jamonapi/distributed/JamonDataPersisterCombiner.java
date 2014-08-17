package com.jamonapi.distributed;

import com.jamonapi.MonitorComposite;
import com.jamonapi.MonitorCompositeIterator;
import com.jamonapi.utils.Misc;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by stevesouza on 8/16/14.
 */
public class JamonDataPersisterCombiner  {
    private final String ALL_DATA = "select * from array";
    private JamonDataPersister persister;

    public JamonDataPersisterCombiner(JamonDataPersister persister) {
        this.persister = persister;
    }

    public MonitorComposite get(String... instanceKeys) {
        return query(ALL_DATA, instanceKeys);
    }


    private MonitorComposite query(String arraySql, String... instanceKeys) {
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

    public void remove(String... instanceKey) {
        for (int i=0;i<instanceKey.length;i++) {
            persister.remove(instanceKey[i]);
        }
    }

}
