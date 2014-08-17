package com.jamonapi;

import java.util.*;

/** Class that can be used as a composite key for MonitorFactor.add(compositeKey, 100) method calls
 * Note the passed in LinkedHashMap is used as a key to another Map that looks up the associated monitor.
 * Sun java docs says this regarding keys in Maps:  "Note: great care must be exercised if mutable objects
 * are used as map keys. The behavior of a map is not specified if the value of an
 * object is changed in a manner that affects equals comparisons while the object is a key in the map."
 * So once a value is placed in the LinkedHashMap is passed to the MonKeyBase
 * constructor it should not be changed.
 *
 */
public class MonKeyBase implements MonKey {

    private static final long serialVersionUID = 279L;

    /**
     * A key implmentation for label, and units type monitors.
     */
    private Map keyMap;
    private String rangeKeyStr;
    private Object details;
    private String instanceName=DEFAULT_INSTANCE_NAME;

    /** Calls the other constructor.  The keyMap will be used to create the range name */
    public MonKeyBase(LinkedHashMap keyMap) {
        this(null, keyMap);
    }

    /** The LinkHashMap will contain key value pairs.  For example: fn=steve, ln=souza, age=44. LinkHashMap is used
     * as the insertion order needs to be retained for displaying header, and data.  The rangeKeyString is used
     * to look up an associated range should one exist */
    public MonKeyBase(String rangeKeyStr, LinkedHashMap keyMap) {
        this.rangeKeyStr=rangeKeyStr;
        this.keyMap = (keyMap == null) ? new LinkedHashMap() : keyMap;
    }

    /**
     * Returns any object that has a named key. In this keys case 'label' and
     * 'units' makes sense, but any values are acceptible.
     */
    public Object getValue(String key) {
        if (LABEL_HEADER.equalsIgnoreCase(key))
            return getLabel();
        else if ("details".equalsIgnoreCase(key))
            return getDetails();
        else if ("instanceName".equalsIgnoreCase(key))
            return getInstanceName();
        else
            return keyMap.get(key);
    }

    public String getLabel() {
        return toString();
    }

    /**
     * This method is called automatically by a HashMap when this class is used
     * as a HashMap key. A Coordinate is considered equal if its x and y
     * variables have the same value.
     */

    @Override
    public boolean equals(Object compareKey) {

        if (this==compareKey)
            return true;
        else if (compareKey instanceof MonKeyBase) {
            MonKeyBase key=(MonKeyBase) compareKey;
            return keyMap.equals(key.getMonKeyMap());
        } else
            return false;

    }

    /** Used when key is put into a Map to look up the monitor */
    @Override
    public int hashCode() {
        return keyMap.hashCode();
    }

    /** Return the map underlying this Object */
    public Map getMonKeyMap() {
        return keyMap;
    }

    @Override
    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    @Override
    public String getInstanceName() {
        return instanceName;
    }

    /** Puts the word 'Label' into the list.  Used in the basic JAMon report */
    public List getBasicHeader(List header) {
        header.add(INSTANCE_HEADER);
        header.add(LABEL_HEADER);
        return header;
    }

    /** Returns each key in the map as a header element in the list */
    public List getDisplayHeader(List header) {
        return getHeader(header);
    }

    /** Returns each key in the map as a header element in the list */
    public List getHeader(List header) {
        header.add(INSTANCE_HEADER);
        Iterator iter=keyMap.keySet().iterator();
        while(iter.hasNext()) {
            header.add(iter.next());
        }

        return header;
    }


    /** Returns the values assoicated with the key as a comma delimited entry in the list.
     * For example if the map contains firstname, lastname, age then something like
     * steve, souza, 44 would be returned
     */
    public List getBasicRowData(List rowData) {
        rowData.add(getInstanceName());
        Collection row=keyMap.values();
        int currentElement=1;
        int lastElement=row.size();
        Iterator iter=row.iterator();
        StringBuffer buff=new StringBuffer();

        // loop through elements creating a comma delimeted list of the values
        while(iter.hasNext()) {
            buff.append(iter.next());
            if (currentElement!=lastElement)
                buff.append(", ");

            currentElement++;
        }

        if (buff.length()>0)
            rowData.add(buff.toString());

        return rowData;
    }

    /** Add each value from the map at an element to the list.
     */
    public List getRowData(List rowData) {
        rowData.add(getInstanceName());
        Collection row=keyMap.values();
        Iterator iter=row.iterator();
        // loop through elements creating a comma delimeted list of the values
        while(iter.hasNext()) {
            rowData.add(iter.next());
        }

        return rowData;
    }

    /** Add each value from the map at an element to the list.
     */
    public List getRowDisplayData(List rowData) {
        return getRowData(rowData);
    }

    /** Returns a string representation of this object:  JAMon Key, firstname=steve, lastname=souza, age=44*/
    @Override
    public String toString() {
        Iterator iter=keyMap.entrySet().iterator();
        StringBuffer buff=new StringBuffer("JAMon Key");
        while(iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            buff.append(", ").append(entry.getKey()).append("=").append(entry.getValue());

        }

        return buff.toString();
    }

    /** Returns either the passed in range key, or it builds the key from the maps keys concatenated */
    public String getRangeKey() {
        if (rangeKeyStr==null) {
            StringBuffer buff=new StringBuffer();
            Iterator iter=keyMap.keySet().iterator();
            while(iter.hasNext())
                buff.append(iter.next()).append(" ");

            rangeKeyStr=buff.toString();
        }

        return rangeKeyStr;
    }


    public Object getDetails() {
        if (details==null) {
            List list=new ArrayList();
            Iterator iter=keyMap.entrySet().iterator();
            //StringBuffer buff=new StringBuffer();
            while(iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Object value=entry.getValue();
                if (value instanceof MonKeyItem)
                    value=((MonKeyItem)value).getDetails();

                list.add(value);
            }

            return list;
        } else
            return details;
    }

    public void setDetails(Object details) {
        this.details=details;

    }

    public int getSize() {
        return toString().length();
    }

}
