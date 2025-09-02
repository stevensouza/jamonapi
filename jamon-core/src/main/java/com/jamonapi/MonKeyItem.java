package com.jamonapi;

import java.io.Serializable;

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

public interface MonKeyItem extends Serializable {
    static final long serialVersionUID = 279L;
    static final String INSTANCE_HEADER = "Instance";
    static final String DEFAULT_INSTANCE_NAME = "local";

    public Object getDetails();
    public void setDetails(Object details);

    /** Now that jamon can track data on multiple vm's we need a way to better identify the keys.  A typical
     * value for this might be: myapp_jetty9_production.  It is not part of the key used for aggregation (in the map)
     * but is used for informational purposes in the jamon report
     *
     * @param instanceName
     */
    public void setInstanceName(String instanceName);
    public String getInstanceName();

}
