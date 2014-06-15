package com.jamonapi.proxy;

/** Various parameters that are needed by all proxy jamon monitors.  They 
 * are passed around via this object and so shared by all.
 */
import java.util.List;

import com.jamonapi.utils.BufferList;

class Params {
      // if the following are set to true/false it affects all proxy monitors.
	  boolean isEnabled=true;
	  boolean isInterfaceEnabled=true;
	  boolean isExceptionSummaryEnabled=true;
	  boolean isExceptionDetailEnabled=true;
	  boolean isSQLSummaryEnabled=true;
	  boolean isSQLDetailEnabled=true;
	  boolean isResultSetEnabled=false;
      // ongoing counters that let you know the id of the exception thrown or sql statement executed since the server has been up
	  long    exceptionID=0;// note access to incrementing id's was not made thread safe at this time. If this is used for display only this is acceptable
      long    sqlID=0; 
	    // variables that store and display any exceptions the proxy throws
	  String[] exceptionHeader={"ID", "StartTime", "ExceptionStackTrace", "MethodName",  };
	  BufferList exceptionBuffer=new BufferList(exceptionHeader);
      
	    // variables that store and display any sql executed.
	  String[] sqlHeader={"ID", "StartTime", "Executiontime", "StatementReuse", "SQL",  "ExceptionStackTrace", "MethodName", };
	  BufferList sqlBuffer=new BufferList(sqlHeader, 100);
      // any String entries put in this data structure will have a record that shows up in jamon if a query has the string.  A good use for this
      // is table names.
	  List matchStrings;
	  
	  public String toString() {
          
          return  "isEnabled="+isEnabled+
            ", isInterfaceEnabled="+isInterfaceEnabled+
            ", isExceptionSummaryEnabled="+isExceptionSummaryEnabled+ 
            ", isExceptionDetailEnabled="+isExceptionDetailEnabled+
            ", isSQLSummaryEnabled="+isSQLSummaryEnabled+
            ", isSQLDetailEnabled="+isSQLDetailEnabled+
            ", isResultSetEnabled="+isResultSetEnabled;
	  }
}
