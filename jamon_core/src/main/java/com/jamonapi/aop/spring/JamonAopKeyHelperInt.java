package com.jamonapi.aop.spring;

/**
 * This was intended to be more generic, but currently works with JoinPoints only.
 *
 * This class creates jamon labels (method, and exception) and creates details too
 * (for method and exception).
 * T = joinPoint or method
 */
public interface JamonAopKeyHelperInt<T> {
    /** jamon label to be used in key. Example: void com.stevesouza.spring.MonitorMe3.myMethod2(String)  */
    public String getLabel(T proceedingJoinPoint);

    /**
     * Get a jamon key that represents the excepton being thrown.  This is typically the class name
     * i.e. java.lang.RuntimeException
     */
    public String getExceptionLabel(Throwable exception);

    /** Normal details for standard monitor.  A typical use is to display in a JAMonBufferListener */
    public String getDetails(T proceedingJoinPoint);

    /**  Details for when an exception occurred in the method.  Usually the stacktrace. */
    public String getDetails(T proceedingJoinPoint, Throwable exception);

    /** If true then the method argument details will be appended to the details whenever the monitored
     * method is called.  Say you invoked myObject.myMethod("steve") then something like the
     * following would be appended to the details:<br/>
     *     arguments(1):<br/>
     *     steve<br/>
     *
     * @param useArgsWithMethodDetails
     */
    public void setUseArgsWithMethodDetails(boolean useArgsWithMethodDetails);

    /** If true then the method argument details will be appended to the details when an exception is thrown only.
     * Say you invoked myObject.myMethod("steve") and an exception was thrown then something like the following
     * would be appended to the
     * details:<br/>
     *     arguments(1):<br/>
     *     steve<br/>
     *
     * @param useArgsWithExceptionDetails
     */
    public void setUseArgsWithExceptionDetails(boolean useArgsWithExceptionDetails);

}
