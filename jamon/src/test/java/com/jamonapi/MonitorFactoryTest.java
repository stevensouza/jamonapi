package com.jamonapi;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

public class MonitorFactoryTest {

    private static final int SIZE = 50;
    private static final int FIRST_MIN = 0;
    private static final int FIRST_MAX = 40;
    private static final int VALUE_INDEX = 2;
    private static final int MAX_ACTIVE_INDEX = 3;

    @Before
    public void setUp() throws Exception {
        MonitorFactory.setEnabled(true);
        MonitorFactory.reset();
    }

    @After
    public void tearDown() throws Exception {
        // Reset JAMon after each test method.  The Monitors are static and so would otherwise stick around
        MonitorFactory.setEnabled(true);
        MonitorFactory.reset();
    }


    /** Test that calls {@code MonitorFactory#start(String)} (i.e. tests the stop watch capability of JAMon) */
    @Test
    public void testTimeMonitor() throws InterruptedException {
        final String label="sql";
        double total=0;
        double min=Double.MAX_VALUE;
        double max=Double.MIN_VALUE;
        for (int i = 1; i <= 10; i++) {
            Monitor mon=MonitorFactory.start(label);
            Thread.sleep(10);
            mon.stop();

            // set values to compare with jamon's results
            double loopValue = mon.getLastValue();
            if (loopValue<min) {
                min=loopValue;
            }

            if (loopValue>max) {
                max=loopValue;
            }

            total+=loopValue;

            assertThat(mon.getLabel()).isEqualTo(label);
            assertThat(mon.getUnits()).isEqualTo("ms.");
            assertThat(mon.getHits()).isEqualTo(i);
            assertThat(mon.getTotal()).isEqualTo(total);
            assertThat(mon.getAvg()).isEqualTo(total/i);
            assertThat(mon.getMin()).isEqualTo(min);
            assertThat(mon.getMax()).isEqualTo(max);
            assertThat(mon.getLastValue()).isEqualTo(loopValue);
            assertThat(mon.getActive()).isEqualTo(0);
            assertThat(mon.getMaxActive()).isEqualTo(1);
            assertThat(mon.getStdDev()).isGreaterThanOrEqualTo(0);

            // Some values differ for the first run and subsequent runs.
            if (i>1) {
                assertThat(mon.getFirstAccess().before(new Date())).isTrue();
                assertThat(mon.getLastAccess().after(mon.getFirstAccess())).isTrue();
            }
        }
    }

    /** Test that calls {@code MonitorFactory#add(String, String, double)} using positive numbers */
    @Test
    public void testPositiveMonitor() throws InterruptedException {
        final String label="NIC.bytes.sent";
        final String units="bytes";
        double total=0;
        for (int i = 1; i <= 10; i++) {
            double loopValue = i * 1000;
            MonitorFactory.add(label, units, loopValue);
            total+=loopValue;
            Monitor mon=MonitorFactory.getMonitor(label, units);

            assertThat(mon.getLabel()).isEqualTo(label);
            assertThat(mon.getUnits()).isEqualTo(units);
            assertThat(mon.getHits()).isEqualTo(i);
            assertThat(mon.getTotal()).isEqualTo(total);
            assertThat(mon.getAvg()).isEqualTo(total/i);
            assertThat(mon.getMin()).isEqualTo(1000);
            assertThat(mon.getMax()).isEqualTo(loopValue);
            assertThat(mon.getLastValue()).isEqualTo(loopValue);
            assertThat(mon.getActive()).isEqualTo(0);
            assertThat(mon.getMaxActive()).isEqualTo(0);

            // Some values differ for the first run and subsequent runs.
            if (i>1) {
                assertThat(mon.getStdDev()).isGreaterThan(0);
                assertThat(mon.getFirstAccess().before(new Date())).isTrue();
                assertThat(mon.getLastAccess().after(mon.getFirstAccess())).isTrue();
            } else {
                assertThat(mon.getStdDev()).isEqualTo(0);
                Thread.sleep(10);
            }
        }
    }


    /** Test that calls {@code MonitorFactory#add(String, String, double)} using negative numbers */
    @Test
    public void testNegativeMonitor() throws InterruptedException {
        final String label="negativetest";
        final String units="neg";
        double total=0;
        for (int i = 1; i <= 10; i++) {
            double loopValue = i * -1000;
            MonitorFactory.add(label, units, loopValue);
            total+=loopValue;
            Monitor mon=MonitorFactory.getMonitor(label, units);

            assertThat(mon.getLabel()).isEqualTo(label);
            assertThat(mon.getUnits()).isEqualTo(units);
            assertThat(mon.getHits()).isEqualTo(i);
            assertThat(mon.getTotal()).isEqualTo(total);
            assertThat(mon.getAvg()).isEqualTo(total/i);
            assertThat(mon.getMin()).isEqualTo(loopValue);
            assertThat(mon.getMax()).isEqualTo(-1000);
            assertThat(mon.getLastValue()).isEqualTo(loopValue);
            assertThat(mon.getActive()).isEqualTo(0);
            assertThat(mon.getMaxActive()).isEqualTo(0);

            // Some values differ for the first run and subsequent runs.
            if (i>1) {
                assertThat(mon.getStdDev()).isGreaterThan(0);
                assertThat(mon.getFirstAccess().before(new Date())).isTrue();
                assertThat(mon.getLastAccess().after(mon.getFirstAccess())).isTrue();
            } else {
                assertThat(mon.getStdDev()).isEqualTo(0);
                Thread.sleep(10);
            }
        }
    }


    @Test
    public void testEnabledDisabled() {
        assertThat(MonitorFactory.getFactory()).isNotNull();
        assertThat(MonitorFactory.isEnabled()).isTrue();

        for (int i=0;i<100;i++) {
            MonitorFactory.add("key"+i, "count", 1);
            MonitorFactory.start("key").stop();
        }
        assertThat(MonitorFactory.getRootMonitor().getNumRows()).isEqualTo(102);

        MonitorFactory.disable();
        assertThat(MonitorFactory.isEnabled()).isFalse();
        assertThat(MonitorFactory.getRootMonitor().getNumRows()).isEqualTo(0);

        MonitorFactory.enable();
        assertThat(MonitorFactory.getRootMonitor().getNumRows()).isEqualTo(102);
        assertThat(MonitorFactory.isEnabled()).isTrue();
    }

    @Test
    public void testStart() throws InterruptedException {
        Monitor mon=MonitorFactory.start();
        Thread.sleep(10);
        mon.stop();
        assertThat(mon.getLastValue()).isGreaterThanOrEqualTo(5);
    }

    @Test
    public void testTimeMonNano() throws InterruptedException {
        Monitor mon=MonitorFactory.startNano("mynanotimer");
        long nanoTime = System.nanoTime();
        Thread.sleep(10);
        mon.stop();
        // test for bug: https://sourceforge.net/p/jamonapi/bugs/16/
        assertThat(mon.getHits()).isEqualTo(1);
        assertThat(mon.getLastValue()).isEqualTo(10*TimeMonNano.NANOSECS_PER_MILLISEC, offset(50.0*TimeMonNano.NANOSECS_PER_MILLISEC));
        assertThat((Long)mon.getValue("starttime")).isLessThan(Long.valueOf(nanoTime));
        mon.reset();
        assertThat((Long)mon.getValue("starttime")).isEqualTo(0);
    }

    @Test
    public void testActive() {
        Monitor mon=MonitorFactory.start("himom");
        assertThat(mon.getActive()).isEqualTo(1);
        mon.stop();
        assertThat(mon.getActive()).isEqualTo(0);
        mon=MonitorFactory.start("himom");
        assertThat(mon.getActive()).isEqualTo(1);
        mon=MonitorFactory.start("himom");
        assertThat(mon.getActive()).isEqualTo(2);
        MonitorFactory.start("hidad");
        assertThat(mon.getActive()).isEqualTo(2);
        assertThat(MonitorFactory.getTimeMonitor("hidad").getActive()).isEqualTo(1);
    }

    @Test
    public void testMonitorExists() {
        MonitorFactory.start("test");
        assertThat(MonitorFactory.exists("test", "ms.")).isTrue();
        assertThat(MonitorFactory.exists("idonotexist", "ms.")).isFalse();
    }


    @Test
    public void testGetComposite() {
        MonitorFactory.start("test1").stop();
        MonitorFactory.start("test2").stop();
        assertThat(MonitorFactory.getComposite("ms.").getMonitors()).hasSize(2);
        assertThat(MonitorFactory.getComposite("ms.").getMonitors()[0].getHits()).isEqualTo(1);
    }

    @Test
    public void testGetNumRows() {
        MonitorFactory.start("test1").stop();
        MonitorFactory.start("test2").stop();
        MonitorFactory.add("test1", "count", 100);
        assertThat(MonitorFactory.getNumRows()).isEqualTo(4);
    }

    @Test
    public void testGetRangeHeader() {
        assertThat(MonitorFactory.getRangeHeader()).isNotNull();
        assertThat(MonitorFactory.getRangeHeader()).isNotEmpty();
    }

    @Test
    public void testGetRangeNames() {
        assertThat(MonitorFactory.getRangeNames().length).isEqualTo(3);
    }

    @Test
    public void testGetRootMonitor() {
        MonitorFactory.start("test");
        assertThat(MonitorFactory.getRootMonitor().getNumRows()).isEqualTo(2);
    }


    @Test
    public void testRemoveMonitor() {
        MonitorFactory.start("test");
        assertThat(MonitorFactory.exists("test", "ms.")).isTrue();

        MonitorFactory.remove("test", "ms.");
        assertThat(MonitorFactory.exists("test", "ms.")).isFalse();
    }



    @Test
    public void testReset() {
        MonitorFactory.start("test1");
        MonitorFactory.start("test2");
        assertThat(MonitorFactory.getNumRows()).isEqualTo(3);

        MonitorFactory.reset();
        assertThat(MonitorFactory.getNumRows()).isEqualTo(1);
    }

    @Test
    public void testGetBasicHeader() {
        MonitorFactory.start("test");
        assertThat(MonitorFactory.getRootMonitor().getBasicHeader().length).isEqualTo(17);
    }


    @Test
    public void testMaxNumMonitors() {
        assertThat(MonitorFactory.getMaxNumMonitors()).isEqualTo(0);
        MonitorFactory.setMaxNumMonitors(2);
        assertThat(MonitorFactory.getMaxNumMonitors()).isEqualTo(2);
        for (int i=0;i<5;i++) {
            MonitorFactory.start("monnumber "+i).stop();
        }

        assertThat(MonitorFactory.getNumRows()).isEqualTo(2);
    }


    @Test
    public void testGetVersion() {
        assertThat(MonitorFactory.getVersion().compareTo("2.74")>0).isTrue();
    }


    @Test
    public void testKeySizeTracking() {
        assertThat(MonitorFactory.isTotalKeySizeTrackingEnabled()).isFalse();
        MonitorFactory.enableTotalKeySizeTracking();
        assertThat(MonitorFactory.isTotalKeySizeTrackingEnabled()).isTrue();
        assertThat(MonitorFactory.getTotalKeySize()).isEqualTo(0);
        MonitorFactory.reset();
        assertThat(MonitorFactory.isTotalKeySizeTrackingEnabled()).isTrue();

        MonitorFactory.start("himom").stop();
        assertThat(MonitorFactory.getTotalKeySize()).isEqualTo(5);

        MonitorFactory.start("himom").stop();
        assertThat(MonitorFactory.getTotalKeySize()).isEqualTo(5);
        MonitorFactory.start("hidad").stop();
        assertThat(MonitorFactory.getTotalKeySize()).isEqualTo(10);
        MonitorFactory.remove("himom", "ms.");
        assertThat(MonitorFactory.getTotalKeySize()).isEqualTo(5);

        MonitorFactory.disableTotalKeySizeTracking();
        assertThat(MonitorFactory.isTotalKeySizeTrackingEnabled()).isFalse();
        assertThat(MonitorFactory.getTotalKeySize()).isEqualTo(0);
        MonitorFactory.reset();
        MonitorFactory.start("himom").stop();
        assertThat(MonitorFactory.getTotalKeySize()).isEqualTo(0);
    }

    @Test
    public void testGetData() {
        MonitorFactory.start("purchasesTimeTestRange").stop();
        for (int i = 0; i < 100; i++)
            MonitorFactory.add("purchasesRange", "dollars", 1000.0);

        // test headers
        String[] header = MonitorFactory.getComposite("ms.").getHeader();
        assertThat(header.length).isGreaterThan(0);
        header = MonitorFactory.getComposite("ms.").getBasicHeader();
        assertThat(header.length).isGreaterThan(0);
        header = MonitorFactory.getComposite("ms.").getDisplayHeader();
        assertThat(header.length).isGreaterThan(0);

        // test ms. data
        Object[][] data = MonitorFactory.getComposite("ms.").getData();
        assertThat(data.length).isEqualTo(1);
        data = MonitorFactory.getComposite("ms.").getBasicData();
        assertThat(data.length).isEqualTo(1);
        data = MonitorFactory.getComposite("ms.").getDisplayData();
        assertThat(data.length).isEqualTo(1);
        assertThat(MonitorFactory.getComposite("ms.").getMonitors().length).isEqualTo(1);

        // test all data
        data = MonitorFactory.getRootMonitor().getData();
        assertThat(data.length).isEqualTo(3);
        data = MonitorFactory.getData();
        assertThat(data.length).isEqualTo(3);
        assertThat(MonitorFactory.getRootMonitor().getMonitors().length).isEqualTo(3);
    }

    @Test
    public void testGetReport() {
        MonitorFactory.start("himom").stop();
        assertThat(MonitorFactory.getReport().contains("<tr>")).isTrue();
        assertThat(MonitorFactory.getReport("ms.").contains("<tr>")).isTrue();

    }

    @Test
    public void testIterator() {
        MonitorFactory.start("himom").stop();
        MonitorFactory.start("hidad").stop();
        Iterator iter=MonitorFactory.iterator();
        int numCount=0;
        while (iter.hasNext()) {
            numCount++;
            iter.next();
        }

        assertThat(numCount).isEqualTo(3);
    }

    @Test
    public void testEnableActivityTracking() {
        assertThat(MonitorFactory.isGlobalActiveEnabled()).isTrue();
        assertThat(MonitorFactory.isActivityTrackingEnabled()).isFalse();

        MonitorFactory.enableGlobalActive(false);
        MonitorFactory.enableActivityTracking(false);
        assertThat(MonitorFactory.isGlobalActiveEnabled()).isFalse();
        assertThat(MonitorFactory.isActivityTrackingEnabled()).isFalse();

        MonitorFactory.enableGlobalActive(true);
        MonitorFactory.enableActivityTracking(true);
        assertThat(MonitorFactory.isGlobalActiveEnabled()).isTrue();
        assertThat(MonitorFactory.isActivityTrackingEnabled()).isTrue();
    }


    @Test
    public void testMaxSqlSize() {
        assertThat(MonitorFactory.getMaxSqlSize()).isEqualTo(0);
        MonitorFactory.setMaxSqlSize(100);
        assertThat(MonitorFactory.getMaxSqlSize()).isEqualTo(100);
    }


    @Test
    public void testRangeDefaults() {
        assertThat(MonitorFactory.getRangeNames().length).isEqualTo(3);
        MonitorFactory.setRangeDefault("dollars", MonitorFactoryTest.getTestHolder());
        MonitorFactory.start("purchasesTimeTestRange").stop();
        for (int i = 1; i <= 100; i++) {
            MonitorFactory.add("purchasesRange", "dollars", 1000.0);
        }

        assertThat(MonitorFactory.getRangeNames().length).isEqualTo(4);
        assertThat(MonitorFactory.getMonitor("purchasesRange", "dollars").getHits()).isEqualTo(100);
        assertThat(MonitorFactory.getMonitor("purchasesRange", "dollars").getAvg()).isEqualTo(1000);
        assertThat(MonitorFactory.getMonitor("purchasesRange", "dollars").getTotal()).isEqualTo(100*1000);

        MonitorFactory.setRangeDefault("bytes", MonitorFactoryTest.getTestHolder());
        MonitorFactory.setRangeDefault("cents", MonitorFactoryTest.getTestHolder());
        MonitorFactory.setRangeDefault("minutes", MonitorFactoryTest.getTestHolder());
        MonitorFactory.setRangeDefault("MB", MonitorFactoryTest.getTestHolder());
        MonitorFactory.setRangeDefault("KB", MonitorFactoryTest.getTestHolder());
        MonitorFactory.setRangeDefault("points", MonitorFactoryTest.getTestHolder());
        assertThat(MonitorFactory.getRangeNames().length).isEqualTo(10);
    }

    @Test
    public void testBufferListener() throws Exception {
        Monitor mon=MonitorFactory.start().stop();
        mon.addListener("value",new JAMonBufferListener("first"));
        assertThat(mon.getListenerType("value").getListener("first")).isNotNull();
        assertThat(mon.getListenerType("value").hasListener("first")).isTrue();
        assertThat(mon.getListenerType("value").hasListeners()).isTrue();


        mon.addListener("value",new JAMonBufferListener("second"));
        assertThat(mon.getListenerType("value").getListener("second")).isNotNull();
        assertThat(mon.getListenerType("value").hasListener("second")).isTrue();
        assertThat(mon.getListenerType("value").hasListeners()).isTrue();

        mon.removeListener("value", "first");
        assertThat(mon.getListenerType("value").getListener("first")).isNull();
        assertThat(mon.getListenerType("value").hasListener("first")).isFalse();
        assertThat(mon.getListenerType("value").hasListeners()).isTrue();

        mon.removeListener("value", "second");
        assertThat(mon.getListenerType("value").getListener("second")).isNull();
        assertThat(mon.getListenerType("value").hasListener("second")).isFalse();
        assertThat(mon.getListenerType("value").hasListeners()).isFalse();

        mon.add(FIRST_MIN);
        mon.add(FIRST_MAX);

        mon.addListener("value",new JAMonBufferListener("1"));
        mon.addListener("max",new JAMonBufferListener("2"));
        mon.addListener("min",new JAMonBufferListener("3"));
        mon.addListener("maxactive",new JAMonBufferListener("4"));

        for (int i=0;i<SIZE;i++) {
            mon.add(i);
        }

        mon.start().start().start().stop().stop().stop();
        assertThat(mon.getMaxActive()).isEqualTo(3);

        for (int i=0;i<SIZE;i++) {
            mon.add(i);
        }

        assertValueListenerIsNonDecreasing(mon);
        assertMaxListenerGreaterThanFirstMax(mon);
        assertMinListenerEqualToZero(mon);
        assertMaxActiveListenerValues(mon);
    }

    @Test
    public void testOnlyOneListenerOfTheSameName() throws Exception {
        Monitor mon = MonitorFactory.start().stop();
        mon.addListener("value", new JAMonBufferListener("first"));
        mon.addListener("value", new JAMonBufferListener("first"));

        assertThat(mon.getListenerType("value").getData().length).isEqualTo(1);

        mon.addListener("value", new JAMonBufferListener("second"));
        assertThat(mon.getListenerType("value").getData().length).isEqualTo(2);
    }
        @Test
    public void testTrackExceptionWithoutMon() {
        Monitor mon = MonitorFactory.addException(new RuntimeException("my exception"));
        assertThat(mon.getHits()).isEqualTo(1);
        assertThat(mon.getMonKey().getLabel()).isEqualTo("java.lang.RuntimeException");
        assertThat(mon.getMonKey().getDetails().toString()).contains("java.lang.RuntimeException");

        mon = MonitorFactory.getMonitor(MonitorFactory.EXCEPTIONS_LABEL, "Exception");
        assertThat(mon.getHits()).isEqualTo(1);
        assertThat(mon.getMonKey().getLabel()).isEqualTo(MonitorFactory.EXCEPTIONS_LABEL);
        assertThat(mon.getMonKey().getDetails().toString()).contains(MonitorFactory.EXCEPTIONS_LABEL);
    }

    @Test
    public void testTrackExceptionWithMon() {
        Monitor mon1 = MonitorFactory.start("anytimer").stop();
        Monitor mon =  MonitorFactory.getMonitor(MonitorFactory.EXCEPTIONS_LABEL, "Exception");
        mon = MonitorFactory.addException(mon1, new RuntimeException("my exception"));

        assertThat(MonitorFactory.getRootMonitor().hasListeners()).isTrue();
        // ensure specific exception monitor is created and stacktrace is in details
        assertThat(mon.getHits()).isEqualTo(1);
        assertThat(mon.getMonKey().getLabel()).isEqualTo("java.lang.RuntimeException");
        assertThat(mon.getMonKey().getDetails().toString()).contains("java.lang.RuntimeException");

        // ensure general exception monitor is created and stacktrace is in details
        mon = MonitorFactory.getMonitor(MonitorFactory.EXCEPTIONS_LABEL, "Exception");
        assertThat(mon.getHits()).isEqualTo(1);
        assertThat(mon.getMonKey().getLabel()).isEqualTo(MonitorFactory.EXCEPTIONS_LABEL);
        JAMonBufferListener bufferListener = (JAMonBufferListener) mon.getListenerType("value").getListener("FIFOBuffer");
        assertThat(bufferListener.getBufferList().getRowCount()).isEqualTo(1);

        // ensure the timer monitor also has the stack trace in its details
        assertThat(mon1.getMonKey().getDetails().toString()).contains("java.lang.RuntimeException");
    }

    @Test
    public void testTrackExceptionWithMon_fromDefaultProperties() {
        List<JamonPropertiesLoader.JamonListenerProperty> listeners = new JamonPropertiesLoader("I_DO_NOT_EXIST.properties").getListeners();
        Monitor mon1 = MonitorFactory.start("anytimer").stop();
        Monitor mon = MonitorFactory.addException(mon1, new RuntimeException("my exception"));

        assertThat(MonitorFactory.getRootMonitor().hasListeners()).isTrue();
        // ensure specific exception monitor is created and stacktrace is in details
        assertThat(mon.getHits()).isEqualTo(1);
        assertThat(mon.getMonKey().getLabel()).isEqualTo("java.lang.RuntimeException");
        assertThat(mon.getMonKey().getDetails().toString()).contains("java.lang.RuntimeException");

        // ensure general exception monitor is created and stacktrace is in details
        mon = MonitorFactory.getMonitor(MonitorFactory.EXCEPTIONS_LABEL, "Exception");
        assertThat(mon.getHits()).isEqualTo(1);
        assertThat(mon.getMonKey().getLabel()).isEqualTo(MonitorFactory.EXCEPTIONS_LABEL);
        JAMonBufferListener bufferListener = (JAMonBufferListener) mon.getListenerType("value").getListener("FIFOBuffer");
        assertThat(bufferListener.getBufferList().getRowCount()).isEqualTo(1);

        // ensure the timer monitor also has the stack trace in its details
        assertThat(mon1.getMonKey().getDetails().toString()).contains("java.lang.RuntimeException");
    }

    @Test
    public void testTrackExceptionWithMon_fromProperties() {
        List<JamonPropertiesLoader.JamonListenerProperty> listeners = new JamonPropertiesLoader("jamonapi2.properties").getListeners();
        assertThat(listeners).hasSize(4);
        MonitorFactory.addListeners(listeners);
        Monitor mon1 = MonitorFactory.start("anytimer").stop();
        Monitor mon = MonitorFactory.addException(mon1, new RuntimeException("my exception"));

        assertThat(MonitorFactory.getRootMonitor().hasListeners()).isTrue();
        // ensure specific exception monitor is created and stacktrace is in details
        assertThat(mon.getHits()).isEqualTo(1);
        assertThat(mon.getMonKey().getLabel()).isEqualTo("java.lang.RuntimeException");
        assertThat(mon.getMonKey().getDetails().toString()).contains("java.lang.RuntimeException");

        // ensure general exception monitor is created and stacktrace is in details
        mon = MonitorFactory.getMonitor(MonitorFactory.EXCEPTIONS_LABEL, "Exception");
        assertThat(mon.getHits()).isEqualTo(1);
        assertThat(mon.getMonKey().getLabel()).isEqualTo(MonitorFactory.EXCEPTIONS_LABEL);
        JAMonBufferListener bufferListener = (JAMonBufferListener) mon.getListenerType("value").getListener("FIFOBuffer");
        assertThat(bufferListener.getBufferList().getRowCount()).isEqualTo(1);

        // ensure java.lang.RuntimeException monitor is created and stacktrace is in details
        mon = MonitorFactory.getMonitor("java.lang.RuntimeException", "Exception");
        assertThat(mon.getHits()).isEqualTo(1);
        assertThat(mon.getMonKey().getLabel()).isEqualTo("java.lang.RuntimeException");
        bufferListener = (JAMonBufferListener) mon.getListenerType("value").getListener("FIFOBuffer");
        assertThat(bufferListener.getBufferList().getRowCount()).isEqualTo(1);

        // ensure the timer monitor also has the stack trace in its details
        assertThat(mon1.getMonKey().getDetails().toString()).contains("java.lang.RuntimeException");
    }

    @Test
    public void testFactoryDisabledSerialization() {
        MonitorFactory.setEnabled(false);
        MonitorFactoryInterface factory = MonitorFactory.getFactory();
        factory.start("hi").stop();
        MonitorFactoryInterface copy = factory.copy();
        assertThat(copy.getNumRows()).isEqualTo(0);
        assertThat(copy.getNumRows()).isEqualTo(factory.getNumRows());
    }

    @Test
    public void testFactoryEnabledSerialization() {
        MonitorFactoryInterface factory = MonitorFactory.getFactory();
        factory.start("hi").stop();
        factory.add("filesize1", "mb", 100);
        MonitorFactoryInterface copy = factory.copy();

        assertThat(copy.getRootMonitor().getReport()).isEqualTo(factory.getRootMonitor().getReport());
        assertThat(copy.getMonitor("hi", "ms.").getHits()).isEqualTo(1);

        copy.start("hi").stop();
        copy.add("filesize2", "mb", 100);
        assertThat(copy.getNumRows()).isEqualTo(4);
        assertThat(copy.getMonitor("hi", "ms.").getHits()).isEqualTo(2);
    }

    @Test
    public void testEnableDisableDebugFactory() {
        assertThat(MonitorFactory.isDebugEnabled()).isFalse();
        MonitorFactory.getDebugFactory().start("anykey").stop();
        assertThat(MonitorFactory.getNumRows()).isEqualTo(1);

        MonitorFactory.setDebugEnabled(true);
        assertThat(MonitorFactory.isDebugEnabled()).isTrue();
        MonitorFactory.getDebugFactory().start("anykey").stop();
        assertThat(MonitorFactory.getNumRows()).isEqualTo(2);

        MonitorFactory.setDebugEnabled(false);
        assertThat(MonitorFactory.isDebugEnabled()).isFalse();
        MonitorFactory.getDebugFactory().start("anykey").stop();
        assertThat(MonitorFactory.getNumRows()).isEqualTo(2);
        assertThat(MonitorFactory.getMonitor("anykey", "ms.").getHits()).isEqualTo(1);
    }

    private void assertValueListenerIsNonDecreasing(Monitor mon) {
        Double previous=-1000.0;
        // value listener should be nondecreasing.
        List<Double> values = getListenerValues(mon.getListenerType("value").getListener(), VALUE_INDEX);
        assertThat(values.size()).isEqualTo(SIZE);
        for (Double value : values)  {
            assertThat(value).isGreaterThanOrEqualTo(previous);
            previous=value;
        }
    }

    private void assertMaxListenerGreaterThanFirstMax(Monitor mon) {
        Double previous=-1000.0;
        // max listener should be nondecreasing.
        List<Double> values = getListenerValues(mon.getListenerType("max").getListener(), VALUE_INDEX);
        assertThat(values.size()).isGreaterThan(0);
        for (Double value : values)  {
            assertThat(value).isGreaterThanOrEqualTo(FIRST_MAX);
            assertThat(value).isGreaterThanOrEqualTo(previous);
            previous=value;
        }
    }

    private void assertMinListenerEqualToZero(Monitor mon) {
        // min listener should be nondecreasing.
        List<Double> values = getListenerValues(mon.getListenerType("min").getListener(), VALUE_INDEX);
        assertThat(values.size()).isGreaterThan(0);
        for (Double value : values)  {
            assertThat(value).isEqualTo(FIRST_MIN);
        }
    }

    private void assertMaxActiveListenerValues(Monitor mon) {
        // maxactive listener should be nondecreasing.
        List<Double> values = getListenerValues(mon.getListenerType("maxactive").getListener(), MAX_ACTIVE_INDEX);
        assertThat(values.size()).isEqualTo(3);
        assertThat(values.get(0)).isEqualTo(3);
        assertThat(values.get(1)).isEqualTo(2);
        assertThat(values.get(2)).isEqualTo(1);
    }


    /** return a test range holder */
    static RangeHolder getTestHolder() {
        RangeHolder rh = new RangeHolder();
        rh.add("10_display", 10);
        rh.add("20_display", 20);
        rh.add("30_display", 30);
        rh.add("40_display", 40);
        rh.add("50_display", 50);
        rh.add("60_display", 60);
        rh.add("70_display", 70);
        rh.add("80_display", 80);
        rh.add("90_display", 90);
        rh.add("100_display", 100);
        rh.add("110_display", 110);
        rh.add("120_display", 120);
        rh.add("130_display", 130);
        rh.add("140_display", 140);
        rh.add("150_display", 150);
        // note last range is always called lastRange and is added automatically
        return rh;

    }

    private static List<Double> getListenerValues(JAMonListener listener, int colNum) {
        List<Double> values=new ArrayList<Double>();
        Object[][] data=((JAMonBufferListener)listener).getDetailData().getData();
        for (int i=0;i<data.length;i++) {
            values.add(Double.valueOf(data[i][colNum].toString()));
        }

        return values;
    }

}
