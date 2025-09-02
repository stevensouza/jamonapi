package com.jamonapi.distributed;

import com.jamonapi.*;
import com.jamonapi.utils.BufferList;
import com.jamonapi.utils.Misc;
import com.jamonapi.utils.SerializationUtils;

import java.util.*;

/**
 * Combines multiple MonitorComposite objects into one by getting them from the @link JamonDataPersister.
 * <p>
 * Created by stevesouza on 8/16/14.
 */
public class MonitorCompositeCombiner {
    private JamonDataPersister persister;

    static final String SUMMARY_LISTENER = "FIFOBufferInstanceSummary";
    static final String AGGREGATED_INSTANCENAME = "aggregated";
    static final String AGGREGATED_MONITOR_LABEL = "com.jamonapi.distributed.aggregated";
    static final String FIFO_BUFFER = "FIFOBuffer";
    // The amount of server instances that will be held in the summary buffer for each instance.  This buffer contains each of the top level monitors
    // of the same key for each server.  Example: Monitor for all servers that have SQL All.
    static final int SUMMARY_FIFO_BUFFER_SIZE = Integer.valueOf(JamonPropertiesLoader.PROPS.getProperty("monitorCompositeCombiner.summaryFifoBufferSize", "100"));
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


    /**
     * Take a list of instance names, query them and combine their monitor composite data into one aggregated
     * monitor composite. This allows to look at a summary of all servers data in one report.
     *
     * @param instanceKeys A list of servers that have jamon data on them.
     * @return An aggregated version of all the servers jamon data.
     * @since 2.82
     */
    public MonitorComposite aggregate(String... instanceKeys) {
        FactoryEnabled factory = new FactoryEnabled(false);

        for (String instanceKey : instanceKeys) {
            Monitor mon = startInstanceMonitor();
            // Note: I had a difficult to figure out bug due to the local instance if I didn't copy/clone. Technically
            // as hazelcast already returns a copy this is only required for the local instance, however should I change the
            // implementation later to something that doesn't support hazelcasts contract it is safer to make a copy. Alternatively
            // I could change the jamon persisters contract to always return a copy. I didn't opt for this. The bug would display
            // 'local' as an instance name in the fifo buffer display page even if I only selected 'local-saved', and
            // 'tomcat8-production'.
            MonitorComposite monitorComposite = persister.get(instanceKey).copy();
            mon.getMonKey().setDetails(monitorComposite.getInstanceName());
            aggregate(factory, monitorComposite, instanceKeys.length);
            mon.stop();
        }

        MonitorComposite aggregated = factory.getRootMonitor();
        aggregated.setInstanceName(AGGREGATED_INSTANCENAME);
        return aggregated;
    }

    /**
     * Combine multiple MonitorComposites into 1 MonitorComposite, that is a sequential concatenation of all the
     * individual servers jamon data.
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
        List<MonitorComposite> monitorCompositeResultsList = new ArrayList<>();
        List<String> instanceNameList = new ArrayList<>();

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

    private void aggregate(FactoryEnabled factory, MonitorComposite monitorComposite, int numInstances) {
        Monitor[] monitors = monitorComposite.getMonitors();
        // loop monitors creating aggregated monitors and setup their listeners
        for (Monitor monitor : monitors) {
            // make a copy of the key as we have to change the instance name and don't want to change it for the local instance
            MonKey key = SerializationUtils.deepCopy(monitor.getMonKey());
            key.setInstanceName(AGGREGATED_INSTANCENAME); // keys as displayed in jamon admin page

            // summaryMonitor would combine data from all monitors with the same key in all composite monitors
            // i.e. local, local_saved, tomcat8_production for example.
            Monitor summaryMonitor = factory.getMonitor(key);

            // copy from monitor data into summaryMonitor
            merge(monitor, summaryMonitor);
            // add the summary buffer to the summaryMonitor
            createSummaryFifoBufferIfAbsent(summaryMonitor);
            addMonitorDataToSummaryFifoBuffer(monitor, summaryMonitor);
            DistributedUtils.copyJamonBufferListenerData(monitor, summaryMonitor, numInstances);
        }
    }

    private Monitor startInstanceMonitor() {
        Monitor mon = MonitorFactory.start(AGGREGATED_MONITOR_LABEL);
        if (!mon.hasListeners()) {
            mon.addListener("value", JAMonListenerFactory.get(FIFO_BUFFER));
        }
        return mon;
    }

    private List<MonitorComposite> getMonitorComposites(String[] instanceKeys) {
        List<MonitorComposite> monitorCompositeList = new ArrayList<MonitorComposite>();
        for (String instanceKey : instanceKeys) {
            MonitorComposite monitorComposite = persister.get(instanceKey);
            if (monitorComposite != null) {
                monitorCompositeList.add(monitorComposite);
            }
        }
        return monitorCompositeList;
    }

    private void createSummaryFifoBufferIfAbsent(Monitor summaryMonitor) {
        if (!summaryMonitor.hasListener("value", SUMMARY_LISTENER)) {
            summaryMonitor.addListener("value", getSummaryFIFOBufferListener(summaryMonitor));
        }
    }

    // Each monitor from an instance will have its value saved for display in a fifo buffer.
    private void addMonitorDataToSummaryFifoBuffer(Monitor monitor, Monitor summaryMonitor) {
        JAMonBufferListener jaMonBufferListener = (JAMonBufferListener) summaryMonitor.getListenerType("value").getListener(SUMMARY_LISTENER);
        jaMonBufferListener.addRow(getRowData(monitor).toArray());
    }


    private JAMonBufferListener getSummaryFIFOBufferListener(Monitor mon) {
        BufferList bufferList = new BufferList(getHeader(mon).toArray(new String[0]), SUMMARY_FIFO_BUFFER_SIZE);
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
        rowData.add(mon.getStdDev());
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
     * @param instanceKeys An instance to remove
     */
    public void remove(String... instanceKeys) {
        for (String instanceKey : instanceKeys) {
            persister.remove(instanceKey);
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
        // note std deviation is not calculated when instances are merged. Sum of squares would be
        // required and this is a private data structure. If it is of interest downstream it could be added
        to.setTotalActive(to.getAvgActive() * to.getHits() + from.getAvgActive() * from.getHits());
        to.setHits(to.getHits() + from.getHits());
        to.setTotal(to.getTotal() + from.getTotal());
        to.setMin(Math.min(to.getMin(), from.getMin()));
        to.setMax(Math.max(to.getMax(), from.getMax()));
        to.setMaxActive(Math.max(to.getMaxActive(), from.getMaxActive()));
        to.setActive(to.getActive() + from.getActive());
        to.setFirstAccess(Misc.min(to.getFirstAccess(), from.getFirstAccess()));
        Date lastAccess = Misc.max(to.getLastAccess(), from.getLastAccess());
        to.setLastAccess(lastAccess);
        // need to do a date compare to use the max dates lastValue
        if (lastAccess != null && lastAccess.equals(from.getLastAccess())) {
            to.setLastValue(from.getLastValue());
        }

        to.setPrimary(to.isPrimary() || from.isPrimary());
        return to;
    }

}
