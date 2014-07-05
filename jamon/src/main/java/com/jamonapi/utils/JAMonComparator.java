package com.jamonapi.utils;

import java.io.Serializable;
import java.util.Comparator;

public class JAMonComparator implements Comparator, Serializable {
    private static final int LESSTHAN=-1;
    private static final int EQUALTO=0;
    private static final int GREATERTHAN=1;
    private static final long serialVersionUID = -3900778549163179687L;
    private boolean naturalOrder=true;
    private Comparator childComparator=null;// used if JAMonComparator is used as a decorator.

    public JAMonComparator() {
    }

    public JAMonComparator(boolean naturalOrder) {
        this.naturalOrder=naturalOrder;
    }

    public  JAMonComparator(boolean naturalOrder, Comparator childComparator) {
        this.naturalOrder=naturalOrder;
        this.childComparator=childComparator;

    }

    /**
     * Returns the following:
     * <ol>
     * <li>if newObj object is less than existingObj - returns a negative integer,
     * <li>if the objects are equal - returns zero
     * <li>if newObj object is greater than existingObj - return positive integer
     * </ol>
     */
    public int compare(Object newObj, Object existingObj) {
        // Note the following if condition ensures that nulls may be in the array.
        if ((existingObj==null && newObj==null) ||
                !(existingObj instanceof Comparable) ||
                !(newObj instanceof Comparable)) // 2 nulls are considered equal.  if either object is not comparable return that they are equal
            return EQUALTO;
        // if either other value is null then we don't want this to return less
        // (i.e. true for this conditional) so return 1 (greater than)
        // When existing is null always replace.
        else if (existingObj==null)
            return GREATERTHAN;
        else if (newObj==null)  // when new is null never replace
            return LESSTHAN;

        if (childComparator==null) {
            // Note I am using the passed in value as the Comparable type as this seems more flexible.  For example
            // You may not have control over the array values, but you do over what comparable value you pass in, but being
            // as the comparison is really asking if the col value is greater than the comparison value I am taking the negative
            // value.  i.e. The compareTo() test is really asking if the compValue is greater than the colValue so flipping it with a
            // negative gives us the right answer.
            return reverseIfNeeded(compareThis(newObj, existingObj));
        }
        else
            return reverseIfNeeded(childComparator.compare(newObj, existingObj));



    }

    // I don't want jamon to throw exceptions when objects are not off compatible types (say a String and a Double) and so in this case
    // I will return that they are equal.  for comparisons of the same type regular comparisons will occur.
    protected int compareThis(Object newObj, Object existingObj) {
        int retValue=0;
        try {
            Comparable comparable=(Comparable) newObj;
            retValue=comparable.compareTo(existingObj);
        } finally {
            return retValue;
        }

    }

    private int reverseIfNeeded(int retVal) {
        return (naturalOrder) ? retVal : -retVal;
    }

    public boolean isNaturalOrder() {
        return naturalOrder;
    }

}
