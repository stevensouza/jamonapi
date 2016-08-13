
package com.jamonapi;


import com.jamonapi.utils.DetailData;
import com.jamonapi.utils.Misc;
import com.jamonapi.utils.SerializationUtils;

import java.util.*;

/**
 * Treats groups of monitors the same way you treat one monitor.  i.e. you can enable/disable/reset
 * etc a group of monitors.
 */
public class MonitorComposite extends Monitor implements DetailData  {

    private static final long serialVersionUID = 279L;
    private static final String LOCAL="local";
    private static final String INSTANCE_NAME_HEADER = "Instance";

    private final Monitor[] monitors;// the monitors in the composite
    private final int numRows; // rows in the composite
    private final static int TYPICAL_NUM_CHILDREN=200;// hopefully makes it so the monitor need not grow all the time
    private Map<MonKey, Monitor> map;
    private Date dateCreated;
    private String instanceName=LOCAL;

    /** Creates a new instance of MonitorComposite */
    public MonitorComposite(Monitor[] monitors) {
        this.monitors = monitors;
        numRows = (monitors==null) ? 0 : monitors.length;
        initializeForLookUps();
        dateCreated = new Date();
    }

    private void initializeForLookUps() {
        map = new HashMap<MonKey, Monitor>();
        if (monitors!=null) {
            for (Monitor mon : monitors) {
                map.put(mon.getMonKey(), mon);
            }
        }
    }

    MonitorComposite() {
        this(null);
    }

    public Monitor[] getMonitors() {
        return monitors;
    }

    private Monitor[] getMonitorsWithUnits(String units) {
        if (monitors==null || units==null) {
            return null;
        } else if ("AllMonitors".equalsIgnoreCase(units)) {
            return monitors;
        }

        int size=monitors.length;
        List rows=new ArrayList(monitors.length);
        for (int i=0;i<size;i++) {
            // if units of range match units of this monitor then
            if (units.equalsIgnoreCase(monitors[i].getMonKey().getRangeKey())) {
                rows.add(monitors[i]);
            }
        }

        if (rows.size()==0) {
            return null;
        } else {
            return (MonitorImp[]) rows.toArray(new MonitorImp[0]);
        }
    }

    /**
     *
     * @return A collection of all the unit types that are currently in this object (i.e. ms., ns., Exception,...)
     * @since  2.81
     */
    public Collection<String> getDistinctUnits() {
        Set<String> units = new TreeSet<String>();
        int size=monitors.length;
        for (int i=0;i<size;i++) {
            units.add(monitors[i].getMonKey().getRangeKey());
        }

        return units;
    }

    public MonitorComposite filterByUnits(String units) {
        return new MonitorComposite(getMonitorsWithUnits(units)).setInstanceName(getInstanceName()).setDateCreated(getDateCreated());
    }

    /** Note this works with Local, however when it is put into the map by combining multiple MonitorComposites that have
     * the same key (for example on different instances) it does not currently work.
     *
     * @param key
     * @return
     */
    public boolean exists(MonKey key) {
       return map.containsKey(key);
    }

    public Monitor getMonitor(MonKey key) {
        return map.get(key);
    }

    public String getInstanceName() {
        return instanceName;
    }

    public MonitorComposite setInstanceName(String instanceName) {
        this.instanceName = instanceName;
        return this;
    }

    /** means is in this jvm and not data distributed from another machine */
    public boolean isLocalInstance() {
        return LOCAL.equalsIgnoreCase(instanceName);
    }

    /** Pass in an array with col1=lables, and col2=units and then call methods */
    public static MonitorComposite getMonitors(String[][] labels) {
        int numRows=(labels==null) ? 0 : labels.length;
        int numCols=(labels[0]==null) ? 0 : labels[0].length;

        Monitor[] monArray=new Monitor[numRows];

        for (int i=0; i<numRows; i++) {
            MonKey key=null;
            if (numCols==2)// i.e 2 columns - summary, units (ex: sp_proc ?, ms.)
                key=new MonKeyImp(labels[i][0],labels[i][1]);
            else if (numCols==3) // 3 columns - summary, detail, units (ex: sp_proc ?, sp_proc 'steve', ms.)
                key=new MonKeyImp(labels[i][0],labels[i][1], labels[i][2]);

            monArray[i]=MonitorFactory.getMonitor(key);
        }

        return new MonitorComposite(monArray);

    }


    public int getNumRows() {
        return numRows;
    }

    /** Return the header that applies to all monitors.  It does not include range column headers.
     ** It will contain label, hits, total, avg, min, max and active among other columns
     **/
    public String[] getBasicHeader() {
        List header=new ArrayList();
        if (hasData()) {
            // being as all monitors in the composite should have the same range
            //  getting the first one should suffice to get the header
            getFirstMon().getBasicHeader(header);
            return (String[]) header.toArray(new String[0]);
        } else
            return null;
    }

    /** Return the header with basic data and columns for each field within the range. note getHeader only works if the range of all monitors in the composite are the same.
     **/
    public String[] getHeader() {
        List header=new ArrayList();
        if (hasData()) {
            // being as all monitors in the composite should have the same range
            //  getting the first one should suffice to get the header
            getFirstMon().getHeader(header);
            return (String[]) header.toArray(new String[0]);
        } else
            return null;
    }


    /** Return the header with basic data and one column for each range.  Note this only will work with ranges of the same type */
    public String[] getDisplayHeader() {
        List header=new ArrayList();
        if (hasData()) {
            // being as all monitors in the composite should have the same range
            //  getting the first one should suffice to get the header
            getFirstMon().getDisplayHeader(header);
            return (String[]) header.toArray(new String[0]);
        } else
            return null;
    }


    // Various get data methods (for all data, basic data, and display data
    // note getData will only return an array with the same number of columns in every row
    // if the range of all monitors in the composite are the same.
    /** Get all data including basic data as well as each element within the range */
    public Object[][] getData() {
        if (!hasData())
            return null;

        Object[][] data=new Object[getNumRows()][];
        for (int i=0;i<numRows;i++) {
            data[i]=getRowData((MonitorImp)monitors[i]);
        }

        return data;

    }


    /** Get basic data (which excludes range data) */
    public Object[][] getBasicData() {
        if (!hasData())
            return null;

        Object[][] data=new Object[getNumRows()][];
        for (int i=0;i<numRows;i++) {
            data[i]=getBasicRowData((MonitorImp)monitors[i]);
        }

        return data;

    }


    /** Get display data including 1 column for each range */
    public Object[][] getDisplayData() {
        if (!hasData())
            return null;

        Object[][] data=new Object[getNumRows()][];
        for (int i=0;i<numRows;i++) {
            data[i]=getRowDisplayData((MonitorImp)monitors[i]);
        }

        return data;

    }

    /** A basic report in html format.  It has summary info for all monitors but
     * no range info
     */
    public String getReport() {
        return getReport(0, "asc");
    }

    /** A basic report in html format that is sorted.  It has summary info for all monitors but
     * no range info
     */
    public String getReport(int sortCol, String sortOrder) {
        if (!hasData())
            return "";

        String[] header=getBasicHeader();
        Object[][] data=Misc.sort(getBasicData(), sortCol, sortOrder);
        int rows=data.length;
        int cols=header.length;

        StringBuffer html=new StringBuffer(100000);// guess on report size
        html.append("\n<table border='1' rules='all'>\n");

        for (int i=0;i<cols;i++)
            html.append("<th>"+header[i]+"</th>");

        html.append("<th>"+header[0]+"</th>");//repeat first header
        html.append("\n");

        for (int i=0;i<rows;i++) {
            html.append("<tr>");
            for (int j=0;j<cols;j++) {
                html.append("<td>"+data[i][j]+"</td>");
            }
            html.append("<td>"+data[i][0]+"</td>");// repeat first column
            html.append("</tr>\n");
        }


        html.append("</table>");

        return html.toString();
    }

    /** Does this have data? */
    public boolean hasData() {
        return (getNumRows()==0) ? false : true;
    }

    // Various get row data methods (for full row, basic row, and display row
    private Object[] getRowData(MonitorImp mon) {
        List row=new ArrayList(TYPICAL_NUM_CHILDREN);
        mon.getRowData(row);
        return row.toArray();
    }

    private Object[] getBasicRowData(MonitorImp mon) {
        List row=new ArrayList();
        mon.getBasicRowData(row);
        return row.toArray();
    }


    private Object[] getRowDisplayData(MonitorImp mon) {
        List row=new ArrayList(TYPICAL_NUM_CHILDREN);
        mon.getRowDisplayData(row);
        return row.toArray();
    }


    @Override
    public void reset() {
        for (int i=0;i<numRows;i++)
            monitors[i].reset();
    }


    @Override
    public void disable() {
        for (int i=0;i<numRows;i++)
            monitors[i].disable();
    }

    @Override
    public void enable() {
        for (int i=0;i<numRows;i++)
            monitors[i].enable();
    }

    @Override
    public double getActive() {
        double value=0;
        for (int i=0;i<numRows;i++) {
            value+=monitors[i].getActive();
        }

        return value;
    }

    @Override
    public double getAvg() {
        double hits=getHits();
        double total=0;

        for (int i=0;i<numRows;i++) {
            total+=monitors[i].getTotal();
        }

        if (hits==0)
            return 0;
        else
            return total/hits;
    }

    /** This returns a weighted average */
    @Override
    public double getAvgActive() {
        double weightedActive=0;
        double totalHits=0;

        for (int i=0;i<numRows;i++) {
            double hits=monitors[i].getHits();
            weightedActive=hits*monitors[i].getAvgActive();
            totalHits+=hits;
        }

        if (totalHits==0)
            return 0;
        else
            return weightedActive/totalHits;
    }


    @Override
    public Date getFirstAccess() {
        Date firstAccess=null;
        for (int i=0;i<numRows;i++) {
            Date thisDate=monitors[i].getFirstAccess();
            if (firstAccess==null || thisDate.compareTo(firstAccess)<0) // thisDate<firstDate
                firstAccess=thisDate;

        }

        return firstAccess;
    }


    public Date getDateCreated() {
        return dateCreated;
    }

    /**
      @since 2.79
     */
    public MonitorComposite setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }


    @Override
    public double getHits() {
        double value=0;
        for (int i=0;i<numRows;i++) {
            value+=monitors[i].getHits();
        }

        return value;
    }


    @Override
    public MonKey getMonKey() {
        if (!hasData())
            return null;

        return getFirstMon().getMonKey();
    }


    @Override
    public Date getLastAccess() {
        Date lastAccess=null;
        for (int i=0;i<numRows;i++) {
            Date thisDate=monitors[i].getLastAccess();
            if (lastAccess==null || thisDate.compareTo(lastAccess)>0) // thisDate>lastAccess
                lastAccess=thisDate;

        }

        return lastAccess;
    }


    @Override
    public double getLastValue() {
        Date date=getLastAccess();
        for (int i=0;i<numRows;i++) {
            if (date.compareTo(monitors[i].getLastAccess())>=0) // date>=getLastAccess)
                return monitors[i].getLastValue();
        }

        return 0;
    }


    @Override
    public double getMax() {
        double max=MonInternals.MAX_DOUBLE;

        for (int i=0;i<numRows;i++) {
            double thisMax=monitors[i].getMax();

            if (thisMax>max)
                max=thisMax;
        }

        return max;
    }


    @Override
    public double getMaxActive() {
        double max=MonInternals.MAX_DOUBLE;

        for (int i=0;i<numRows;i++) {
            double thisMax=monitors[i].getMaxActive();

            if (thisMax>max)
                max=thisMax;
        }

        return max;
    }

    @Override
    public double getMin() {
        double min=MonInternals.MIN_DOUBLE;

        for (int i=0;i<numRows;i++) {
            double thisMin=monitors[i].getMin();

            if (thisMin<min)
                min=thisMin;
        }

        return min;
    }

    @Override
    public Range getRange() {
        // Composite range???
        // if they all have the same range then return the range. else return the nullrange
        return null;
    }

    /** This is not a true standard deviation but a average weighted std deviation. However
     * individual monitors do have a true standard deviation
     */
    @Override
    public double getStdDev() {

        double weightedStdDev=0;
        double totalHits=0;

        for (int i=0;i<numRows;i++) {
            double hits=monitors[i].getHits();
            weightedStdDev=hits*monitors[i].getStdDev();
            totalHits+=hits;
        }

        if (totalHits==0)
            return 0;
        else
            return weightedStdDev/totalHits;
    }

    @Override
    public double getTotal() {
        double value=0;
        for (int i=0;i<numRows;i++) {
            value+=monitors[i].getTotal();
        }

        return value;
    }


    /** It just takes one of the monitors to not be enabled for the composite to be false */
    @Override
    public boolean isEnabled() {
        for (int i=0;i<numRows;i++) {
            if (!monitors[i].isEnabled())
                return false;
        }

        return true;
    }

    /** It just takes one of the monitors to not be primary for the composite to be false */
    @Override
    public boolean isPrimary() {
        for (int i=0;i<numRows;i++) {
            if (!monitors[i].isPrimary())
                return false;
        }

        return true;
    }

    @Override
    public void setActive(double value) {
        for (int i=0;i<numRows;i++)
            monitors[i].setActive(0);
    }

    @Override
    public void setFirstAccess(java.util.Date date) {
        for (int i=0;i<numRows;i++)
            monitors[i].setFirstAccess(date);

    }

    @Override
    public void setHits(double value) {
        for (int i=0;i<numRows;i++)
            monitors[i].setHits(value);
    }

    @Override
    public void setLastAccess(java.util.Date date) {
        for (int i=0;i<numRows;i++)
            monitors[i].setLastAccess(date);
    }

    @Override
    public void setLastValue(double value) {
        for (int i=0;i<numRows;i++)
            monitors[i].setLastValue(value);
    }

    @Override
    public void setMax(double value) {
        for (int i=0;i<numRows;i++)
            monitors[i].setMax(value);
    }


    @Override
    public void setMaxActive(double value) {
        for (int i=0;i<numRows;i++)
            monitors[i].setMaxActive(value);

    }

    @Override
    public void setMin(double value) {
        for (int i=0;i<numRows;i++)
            monitors[i].setMin(value);
    }

    @Override
    public void setPrimary(boolean isPrimary) {
        for (int i=0;i<numRows;i++)
            monitors[i].setPrimary(isPrimary);
    }

    @Override
    public void setTotal(double value) {
        for (int i=0;i<numRows;i++)
            monitors[i].setTotal(value);
    }

    @Override
    public void setTotalActive(double value) {
        for (int i=0;i<numRows;i++)
            monitors[i].setTotalActive(value);
    }

    @Override
    public Monitor start() {
        for (int i=0;i<numRows;i++)
            monitors[i].start();

        return this;
    }

    @Override
    public Monitor stop() {
        for (int i=0;i<numRows;i++)
            monitors[i].stop();

        return this;
    }


    private MonitorImp getFirstMon() {
        return (MonitorImp)monitors[0];
    }

    @Override
    public Monitor add(double value) {
        return add(1, value);
    }

    @Override
    public Monitor add(int hits, double value) {
        for (int i=0;i<numRows;i++)
            monitors[i].add(hits,value);

        return this;
    }


    @Override
    public boolean hasListeners() {
        if (super.hasListeners())
            return true;

        for (int i=0;i<numRows;i++) {
            if (monitors[i].hasListeners())
                return true;
        }

        return false;
    }


    @Override
    public JAMonDetailValue getJAMonDetailRow() {
        return JAMonDetailValue.NULL_VALUE;
    }


    @Override
    public void setActivityTracking(boolean trackActivity) {
        super.setActivityTracking(trackActivity);
        for (int i=0;i<numRows;i++)
            monitors[i].setActivityTracking(trackActivity);

    }

    /**
     * @since  2.79
     * @return a deep copy of this object
     */
    public MonitorComposite copy() {
        return SerializationUtils.deepCopy(this);
    }


}
