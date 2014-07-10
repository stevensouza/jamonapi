package com.jamonapi.distributed;

import com.jamonapi.MonitorComposite;
import com.jamonapi.MonitorFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class DistributedJamonHazelcastTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    //   MonitorComposite composite =  driver.getMonitors(nodeName);
//                    System.out.println("****distributed mapsize: " + driver.getMap().size() + ", MonitorComposite rows: " + composite.getNumRows());
//                    System.out.println("**** cluster members: " + driver.hazelCast.getCluster().getMembers());
//                    System.out.println("****"+driver.hazelCast.getCluster().getLocalMember().toString());
//                    System.out.println("****"+driver.hazelCast.getName());
//                    System.out.println(driver.jamonDataMap.getLocalMapStats());

    @Test
    public void joinCluster() throws InterruptedException {
            DistributedJamonHazelcast jamonData = new DistributedJamonHazelcast();
            int i=0;
            while (true) {
                i++;
                MonitorFactory.add(DistributedJamonHazelcastTest.class.getCanonicalName() + "-" + i, "count", i);
                TimeUnit.SECONDS.sleep(1);
                if (i%10==0) {
                    jamonData.put();
                }

                if (i==100) {
                    break;
                }
            }

        }

}