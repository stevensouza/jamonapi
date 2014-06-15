package com.jamonapi.utils;


/**
 * ArraySorter is used to sort 2 dimensional arrays of objects by one of the columns in the array.  Right now this class
 * sorts only for the monitor report, but could be made more generic.  Look in the main method to see how this class is used.
 *
 **/

import java.util.*;

class ArraySorter {
    
    private static void display(Object[][] array) {
        
        int rows=array.length;
        int cols=array[0].length;
        
        for (int i=0; i<rows; i++) {
            String rowData="";
            for (int j=0; j<cols; j++) {
                rowData+=array[i][j]+" ";
            }
            
            System.out.println(rowData);
        }
    }
    
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
    
    /** Test code for ArraySorter **/
    
    public static void main(String[] args) throws Exception {
        Object[][] array={{"7","8", "9"}, {"1","2", "3"}, {"4","5", "6"},};
        System.out.println("unsorted array");
        display(array);
        
        System.out.println("sorted array: asc on col 0");
        ArraySorter sorter=new ArraySorter(array, 0, "asc");
        display(sorter.sort());
        
        System.out.println("sorted array: desc on col 0");
        sorter=new ArraySorter(array, 0, "desc");
        display(sorter.sort());
        
        array=new Double[][]{{new Double(10),new Double(30)},{new Double(20),new Double(40)}};

        System.out.println("sorted double array: desc on col 1");
        sorter=new ArraySorter(array, 1, "desc");
        display(sorter.sort());        
                
    }
}

