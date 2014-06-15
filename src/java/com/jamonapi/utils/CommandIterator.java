package com.jamonapi.utils;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/** Used with the Command interface to implement the Gang of 4 Command pattern to execute some logic for
 *  every entry of various iterators.  This class allows a Command object to be passed to various iterators.
 *  This capability is also similar to function pointers in C.
 **/
public class CommandIterator extends java.lang.Object {

    private CommandIterator() {
    }

    /** Iterate through a Map passing Command object a Map.Entry.
     * 
     * <p>Command code would look something like:</p>
     * <pre>{@code
     *   entry = (Map.Entry) object;
     *   entry.getKey(), entry.getValue();
     * }</pre>
     **/
    public static void iterate(Map map, Command command)throws Exception     {
        iterate(map.entrySet().iterator() , command);
    }

    /** Iterate through a Collection passing the Command object each element in the collection. **/
    public static void iterate(Collection collection, Command command)throws Exception     {
        iterate(collection.iterator() , command);
    }


    /** Iterate through an Enumeration passing the Command object each element in the Collection **/
    public static void iterate(Enumeration enumer, Command command)throws Exception     {
        iterate(new EnumIterator(enumer) , command);
    }

    /** Iterate passing each Command each Object that is being iterated **/
    public static void iterate(Iterator iterator, Command command)throws Exception     {
        while (iterator.hasNext())  {
            command.execute(iterator.next());
        }
    }

}

