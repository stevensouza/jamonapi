package com.jamonapi.utils;



/** Simple Wrapper utility class that makes an Enumeration behave like an Iterator.  **/

import java.util.*;

public class EnumIterator extends java.lang.Object implements java.util.Iterator {
    Enumeration enumer;

    public EnumIterator(Enumeration enumer) {
        this.enumer=enumer;
    }
    
    public boolean hasNext() {
        return enumer.hasMoreElements();
    }
    
    public Object next() {
        return enumer.nextElement();
    }
    
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
}

