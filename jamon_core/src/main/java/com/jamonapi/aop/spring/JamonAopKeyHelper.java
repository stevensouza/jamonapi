package com.jamonapi.aop.spring;

import com.jamonapi.utils.Misc;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

/**
 * Class that monitors mehtods, and tracks their excepctions with jamon.  The difference between this class and
 * it base class is this class can put the values passed as arguments in jamon.  For example if a method was invoked
 * as 'myObject.myMethod("jeff", "beck"); the arguments jeff, beck would be accessible in the jamonListener
 * via jamon details for the method (useArgsWtihMethodDetails) or the exception (useArgsWithExceptionDetails).  If both
 * are disabled then it has the same effect as its base class
 */
@Component
public class JamonAopKeyHelper implements JamonAopKeyHelperInt<ProceedingJoinPoint> {

    // store args in jamon as the normal details per method invocation
    private boolean useArgsWithMethodDetails = false;
    // store args if an exception is thrown in the exception details
    private boolean useArgsWithExceptionDetails = false;

    public JamonAopKeyHelper() {
    }

    public JamonAopKeyHelper(boolean useArgsWithMethodDetails, boolean useArgsWithExceptionDetails) {
        this.useArgsWithMethodDetails = useArgsWithMethodDetails;
        this.useArgsWithExceptionDetails = useArgsWithExceptionDetails;
    }

    public void setUseArgsWithMethodDetails(boolean useArgsWithMethodDetails) {
        this.useArgsWithMethodDetails = useArgsWithMethodDetails;
    }

    public void setUseArgsWithExceptionDetails(boolean useArgsWithExceptionDetails) {
        this.useArgsWithExceptionDetails = useArgsWithExceptionDetails;
    }

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
        String signature = getLabel(proceedingJoinPoint);
        return createDetailMessage(proceedingJoinPoint, signature, useArgsWithMethodDetails);
    }

    @Override
    public String getDetails(ProceedingJoinPoint proceedingJoinPoint, Throwable exception) {
        String detailMessage = createDetailMessage(proceedingJoinPoint, "", useArgsWithExceptionDetails);
        String stackTrace = new StringBuffer(detailMessage)
                .append("\n\nstackTrace=")
                .append(Misc.getExceptionTrace(exception))
                .toString();
        return stackTrace;
    }

    private void appendArgs(StringBuilder sb, Object[] args) {
        if (args==null) {
            return;
        }

        int lastElement = args.length-1;
        for (int i=0; i<=lastElement; i++) {
            if (i==0) {
                sb.append("\narguments(").append(args.length).append("):\n");
            }
            sb.append(args[i]);
            if (lastElement!=0 && i!=lastElement) {
                sb.append(",\n");
            }
        }

    }

    private String createDetailMessage(ProceedingJoinPoint proceedingJoinPoint, String baseMessage, boolean useArgsInDetails) {
        if (useArgsInDetails) {
            StringBuilder sb = new StringBuilder().append(baseMessage);
            appendArgs(sb, proceedingJoinPoint.getArgs());
            return sb.toString();
        } else {
            return baseMessage;
        }
    }

}
