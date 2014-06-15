package com.jamonapi;

/** Used for MonKey to allow jamon to have the generalized form of the key for aggregation, and the
 * more specific form for writing out details say to a buffer.
 * 
 * <p>For example the generalized form of a query would be something like:  select * from table where
 * name=?.  The detail form would be something like:  select * from table where name='steve'</p>
 * 
 * <p>MonKeyItems can be placed as objects in MonKeyImp, and MonKeyBase</p>
 * 
 * <p>Note toString() should always return the more generalized form.</p>
 * 
 * @author steve souza
 *
 */

public interface MonKeyItem {
    public Object getDetails();

    public void setDetails(Object details);
}
