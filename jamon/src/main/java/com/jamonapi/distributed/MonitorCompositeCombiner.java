package com.jamonapi.distributed;

import com.jamonapi.*;
import com.jamonapi.utils.*;

import java.util.*;

/**
 * Combines multiple MonitorComposite objects into one by getting them from the @link JamonDataPersister.
 *
 * Created by stevesouza on 8/16/14.
 */
public class MonitorCompositeCombiner {
    private JamonDataPersister persister;

    private static final String SUMMARY_LISTENER = "InstanceSummaryFIFOBuffer";

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
        for (int i=0;i<instanceKeys.length;i++) {
            MonitorComposite monitorComposite = persister.get(instanceKeys[i]);
            if (monitorComposite!=null) {
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
    public static MonitorComposite append(Collection<MonitorComposite> monitorCompositeList) {
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

    public static MonitorComposite aggregate(Collection<MonitorComposite> monitorCompositeList) {
        FactoryEnabled factory = new FactoryEnabled();
        factory.remove(new MonKeyImp(MonitorFactory.EXCEPTIONS_LABEL, "Exception"));
        MonitorComposite mc = append(monitorCompositeList);
        Monitor[] monitors = mc.getMonitors();
        // 1) iterate data creating monitors and and setup listeners
        // 2) loop through a second time and merge monitor values and populate listeners (maybe done in same method)
        for (Monitor monitor : monitors) {
            MonKey key = SerializationUtils.deepCopy(monitor.getMonKey());
            key.setInstanceName("aggregate");
//            List row=new ArrayList();
//            mon.getBasicRowData(row);
//            return row.toArray();
//            key.setDetails(String.format("instanceName=%s, %s - %s", monitor.getMonKey().getInstanceName(), monitor, getRowData(monitor))); // make tablular

//            key.setDetails(getRowData(monitor).toArray()); // make tablular
//            key.getBasicHeader()
            // exception instnace name is local - when creating factory?
//            factory.getMonitor(key).add(1);
            /*
                    Monitor mon =  getMonitor(MonitorFactory.EXCEPTIONS_LABEL, "Exception");
        if (!mon.hasListener("value", "FIFOBuffer")) {
            mon.addListener("value", JAMonListenerFactory.get("FIFOBuffer"));
        }
            private static JAMonBufferListener getFIFO() {
        BufferHolder bufferHolder=new FIFOBufferHolder();
        BufferList bufferList=new BufferList(JAMonBufferListener.DEFAULT_HEADER,bufferHolder);
        return new JAMonBufferListener("FIFOBuffer", bufferList);


    }
             */

            /*
              Returns label, value, time as an Object[] of 3 values.
              JAMonDetailValue
            public Object[] toArray() {
                if (row==null) {
                    if (keyToString)
                        row = new Object[]{Misc.getAsString(key.getDetails()),new Double(value), new Double(active), new Date(time)};
                    else {
                        List list=new ArrayList();
                        Misc.addTo(list, key.getDetails());
                        list.add(new Double(value));
                        list.add(new Double(active));
                        list.add(new Date(time));
                        row=list.toArray();
                    }
                }

JAMonBufferListener
    public void addRow(Object[] row) {
        list.addRow(row);
    }


             */
            Monitor summaryMonitor = factory.getMonitor(key);
            if (!summaryMonitor.hasListeners()) {
                //     public BufferList(String[] header, int bufferSize,  BufferHolder bufferHolder) {

                summaryMonitor.addListener("value", getSummaryFIFOBufferListener(summaryMonitor));

//                summaryMonitor.addListener("value", JAMonListenerFactory.get("FIFOBuffer"));
            }
            merge(monitor, summaryMonitor);
/**
 * xxxxx*****
 * jamonbufferrrlistener=====null
 * xxx=[local, com.jamonapi.Exceptions, Exception,
 */
            System.err.println("xxxxx*****");
//        to.getListenerType("value").getListener("FIFOBuffer").processEvent(to);
            JAMonBufferListener jaMonBufferListener = (JAMonBufferListener) summaryMonitor.getListenerType("value").getListener(SUMMARY_LISTENER);
            System.err.println("jamonbufferrrlistener====="+jaMonBufferListener);
            System.err.println("xxx="+getRowData(monitor));
            jaMonBufferListener.addRow(getRowData(monitor).toArray());
            //monitor.getMonKey().clone();
            // iterator for append sets instancename....monitor.getMonKey().setInstanceName("aggregate");
        }
        return factory.getRootMonitor();
    }

    private static List<String> getHeader(Monitor mon) {
        List<String> header = new ArrayList();
        mon.getMonKey().getHeader(header);
        return getDataPartHeader(header, "");
    }


    private static List<String> getDataPartHeader(List<String> header, String prefix) {
        header.add(prefix+"Hits");
        header.add(prefix+"Avg");
        header.add(prefix+"Total");
        header.add(prefix+"StdDev");
        header.add(prefix+"LastValue");
        header.add(prefix+"Min");
        header.add(prefix+"Max");
        header.add(prefix+"Active");
        header.add(prefix+"AvgActive");
        header.add(prefix+"MaxActive");
        header.add(prefix+"FirstAccess");
        header.add(prefix+"LastAccess");
        header.add(prefix+"Enabled");
        header.add(prefix+"Primary");
        header.add(prefix+"HasListeners");
        return header;

    }


    private static JAMonBufferListener getSummaryFIFOBufferListener(Monitor mon) {
        //     public BufferList(String[] header, int bufferSize,  BufferHolder bufferHolder) {

        BufferHolder bufferHolder=new FIFOBufferHolder();
        BufferList bufferList=new BufferList(getHeader(mon).toArray(new String[0]),100, bufferHolder);
        return new JAMonBufferListener(SUMMARY_LISTENER, bufferList);

    }

    private static List getRowData(Monitor mon) {
        List rowData = new ArrayList();
        mon.getMonKey().getRowData(rowData);
//        mon.getMonKey().getBasicRowData(rowData);
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
         * @param instanceKey
         */
    public void remove(String... instanceKey) {
        for (int i=0;i<instanceKey.length;i++) {
            persister.remove(instanceKey[i]);
        }
    }


    public static Monitor merge(Monitor from, Monitor to) {
        // next
        //  - unit tests
        //  - null and null date
        //  - active stats?
        //  - std dev?
        //  - listeners/buffers
        //    - save full monitor for each server?
        //    - save numinstances as well as buffer of the names
        //    - save most recent n.  configurable?
        //    - save other buffers? max, min, ...
        //  - log4j
        //  - steps for jetty, automon, tomcat
        to.setHits(to.getHits()+from.getHits());
        // to.add(...) ??
        to.setTotal(to.getTotal()+from.getTotal());
        to.setMin(Math.min(to.getMin(), from.getMin()));
        to.setMax(Math.max(to.getMax(),  from.getMax()));
        to.setMaxActive(Math.max(to.getMaxActive(), from.getMaxActive()));
        to.setActive(to.getActive()+from.getActive());
        // to.setTotalActive();//?
        // to.globalactive....
        // null and null_date should never be the min or max if the other one has a value.
        // ??????
        //       List row=new ArrayList();
        //        mon.getBasicRowData(row);
        // mon.getBasicHeaderData(headeer)
//        public List getBasicHeader(List header) {
//            monData.key.getBasicHeader(header);
//            getThisData(header);
//
//            return header;
//        }

//   mondetail.jsp
//       setBufferSize((JAMonBufferListener) listener, bufferSize);
//
//        DetailData detailData=((JAMonBufferListener)listener).getDetailData();
//        ResultSetConverter rsc=getResultSetConverter(detailData.getHeader(), detailData.getData(), arraySQLExec);

        to.setFirstAccess(Misc.min(to.getFirstAccess(), from.getFirstAccess()));// ?
        Date lastAccess = Misc.max(to.getLastAccess(), from.getLastAccess());
        to.setLastAccess(lastAccess);//?
        // need to do a date compare to use the max dates lastValue
        if (lastAccess!=null && lastAccess.equals(from.getLastAccess())) {
            // doesn't work - need max access date for all monitors not just this one.?
          to.setLastValue(from.getLastValue());
        }

        to.getAvgActive();// uses     public void setTotalActive(double value) {
        to.getAvgGlobalActive();// no access to it i think so just make 0 or default?
        to.getAvgPrimaryActive();//no access to it i think so just make 0 or default?
        to.getStdDev(); // track in a peer structurre?
        to.setPrimary(to.isPrimary() || from.isPrimary());//?

        return to;
    }

//    x public double getTotal();
//    x public double getAvg();
//    x public double getMin();
//   x  public double getMax();
//    x public double getHits();
//    public double getStdDev();
//    x public Date getFirstAccess();
//    x public Date getLastAccess();
//    x public double getLastValue();
//? public boolean isEnabled();
// x public double getActive();
//   x public double getMaxActive();
//    public void setTotalActive(double value);
//    public double getAvgActive();
//    public boolean isPrimary();
//public boolean hasListeners(String listenerTypeName);
//    public boolean hasListeners();
}
