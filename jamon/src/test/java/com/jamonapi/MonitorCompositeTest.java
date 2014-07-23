package com.jamonapi;

import com.jamonapi.utils.Misc;
import com.jamonapi.utils.SerializationUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import static org.fest.assertions.api.Assertions.assertThat;

//import org.apache.commons.lang3.SerializationUtils;


public class MonitorCompositeTest {
    private static final int BUFFER_SIZE = 50;
    private static final String EXCEPTION_METHOD = "mymethodexception";
    private static final String EXCEPTION_NAME = "My Exception";

    private Object[][] possibleListeners = JAMonListenerFactory.getData();

    @Before
    public void setUp() throws Exception {
        // Add every possible listener type to ensure that they are all serialized/deserialized
        Monitor mon = MonitorFactory.getMonitor("mymethodexception", "ms.");
        for (Object[] listenerType : possibleListeners) {
            mon.addListener("value", JAMonListenerFactory.get(listenerType[0].toString()));
        }
        // Add data that we can use to test serialization/deserialization.
        // This includes different types of monitors behind the scenes like TimeMon, and TimeNano.
        // A stacktraces is also put in the details of the key so it can be checked to see if it was properly
        // deserialized too.
        for (int i=0; i < BUFFER_SIZE; i++) {
            methodWithException(i);
            MonitorFactory.add("mylabel", "count", 1);
            MonitorFactory.add("mylabel" + i, "count", 1);
            Monitor mon0 = MonitorFactory.startNano("mynanotimer"+i);
            Monitor mon1 = MonitorFactory.startPrimary("mytimer"+i);
            mon1.stop();
            mon0.stop();
        }

    }

    @After
    public void tearDown() throws Exception {
        // Reset JAMon after each test method.  The Monitors are static and so would otherwise stick around
        MonitorFactory.reset();
    }

    /*
      @Test
    public void testSerialization() throws Throwable {
        String message = "serialize/deserialize me steve";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        com.jamonapi.utils.SerializationUtils.serialize(message, outputStream);
        String answer = com.jamonapi.utils.SerializationUtils.deserialize(new ByteArrayInputStream(outputStream.toByteArray()));
        assertThat(answer).isEqualTo(message);
    }

     */
    @Test
    public void testSerialization() throws Throwable {
        // serialize and deserialize monitors
        MonitorComposite original = MonitorFactory.getRootMonitor();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        SerializationUtils.serialize(original, outputStream);
        MonitorComposite deserialized = SerializationUtils.deserialize(new ByteArrayInputStream(outputStream.toByteArray()));

        // Do a deep comparison to see if the arrays are equal.
        // Note getData flattens the data to return any ranges also.  It doesn't however return
        // Listener buffer data.  A following check looks at that.
        assertThat(Arrays.deepEquals(original.getData(), deserialized.getData())).isTrue();
        assertThat(original.getReport()).isEqualTo(deserialized.getReport());

        // One of the monitors was given all current listener types.  Each of them should have 50 rows of data
        Monitor mon = getMonitorWithListeners(deserialized);
        CompositeListener compositeListener = (CompositeListener) mon.getListenerType("value").getListener();
        assertThat(compositeListener.getRowCount()).isEqualTo(possibleListeners.length);

        for (Object[] listenerType : compositeListener.getData()) {
            JAMonBufferListener bufferListener = (JAMonBufferListener) mon.getListenerType("value").getListener(listenerType[0].toString());
            // each should have 50 rows of data.
            assertThat(bufferListener.getRowCount()).isEqualTo(BUFFER_SIZE);
            Object[][] data = bufferListener.getDetailData().getData();
            // each row should have a stacktrace in it.  We look for "My ExceptionN"
            // and RuntimeException
            for (int i=0; i < data.length; i++) {
                String stackTrace = data[i][0].toString();
                assertThat(stackTrace).contains(EXCEPTION_NAME + i);
                assertThat(stackTrace).contains("RuntimeException");
            }
        }

    }


    @Test
    public void testGetMonitorWithUnits() {
       MonitorComposite composite = MonitorFactory.getRootMonitor();
       assertThat(composite.filterByUnits("AllMonitors").getNumRows()).isEqualTo(composite.getNumRows());
       assertThat(composite.filterByUnits("ms.").getNumRows()).isEqualTo(BUFFER_SIZE+1);
       assertThat(composite.filterByUnits("ns.").getNumRows()).isEqualTo(BUFFER_SIZE);
       assertThat(composite.filterByUnits("count").getNumRows()).isEqualTo(BUFFER_SIZE+1);
       assertThat(composite.filterByUnits("no_exist_units").getMonitors()).isNull();
    }

    @Test
    public void testMonitorExists() {
        MonitorComposite composite = MonitorFactory.getRootMonitor();
        assertThat(composite.exists(new MonKeyImp("mylabel", "count" ))).isTrue();
        assertThat(composite.exists(new MonKeyImp("I_DO_NOT", "EXIST" ))).isFalse();
    }

    @Test
    public void testGetMonitor() {
        MonitorComposite composite = MonitorFactory.getRootMonitor();
        assertThat(composite.getMonitor(new MonKeyImp("mylabel", "count" ))).isNotNull();
        assertThat(composite.getMonitor(new MonKeyImp("I_DO_NOT", "EXIST" ))).isNull();
    }

    @Test
    public void testGetInstanceName() {
        MonitorComposite composite = MonitorFactory.getRootMonitor();
        assertThat(composite.getInstanceName()).isEqualTo("local");
        assertThat(composite.setInstanceName("newname").getInstanceName()).isEqualTo("newname");
    }

    @Test
    public void testIsLocalInstance() {
        MonitorComposite composite = MonitorFactory.getRootMonitor();
        assertThat(composite.isLocalInstance()).isTrue();
        assertThat(composite.setInstanceName("newname").isLocalInstance()).isFalse();
    }


    private Monitor getMonitorWithListeners(MonitorComposite monitorComposite) {
        MonKey key = new MonKeyImp(EXCEPTION_METHOD, "ms.");
        for (Monitor mon : monitorComposite.getMonitors()) {
            if (mon.hasListeners() && mon.getMonKey().equals(key)) {
                return mon;
            }
        }
        return null;
    }


    private void methodWithException(int i) {
        Exception exception = new RuntimeException(EXCEPTION_NAME +i);
        MonKey key = new MonKeyImp(EXCEPTION_METHOD, Misc.getExceptionTrace(exception), "ms.");
        MonitorFactory.start(key).stop();
    }



}