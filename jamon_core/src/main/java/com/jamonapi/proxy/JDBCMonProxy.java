package com.jamonapi.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.jamonapi.BasicTimingMonitor;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorComposite;
import com.jamonapi.MonitorFactory;
import com.jamonapi.utils.Misc;

/**
 * Class that implements JDBC specific proxied monitoring.  The following are monitored by this class
 * <ol>
 *  <li>All SQL statements executed. For Statements argument values are replaced by '?' and for CallableStatements and PreparedStatement the original query with the '?'
 * is used (select * from table where key=?)
 *  <li>The first keyword of the SQL command (i.e. select/update/delete/insert/create/...
 *  <li>String matches.  Developers pass in these strings. This is useful for tracking table accesses,
 *  <li>SQL Details.  This keeps track  of the most recent N queries.
 *  </ol>
 * 
 * <p>In addition the standard MonProxy stats are tracked (Interface method calls, and Exception details).
 * 
 */

class JDBCMonProxy extends MonProxy {

    // The queries associated with the PreparedStatments are kept here.  PreparedStatements have no way to get the query out.
    // A weakHashMap() is used as you can't guarantee when a PreparedStatement closes, and there has to be some way of cleaning up
    // stale objects in the map.  WeakHashMaps() automatically remove any Object that has no other reference.
    private static Map statementsMap=Collections.synchronizedMap(new WeakHashMap());
    private static Long DEFAULT_SQL_TIME=new Long(-99);// number for when the query hasn't finished executing yet.
    private static int ARGS_SQL_STATEMENT=0;// The sql is always the first argument to methods like executeQuery(sql,...);
    // the following numbers correspond to this header
    //String[] sqlHeader={"ID", "StartTime", "Executiontime", "StatementReuse", "SQL",  "ExceptionStackTrace", "MethodName", };
    private static int SQL_EXECUTION_TIME_IND=2;
    private static int SQL_EXCEPTION_IND=5;


    JDBCMonProxy(Object monitoredObj, Params params, MonProxyLabelerInt labelerInt) {
        super(monitoredObj, params, labelerInt);
    }


    /** Method that monitors method invocations of the proxied interface.  This method is not explicitly called.
     *  The Proxy class automatically calls it.
     * 
     * 
     */
    @Override
    public Object invoke(Object proxy, Method method,  Object[] args) throws Throwable {
        BasicTimingMonitor mon=null;
        Object[] row=null;
        SQLDeArgMon sqlMon=null;
        String stackTrace="";
        // These values need to be checked at beginning and end of routine, so need to ensure they don't change during this method call
        // hence the local versions of them.
        boolean isSQLSummaryEnabled=params.isSQLSummaryEnabled && params.isEnabled ;
        boolean isSQLDetailEnabled=params.isSQLDetailEnabled && params.isEnabled;
        boolean executingQuery=isExecuteQueryMethod(method.getName()); // determine if a query is being executed.


        // Because this monitor string is created every time I use a StringBuffer as it should be more effecient.
        // I didn't do this in the exception part of the code as that shouldn't be called as often.
        try {

            // If enabled and the method that is being called executes a query then monitor the sql executed.  Note that 'Statement' 'execute'
            // methods will have sql passed to them, and and PreparedStatement and CallableStatements will not.
            // When needed PrepatedStatement/CallableStatements will get their sql from the WeakHashMap  from when the PreparedStatement was created.
            if ( executingQuery && (isSQLSummaryEnabled || isSQLDetailEnabled)) {
                String actualSQL=null;
                // Start timing the query and add a row to the recently executed query buffer
                mon=new BasicTimingMonitor();
                mon.start();

                int statementReuseCounter=0;//createStatement will always have a value of 0 as they can't be reused.  PreparedStaement is tracked

                if (isStatementObject(args)) {// a query associated with a Statement is being executed.
                    actualSQL=getSQL(args[ARGS_SQL_STATEMENT]);//get the sql being executed
                    sqlMon=new SQLDeArgMon("Statement", actualSQL, params.matchStrings);// parses sql and puts ? marks associated with args, and sets up monitors.

                } else { // a query associated with a PreparedStatement/CallableStatement is being executed
                    sqlMon=(SQLDeArgMon) statementsMap.get(getMonitoredObject());  // get existing object associated with this PreparedStatement/CallableStatement
                    statementReuseCounter=sqlMon.incrementCounter();// increment the reuse counter.
                    if (isSQLSummaryEnabled)
                        MonitorFactory.add("MonProxy-SQL-PreparedStatement Reuse","count", 2*statementReuseCounter);// to get the average to be accurate multiply by 2

                    actualSQL=getSQL(sqlMon.getSQL());// in the case of a preparedStatement the actual sql includes question marks.

                }

                // create a monitor for:  select * from table where name=?
                if (isSQLSummaryEnabled)
                    sqlMon.start();

                if (isSQLDetailEnabled) {
                    row=new Object[] {new Long(++params.sqlID), new Date(), DEFAULT_SQL_TIME,  new Integer(statementReuseCounter), actualSQL, "", method.toString(),};
                    params.sqlBuffer.addRow(row);
                }
            } // end if enabled


            // Invoke the underlying method
            Object returnValue=super.invoke(proxy, method, args);

            // If sql monitoring is not enabled or if the proxy is already there then don't wrap a proxy.  For example Statements can
            // return the underlying Connection which will probably already be Monitored. Note for jdbc if the Object returns another jdbc Object
            // type that should be monitored it will also be monitored.
            if (!(params.isEnabled) || returnValue instanceof MonProxy)
                return returnValue;
            else if ((isSQLSummaryEnabled || isSQLDetailEnabled) && returnsPreparedStatement(method.getName()) && isPreparedStatement(returnValue)) { // not sure if the returnsPreparedStatement is needed or not
                // Associate sql such as 'select * from table where name=?' with the PreparedStatement in the WeakHashMap so when later executeQuery() is
                // issued against the PreparedStatement we can count stats of the preparedStatements reuses.  PreparedStatements have no way of getting out
                // what sql is associated.  WeakHashMap will not leave the memory hanging when the PreparedStatement goes out of scope.
                String actualSQL=getSQL(args[ARGS_SQL_STATEMENT]);
                statementsMap.put(returnValue, new SQLDeArgMon("PreparedStatement",actualSQL, params.matchStrings));
                return monitorJDBC(returnValue);
            } else if ((isSQLSummaryEnabled || isSQLDetailEnabled) && (shouldMonitorResultSet(returnValue) || shouldMonitorOtherJDBC(returnValue)))
                return monitorJDBC(returnValue);
            else
                return returnValue;

        } catch (Throwable e) {
            // If there is a stack trace it will be part of the SQL details row.  Note a user reported a null pointer exception being thrown,
            // so i added the 'row!=null' check.
            if (executingQuery && isSQLDetailEnabled && row!=null) {
                stackTrace=Misc.getExceptionTrace(e);
                row[SQL_EXCEPTION_IND]=stackTrace;
            }

            throw e;
        } finally {
            // mon != null means either or both of the sql detail monitoring or sql summary monitoring is enabled.
            if (mon!=null && executingQuery) {
                long executionTime=mon.stop();

                if (isSQLDetailEnabled && row!=null)
                    row[SQL_EXECUTION_TIME_IND]=new Long(executionTime);

                if (isSQLSummaryEnabled)
                    sqlMon.add(executionTime).appendDetails(stackTrace).stop();
            }

        }
    }


    private boolean isExecuteQueryMethod(String methodName) {
        return "executeQuery".equals(methodName) || "executeUpdate".equals(methodName) || "execute".equals(methodName);
    }

    // note i don't think this is right in that prepareCall returns a CallableStatement which inherits from PreparedStatement and so adds more methods.  I
    // am not sure why i originally didn't add CallableStatement seperately
    private boolean returnsPreparedStatement(String methodName) {
        return "prepareStatement".equals(methodName) || "prepareCall".equals(methodName);
    }

    private boolean isStatementObject(Object[] args) {
        return (args!=null && args.length>=1);
    }

    private boolean shouldMonitorOtherJDBC(Object value) {
        return (value instanceof Statement || value instanceof Connection);
    }

    private boolean shouldMonitorResultSet(Object value) {
        return (value instanceof ResultSet && params.isResultSetEnabled && params.isInterfaceEnabled);
    }

    private Object monitorJDBC(Object returnValue) {
        if (returnValue instanceof ResultSet) {
            returnValue=MonProxyFactory.monitor((ResultSet) returnValue);
            cloneLabeler(returnValue);
        } else if (returnValue instanceof CallableStatement) {// note CallableStatement must occur before PreparedStatement and Satement as CallableStatement would always be true for both of them.
            returnValue=MonProxyFactory.monitor((CallableStatement) returnValue);
            cloneLabeler(returnValue);
        } else if (returnValue instanceof PreparedStatement) {// note PreparedStatement must occur before Statement as PreparedStatement would always be true for Satement too.
            returnValue=MonProxyFactory.monitor((PreparedStatement) returnValue);
            cloneLabeler(returnValue);
        } else if (returnValue instanceof Statement) {
            returnValue=MonProxyFactory.monitor((Statement) returnValue);
            cloneLabeler(returnValue);
        } else if (returnValue instanceof Connection) {
            returnValue=MonProxyFactory.monitor((Connection) returnValue);
            cloneLabeler(returnValue);
        }

        return returnValue;
    }

    /** Being as jdbc creates MonProxies for things like Statements and PreparedStatements from the original Monitored Connection it makes sense that they would
     * share the same type of Labeler.  The following clones the labeler of this Object.  The value passed into this method is is the newly created Proxy object that
     * has a MonProxy object as the inocation handler.  This invocation handler is returned and its labeler is set to the parents that created it (this objects labeler).
     * @param returnValue
     * @return
     */
    private void cloneLabeler(Object returnValue) {
        MonProxyLabelerInt labeler=getLabeler();
        MonProxy monProxy=MonProxyFactory.getMonProxy((Proxy) returnValue); // just created monitored object
        labeler=(MonProxyLabelerInt)labeler.clone();
        labeler.init(monProxy);// similar to a constructor.  leaves it up to the implementation class to decide how to handle it.
        monProxy.setLabeler(labeler); // clone this objects labeler and set it to be used by the newly created object.
    }


    // This will return true for CallableStatements too.
    private boolean isPreparedStatement(Object value) {
        return value instanceof PreparedStatement;
    }

    private String getSQL(Object sql) {
        return (sql==null) ? "null sql" : sql.toString();
    }

    /**
     * This class associates queries with the PreparedStatement and tracks accesses to it.  It also removes values from a Statements sql and
     * replaces it with '?'.  For example:  select * from table where key='steve', becomes select * from table where key=?
     *
     */
    private static class SQLDeArgMon {
        private int accessCounter=0;
        private String sql;

        private int monRows;
        private static final int LABEL_IND=0;
        private MonitorComposite monitors=null;
        private String[][] data=null;

        //The constructor creates monitors for 1) the first keyword (i.e. select/delete/...), 2) the sql for the PreparedStatement/Statement
        // 3) Any keyword matches passed in (such as table names)

        SQLDeArgMon(String statementType, String sql, List matchStrings) {

            SQLDeArger sqld=new SQLDeArger(sql, matchStrings, MonitorFactory.getMaxSqlSize());
            this.sql=sqld.getParsedSQL();
            data=sqld.getAll(); // contains 3 cols: summary sql dearged, detail sql, ms. (units)
            monRows=data.length;


            for (int i=0;i<monRows;i++) {
                StringBuffer label=new StringBuffer("MonProxy-SQL-");
                // note this loop matches one in SQLDeArger.getAll and positions are important
                // The constructor must be changed if this method changes - kind of ugly...
                // The following labels will appear in the jamon report.
                if (i==0) //All
                    data[i][LABEL_IND]=label.append("Type: ").append(data[i][LABEL_IND]).toString();
                else if (i==1) // SQL Type - i.e. select, delete, update etc.
                    data[i][LABEL_IND]=label.append("Type: ").append(data[i][LABEL_IND]).toString();
                else if (i==2) // Parsed SQL (Statement, or PreparedStatement then - select * from table where key=?
                    data[i][LABEL_IND]=label.append(statementType).append(": ").append(data[i][LABEL_IND]).toString();
                else // Match - Any string matches in the sql.
                    data[i][LABEL_IND]=label.append("Match: ").append(data[i][LABEL_IND]).toString();

            }

        }

        private SQLDeArgMon start() {
            synchronized (this) { // keep data from being clobbered while being accessed by this method
                if (monitors==null) {
                    // note if this is called in the constructor then monitors would be created even if sql summary is disabled
                    monitors=MonitorComposite.getMonitors(data);
                    data=null;
                }
            }

            monitors.start();
            return this;
        }

        // if a stacktrace exists then concatenate the stack trace to the query like this
        //  "select * from table"+"\n"+stackTrace
        // The monKeyDetails can be displayed by jamon listeners.
        private SQLDeArgMon appendDetails(String stackTrace) {
            if ("".equalsIgnoreCase(stackTrace))
                return this;

            Monitor[] monArr=monitors.getMonitors();
            StringBuilder sb=new StringBuilder();
            sb.append(monArr[0].getMonKey().getDetails()).append("\n").append(stackTrace);

            for (int i=0;i<monArr.length;i++) {
                Monitor mon=monArr[i];
                mon.getMonKey().setDetails(sb.toString());
            }

            return this;
        }

        private SQLDeArgMon stop() {
            monitors.stop();
            return this;
        }

        private SQLDeArgMon add(double time) {
            monitors.add(time);
            return this;
        }

        private synchronized int incrementCounter() {
            return accessCounter++;
        }

        private String getSQL() {
            return sql;
        }


        @Override
        public String toString() {
            return "accessCounter="+accessCounter+", sql="+sql;
        }
    }

}
