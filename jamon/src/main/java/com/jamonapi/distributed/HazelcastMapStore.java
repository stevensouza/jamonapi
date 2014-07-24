package com.jamonapi.distributed;

import com.hazelcast.core.MapStore;
import com.jamonapi.MonitorComposite;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by stevesouza on 7/23/14.
 */
public class HazelcastMapStore implements MapStore<String, Object> {

    private String mapName="jamonapi";


    public HazelcastMapStore() {

    }
    public HazelcastMapStore(String mapName) {
        System.out.println("mapName="+mapName);
        this.mapName = mapName;
    }

    @Override
    public void store(String key, Object value) {
        System.out.println("store: key="+key+", value="+value);
    }

    @Override
    public void storeAll(Map<String, Object> map) {
        System.out.println("storeAll of "+map);

    }

    @Override
    public void delete(String key) {
        System.out.println("delete key="+key);


    }

    @Override
    public void deleteAll(Collection<String> keys) {
        System.out.println("delete keys="+keys);


    }

    @Override
    public Object load(String key) {
        System.out.println("load key="+key);

        return null;
    }

    @Override
    public Map<String, Object> loadAll(Collection<String> keys) {
        System.out.println("load all keys/values: keys="+keys);

        return null;
    }

    @Override
    public Set<String> loadAllKeys() {

        System.out.println("load all keys");

        return null;
    }
}
