package com.jamonapi;

/** Class used to test all classes in JAMon.  It is only used during testing.  Mostly it calls other classes Main method.  JAMon *  places test code in main() methods.  TestClass also adds test code of its own **/

public class TestClass extends java.lang.Object implements Runnable {    
    static final int THREADS=25000;    private int threadNum;
    public TestClass(int threadNum, long lobits, long hibits, Monitor mon) {        this.threadNum=threadNum;        this.lobits=lobits;        this.hibits=hibits;        this.mon=mon;
    }
    
 
    public void run() {
        // Alternating threads are either setting the high or lo bit.  The idea is that if a thread        // is interrupted before mon.increase(...) we may get a different value in mon than expected.        // The expected value is compared to the actual value in main(...).        try {            long incr=0;            if (threadNum%2==0) {                incr=lobits;            }            else                 incr=hibits;            mon.add(incr);        } catch(Exception e) {            throw new RuntimeException(e.getMessage());        }    }
    long lobits, hibits;    Monitor mon;
    static private class TimingMonitorThreads implements Runnable {        Monitor mon;        TimingMonitorThreads(Monitor mon) {            this.mon=mon;        }
        public void run() {            mon.start().stop();            MonitorFactory.start("multi-threaded test").stop();            MonitorFactory.start("multi-threaded test").stop();            MonitorFactory.start("multi-threaded test").stop();            MonitorFactory.start("multi-threaded test").stop();        }    }
        public static void testMonKey(String label) {        System.out.println("\n***** Testing getLabel, getUnits, getMonKey, getRange for "+label);        Monitor mon=MonitorFactory.start(label).stop();        System.out.println(mon.getMonKey());        System.out.println(mon.getLabel());        System.out.println(mon.getUnits());        System.out.println(mon.getMonKey().getValue("label"));        System.out.println(mon.getMonKey().getValue("Units"));        System.out.println(mon.getRange());    }
    public static void main(String[] args) throws Exception {
        System.out.println("***** Class unit tests");        System.out.println("\nMonitorFactory.main()");
        MonitorFactory.main(null);
        System.out.println("\nTestClassPerformance.main()");        TestClassPerformance.main(args);        Monitor timingMon;
        System.out.println("\n***** MonitorFactory.getData():");        Monitor m1=MonitorFactory.start("pages.purchase.test");        Monitor m2=MonitorFactory.start("steve.souza.test");
        Thread.sleep(350);        m1.stop();        Thread.sleep(650);        m2.stop();        Object[][] rows=MonitorFactory.getData();
        for (int i=0; i<rows.length; i++) {            String rowData="row"+i+"=[";            for (int j=0; j<rows[0].length; j++) {                rowData+=rows[i][j]+", ";            }            System.out.println(rowData+"]");        }
                System.out.println("\n***** Multi-threaded test");        long LOBIT=0x00000001;//1        long HIBIT=0x10000000;//268,435,456
        timingMon=MonitorFactory.start();        ThreadGroup threadGroup=new ThreadGroup("threadGroup");
        //Note mon1 is shared by all instances of the thread and so will test concurrent access.
        Monitor mon1=MonitorFactory.getTimeMonitor("mon1");        for (int i=0; i<THREADS; i++) // mon1 should be THREADS*1            new Thread(threadGroup, new TestClass(i, LOBIT, LOBIT, mon1)).start();
        Monitor mon2=MonitorFactory.getTimeMonitor("mon2");        for (int i=0; i<THREADS; i++)//THREADS*HIBIT            new Thread(threadGroup, new TestClass(i, HIBIT, HIBIT, mon2)).start();
        Monitor mon3=MonitorFactory.getTimeMonitor("mon3");        for (int i=0; i<THREADS; i++)  //(THREADS/2)*LOBIT + (THREADS/2)*HIBIT              new Thread(threadGroup, new TestClass(i, LOBIT, HIBIT, mon3)).start();
        while(threadGroup.activeCount()!=0)            ;
        System.out.println("Threads have finished processing. It took "+timingMon.stop());        System.out.println("Total should equal "+THREADS+" - "+mon1);        System.out.println("Total should equal "+THREADS*HIBIT+" - "+mon2);        double threadCount=THREADS/2;        System.out.println("Total should equal "+(threadCount*LOBIT + threadCount*HIBIT) +" - "+mon3);
        Monitor mon4=MonitorFactory.start("mon4");        threadGroup=new ThreadGroup("timingMonitorThreads");        for (int i=0; i<THREADS; i++)            new Thread(threadGroup, new TimingMonitorThreads(mon4)).start();
        while(threadGroup.activeCount()!=0)            ;
        System.out.println("hits should be "+(THREADS+1)+"= "+mon4.stop());        System.out.println("'multi-threaded test' hits should equal "+THREADS*4+" - "+MonitorFactory.getMonitor("multi-threaded test","ms."));
        System.out.println("\n***** MonitorFactory.getHeader():");        String[] header=MonitorFactory.getHeader();

        for (int i=0; i<header.length; i++)           System.out.println(header[i]);
         testMonKey("TestingMonKey");        MonitorFactory.disable();        testMonKey("DisableTestingMonKey");        MonitorFactory.enable();                System.out.println("\n***** MonitorFactory.getReport() 1:");        System.out.println(MonitorFactory.getReport());
    }
}


