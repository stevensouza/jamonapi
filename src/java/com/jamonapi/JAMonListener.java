package com.jamonapi;

import java.util.EventListener;

/** Interface that can be implemented if you want to code something to listen for JAMon events
 *  such as a new max/min/max active have occured, or even if the monitor has fired.  It also implements
 *  the java EventListener tag interface.
 * 
 * @author steve souza
 *
 */

public interface JAMonListener extends EventListener {
    /** Gets the name of this listener */
    public String getName();

    /** Set the name of the listener */
    public void setName(String name);

    /** Called and the current monitor is past in.  This allows for the listener to take any action such as looking for new maximums, logging the information or anything else that is useful */
    public void processEvent(Monitor mon);
}
