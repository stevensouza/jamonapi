package com.jamonapi.aop.spring;

import com.jamonapi.utils.Misc;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

/**
 * Class that monitors helps create keys, details, arguments and exceptions for jamon aop.  With agument tracking
 * If a method was invoked as 'myObject.myMethod("jeff", "beck"); the arguments jeff, beck would be accessible
 * in the jamonListener  via jamon details for the method (setUseArgsWithMethodDetails) or the exception
 * (setUseArgsWithExceptionDetails).
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

    /**
     * Track arguments for all methods monitored.
     * @param useArgsWithMethodDetails
     */
    public void setUseArgsWithMethodDetails(boolean useArgsWithMethodDetails) {
        this.useArgsWithMethodDetails = useArgsWithMethodDetails;
    }

    /**
     * Track arguments for all exceptions thrown.
     * @param useArgsWithExceptionDetails
     */
    public void setUseArgsWithExceptionDetails(boolean useArgsWithExceptionDetails) {
        this.useArgsWithExceptionDetails = useArgsWithExceptionDetails;
    }

    /** Create the jamon label as a method name
     *
     * @param proceedingJoinPoint
     * @return
     */
    @Override
    public String getLabel(ProceedingJoinPoint proceedingJoinPoint) {
        return proceedingJoinPoint.getSignature().toString();
    }

    /** Create a label out of the passed in exception. It will be the exception class name
     *
     * @param exception
     * @return
     */
    @Override
    public String getExceptionLabel(Throwable exception) {
        return exception.getClass().getName();
    }

    /** Create details for the invoked method.  This is used in jamon listeners
     * (for example viewable in the jamon web app).
     *
     * @param proceedingJoinPoint
     * @return
     */
    @Override
    public String getDetails(ProceedingJoinPoint proceedingJoinPoint) {
        String signature = getLabel(proceedingJoinPoint);
        return createDetailMessage(proceedingJoinPoint, signature, useArgsWithMethodDetails);
    }

    /** Create details for when an exception is thrown.  This would be the stack trace and possibly the methods
     * arguments.
     *
     * @param proceedingJoinPoint
     * @param exception
     * @return
     */
    @Override
    public String getDetails(ProceedingJoinPoint proceedingJoinPoint, Throwable exception) {
        String detailMessage = createDetailMessage(proceedingJoinPoint, "", useArgsWithExceptionDetails);
        String stackTrace = new StringBuffer(detailMessage)
                .append("\n\nstackTrace=")
                .append(Misc.getExceptionTrace(exception))
                .toString();
        return stackTrace;
    }

    // append the method arguments to the base detail message. for example myMethod("steve") would append
    // arguments(1):
    // steve
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

    // build the detail message - appending the args if true is passed in.
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
