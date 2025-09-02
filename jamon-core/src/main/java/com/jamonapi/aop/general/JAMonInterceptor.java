package com.jamonapi.aop.general;

import com.jamonapi.MonKeyImp;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.jamonapi.utils.Misc;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/** 
 * JAMon Interceptor for measuring method execution time, and tracking any exceptions the method calls:
 *  can be used in EJB, or CDI in general.  Note when an exception occurs several exception monitors are
 *  created from very general (com.jamonapi.Exceptions) to specific (for example com.mypackage.MyException).
 *  Also a 'detail' data string is created so more context can be viewed when an exception is thrown.  The 'detail'
 *  contains the method name, stacktrace, and optionally (enabled by deafault)
 *  all of the method parameter names and values.
 */
public class JAMonInterceptor {

    /**
     * Exception units
     */
    protected static final String EXCEPTION_UNITS ="Exception";

    /**
     * Returned when a method parameter is null
     */
    protected static final String NULL_STR = "<null>";

    /**
     * used when a value isn't returned, and yet we don't want to throw an exception (hey it's just monitoring)
     */
    protected static final String UNKNOWN =  "???";


    /**
     * Maximum length for a parameter in the exception dump
     */
    protected static final int DEFAULT_ARG_STRING_MAX_LENGTH = 125;

    /**
     * Parameters kept in the details section are capped at a max length and this string is put at the end of
     * the string after the truncation point to indicate there is more data that is not shown.
     */
    protected static final String DEFAULT_MAX_STRING_ENDING = "...";

    protected static final String MONITOR_PREFIX="JAMonInterceptor.";

    /**
     * JAMon exception label - can be overridden in subclasses. This name should represent a class of errors such as
     *  EJB errors as opposed to other types such as jdbc, or http errors.
     *
     * Example default value: com.jamonapi.aop.general.JAMonInterceptor
     */
    protected String exceptionLabel = getClass().getName();

    /**
     * JAMon unknown label - Used when a monitored method is unknown. Note I think the only
     * situation this is needed is in testing though
     *
     *  Example default value: com.jamonapi.aop.general.JAMonInterceptor.???
     */
    private final String unknownTimeLabel = getClass().getName()+"."+UNKNOWN;

    protected static final String LINE_SEPARATOR = "\n";


    /** Enable/disable saving method parameter names and values in the 'detail' data created when an exception is thrown.
     * 'detail' always contains the method name and the stacktrace and optionally it can contain the parameter names
     * and values too.  This is enabled by default
     */
    private boolean useParametersInDetails = true;

    protected JAMonInterceptor(String exceptionLabel) {
        this.exceptionLabel = exceptionLabel;
    }

    public JAMonInterceptor() {
    }


    public boolean useParametersInDetails() {
        return useParametersInDetails;
    }

    /**
     * @param useParametersInDetails Enable/disable saving parameter names and values in the 'detail' data section.
     */
    public JAMonInterceptor setUseParametersInDetails(boolean useParametersInDetails) {
        this.useParametersInDetails = useParametersInDetails;
        return this;
    }

    /** Decorates method calls by monitoring them for performance and exceptions
     *
      * @param ctx
     * @return
     * @throws Exception
     */
    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {
        Monitor mon=null;
        MonKeyImp key=null;
        String label=null;

        if(!isMonitored(ctx)) {
            return ctx.proceed();
        }

        try {
            label = getJamonLabel(ctx);
            key = new MonKeyImp(label, "ms.");
            mon = MonitorFactory.start(key);
            return ctx.proceed();
        } catch (Exception e) {
            if (key != null) {
                key.setDetails(onException(ctx, label, e));
            }
            throw e;
        } finally {
            if(mon != null) {
                mon.stop();
            }
        }
    }
    
    /**
     * Returns JAMon's label for the specified Invocation context.
     * By default it contains "method(args.length)"
     *
     * @param ctx Invocation context
     * @return fully qualified label
     */
    protected String getJamonLabel(InvocationContext ctx) {
        String methodName = (ctx.getMethod() != null ? ctx.getMethod().toString() : unknownTimeLabel);
        return MONITOR_PREFIX+methodName;
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
     * This method creates a 'detail' string for viewing and creates three JAMon exception monitor to
     * keep track of the exceptions -
     * 1) one monitor is used as overall exception count,
     * 2) another monitors tracks the EJB-related exceptions,
     * 3) And a third tracks specific exception such as java.lang.RuntimeException.
     *
     * This method can be overridden.
     *
     * @param ctx Invocation context
     * @param label the label used for this method invocation
     * @param exception the offending exception
     * @return Detailed string representing the exception.  It has the method call, stacktrace and function parameter
     * names and values (but anything can be returned that is useful context for debugging)
     */
    protected String onException(InvocationContext ctx, String label, Exception exception) throws Exception {
        Object[] parameters = ctx.getParameters();
        String details = createExceptionDetails(label, parameters, exception);

        // most general counter: com.jamonapi.Exceptions
        MonitorFactory.add(new MonKeyImp(MonitorFactory.EXCEPTIONS_LABEL, details, EXCEPTION_UNITS), 1);

        // counter specific to this class of exceptions such as ejb. example:  com.jamonapi.EjbExceptions
        MonitorFactory.add(new MonKeyImp(exceptionLabel, details, EXCEPTION_UNITS), 1);

        // most specific exception counter.  Example:  com.myapp.MyGreatException
        if (exception!=null) {
            MonitorFactory.add(new MonKeyImp(exception.getClass().getName(), details, EXCEPTION_UNITS), 1);
        }

        return details;
    }

    /**
     * Default exception handling.
     * Creates the String used to display exception information in
     * JAMon's "Exception Details" view.  This logic creates a string representation of the stacktrace as well as
     * all method parameter names and values.
     *
     * @param label the label used for this method invocation
     * @param parameters the method invocation parameters
     * @param exception the offending exception
     * @return exception the offending exception thrown to the caller
     */
    protected  String createExceptionDetails(String label, Object[] parameters, Exception exception) {
        StringBuilder temp = new StringBuilder();

        addJamonLabelToDetails(temp, label);
        addParameterInfoToDetails(temp, parameters);
        addExceptionStackTraceToDetails(temp, exception);

        return temp.toString();
    }

    /**
     * Turns a single method parameter into a string. To keep
     * the functionality safe we truncate overly long strings and
     * ignore any exceptions.
     */
    protected String toString(Object parameter) {
        if(parameter == null) {
            return NULL_STR;
        }
        
        try {
            String result = Misc.getAsString(parameter);
            if(result.length() > DEFAULT_ARG_STRING_MAX_LENGTH) {
                result = result.substring(0, DEFAULT_ARG_STRING_MAX_LENGTH) + DEFAULT_MAX_STRING_ENDING;
            }
            return result;
        }
        catch(Throwable e) {
            return UNKNOWN;
        }
    }

    private void addJamonLabelToDetails(StringBuilder temp, String label) {
        temp.append(label).append(",").append(LINE_SEPARATOR);
    }

    private void addExceptionStackTraceToDetails(StringBuilder temp,Exception exception) {
        if(exception != null) {
            temp.append("=== Stack Trace ===").append(LINE_SEPARATOR);
            temp.append(Misc.getExceptionTrace(exception));
        }
    }

    private void addParameterInfoToDetails(StringBuilder temp, Object[] parameters) {
        if(parameters != null && useParametersInDetails()) {
            temp.append("=== Parameters ===").append(LINE_SEPARATOR);
            for(int i=0; i<parameters.length; i++) {
                Object parameter = parameters[i];
                temp.append("[").append(i).append("]={");
                temp.append(toString(parameter));
                temp.append("}");
                temp.append(LINE_SEPARATOR);
            }
        }
    }
    
}
