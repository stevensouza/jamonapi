package com.jamonapi;

import java.util.List;

/** Used to implement getting data from monitors, keys and frequencyDist. Purely
 * an interface of implementation details of no interest to the end user.   Used to create
 * header, and row data for tabular data representations. */

interface RowData {
    /** i.e. Label */
    public List getBasicHeader(List header);

    /** i.e. Get all key columns as part of the header i.e. Label, Units.  This will include range headers */
    public List getHeader(List header);

    /** i.e. Get the display header.  Often same as getHeader */
    public List getDisplayHeader(List header);

    /** Get all data for a row including range data */
    public List getRowData(List rowData);

    /** Get all data for a row excluding row ranges, and put key data into one cell */
    public List getBasicRowData(List rowData);

    /** Get data excluding ranges, but break out key columns */
    public List getRowDisplayData(List rowData);
}
