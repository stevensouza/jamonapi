package com.jamonapi;


import java.io.Serializable;

/**
 * Contains the data associated with a monitor. These internals can be passed
 * around and shared by other monitor instances that are tracking aggregate
 * stats for the same MonKey. It mostly acts as a Struct with the exception of
 * the reset() method.
 * 
 * Created on December 11, 2005, 10:19 PM
 */

final class MonInternals implements Serializable {

	private static final long serialVersionUID = -1687353465350155034L;
	
	/** seed value to ensure that the first value always sets the max */
    static final double MAX_DOUBLE = -Double.MAX_VALUE;
    /** seed value to ensure that the first value always sets the min */
    static final double MIN_DOUBLE = Double.MAX_VALUE;

    MonKey key;

    /** the total for all values */
    double total = 0.0;
    /** The minimum of all values */
    double min = MIN_DOUBLE;
    /** The maximum of all values */
    double max = MAX_DOUBLE;
    /** The total number of occurrences/calls to this object */
    double hits = 0.0;
    /** Intermediate value used to calculate std dev */
    double sumOfSquares = 0.0;
    /** The most recent value that was passed to this object */
    double lastValue = 0.0;
    /** The first time this object was accessed */
    long firstAccess = 0;
    /** The last time this object was accessed */
    long lastAccess = 0;
    /** Is this a time monitor object? Used for performance optimizations */
    boolean isTimeMonitor = false;


    /** jamon 2.4 from BaseMon enable/disable */
    boolean enabled = true;
    // affects whether primaryActive and allActive are called but not thisActive (this is called regardless of this setting)
    // tracking activity only effects ranges.  A regular monitors activity tracking is in a sense always on.  The range activity
    // tracks performance in each range as it compares to the primary active, all active and this active counters
    boolean trackActivity = false;
    String name = "";// for regular monitors empty. For range monitors
    // "Range1_"
    String displayHeader = "";// for regular monitors empty. rangeholder name
    // for ranges (i.e. 0_20ms)

    /** * added for jamon 2.4 from Mon. Note must reset() variables if I add new ones! */
    double maxActive = 0.0;
    double totalActive = 0.0;
    boolean isPrimary = false;
    boolean startHasBeenCalled = false;
    private ActivityStats activityStats;
    // from MonitorImp
    RangeImp range;
    double allActiveTotal; // used to calculate the average active total
    // monitors for this distribution
    double primaryActiveTotal;
    double thisActiveTotal; // used in activity tracking whereas totalActive is not. totalActive is always shown on the jamonadminpages whereas thisActiveTotal is only shown on ranges.

    private Listeners listeners;

    /** created from Monitor if it is needed */
    public Listeners createListeners() {
        listeners=new Listeners(this);
        return listeners;
    }

    public boolean hasListeners() {
        return (listeners!=null);
    }

    public boolean hasListener(int listenerType) {
        return (listeners!=null) && listeners.hasListener(listenerType);
    }

    /** No check for null objects.  If that is important first call hasListener(listenerType)
     * above
     * 
     * @param listenerType
     * @return
     */
    public JAMonListener getListener(int listenerType) {
        return listeners.getListenerType(listenerType).getListener();
    }

    public Listeners getListeners() {
        return listeners;
    }

    public void setActivityStats(ActivityStats stats) {
        this.activityStats=stats;
    }

    public ActivityStats getActivityStats() {
        return activityStats;
    }

    /** increment allActive and primaryActive (not this active as that is done every time) */
    public void incrementActivity() {
        if (trackActivity) {
            activityStats.allActive.increment();

            if (isPrimary)
                activityStats.primaryActive.increment();
        }
    }

    /** decrement allActive and primaryActive (not this active as that is done every time) */
    public void decrementActivity() {
        if (trackActivity) {
            if (isPrimary) {
                activityStats.primaryActive.decrement();
            }

            activityStats.allActive.decrement();
        }
    }

    public void  updateActivity() {
        // All these changes have to do with tracking activity in the ranges.
        if (trackActivity) {
            // total of this monitors active
            thisActiveTotal += activityStats.thisActive.getCount();
            // total of primary actives
            primaryActiveTotal += activityStats.primaryActive.getCount();
            // total of all monitors actives
            allActiveTotal += activityStats.allActive.getCount();
        }
    }

    /** done wheter activity tracking is on or off */
    public double incrementThisActive() {
        return activityStats.thisActive.incrementAndReturn();
    }

    public void decrementThisActive() {
        activityStats.thisActive.decrement();
    }

    public void stop(double active) {
        totalActive += active;// allows us to track the  average active for THIS instance.
        decrementThisActive();
        decrementActivity();
    }

    /** same as stop above but subtracts out the active that was calculated at the last start */
    public void skip() {
        decrementThisActive();
        decrementActivity();
    }


    public double getThisActiveCount() {
        return activityStats.thisActive.getCount();
    }

    public void setThisActiveCount(double value) {
        activityStats.thisActive.setCount(value);
    }


    /** This is called from monmanage.jsp and resets the monitor to defaults */
    public void reset() {
        // note you don't change enabled status although you can still reset.
        // don't change isPrimary
        hits = total = sumOfSquares = lastValue = 0.0;
        firstAccess = lastAccess = 0;
        min = MIN_DOUBLE;
        max = MAX_DOUBLE;
        startHasBeenCalled = false;
        trackActivity = false;

        listeners=null;

        // added from mon class
        maxActive = totalActive = 0.0;
        activityStats.thisActive.setCount(0);

        // added from frequencydistbase
        allActiveTotal = primaryActiveTotal = thisActiveTotal = 0;
        if (range != null) {
            range.reset();
        }
    }


}
