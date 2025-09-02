package com.jamonapi;


/** Class used to help in the jamon multi-threaded tests JAMon. */
public class MultiThreadedTestHelper  implements Runnable {

    static final int THREADS=25000;    private int threadNum;
    long lobits, hibits;
    Monitor mon;


    public MultiThreadedTestHelper(int threadNum, long lobits, long hibits, Monitor mon) {        this.threadNum=threadNum;        this.lobits=lobits;        this.hibits=hibits;        this.mon=mon;
    }



    public void run() {
        // Alternating threads are either setting the high or lo bit.  The idea is that if a thread        // is interrupted before mon.increase(...) we may get a different value in mon than expected.        // The expected value is compared to the actual value in main(...).        try {            long incr=0;            if (threadNum%2==0) {                incr=lobits;            }            else                incr=hibits;            mon.add(incr);        } catch(Exception e) {            throw new RuntimeException(e.getMessage());        }    }

    static  class TimingMonitorThreads implements Runnable {        Monitor mon;        TimingMonitorThreads(Monitor mon) {            this.mon=mon;        }
        public void run() {            mon.start().stop();            MonitorFactory.start("multi-threaded test").stop();            MonitorFactory.start("multi-threaded test").stop();            MonitorFactory.start("multi-threaded test").stop();            MonitorFactory.start("multi-threaded test").stop();        }    }

}


