package com.jamonapi;

import com.jamonapi.utils.BufferList;


/** Class that works like JAMonBufferListeners but allows users to share buffers
 *  between monitors with the jamon gui...
 * 
 * @author steve souza
 *
 */

public class SharedJAMonBufferListener extends JAMonBufferListener {
    private static final long serialVersionUID = 278L;
    private int ID=1;
    private boolean factory=true;

    public SharedJAMonBufferListener() {
        super("SharedJAMonBufferListener");
    }

    /** Pass in the jamonListener name */
    public SharedJAMonBufferListener(String name){
        super(name);
    }

    /** Name the listener and pass in the jamon BufferList to use */
    public SharedJAMonBufferListener(String name, BufferList list) {
        super(name, list);
    }

    /** Returns a usable copy of this object and puts that object into the JAMonListenerFactory so it can be used
     * by other Monitor's
     */
    @Override
    public JAMonListener copy() {
        // If this is a factory creator it creates a numbered version and it
        // can be used to share between different monitors
        if (factory) {
            SharedJAMonBufferListener listener=new SharedJAMonBufferListener("_"+getName()+getNextID(), getBufferList().copy());
            listener.setFactoryInstance(false);
            JAMonListenerFactory.put(listener);
            return listener;
        } else
            return this;

    }

    private synchronized int getNextID() {
        return ID++;
    }

    private void setFactoryInstance(boolean factoryInstance) {
        factory=factoryInstance;
    }

}
