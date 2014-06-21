package com.jamonapi;
import com.jamonapi.utils.DetailData;

/** Object that contains a listener type such as value, max, min or maxactive.
 * This object can contain multiple objects per listener type.
 */
public final class ListenerType implements DetailData {

    private JAMonListener listener;
    // will lock on MonInternal to allow direct access to listener which improves performance.
    // This brought my TestPerformanceClass performance from about 600 ms. to 484 ms.
    private Object lockObj;

    ListenerType(Object lockObj) {
        this.lockObj=lockObj;
    }
    /** Add the listener to the addTo listener.  If addto is already a composite
     * listener simply add it.  If it is not then create a new CompositeListener
     * and add the current listener as well as the new one being passed in to the new
     * CompositeListener.  Note this code is only called if there is already a Listener
     */
    private CompositeListener addCompositeListener(JAMonListener listenerToAdd) {
        if (listener instanceof CompositeListener)
            return ((CompositeListener) listener).addListener(listenerToAdd);
        else
            return new CompositeListener().addListener(listener).addListener(
                    listenerToAdd);

    }

    /** Get underlying listener class */
    public final JAMonListener getListener() {
        return listener;
    }

    /**
     * Add a listener that receives notification every time this monitors
     * add method is called. If null is passed all associated Listeners will
     * be detached.
     */

    // Some jamon 2.4 introduced methods. Mostly listener related.
    /** Add a listener to this listener type.  Any number of listeners are supported. */
    public  void addListener(JAMonListener listenerToAdd) {
        synchronized(lockObj) {
            // this first case is either 1) the first listener for this type.
            // in this case for performance reasons there is no reason to create
            // a compositeListener at this point, 2) a null was passed so the listener
            // will be nullified. Either way the listener is assigned to the variable
            // representing the Listenertype
            if (listener == null || listenerToAdd == null)
                listener = listenerToAdd;
            else // else add the listener to the composite (create the composite if necessary)
                listener = addCompositeListener(listenerToAdd);
        }

    }



    /** Get a handle to the listener by name.  This handle could be used to access and display a buffer for example */
    public JAMonListener getListener(String listenerName) {
        synchronized(lockObj) {
            // first look at listener to see if it is named that.  if this doesn't match
            // and if it is composite listener and it has a child of given name then
            // return it.
            if (listener == null)
                return null;
            else if (listener.getName().equalsIgnoreCase(listenerName))
                return listener;
            else if (listener instanceof CompositeListener)
                return ((CompositeListener) listener)
                .getListener(listenerName);
            else
                return null;
        }

    }


    /** Remove the named listener */
    public void removeListener(String listenerName) {
        synchronized(lockObj) {
            // if passed value, max, min, or maxactive
            if (listener==null)
                return;
            else if (listener.getName().equalsIgnoreCase(listenerName))
                listener=null;
            else if (listener instanceof CompositeListener) {
                CompositeListener compListener = (CompositeListener) listener;
                compListener.removeListener(listenerName);

                // If the composite listener is empty nullify it
                // else if it has one listener use it by itself and get rid
                // of the CompositeListener.
                if (compListener.getNumListeners() == 0)
                    listener=null;
                else if (compListener.getNumListeners() == 1)
                    listener=compListener.getListener(0);// get the only listener
            }
        }
    }


    /** Returns true if any listeners exist */
    public boolean hasListeners() {
        synchronized(lockObj) {
            return (listener==null) ? false : true;
        }

    }


    /** Returns true if listener type exists (value/max/min/maxactive) or listener exists
     * by name.
     */
    public boolean hasListener(String listenerName) {
        synchronized(lockObj) {

            if (listener == null)
                return false;
            else if (listener.getName().equalsIgnoreCase(listenerName))
                return true;
            else if (listener instanceof CompositeListener)
                return ((CompositeListener) listener)
                .hasListener(listenerName);
            else
                return false;
        }
    }


    /** Return listeners for display purposes in menus for example */
    public Object[][] getData() {
        synchronized(lockObj) {

            if (listener==null)
                return null;
            else if (listener instanceof CompositeListener) {
                CompositeListener compListener=(CompositeListener) listener;
                return compListener.getData();
            } else
                return new CompositeListener().addListener(listener).getData();
        }
    }


    /** Return header info for display purposes */
    public String[] getHeader() {
        synchronized(lockObj) {

            if (listener instanceof CompositeListener) {
                CompositeListener compListener=(CompositeListener) listener;
                return compListener.getHeader();
            } else
                return new CompositeListener().getHeader();
        }
    }

}
