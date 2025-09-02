package com.jamonapi;


/** Class that contains buckets of time that are tracked within a monitor.
 */
public interface Range  extends JAMonListener {

    /**Return a FrequencyDist associated with the passed in value. */
    public FrequencyDist getFrequencyDist(double value);

    /** Return the array of all FrequencyDists in this range*/
    public FrequencyDist[] getFrequencyDists();

    /** Add a value to the FrequencyDist associated with the value that is passed */
    public void add(double value);

    /** Reset all FrequencyDists associated with this Range */
    public void reset();

    /** Get the logical operator associated with the top end point.  Possible values
     * are < and <=
     **/
    public String getLogicalOperator();

}
