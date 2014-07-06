package com.jamonapi.distributed;

import com.jamonapi.utils.Misc;

/**
 * Created by stevesouza on 7/6/14.
 */
public class JamonDataFactory {

    public JamonData get() {
        JamonData jamonData =  create("com.jamonapi.distributed.DistributedJamonHazelcast");
        if (jamonData==null) {
            jamonData = new LocalJamonData();
        }

       return jamonData;
    }

    private static JamonData create(String className) {
        try {
            return (JamonData) Class.forName(className).newInstance();
        } catch (Exception e) {

        }
        return null;
    }
}
