package com.jamonapi.distributed;

import com.hazelcast.core.MapLoader;
import com.hazelcast.core.MapStoreFactory;

import java.io.Serializable;
import java.util.Properties;

/**
 * Created by stevesouza on 7/24/14.
 *
 * @deprecated This class currently won't save the map properly.
 */
public class FileSystemMapStoreFactory implements MapStoreFactory<String, Serializable> {

    @Override
    public MapLoader<String, Serializable> newMapStore(String mapName, Properties properties) {
        return new HazelcastMapStore(mapName);
    }
}
