package com.jamonapi.aop.spring;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JamonAopKeyHelperTest {
    private static final String SIGNATURE = "void com.stevesouza.spring.MonitorMeClass.anotherMethod(String)";
    private static final String EXCEPTION_LABEL = FileNotFoundException.class.getName();

    private JamonAopKeyHelper helper = new JamonAopKeyHelper(true, true);

    private ProceedingJoinPoint pjp;
    private Signature signature;

    @Test
    @Before
    public void setUp() {
        pjp = mock(ProceedingJoinPoint.class);
        signature  = mock(Signature.class);
        when(pjp.getSignature()).thenReturn(signature);
        when(signature.toString()).thenReturn(SIGNATURE);
    }

    @Test
    public void testGetDetails_NoArgs() throws Exception {
        assertThat(helper.getDetails(pjp)).isEqualTo(SIGNATURE);
    }
    @Test
    public void testGetDetails_OneArg_Ignore() throws Exception {
        helper.setUseArgsWithMethodDetails(false);
        when(pjp.getArgs()).thenReturn(new Object[]{"firstArg"});
        assertThat(helper.getDetails(pjp)).isEqualTo(SIGNATURE);
    }

    @Test
    public void testGetDetails_OneArg() throws Exception {
        String answer = SIGNATURE+"\n"+
                        "arguments(1):\n"+
                        "firstArg";
        when(pjp.getArgs()).thenReturn(new Object[]{"firstArg"});
        assertThat(helper.getDetails(pjp)).isEqualTo(answer);
    }

    @Test
    public void testGetDetails_TwoArg() throws Exception {
        String answer = SIGNATURE+"\n"+
                "arguments(2):\n"+
                "firstArg,\n"+
                "secondArg";
        when(pjp.getArgs()).thenReturn(new Object[]{"firstArg", "secondArg"});
        assertThat(helper.getDetails(pjp)).isEqualTo(answer);
    }

    @Test
    public void testGetDetails_ThreeArg() throws Exception {
        String answer = SIGNATURE+"\n"+
                "arguments(3):\n"+
                "firstArg,\n"+
                "secondArg,\n"+
                "thirdArg";
        when(pjp.getArgs()).thenReturn(new Object[]{"firstArg", "secondArg", "thirdArg"});
        assertThat(helper.getDetails(pjp)).isEqualTo(answer);
    }

    @Test
        public void testGetDetails_WithException() throws Exception {
        assertThat(helper.getDetails(pjp, new FileNotFoundException())).contains("stackTrace=java.io.FileNotFoundException");
    }

    @Test
    public void testGetDetails_WithException_ThreeArg() throws Exception {
        String argsAnswer = "arguments(3):\n"+
        "firstArg,\n"+
        "secondArg,\n"+
        "thirdArg";
        when(pjp.getArgs()).thenReturn(new Object[]{"firstArg", "secondArg", "thirdArg"});
        assertThat(helper.getDetails(pjp, new FileNotFoundException())).contains("stackTrace=java.io.FileNotFoundException");
        assertThat(helper.getDetails(pjp, new FileNotFoundException())).contains(argsAnswer);
    }

    @Test
    public void testGetDetails_WithException_ThreeArg_Ignore() throws Exception {
        String argsAnswer = "arguments(3):\n"+
                "firstArg,\n"+
                "secondArg,\n"+
                "thirdArg";
        helper.setUseArgsWithExceptionDetails(false);
        when(pjp.getArgs()).thenReturn(new Object[]{"firstArg", "secondArg", "thirdArg"});
        assertThat(helper.getDetails(pjp, new FileNotFoundException())).contains("stackTrace=java.io.FileNotFoundException");
        assertThat(helper.getDetails(pjp, new FileNotFoundException())).doesNotContain(argsAnswer);
    }
}
