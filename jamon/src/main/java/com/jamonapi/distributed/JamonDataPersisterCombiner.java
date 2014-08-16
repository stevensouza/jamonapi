package com.jamonapi.distributed;

import com.jamonapi.MonitorComposite;
import com.jamonapi.MonitorCompositeIterator;
import com.jamonapi.utils.Misc;

import java.util.*;

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

    public MonitorComposite query(String arraySql, String... instanceKeys) {
        if (instanceKeys.length==1) {
            return persister.get(instanceKeys[0]);
        }

        Map<String, MonitorComposite> map = new HashMap<String, MonitorComposite>();
        for (int i=0;i<instanceKeys.length;i++) {
            map.put(instanceKeys[i], persister.get(instanceKeys[i]));
        }

        MonitorComposite mc = new MonitorCompositeIterator(map.values()).toMonitorComposite();
        return mc.setInstanceName(Misc.getAsString(instanceKeys));
    }

    public void remove(String... instanceKey) {
        for (int i=0;i<instanceKey.length;i++) {
            persister.remove(instanceKey[i]);
        }
    }

    private static String removeExtraSpaces(String str) {
        return str.replaceAll("\\s{2,}", " ").trim();
    }

    public static void main(String [] arga) {
        String d="hello my name is        select        *       from    array    ";
        System.out.println(d.replaceAll("\\s{2,}", " "));
    }
}
