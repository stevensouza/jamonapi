package com.jamonapi;


/**
 * Class used to test performance of JAMon.  It is only used for testing purposes.
 *
 * @author  steve souza
 */
public class TestClassPerformance {
    
    /** Creates a new instance of TestClassPerformance */
    public TestClassPerformance() {
        this(100000);
    }
    
    private int testIterations;
    
    public TestClassPerformance(int testIterations) {
        this.testIterations=testIterations;
    }
    
    private Monitor testMon;
    
    public void timingNoMonitor() throws Exception {
        // The null monitor has the best possible performance
        System.out.println("\ntimingNoMonitor() - timing the old fashioned way with System.currentTimeMillis() (i.e.no monitors)");
        System.out.println("System.currentTimeMillis() - startTime");
        long startTime=0;
        
        // note the assignment to startTime is meaningless.  The calculation for elapsed time would really be
        // as follows, however being as most implementations of this timing method would not assign the results
        // to an endTime variable, I thought it would be a better comparison to also leave it out in the performance
        // test.
        //  startTime = System.currentTimeMillis();
        //  endTime = System.currentTimeMillis() - startTime;
        for (int i=0; i<testIterations; i++) {
            startTime = System.currentTimeMillis() - System.currentTimeMillis() - startTime;
        }
    }
    
    public void basicTimingMonitor() throws Exception {
        // The null monitor has the best possible performance
        System.out.println("\nbasicTimingMonitor() - this is the most lightweight of the Monitors");
        System.out.println("\tBasicTimingMonitor mon=new BasicTimingMonitor();");
        System.out.println("\tmon.start()");
        System.out.println("\tmon.stop()");
        BasicTimingMonitor mon=new BasicTimingMonitor();
        
        for (int i=0; i<testIterations; i++) {
            mon.start();
            mon.stop();
        }
    }
    
    public void nullMonitor() throws Exception {
        // The null monitor has the best possible performance, however it doesn't do anything so is only appropriate when monitoring 
        // is disabled.
        System.out.println("\nNullMonitor() - Factory disabled so a NullMonitor is returned");
        System.out.println("\tMonitorFactory.setEnabled(false);");
        System.out.println("\tMonitor mon=MonitorFactory.start();");
        System.out.println("\tmon.stop()");
        
        MonitorFactory.setEnabled(false);
        
        for (int i=0; i<testIterations; i++) {
            testMon=MonitorFactory.start();
            testMon.stop();
        }
        
        MonitorFactory.setEnabled(true);
    }
    
//    public void testAdd() {
//    	long loop=testIterations*10;
//    	for (int i=0;i<loop;i++)
//    		MonitorFactory.add("delme","test",1);
//    }
//    
    
    public void nullMonitor2() throws Exception {
        // The null monitor has the best possible performance
        System.out.println("\nNullMonitor2() - Factory disabled so a NullMonitor is returned");
        System.out.println("\tMonitorFactory.setEnabled(false);");
        System.out.println("\tMonitor mon=MonitorFactory.start('pages.admin');");
        System.out.println("\tmon.stop()");
        
        MonitorFactory.setEnabled(false);
        
        for (int i=0; i<testIterations; i++) {
            testMon=MonitorFactory.start("pages.admin");
            testMon.stop();
        }
        
        MonitorFactory.setEnabled(true);
    }
    
    public void factoryBasicMonitor() throws Exception {
        System.out.println("\nbasic Factory TimingMonitor()");
        System.out.println("\tMonitor mon=MonitorFactory.start();");
        System.out.println("\tmon.stop();");
        
        //testMon=new TimingMonitor();  the following has the same performance characteristics but you also
        // have the added option of disabling the service and returning a null monitor
        for (int i=0; i<testIterations; i++) {
            testMon=MonitorFactory.start();
            testMon.stop();
        }
        
    }

    public void factoryMonitor() throws Exception {
        System.out.println("\nFull Factory TimingMonitor()- uses cached version so doesn't create child monitors");
        System.out.println("\tMonitor mon=MonitorFactory.start('pages.admin');");
        System.out.println("\tmon.stop();");
        
        for (int i=0; i<testIterations; i++) {
            testMon=MonitorFactory.start("pages.admin");  
            testMon.stop();
        }
        
        System.out.println(testMon);
        
    }
    
   
    public void addMonitor() throws Exception {
        System.out.println("Calling add...\n");
        System.out.println("\tMonitorFactory.add('label','units',1);");
        
        for (int i=0; i<testIterations; i++) {
           MonitorFactory.add("label","units",1);  
        }
        
        System.out.println(MonitorFactory.getMonitor("label", "units"));
        
    }
   
    
    /*
    
    public void debugFactoryMonitor() throws Exception {
        System.out.println("\nFull Factory TimingMonitor() using debug factory - uses cached version so doesn't create child monitors");
        System.out.println("\tMonitor mon=MonitorFactory.getDebugFactory().start('pages.admin');");
        System.out.println("\tmon.stop();");
        
        for (int i=0; i<testIterations; i++) {
            testMon=MonitorFactory.getDebugFactory().start("pages.admin");  // not executed if debug disabled.
            testMon.stop();
        }
        
        System.out.println(testMon);
        
    }*/
    
    private static void log(Monitor mon)  {
        System.out.println("It took "+mon);
    }
    
    /** Test class for performance numbers of JAMon.  You can execute the test code in the following 2 ways:
     *
     *  To execute with the default number of iterations (currently 100,000).  This takes about .5 seconds on my Pentium IV.
     *  java -cp JAMon.jar com.jamonapi.TestClassPerformance
     *
     *  To execute with a different number of iterations pass the number after the class name.
     *  java -cp JAMon.jar com.jamonapi.TestClassPerformance 500000
     **/
    public static void main(String[] args) throws Exception {
        TestClassPerformance test;
        
        if (args.length==0) 
            test=new TestClassPerformance();
        else {
            int testIterations=Integer.parseInt(args[0]); 
            test=new TestClassPerformance(testIterations);
        }
        
        System.out.println("\n***** Performance Tests:");
        System.out.println("All performance code loops "+test.testIterations+" times");
        
        Monitor totalTime=MonitorFactory.start();

        Monitor timingMon=MonitorFactory.start();
        test.timingNoMonitor();
        log(timingMon.stop());
        
        timingMon.start();
        test.basicTimingMonitor();
        log(timingMon.stop());
        
        timingMon.start();
        test.nullMonitor();
        log(timingMon.stop());
        
        timingMon.start();
        test.nullMonitor2();
        log(timingMon.stop());
        
        timingMon.start();
        test.factoryBasicMonitor();
        log(timingMon.stop());
        
       
        timingMon.start();
        test.factoryMonitor();
        log(timingMon.stop());
        
        System.out.println("\nExecuting full factory monitors a second time.  The second time reflects performance characteristics more accurately");
      //  test.testAdd();
        timingMon.start();
        test.factoryMonitor();
        log(timingMon.stop());
     
        timingMon.start();
        test.addMonitor();
        log(timingMon.stop());
        
        System.out.println("\n***** Total time for performance tests were: "+totalTime.stop());
        
    }
    
}
