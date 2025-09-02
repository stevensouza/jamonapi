package com.jamonapi;

import com.jamonapi.utils.BufferList;
import com.jamonapi.utils.BufferListDetailData;
import com.jamonapi.utils.DetailData;
import com.jamonapi.utils.ToArray;

import java.util.ArrayList;
import java.util.List;

/** JAMonListener that puts jamon data into a buffer that allows you to display the last N configurable
 * detail events.  The buffer will have the detail label, value and invocation date for the monitor that
 * was fired.
 * 
 * @author steve souza
 *
 */

public  class JAMonBufferListener implements JAMonListener, CopyJAMonListener {

    private static final long serialVersionUID = 278L;
    private BufferList list;
    private String name;
    static final String[] DEFAULT_HEADER=getDefaultHeaderInfo().getHeader();
    static final int VALUE_COL=getDefaultHeaderInfo().getLastValueIndex();
    static final int DATE_COL=getDefaultHeaderInfo().getDateIndex();

    public JAMonBufferListener() {
        this("JAMonBufferListener");
    }

    /** Pass in the jamonListener name */

    public JAMonBufferListener(String name){
        this(name, new BufferList(DEFAULT_HEADER,50));
    }

    /** Name the listener and pass in the jamon BufferList to use */
    public JAMonBufferListener(String name, BufferList list) {
        this.name=name;
        this.list=list;
    }

    /** When this event is fired the monitor will be added to the rolling buffer */
    public void processEvent(Monitor mon) {
        list.addRow(mon.getJAMonDetailRow());
    }

    /** Add a row to the buffer */
    public void addRow(ToArray row) {
        list.addRow(row);
    }

    /** Add a row to the buffer */
    public void addRow(Object[] row) {
        list.addRow(row);
    }

    /** get the underlying bufferList which can then be used to display its contents */
    public BufferList getBufferList() {
        return list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;

    }

    /** Make a copy of this instance */
    public JAMonListener copy() {
        return new JAMonBufferListener(getName(), list.copy());
    }


    public DetailData getDetailData() {
        return new BufferListDetailData(list);
    }

    public int getRowCount() {
        return list.getRowCount();
    }

    public boolean hasData() {
        return list.hasData();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }


    public static HeaderInfo getDefaultHeaderInfo() {
        return getHeaderInfo(new String[]{"InstanceName", "Label"});

    }

    public static HeaderInfo getHeaderInfo(String[] firstPart) {
        return new HeaderInfo(firstPart);
    }

    // class makes sure header always returns lastvalue, active and date.
    public static class HeaderInfo {
        private String[] header;
        private int lastValueIndex;
        private int dateIndex;
        private int activeIndex;

        public HeaderInfo(String[] firstPart)  {
            header=makeHeader(firstPart);
            for (int i=0;i<header.length;i++) {
                if ("LastValue".equalsIgnoreCase(header[i]))
                    lastValueIndex=i;
                else if ("Date".equalsIgnoreCase(header[i]))
                    dateIndex=i;
                else if ("Active".equalsIgnoreCase(header[i]))
                    activeIndex=i;
            }

        }

        public String[] getHeader() {
            return header;
        }

        public int getLastValueIndex() {
            return lastValueIndex;
        }

        public int getDateIndex() {
            return dateIndex;
        }

        public int getActiveIndex() {
            return activeIndex;
        }

        public int getNumCols() {
            return header.length;
        }

        /**
         * Return an array with a place to hold an object for the values in header and values populated for monitor data - last value, active and last access
         */
        public Object[] getData(Monitor mon) {
            Object[] retData=new Object[header.length];// all but the 2 values will be null and will be replaced by caller.
            retData[lastValueIndex]=Double.valueOf(mon.getLastValue());
            retData[activeIndex]=Double.valueOf(mon.getActive());
            retData[dateIndex]=mon.getLastAccess();
            return retData;
        }

        /** Pass in list and add monitor data to the end of it */
        public Object[] getData(List dataList, Monitor mon) {
            dataList.add(Double.valueOf(mon.getLastValue()));
            dataList.add(Double.valueOf(mon.getActive()));
            dataList.add(mon.getLastAccess());
            return dataList.toArray();
        }

        // combine constructor header with jamon header
        static String[] makeHeader(String[] firstPart) {
            int firstPartCount = (firstPart==null) ? 0 : firstPart.length;
            List newHeader=new ArrayList();
            for (int i=0;i<firstPartCount;i++)
                newHeader.add(firstPart[i]);

            // add standard jamon header info to the header
            String[] lastPart=new String[] {"LastValue","Active","Date"};
            for (int i=0;i<lastPart.length;i++)
                newHeader.add(lastPart[i]);

            return (String[])newHeader.toArray(new String[0]);


        }
    }

}
