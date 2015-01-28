package com.jamonapi;

import com.jamonapi.utils.*;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

/** <p>Factory used to hold JAMonListeners.  Developers may put any listeners that implement
 * JAMonBufferListeners.</p>
 * 
 * <p>Any listener may be retrieved by passing in the JAMonListener name.  At this time JAMon ships
 * with the following listeners that can be referenced by name.  Every buffer has a shared counterpart
 * that allows different montiors to share the same buffer.  In every other way they are
 * the same as their similarly named counterparts.
 * </p>
 * 
 * <p>
 * <ul>
 * <li><b>FIFOBuffer</b> - Holds most recent objects in the buffer<br>
 * <li><b>NLargestValueBuffer</b> - Keeps the largest values in the buffer<br>
 * <li><b>NSmallestValueBuffer</b> - Keeps the smallest values in the buffer<br>
 * <li><b>NLargestValueBuffer7Days</b> - When buffer is full the oldest data in buffer that is over 7 days is removed.  If no data
 *  is older than 7 days then the smallest is removed. <br>
 * <li><b>NLargestValueBuffer24Hrs</b> - When buffer is full the oldest data in buffer that is over 24 hours is removed.  If no data
 *  is older than 7 days then the smallest is removed. <br>
 * <li><b>NSmallestValueBuffer7Days</b> - When buffer is full the oldest data in buffer that is over 7 days is removed.  If no data
 *  is older than 7 days then the largest is removed. <br>
 * <li><b>NSmallestValueBuffer24Hrs</b> - When buffer is full the oldest data in buffer that is over 24 hours is removed.  If no data
 *  is older than 7 days then the largest is removed. <br>
 * <li><b>SharedFIFOBuffer</b> - Holds most recent objects in the buffer<br>
 * <li><b>SharedNLargestValueBuffer</b> - Keeps the largest values in the buffer<br>
 * <li><b>SharedNSmallestValueBuffer</b> - Keeps the smallest values in the buffer<br>
 * <li><b>SharedNLargestValueBuffer7Days</b> - When buffer is full the oldest data in buffer that is over 7 days is removed.  If no data
 *  is older than 7 days then the smallest is removed. <br>
 * <li><b>SharedNLargestValueBuffer24Hrs</b> - When buffer is full the oldest data in buffer that is over 24 hours is removed.  If no data
 *  is older than 7 days then the smallest is removed. <br>
 * <li><b>SharedNSmallestValueBuffer7Days</b> - When buffer is full the oldest data in buffer that is over 7 days is removed.  If no data
 *  is older than 7 days then the largest is removed. <br>
 * <li><b>SharedNSmallestValueBuffer24Hrs</b> - When buffer is full the oldest data in buffer that is over 24 hours is removed.  If no data
 *  is older than 7 days then the largest is removed. <br>
 * <li><b>HTTPBufferListener</b> - Buffer that holds data specific to http requests.<br>
 * <li><b>ExceptionBufferListener</b> - Buffer that holds data specific to monitors that track exceptions in the detail buffer<br>
 * </ul>
 * </p>
 *
 */

public class JAMonListenerFactory {
    private static final boolean NATURAL_ORDER=true;
    private static final boolean REVERSE_ORDER=false;
    private static String[] HEADER={"ListenerName", "Listener"};
    private static Map map = Misc.createCaseInsensitiveMap();

    static {
        reset();
    }

    /** Reset all listeners in factory to default settings.  Note this is required to get rid of shared buffer data in
     * memory.
     */
    public static void reset() {
        // put factory instances into the JAMonListenerFactory
        map = Misc.createCaseInsensitiveMap();
        put(getFIFO());
        put(getNLargest());
        put(getNSmallest());
        put(getNLargest7Days());
        put(getNLargest24Hrs());
        put(getNSmallest7Days());
        put(getNSmallest24Hrs());

        // allows sharing buffers between monitors!
        put(getSharedFIFO());
        put(getSharedNLargest());
        put(getSharedNSmallest());
        put(getSharedNLargest7Days());
        put(getSharedNLargest24Hrs());
        put(getSharedNSmallest7Days());
        put(getSharedNSmallest24Hrs());
    }

    /** Developers may register their own listeners to be made available for use in JAMon */
    public static void put(JAMonListener jamonListener) {
        map.put(jamonListener.getName(), jamonListener);
    }

    /** Returns an array of all registered JAMonListeners in the format: key, JamonListener factory instance */
    public static Object[][] getData() {
        Iterator iter=map.entrySet().iterator();
        Object[][] data=new Object[map.size()][];

        int i=0;
        while (iter.hasNext()) {
            data[i]=new Object[2];
            Map.Entry entry=(Map.Entry) iter.next();
            data[i][0]=entry.getKey();
            data[i][1]=entry.getValue();
            i++;
        }

        return data;
    }

    /** Returns the header for display of JAMonListeners */
    public static String[] getHeader() {
        return HEADER;
    }

    /** Get an instance of the named factory instance.  If the Liistener implements
     * CopyJAMonListener then copy will be called.  If not then the default constructor will
     * be called.
     * 
     * @param listenerName
     * @return JAMonListener
     */
    public static JAMonListener get(String listenerName) {
        try {
            JAMonListener factoryInstance =(JAMonListener)map.get(listenerName);
            if (factoryInstance instanceof CopyJAMonListener) {
                return ((CopyJAMonListener)factoryInstance).copy();
            }  else {
                JAMonListener newInst = factoryInstance.getClass().newInstance();
                newInst.setName(factoryInstance.getName());
                return newInst;
            }

        } catch (Exception e) {
            throw new RuntimeException("Error getting listener from factory: "+listenerName+", "+e);
        }

    }

    /** Various factory methods used to populate the JAMonListenerFactory with default JAMonBufferListeners */
    private static BufferHolder getBufferHolderNLargest7Days() {
        // Keeps only the largest values within 7 days.
        JAMonArrayComparator jac=new JAMonArrayComparator();
        DateMathComparator dmc=new DateMathComparator(Calendar.DAY_OF_YEAR, -7);

        jac.addCompareCol(JAMonBufferListener.DATE_COL, dmc);
        jac.addCompareCol(JAMonBufferListener.VALUE_COL, NATURAL_ORDER);

        BufferHolder bufferHolder=new NExtremeArrayBufferHolder(jac);
        return bufferHolder;
    }

    private static BufferHolder getBufferHolderNLargest24Hrs() {
        // Keeps only the largest values within 24 hrs.
        JAMonArrayComparator jac=new JAMonArrayComparator();
        DateMathComparator dmc=new DateMathComparator(Calendar.HOUR_OF_DAY, -24);

        jac.addCompareCol(JAMonBufferListener.DATE_COL, dmc);
        jac.addCompareCol(JAMonBufferListener.VALUE_COL, NATURAL_ORDER);

        BufferHolder bufferHolder=new NExtremeArrayBufferHolder(jac);
        return bufferHolder;
    }

    private static BufferHolder getBufferHolderNSmallest7Days() {
        // Keeps only the smallest values within 7 days.
        JAMonArrayComparator jac=new JAMonArrayComparator();
        DateMathComparator dmc=new DateMathComparator(Calendar.DAY_OF_YEAR, -7);

        jac.addCompareCol(JAMonBufferListener.DATE_COL, dmc);
        jac.addCompareCol(JAMonBufferListener.VALUE_COL, REVERSE_ORDER);

        BufferHolder bufferHolder=new NExtremeArrayBufferHolder(jac);
        return bufferHolder;

    }

    private static BufferHolder getBufferHolderNSmallest24Hrs() {
        // Keeps only the smallest values within 24 hrs.
        JAMonArrayComparator jac=new JAMonArrayComparator();
        DateMathComparator dmc=new DateMathComparator(Calendar.HOUR_OF_DAY, -24);

        jac.addCompareCol(JAMonBufferListener.DATE_COL, dmc);
        jac.addCompareCol(JAMonBufferListener.VALUE_COL, REVERSE_ORDER);

        BufferHolder bufferHolder=new NExtremeArrayBufferHolder(jac);
        return bufferHolder;

    }

    private static JAMonBufferListener getFIFO() {
        BufferHolder bufferHolder=new FIFOBufferHolder();
        BufferList bufferList=new BufferList(JAMonBufferListener.DEFAULT_HEADER,bufferHolder);
        return new JAMonBufferListener("FIFOBuffer", bufferList);
    }

    private static JAMonBufferListener getNLargest() {
        BufferHolder bufferHolder=new NExtremeArrayBufferHolder(NATURAL_ORDER, JAMonBufferListener.VALUE_COL);
        BufferList bufferList=new BufferList(JAMonBufferListener.DEFAULT_HEADER, bufferHolder);
        return new JAMonBufferListener("NLargestValueBuffer", bufferList);

    }

    private static JAMonBufferListener getNSmallest() {
        BufferHolder bufferHolder=new NExtremeArrayBufferHolder(REVERSE_ORDER, JAMonBufferListener.VALUE_COL);
        BufferList bufferList=new BufferList(JAMonBufferListener.DEFAULT_HEADER, bufferHolder);
        return new JAMonBufferListener("NSmallestValueBuffer", bufferList);

    }

    private static JAMonBufferListener getNLargest7Days() {
        BufferList bufferList=new BufferList(JAMonBufferListener.DEFAULT_HEADER, getBufferHolderNLargest7Days());
        return new JAMonBufferListener("NLargestValueBuffer7Days", bufferList);

    }

    private static JAMonBufferListener getNLargest24Hrs() {
        BufferList bufferList=new BufferList(JAMonBufferListener.DEFAULT_HEADER, getBufferHolderNLargest24Hrs());
        return new JAMonBufferListener("NLargestValueBuffer24Hrs", bufferList);

    }

    private static JAMonBufferListener getNSmallest7Days() {
        BufferList bufferList=new BufferList(JAMonBufferListener.DEFAULT_HEADER, getBufferHolderNSmallest7Days());
        return new JAMonBufferListener("NSmallestValueBuffer7Days", bufferList);

    }

    private static JAMonBufferListener getNSmallest24Hrs() {
        BufferList bufferList=new BufferList(JAMonBufferListener.DEFAULT_HEADER, getBufferHolderNSmallest24Hrs());
        return new JAMonBufferListener("NSmallestValueBuffer24Hrs", bufferList);

    }

    private static JAMonBufferListener getSharedFIFO() {
        BufferHolder bufferHolder=new FIFOBufferHolder();
        BufferList bufferList=new BufferList(JAMonBufferListener.DEFAULT_HEADER,bufferHolder);
        return new SharedJAMonBufferListener("SharedFIFOBuffer", bufferList);
    }

    private static JAMonBufferListener getSharedNSmallest() {
        BufferHolder bufferHolder=new NExtremeArrayBufferHolder(REVERSE_ORDER, JAMonBufferListener.VALUE_COL);
        BufferList bufferList=new BufferList(JAMonBufferListener.DEFAULT_HEADER, bufferHolder);
        return new SharedJAMonBufferListener("SharedNSmallestValueBuffer", bufferList);
    }

    private static JAMonBufferListener getSharedNLargest()  {
        BufferHolder bufferHolder=new NExtremeArrayBufferHolder(NATURAL_ORDER, JAMonBufferListener.VALUE_COL);
        BufferList bufferList=new BufferList(JAMonBufferListener.DEFAULT_HEADER, bufferHolder);
        return new SharedJAMonBufferListener("SharedNLargestValueBuffer", bufferList);
    }

    private static JAMonBufferListener getSharedNLargest7Days() {
        BufferList bufferList=new BufferList(JAMonBufferListener.DEFAULT_HEADER, getBufferHolderNLargest7Days());
        return new SharedJAMonBufferListener("SharedNLargestValueBuffer7Days", bufferList);
    }

    private static JAMonBufferListener getSharedNLargest24Hrs() {
        BufferList bufferList=new BufferList(JAMonBufferListener.DEFAULT_HEADER, getBufferHolderNLargest24Hrs());
        return new SharedJAMonBufferListener("SharedNLargestValueBuffer24Hrs", bufferList);
    }

    private static JAMonBufferListener getSharedNSmallest7Days() {
        BufferList bufferList=new BufferList(JAMonBufferListener.DEFAULT_HEADER, getBufferHolderNSmallest7Days());
        return new SharedJAMonBufferListener("SharedNSmallestValueBuffer7Days", bufferList);
    }

    private static JAMonBufferListener getSharedNSmallest24Hrs() {
        BufferList bufferList=new BufferList(JAMonBufferListener.DEFAULT_HEADER, getBufferHolderNSmallest24Hrs());
        return new SharedJAMonBufferListener("SharedNSmallestValueBuffer24Hrs", bufferList);
    }

}
