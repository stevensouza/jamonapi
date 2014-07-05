package com.jamonapi;

import com.jamonapi.utils.Misc;
import com.jamonapi.utils.ToArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Class used to add the label, value and time invoked for the associated monitor.  Used in the
 * jamonBufferListener class.
 * 
 * @author steve souza
 */
public final class JAMonDetailValue implements Serializable, ToArray {
    private static final long serialVersionUID = 2210164449488502225L;
    private final MonKey key;
    private final double value; // monitors lastValue
    private final long time;  // invocation time
    private final double active;
    private boolean keyToString=true;
    private Object[] row;

    static JAMonDetailValue NULL_VALUE=new JAMonDetailValue(new MonKeyImp("Null JAMonDetails Object","Null JAMonDetails Object",""),0,0,0);

    public JAMonDetailValue(MonKey key, double value, double active, long time) {
        this.key=key;
        this.value=value;
        this.active=active;
        this.time=time;
    }

    /** Returns label, value, time as an Object[] of 3 values. */
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

        return row;
    }

    public void setKeyToString(boolean keyToString) {
        this.keyToString=keyToString;
    }

    public boolean isKeyToString() {
        return keyToString;
    }

}
