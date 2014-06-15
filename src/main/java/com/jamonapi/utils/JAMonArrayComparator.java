
package com.jamonapi.utils;  // FormattedDataSet API

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/** * This class allows you to compare Object[] arrays by multiple columns.  It can also compare any Object that implements the ToArray interface. * Each column will use the underlying * Compareable interface in natural or reverse order depending on how it is added, or will use the passed in Comparator. *  * 
 * <p>Note I took this code from fdsapi.com, and would like to eventually merge these 2 projects, so
 * this class will eventually be replaced by the one in FDS. */
public class JAMonArrayComparator extends java.lang.Object implements Comparator
{    private List sortCols=new ArrayList();    private static final Comparator defaultComparator=new JAMonComparator();
    public JAMonArrayComparator() {    }
    /** Sort/compare the passed in col number starting at 0 in natural (true) or reverse (false)     * order based on the columns Compareable interface being called.
     *      * @param sortCol     * @param naturalOrder     */
    public JAMonArrayComparator(int sortCol, boolean naturalOrder) {        addCompareCol(sortCol, naturalOrder);    }
    /** Compare the passed in col in natural order */
    public void addCompareCol(int sortCol) {        sortCols.add(new ArrayElementComparator(sortCol, true));    }    /** Compare the passed in col in natural or reverse order */
    public void addCompareCol(int sortCol, boolean naturalOrder) {        sortCols.add(new ArrayElementComparator(sortCol, naturalOrder));
    }    /** Compare the passed in col based on the passed in Comparator */    public void addCompareCol(int sortCol, Comparator comparator) {        sortCols.add(new ArrayElementComparator(sortCol, comparator));    }
    /**     * Method used by the comparator interface.
     * <pre>     *   o1 < o2 - returns a negative integer     *   o1 == o2 - returns zero     *   o1 > o2 - returns a postitive integer
     * </pre>
     *      *  <p>Iterate through all columns that should be compared (in the proper order)     *  and call the Comparator for each of the column elements.     */
    public int compare(Object o1, Object o2) {        Object[] array1=null;        Object[] array2=null;        int compareVal=0;        // Put data into array format or call the underlying defaultComparator if        // the data isn't tabular        if (o1 instanceof ToArray  &&  o2 instanceof ToArray) {            array1=((ToArray)o1).toArray();            array2=((ToArray)o2).toArray();        } else if (o1 instanceof Object[] && o2 instanceof Object[]) {            array1=(Object[])o1;            array2=(Object[])o2;        } else {            return defaultComparator.compare(o1, o2);        }        /** Iterate through the Arrays comparators to compare columns */        Iterator iter=sortCols.iterator();        while (iter.hasNext()) {            ArrayElementComparator elementComp=(ArrayElementComparator) iter.next();            Object element1=array1[elementComp.getSortCol()];            Object element2=array2[elementComp.getSortCol()];            compareVal=elementComp.compare(element1,element2);            if (compareVal!=0) // if one of the elments is not equal we know all we need to know and can return                break;        }        return compareVal;    }

}

