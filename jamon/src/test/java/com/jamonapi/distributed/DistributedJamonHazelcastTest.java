package com.jamonapi.distributed;

import com.jamonapi.MonitorComposite;
import com.jamonapi.MonitorFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;


public class DistributedJamonHazelcastTest {

    @Before
    public void setUp() throws Exception {
        MonitorFactory.reset();
    }

    @After
    public void tearDown() throws Exception {

    }


    @Test
    public void putGetRemove() throws InterruptedException {
            HazelcastPersister jamonData = new HazelcastPersister();
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

          // rows should be 100 for the loop and 1 for the call to get = 101
          assertThat(jamonData.get(jamonData.getInstance()).getNumRows()).isEqualTo(101);
          jamonData.remove(jamonData.getInstance());
          assertThat(jamonData.get(jamonData.getInstance())).isNull();
          jamonData.shutDownHazelCast();
    }


    /** When hazelcast throws exceptions jamon should still work */
    @Test
    public void testHazelCastExceptions() {
        HazelcastPersister jamonData = new HazelcastPersister(null);
        assertThat(jamonData.getInstances()).hasSize(2);
        MonitorComposite mc = jamonData.get("NO_EXIST");
        assertThat(mc.isLocalInstance()).isTrue();
        // no assertion for put, but shouldn't throw exception
        jamonData.put();
    }

}
