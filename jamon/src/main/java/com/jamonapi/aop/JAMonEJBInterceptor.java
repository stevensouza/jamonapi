package com.jamonapi.aop;

import com.jamonapi.aop.general.JAMonInterceptor;

/** 
 * Class for monitoring EJB's via AOP. This class exists for
 * mainly for backward compatibility with previous versions of JAMon.
 */
public class JAMonEJBInterceptor extends JAMonInterceptor{

    /**
     * Mimic the labels of the original implementation.
     */
    public JAMonEJBInterceptor() {
        this.interceptorPrefix = "JAMonEJBInterceptor: ";
        this.hierarchyDelimiter = "";
        this.exceptionLabel = "JAMonEJBInterceptor.EJBException";
        this.unknownLabel = interceptorPrefix + hierarchyDelimiter + "???";
    }
}
