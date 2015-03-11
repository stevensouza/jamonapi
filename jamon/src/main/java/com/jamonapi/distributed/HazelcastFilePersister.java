package com.jamonapi.distributed;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**  Class that interacts with HazelCast to save jamon data to it so data from any jvm's in the hazelcast cluster
 * can be visible via the jamon web app.  Note in most cases hazelcast exceptions are not bubbled up in this class
 * as jamon should still be availalbe even if HazelCast has issues.  The exceptions and stack traces can be
 * seen in jamon however.
 *
 * Created by stevesouza on 7/6/14.
 */

public class HazelcastFilePersister extends HazelcastPersister {
	private static final Logger LOGGER = LoggerFactory.getLogger(HazelcastFilePersister.class);

    public HazelcastFilePersister() {
        super(new HazelcastPersisterImp(), new LocalJamonFilePersister());

        LOGGER.info("Created HazelcastFilePersister with default jamonProperties.");
    }

    public HazelcastFilePersister(final Properties jamonProperties) {
        super(new HazelcastPersisterImp(), new LocalJamonFilePersister(jamonProperties));
        
        LOGGER.info("Created HazelcastFilePersister with provided jamonProperties.");
    }
}
