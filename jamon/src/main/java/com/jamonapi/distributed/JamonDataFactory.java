package com.jamonapi.distributed;

/**
 * Created by stevesouza on 7/6/14.
 */
public class JamonDataFactory {

    public JamonData get() {
       return new LocalJamonData();
    }
}
