package com.jamonapi;

import com.jamonapi.utils.Misc;
import com.jamonapi.utils.SerializationUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

//import org.apache.commons.lang3.SerializationUtils;


public class MonitorCompositeTest {
    private static final int BUFFER_SIZE = 50;
    private static final String EXCEPTION_METHOD = "mymethodexception";
    private static final String EXCEPTION_NAME = "My Exception";
    private static final int LABEL_INDEX = 1;

    @Before
    public void setUp() throws Exception {
        MonitorFactory.reset();
        JAMonListenerFactory.reset();
        Object[][] possibleListeners = JAMonListenerFactory.getData();
        // Add every possible listener type to ensure that they are all serialized/deserialized
        Monitor mon = MonitorFactory.getMonitor(EXCEPTION_METHOD, "ms.");
        for (Object[] listenerType : possibleListeners) {
            JAMonListener listener = JAMonListenerFactory.get(listenerType[0].toString());
            mon.addListener("value", listener);
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
        JAMonListenerFactory.reset();
    }

    @Test
    public void testSerialization() throws Throwable {
        // serialize and deserialize monitors
        MonitorComposite original = MonitorFactory.getRootMonitor();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        SerializationUtils.serialize(original, outputStream);
        MonitorComposite deserialized = SerializationUtils.deserialize(new ByteArrayInputStream(outputStream.toByteArray()));
        deepComparisonAssertions(original, deserialized);
    }

    @Test
    public void testDeepCopy() throws Throwable {
        // do a deep copy of the object.
        MonitorComposite original = MonitorFactory.getRootMonitor();
        MonitorComposite deserialized = original.copy();

        deepComparisonAssertions(original, deserialized);
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
    public void testGetDistinctUnits() {
        MonitorFactory.start("timelabel1").stop();
        MonitorFactory.start("timelabel2").stop();
        MonitorFactory.start("timelabel3").stop();

        Collection<String> units = MonitorFactory.getRootMonitor().getDistinctUnits();
        assertThat(units).containsExactly("Exception", "count", "ms.", "ns.");
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

    @Test
    public void shouldReturnInstanceNameInData() {
        MonitorComposite composite = MonitorFactory.getRootMonitor();
        int NUM_COLUMNS = 17;
        assertThat(composite.getBasicHeader().length).isEqualTo(NUM_COLUMNS);
        Object data = composite.getBasicData();
        assertThat(composite.getBasicData()[0].length).isEqualTo(NUM_COLUMNS);
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
        MonitorFactory.add(key, i).start().stop();
    }

    private void deepComparisonAssertions(MonitorComposite original, MonitorComposite copy) {
        // Do a deep comparison to see if the arrays are equal.
        // Note getData flattens the data to return any ranges also.  It doesn't however return
        // Listener buffer data.  A following check looks at that.
        assertThat(Arrays.deepEquals(original.getData(), copy.getData())).isTrue();
        assertThat(original.getReport()).isEqualTo(copy.getReport());

        // One of the monitors was given all current listener types.  Each of them should have 50 rows of data
        Monitor mon = getMonitorWithListeners(copy);
        CompositeListener compositeListener = (CompositeListener) mon.getListenerType("value").getListener();
        // due to shared buffers being created the number of listeners and the composite uses them (_Shared...)
        // but does not contain the actual factory instance (Shared...) the factory will have more elements (1
        // for each shard listener) than the composite.
        assertThat(compositeListener.getRowCount()).isLessThanOrEqualTo(JAMonListenerFactory.getData().length);

        for (Object[] listenerType : compositeListener.getData()) {
            JAMonBufferListener bufferListener = (JAMonBufferListener) mon.getListenerType("value").getListener(listenerType[0].toString());
            // each should have 50 rows of data.
            assertThat(bufferListener.getRowCount()).isEqualTo(BUFFER_SIZE);
            Object[][] data = bufferListener.getDetailData().getData();
            // each row should have a stacktrace in it.  We look for "My ExceptionN"
            // and RuntimeException
            for (int i=0; i < data.length; i++) {
                String stackTrace = data[i][LABEL_INDEX].toString();
                assertThat(stackTrace).contains(EXCEPTION_NAME + i);
                assertThat(stackTrace).contains("RuntimeException");
            }
        }
    }


}
