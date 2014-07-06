package com.jamonapi.distributed;

import com.jamonapi.utils.Misc;

/**
 * Created by stevesouza on 7/6/14.
 */
public class JamonDataFactory {

    // CHANGE FROM STATIC !!!!
    private static JamonData jamonData;
    public JamonData get() {
        if (jamonData==null) {
            jamonData =  create("com.jamonapi.distributed.DistributedJamonHazelcast");
            if (jamonData==null) {
                jamonData = new LocalJamonData();
            }
        }

       return jamonData;
    }

    private static JamonData create(String className) {
        try {
            return (JamonData) Class.forName(className).newInstance();
        } catch (Throwable e) {
        }
        return null;
    }
}
