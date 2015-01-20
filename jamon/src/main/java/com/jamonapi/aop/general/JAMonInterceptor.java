package com.jamonapi.aop.general;

import com.jamonapi.MonKeyImp;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.jamonapi.utils.Misc;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/** 
 * JAMon Interceptor measuring method execution time - can be used in EJB, or CDI in general.
 */
public class JAMonInterceptor {

    /**
     * Default exception label
     */
    public static final String DEFAULT_EXCEPTION_LABEL ="JAMonInterceptor.Exception";

    /** 
     * Hierarchy delimiter in Simon name.
     */
    public static final String DEFAULT_HIERARCHY_DELIMITER = ".";
    
    /**
     * Default prefix for JAMon interceptor if no "prefix" init parameter is used.
     */
    public static final String DEFAULT_INTERCEPTOR_PREFIX = "JAMonInterceptor";

    /**
     * Default label for an unknown method
     */
    public static final String DEFAULT_UNKNOWN_LABEL = DEFAULT_INTERCEPTOR_PREFIX + DEFAULT_HIERARCHY_DELIMITER + "???";

    /**
     * JAMon name prefix - can be overridden in subclasses.
     */
    protected String interceptorPrefix;

    /**
     * JAMon hierarchy prefix - can be overridden in subclasses.
     */
    protected String hierarchyDelimiter;

    /**
     * JAMon exception label - can be overridden in subclasses.
     */
    protected String exceptionLabel;

    /**
     * JAMon unknown label - can be overridden in subclasses.
     */
    protected String unknownLabel;

    private static final int DETAILS_INDEX_EXCEPTION =1;

    public JAMonInterceptor() {
        this.interceptorPrefix = DEFAULT_INTERCEPTOR_PREFIX;
        this.hierarchyDelimiter = DEFAULT_HIERARCHY_DELIMITER;
        this.exceptionLabel = DEFAULT_EXCEPTION_LABEL;
        this.unknownLabel = DEFAULT_UNKNOWN_LABEL;
    }

    /**
     * Returns JAMon's label for the specified Invocation context.
     * By default it contains "prefix.method(args.length)"
     * This method can be overridden.
     *
     * @param ctx Invocation context
     * @return fully qualified label
     */
    protected String getJamonLabel(InvocationContext ctx) {
        String methodName = (ctx.getMethod() != null ? ctx.getMethod().toString() : unknownLabel);
        return interceptorPrefix + hierarchyDelimiter + methodName;
    }

    /**
     * Indicates whether the method invocation should be monitored.
     * Default behavior always returns true.
     * This method can be overridden
     *
     * @param ctx Method invocation context
     * @return true to enable JAMon, false either
     */
    @SuppressWarnings("unused")
    protected boolean isMonitored(InvocationContext ctx) {
        return true;
    }

    /**
     * Default exception handling.
     * Creates an JAMon exception monitor to keep track of the exceptions.
     * This method can be overridden.
     *
     * @param ctx Invocation context
     * @return exception the offending exception
     */
    @SuppressWarnings("unused")
    protected Exception onException(InvocationContext ctx, String label, Exception exception) throws Exception {
        Object[] details = new Object[]{label, ""};
        details[DETAILS_INDEX_EXCEPTION]=Misc.getExceptionTrace(exception);
        MonitorFactory.add(new MonKeyImp(exceptionLabel, details, "Exception"), 1);
        MonitorFactory.add(new MonKeyImp(MonitorFactory.EXCEPTIONS_LABEL, details, "Exception"), 1);
        return exception;
    }
    
    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {

        Monitor mon=null;
        String label=unknownLabel;

        if(!isMonitored(ctx)) {
            return ctx.proceed();
        }

        try {
            label = getJamonLabel(ctx);
            Object details = new Object[]{label, ""};
            mon = MonitorFactory.start(new MonKeyImp(label, details, "ms."));
            return ctx.proceed();
        } catch (Exception e) {
            throw onException(ctx, label, e);
        } finally {
            if(mon != null) {
                mon.stop();
            }
        }
    }
}
