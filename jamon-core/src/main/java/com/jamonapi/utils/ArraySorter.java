package com.jamonapi.utils;

import java.util.Arrays;

/**
 * ArraySorter is used to sort 2 dimensional arrays of objects by one of the columns in the array.  Right now this class
 * sorts only for the monitor report, but could be made more generic.
 *
 */
class ArraySorter {

    public ArraySorter(Object[][] array, int sortCol, String sortOrder) {
        this.array=array;
        this.sortCol=sortCol;
        this.sortOrder=sortOrder;
    }

    private Object[][] array;
    private int sortCol;
    private String sortOrder;

    private int getRows() {
        return array.length;
    }

    private int getCols() {
        return array[0].length;
    }

    private Object[] getArrayToSort() {

        Object[] arrayToSort=new Object[getRows()];
        for (int i=0; i<getRows(); i++) {
            arrayToSort[i]=array[i];
        }

        return arrayToSort;

    }

    private ArraySorterEntry[] getArraySorterEntries() {
        Object[] arrayToSort = getArrayToSort();

        ArraySorterEntry[] arraySorterEntries=new ArraySorterEntry[getRows()];

        for(int i=0; i<getRows(); i++)  {
            arraySorterEntries[i] = new ArraySorterEntry(arrayToSort[i], (Comparable) array[i][sortCol]) ;
        }


        return arraySorterEntries;
    }


    public Object[][] sort() {

        ArraySorterEntry[] arraySorterEntries=getArraySorterEntries();
        Arrays.sort(arraySorterEntries);

        Object[][] returnArray = new Object[getRows()][getCols()];
        for (int i=0; i<getRows(); i++) {
            returnArray[i]=(Object[])arraySorterEntries[i].getSortedObject();
        }

        return returnArray;

    }



    /**
     * inner class ArraySorterEntry
     **/

    private class ArraySorterEntry implements Comparable {
        private Object arrayValueToSort;
        private Comparable valueToSortBy;

        public ArraySorterEntry(Object arrayValueToSort, Comparable valueToSortBy) {
            this.arrayValueToSort = arrayValueToSort;
            this.valueToSortBy    = valueToSortBy;
        }


        public int compareTo(Object o) {
            ArraySorterEntry sorter = (ArraySorterEntry)o;
            int compare=valueToSortBy.compareTo(sorter.valueToSortBy);
            if (compare==0 || "asc".equalsIgnoreCase(sortOrder))
                return compare;
            else if ("desc".equalsIgnoreCase(sortOrder))
                return -compare;
            else
                throw new RuntimeException("Programming error: The only valid sort orders are 'asc' and 'desc', but '"+sortOrder+"' was passed");
        }

        public Object getSortedObject() {
            return arrayValueToSort;
        }

    }

    /******************* end inner class */
}

