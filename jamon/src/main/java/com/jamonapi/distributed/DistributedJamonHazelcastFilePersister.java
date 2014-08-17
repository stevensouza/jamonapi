package com.jamonapi.distributed;

/**  Class that interacts with HazelCast to save jamon data to it so data from any jvms in the hazelcast cluster
 * can be visible via the jamon web app.  Note in must cases hazelcast exceptions are not bubbled up in this class
 * as I would still like jamon to be availalbe even if HazelCast has issues.  The exceptions and stack traces can be
 * seen in jamon however.
 *
 * Created by stevesouza on 7/6/14.
 */

public class DistributedJamonHazelcastFilePersister extends DistributedJamonHazelcastPersister {

    public DistributedJamonHazelcastFilePersister() {
        super(new DistributedJamonHazelcastPersisterImp(), new LocalJamonFilePersister());
    }

}
