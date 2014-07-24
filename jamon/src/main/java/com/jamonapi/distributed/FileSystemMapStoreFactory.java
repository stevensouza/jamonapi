package com.jamonapi.distributed;

import com.hazelcast.core.MapLoader;
import com.hazelcast.core.MapStoreFactory;

import java.util.Properties;

/**
 * Created by stevesouza on 7/24/14.
 */
public class FileSystemMapStoreFactory implements MapStoreFactory<String,Object> {

    @Override
    public MapLoader<String, Object> newMapStore(String mapName, Properties properties) {
        return new HazelcastMapStore(mapName);
    }
}
