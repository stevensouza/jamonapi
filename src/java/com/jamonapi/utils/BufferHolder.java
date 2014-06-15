package com.jamonapi.utils;

import java.util.List;


/** Interface used to add/remove values from a BufferList based on whether shouldReplaceWith(...)
 * returns true or not.
 *
 */
public interface BufferHolder {
    /** Remove the smallest element from the BufferList if the buffer is full and  shouldReplaceWith(...)
     * returns true.
     * 
     * @param replaceWithObj
     */
    public void remove(Object replaceWithObj);

    /** Add the passed object to the array if BufferList is not full or shouldReplaceWith returns true*/
    public void add(Object replaceWithObj);

    /** Returns true if this object is greater than the smallest value in the buffer */
    public boolean shouldReplaceWith(Object replaceWithObj);

    /** Get the underlying collection */
    public List getCollection();

    /** Get the Collection in sorted order */
    public List getOrderedCollection();

    public void setCollection(List list);

    /** return a usable copy of the BufferHolder */
    public BufferHolder copy();
}
