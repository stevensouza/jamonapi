package com.jamonapi.distributed;

import com.jamonapi.MonitorComposite;
import com.jamonapi.MonitorFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.fest.assertions.api.Assertions.assertThat;


public class DistributedJamonHazelcastTest {

    @Before
    public void setUp() throws Exception {
        MonitorFactory.reset();
    }

    @After
    public void tearDown() throws Exception {

    }


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

          jamonData.shutDownHazelCast();
        }

    /** When hazelcast throws exceptions jamon should still work */
    @Test
    public void testHazelCastExceptions() {
        DistributedJamonHazelcast jamonData = new DistributedJamonHazelcast(null);
        assertThat(jamonData.getInstances()).hasSize(2);
        MonitorComposite mc = jamonData.getMonitors("NO_EXIST");
        assertThat(mc.isLocalInstance()).isTrue();
        // no assertion for put, but shouldn't throw exception
        jamonData.put();
    }

}