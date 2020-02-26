package com.jamonapi.utils;

import org.junit.Test;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class MiscTest {
    private final Date NULL_DATE = new Date(0);
    private final Date min = new Date();
    private final Date max = new Date(min.getTime()+1000000);

    @Test
    public void testDateMethods() {
        GregorianCalendar cal=new GregorianCalendar(2013, 9, 1); // Tuesday Oct 1 2013

        // Get today's date
        Date date = cal.getTime();

        // examples using shorter functions
        assertThat(Misc.getMonth(date)).isEqualTo("10");
        assertThat(Misc.getShortDate(date)).isEqualTo("10/01/13");
        assertThat(Misc.getFormattedDate("dd-MMM-yy",date)).isEqualTo("01-Oct-13");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCaseInsensitiveMap() {
        Map<String, String> m=Misc.createCaseInsensitiveMap();
        m.put("Steve", "SouzaOriginal");
        m.put("STEVE", "Souza");

        assertThat(m.size()).isEqualTo(1);
        assertThat(m.get("Steve")).isEqualTo("Souza");
        assertThat(m.get("StEvE")).isEqualTo("Souza");
        assertThat(m.get("steve")).isEqualTo("Souza");
        assertThat(m.get("STEVE")).isEqualTo("Souza");
    }

    @Test
    public void testArrayTrim() {
        String[] array={" a ", "b", " c", "d "};
        array = Misc.trim(array);
        assertThat(array[0]).isEqualTo("a");
        assertThat(array[1]).isEqualTo("b");
        assertThat(array[2]).isEqualTo("c");
        assertThat(array[3]).isEqualTo("d");
    }

    @Test
    public void compareDateWithNulls() {
        assertThat(Misc.compare(null, null)).isEqualTo(0);
        assertThat(Misc.compare(NULL_DATE, null)).isEqualTo(0);
        assertThat(Misc.compare(null, NULL_DATE)).isEqualTo(0);
        assertThat(Misc.compare(new Date(), null)).isEqualTo(1);
        assertThat(Misc.compare(null, new Date())).isEqualTo(-1);
        assertThat(Misc.compare(new Date(), NULL_DATE)).isEqualTo(1);
        assertThat(Misc.compare(NULL_DATE, new Date())).isEqualTo(-1);
    }

    @Test
    public void compareDate() {
        assertThat(Misc.compare(min, min)).isEqualTo(0);
        assertThat(Misc.compare(max, min)).isEqualTo(1);
        assertThat(Misc.compare(min, max)).isEqualTo(-1);
    }

    @Test
    public void minDateWithNulls() {
        assertThat(Misc.min(min, NULL_DATE)).isEqualTo(min);
        assertThat(Misc.min(NULL_DATE, min)).isEqualTo(min);
        assertThat(Misc.min(min, null)).isEqualTo(min);
        assertThat(Misc.min(null, min)).isEqualTo(min);
    }

    @Test
    public void minDate() {
        assertThat(Misc.min(min, min)).isEqualTo(min);
        assertThat(Misc.min(min, max)).isEqualTo(min);
        assertThat(Misc.min(max, min)).isEqualTo(min);
    }

    @Test
    public void maxDateWithNulls() {
        assertThat(Misc.max(min, NULL_DATE)).isEqualTo(min);
        assertThat(Misc.max(NULL_DATE, min)).isEqualTo(min);
        assertThat(Misc.max(min, null)).isEqualTo(min);
        assertThat(Misc.max(null, min)).isEqualTo(min);
    }

    @Test
    public void maxDate() {
        assertThat(Misc.max(min, min)).isEqualTo(min);
        assertThat(Misc.max(min, max)).isEqualTo(max);
        assertThat(Misc.max(max, min)).isEqualTo(max);
    }

}
