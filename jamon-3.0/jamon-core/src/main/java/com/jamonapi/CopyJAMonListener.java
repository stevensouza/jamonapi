package com.jamonapi;

/** A listener implements this interface if it also can act as a factory.  copy()
 * should return an exact copy of the JAMonListener
 * 
 * @author steve souza
 */
public interface CopyJAMonListener {
    public JAMonListener copy();
}
