package com.jamonapi.aop.spring;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JamonAopKeyHelperSimpleTest {

    private static final String SIGNATURE = "void com.stevesouza.spring.MonitorMeClass.anotherMethod(String)";
    private static final String EXCEPTION_LABEL = FileNotFoundException.class.getName();

    private JamonAopKeyHelperSimple helper = new JamonAopKeyHelperSimple();

    private ProceedingJoinPoint pjp;
    private Signature signature;

    @Before
    public void setUp() {
        pjp = mock(ProceedingJoinPoint.class);
        signature  = mock(Signature.class);
        when(pjp.getSignature()).thenReturn(signature);
        when(signature.toString()).thenReturn(SIGNATURE);
    }

    @Test
    public void testGetLabel() throws Exception {
       assertThat(helper.getLabel(pjp)).isEqualTo(SIGNATURE);
    }

    @Test
    public void testGetExceptionLabel() throws Exception {
        assertThat(helper.getExceptionLabel(new FileNotFoundException())).isEqualTo(EXCEPTION_LABEL);
    }

    @Test
    public void testGetDetails() throws Exception {
        assertThat(helper.getDetails(pjp)).isEqualTo(SIGNATURE);
    }

    @Test
    public void testGetDetails_WithException() throws Exception {
        assertThat(helper.getDetails(pjp, new FileNotFoundException())).startsWith("stackTrace=java.io.FileNotFoundException");
    }
}