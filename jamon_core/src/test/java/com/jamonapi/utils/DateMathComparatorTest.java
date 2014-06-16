package com.jamonapi.utils;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.fest.assertions.api.Assertions.assertThat;


public class DateMathComparatorTest {

    @Test
    public void testMinus8Days() {
        Date today=new Date();

        Calendar calendar=new GregorianCalendar();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_YEAR, -8);
        Date minusDays=calendar.getTime();
        // The first date passed in is more recent than 7 hours ago and the second is not, so the result is 1 (first is greater)
        assertThat(new DateMathComparator(Calendar.DAY_OF_YEAR, -7).compare(today, minusDays)).isGreaterThan(0);
        assertThat(new DateMathComparator(Calendar.DAY_OF_YEAR, -7).compare(minusDays, today)).isLessThan(0);

    }

    @Test
    public void testMinus6Days() {
        Date today=new Date();

        Calendar calendar=new GregorianCalendar();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_YEAR, -6);
        Date minusDays=calendar.getTime();
        // Both dates passed in are more recent than 7 days ago so the result is 0 (they are "equal")
        assertThat(new DateMathComparator(Calendar.DAY_OF_YEAR, -7).compare(today, minusDays)).isEqualTo(0);
        assertThat(new DateMathComparator(Calendar.DAY_OF_YEAR, -7).compare(minusDays, today)).isEqualTo(0);
    }

    @Test
    public void testMinus6Hours() {
        Date today=new Date();

        Calendar calendar=new GregorianCalendar();
        calendar.setTime(today);
        calendar.add(Calendar.HOUR_OF_DAY, -6);
        Date minusHours=calendar.getTime();
        // Both dates passed in are more recent than 7 hours ago so the result is 0 (they are "equal")
        assertThat(new DateMathComparator(Calendar.HOUR_OF_DAY, -7).compare(today, minusHours)).isEqualTo(0);
        assertThat(new DateMathComparator(Calendar.HOUR_OF_DAY, -7).compare(minusHours, today)).isEqualTo(0);
    }

    @Test
    public void testMinus8Hours() {
        Date today=new Date();

        Calendar calendar=new GregorianCalendar();
        calendar.setTime(today);
        calendar.add(Calendar.HOUR_OF_DAY, -8);
        Date minusHours=calendar.getTime();
        // The first date passed in is more recent than 7 hours ago and the second is not, so the result is 1 (first is greater)
        assertThat(new DateMathComparator(Calendar.HOUR_OF_DAY, -7).compare(today, minusHours)).isGreaterThan(0);
        assertThat(new DateMathComparator(Calendar.HOUR_OF_DAY, -7).compare(minusHours, today)).isLessThan(0);
    }

}
