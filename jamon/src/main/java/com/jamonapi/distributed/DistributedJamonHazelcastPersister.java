package com.jamonapi.distributed;

/**  Decorator class for HazelCast persister. The decorate class interacts with HazelCast and the decorator monitors
 * these interactions with jamon and tracks any exceptions thrown.
 *
 * Created by stevesouza on 7/6/14.
 */

public class DistributedJamonHazelcastPersister extends JamonDataPersisterDecorator {

    public DistributedJamonHazelcastPersister() {
        this(new DistributedJamonHazelcastPersisterImp());
    }

    public DistributedJamonHazelcastPersister(DistributedJamonHazelcastPersisterImp hazelcastPersisterImp) {
        super(hazelcastPersisterImp);
    }

    public void shutDownHazelCast() {
        ((DistributedJamonHazelcastPersisterImp) getJamonDataPersister()).shutDownHazelCast();
    }

}
