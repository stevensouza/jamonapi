package com.jamonapi;

import com.jamonapi.utils.DetailData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** A class that can contain other listeners that can listen to jamon events of interest.
 * These classes will all implement the JAMonListener interface too.  This is an example of the
 * Gang of 4 Composite design pattern.
 * 
 * @author steve souza
 *
 */
public class CompositeListener implements JAMonListener, DetailData {

    private static final long serialVersionUID = 278L;

    // A variable that will hold the list of Listeners
    private List listenerList=new ArrayList(4);
    // The name of the composite listener
    private String name;

    /** Uses the CompositeListener name */
    public CompositeListener() {
        this("CompositeJAMonListener");
    }

    /** Pass in a Listener name that allows you to differentiate this listener from others */
    public CompositeListener(String name) {
        this.name=name;
    }

    /** Add a listener to the composite and return this object */
    public CompositeListener addListener(JAMonListener listener) {
        // added a check to not have the same listener name like FIFOBuffer.  This will allow for one time initialization of
        // a listener such as a FIFOBuffer on an exception monitor.  Repeat adding of a listener with this name
        // will have no effect.  This is useful if different parts of the program initialize an exception listener for example.
        if (listener instanceof  CompositeListener || !hasListener(listener.getName())) {
            listenerList.add(listener);
        }
        return this;
    }

    /** Return the listener associated with the passed in name */
    public JAMonListener getListener(String listenerName) {
        int rows=getNumListeners();

        for (int i=0;i<rows;i++) {
            JAMonListener listener=(JAMonListener) listenerList.get(i);
            String name=listener.getName();
            if (listenerName.equalsIgnoreCase(name))
                return listener;
            else if (listener instanceof CompositeListener) {
                listener = ((CompositeListener)listener).getListener(listenerName);
                if (listener!=null)
                    return listener;
            }
        }

        return null;
    }


    /** Return the listener associated with the index */
    public JAMonListener getListener(int index) {
        return (JAMonListener) listenerList.get(index);
    }

    /** Remove the named listener from this CompositeListener */
    public CompositeListener removeListener(String listenerName) {
        int rows=getNumListeners();

        for (int i=0;i<rows;i++) {
            JAMonListener listener=(JAMonListener) listenerList.get(i);
            String name=listener.getName();
            if (listenerName.equalsIgnoreCase(name)) {
                listenerList.remove(i);
                break;
            } 	else if (listener instanceof CompositeListener && ((CompositeListener)listener).hasListener(listenerName)) {
                ((CompositeListener)listener).removeListener(listenerName);
                break;
            }

        }

        return this;
    }

    /** return true if the named listener exists */
    public boolean hasListener(String listenerName) {
        int rows=getNumListeners();

        for (int i=0;i<rows;i++) {
            JAMonListener listener=(JAMonListener) listenerList.get(i);
            String name=listener.getName();
            if (listenerName.equalsIgnoreCase(name))
                return true;
            else if (listener instanceof CompositeListener) {
                return ((CompositeListener)listener).hasListener(listenerName);
            }
        }

        return false;
    }

    /** Return the number of listeners */
    public int getNumListeners() {
        return listenerList.size();
    }


    /** Also returns the number of listeners */
    public int getRowCount() {
        return getNumListeners();
    }

    /** Return the name of this instance */
    public String getName() {
        return name;
    }

    /** sets the name of this monitor instance */
    public void setName(String name) {
        this.name=name;

    }

    /** Notify all listeners that are part of this composite of a jamon event and pass them the
     *  monitor that triggered the event.
     */
    public void processEvent(Monitor mon) {
        Iterator iter=listenerList.iterator();

        while (iter.hasNext()) {
            JAMonListener listener=(JAMonListener)iter.next();
            listener.processEvent(mon);
        }

    }

    /** Get an iterator that will contain the Composite's JAMonListener objects.  The objects will
     * be safe cast to JAMonListener */
    public Iterator iterator() {
        return listenerList.iterator();
    }

    public boolean isEmpty() {
        return listenerList.isEmpty();
    }

    public boolean hasData() {
        return !isEmpty();
    }


    /** Return all the listeners in the composite */
    public Object[][] getData() {
        if (isEmpty())
            return null;

        int numListeners=getNumListeners();
        List list=new ArrayList();
        for (int i=0;i<numListeners;i++) {
            add(list, getListenerData(getListener(i)));
        }

        return toArray(list);

    }

    private void add(List list, Object[][] data) {
        int dataSize=(data==null) ? 0 : data.length;
        for (int i=0;i<dataSize;i++)
            list.add(data[i]);

    }

    private Object[][] getListenerData(JAMonListener listener) {
        if (listener instanceof CompositeListener)
            return ((CompositeListener)listener).getData();
        else
            return new Object[][]{{listener.getName()}};
    }

    private Object[][] toArray(List list) {
        if (list.size()==0)
            return null;
        else {
            Object[][] listenerArray=new Object[list.size()][];
            list.toArray(listenerArray);
            return listenerArray;
        }
    }

    private static final String[] HEADER={"ListenerName"};
    public String[] getHeader() {
        return HEADER;
    }

    public static Object[][] getData(JAMonListener listener) {
        if (listener==null)
            return null;
        // don't need to wrap listener if it is already a compositelistener, but
        // it makes the following code easier.
        return new CompositeListener().addListener(listener).getData();
    }

    public static String[] getHeader(JAMonListener listener) {
        // At this point all headers are the same.
        return HEADER;
    }

}
