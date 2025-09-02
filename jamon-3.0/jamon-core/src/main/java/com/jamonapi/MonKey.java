package com.jamonapi;

/** Key that allows for a monitor to be passed any number of keys used in the equivalent
 * of a group by clause.  Put in hashmap to identify a Monitor.  Implementations  will need
 * to implement equals, and hashcode.   MonKeys are the way Monitors are identified in the
 * storing Map
 */

public interface MonKey extends RowData, MonKeyItem {
    static final String LABEL_HEADER="Label";
    static final String UNITS_HEADER="Units";

    /** return any value associated with the key.
     * new MonKey(label, units).  would return the value associated with label
     * or units if:  getValue("label"), or getValue("units");
     */
    public Object getValue(String primaryKey);

    /** Uses this value to look up an associated Range */
    public String getRangeKey();

    public String getLabel();

    /** Returns the size of the key in characters within the key.  Note it doesn't calculate the actual memory footprint
     * as unicode characters take more memory than a single character in a string. This info is used to limit the memory
     * footprint of jamon.
     * 
     * @return returns the size of the key
     */
    public int getSize();

}
