package com.jamonapi.distributed;

import com.jamonapi.MonitorComposite;
import com.jamonapi.MonitorFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;


public class DistributedJamonHazelcastTest {

    private HazelcastPersister jamonData;

    @Before
    public void setUp() throws Exception {
        MonitorFactory.reset();
        jamonData = new HazelcastPersister();
    }

    @After
    public void tearDown() throws Exception {
        if (jamonData != null) {
            jamonData.shutDownHazelCast();
        }
    }


    @Test
    public void putGetRemove() throws InterruptedException {
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

          // rows should be 100 for the loop and 1 for the call to jamonData.put(), com.jamonapi.Exceptions = 102
          assertThat(jamonData.get(jamonData.getInstance()).getNumRows()).isEqualTo(102);
          jamonData.remove(jamonData.getInstance());
          assertThat(jamonData.get(jamonData.getInstance())).isNull();
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
