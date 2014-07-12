package com.jamonapi;

import com.jamonapi.utils.DetailData;
import com.jamonapi.utils.Misc;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/** Factory that creates Monitors.  Main workhorse for creating monitors. This can be created directly.  {@code MonitorFactory} is simply
 * a wrapper that makes calling this class simpler.   {@code MonitorFactory} contains a static reference to
 * a {@code FactoryEnabled} class.
 * 
 * @author steve souza
 *
 */

public class FactoryEnabled implements MonitorFactoryInterface {

    /** Creates a new instance of MonFactoryEnabled.  Also initializes the standard
     * JAMon time monitor range (ms.)
     */
    public FactoryEnabled() {
        initialize();
    }

    // note the default capacity for a HashMap is 16 elements with a load factor of
    // .75.  This means that after 12 elements have been loaded the HashMap doubles,
    // and doubles again at 24 etc.  By setting the HashMap to a higher value it need not
    // grow as often.  You can also have jamon use a different map type via the setMap method.
    private Map map;
    private Counter allActive;
    private Counter primaryActive;
    private boolean activityTracking=false;
    private RangeFactory rangeFactory;// Builds Range objects
    private GetMonitor getMonitor;
    private int maxMonitors;
    private AtomicLong totalKeySize;
    private int maxSqlSize;

    private static final boolean PRIMARY=true;
    private static final boolean NOT_PRIMARY=false;
    private static final boolean TIME_MONITOR=true;
    private static final boolean NOT_TIME_MONITOR=false;
    private static final int DEFAULT_MAP_SIZE=500;
    private static final NullMonitor NULL_MON=new NullMonitor();// used when disabled

    private synchronized void initialize() {
        allActive=new Counter();
        primaryActive=new Counter();
        rangeFactory=new RangeFactory();// Builds Range objects
        activityTracking=false;

        setRangeDefault("ms.", RangeHolder.getMSHolder());
        setRangeDefault("percent", RangeHolder.getPercentHolder());
        setMap(Misc.createConcurrentMap(DEFAULT_MAP_SIZE));

        if (isTotalKeySizeTrackingEnabled()) {
            enableTotalKeySizeTracking();
        }
    }

    public Monitor add(MonKey key, double value) {
        return getMonitor(key).add(value);
    }

    public Monitor add(String label, String units, double value) {
        return getMonitor(new MonKeyImp(label, units)).add(value);
    }

    public Monitor start(MonKey key) {
        return getTimeMonitor(key, NOT_PRIMARY).start();
    }

    public Monitor start(String label) {
        return getTimeMonitor(new MonKeyImp(label, "ms."), NOT_PRIMARY).start();
    }

    public Monitor startNano(String label) {
        return getNanoMonitor(new MonKeyImp(label, "ns.")).start();
    }

    public Monitor startNano(MonKey key) {
        return getNanoMonitor(key).start();
    }

    public Monitor startPrimary(MonKey key) {
        return getTimeMonitor(key, PRIMARY).start();
    }

    public Monitor startPrimary(String label) {
        return getTimeMonitor(new MonKeyImp(label, "ms."), PRIMARY).start();
    }

    public Monitor start() {
        return new TimeMon2().start();
    }

    public Monitor getMonitor() {
        return new MonitorImp(new MonKeyImp("defaultMon","defaultMon"), null, new ActivityStats(), false);
    }

    /** allows for using a faster/open source map.  */
    public void setMap(Map map) {
        // assign this map first.  As there could be synchronization problems if the map is first assigned
        // and the ConcurrentHashMap's GetMonitor is used for a standard map.  If the object is a ConcurrentHashMap
        // then the GetMonitor will be reassigned below.  GetMonitorMap works with any kind of map whereas
        // GetMonitor only works with a ConcurrentHashMap
        getMonitor=new GetMonitorMap();
        this.map=map;
        // If a ConcurrentHashMap is used the GetMonitor class has better concurrency as it does not need to be
        // synchronized.
        if (map instanceof ConcurrentHashMap)
            getMonitor=new GetMonitor();
    }

    public Monitor getMonitor(MonKey key) {
        return getMonitor(key, NOT_PRIMARY, NOT_TIME_MONITOR);
    }


    public Monitor getMonitor(String label, String units) {
        return getMonitor(new MonKeyImp(label, units));
    }

    public Monitor getTimeMonitor(MonKey key) {
        return getTimeMonitor(key, NOT_PRIMARY);
    }

    /** Note this creates 2 exception monitors and the more specific one is returned */
    @Override
    public Monitor addException(Monitor mon, Throwable throwable) {
      String stackTtrace = new StringBuffer("stackTrace=")
         .append(Misc.getExceptionTrace(throwable))
         .toString();
      if (mon!=null) {
          MonKey key = mon.getMonKey();
          key.setDetails(stackTtrace);
      }

      MonitorFactory.add(new MonKeyImp(MonitorFactory.EXCEPTIONS_LABEL, stackTtrace, "Exception"), 1);
      return MonitorFactory.add(new MonKeyImp(throwable.getClass().getName(), stackTtrace, "Exception"), 1);
    }

    @Override
    public Monitor addException(Throwable throwable) {
        return addException(null, throwable);
    }


    public Monitor getTimeMonitor(String label) {
        return getTimeMonitor(new MonKeyImp(label, "ms."), NOT_PRIMARY);
    }


    // Range methods

    // logical should be < or <= to determine how to use range values.
    // if it is neither it defaults to <=
    /** Note if a null is passed in it will have the same effect as an empty
     * RangeHolder (i.e. it will perform null operations)
     */
    public void setRangeDefault(String key, RangeHolder rangeHolder) {
        RangeImp range=null;
        if (rangeHolder!=null && rangeHolder.getSize()>0)
            range=new RangeBase(rangeHolder);

        rangeFactory.setRangeDefault(key, range );
    }

    public String[] getRangeHeader() {
        return rangeFactory.getHeader();
    }

    public Object[][] getRangeNames() {
        return rangeFactory.getData();
    }

    public void remove(MonKey key) {
        map.remove(key);
        if (totalKeySize!=null) {
            decrementKeySize(key.getSize());
        }
    }


    public void remove(String label, String units) {
        remove(new MonKeyImp(label, units));
    }


    public boolean exists(MonKey key) {
        return map.containsKey(key);
    }


    public boolean exists(String label, String units) {
        return map.containsKey(new MonKeyImp(label, units));
    }


    public int getNumRows() {
        return map.size();
    }


    // MonitorComposite methods
    /** getComposite("AllMonitors") is the same as getRootMonitor() */
    public MonitorComposite getRootMonitor() {
        return new MonitorComposite(getMonitors());
    }

    /** Pass in the units (or range type) and return all monitors of that
     * type.  'AllMonitors' is a special argument returns a composite of surprise surprise all monitors
     *getComposite("AllMonitors") is the same as getRootMonitor() ;
     **/
    public MonitorComposite getComposite(String units) {
        return getRootMonitor().filterByUnits(units);
    }

    public String getVersion() {
        return VERSION;
    }

    /** Returns the total of all keys in the monitor map. If there are no entries or if tracking
     * is disabled then 0 is returned
     */
    public long getTotalKeySize() {
        return isTotalKeySizeTrackingEnabled() ? totalKeySize.get() : 0;
    }


    // INTERNALS/PRIVATES FOLLOW
    private MonitorImp getTimeMonitor(MonKey key, boolean isPrimary) {
        return getMonitor(key, isPrimary, TIME_MONITOR);
    }

    /***This was changed 11/24/06 to fix syncronization problems reported as bug */
    private MonitorImp getMonitor(MonKey key, boolean isPrimary, boolean isTimeMonitor) {
        MonitorImp mon=getMonitor.getMon(key, isPrimary, isTimeMonitor);
        if (mon.isEnabled()) {
            mon = (isTimeMonitor) ?  new TimeMon(key, mon.getMonInternals()) : new DecoMon(key, mon.getMonInternals());
        }

        return mon;
    }


    private MonitorImp getNanoMonitor(MonKey key) {
        boolean isPrimary=false;
        boolean isTimeMon=true;
        MonitorImp mon=getMonitor.getMon(key, isPrimary, isTimeMon);
        if (mon.isEnabled()) {
            mon = new TimeMonNano(key, mon.getMonInternals());
        }

        return mon;
    }


    private MonitorImp createMon(MonKey key, boolean isPrimary, boolean isTimeMonitor)  {
        ActivityStats activityStats=new ActivityStats(new Counter(), primaryActive, allActive);
        // get default range for this type and assign it to the monitor
        RangeImp range=rangeFactory.getRangeDefault(key.getRangeKey(), activityStats);
        MonitorImp mon=new MonitorImp(key, range, activityStats, isTimeMonitor);
        // activity tracking is off by default.
        if (isTotalKeySizeTrackingEnabled()) {
            incrementKeySize(key.getSize());
        }
        if (activityTracking) {
            mon.setActivityTracking(activityTracking);
        }

        mon.setPrimary(isPrimary);
        return mon;
    }


    private void incrementKeySize(int keySize) {
        totalKeySize.addAndGet(keySize);
    }

    private void decrementKeySize(int keySize) {
        totalKeySize.addAndGet(-keySize);
    }


//    private MonitorImp[] getMonitors(String units) {
//        MonitorImp[] monitors=getMonitors();
//        if (monitors==null || units==null)
//            return null;
//        else if ("AllMonitors".equalsIgnoreCase(units))
//            return monitors;
//
//        List rows=new ArrayList(500);
//
//        int size=monitors.length;
//        for (int i=0;i<size;i++) {
//            // if units of range match units of this monitor then
//            if (units.equalsIgnoreCase(monitors[i].getMonKey().getRangeKey()))
//                rows.add(monitors[i]);
//        }
//
//        if (rows.size()==0)
//            return null;
//        else
//            return (MonitorImp[]) rows.toArray(new MonitorImp[0]);
//
//    }

    private Collection getAllMonitors() {
        Collection monitors=map.values();
        if (monitors==null || monitors.size()==0)
            return null;
        else
            return monitors;

    }


    private MonitorImp[] getMonitors() {
        Collection monitors=getAllMonitors();
        if (monitors==null || monitors.size()==0)
            return null;
        else
            return (MonitorImp[]) monitors.toArray(new MonitorImp[0]);
    }


    private MonitorImp getExistingMonitor(MonKey key) {
        return (MonitorImp) map.get(key);
    }

    private void putMon(MonKey key, MonitorImp mon) {
        map.put(key, mon);
    }



    /** Builds ranges */
    private static class RangeFactory  implements DetailData {
        private static final long serialVersionUID = 278L;
        private Map rangeFactoryMap=Misc.createConcurrentMap(50);


        private void setRangeDefault(String key, RangeImp range) {
            rangeFactoryMap.put(key, range);
            // at this point we could set primary and all active counters
        }


        /** null or range doesn't exists returns a null range object */
        private RangeImp getRangeDefault(String key, ActivityStats activityStats) {
            RangeImp range=(RangeImp) rangeFactoryMap.get(key);
            if (range!=null)
                range=range.copy(activityStats);

            return range;

        }

        public String[] getHeader() {
            return new String[]{"RangeName"};
        }

        public Object[][] getData() {
            return getRangeNames(getSortedRangeNames());
        }

        // Returns a choice of ranges.
        private Object[][] getRangeNames(Object[] rangeNames) {
            int len=(rangeNames==null) ? 0 : rangeNames.length;
            Object[][] data=new Object[len+1][];
            // always populate first entry with 'AllMonitors'
            data[0]=new Object[] {"AllMonitors"};

            for (int i=0;i<len;i++)
                data[i+1]=new Object[]{rangeNames[i]};

            return data;

        }

        // returns sorted array of all the unit types
        private Object[] getSortedRangeNames() {
            Object[] rangeNames=rangeFactoryMap.keySet().toArray();

            if (rangeNames==null)
                return null;
            else {
                Arrays.sort(rangeNames);
                return rangeNames;
            }
        }


    }




    /** Wipe out existing jamon data.  Same as instantiating a new FactoryEnabled object. */
    public void reset() {
        initialize();
    }

    public void enableGlobalActive(boolean enable) {
        allActive.enable(enable);

    }
    public boolean isGlobalActiveEnabled() {
        return allActive.isEnabled();
    }


    /**
     * The iterator returns Monitor objects only.  However, the MonKey can also be retrieved by calling {@code mon.getMonKey()}.
     * Alternatively the backing map can be retrieved and either the MonKey or Monitor can be iterated separately.
     *
     * @deprecated
     */
    @Deprecated
    public Iterator iterator() {
        return map.values().iterator();
    }

    public Map getMap() {
        return map;
    }

    public void enableActivityTracking(boolean enable) {
        this.activityTracking=enable;
        // enable/disable any already created monitors
        Monitor[] monitors=getMonitors();
        int len=(monitors==null) ? 0 : monitors.length;
        for (int i=0;i<len;i++)
            monitors[i].setActivityTracking(enable);
    }

    public boolean isActivityTrackingEnabled() {
        return activityTracking;
    }

    public int getMaxNumMonitors() {
        return maxMonitors;
    }

    public void setMaxNumMonitors(int maxMonitors) {
        this.maxMonitors=maxMonitors;

    }

    // returns a monitor indicating the max number of monitors has been reached if the threshold has been reached.
    private boolean monitorThresholdReached() {
        return (maxMonitors>0 && map.size()>=maxMonitors);
    }


    // class used to get data from a ConcurrentHashMap need not be synchronized
    private class GetMonitor {

        protected MonitorImp getMon(MonKey key, boolean isPrimary, boolean isTimeMonitor) {
            // note using MonKey over String concatenation doubled the speed
            // of the code, and was only slightly slower than using just the label
            // as the key
            MonitorImp mon=getExistingMonitor(key);
            // There is a chance of 2 threads going into the next code simultaneously, but this can be handled in
            // a thread safe manner via the following code with a ConcurrentHashMap
            if (mon==null) {
                if (monitorThresholdReached()) {
                    return NULL_MON;
                }
                // create the monitor.  There is a chance that between the check for mon==null above and
                // the call to putIfAbsent that another thread created the monitor already.  If so then
                // a nonnull value will be returned by putIfAbsent.  In this case (rare) use the value
                // returned by putIfAbsent and not the monitor returned by createMon.
                mon=createMon(key, isPrimary, isTimeMonitor);
                MonitorImp tempMon=(MonitorImp) ((ConcurrentHashMap) map).putIfAbsent(key, mon);
                if (tempMon!=null)
                    mon=tempMon;
            }

            return mon;
        }

    }

    // Note the method must be synchronized due to the use of a regular map.  It is better to use the
    // concurrentMap implementation above.
    private class GetMonitorMap extends GetMonitor {

        @Override
        protected synchronized  MonitorImp getMon(MonKey key, boolean isPrimary, boolean isTimeMonitor) {
            // note using MonKey over String concatenation doubled the speed
            // of the code, and was only slightly slower than using just the label
            // as the key
            MonitorImp mon=getExistingMonitor(key);
            // chance of 2 threads going into the next code simultaneously
            if (mon==null) {
                if (monitorThresholdReached()) {
                    return NULL_MON;
                }

                mon=createMon(key, isPrimary, isTimeMonitor);
                putMon(key, mon);
            }

            return mon;
        }

    }


    public void enableTotalKeySizeTracking() {
        totalKeySize=new AtomicLong();
    }

    public void disableTotalKeySizeTracking() {
        totalKeySize=null;
    }

    public boolean isTotalKeySizeTrackingEnabled() {
        return  totalKeySize!=null;
    }

    public int getMaxSqlSize() {
        return maxSqlSize;
    }

    public void setMaxSqlSize(int size) {
        this.maxSqlSize=size;
    }


}
