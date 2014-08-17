package com.jamonapi;

import java.util.List;
/**
 * Main workhorse for monitors.  For display gets info from key, monitor, and range.
 *
 * Created on January 21, 2006, 6:08 PM
 */

class MonitorImp extends Monitor implements RowData  {

    private static final long serialVersionUID = 279L;

    MonitorImp(MonInternals monData) {
        super(monData);
    }

    private static final MonKey NULL_MON_KEY=new NullMonKey();
    MonitorImp() {
        this(NULL_MON_KEY,null,null,false);
    }

    // inherited by FrequencyDist
    MonitorImp(MonKey key, RangeImp range, ActivityStats activityStats,
            boolean isTimeMonitor) {
        this.monData.key = key;
        this.monData.range = range;
        this.monData.setActivityStats(activityStats);
        this.monData.isTimeMonitor = isTimeMonitor;
    }


    public List getBasicHeader(List header) {
        monData.key.getBasicHeader(header);
        getThisData(header);

        return header;
    }

    private List getThisData(List header) {
        header.add(monData.name+"Hits");
        header.add(monData.name+"Avg");
        header.add(monData.name+"Total");
        header.add(monData.name+"StdDev");
        header.add(monData.name+"LastValue");
        header.add(monData.name+"Min");
        header.add(monData.name+"Max");
        header.add(monData.name+"Active");
        header.add(monData.name+"AvgActive");
        header.add(monData.name+"MaxActive");
        header.add(monData.name+"FirstAccess");
        header.add(monData.name+"LastAccess");
        header.add(monData.name+"Enabled");
        header.add(monData.name+"Primary");
        header.add(monData.name+"HasListeners");
        return header;
    }

    public List getHeader(List header) {
        monData.key.getHeader(header);
        getThisData(header);

        if (monData.range!=null)
            monData.range.getHeader(header);

        return header;
    }

    public List getDisplayHeader(List header) {
        monData.key.getDisplayHeader(header);
        getThisData(header);

        if (monData.range!=null)
            monData.range.getDisplayHeader(header);

        return header;
    }


    public List getBasicRowData(List rowData) {
        monData.key.getBasicRowData(rowData);
        return getThisRowData(rowData);
    }

    private List getThisRowData(List rowData) {
        rowData.add(new Double(getHits()));
        rowData.add(new Double(getAvg()));
        rowData.add(new Double(getTotal()));
        rowData.add(new Double(getStdDev()));
        rowData.add(new Double(getLastValue()));
        rowData.add(new Double(getMin()));
        rowData.add(new Double(getMax()));
        rowData.add(new Double(getActive()));
        rowData.add(new Double(getAvgActive()));
        rowData.add(new Double(getMaxActive()));
        rowData.add(getFirstAccess());
        rowData.add(getLastAccess());
        rowData.add(bool(isEnabled()));
        rowData.add(bool(isPrimary()));
        rowData.add(bool(hasListeners()));

        return rowData;


    }

    private static Boolean bool(boolean bool) {
        return (bool) ? Boolean.TRUE : Boolean.FALSE;
    }

    public List getRowData(List rowData) {
        monData.key.getRowData(rowData);
        getThisRowData(rowData);

        if (monData.range!=null)
            monData.range.getRowData(rowData);

        return rowData;

    }


    public List getRowDisplayData(List rowData) {
        monData.key.getRowDisplayData(rowData);
        getThisRowData(rowData);

        if (monData.range!=null)
            monData.range.getRowDisplayData(rowData);

        return rowData;
    }

    public RangeHolder getRangeHolder() {
        RangeImp r=(RangeImp) getRange();
        if (r!=null)
            return r.getRangeHolder();
        else
            return null;
    }

    void setActivityStats(ActivityStats activityStats) {
        monData.setActivityStats(activityStats);
    }


    private static class NullMonKey implements MonKey {

        private static final long serialVersionUID = 279L;

        public String getLabel() {
            return "";
        }

        public Object getDetails() {
            return "";
        }

        public String getRangeKey() {
            return "";
        }

        public Object getValue(String primaryKey) {
            return "";
        }

        public List getBasicHeader(List header) {
            return header;
        }

        public List getBasicRowData(List rowData) {
            return rowData;
        }

        public List getDisplayHeader(List header) {
            return header;
        }

        public List getHeader(List header) {
            return header;
        }

        public List getRowData(List rowData) {
            return rowData;
        }

        public List getRowDisplayData(List rowData) {
            return rowData;
        }

        public void setDetails(Object details) {

        }

        @Override
        public void setInstanceName(String instanceName) {

        }

        @Override
        public String getInstanceName() {
            return DEFAULT_INSTANCE_NAME;
        }

        public int getSize() {
            return 1;
        }

    }



}
