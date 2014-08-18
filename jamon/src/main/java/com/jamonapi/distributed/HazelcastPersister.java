package com.jamonapi.distributed;

/**  Decorator class for HazelCast persister. The decorate class interacts with HazelCast and the decorator monitors
 * these interactions with jamon and tracks any exceptions thrown.
 *
 * Created by stevesouza on 7/6/14.
 */

public class HazelcastPersister extends JamonDataPersisterDecorator {

    public HazelcastPersister() {
        this(new HazelcastPersisterImp(), new LocalJamonDataPersister());
    }

    HazelcastPersister(HazelcastPersisterImp hazelcastPersisterImp) {
        super(hazelcastPersisterImp, new LocalJamonDataPersister());
    }

    HazelcastPersister(JamonDataPersister persister, LocalJamonDataPersister localJamonData) {
        super(persister, localJamonData);
    }


    public void shutDownHazelCast() {
        ((HazelcastPersisterImp) getJamonDataPersister()).shutDownHazelCast();
    }

}
