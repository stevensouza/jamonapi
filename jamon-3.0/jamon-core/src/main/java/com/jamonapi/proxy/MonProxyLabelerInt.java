package com.jamonapi.proxy;

import java.lang.reflect.Method;

/** This method is called by MonProxy to determine the jamon summary label for each method invoked.  It also creates a monitor when the interface throws an
 * exception.  getSummaryLabel(...) and getExceptionLabel(...) are called respectively for each type of monitor.  You can replace the default implementation
 * with your own should you want.  Simply call MonProxy.setLabeler(...) and it will be used.
 * Note you should implement public Object clone().
 */
public interface MonProxyLabelerInt extends Cloneable {
    /** Summary label that you want jamon to use */
    public String getSummaryLabel(Method method);

    /** When an exception is thrown this returns what jamon label do you want to use */
    public String getExceptionLabel(Method method);

    /** Note init is called at time of initialization. This is a good time to get the class name being monitored for example
     * via something like monProxy.getMonitoredObject().getClass().getName()
     */
    public void init(MonProxy monProxy);

    public Object clone();
}
