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
        runApp();

        String report = MonitorFactory.getReport();

        assertThat(MonitorFactory.getNumRows()).isEqualTo(6);
        assertThat(report).contains("HelloSpringBean.getMyString()");
        assertThat(report).contains("HelloSpringBean.setMyString(String)");
        assertThat(report).contains("MonitorMe.anotherMethod(String)");
        assertThat(report).contains("MonitorMe.anotherMethodForMe()");
        assertThat(report).contains("java.io.FileNotFoundException");
        assertThat(report).contains(JAMON_EXCEPTION);
        assertThat(MonitorFactory.getMonitor(JAMON_EXCEPTION, EXCEPTION).hasListener(VALUE_LISTENER, BUFFER)).isTrue();
    }

    @Test
    public void testMethodsAreMonitored_BufferListenerDisabled() throws Exception {
        JamonAspect aspect = context.getBean("jamonAspect", JamonAspect.class);
        aspect.setExceptionBufferListener(false);

        runApp();

        assertThat(MonitorFactory.getNumRows()).isEqualTo(6);
        assertThat(MonitorFactory.getMonitor(JAMON_EXCEPTION, EXCEPTION).hasListener(VALUE_LISTENER, BUFFER)).isFalse();
    }

    @Test
    public void testMethodsAreMonitored_ArgsWithExceptionDetails() throws Exception {
        JamonAspect aspect = context.getBean("jamonAspect", JamonAspect.class);
        aspect.setExceptionBufferListener(true);
        aspect.setUseArgsWithExceptionDetails(true);

        runApp();

        assertThat(MonitorFactory.getNumRows()).isEqualTo(6);
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

        assertThat(MonitorFactory.getNumRows()).isEqualTo(6);
        String bufferValue = getBufferValue(JAMON_EXCEPTION, EXCEPTION);
        assertThat(bufferValue).doesNotContain("arguments(1)");
        assertThat(bufferValue).doesNotContain("hello");

        bufferValue = getBufferValue(METHOD, "ms.");
        assertThat(bufferValue).contains("arguments(1)");
        assertThat(bufferValue).contains("hello");
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

        for (int i=0;i<10;i++) {
           monitorMe.anotherMethodForMe();
           monitorMe.helloWorld();
           try {
              monitorMe.anotherMethod("argument.txt"); // throws exception
            } catch (Exception e) {
            }
        }

        String report = MonitorFactory.getReport();

        assertThat(MonitorFactory.getNumRows()).isEqualTo(5);
        assertThat(report).contains("MonitorMe.anotherMethod(String)");
        assertThat(report).contains("MonitorMe.anotherMethodForMe()");
        assertThat(report).contains("MonitorMe.helloWorld()");
        assertThat(report).contains("java.io.FileNotFoundException");
        assertThat(report).contains(JAMON_EXCEPTION);
        assertThat(MonitorFactory.getMonitor(JAMON_EXCEPTION, EXCEPTION).hasListener(VALUE_LISTENER, BUFFER)).isTrue();
    }

}
