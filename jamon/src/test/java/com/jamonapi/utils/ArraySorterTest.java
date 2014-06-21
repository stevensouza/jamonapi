package com.jamonapi.utils;

import org.junit.Test;

import java.util.Arrays;

import static org.fest.assertions.api.Assertions.assertThat;

public class ArraySorterTest {

    private static Object[][] getData() {
        Object[][] array={{"7","8", "9"}, {"1","2", "3"}, {"4","5", "6"},};
        return array;
    }

    /** Test array is sorted in ascending order by the first column */
    @Test
    public void testSortStringArrayAscOnCol1() {
        Object[][] array=getData();
        ArraySorter sorter=new ArraySorter(array, 0, "asc");
        array=sorter.sort();

        assertThat(Arrays.toString(array[0])).isEqualTo("[1, 2, 3]");
        assertThat(Arrays.toString(array[1])).isEqualTo("[4, 5, 6]");
        assertThat(Arrays.toString(array[2])).isEqualTo("[7, 8, 9]");
    }


    /** Test array is sorted in descending order by the first column */
    @Test
    public void testSortStringArrayDescOnCol1() {
        Object[][] array=getData();
        ArraySorter sorter=new ArraySorter(array, 0, "desc");
        array=sorter.sort();

        assertThat(Arrays.toString(array[0])).isEqualTo("[7, 8, 9]");
        assertThat(Arrays.toString(array[1])).isEqualTo("[4, 5, 6]");
        assertThat(Arrays.toString(array[2])).isEqualTo("[1, 2, 3]");

    }

    /** Test array is sorted in descending order by the 2nd column */
    @Test
    public void testSortStringArrayDescOnCol2() {
        Object[][] array=getData();
        ArraySorter sorter=new ArraySorter(array, 1, "desc");
        array=sorter.sort();

        assertThat(Arrays.toString(array[0])).isEqualTo("[7, 8, 9]");
        assertThat(Arrays.toString(array[1])).isEqualTo("[4, 5, 6]");
        assertThat(Arrays.toString(array[2])).isEqualTo("[1, 2, 3]");
    }

    /** Test array is sorted in descending order by the first column */
    @Test
    public void testSortDoubleArrayAscOnCol2() {
        Object[][] array=new Double[][]{{new Double(10),new Double(30)},{new Double(20),new Double(40)}};
        ArraySorter sorter=new ArraySorter(array, 1, "desc");
        array=sorter.sort();

        assertThat(Arrays.toString(array[0])).isEqualTo("[20.0, 40.0]");
        assertThat(Arrays.toString(array[1])).isEqualTo("[10.0, 30.0]");

    }

}
