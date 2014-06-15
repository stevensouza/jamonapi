package com.jamonapi.utils;




/** Used with the Command interface to implement the Gang of 4 Command pattern to execute some logic for 
 *  every entry of various iterators.  This class allows a Command object to be passed to various iterators.
 *  This capability is also similar to function pointers in C.
 **/


import java.util.*;

public class CommandIterator extends java.lang.Object {
    private CommandIterator() {
    }
    
    /** Iterate through a ResultSet passing in a Command object.  The command object will be passed an Object[] 
     *  representing 1 row of the result set 
     **/
  /*  public static void iterate(ResultSet resultSet, Command command)throws Exception     {
        ResultSetUtils rsu = ResultSetUtils.createInstance();
        ArrayList arrayList = new ArrayList();
        rsu.resultSetToArrayList(arrayList,resultSet);
        iterate(arrayList, command);
    }
    */
    
    /** Iterate through a Map passing Command object a Map.Entry.   
     * Command code would look something like:
     *   entry = (Map.Entry) object;
     *   entry.getKey(), entry.getValue();
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
    
    /** Test code for this class **/
    public static void main(String argv[]) throws Exception     {
        ArrayList arrayList = new ArrayList();
        Vector vector = new Vector();
        
        class TestCommand implements Command {
            public void execute(Object value) throws Exception {
                System.out.println("command"+value);
            }
            
        };
        
        TestCommand testCommand = new TestCommand();
        
        arrayList.add("1");
        arrayList.add("2");
        arrayList.add("3");
        
        vector.addElement("4");
        vector.addElement("5");
        vector.addElement("6");
        
        iterate(arrayList, testCommand);
        iterate(vector.elements(), testCommand);
    }
    

}

