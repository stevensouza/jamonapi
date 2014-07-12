package com.jamonapi.distributed;

/**
 * Class that instanciates the JamonData class.  Note this could be a local implementation or a distributed jamon intrface
 * such as HazelCast.
 *
 * Created by stevesouza on 7/6/14.
 */
public class JamonDataFactory {

    private static JamonDataFactory factory = new JamonDataFactory();

    private JamonDataFactory() {
    }
    private JamonData jamonData;
    public static JamonData get() {
        if (factory.jamonData==null) {
            factory.initialize();
        }
         return factory.jamonData;
    }


    private void initialize() {
        jamonData =  create("com.jamonapi.distributed.DistributedJamonHazelcast");
        if (jamonData==null) {
           jamonData = new LocalJamonData();
        }
    }

    private static JamonData create(String className) {
        try {
            return (JamonData) Class.forName(className).newInstance();
        } catch (Throwable e) {
        }
        return null;
    }
}
