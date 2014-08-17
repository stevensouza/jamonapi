package com.jamonapi;

import java.util.List;

/**
 * A key implmentation for label, and units type monitors.
 * Note this could also be implemented with the following use of MonKeyBase.  This
 * class predates that one and would not have to use a Map for basic functions
 * and so MAY be more efficient (this wasn't tested).  Using label, and units
 * is the most common monitor that will be used in most cases.>
 * 
 *  <p>This could be implemented like the following.</p>
 *   <pre>{@code
 *    LinkedHashMap lm=new LinkedHashMap();
 *    lm.put("Label", "mypakcage.myclass");
 *    lm.put(""Units", "ms.");
 *    MonKey mk=new MonKeyBase(lm);
 * }</pre>
 * 
 */

public class MonKeyImp implements MonKey {

    private static final long serialVersionUID = 279L;
    private final String summaryLabel; // pageHits for example.  A relatively unique key value to be used in a hash map
    private Object details; // The actual page name for the detail buffer.  pageHits for example
    private final String units; // ms. for example
    private Object param;
    private String instanceName=DEFAULT_INSTANCE_NAME;

    public MonKeyImp(String summaryLabel, String units) {
        this(summaryLabel, summaryLabel, units);
    }

    /** Object details can be an Object[], a Collection, or a Java Object.  */
    public MonKeyImp(String summaryLabel, Object details, String units) {
        this.summaryLabel = (summaryLabel==null) ? "" : summaryLabel;
        this.details = details;
        this.units= (units==null) ? "" : units;
    }

    public MonKeyImp(MonKeyItem keyItem, String units) {
        this.summaryLabel = (keyItem==null) ? "" : keyItem.toString();
        this.units= (units==null) ? "" : units;
        this.details=keyItem.getDetails();
    }


    /** Returns the label for the monitor */
    public String getLabel() {
        return summaryLabel;
    }

    /** Returns the units for the monitor */
    public String getUnits() {
        return units;
    }

    public Object getDetails() {
        return details;
    }


    public void setDetails(Object details) {
        this.details=details;

    }

    @Override
    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    @Override
    public String getInstanceName() {
        return instanceName;
    }

    /** Returns any object that has a named key.  In this keys case
     * 'label' and 'units' makes sense, but any values are acceptible.
     */
    public Object getValue(String key) {
        if (LABEL_HEADER.equalsIgnoreCase(key))
            return getLabel();
        else if (UNITS_HEADER.equalsIgnoreCase(key))
            return getUnits();
        else if ("param".equalsIgnoreCase(key))
            return getParam();
        else if ("details".equalsIgnoreCase(key))
            return getDetails();
        else if ("instanceName".equalsIgnoreCase(key))
            return getInstanceName();
        else
            return null;

    }

    /** Used to get any arbitrary Object into the key.  It will not be used as part of the key, however it can be retrieved later for example
     * in the JAMonBufferListener.
     * 
     * @return Object
     */
    public Object getParam() {
        return param;
    }


    /** Used to set any arbitrary Object into the key.  It will not be used as part of the key, however it can be retrieved later for example
     * in the JAMonBufferListener.
     * 
     */
    public void setParam(Object param) {
        this.param=param;
    }

    /** This method is called automatically by a HashMap when this class is used as a HashMap key.
     * A Coordinate is considered equal if its x and y variables have the same value.
     */
    @Override
    public boolean equals(Object compareKey) {

        return (
                compareKey instanceof MonKeyImp &&
                summaryLabel.equals(((MonKeyImp) compareKey).summaryLabel) &&
                units.equals(((MonKeyImp) compareKey).units)
        );

    }

    /** Used when key is put into a Map to look up the monitor */
    @Override
    public int hashCode() {
        return (summaryLabel.hashCode() + units.hashCode());
    }

    public List getBasicHeader(List header) {
        header.add(INSTANCE_HEADER);
        header.add(LABEL_HEADER);
        return header;
    }


    public List getDisplayHeader(List header) {
        return getHeader(header);
    }

    public List getHeader(List header) {
        header.add(INSTANCE_HEADER);
        header.add(LABEL_HEADER);
        header.add(UNITS_HEADER);
        return header;
    }

    public List getBasicRowData(List rowData) {
        rowData.add(getInstanceName());
        rowData.add(getLabel()+", "+getUnits());
        return rowData;
    }


    public List getRowData(List rowData) {
        rowData.add(getInstanceName());
        rowData.add(getLabel());
        rowData.add(getUnits());

        return rowData;
    }

    public List getRowDisplayData(List rowData) {
        return getRowData(rowData);
    }


    @Override
    public String toString() {
        return new StringBuffer().append("JAMon Label=").append(getLabel()).append(", Units=").append(getUnits()).toString();

    }

    public String getRangeKey() {
        return getUnits();
    }

    public int getSize() {
        return summaryLabel.length();
    }


}
