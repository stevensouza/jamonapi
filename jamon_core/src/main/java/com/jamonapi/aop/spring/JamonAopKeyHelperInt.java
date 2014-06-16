package com.jamonapi.aop.spring;

/**
 * Created by stevesouza on 6/8/14.
 *
 * T = joinPoint or method
 * M = Monitor like jamon or something else
 */
public interface JamonAopKeyHelperInt<T> {
    /** jamon label to be used in key */
    public String getLabel(T proceedingJoinPoint);
    /**
     * get a jamon key that represents the excepton being thrown.  This is typically the class name
     * i.e. java.lang.RuntimeException
     */
    public String getExceptionLabel(Throwable exception);
    /** normal details for standard monitor */
    public String getDetails(T proceedingJoinPoint);
    /**  details for when an exception occurred in the method */
    public String getDetails(T proceedingJoinPoint, Throwable exception);

    public void setUseArgsWithMethodDetails(boolean useArgsWithMethodDetails);

    public void setUseArgsWithExceptionDetails(boolean useArgsWithExceptionDetails);

}
