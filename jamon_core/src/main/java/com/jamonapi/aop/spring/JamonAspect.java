package com.jamonapi.aop.spring;

import com.jamonapi.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;

/**
 * This class tracks the performance of classes and methods:<br/>
 *   JAMon Label=void com.stevesouza.spring.MonitorMe3.anotherMethod2(), Units=ms.: (LastValue=0.0, Hits=10.0, Avg=0.3, Total=3.0, Min=0.0, Max=1.0, Active=0.0, Avg Active=1.0, Max Active=1.0, First Access=Tue Jun 03 21:11:21 CEST 2014, Last Access=Tue Jun 03 21:11:26 CEST 2014)
 *
 * <p>And Exceptions if the method throws one.  Exceptions are tracked under a general name for all exceptions 'com.jamonapi.Exceptions', as
 * well as the specific exception name (i.e. java.io.IOException, ...). An example follows:
 *   <br/>JAMon Label=java.io.IOException, Units=Exception: (LastValue=1.0, Hits=10.0, Avg=1.0, Total=10.0, Min=1.0, Max=1.0, Active=0.0, Avg Active=0.0, Max Active=0.0, First Access=Tue Jun 03 21:11:21 CEST 2014, Last Access=Tue Jun 03 21:11:26 CEST 2014)
 *   <br/>JAMon Label=com.jamonapi.Exceptions, Units=Exception: (LastValue=1.0, Hits=10.0, Avg=1.0, Total=10.0, Min=1.0, Max=1.0, Active=0.0, Avg Active=0.0, Max Active=0.0, First Access=Tue Jun 03 21:11:21 CEST 2014, Last Access=Tue Jun 03 21:11:26 CEST 2014)
 * </p>
 *
 * <p>The detailed stacktrace is kept if an exception is thrown.  This can be viewed in the jamon war, if setExceptionBufferListener
 * is enabled which it is by default.</p>
 *
 * <p>There are also method argument values which can be saved in the jamon details for both the method invocation jamon details and the stack trace
 * jamon details.</p>
 *
 * <p>Spring can automatically find this aspect - or alternatively it could be created explicitly in applicationContext.xml.
 * See applicationContext.xml in the test code for some sample usage.  By default each aspect is a singleton
 * within the applicationContext</p>
 */

@Aspect
public class JamonAspect {

    private static final String EXCEPTION = "Exception";

    // The pointcut could be defined here, but it is more flexibly defined in applicationContext.xml
    //     @Around("com.stevesouza.spring.aop.SystemAopPointcutDefinitions.camelOperation()")
    // or  @Around("com.stevesouza.spring.aop.SystemAopPointcutDefinitions.monitorAnnotatedClass()")

    // generates jamon labels and details.
    protected JamonAopKeyHelperInt keyHelper;

    // Note the buffer listener is enabled by default.
    public JamonAspect() {
        keyHelper = new JamonAopKeyHelper();
        setExceptionBufferListener(true);
    }

    public JamonAspect(JamonAopKeyHelperInt keyHelper) {
       setKeyHelper(keyHelper);
       setExceptionBufferListener(true);
    }

    /** Wrap jamon calls around the invoked method.  Also track exceptions if they are thrown */
    public Object monitor(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object retVal = null;
        String label = keyHelper.getLabel(proceedingJoinPoint);
        String details = keyHelper.getDetails(proceedingJoinPoint);
        MonKeyImp key = new MonKeyImp(label, details, "ms.");
        Monitor mon = MonitorFactory.start(key);
        try {
            retVal = proceedingJoinPoint.proceed();
        } catch (Throwable t) {
            String exceptionDetails = keyHelper.getDetails(proceedingJoinPoint, t);
            key.setDetails(exceptionDetails);
            trackException(t, exceptionDetails);
            throw t;
        } finally {
            mon.stop();
        }

        return retVal;
    }

    // add monitors for the thrown exception and also put the stack trace in the details portion of the key
    private void trackException(Throwable exception, String exceptionDetails) {
        MonitorFactory.add(new MonKeyImp(keyHelper.getExceptionLabel(exception), exceptionDetails, EXCEPTION), 1);
        MonitorFactory.add(new MonKeyImp(MonitorFactory.EXCEPTIONS_LABEL, exceptionDetails, EXCEPTION), 1);
    }

    public void setKeyHelper(JamonAopKeyHelperInt keyHelper) {
        this.keyHelper = keyHelper;
    }

    /** If true is passed in then a buffer will contain the most recent stack traces.  This is viewable via the jamon
     * web app or programmatically.  This is very handy for debugging.
     *
     * By default this is enabled (true)
     * @param enable
     */
    public void setExceptionBufferListener(boolean enable) {
        MonKey key = new MonKeyImp(MonitorFactory.EXCEPTIONS_LABEL, EXCEPTION);
        boolean hasBufferListener = MonitorFactory.getMonitor(key).hasListener("value", "FIFOBuffer");

        if (enable && !hasBufferListener) {
            MonitorFactory.getMonitor(key).addListener("value", JAMonListenerFactory.get("FIFOBuffer"));
        } else if (!enable && hasBufferListener) {
            MonitorFactory.getMonitor(key).removeListener("value", "FIFOBuffer");
        }
    }

    /** Specifies to have the methods arguments viewable in the jamon monitor details. This is viewable from the jamon
     * web application.  It will allow you to see what values were passed to monitored methods via the web app.
     * By default this is disabled.
     * @param useArgsWithMethodDetails
     */
    public void setUseArgsWithMethodDetails(boolean useArgsWithMethodDetails) {
        keyHelper.setUseArgsWithMethodDetails(useArgsWithMethodDetails);
    }

    /**
     * Specifies to have the methods arguments viewable in the jamon monitor details. This is viewable from the jamon
     * web application.  It will allow you to see what values were passed to monitored methods that threw an exception.
     * By default this is disabled.
     * @param useArgsWithExceptionDetails
     */
    public void setUseArgsWithExceptionDetails(boolean useArgsWithExceptionDetails) {
        keyHelper.setUseArgsWithExceptionDetails(useArgsWithExceptionDetails);
    }

}
