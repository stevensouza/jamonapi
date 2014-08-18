package com.jamonapi.distributed;

/**  Class that interacts with HazelCast to save jamon data to it so data from any jvm's in the hazelcast cluster
 * can be visible via the jamon web app.  Note in most cases hazelcast exceptions are not bubbled up in this class
 * as jamon should still be availalbe even if HazelCast has issues.  The exceptions and stack traces can be
 * seen in jamon however.
 *
 * Created by stevesouza on 7/6/14.
 */

public class HazelcastFilePersister extends HazelcastPersister {

    public HazelcastFilePersister() {
        super(new HazelcastPersisterImp(), new LocalJamonFilePersister());
    }

}
