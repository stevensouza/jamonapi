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

	/**
	 * A key implmentation for label, and units type monitors.
	 */

	private Map keyMap;
	private String rangeKeyStr;

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
		return keyMap.get(key);
	}

	/**
	 * This method is called automatically by a HashMap when this class is used
	 * as a HashMap key. A Coordinate is considered equal if its x and y
	 * variables have the same value.
	 */

	public boolean equals(Object compareKey) {
		
	   if (compareKey instanceof MonKeyBase) {
		   MonKeyBase key=(MonKeyBase) compareKey;
		   return keyMap.equals(key.getMonKeyMap());
	   } else 
		   return false;
   
	}

	/** Used when key is put into a Map to look up the monitor */
	public int hashCode() {
		return keyMap.hashCode();
	}
	
	/** Return the map underlying this Object */
	public Map getMonKeyMap() {
		return keyMap;
	}

	/** Puts the word 'Label' into the list.  Used in the basic JAMon report */
	public List getBasicHeader(List header) {
		header.add(LABEL_HEADER);
		return header;
	}

	/** Returns each key in the map as a header element in the list */
	public List getDisplayHeader(List header) {
		return getHeader(header);
	}

	/** Returns each key in the map as a header element in the list */
	public List getHeader(List header) {
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

	
	public String getDetailLabel() {
		
		Iterator iter=keyMap.entrySet().iterator();
		StringBuffer buff=new StringBuffer();
		while(iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object value=entry.getValue();
			if (value instanceof MonKeyItem)
			  value=((MonKeyItem)value).getDetailLabel();
			
			buff.append(value).append(", ");
			
		}
		
		return buff.toString();
	}
	
	private void testDisplay() {

		System.out.println("\n***");
		System.out.println("toString()="+this);
		System.out.println("getValue calls="+getValue("fn")+", "+getValue("ln")+", "+getValue("age"));
		System.out.println("getRangeKey()="+getRangeKey());
	
		// Header calls
		System.out.println("\nHeader calls");
		System.out.println("getBasicHeader()="+getBasicHeader(new ArrayList()));
		System.out.println("getHeader()="+getHeader(new ArrayList()));
		System.out.println("getDisplayHeader()="+getDisplayHeader(new ArrayList()));

		// Data Calls
		System.out.println("\nData calls");
		System.out.println("getBasicRowData()="+getBasicRowData(new ArrayList()));
		System.out.println("getRowData()="+getRowData(new ArrayList()));
		System.out.println("getRowDisplayData()="+getRowDisplayData(new ArrayList()));
		
	}
	
	

	
	
	public static void main(String[] arg) {
		LinkedHashMap steveMap=new LinkedHashMap();
		LinkedHashMap mindyMap=new LinkedHashMap();
		
		steveMap.put("fn","steve");
		steveMap.put("ln","souza");
		steveMap.put("age", new Long(44));
	
		mindyMap.put("fn","mindy");
		mindyMap.put("ln","bobish");
		mindyMap.put("age", new Long(33));
		
		MonKey steveMonKey=new MonKeyBase("Steves Range", steveMap);
		MonKey steveMonKey2=new MonKeyBase(steveMap);
		MonKey mindyMonKey=new MonKeyBase(mindyMap);
		
		System.out.println("do 2 steves equal="+steveMonKey.equals(steveMonKey));
		System.out.println("do mindy and steve equal="+steveMonKey.equals(mindyMonKey));

		((MonKeyBase)steveMonKey).testDisplay();
		((MonKeyBase)steveMonKey2).testDisplay();
		((MonKeyBase)mindyMonKey).testDisplay();
			

	}

//	public String getSummmaryLabel() {
//	    // TODO Auto-generated method stub
//	    return null;
//    }


}
