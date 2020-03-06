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
    static final int SUMMARY_FIFO_BUFFER_SIZE = 100;


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


    // test filterbyunits test
    // copy jamon listener buffers     // configurable sizes and features??
    //      i.e. other jamonlisteners
    //      max,min,fifo,??
    //             JAMonBufferListener bufferListener = (JAMonBufferListener) mon.getListenerType("value").getListener(listenerType[0].toString());
    //            // each should have 50 rows of data.
    //            assertThat(bufferListener.getRowCount()).isEqualTo(BUFFER_SIZE);
    //            Object[][] data = bufferListener.getDetailData().getData();
    //     JAMonBufferListener.public void addRow(Object[] row) {
    //  how to get data in order?
    // maybe make the ijnstancename an extrra column
    // always add to fifo buffer in the respective listenertype.
    // JAMonDetailRow = note Arrays.asList(mon.getInstanceName(), label, ...)
    //         Object[][] data=((JAMonBufferListener)listener).getDetailData().getData();
    // public class BufferList implements DetailData {
    //    bufferListener.getBufferList().getRowCount())
    // note i think the following consructor is used if i want different data to be dislayed in each row
//    public JAMonBufferListener(String name){
//        this(name, new BufferList(DEFAULT_HEADER,50));
//    }
//
//    /** Name the listener and pass in the jamon BufferList to use */
//    public JAMonBufferListener(String name, BufferList list) {
//        this.name=name;
//        this.list=list;
//    }
//    private static JAMonBufferListener getFIFO() {
//        BufferHolder bufferHolder=new FIFOBufferHolder();
//        BufferList bufferList=new BufferList(JAMonBufferListener.DEFAULT_HEADER,bufferHolder);
//              public BufferList(String[] header, int bufferSize,  BufferHolder bufferHolder) {
//        return new JAMonBufferListener("FIFOBuffer", bufferList);
//    }
    // public class DetailDataWrapper implements DetailData {
    //     public BufferList(String[] header, int bufferSize,  BufferHolder bufferHolder) {

    //  List original=bufferHolder.getOrderedCollection();
    //  Log4JBufferListener ----
    // getBufferList().addRow(toArray(key.getLoggingEvent(), mon));
//    private static HeaderInfo log4jHeader=getHeaderInfo(new String[] { "Label", "LoggerName",
//            "Level", "ThreadName", "Exception" });

//    protected Object[] toArray(LoggingEvent event, Monitor mon) {
//        // populate header with standard monitor data first and after the fact by log4j data
//        Object[] data=log4jHeader.getData(mon);
//        data[0]=mon.getMonKey().getDetails();
//        data[1]=event.getLoggerName();
//        data[2]=event.getLevel().toString();
//        data[3]=event.getThreadName();
//        data[4]=(event.getThrowableInformation() == null || event.getThrowableInformation().getThrowable() == null) ?
//                "" : Misc.getExceptionTrace(event.getThrowableInformation().getThrowable());
//
//        return data;
//    }
    // JAMonBufferListener - Label
    //     public static HeaderInfo getHeaderInfo(String[] firstPart) {
    //        return new HeaderInfo(firstPart);
    //    }
//public static HeaderInfo getDefaultHeaderInfo() {
//    return getHeaderInfo(new String[]{"Label"});

//}
//JAMonDetailValue
// public Object[] toArray() {
//    if (row==null) {
//        if (keyToString)
//            row = new Object[]{Misc.getAsString(key.getDetails()),new Double(value), new Double(active), new Date(time)};
//        else {
//            List list=new ArrayList();
//            Misc.addTo(list, key.getDetails()); // key details is a List with instanceName, label
//            list.add(new Double(value));
//            list.add(new Double(active));
//            list.add(new Date(time));
//            row=list.toArray();
//        }
// Misc
//    public static void addTo(Collection coll, Object objToAdd) {
    // List list ;
    // list.add(instanceName);
    // list.add(mon.getLabel())
    // key.setDetails(list)
    //pass in and change instancename from mc to key.
//                        except for local
//                        config from properties
//                        SAVE TIME FORR EACH PROCESSED INSTANCE WITH A FIFOBUFFER
//                        try log4j listener for instnace name


    // make this configurable from both size and whether to do or not.?????
    //        //  - log4j
    //        //  - steps for jetty, automon, tomcat
    // start/stop for each instance to aggregate
    //      numinstances fifobuffer add it
    // git push/ git push github_origin
    // upgrade log4j
    // can i change serializable log4j so it doesn't fail?
    //      it was a serialization issue due to log4j jar missing in jetty
    //      stackTrace=com.hazelcast.nio.serialization.HazelcastSerializationException: java.lang.NoClassDefFoundError: org/apache/log4j/spi/LoggingEvent
    //  probably get rid of that error by not rreturning loggingevent
    //     public LoggingEvent getLoggingEvent() {
    //        return (LoggingEvent) getParam();
    //    }
    // what java version do i support?
    // documentatiion - inline and web site?
    //
    // video - about new features and about running, checking in, and compiling
    // x upgrade hazel cast to 4
    // x combiner tests


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
            MonitorComposite monitorComposite = persister.get(instanceKey);
            incrementInstanceMonitor(factory, monitorComposite.getInstanceName());
            aggregate(factory, monitorComposite);
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

    private void aggregate(FactoryEnabled factory, MonitorComposite monitorComposite) {
        Monitor[] monitors = monitorComposite.getMonitors();

        // loop monitors creating aggregated monitors and setup their listeners
        for (Monitor monitor : monitors) {
            // make a copy of the key as we have to change the instance name and don't want to change it for the local instance
            MonKey key = SerializationUtils.deepCopy(monitor.getMonKey());
            key.setInstanceName(AGGREGATED_INSTANCENAME);

            Monitor summaryMonitor = factory.getMonitor(key);
            if (!monitorComposite.isLocalInstance()) {
                // done so monitors can be identified by instance when viewed in jamonadmin.jsp
                // Don't want to do this for the local instance as it is not needed as it is the default and
                // it is the only one that is not a copy/clone of the original.
                monitor.getMonKey().setInstanceName(monitorComposite.getInstanceName());
            }
            merge(monitor, summaryMonitor);
            createSummaryFifoBufferIfAbsent(summaryMonitor);
            addMonitorDataToSummaryFifoBuffer(monitor, summaryMonitor);
            DistributedUtils.copyJamonBufferListenerData(monitor, summaryMonitor, monitorComposite.getInstanceName());
        }

    }

    private void incrementInstanceMonitor(FactoryEnabled factory, String instanceName) {
        MonKey key = new MonKeyImp(AGGREGATED_MONITOR_LABEL, "count");
        key.setDetails(instanceName);
        key.setInstanceName(AGGREGATED_INSTANCENAME);
        factory.getMonitor(key).add(1);
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
