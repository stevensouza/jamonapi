package com.jamonapi;

/**

 * Static MonitorFactory that is good to use in most cases.  In the situation
 *when you want a different factory then instanciated FactoryEnabled directly.
 *Note this is mostly a wrapper for FactoryEnabled and FactoryDisabled. You can also 
 *get the underlying factory with a call to getFactory()
 *
 * Created on December 10, 2005, 11:41 AM
 *
 * Partial Change history for JAMon 2.0
 *
 * 12/29/05 - coded the ability to enable/disable monitors - 2 hrs
 * 12/30/05 - improved performance of time monitors 20% by not recalculating last active time.
 Time went from 2443 ms. to 1983 ms. - 1 hr
 * 1/2/05 - fixed bug where i was using the execution time and not the end time to set the last
 *              access stats.
 * 1/6/06 - save most recent value/last value.   Ed came up with a good reason for this.
 * 1/9/06 - coded reset (mon, range, freqdist)
 * 1/17/06 - coded ability for ranges to either be < (time) or <= (most others).  note this makes time
 *   inconsistent with previous release where it was < for the first entry (0), and <= for the others.
 * 1/17/06 - worked on range copy function.  wasn't properly initializes FrequencyDist objects
 * 1/17/06 - so far this release about 800k starts/stops a second.  previous release 500k.  About 60% faster.
 * 1/19/06 - worked on null range object having the same number of frequencydists as its real monitors range
 * 1/20/06 - added range getheader and getdata methods for display in drop down.
 * 1/21/06 - added display of enabled status to returned data.
 * 1/21/06 - added getBasicHeader, and getBasicData
 * 1/21/06 - added getComposite by range name/units
 * 1/21/06 - changed order of last value (after total now)
 * 1/21/06 - had nullmonitor return same units as its real monitor
 * 1/21/06 - hid some implementation details in an implementation MonitorImp interface.  This helped
 *             get rid of some ugly casting without exposing end users to the implementation interface
 * 1/21/06 - added MonitorComposite.hasData() method 
 * 1/22/06 - added getBasicData(), getDisplayData(), and getData() methods to monitor composite
 * 1/22/06 - created FrequencyDistImp to hide implementation of frequencies from end users.
 * 1/22/06 - created RangeImp to hide the implementation of ranges from end users
 * 1/27/06 - Added stats to range that are more like monitor stats (i.e. getStdDev, getMin, getMax) and
 *            so was able to reduce code by finding a common abstraction between Monitors and FrequencyDists. 
 *            The abstraction is called BaseStats
 * 1/27/06 - Coded toString() method for Monitors (it has changed is appearance)
 * 1/31/06 - Added FactoryDisabled, and FactoryEnabled and changed MonitorFactory to use them.
 * 2/4/06 - Improved overall and synchronized performane.  Performed synchronization performance tests.  see results on delme directory
 *  html and also grouped bookmakr jamon sync test.  I got 5 simultaneous loops going
 *  of 1 milliona start stops (JAMonAdminOld.jsp).  Results follow
 *  1) post changes of 2/7/06 new jamon with synchronization code - max was 20 seconds! fastest was 1.9 seconds. Synchronized
 *       speed was 5 times faster than when I started on 2/4, and 3 times faster than jamon 1.0!
 *  2) post changes of 2/4/06 new jamon with synchronization code - max was 47 seconds. fastest 2.1 seconds
 *  3) pre-changes of 2/4/06 new jamon with synchronization code - max was 75 seconds. fastest 2.6 seconds
 *  4) old jamon with synchronziation code - max was 60 seconds htough one execution was slower than new 2.9 seconds
 *  5) new jamon nonsynchronized hashamp - max was 100 seconds, fastest 5 seconds (not sure why)
 *  6) new code no synchronziation (ideal time) - max was 10 seconds, fastest 2.1 seconds. so performance isn't much better
 *      one no blocking happens.
 * 2/7/06 - Put better synchronization in code that does not call start/stop (just add).  I no longer track active in this
 *      case. Performance test 1,000,000 times:  with add no range 670 ms., start/stop timer 1600 ms. 20% improvement over
 *      12/30 performance.  JAMon 1.0 was 300,000 calls to start/stop a second.   JAMon 2.0 is 625,000 calls a second!
 * 2/11/06 - Created the JAMonAdmin.jsp over the  last several days with lots of features like arraysql, variable formatting, 
 *      and variable reports.  Included LocaleContext for internationalization.
 * 2/16/06 - Commented code and worked on LocaleContext
 *   - have primary active and global active work at (the unit level), range level, not hte factory level.
 * 2/20/06 - Completed updates to JAMon user's guide
 * 2/25/06 - Added a few methods back due to backwards compatibility issues, and also added Eric's improvements to jamon admin page
 * 2/26/06 - Added all methods into MonitorFactory class that were part of JAMon 1.0.  Backwards compatibility of JAMon 2.0 for this 
 *   most used class should be ok.  The only difference that I can think of is that the getReport methods returned an exception before
 *   and now don't.
 *   - Changed some text in JAMonAdmin.jsp
 * 3/1/06 - Prepared for final release.   
 *- contact hsqldb, and don calderwood, jack shirazi
 - pause/resume - see email
 - persistance
 - add examples to help per email - utilities
 * - help file wording and erics changes
 * - left off in this class for review of code. MonitorFactory
 *
 */

import java.util.*;

public class MonitorFactory {

	private static MonitorFactoryInterface factory; // current factory
	private static MonitorFactoryInterface enabledFactory; // factory for enabled monitors
	private static MonitorFactoryInterface disabledFactory; // factory for disabled moniors
	private static MonitorFactoryInterface debugFactory;

	static {
		// enable the factory by default.
		factory = debugFactory = enabledFactory = new FactoryEnabled();
		disabledFactory = new FactoryDisabled(enabledFactory);
	}

	/** Get the current Factory (could be the enabled or disabled factory depending on what is enabled)
	 **/

	public static MonitorFactoryInterface getFactory() {
		return factory;
	}

	/** Returns the factory for creating debug monitors.  The debug factory can be disabled independently from the
	 * regular factory.  Debug monitors are no different than monitors returned by the regular monitor factory.
	 * However the debug factory can be used to monitor items in a test environment and disable them in production.
 *
	 * Sample Call: MonitorFactory.getDebugFactory().start();     
	 *     
	 */

	public static MonitorFactoryInterface getDebugFactory() {
		// both the regular factory (isEnabled()) and the debug factory must be enabled
		// in order to return the non null factory.

		if (isEnabled())
			return debugFactory;
		else
			return disabledFactory;
	}

	/** <p>Aggregate the passed in value with the monitor associated with the label, and the units. The aggregation tracks
	 * hits, avg, total, min, max and more.  Note the monitor returned is threadsafe.  However, it is best to get a monitor
	 * vi this method and not reuse the handle as TimeMonitors are not thread safe (see the getTimeMonitor method.</p>
	 *
 * <b>Sample call:</b><br>
	 * <blockquote><code><pre>
	 * Monitor mon=MonitorFactory.add("bytes.sent","MB", 1024);
	 *</pre></code></blockquote><br><br>
	 *
	 */

	public static Monitor add(String label, String units, double value) {
		return factory.add(label, units, value);
	}
	
    /** Used when you want to create your own key for the monitor.  This works similarly to a group by clause where the key is
     * any columns used after the group by clause.
     */
    public static Monitor add(MonKey key, double value) {
    	return factory.add(key, value);
    }

	/** <p>Return a timing monitor with units in milliseconds.  stop() should be called on the returned monitor to indicate the time
	 * that the process took. Note time monitors keep the starttime as an instance variable and so every time you want to use a TimeMonitor
	 * you should get a new instance. </p>
	 *

	 * <b>Sample call:</b><br>
	 * <blockquote><code><pre>
	 *  Monitor mon=MonitorFactory.start("pageHits");<br>
	 ...code being timed...<br>
	 mon.stop();
	 *
	 *</pre></code></blockquote><br><br>
	 *

	 */

	public static Monitor start(String label) {
		return factory.start(label);
	}

	/** <p>Return a timing monitor with units in milliseconds, that is not aggregated into the jamon stats.  stop() should be called on the returned monitor to indicate the time
	 * that the process took. Note time monitors keep the starttime as an instance variable and so every time you want to use a TimeMonitor
	 * you should get a new instance. </p>
	 *
	 * <b>Sample call:</b><br>
	 * <blockquote><code><pre>
	 *  Monitor mon=MonitorFactory.start();<br>
	 ...code being timed...<br>
	 mon.stop();
	 *
	 *</pre></code></blockquote><br><br>
	 *
	 */

	public static Monitor start() {
		return factory.start();
	}

	/** <p>Return a timing monitor with units in milliseconds, that is not aggregated into the jamon stats. The concept of primary allows
	 you to correlate performance of all monitors with the most resource intensive things the app does which helps you determine scalability.
	 </p>
	 *
	 * <b>Sample call:</b><br>
	 * <blockquote><code><pre>
	 *  Monitor mon=MonitorFactory.startPrimary("myPrimaryMonitor");<br>
	 ...code being timed...<br>
	 mon.stop();
	 *
	 *</pre></code></blockquote><br><br>
	 *
	 */

	public static Monitor startPrimary(String label) {
		return factory.startPrimary(label);
	}
	

	
    /** Start a monitor with the specified key and mark it as primary */
    public static Monitor startPrimary(MonKey key) {
    	return factory.startPrimary(key);
    }
    
	
	
    /** Start using the passed in key.  Note activity stats are incremented */
    public static Monitor start(MonKey key) {
    	return factory.start(key);
    }

	/** <p>Return the monitor associated with the label, and units.  All statistics associated with the monitor can then be accessed such
	 as hits, total, avg, min, and max. If the monitor does not exist it will be created.
	 </p>
	 *
	 * <b>Sample call:</b><br>
	 * <blockquote><code><pre>
	 *  Monitor mon=MonitorFactory.getMonitor("myPrimaryMonitor");<br>
	 *
	 *</pre></code></blockquote><br><br>
	 *
	 */

	public static Monitor getMonitor(String label, String units) {
		return factory.getMonitor(label, units);
	}
	
    /** Get the monitor associated with the passed in key.  It will be created if it doesn't exist */
    public static Monitor getMonitor(MonKey key) {
    	return factory.getMonitor(key);
    }

	/** <p>Return the time monitor associated with the label.  All statistics associated with the monitor can then be accessed such
	 as hits, total, avg, min, and max. If the monitor does not exist it will be created.
	 </p>
	 *
	 * <b>Sample call:</b><br>
	 * <blockquote><code><pre>
	 *  Monitor mon=MonitorFactory.getTimeMonitor("myPrimaryMonitor");<br>
	 *
	 *</pre></code></blockquote><br><br>
	 *
	 */
	public static Monitor getTimeMonitor(String label) {
		return factory.getTimeMonitor(label);
	}
	
    /** Get the time monitor associated with the passed in key.  It will be created if it doesn't exist.  The units
     * are in ms.*/
   public static Monitor getTimeMonitor(MonKey key) {
	   return factory.getTimeMonitor(key);
   }

	/** <p>Determine if the monitor associated with the label, and the units currently exists.  
	 </p>
	 *
	 * <b>Sample call:</b><br>
	 * <blockquote><code><pre>
	 *  Monitor mon=MonitorFactory.getTimeMonitor("myPrimaryMonitor");<br>
	 *
	 *</pre></code></blockquote><br><br>
 *
	 */

	public static boolean exists(String label, String units) {
		return factory.exists(label, units);
	}
	
	
    /** Return true if the monitor associated with the passed in key exists */
    public static boolean exists(MonKey key) {
    	return factory.exists(key);
    }

	/** <p>Return the composite monitor (a collection of monitors) associated with the passed in units. Note in JAMon 1.0 this
	 *  method would take a lable and would return all monitors that matched that criterion.   This ability is now better performed
	 *  using ArraySQL from the FormattedDataSet API.  See JAMonAdmin.jsp for an example.
	 </p>

	 *
	 * <b>Sample call:</b><br>
	 * <blockquote><code><pre>
	 *  Monitor mon=MonitorFactory.getComposite("ms.");<br>
	 *  mon=MonitorFactory.getComposite("allMonitors");<br>
	 *
	 *</pre></code></blockquote><br><br>
	 *
	 */

	public static MonitorComposite getComposite(String units) {
		return factory.getComposite(units);
	}

	/**
	 * This returns the number of monitors in this factory.
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	public static int getNumRows() {
		return factory.getNumRows();
	}

	/**
	 * Return the header for displaying what ranges are available.
	 * 
	 */

	public static String[] getRangeHeader() {
		return factory.getRangeHeader();
	}

	/**
	 * Return the ranges in this factory.
	 * 
	 */

	public static Object[][] getRangeNames() {
		return factory.getRangeNames();
	}

	/** Return the composite monitor of all monitors for this factory */

	public static MonitorComposite getRootMonitor() {
		return factory.getRootMonitor();
	}

	/** Return the version of JAMon */

	public static String getVersion() {
		return factory.getVersion();
	}

	/** Remove/delete the specified monitor */

	public static void remove(String label, String units) {
		factory.remove(label, units);
	}
	
    /** Remove the monitor associated with the passed in key */
    public static void remove(MonKey key) {
    	factory.remove(key);
    }

	/**
	 * Use the specified map to hold the monitors. This map should be
	 * threadsafe. This allows for the use of a faster map than
	 * 
	 * the default synchronzied HashMap()
	 * 
	 */

	public static void setMap(Map map) {
		factory.setMap(map);
	}

	/**
	 * Associate a range with a key/unit. Any monitor with the given unit will
	 * have this range. Any monitor with
	 * 
	 * no range associated with its unit will have no range.
	 * 
	 */

	public static void setRangeDefault(String key, RangeHolder rangeHolder) {
		factory.setRangeDefault(key, rangeHolder);
	}

	/**
	 * Enable/Disable MonitorFactory. When enabled (true) the factory returns
	 * monitors that store aggregate stats. When disabled (false)
	 * 
	 * null/noop monitors are returned. enable()/disable() can also be used to
	 * perform the same function
	 * 
	 * 
	 * 
	 */

	public static void setEnabled(boolean enable) {
		if (enable)
			factory = enabledFactory;
		else
			factory = disabledFactory;

	}

	/**
	 * 
	 * Enable or disable the debug factory. The debug factory can be
	 * enabled/disabled at runtime. Calling this method with a false
	 * 
	 * also disables calls to MonitorFactory.getDebugFactory(int
	 * debugPriorityLevel)
	 * 
	 * 
	 * 
	 * Sample Call:
	 * 
	 * MonitorFactory.setDebugEnabled(false);
	 * 
	 * MonitorFactory.getDebugFactory().start(); // no stats are gathered.
	 * 
	 */

	public static void setDebugEnabled(boolean enable) {
		if (enable)
			debugFactory = enabledFactory;
		else
			debugFactory = disabledFactory;

	}

	/**
	 * Enable MonitorFactory. When enabled the factory returns monitors that
	 * store aggregate stats. This method has the same
	 * 
	 * effect as calling MonitorFactor.setEnabled(true).
	 * 
	 */

	public static void enable() {
		setEnabled(true);
	}

	/**
	 * Disable MonitorFactory. When disabled the factory returns null/noop
	 * monitors. This method has the same
	 * 
	 * effect as calling MonitorFactor.setEnabled(true).
	 * 
	 */

	public static void disable() {
		setEnabled(false);
	}

	/**
	 * Is the MonitorFactory currently enabled?
	 * 
	 */

	public static boolean isEnabled() {
		return (factory == enabledFactory) ? true : false;
	}

	/**
	 * Is the Debug Monitor Factory currently enabled?
	 * 
	 */

	public static boolean isDebugEnabled() {
		return (debugFactory == enabledFactory) ? true : false;
	}

	/**
	 * Reset/remove all monitors. If the factory is disabled this method has no
	 * action.
	 */

	public static void reset() {

		if (isEnabled())
			enabledFactory = factory = new FactoryEnabled();
	}


	/**
	 * This returns the header for basic data with no range info in the header.
	 * This method is deprecated. use the methods associated
	 * 
	 * with the CompositeMonitor. The various getXXXHeader() methods of
	 * CompositeMonitors can return this information and more.
	 * 
	 * 
	 * 
	 * 
	 */

	public static String[] getHeader() {
		return factory.getRootMonitor().getBasicHeader();
	}

	/**
	 * This returns the data for basic data with no range info.
	 * 
	 * The various getXXXData() methods of CompositeMonitors can return this
	 * information and more.
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	public static Object[][] getData() {
		return factory.getRootMonitor().getBasicData();
	}

	/**
	 * This returns an HTML report for basic data with no range info in the
	 * header.
	 * 
	 */

	public static String getReport() {
		return factory.getRootMonitor().getReport();
	}

	/**
	 * This returns an HTML report for basic data with no range info in the
	 * header for the past in units.
	 * 
	 * This method will be removed in the next release.
	 */

	public static String getReport(String units) {
		return getComposite(units).getReport();
	}


	/** return a test range holder */

	private static RangeHolder getTestHolder() {

		RangeHolder rh = new RangeHolder();
		rh.add("10_display", 10);
		rh.add("20_display", 20);
		rh.add("30_display", 30);
		rh.add("40_display", 40);
		rh.add("50_display", 50);
		rh.add("60_display", 60);
		rh.add("70_display", 70);
		rh.add("80_display", 80);
		rh.add("90_display", 90);
		rh.add("100_display", 100);
		rh.add("110_display", 110);
		rh.add("120_display", 120);
		rh.add("130_display", 130);
		rh.add("140_display", 140);
		rh.add("150_display", 150);
		// note last range is always called lastRange and is added automatically
		return rh;

	}

	public static void main(String[] args) {

		Monitor timer = MonitorFactory.start("totaltime");
		for (int i = 1; i <= 10; i++) {
			System.out.println(MonitorFactory.add("NIC.bytes.sent", "bytes", i * 1000));
			System.out.println(MonitorFactory.add("negativetest", "neg", -1000.0 * i));
		}

		System.out.println("");
		Monitor m = null;
		m = MonitorFactory.start("purchasesTimeTestNoRange");

		for (int i = 1; i <= 1000000; i++)
			MonitorFactory.add("purchasesNoRange", "dollars", 1000.0);

		System.out.println("purchasesTimeTestNoRange=" + m.stop().getTotal());

		m = MonitorFactory.start("testTimerTimeTest");
		for (int i = 1; i <= 1000000; i++)
			MonitorFactory.start("testTimer").stop();

		System.out.println("testTimerTimeTest=" + m.stop().getTotal());
		for (int i = -5; i <= 20; i++) {
			MonitorFactory.add("purchases", "dollars", i * 50);

		}

		System.out.println("");

		System.out.println(MonitorFactory.add("NIC.bytes.received", "bytes", 250.0));
		System.out.println(MonitorFactory.add("NIC.bytes.received", "bytes", 250.0));

		timer.stop();

		for (int i = -5; i < 25; i++)
			MonitorFactory.add("timetest", "ms.", i * 5);

		System.out.println(MonitorFactory.getMonitor("purchases", "dollars").getHits());
		System.out.println(MonitorFactory.getTimeMonitor("testTimer").getHits());
        // case sensitive so won't print
		System.out.println(MonitorFactory.getTimeMonitor("testtimer").getHits());
		System.out.println("Total time=" + timer.getTotal());

		MonitorFactory.reset();
		MonitorFactory.setRangeDefault("dollars", getTestHolder());
		m = MonitorFactory.start("purchasesTimeTestRange");
		for (int i = 1; i <= 1000000; i++)
			MonitorFactory.add("purchasesRange", "dollars", 1000.0);

		System.out.println("purchasesTimeTestRange=" + m.stop().getTotal());
		Object[][] data = null;
		MonitorFactory.setRangeDefault("bytes", getTestHolder());
		MonitorFactory.setRangeDefault("cents", getTestHolder());
		MonitorFactory.setRangeDefault("minutes", getTestHolder());
		MonitorFactory.setRangeDefault("MB", getTestHolder());
		MonitorFactory.setRangeDefault("KB", getTestHolder());
		MonitorFactory.setRangeDefault("points", getTestHolder());

		String[] header = MonitorFactory.getComposite("ms.").getHeader();

		data = MonitorFactory.getComposite("ms.").getData();
		header = MonitorFactory.getComposite("ms.").getBasicHeader();

		data = MonitorFactory.getComposite("ms.").getBasicData();
		header = MonitorFactory.getComposite("ms.").getDisplayHeader();

		data = MonitorFactory.getComposite("ms.").getDisplayData();
		MonitorFactory.getComposite("ms.").disable();

		header = MonitorFactory.getComposite("ms.").getHeader();
		data = MonitorFactory.getComposite("ms.").getData();
        
		System.out.println("header length="+header.length+", data length="+data.length);
		System.out.println("JAMon Version=" + MonitorFactory.getVersion());

	}

}
