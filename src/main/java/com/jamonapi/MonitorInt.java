package com.jamonapi;

import java.util.Date;


interface MonitorInt  {

    /** Used in call to addListener(...). i.e. addListener(Monitor.MAX, ...).  Also used to return values from getObject(key).
     * For example mon.getValue("max"); */

    public static final String VALUE = "value";
    public static final String LASTVALUE = "lastvalue";
    public static final String MAX = "max";
    public static final String MIN = "min";
    public static final String MAXACTIVE = "maxactive";
    public static final String TOTAL = "total";
    public static final String AVG = "avg";
    public static final String HITS = "hits";
    public static final String STDDEV = "stddev";
    public static final String FIRSTACCESS = "firstaccess";
    public static final String LASTACCESS = "lastaccess";
    public static final String ACTIVE = "active";
    public static final String AVGACTIVE = "avgactive";

    public double getTotal();

    public void setTotal(double value);

    public double getAvg();

    public double getMin();

    public void setMin(double value);

    public double getMax();

    public void setMax(double value);

    public double getHits();

    public void setHits(double value);

    public double getStdDev();

    public void setFirstAccess(Date date);

    public Date getFirstAccess();

    public void setLastAccess(Date date);

    public Date getLastAccess();

    public double getLastValue();

    public void setLastValue(double value);
    /**
     * Returns any object that has a named key. For this object 'total' and
     * 'avg', and 'min' are valid. It is case insenstive.  Implementations may return other values
     * This allows values to be returned from a Monitor without having to cast.
     */
    public Object getValue(String key);


    /** Start a monitor.  This increments the active counter by one. Calling start is not
     * required.  If it is called stop should be called too. */
    public Monitor start();

    /** Stop a monitor.  The decrements the active counter by one.  Calling stop is
     *  required if start is called.
     */
    public Monitor stop();

    /**
     * Works simililarly to stop except the stats are not recorded.  The only action is to decrement active.
     * 
     * @return Monitor
     */
    public Monitor skip();

    /** This method adds a value to the monitor (and aggegates statistics on it)
     */
    public Monitor add(double value);

    /** reset all values in the monitor to their defaults */
    public void reset();

    /** enable the monitor.  If the monitor is enabled all other calls to the monitor
     * have an action
     */
    public void enable();

    /** Disable the monitor.  If a monitor is disabled all other calls to the monitor
     * are noops.
     **/
    public void disable();

    /** Is the monitor enabled. */
    public boolean isEnabled();

    /** Return the Range object associated with this monitor.  The range object is a compromise
   between saving all data or none
     */
    public Range getRange();

    /** Return the label associated with this monitor.  */
    public MonKey getMonKey();

    public double getActive();

    public void setActive(double value);

    public double getMaxActive();

    public void setMaxActive(double value);

    public void setTotalActive(double value);

    public double getAvgActive();

    public boolean isPrimary();

    /** Indicate that this a primary Monitor.  See www.jamonapi.com for an explanation of primary monitors **/
    public void setPrimary(boolean isPrimary);

    // Some jamon 2.4 introduced methods.  Mostly listener related.
    public ListenerType getListenerType(String listenerType);

    /** Returns true if this listenertype ('max', 'min', 'value', 'maxactive') has any listeners at all
     * 
     * @param listenerTypeName
     * @return boolean
     */
    public boolean hasListeners(String listenerTypeName);

    /** Introduced as a way to add listeners that allows for lazy initialization saving a fair amount of memory.  Note
     * a future enhancement would be to delete the Listeners object when all listeners are removed.
     * 
     * @since 2.71
     */
    public void addListener(String listenerTypeName, JAMonListener listener);

    /** Pass in a listenertype like 'max', 'min', 'value' and the listener name and true will be returned if the listener exists
     * 
     * @since 2.71
     */
    public boolean hasListener(String listenerTypeName, String listenerName);

    public void removeListener(String listenerTypeName, String listenerName);

    public boolean hasListeners();

    public void setActivityTracking(boolean trackActivity);

    public boolean isActivityTracking();

    public JAMonDetailValue getJAMonDetailRow();
}
