package com.jamonapi.utils;

import java.lang.reflect.Constructor;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

/** Difficult to group Utilities **/
public class Misc {
    /** Returns an Objects ClassName minus the package name
     * 
     *  <p>Sample Call:</p>
     *      String className=Misc.getClassName("My Object"); // returns "String"
     * 
     **/
    public static String getClassName(Object object) {
        String className = (object==null ? "null" : object.getClass().getName());  // gov.gsa.fss.fim.Misc
        return className.substring(className.lastIndexOf(".")+1);    // Misc
    }

    /** Return Exception information as a row (1 dim array) */
    public static String getExceptionTrace(Throwable exception) {

        // each line of the stack trace will be returned in the array.
        StackTraceElement elements[] = exception.getStackTrace();
        StringBuffer trace = new StringBuffer().append(exception).append("\n");

        for (int i = 0; i < elements.length; i++) {
            trace.append(elements[i]).append("\n");
        }

        return trace.toString();
    }


    /** Note if the passed object is a Colleciton itself or an Object[] it will be expanded */
    public static void addTo(Collection coll, Object objToAdd) {
        if (objToAdd instanceof Collection)
            coll.addAll((Collection)objToAdd);
        else if (objToAdd instanceof Object[])
            coll.addAll(Arrays.asList((Object[])objToAdd));
        else if (objToAdd instanceof ToArray)
            coll.addAll(Arrays.asList(((ToArray)objToAdd).toArray()));
        else
            coll.add(objToAdd);
    }
    
    
    public static String getAsString(Object obj) {
        try {
            if (obj==null)
                return null;
            else if (obj instanceof Collection)
                return getCollAsString((Collection)obj);
            else if (obj instanceof Object[])
                return getArrAsString((Object[]) obj);
            else if (obj instanceof ToArray)
                return getArrAsString(((ToArray)obj).toArray());
            else if (obj instanceof Throwable)
                return getExceptionTrace((Throwable)obj);
            else
                return obj.toString();
        }
        catch(Throwable e) {
            // ignore any exception here since
            return "???";
        }
    }

    private static String getCollAsString(Collection coll) {
        int currentElement=1;
        int lastElement=coll.size();
        Iterator iter=coll.iterator();
        StringBuffer buff=new StringBuffer();

        // loop through elements creating a comma delimeted list of the values
        while(iter.hasNext()) {
            Object obj=iter.next();
            if (obj instanceof Throwable)
                obj=getExceptionTrace((Throwable)obj);

            buff.append(obj);
            if (currentElement!=lastElement)
                buff.append(",\n");

            currentElement++;
        }

        return buff.toString();
    }

    private static String getArrAsString(Object[] arr) {
        int lastElement=arr.length-1;
        StringBuffer buff=new StringBuffer();

        for (int i=0;i<arr.length;i++) {
            Object obj=arr[i];
            if (obj instanceof Throwable)
                obj=getExceptionTrace((Throwable)obj);

            buff.append(obj);
            if (i!=lastElement)
                buff.append(",\n");
        }

        return buff.toString();
    }


    public static void isObjectString(Object arg) {
        if (!(arg instanceof String))
            throw new IllegalArgumentException("Illegal Argument exception: This object must be of type String.");
    }

    /** Sort a 2 dimensional array based on 1 columns data in either ascending or descending order.
     * 
     * @param array - Array to be sorted
     * @param  sortCol - column to sort by
     * @param  sortOrder - sort the column in ascending or descending order.  Valid arguments are "asc" and "desc".
     **/
    public static Object[][] sort(Object[][] array, int sortCol, String sortOrder) {
        ArraySorter sorter=new ArraySorter(array, sortCol, sortOrder);
        return sorter.sort();
    }


    public static void disp(Object[][] data) {
        for (int i=0;i<data.length;i++) {
            System.out.println();
            System.out.print("row="+i+", data=");
            for (int j=0;j<data[i].length;j++)
                System.out.print(data[i][j]+",");
        }

        System.out.println();

    }

    /**
     * @param array  array to do a string trim for all elements
     * @return trimmed array (done in place)
     */
    public static String[] trim(String[] array) {
        int len = (array==null) ? 0 : array.length;
        for (int i=0;i<len;i++) {
            array[i] = array[i].trim();
        }

        return array;
    }


    public static Object[][] allocateArray(int rows, int cols) {
        if (rows<=0 || cols<=0)
            return null;

        Object[][] data=new Object[rows][];
        for(int i=0;i<cols;i++)
            data[i]=new Object[cols];

        return data;
    }


    /** Formats todays date with the passed in date format String.  See the main method for sample Formats */
    public static String getFormattedDateNow(String format) {
        return getFormattedDate(format, new Date());
    }


    /** Formats the passed in date with the passed in date format String.  See the main method for
     * sample Formats.  */
    public static String getFormattedDate(String format, Date date) {
        return new SimpleDateFormat(format).format(date);    // 02
    }

    private static Format monthFormat=new SimpleDateFormat("MM");
    /** Get the month out of the passed in Date.  It would return 05 if you passed in the date
     *  05/15/2004
     */
    public static String getMonth(Date date) {
        return monthFormat.format(date);
    }


    /** Return the 2 digit month of todays date     */
    public static String getMonth() {
        return getMonth(new Date());
    }

    private static Format dayOfWeekFormat=new SimpleDateFormat("E");
    /** Return the day of week from the passed in Date.  For example Mon for Monday. */
    public static String getDayOfWeek(Date date) {
        return dayOfWeekFormat.format(date);
    }

    /** Get the day of week for today.  For example Mon for Monday. */
    public static String getDayOfWeek() {
        return getDayOfWeek(new Date());
    }

    private static Format shortDateFormat=new SimpleDateFormat("MM/dd/yy");
    /** Get the short date for the passed in day.  i.e 05/15/04 */
    public static String getShortDate(Date date) {
        return shortDateFormat.format(date);
    }

    /** Get the short date for Today.  i.e 05/15/04 */
    public static String getShortDate() {
        return getShortDate(new Date());
    }


    /** Create a case insenstive Tree Map.  More of a standard implementation that I wasn't aware
     * of when creating AppMap.  It may only be able to handle Strings as a key.  That isn't clear */
    public static Map createCaseInsensitiveMap() {
        return new TreeMap(String.CASE_INSENSITIVE_ORDER);
    }


    /** Returns a ConcurrentHashMap if the jdk supports (1.5 or higher) it or else it returns a synchronized HashMap */
    public static Map createConcurrentMap(int size) {
        Map map=null;
        try {
            // if the concurrentHashMap is available (jdk 1.5 or higher) then use it.
            Class cls=Class.forName("java.util.concurrent.ConcurrentHashMap");
            // create a constructor that takes the size i.e. new ConcurrentHashMap(size)
            Class constructorArgs[] = new Class[] {Integer.TYPE};
            Constructor ct = cls.getConstructor(constructorArgs);
            Object[] params=new Object[] {new Integer(size)};
            map = (Map) ct.newInstance(params);
        } catch (Exception e) {
            map=Collections.synchronizedMap(new HashMap(size));
        }

        return map;
    }

}

