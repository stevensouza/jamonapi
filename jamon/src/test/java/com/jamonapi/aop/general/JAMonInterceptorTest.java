package com.jamonapi.aop.general;

import com.jamonapi.MonitorFactory;
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
        args[4] = new ExceptionGenerator();
    }

    @Test
    public void testIntercept() throws Exception {

        InvocationContext context = mock(InvocationContext.class);
        when(context.getParameters()).thenReturn(args);
        when(context.getTarget()).thenReturn(this);
        // Mockito can't mock final classes
        when(context.getMethod()).thenReturn(null);

        JAMonInterceptor interceptor = new TestJAMonInterceptor();
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

        JAMonInterceptor interceptor = new TestJAMonInterceptor();
        interceptor.onException(context, "JAMonInterceptor.???", new RuntimeException());

        assertThat( MonitorFactory.getNumRows()).describedAs("Expecting two exception labels").isEqualTo(2);
    }

    class TestJAMonInterceptor extends JAMonInterceptor {

        @Override
        public Exception onException(InvocationContext ctx, String label, Exception exception) throws Exception {
            return super.onException(ctx, label, exception);
        }

        @Override
        public Object intercept(InvocationContext ctx) throws Exception {
            return super.intercept(ctx);
        }
    }

    class ExceptionGenerator {
        @Override
        public String toString() {
            throw new RuntimeException("[toString] Always throwing an exception ...");
        }
    }
}
