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
    public static final String DEFAULT_EXCEPTION_LABEL ="Exception";

    /** 
     * Hierarchy delimiter in Simon name.
     */
    public static final String DEFAULT_HIERARCHY_DELIMITER = ".";
    
    /**
     * Default prefix for JAMon interceptor if no "prefix" init parameter is used.
     */
    public static final String DEFAULT_INTERCEPTOR_PREFIX = "";

    /**
     * Default label for an unknown method
     */
    public static final String DEFAULT_UNKNOWN_LABEL = DEFAULT_INTERCEPTOR_PREFIX + DEFAULT_HIERARCHY_DELIMITER + "???";

    /**
     * Maximum length for parameters in the exception dump
     */
    public static final int DEFAULT_ARG_STRING_MAX_LENGTH = 125;
    
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

    public JAMonInterceptor() {
        this.interceptorPrefix = DEFAULT_INTERCEPTOR_PREFIX;
        this.hierarchyDelimiter = DEFAULT_HIERARCHY_DELIMITER;
        this.exceptionLabel = DEFAULT_EXCEPTION_LABEL;
        this.unknownLabel = DEFAULT_UNKNOWN_LABEL;
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
    
    /**
     * Returns JAMon's label for the specified Invocation context.
     * By default it contains "prefix.method(args.length)"
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
     *
     * @param ctx Method invocation context
     * @return true to enable JAMon, false either
     */
    protected boolean isMonitored(InvocationContext ctx) {
        return true;
    }

    /**
     * Default exception handling.
     * Creates an JAMon exception monitor to keep track of the exceptions.
     * This method can be overridden.
     *
     * @param ctx Invocation context
     * @param label the label used for this method invocation
     * @param exception the offending exception
     * @return exception the offending exception
     */
    protected Exception onException(InvocationContext ctx, String label, Exception exception) throws Exception {
        Object[] parameters = ctx.getParameters();
        Object[] details = createExceptionDetails(label, parameters, exception);
        // TODO sgoeschl 2015-01-30 why we need the second MonKeyIml here?!
        // MonitorFactory.add(new MonKeyImp(exceptionLabel, details, "Exception"), 1);
        MonitorFactory.add(new MonKeyImp(MonitorFactory.EXCEPTIONS_LABEL, details, "Exception"), 1);
        return exception;
    }

    /**
     * Default exception handling.
     * Creates the array used to display exception information in
     * JAMon's "Exception Details" view.
     *
     * @param label the label used for this method invocation
     * @param parameters the method invocation parameters
     * @param exception the offending exception
     * @return exception the offending exception thrown to the caller
     */
    protected Object[] createExceptionDetails(String label, Object[] parameters, Exception exception) {
        
        StringBuilder temp = new StringBuilder();
        String lineSeparator = System.getProperty("line.separator");

        if(parameters != null) {
            temp.append("=== Parameters ===").append(lineSeparator);
            for(int i=0; i<parameters.length; i++) {
                Object parameter = parameters[i];
                temp.append("[").append(i).append("]={");
                temp.append(toString(parameter));
                temp.append("}");
                temp.append(lineSeparator);
            }
        }

        if(exception != null) {
            temp.append("=== Stack Trace ===").append(lineSeparator);
            temp.append(Misc.getExceptionTrace(exception));
        }

        return new Object[] {label, temp.toString()};
    }
    
    /**
     * Turns a single method parameter into a string. To keep
     * the functionality safe we truncate overly long strings and
     * ignore any exceptions.
     */
    protected String toString(Object parameter) {
    
        if(parameter == null) {
            return "<null>";
        }
        
        try {
            String result = Misc.getAsString(parameter);
            if(result.length() > DEFAULT_ARG_STRING_MAX_LENGTH) {
                result = result.substring(0, DEFAULT_ARG_STRING_MAX_LENGTH) + "...";
            }
            return result;
        }
        catch(RuntimeException e) {
            return "???";
        }
    }
    
}
