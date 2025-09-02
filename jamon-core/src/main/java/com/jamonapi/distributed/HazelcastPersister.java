package com.jamonapi.distributed;

/**  Wrapper for HazelCast persister that provides other services such as monitoring.
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
