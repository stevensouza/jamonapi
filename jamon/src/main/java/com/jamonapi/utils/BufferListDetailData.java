package com.jamonapi.utils;

/** This class builds the returned array data based on info from header and first row.
 * <ul>
 *  <li>If header has more columns than data then each data is padded with nulls.
 *  <li>If first rows data has more columns than header then the header is padded with headers named 'colN' where N is the column number.
 *  <li>If any rows of data have fewer columns than either the data in the first row or the header
 * they are shrunken or grown as neccesary filling any excess columns with nulls, and truncating any excess columns.
 * </ul>
 */
public class BufferListDetailData implements DetailData {
    private static final long serialVersionUID = -4845836085102900245L;

    private String[] header;
    private Object[][] data;
    private Object[] firstRow;
    private int colsInHeader;
    private int colsInFirstRow;


    public BufferListDetailData(BufferList bufferList) {
        initalize(bufferList);
    }

    public String[] getHeader() {
        return header;
    }

    public Object[][] getData() {
        return data;
    }

    public int getRowCount() {
        if (isEmpty())
            return 0;
        else
            return data.length;
    }

    public boolean hasData() {
        return !isEmpty();
    }

    public boolean isEmpty() {
        return (data==null || data.length==0);
    }

    private void initalize(BufferList bufferList) {
        /* object is value object.
         * if header is bigger than data of first row then return header up to column width
         * if header is smaller than data then PAD HEADER with colN
         * if first row is smaller than subsequent rows then use colsize of first row and exclude other cols
         * if first row is bigger than subsequent rows then pad row with nulls
         */

        Object[] rows=getAllRows(bufferList);
        if (rows!=null) {
            firstRow=getRow(rows[0]);
            colsInFirstRow=firstRow.length;
        }

        colsInHeader=(bufferList.getHeader()==null) ? 0 : bufferList.getHeader().length;
        header=buildHeader(bufferList.getHeader());
        data=buildData(rows);
    }

    private Object[] getAllRows(BufferList bufferList) {
        Object[] bufferListArray=null;
        synchronized(bufferList) {
            if (bufferList.getRowCount()>0) {
                bufferListArray=bufferList.getCollection().toArray();
            }
        }

        return bufferListArray;
    }


    private String[] buildHeader(String[] h) {
        // if header is bigger than data of first row then return header up to column width
        // if header is smaller than data then PAD HEADER with colN

        Object[] head=resize(h, getColCount());
        if (head==null)
            return null;

        String[] headerStr=new String[head.length];
        for (int i=0;i<head.length;i++) {
            if (head[i]==null) {
                headerStr[i]="col"+i;
            } else
                headerStr[i]=head[i].toString();
        }

        return headerStr;

    }


    private Object[][] buildData(Object[] rows) {
        if (rows==null)
            return null;

        Object[][] localData=new Object[rows.length][];
        int numCols=getColCount();
        for (int i=0;i<rows.length;i++) {
            localData[i]=resize(getRow(rows[i]), numCols);
        }

        return localData;
    }


    private Object[] resize(Object[] originalData, int size) {
        if (originalData==null)
            return null;
        else if (size==originalData.length)
            return originalData;

        Object[] newData=new Object[size];
        // (shouldShrink) ? shrink : grow
        int loopSize=(originalData.length>size) ? size : originalData.length;

        for (int i=0;i<loopSize;i++) {
            newData[i]=originalData[i];
        }

        return newData;
    }


    private Object[] getRow(Object obj) {
        if (obj instanceof Object[])
            return (Object[])obj;
        else if (obj instanceof ToArray)
            return ((ToArray)obj).toArray();
        else
            return new Object[]{obj};
    }

    private int getColCount() {
        return (colsInHeader>colsInFirstRow) ? colsInHeader : colsInFirstRow;
    }


}
