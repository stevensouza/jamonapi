package com.jamonapi.aop.general;

import com.jamonapi.MonitorFactory;
import com.jamonapi.utils.Misc;
import org.junit.Before;
import org.junit.Test;

import javax.interceptor.InvocationContext;
import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Some sanity check that we are not throwing any unwanted exceptions.
 */
public class JAMonInterceptorTest {

    private String stringParameter = "stringParameter";
    private String longStringParameter;
    private String[] stringArray = new String[] { "array.0", null, "", "array.3"};
    private Collection<String> collectionParameter;
    private Object[] args;

    @Before
    public void setUp() {

        // reset JAMon statistics before each run
        MonitorFactory.reset();

        char[] array = new char[1024 * 1024 * 1];
        int pos = 0;
        while (pos < array.length) {
            array[pos] = 'A';
            pos++;
        }
        longStringParameter = new String(array);

        collectionParameter = new ArrayList<String>();
        collectionParameter.add("collection.0");
        collectionParameter.add(null);
        collectionParameter.add("");
        collectionParameter.add("collection.4");

        args = new Object[6];
        args[0] = stringParameter;
        args[1] = longStringParameter;
        args[2] = stringArray;
        args[3] = collectionParameter;
        args[4] = null;
        args[5] = new ExceptionGenerator();
    }

    @Test
    public void testIntercept() throws Exception {

        InvocationContext context = mock(InvocationContext.class);
        when(context.getParameters()).thenReturn(args);
        when(context.getTarget()).thenReturn(this);
        // Mockito can't mock final classes
        when(context.getMethod()).thenReturn(null);

        JAMonInterceptor interceptor = new JAMonInterceptor();
        interceptor.intercept(context);

        assertThat(MonitorFactory.getNumRows()).describedAs("Expecting one invocation label").isEqualTo(2);
    }

    @Test
    public void testOnException() throws Exception {

        InvocationContext context = mock(InvocationContext.class);
        when(context.getParameters()).thenReturn(args);
        when(context.getTarget()).thenReturn(this);
        // Mockito can't mock final classes
        when(context.getMethod()).thenReturn(null);

        JAMonInterceptor interceptor = new JAMonInterceptor();
        interceptor.onException(context, "JAMonInterceptor.???", new RuntimeException());

        assertThat(MonitorFactory.getNumRows()).describedAs("Expecting three exception labels").isEqualTo(3);
    }

    @Test
    public void testToString_WithANull() throws Exception {
        JAMonInterceptor interceptor = new JAMonInterceptor();
        assertThat(interceptor.toString(null)).isEqualTo(JAMonInterceptor.NULL_STR);
    }

    @Test
    public void testToString_WithALongString() throws Exception {
        JAMonInterceptor interceptor = new JAMonInterceptor();
        assertThat(interceptor.toString(longStringParameter).length())
                .describedAs("Should return <null>")
                .isEqualTo(JAMonInterceptor.DEFAULT_ARG_STRING_MAX_LENGTH + JAMonInterceptor.DEFAULT_MAX_STRING_ENDING.length());
        assertThat(interceptor.toString(longStringParameter))
                .describedAs("Should return truncate a long string")
                .endsWith(JAMonInterceptor.DEFAULT_MAX_STRING_ENDING);
    }

    @Test
    public void testToString_WithExceptionThrown() throws Exception {
        JAMonInterceptor interceptor = new JAMonInterceptor();
        assertThat(interceptor.toString(new ExceptionGenerator()))
                .describedAs("When an exception is thrown in the method it should succeed and return a value")
                .isEqualTo(JAMonInterceptor.UNKNOWN);
    }

    @Test
    public void testcreateExceptionDetails_WithNulls() throws Exception {
        JAMonInterceptor interceptor = new JAMonInterceptor();
        String details = interceptor.createExceptionDetails("mylabel", null, null);
        assertThat(details)
            .describedAs("Should contain label and only label")
            .isEqualTo("mylabel,\n");
    }

    @Test
    public void testcreateExceptionDetails_WithParameters() throws Exception {
        JAMonInterceptor interceptor = new JAMonInterceptor();
        String details = interceptor.createExceptionDetails("mylabel", new Object[]{new Integer(1962), "hello"}, null);
        assertThat(details).contains("mylabel");
        assertThat(details).contains("1962");
        assertThat(details).contains("hello");
    }

    @Test
    public void testcreateExceptionDetails_WithParametersAndException() throws Exception {
        JAMonInterceptor interceptor = new JAMonInterceptor();
        RuntimeException e = new RuntimeException("my fancy error message");
        String details = interceptor.createExceptionDetails("mylabel", new Object[]{new Integer(1962), "hello"}, e);
        assertThat(details).contains("mylabel");
        assertThat(details).contains("1962");
        assertThat(details).contains("hello");
        assertThat(details).contains("my fancy error message");
    }

    @Test
    public void testcreateExceptionDetails_WithException() throws Exception {
        JAMonInterceptor interceptor = new JAMonInterceptor();
        RuntimeException e = new RuntimeException("my fancy error message");
        String details = interceptor.createExceptionDetails("mylabel", null, e);
        assertThat(details).contains("mylabel");;
        assertThat(details).contains("my fancy error message");
    }



    private class ExceptionGenerator {
        @Override
        public String toString() {
            throw new RuntimeException("[toString] Always throwing an exception ...");
        }
    }
}
