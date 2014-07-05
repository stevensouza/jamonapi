package com.jamonapi;
import java.util.Iterator;
import java.util.Map;

/**
 * Static MonitorFactory that is good to use in most cases.  In the situation
 * when you want a different factory then instanciated FactoryEnabled directly.
 * Note this is mostly a wrapper for FactoryEnabled and FactoryDisabled. You can also
 * get the underlying factory with a call to getFactory()
 */
public class MonitorFactory {

    public  static final String EXCEPTIONS_LABEL="com.jamonapi.Exceptions";
    private static MonitorFactoryInterface factory; // current factory
    private static MonitorFactoryInterface enabledFactory; // factory for enabled monitors
    private static MonitorFactoryInterface disabledFactory; // factory for disabled monitors
    private static MonitorFactoryInterface debugFactory;

    static {
        // enable the factory by default.
        init();
    }

    /** Get the current Factory (could be the enabled or disabled factory depending on what is enabled) */
    public static MonitorFactoryInterface getFactory() {
        return factory;
    }

    /** Returns the factory for creating debug monitors.  The debug factory can be disabled independently from the
     * regular factory.  Debug monitors are no different than monitors returned by the regular monitor factory.
     * However the debug factory can be used to monitor items in a test environment and disable them in production.
     *
     * <p>Sample Call: MonitorFactory.getDebugFactory().start();
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

    /** Aggregate the passed in value with the monitor associated with the label, and the units. The aggregation tracks
     * hits, avg, total, min, max and more.  Note the monitor returned is threadsafe.  However, it is best to get a monitor
     * vi this method and not reuse the handle as TimeMonitors are not thread safe (see the getTimeMonitor method.
     *
     * <p>Sample call:</p>
     * <pre>{@code
     * Monitor mon=MonitorFactory.add("bytes.sent","MB", 1024);
     * }</pre>
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

    /** Return a timing monitor with units in milliseconds.  stop() should be called on the returned monitor to indicate the time
     * that the process took. Note time monitors keep the starttime as an instance variable and so every time you want to use a TimeMonitor
     * you should get a new instance.
     *
     * <p>Sample call:</p>
     * <pre>{@code
     *  Monitor mon=MonitorFactory.start("pageHits");
     *   ...code being timed...
     *  mon.stop();
     * }</pre>
     *
     */

    public static Monitor start(String label) {
        return factory.start(label);
    }

    /** Return a timing monitor with units in milliseconds, that is not aggregated into the jamon stats.  stop() should be called on the returned monitor to indicate the time
     * that the process took. Note time monitors keep the starttime as an instance variable and so every time you want to use a TimeMonitor
     * you should get a new instance.
     *
     * <p>Sample call:</p>
     * <pre>{@code
     *  Monitor mon=MonitorFactory.start();
     *    ...code being timed...
     *  mon.stop();
     * }</pre>
     *
     */
    public static Monitor start() {
        return factory.start();
    }

    public static Monitor getMonitor() {
        return factory.getMonitor();
    }

    /** Return a timing monitor with units in milliseconds, that is not aggregated into the jamon stats. The concept of primary allows
     * you to correlate performance of all monitors with the most resource intensive things the app does which helps you determine scalability.
     *
     * <p>Sample call:</p>
     * <pre>{@code
     *  Monitor mon=MonitorFactory.startPrimary("myPrimaryMonitor");
     *    ...code being timed...
     *  mon.stop();
     * }</pre>
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


    /** start nanosecond timer */
    public static Monitor startNano(String label) {
        return factory.startNano(label);
    }

    /** Provide your own key to a nanosecond timer */
    public static Monitor startNano(MonKey key) {
        return factory.startNano(key);
    }

    /**Return the monitor associated with the label, and units.  All statistics associated with the monitor can then be accessed such
     * as hits, total, avg, min, and max. If the monitor does not exist it will be created.
     * 
     * <p>Sample call:</p>
     * <pre>{@code
     *  Monitor mon=MonitorFactory.getMonitor("myPrimaryMonitor");
     * }</pre>
     *
     */
    public static Monitor getMonitor(String label, String units) {
        return factory.getMonitor(label, units);
    }

    /** Get the monitor associated with the passed in key.  It will be created if it doesn't exist */
    public static Monitor getMonitor(MonKey key) {
        return factory.getMonitor(key);
    }

    /** Return the time monitor associated with the label.  All statistics associated with the monitor can then be accessed such
     * as hits, total, avg, min, and max. If the monitor does not exist it will be created.
     *
     * <p>Sample call:</p>
     * <pre>{@code
     *  Monitor mon=MonitorFactory.getTimeMonitor("myPrimaryMonitor");
     * }</pre>
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

    /** Determine if the monitor associated with the label, and the units currently exists.
     *
     * <p>Sample call:</p>
     * <pre>{@code
     *  Monitor mon=MonitorFactory.getTimeMonitor("myPrimaryMonitor");
     * }</pre>
     *
     */
    public static boolean exists(String label, String units) {
        return factory.exists(label, units);
    }


    /** Return true if the monitor associated with the passed in key exists */
    public static boolean exists(MonKey key) {
        return factory.exists(key);
    }

    /** Return the composite monitor (a collection of monitors) associated with the passed in units. Note in JAMon 1.0 this
     *  method would take a lable and would return all monitors that matched that criterion.   This ability is now better performed
     *  using ArraySQL from the FormattedDataSet API.  See JAMonAdmin.jsp for an example.
     * 
     * <p>Sample call:</p>
     * <pre>{@code
     *  Monitor mon=MonitorFactory.getComposite("ms.");
     *  mon=MonitorFactory.getComposite("allMonitors");
     * }</pre>
     *
     */
    public static MonitorComposite getComposite(String units) {
        return factory.getComposite(units);
    }

    /**  This returns the number of monitors in this factory. */
    public static int getNumRows() {
        return factory.getNumRows();
    }

    /**  Return the header for displaying what ranges are available. */
    public static String[] getRangeHeader() {
        return factory.getRangeHeader();
    }

    /**  Return the ranges in this factory. */
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

    /**  Use the specified map to hold the monitors. This map should be  threadsafe. This allows for the use
     *  of a faster map than the default: synchronzied HashMap()
     */
    public static void setMap(Map map) {
        factory.setMap(map);
    }

    public static Map getMap() {
        return factory.getMap();
    }

    /** Associate a range with a key/unit. Any monitor with the given unit will
     * have this range. Any monitor with no range associated with its unit will have no range.
     */
    public static void setRangeDefault(String key, RangeHolder rangeHolder) {
        factory.setRangeDefault(key, rangeHolder);
    }

    /**
     * Enable/Disable MonitorFactory. When enabled (true) the factory returns monitors that store aggregate
     * stats. When disabled (false)  null/noop monitors are returned. enable()/disable() can also be used to
     * perform the same function
     */
    public static void setEnabled(boolean enable) {
        if (enable)
            factory = enabledFactory;
        else
            factory = disabledFactory;
    }

    /** Enable or disable the debug factory. The debug factory can be
     * enabled/disabled at runtime. Calling this method with a false  also disables
     * calls to MonitorFactory.getDebugFactory(int debugPriorityLevel)
     * 
     * <p>Sample Call:</p>
     * <pre>{@code
     *   MonitorFactory.setDebugEnabled(false);
     *   MonitorFactory.getDebugFactory().start(); // no stats are gathered.
     * }</pre>
     * 
     */
    public static void setDebugEnabled(boolean enable) {
        if (enable)
            debugFactory = enabledFactory;
        else
            debugFactory = disabledFactory;
    }

    /** Enable MonitorFactory. When enabled the factory returns monitors that
     * store aggregate stats. This method has the same effect as calling MonitorFactor.setEnabled(true).
     */
    public static void enable() {
        setEnabled(true);
    }

    /**  Disable MonitorFactory. When disabled the factory returns null/noop
     * monitors. This method has the same  effect as calling MonitorFactor.setEnabled(true).
     */
    public static void disable() {
        setEnabled(false);
    }

    /**  Is the MonitorFactory currently enabled? */
    public static boolean isEnabled() {
        return (factory == enabledFactory) ? true : false;
    }

    /**  Is the Debug Monitor Factory currently enabled?  */
    public static boolean isDebugEnabled() {
        return (debugFactory == enabledFactory) ? true : false;
    }

    public static boolean isGlobalActiveEnabled() {
        return factory.isGlobalActiveEnabled();
    }

    public static void enableGlobalActive(boolean enable) {
        factory.enableGlobalActive(enable);
    }

    /**  Reset/remove all monitors. If the factory is disabled this method has no  action. */
    public static void reset() {
        if (isEnabled())
            init();
    }

    private static void init() {
        // enable the factory by default.
        boolean isKeySizeTrackingEnabled= (factory==null) ? false : factory.isTotalKeySizeTrackingEnabled();
        factory = debugFactory = enabledFactory = new FactoryEnabled();
        if (isKeySizeTrackingEnabled) {
            factory.enableTotalKeySizeTracking();
        }

        disabledFactory = new FactoryDisabled(enabledFactory);
    }


    /** This returns the header for basic data with no range info in the header.
     * This method is deprecated. use the methods associated with the CompositeMonitor.
     * The various getXXXHeader() methods of CompositeMonitors can return this information and more.
     */
    @Deprecated
    public static String[] getHeader() {
        return factory.getRootMonitor().getBasicHeader();
    }

    /** This returns the data for basic data with no range info. The various getXXXData() methods of
     * CompositeMonitors can return this information and more.
     */
    public static Object[][] getData() {
        return factory.getRootMonitor().getBasicData();
    }

    /**  This returns an HTML report for basic data with no range info in the header.
     */
    public static String getReport() {
        return factory.getRootMonitor().getReport();
    }

    /**  This returns an HTML report for basic data with no range info in the header for the past in units.
     * This method will be removed in the next release.
     */
    public static String getReport(String units) {
        return getComposite(units).getReport();
    }

    /** Iterator that contains Monitor's that are in this factory */
    @Deprecated
    public static Iterator iterator() {
        return factory.iterator();
    }


    public static void enableActivityTracking(boolean enable) {
        factory.enableActivityTracking(enable);
    }

    public static boolean isActivityTrackingEnabled() {
        return factory.isActivityTrackingEnabled();
    }

    /**
     * Set the maximum number of monitors that JAMon can store.  This can be set to reduce the jamon memory footprint.  By default
     * JAMon's size is unlimited.
     * 
     * @param maxMonitors maximum number of monitors that JAMon can store.
     */
    public static void setMaxNumMonitors(int maxMonitors) {
        factory.setMaxNumMonitors(maxMonitors);
    }

    /** Get the maximum number of monitors that JAMon can store. */
    public static int getMaxNumMonitors() {
        return factory.getMaxNumMonitors();
    }

    /**  Call this method if you want to be able to call MonitorFactory.getTotalKeySize() to determine
     * the size of all labels/keys in JAMon.
     */
    public static void enableTotalKeySizeTracking() {
        factory.enableTotalKeySizeTracking();
    }

    /**  Call this method if you want to disable key size tracking. */
    public static void disableTotalKeySizeTracking() {
        factory.disableTotalKeySizeTracking();
    }

    /**
     * @return true if totalKeySizeTracking is enabled.
     */
    public static boolean isTotalKeySizeTrackingEnabled() {
        return factory.isTotalKeySizeTrackingEnabled();
    }

    /** Returns the total key sizes which is the size of all String lables stored in JAMon.  This can be helpful to trigger a call to reset if
     * the amount of memory consumed gets too high.
     * 
     * @return Size of all monitor label strings
     */
    public static long getTotalKeySize() {
        return factory.getTotalKeySize();
    }

    /** Set the maximum size for a sql statement.  Sql bigger than this will be truncated before a JAMon label is created via the JAMon JDBC proxy driver.
     * This is useful to limit the size in memory that jamon occupies due to really long sql statements.  For backwards compatibility The default is to not
     * limit the statement size.
     * 
     * @param size
     */
    public static void setMaxSqlSize(int size) {
        factory.setMaxSqlSize(size);
    }

    /** Return the size that sql statements can be before truncating them.  This will limit the size of JAMon labels for sql statements
     * 
     * @return returns the max size that sql statements are allowed to be
     */
    public static int getMaxSqlSize() {
        return factory.getMaxSqlSize();
    }

}
