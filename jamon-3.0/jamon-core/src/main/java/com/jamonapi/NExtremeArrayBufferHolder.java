package com.jamonapi;

import com.jamonapi.utils.BufferHolder;
import com.jamonapi.utils.JAMonArrayComparator;
import com.jamonapi.utils.NExtremeBufferHolder;

import java.util.Comparator;

/** Buffer used to keep the last N recent array values based on the comparator.  Note the Comparator must
 * be thread safe.  It can also stored the last n recent values of ToArray objects.  By using various Comparators
 * you can determine what should stay in the buffer and what should be removed.  JAMon comes with a number of buffers
 * based on this class.
 * 
 * @author steve souza
 *
 */

public class NExtremeArrayBufferHolder extends NExtremeBufferHolder {
    private static final long serialVersionUID = 278L;

    /** Constructor that takes a JAMonArrayComparator that can be used to determine
     * when values should be removed from  and added to the array.  This can be used to
     * decide what to do to values in the buffer based on multiple columns.
     * 
     * @param comparator
     */
    public NExtremeArrayBufferHolder(JAMonArrayComparator comparator) {
        super(true);
        setComparator(comparator);
    }

    /** Pass true for natural order, and false for reverse order and column number in Object[] to compare starting at 0 */
    public NExtremeArrayBufferHolder(boolean naturalOrder, int colToCompare) {
        super(new JAMonArrayComparator(colToCompare, naturalOrder));
    }

    /** Note the only valid Comparator to be passed is JAMonArrayComparator */
    @Override
    public void setComparator(Comparator comparator) {
        if (comparator instanceof JAMonArrayComparator)
            super.setComparator(comparator);
    }


    /** Factory method that returns a usable copy of this object */
    @Override
    public BufferHolder copy() {
        return new NExtremeArrayBufferHolder((JAMonArrayComparator)getComparator());
    }

}
