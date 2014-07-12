package com.jamonapi.utils;

import java.util.LinkedList;
import java.util.List;

/** First-in, first-out buffer.  When the BufferList is filled the first element is removed to
 * make room for the newest value, then the second oldest etc.  Used in BufferList and subsequently
 * JAMonBufferListeners.
 *
 */
public class FIFOBufferHolder implements BufferHolder {
    private static final long serialVersionUID = 278L;
    private LinkedList bufferList=new LinkedList();

    public void add(Object replaceWithObj) {
        bufferList.addLast(replaceWithObj);

    }

    public void remove(Object replaceWithObj) {
        bufferList.removeFirst();
    }

    public boolean shouldReplaceWith(Object replaceWithObj) {
        return true;
    }

    public List getCollection() {
        return bufferList;
    }

    public List getOrderedCollection() {
        return bufferList;

    }

    public void setCollection(List list) {
        this.bufferList=(LinkedList) list;

    }

    public BufferHolder copy() {
        return new FIFOBufferHolder();
    }

}
