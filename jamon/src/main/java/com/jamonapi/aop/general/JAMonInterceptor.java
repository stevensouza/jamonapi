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
    public static final String EXCEPTION_UNITS ="Exception";

    /**
     * Returned when a method parameter is null
     */
    protected static final String NULL_STR = "<null>";

    /**
     * used when a value isn't returned, and yet we don't want to throw an exception (hey it's just monitoring)
     */
    protected static final String UNKNOWN =  "???";


    /**
     * Maximum length for parameters in the exception dump
     */
    public static final int DEFAULT_ARG_STRING_MAX_LENGTH = 125;

    /**
     * Parameters kept in the details section are capped at a max length and this string is put at the end of it to
     * indicate there is more data that is not shown.
     */
    public static final String DEFAULT_MAX_STRING_ENDING = "...";

    /**
     * JAMon exception label - can be overridden in subclasses.
     */
    protected String exceptionLabel = getClass().getName();

    /**
     * JAMon unknown label - can be overridden in subclasses.
     */
    protected String unknownTimeLabel = getClass().getName()+"."+UNKNOWN;


    /** Enable/disable saving method parameters in the details (stacktrace) sent to jamon.  It is enabled by default
     *
     */
    protected boolean useParametersInDetails = true;

    protected JAMonInterceptor(String exceptionLabel) {
        this.exceptionLabel = exceptionLabel;
    }

    public JAMonInterceptor() {
    }


    public boolean useParametersInDetails() {
        return useParametersInDetails;
    }

    public void setUseParametersInDetails(boolean useParametersInDetails) {
        this.useParametersInDetails = useParametersInDetails;
    }

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
            key.setDetails(onException(ctx, label, e));
            throw e;
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
        String methodName = (ctx.getMethod() != null ? ctx.getMethod().toString() : unknownTimeLabel);
        return methodName;
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
     * Creates three JAMon exception monitor to keep track of the exceptions - one
     * monitor is used as overall exception count, a second one is used
     * to track the EJB-related exceptions, and a third the specific exception classname
     * This method can be overridden.
     *
     * @param ctx Invocation context
     * @param label the label used for this method invocation
     * @param exception the offending exception
     * @return Detailed string representing the exception.  It can have the method call, stacktrace and function parameters
     *  or anything else of use.
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
     * Creates the array used to display exception information in
     * JAMon's "Exception Details" view.
     *
     * @param label the label used for this method invocation
     * @param parameters the method invocation parameters
     * @param exception the offending exception
     * @return exception the offending exception thrown to the caller
     */
    protected  String createExceptionDetails(String label, Object[] parameters, Exception exception) {
        String lineSeparator = "\n";
        StringBuilder temp = new StringBuilder();
        temp.append(label).append(",").append(lineSeparator);

        if(parameters != null && useParametersInDetails()) {
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
    
}
