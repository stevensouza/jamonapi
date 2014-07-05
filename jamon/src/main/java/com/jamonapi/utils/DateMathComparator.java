package com.jamonapi.utils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/** Comparator that allows you to pass Calendar fields and a negative number for the number
 * of this filed (i.e. hours/days) that a Date should not exceed.  Use fields like Calendar.DATE, HOUR_OF_DAY, DAY_OF_MONTH, DAY_OF_WEEK, DAY_OF_YEAR ETC.
 * Values to be used for dateToAdd in the constructor could be -7 for 7 days ago, or -24 for 24 hours ago depending on what
 * was passed in the dateField.
 * 
 */
public class DateMathComparator extends JAMonComparator implements Serializable {
    private static final long serialVersionUID = -3153772066240951517L;
    private int dateField;
    private int dateToAdd;

    /**Use fields like Calendar.DATE, HOUR_OF_DAY, DAY_OF_MONTH, DAY_OF_WEEK, DAY_OF_YEAR ETC.*/
    private Calendar calendar=new GregorianCalendar();

    private static final boolean NATURAL_ORDER=true;

    public DateMathComparator(int dateField, int dateToAdd) {
        super(NATURAL_ORDER);
        this.dateField=dateField;
        this.dateToAdd=dateToAdd;
    }

    /** <ol>
     * <li>2 dates are equal if they are both above or below the Calendar field threshold
     * <li>date1 is greater if it is greater than threshold and date2 isn't
     * <li>date1 is less than the threshold than it is greater than date2.
     * </ol>
     */
    @Override
    protected int compareThis(Object o1, Object o2) {
        int retVal=0;
        if (o1 instanceof Date && o2 instanceof Date) {
            Date d1=(Date)o1;
            Date d2=(Date)o2;

            calendar.setTime(new Date());
            calendar.add(dateField, dateToAdd);// i.e. todays date -7 days
            Date dateAddValue=calendar.getTime();

            int d1CompNum=d1.compareTo(dateAddValue);
            int d2CompNum=d2.compareTo(dateAddValue);

            if (d1CompNum<=-1 && d2CompNum<=-1)
                return 0;
            else if (d1CompNum>=1 && d2CompNum>=1)
                return 0;
            else if (d1CompNum==1)
                return 1;
            else if (d1CompNum==-1)
                return -1;
        }

        return retVal;
    }

}
