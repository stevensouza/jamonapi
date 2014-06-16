package com.jamonapi;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class MultiThreadedTest {

    @Test
    public void testMultiThread() {

        long LOBIT=0x00000001;//1
        long HIBIT=0x10000000;//268,435,456

        ThreadGroup threadGroup=new ThreadGroup("threadGroup");
        //Note mon1 is shared by all instances of the thread and so will test concurrent access.
        Monitor mon1=MonitorFactory.getTimeMonitor("mon1");
        for (int i=0; i<MultiThreadedTestHelper.THREADS; i++) // mon1 should be THREADS*1
            new Thread(threadGroup, new MultiThreadedTestHelper(i, LOBIT, LOBIT, mon1)).start();
        Monitor mon2=MonitorFactory.getTimeMonitor("mon2");
        for (int i=0; i<MultiThreadedTestHelper.THREADS; i++)//THREADS*HIBIT
            new Thread(threadGroup, new MultiThreadedTestHelper(i, HIBIT, HIBIT, mon2)).start();
        Monitor mon3=MonitorFactory.getTimeMonitor("mon3");
        for (int i=0; i<MultiThreadedTestHelper.THREADS; i++)  //(THREADS/2)*LOBIT + (THREADS/2)*HIBIT
            new Thread(threadGroup, new MultiThreadedTestHelper(i, LOBIT, HIBIT, mon3)).start();
        while(threadGroup.activeCount()!=0)
            ;

        assertThat(mon1.getTotal()).isEqualTo(MultiThreadedTestHelper.THREADS);
        assertThat(mon2.getTotal()).isEqualTo(MultiThreadedTestHelper.THREADS*HIBIT);

        double threadCount=MultiThreadedTestHelper.THREADS/2;
        assertThat(mon3.getTotal()).isEqualTo(threadCount*LOBIT + threadCount*HIBIT);

        Monitor mon4=MonitorFactory.start("mon4");
        threadGroup=new ThreadGroup("timingMonitorThreads");
        for (int i=0; i<MultiThreadedTestHelper.THREADS; i++)
            new Thread(threadGroup, new MultiThreadedTestHelper.TimingMonitorThreads(mon4)).start();

        while(threadGroup.activeCount()!=0)
            ;

        mon4.stop();
        assertThat(mon4.getHits()).isEqualTo(MultiThreadedTestHelper.THREADS+1);
        assertThat(MonitorFactory.getMonitor("multi-threaded test","ms.").getHits()).isEqualTo(MultiThreadedTestHelper.THREADS*4);
    }

}
