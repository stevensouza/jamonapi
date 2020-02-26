package com.jamonapi.distributed;

import com.jamonapi.*;
import com.jamonapi.utils.BufferList;
import com.jamonapi.utils.Misc;
import com.jamonapi.utils.SerializationUtils;

import java.io.Serializable;
import java.util.*;

/**
 * Combines multiple MonitorComposite objects into one by getting them from the @link JamonDataPersister.
 * <p>
 * Created by stevesouza on 8/16/14.
 */
public class MonitorCompositeCombiner {
    private JamonDataPersister persister;

    private static final String SUMMARY_LISTENER = "FIFOBufferInstanceSummary";

    public MonitorCompositeCombiner(JamonDataPersister persister) {
        this.persister = persister;
    }

    /**
     * Combine MonitorComposites returned by each of the instanceKeys into 1 MonitorComposite.
     *
     * @param instanceKeys
     * @return MonitorComposite
     */
    public MonitorComposite get(String... instanceKeys) {
        return append(getMonitorComposites(instanceKeys));
    }

    public MonitorComposite aggregate(String... instanceKeys) {
        return aggregate(getMonitorComposites(instanceKeys));
    }

    private List<MonitorComposite> getMonitorComposites(String[] instanceKeys) {
        List<MonitorComposite> monitorCompositeList = new ArrayList<MonitorComposite>();
        for (int i = 0; i < instanceKeys.length; i++) {
            MonitorComposite monitorComposite = persister.get(instanceKeys[i]);
            if (monitorComposite != null) {
                monitorCompositeList.add(monitorComposite);
            }
        }
        return monitorCompositeList;
    }


    /**
     * Combine multiple MonitorComposites into 1 MonitorComposite.
     *
     * @param monitorCompositeList
     * @return MonitorComposite
     */
    public MonitorComposite append(Collection<MonitorComposite> monitorCompositeList) {
        Date previousDate = null;
        Date finalDate = null; // assign the date of all the results as the most recent of all monitorComposite dates
        Iterator<MonitorComposite> iter = monitorCompositeList.iterator();
        // note 2 lists are used instead of a Map so if 2 instanceNames are the same (say 'local') each of them can be
        // retained.
        List<MonitorComposite> monitorCompositeResultsList = new ArrayList<MonitorComposite>();
        List<String> instanceNameList = new ArrayList<String>();

        while (iter.hasNext()) {
            MonitorComposite mc = iter.next();
            instanceNameList.add(mc.getInstanceName());
            monitorCompositeResultsList.add(mc);
            if (previousDate == null || mc.getDateCreated().after(previousDate)) {
                finalDate = mc.getDateCreated();
            }
            previousDate = mc.getDateCreated();
        }

        MonitorComposite mc = new MonitorCompositeIterator(monitorCompositeResultsList).toMonitorComposite().setDateCreated(finalDate);
        return mc.setInstanceName(Misc.getAsString(instanceNameList));
    }

    public MonitorComposite aggregate(Collection<MonitorComposite> monitorCompositeList) {
        FactoryEnabled factory = new FactoryEnabled();
        // get rid of following remove.  might not need it now with the following haslistener check.  also
        // wouldn't need to change autoload of exception
        // change hasListener below
        // configurable sizes and features
        // stddev average
        //  others too
        // break out merge and header functions somehow
        // tests
        // other jamonlisteners
        //        // next
//        //  - unit tests
//        //  - null and null date
//        //  - active stats?
//        //  - std dev?
//        //  - listeners/buffers
//        //    - save full monitor for each server?
//        //    - save numinstances as well as buffer of the names
//        //    - save most recent n.  configurable?
//        //    - save other buffers? max, min, ...
//        //  - log4j
//        //  - steps for jetty, automon, tomcat
        factory.remove(new MonKeyImp(MonitorFactory.EXCEPTIONS_LABEL, "Exception")); //??????
        // ???count monitorCompositeList and or add the instancename to the details - fifobuffer????
        //    MonitorFactory.add("com.jamonapi.instances", "count", monitorCompositeList.size());
        MonitorComposite mc = append(monitorCompositeList);
        Monitor[] monitors = mc.getMonitors();
        // 1) iterate data creating monitors and and setup listeners
        // 2) loop through a second time and merge monitor values and populate listeners (maybe done in same method)
        for (Monitor monitor : monitors) {
            // make a copy of the key as we have to change the instance name and don't want to change it for the local instance
            MonKey key = SerializationUtils.deepCopy(monitor.getMonKey());
            key.setInstanceName("aggregated");

            Monitor summaryMonitor = factory.getMonitor(key);
            if (!summaryMonitor.hasListener("value", SUMMARY_LISTENER)) {
                summaryMonitor.addListener("value", getSummaryFIFOBufferListener(summaryMonitor));
            }
            merge(monitor, summaryMonitor);
            addMonitorToSummaryFifoBuffer(summaryMonitor, monitor);
        }
        return factory.getRootMonitor();
    }

    // Each monitor from an instance will have its value saved for display in a fifo buffer.
    private void addMonitorToSummaryFifoBuffer(Monitor summaryMonitor, Monitor monitor) {
        JAMonBufferListener jaMonBufferListener = (JAMonBufferListener) summaryMonitor.getListenerType("value").getListener(SUMMARY_LISTENER);
        jaMonBufferListener.addRow(getRowData(monitor).toArray());
    }


    private JAMonBufferListener getSummaryFIFOBufferListener(Monitor mon) {
        // make this configurable from both size and whether to do or not.?????
        BufferList bufferList = new BufferList(getHeader(mon).toArray(new String[0]), 100);
        return new JAMonBufferListener(SUMMARY_LISTENER, bufferList);
    }

    /**
     * Header to be used in value listener fifo buffer for each instance monitor
     *
     * @param mon a monitor to calculate the header from. Note it is calculated with the first monitor only
     * @return header as a list
     */
    private List<String> getHeader(Monitor mon) {
        List<String> header = new ArrayList();
        mon.getMonKey().getHeader(header);
        header.add("Hits");
        header.add("Avg");
        header.add("Total");
        header.add("StdDev");
        header.add("LastValue");
        header.add("Min");
        header.add("Max");
        header.add("Active");
        header.add("AvgActive");
        header.add("MaxActive");
        header.add("FirstAccess");
        header.add("LastAccess");
        header.add("Enabled");
        header.add("Primary");
        header.add("HasListeners");
        return header;
    }

    /**
     * Convert the monitor to a list so it can be viewed in the jamon web app in a value listener
     * fifo buffer
     *
     * @param mon monitor to be converted to a row
     * @return A row version of the monitor
     */
    private List getRowData(Monitor mon) {
        List rowData = new ArrayList();
        mon.getMonKey().getRowData(rowData);
        rowData.add(mon.getHits());
        rowData.add(mon.getAvg());
        rowData.add(mon.getTotal());
        MonitorCompositeCombiner.StdDev stdDev = (MonitorCompositeCombiner.StdDev) mon.getMonKey().getDetails();
        rowData.add(stdDev.getAvgStdDev());
        rowData.add(mon.getLastValue());
        rowData.add(mon.getMin());
        rowData.add(mon.getMax());
        rowData.add(mon.getActive());
        rowData.add(mon.getAvgActive());
        rowData.add(mon.getMaxActive());
        rowData.add(mon.getFirstAccess());
        rowData.add(mon.getLastAccess());
        rowData.add(mon.isEnabled());
        rowData.add(mon.isPrimary());
        rowData.add(mon.hasListeners());
        return rowData;
    }


    /**
     * Remove any of the MonitorComposites associated with the key.  This data could be in memory,  on HazelCast
     * or in a file for example.
     *
     * @param instanceKey An instance to remove
     */
    public void remove(String... instanceKey) {
        for (String instance : instanceKey) {
            persister.remove(instance);
        }
    }

    /**
     * Take a monitor from one instance (from) and merge it with the ongoing totals for all the instances (to)
     *
     * @param from a monitor from a single instance (such as local, tomcat8_production etc)
     * @param to   the monitor where the aggregation of all single instances will be stored
     * @return the 'to' monitor for convenience.
     */
    Monitor merge(Monitor from, Monitor to) {
        to.setTotalActive(to.getAvgActive() * to.getHits() + from.getAvgActive() * from.getHits());
        to.setHits(to.getHits() + from.getHits());
        to.setTotal(to.getTotal() + from.getTotal());
        to.setMin(Math.min(to.getMin(), from.getMin()));
        to.setMax(Math.max(to.getMax(), from.getMax()));
        to.setMaxActive(Math.max(to.getMaxActive(), from.getMaxActive()));
        to.setActive(to.getActive() + from.getActive());
        to.setFirstAccess(Misc.min(to.getFirstAccess(), from.getFirstAccess()));// ?
        Date lastAccess = Misc.max(to.getLastAccess(), from.getLastAccess());
        to.setLastAccess(lastAccess);//?
        // need to do a date compare to use the max dates lastValue
        if (lastAccess != null && lastAccess.equals(from.getLastAccess())) {
            to.setLastValue(from.getLastValue());
        }

        calculateStdDev(from, to);
        to.setPrimary(to.isPrimary() || from.isPrimary());
        return to;
    }

    private void calculateStdDev(Monitor from, Monitor to) {
        MonKey key = to.getMonKey();
        // note we are wiping out the key details if they exist, but if this is done with the limited use
        // within this class that is ok as it is a key that doesn't use the existing details if they exist.
        // This is a bit of a hack so we can keep track of the std deviation without access to sum of squares etc.
        if (key.getDetails() == null || !(key.getDetails() instanceof StdDev)) {
            key.setDetails(new StdDev());
        }

        StdDev stdDev = (StdDev) key.getDetails();
        stdDev.addStdDev(from.getStdDev());
    }

    /**
     * Class used to take the average of each instances stddeviation. This is done because the inner calculation of sumofsquares
     * etc isn't available. So if there are 3 instances we are summarizing say with a stddev of 5,15,13 then the avgStdDev
     * would be 11 (i.e. 33/3)
     */
    static class StdDev implements Serializable {
        private static final long serialVersionUID = 282L;
        private int count;
        private double totalStDev;

        public StdDev addStdDev(double newValue) {
            count += 1;
            totalStDev += newValue;
            return this;
        }

        public double getAvgStdDev() {
            if (count <= 0) {
                return 0;
            }
            return totalStDev / count;
        }

        @Override
        public String toString() {
            return "StdDev{" +
                    "count=" + count +
                    ", totalStDev=" + totalStDev +
                    '}';
        }
    }

}
