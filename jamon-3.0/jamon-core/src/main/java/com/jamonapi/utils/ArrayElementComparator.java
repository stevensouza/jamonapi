package com.jamonapi.utils;

import java.io.Serializable;
import java.util.Comparator;

/** Maps a Comparator to a column number of an array starting at position 0.  Used by JAMonArrayComparator */
class ArrayElementComparator implements Comparator, Serializable {
    private static final long serialVersionUID = 278L;
    // Used to compare elements of an array that reside within the same column.  The Object[][] array itself
    // will be sorted according to this algorithm.

    private int sortCol;
    private Comparator comparator;

    /** Constructor that takes the position in array to be compared as well as it's comparator */
    public ArrayElementComparator(int sortCol, Comparator comparator) {
        this.comparator=comparator;
        this.sortCol=sortCol;
    }


    public ArrayElementComparator(int sortCol, boolean naturalOrder) {
        comparator=new JAMonComparator(naturalOrder);
        this.sortCol=sortCol;
    }


    /** Return col to be sorted/compared */
    public int getSortCol() {
        return sortCol;
    }

    /** Call the comparator on the column */
    public int compare(Object o1, Object o2) {
        return comparator.compare(o1, o2);
    }


    @Override
    public String toString() {
        return "sortCol="+sortCol+", comparator="+comparator;
    }

}
