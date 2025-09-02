package com.jamonapi.proxy;


import com.jamonapi.utils.BufferList;

import java.util.List;

/** Various parameters that are needed by all proxy jamon monitors.  They
 * are passed around via this object and so shared by all.
 */
class Params {
    // if the following are set to true/false it affects all proxy monitors.
    boolean isEnabled=true;
    boolean isInterfaceEnabled=true;
    boolean isSQLSummaryEnabled=true;
    boolean isSQLDetailEnabled=true;
    boolean isResultSetEnabled=false;

    // ongoing counters that let you know the id of the sql statement executed since the server has been up
    long sqlID=0;

    // variables that store and display any sql executed.
    String[] sqlHeader={"ID", "StartTime", "Executiontime", "StatementReuse", "SQL",  "ExceptionStackTrace", "MethodName", };
    BufferList sqlBuffer=new BufferList(sqlHeader, 100);

    // any String entries put in this data structure will have a record that shows up in jamon if a query has the string.  A good use for this
    // is table names.
    List matchStrings;

    @Override
    public String toString() {
        return  "isEnabled="+isEnabled+
        ", isInterfaceEnabled="+isInterfaceEnabled+
        ", isSQLSummaryEnabled="+isSQLSummaryEnabled+
        ", isSQLDetailEnabled="+isSQLDetailEnabled+
        ", isResultSetEnabled="+isResultSetEnabled;
    }
}
