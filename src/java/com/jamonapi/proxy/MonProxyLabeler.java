package com.jamonapi.proxy;

import java.lang.reflect.Method;

/** Standard implementation of creating the jamon label for the MonProxy class.  Something like this for method calls:
 * 
 *  <p>  MonProxy-Interface (class=org.hsqldb.jdbcPreparedStatement): public abstract java.sql.ResultSet java.sql.PreparedStatement.executeQuery() throws java.sql.SQLException</p>
 * 
 *  <p>and this should the method throw an Exception:</p>
 * 
 *  <p>  MonProxy-Exception: (class=com.mypackage.MyClass): Exception: public void helloWorld() throws MyException</p>
 */
public class MonProxyLabeler implements MonProxyLabelerInt {
    private String summaryPrefix;
    private String exceptionPrefix;

    public MonProxyLabeler() {
        this("","");
    }

    public MonProxyLabeler(String summaryPrefix, String exceptionPrefix) {
        this.summaryPrefix=summaryPrefix;
        this.exceptionPrefix=exceptionPrefix;
    }


    /** Note init is called at time of initialization. This is a good time to get the class name being monitored for example
     * via something like monProxy.getMonitoredObject().getClass().getName()
     */
    public void init(MonProxy monProxy) {
        String className = "(class="+monProxy.getMonitoredObject().getClass().getName()+")";
        summaryPrefix="MonProxy-Interface "+className+": ";
        exceptionPrefix="MonProxy-Exception: " + className+ " Exception: " ;
    }

    public String getSummaryLabel(Method method) {
        return new StringBuffer().
        append(summaryPrefix).
        append(method.toString()).
        toString();
    }

    public String getExceptionLabel(Method method) {
        return new StringBuffer().
        append(exceptionPrefix).
        append(method.toString()).
        toString();
    }

    public String getSummaryPrefix() {
        return summaryPrefix;
    }

    public String getExceptionPrefix() {
        return exceptionPrefix;
    }

    /** This method should return an exact copy of this object though it need not be the same instance. */
    @Override
    public Object clone() {
        return new MonProxyLabeler(summaryPrefix, exceptionPrefix);
    }

}
