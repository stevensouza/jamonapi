package com.jamonapi.aop.spring;

import com.jamonapi.utils.Misc;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

/**
 * Created by stevesouza on 6/8/14.
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

    @Override
    public void setUseArgsWithMethodDetails(boolean useArgsWithMethodDetails) {

    }

    @Override
    public void setUseArgsWithExceptionDetails(boolean useArgsWithExceptionDetails) {

    }

}
