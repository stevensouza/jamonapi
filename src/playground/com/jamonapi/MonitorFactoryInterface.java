package com.jamonapi;

/**
 * Interface used to create Monitors.  It is implemented by both FactoryEnabled and FactoryDisabled 
 * which allows for enabling/disabling monitors at run time.  A
 * Factory is a design concept that is described in the Gang of 4's design patterns.
 *
 * Note the factory will create a monitor if it doesn't exist and use an existing one if it 
 * does.
 * Created on January 29, 2006, 10:31 PM
 */



import java.util.*;

public interface MonitorFactoryInterface {
    
    /** Return a monitor with the given label and units.  Note label has an effect on what range is used.  If no range is 
      * associated with units then it will use the null range (i.e. no range)
     * 
     * Sample Call:  factory.add("com.fdsapi.MyException", "error", 1);
     */
    public Monitor add(String label, String units, double value);
    
    /** Used when you want to create your own key for the monitor.  This works similarly to a group by clause where the key is
     * any columns used after the group by clause.
     */
    public Monitor add(MonKey key, double value);
    
    /** Return a time monitor (the units are implied and are ms. Note activity stats are incremented*/
    public Monitor start(String label);
    /** Start using the passed in key.  Note activity stats are incremented */
    public Monitor start(MonKey key);
    /** Increment activity stats */
    public Monitor start();
    
    /** Start a time monitor and mark it as primary */
    public Monitor startPrimary(String label);
    /** Start a monitor with the specified key and mark it as primary */
    public Monitor startPrimary(MonKey key);
    
    /** Get the monitor associated with the passed in key.  It will be created if it doesn't exist */
    public Monitor getMonitor(MonKey key);
    /** Get the monitor with the passed in label, and units.  It will be created if it doesn't exist */
    public Monitor getMonitor(String label, String units);
    
    /** Get the time monitor associated with the passed in label.  It will be created if it doesn't exist.  The units
      * are in ms.*/
    public Monitor getTimeMonitor(String label);
    /** Get the time monitor associated with the passed in key.  It will be created if it doesn't exist.  The units
      * are in ms.*/
    public Monitor getTimeMonitor(MonKey key);
    
    /** Remove the monitor associated with the passed in label and units */
    public void remove(String label, String units);
    /** Remove the monitor associated with the passed in key */
    public void remove(MonKey key);

    /** Return true if the monitor associated with the passed in label and units exists */
    public boolean exists (String label, String units);
    /** Return true if the monitor associated with the passed in key exists */
    public boolean exists(MonKey key);

    /** Associate a Range mapping to any monitor that has a unit/key name that matches what is passed to key */
    public void setRangeDefault(String key, RangeHolder rangeHolder);
    /** Return the header associated with range names */
    public String[] getRangeHeader();
    /** Retun an array of range names.  This is dynamic based on what was passed to setRangeDefault */
    public Object[][] getRangeNames();
    

    /** Get the number of monitors in this factory */
    public int getNumRows();

    /** Get the root composite monitor that contains all monitors in this factory */
    public MonitorComposite getRootMonitor();
    /* Retun the composite monitor associated with the passed unit.  Note this method changed from jamon 1.0.  
     * Previously it took a regular expression that
    * was matched on the label column and now it looks for any monitors that have the given range/unit key 
    */
    public MonitorComposite getComposite(String units);
    
    /** Get JAMon's version.  Example:  2.0 */
    public String getVersion();
    
    /** Set the map that holds the monitors.  This could be used to aid jamon performance by passing in a high performance
     * Thread safe map such as open source projects and jdk 1.5 have */
    
    public void setMap(Map map);
    
    /** Reset jamon stats for this factory.  Like recreating the factory */
    public void reset();
    
    public boolean isGlobalActiveEnabled();
    public void enableGlobalActive(boolean enable);
    
}
