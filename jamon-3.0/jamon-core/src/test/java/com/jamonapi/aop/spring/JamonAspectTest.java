package com.jamonapi.aop.spring;

import com.jamonapi.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.FileNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

public class JamonAspectTest {
    private static final String JAMON_EXCEPTION=MonitorFactory.EXCEPTIONS_LABEL;
    private static final String EXCEPTION = "Exception";
    private static final String VALUE_LISTENER="value";
    private static final String BUFFER="FIFOBuffer";
    private static final String METHOD = "void com.jamonapi.aop.spring.HelloSpringBean.setMyString(String)";

    private ApplicationContext context;

    @Before
    public void setUp() {
        MonitorFactory.reset();
        context = new ClassPathXmlApplicationContext("applicationContext.xml");
    }

    private void runApp() throws InterruptedException {
            HelloSpringBean hi = (HelloSpringBean) context.getBean("hiSpringBean");
            MonitorMe monitorMe = context.getBean("monitorMe", MonitorMe.class);

            for (int i=0;i<10;i++) {
                hi.getMyString();
                hi.setMyString("hello");
                monitorMe.anotherMethodForMe();
                monitorMe.helloWorld();
                try {
                    monitorMe.anotherMethod("argument.txt"); // throws exception
                } catch (Exception e) {

                }
            }
        }

    @Test(expected = FileNotFoundException.class)
    public void testRethrowsException() throws  FileNotFoundException{
        MonitorMe monitorMe = context.getBean("monitorMe", MonitorMe.class);
        monitorMe.anotherMethod("argument.txt"); // throws exception
    }

    /** methods should be monitored, but not all of them (based on defined pointcuts) */
    @Test
    public void testMethodsAreMonitored() throws Exception {
        runApp(); // runs 10 iterations
        
        // Verify expected AOP method monitors exist with correct hit counts
        assertThat(MonitorFactory.getMonitor("String com.jamonapi.aop.spring.HelloSpringBean.getMyString()", "ms.").getHits()).isEqualTo(10);
        assertThat(MonitorFactory.getMonitor("void com.jamonapi.aop.spring.HelloSpringBean.setMyString(String)", "ms.").getHits()).isEqualTo(10);
        assertThat(MonitorFactory.getMonitor("void com.jamonapi.aop.spring.MonitorMe.anotherMethod(String)", "ms.").getHits()).isEqualTo(10);
        assertThat(MonitorFactory.getMonitor("void com.jamonapi.aop.spring.MonitorMe.anotherMethodForMe()", "ms.").getHits()).isEqualTo(10);
        
        // Verify excluded methods are NOT monitored (pointcut only matches "anotherMethod*", not "helloWorld")
        assertThat(MonitorFactory.exists("void com.jamonapi.aop.spring.MonitorMe.helloWorld()", "ms.")).isFalse();
        
        // Verify exception monitors
        assertThat(MonitorFactory.getMonitor("java.io.FileNotFoundException", "Exception").getHits()).isEqualTo(10);
        assertThat(MonitorFactory.getMonitor(JAMON_EXCEPTION, EXCEPTION).getHits()).isEqualTo(10);
        
        // Verify specific properties
        assertThat(MonitorFactory.getMonitor(JAMON_EXCEPTION, EXCEPTION).hasListener(VALUE_LISTENER, BUFFER)).isTrue();
    }

    @Test
    public void testMethodsAreMonitored_BufferListenerDisabled() throws Exception {
        JamonAspect aspect = context.getBean("jamonAspect", JamonAspect.class);
        aspect.setExceptionBufferListener(false);

        runApp();

        // Verify same monitors exist (method call monitoring is independent of buffer listeners)
        assertThat(MonitorFactory.getMonitor("String com.jamonapi.aop.spring.HelloSpringBean.getMyString()", "ms.").getHits()).isEqualTo(10);
        assertThat(MonitorFactory.getMonitor("void com.jamonapi.aop.spring.MonitorMe.anotherMethod(String)", "ms.").getHits()).isEqualTo(10);
        assertThat(MonitorFactory.getMonitor("java.io.FileNotFoundException", "Exception").getHits()).isEqualTo(10);
        assertThat(MonitorFactory.getMonitor(JAMON_EXCEPTION, EXCEPTION).getHits()).isEqualTo(10);
        
        // Verify buffer listener is disabled
        assertThat(MonitorFactory.getMonitor(JAMON_EXCEPTION, EXCEPTION).hasListener(VALUE_LISTENER, BUFFER)).isFalse();
    }

    @Test
    public void testMethodsAreMonitored_ArgsWithExceptionDetails() throws Exception {
        JamonAspect aspect = context.getBean("jamonAspect", JamonAspect.class);
        aspect.setExceptionBufferListener(true);
        aspect.setUseArgsWithExceptionDetails(true);

        runApp();

        // Verify monitors exist with correct hit counts
        assertThat(MonitorFactory.getMonitor("void com.jamonapi.aop.spring.MonitorMe.anotherMethod(String)", "ms.").getHits()).isEqualTo(10);
        assertThat(MonitorFactory.getMonitor(JAMON_EXCEPTION, EXCEPTION).getHits()).isEqualTo(10);
        
        // Verify exception buffer contains argument details
        String bufferValue = getBufferValue(JAMON_EXCEPTION, EXCEPTION);
        assertThat(bufferValue).contains("arguments(1)");
        assertThat(bufferValue).contains("argument.txt");
    }

    @Test
    public void testMethodsAreMonitored_ArgsWithMethodDetails() throws Exception {
        JamonAspect aspect = context.getBean("jamonAspect", JamonAspect.class);
        aspect.setExceptionBufferListener(true);
        aspect.setUseArgsWithMethodDetails(true);
        MonitorFactory.getMonitor(METHOD,"ms.")
               .addListener("value", JAMonListenerFactory.get(BUFFER));

        runApp();

        // Verify monitors exist with correct hit counts
        assertThat(MonitorFactory.getMonitor("void com.jamonapi.aop.spring.HelloSpringBean.setMyString(String)", "ms.").getHits()).isEqualTo(10);
        assertThat(MonitorFactory.getMonitor(JAMON_EXCEPTION, EXCEPTION).getHits()).isEqualTo(10);
        
        // Verify argument tracking behavior: exceptions don't contain method args, methods do
        String exceptionBufferValue = getBufferValue(JAMON_EXCEPTION, EXCEPTION);
        assertThat(exceptionBufferValue).doesNotContain("arguments(1)");
        assertThat(exceptionBufferValue).doesNotContain("hello");

        String methodBufferValue = getBufferValue(METHOD, "ms.");
        assertThat(methodBufferValue).contains("arguments(1)");
        assertThat(methodBufferValue).contains("hello");
    }

    // get first value in buffer listener if it exists;
    private String getBufferValue(String label, String units) {
        Monitor mon = MonitorFactory.getMonitor(label, units);
        if (!mon.hasListeners()) {
            return "";
        }

        JAMonBufferListener bufferListener = (JAMonBufferListener) mon
                .getListenerType(VALUE_LISTENER).getListener(BUFFER);
        JAMonDetailValue listenerData = (JAMonDetailValue) bufferListener.getBufferList().getCollection().get(0);
        String firstValue="";
        for (Object obj : listenerData.toArray()) {
            firstValue+=obj+"\n";
        }
        return firstValue;
    }

    /** testing with a more simplified spring application context xml that is easier for people to start with. */
    @Test
    public void testMethodsAreMonitored_WithMinmalApplicatonContext() throws Exception {
        context = new ClassPathXmlApplicationContext("minimalApplicationContext.xml");
        MonitorMe monitorMe = context.getBean("monitorMe", MonitorMe.class);

        // Call MonitorMe methods (should be monitored)
        for (int i=0;i<10;i++) {
           monitorMe.anotherMethodForMe();
           monitorMe.helloWorld();
           try {
              monitorMe.anotherMethod("argument.txt"); // throws exception
            } catch (Exception e) {
            }
        }

        // Verify expected AOP method monitors (minimal context monitors ALL MonitorMe methods)
        assertThat(MonitorFactory.getMonitor("void com.jamonapi.aop.spring.MonitorMe.anotherMethod(String)", "ms.").getHits()).isEqualTo(10);
        assertThat(MonitorFactory.getMonitor("void com.jamonapi.aop.spring.MonitorMe.anotherMethodForMe()", "ms.").getHits()).isEqualTo(10);
        assertThat(MonitorFactory.getMonitor("void com.jamonapi.aop.spring.MonitorMe.helloWorld()", "ms.").getHits()).isEqualTo(10);
        
        // Verify exception monitors
        assertThat(MonitorFactory.getMonitor("java.io.FileNotFoundException", "Exception").getHits()).isEqualTo(10);
        assertThat(MonitorFactory.getMonitor(JAMON_EXCEPTION, EXCEPTION).getHits()).isEqualTo(10);
        
        // Verify buffer listener is enabled by default
        assertThat(MonitorFactory.getMonitor(JAMON_EXCEPTION, EXCEPTION).hasListener(VALUE_LISTENER, BUFFER)).isTrue();
    }

}
