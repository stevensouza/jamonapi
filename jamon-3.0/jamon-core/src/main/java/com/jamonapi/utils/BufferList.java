package com.jamonapi.utils;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * This object can contain a configurable number of items in a buffer. The items kept are rows in a 2 dim
 * array and so the data can be viewed in a table.  It is used in jamon to store recent exceptions, and
 * sql queries from the various proxy classes.  However it may be used elsewhere.  It is thread safe.
 * By default the buffer holds 50 elements, but this can be overridden in the constructor.
 *
 * <p>It uses a bufferHolder to determine whether a value should be added and another one removed when the buffer is full.  For example
 * the value could only be added if the new value is greater than the smallest member of the BufferList.  Simply implement the BufferHolder
 * interface to implement your desired rules.
 * </p>
 */

public class BufferList implements DetailData {

    private static final long serialVersionUID = 278L;

    private boolean enabled = true;
    private int bufferSize = 50;
    private String[] header;
    private BufferHolder bufferHolder;

    /**
     * Constructor that takes the header of the structure of the rows that are stored.
     * For example header could be {"Time", "Exception info",};
     * Uses a FIFOBuffer.
     *
     * @param header
     */
    public BufferList(String[] header) {
        this.header = header;
        this.bufferHolder = new FIFOBufferHolder();
    }

    /**
     * Pass in the header and bufferHolder to be used
     */
    public BufferList(String[] header, BufferHolder bufferHolder) {
        this.header = header;
        this.bufferHolder = bufferHolder;
    }

    /**
     * Use a FIFOBuffer and specify its header and size
     */
    public BufferList(String[] header, int bufferSize) {
        this.header = header;
        this.bufferSize = bufferSize;
        this.bufferHolder = new FIFOBufferHolder();
    }

    /**
     * Specify the header, bufferSize and BufferHolder to be used in the BufferList
     */
    public BufferList(String[] header, int bufferSize, BufferHolder bufferHolder) {
        this.header = header;
        this.bufferSize = bufferSize;
        this.bufferHolder = bufferHolder;
    }

    private BufferList(String[] header, int bufferSize, boolean enabled, BufferHolder bufferHolder) {
        this.header = header;
        this.bufferSize = bufferSize;
        this.enabled = enabled;
        this.bufferHolder = bufferHolder;
    }

    /**
     * Get the number of Exceptions that can be stored in the buffer before the oldest entries must
     * be removed.
     */
    public synchronized int getBufferSize() {
        return bufferSize;
    }


    public synchronized void setBufferHolder(BufferHolder bufferHolder) {
        this.bufferHolder = bufferHolder;
    }

    /**
     * Set the number of Exceptions that can be stored in the buffer before the oldest entries must
     * be removed.  A value of 0 will disable the collection of Exceptions in the buffer.  Note if
     * MonProxy is disabled exceptions will also not be put in the buffer.
     */
    public synchronized void setBufferSize(int newBufferSize) {

        if (bufferSize > newBufferSize)
            resetBuffer(reduceBuffer(newBufferSize));

        bufferSize = newBufferSize;

    }

    /**
     * Reduce size of buffer while not losing current elements beyond what the size reduction would cause.  If the size is increased then no loss
     * of data occurs.
     */
    private LinkedList reduceBuffer(int newSize) {
        LinkedList newBuffer = new LinkedList();
        List original = bufferHolder.getOrderedCollection();
        Collections.reverse(original);// reverse to save the most recent values
        Iterator iter = original.iterator();
        int i = 0;
        while (iter.hasNext() && i < newSize) {
            newBuffer.add(iter.next());
            i++;
        }

        return newBuffer;
    }


    /**
     * Remove all Exceptions from the buffer.  Not sure why this was needed vs reset()
     */
    public synchronized void resetBuffer() {
        reset();
    }

    private void resetBuffer(LinkedList list) {
        bufferHolder.setCollection(list);
    }

    /**
     * Return true if the bufferList is empty
     */
    public boolean isEmpty() {
        return getRowCount() == 0;
    }

    /**
     * Return true if the bufferList has data
     */
    public boolean hasData() {
        return !isEmpty();
    }

    /**
     * Return the rows in the BufferList
     */
    public int getRowCount() {
        return (bufferHolder == null) ? 0 : bufferHolder.getCollection().size();
    }

    /**
     * Return the underlying Collection that holds the BufferList
     */
    public List getCollection() {
        return bufferHolder.getCollection();
    }


    /**
     * Returns true if MonProxy is enabled.
     */
    public synchronized boolean isEnabled() {
        return enabled;
    }

    /**
     * Enable monitoring
     */
    public synchronized void enable() {
        enabled = true;
    }

    /**
     * Disable monitoring
     */
    public synchronized void disable() {
        enabled = false;
    }

    /**
     * Reset BufferList.  It will empty the buffer and leave its size at the current value
     */
    public synchronized void reset() {
        bufferHolder.setCollection(new LinkedList());
    }

    /**
     * Get the header that can be used to display the buffer.  Use getDetailData() method instead.
     */
    public String[] getHeader() {
        return header;
    }

    /**
     * Get the  buffer as an array, so it can be displayed.  Use getDetailData() method instead.
     */
    @Deprecated
    public Object[][] getData() {
        return new BufferListDetailData(this).getData();
    }

    public DetailData getDetailData() {
        return new BufferListDetailData(this);
    }


    /**
     * Add a row to be held in the buffer.  If the buffer is full the oldest one will be removed.
     */
    public synchronized void addRow(Object[] row) {
        addRow((Object) row);
    }

    public BufferHolder getBufferHolder() {
        return bufferHolder;
    }

    public synchronized void addRow(Object obj) {
        if (!enabled || bufferSize <= 0)
            return;

        // remove the oldest element if the buffer is to capacity.
        if (getRowCount() >= bufferSize && bufferHolder.shouldReplaceWith(obj)) {
            bufferHolder.remove(obj);
        }

        if (getRowCount() < bufferSize) {
            bufferHolder.add(obj);
        }
    }


    public synchronized BufferList copy() {
        return new BufferList(header, bufferSize, enabled, bufferHolder.copy());
    }

}
