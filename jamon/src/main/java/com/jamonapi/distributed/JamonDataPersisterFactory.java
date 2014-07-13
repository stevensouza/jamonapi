package com.jamonapi.distributed;

/**
 * Class that instanciates the JamonDataPersister class.  Note this could be a local implementation or a distributed jamon intrface
 * such as HazelCast.
 *
 * Created by stevesouza on 7/6/14.
 */
public class JamonDataPersisterFactory {

    private static JamonDataPersisterFactory factory = new JamonDataPersisterFactory();

    private JamonDataPersisterFactory() {
    }
    private JamonDataPersister jamonDataPersister;
    public static JamonDataPersister get() {
        if (factory.jamonDataPersister ==null) {
            factory.initialize();
        }
         return factory.jamonDataPersister;
    }


    private void initialize() {
        jamonDataPersister =  create("com.jamonapi.distributed.DistributedJamonHazelcastPersister");
        if (jamonDataPersister ==null) {
           jamonDataPersister = new LocalJamonDataPersister();
        }
    }

    private static JamonDataPersister create(String className) {
        try {
            return (JamonDataPersister) Class.forName(className).newInstance();
        } catch (Throwable e) {
        }
        return null;
    }
}
