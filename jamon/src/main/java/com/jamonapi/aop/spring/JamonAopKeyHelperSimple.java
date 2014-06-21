package com.jamonapi.aop.spring;

import com.jamonapi.utils.Misc;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

/**
 * Created by stevesouza on 6/8/14.  Simple implementation of creating jamon keys (labels and details).  The usage
 * of arguments is a noop;
 */
@Component
public class JamonAopKeyHelperSimple implements JamonAopKeyHelperInt<ProceedingJoinPoint> {
    @Override
    public String getLabel(ProceedingJoinPoint proceedingJoinPoint) {
        return proceedingJoinPoint.getSignature().toString();
    }

    @Override
    public String getExceptionLabel(Throwable exception) {
        return exception.getClass().getName();
    }

    @Override
    public String getDetails(ProceedingJoinPoint proceedingJoinPoint) {
        return proceedingJoinPoint.getSignature().toString();
    }

    @Override
    public String getDetails(ProceedingJoinPoint proceedingJoinPoint, Throwable exception) {
        return new StringBuffer("stackTrace=")
                .append(Misc.getExceptionTrace(exception))
                .toString();
    }

    /** This method is a noop
     *
     * @param useArgsWithMethodDetails
     */
    @Override
    public void setUseArgsWithMethodDetails(boolean useArgsWithMethodDetails) {

    }

    /** This method is a noop
     *
     * @param useArgsWithExceptionDetails
     */
    @Override
    public void setUseArgsWithExceptionDetails(boolean useArgsWithExceptionDetails) {

    }

}
