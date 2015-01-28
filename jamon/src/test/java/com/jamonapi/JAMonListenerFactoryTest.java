package com.jamonapi;

import com.jamonapi.utils.BufferList;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


public class JAMonListenerFactoryTest {

    private static final String[] EXPECTED_LISTENERS=   {"FIFOBuffer", "helloListener", "NLargestValueBuffer", "NLargestValueBuffer24Hrs", "NLargestValueBuffer7Days", "NSmallestValueBuffer",
        "NSmallestValueBuffer24Hrs", "NSmallestValueBuffer7Days", "SharedFIFOBuffer", "SharedNLargestValueBuffer", "SharedNLargestValueBuffer24Hrs", "SharedNLargestValueBuffer7Days",
        "SharedNSmallestValueBuffer", "SharedNSmallestValueBuffer24Hrs", "SharedNSmallestValueBuffer7Days", "tester"};


    @Test
    public void testHeader() {
        String[] header=JAMonListenerFactory.getHeader();
        assertThat(header.length).isEqualTo(2);
        assertThat(header[0]).isEqualTo("ListenerName");
        assertThat(header[1]).isEqualTo("Listener");
    }

    @Test
    public void testFactoryPopulatedAndReset() {
        final int BUFFER_SIZE=301;
        List<String> listeners=listenerNamesToCollection(JAMonListenerFactory.getData());
        assertThat(listeners).hasSize(14);

        JAMonListenerFactory.put(new CompositeListener("tester"));
        JAMonListenerFactory.put(new JAMonBufferListener("helloListener", new BufferList(new String[]{"hey"},BUFFER_SIZE)));
        listeners=listenerNamesToCollection(JAMonListenerFactory.getData());
        assertThat(listeners).containsOnly(EXPECTED_LISTENERS);
        assertThat(listeners).hasSize(16);

        JAMonBufferListener jbl=(JAMonBufferListener)JAMonListenerFactory.get("helloListener");
        assertThat(jbl.getName()).isEqualTo("helloListener");
        assertThat(jbl.getBufferList().getBufferSize()).isEqualTo(BUFFER_SIZE);

        jbl=(JAMonBufferListener)JAMonListenerFactory.get("FIFOBuffer");
        assertThat(jbl.getName()).isEqualTo("FIFOBuffer");
        assertThat(jbl.getBufferList().getBufferSize()).isEqualTo(50);

        JAMonListenerFactory.reset();
        listeners=listenerNamesToCollection(JAMonListenerFactory.getData());
        assertThat(listeners).hasSize(14);
    }


    @Test
    public void testFifoBuffer_IncreasingValues() {
        // The following values are in increasing order
        List<Integer> values=testArray("FIFOBuffer", Calendar.DAY_OF_YEAR, true);
        // 104 elements were put into a buffer of 50 so the last 50 should remain for the fifo buffer.
        for (int i=0;i<=45;i++) {
            assertThat(values.get(i)).isEqualTo(i+55);
        }

        assertThat(values.get(46)).isEqualTo(1000);
        assertThat(values.get(47)).isEqualTo(-1000);
        assertThat(values.get(48)).isEqualTo(1100);
        assertThat(values.get(49)).isEqualTo(-1100);
    }

    @Test
    public void testFifoBuffer_DecreasingValues() {
        // The following values are in decreasing order
        List<Integer> values=testArray("FIFOBuffer", Calendar.DAY_OF_YEAR, false);
        // 104 elements were put into a buffer of 50 so the last 50 should remain for the fifo buffer.
        for (int i=0;i<=45;i++) {
            assertThat(values.get(i)).isEqualTo(46-i);
        }

        assertThat(values.get(46)).isEqualTo(1000);
        assertThat(values.get(47)).isEqualTo(-1000);
        assertThat(values.get(48)).isEqualTo(1100);
        assertThat(values.get(49)).isEqualTo(-1100);
    }

    @Test
    public void testNLargestValueBuffer_IncreasingValues() {
        // The following values are in increasing order
        List<Integer> values=testArray("NLargestValueBuffer", Calendar.DAY_OF_YEAR, true);
        for (int i=0;i<=47;i++) {
            assertThat(values.get(i)).isEqualTo(i+53);
        }

        assertThat(values.get(48)).isEqualTo(1000);
        assertThat(values.get(49)).isEqualTo(1100);
    }

    @Test
    public void testNLargestValueBuffer_DecreasingValues() {
        // The following values are in increasing order
        List<Integer> values=testArray("NLargestValueBuffer", Calendar.DAY_OF_YEAR, false);
        for (int value=100, i=0;value>=53;value--, i++) {
            assertThat(values.get(i)).isEqualTo(value);
        }

        assertThat(values.get(48)).isEqualTo(1000);
        assertThat(values.get(49)).isEqualTo(1100);
    }

    @Test
    public void testNLargestValueBuffer7Days_IncreasingValues() {
        // The following values are in increasing order
        List<Integer> values=testArray("NLargestValueBuffer7Days", Calendar.DAY_OF_YEAR, true);
        for (int i=0;i<=48;i++) {
            assertThat(values.get(i)).isEqualTo(i+52);
        }

        assertThat(values.get(49)).isEqualTo(1000);
    }

    @Test
    public void testNLargestValueBuffer7Days_DecreasingValues() {
        // The following values are in increasing order
        List<Integer> values=testArray("NLargestValueBuffer7Days", Calendar.DAY_OF_YEAR, false);
        for (int value=100, i=0;value>=52;value--, i++) {
            assertThat(values.get(i)).isEqualTo(value);
        }

        assertThat(values.get(49)).isEqualTo(1000);
    }

    @Test
    public void testNSmallestValueBuffer_IncreasingValues() {
        // The following values are in increasing order
        List<Integer> values=testArray("NSmallestValueBuffer", Calendar.DAY_OF_YEAR, true);
        for (int i=0;i<=47;i++) {
            assertThat(values.get(i)).isEqualTo(i+1);
        }

        assertThat(values.get(48)).isEqualTo(-1000);
        assertThat(values.get(49)).isEqualTo(-1100);
    }

    @Test
    public void testNSmallestValueBuffer_DecreasingValues() {
        // The following values are in increasing order
        List<Integer> values=testArray("NSmallestValueBuffer", Calendar.DAY_OF_YEAR, false);
        for (int value=48, i=0;value>=1;value--, i++) {
            assertThat(values.get(i)).isEqualTo(value);
        }

        assertThat(values.get(48)).isEqualTo(-1000);
        assertThat(values.get(49)).isEqualTo(-1100);
    }


    private static List<String> listenerNamesToCollection(Object[][] listeners) {
        List<String> list=new ArrayList<String>();
        for (int i=0;i<listeners.length;i++) {
            list.add(listeners[i][0].toString());
        }

        return list;
    }

    private static List<Integer> testArray(String label, int dateToAdd, boolean increase) {
        JAMonBufferListener jbl=(JAMonBufferListener)JAMonListenerFactory.get(label);
        BufferList bl=jbl.getBufferList();

        Calendar cal=new GregorianCalendar();
        if (increase) {
            for (int i=1,j=-50;i<=100;i++,j++) {
                cal.setTime(new Date());
                cal.add(dateToAdd, j);
                bl.addRow(new Object[]{"label"+i,new Integer(i),"Active"+i,cal.getTime()});
            }

        } else {
            for (int i=100,j=50;i>=1;i--,j--) {
                cal.setTime(new Date());
                cal.add(dateToAdd, j);
                bl.addRow(new Object[]{"label"+i,new Integer(i),"Active"+i,cal.getTime()});
            }

        }

        // firstVal will be under date threshold, and secondVal will exceed it.
        int firstVal=-5;
        int secondVal=-10;

        if (dateToAdd==Calendar.HOUR_OF_DAY) {
            firstVal=-12;
            secondVal=-36;
        }


        cal.setTime(new Date());
        cal.add(dateToAdd, firstVal);
        bl.addRow(new Object[]{"label",new Integer(1000),"Active",cal.getTime()});

        cal.setTime(new Date());
        cal.add(dateToAdd, firstVal);
        bl.addRow(new Object[]{"label",new Integer(-1000),"Active",cal.getTime()});

        cal.setTime(new Date());
        cal.add(dateToAdd, secondVal);
        bl.addRow(new Object[]{"label",new Integer(1100),"Active",cal.getTime()});

        cal.setTime(new Date());
        cal.add(dateToAdd, secondVal);
        bl.addRow(new Object[]{"label",new Integer(-1100),"Active",cal.getTime()});

        return grabIntegers(bl.getData());
    }

    private static List<Integer> grabIntegers(Object[][] data) {
        List<Integer> list=new ArrayList<Integer>();
        for (int i=0;i<data.length;i++) {
            list.add(Integer.parseInt(data[i][1].toString()));
        }

        return list;
    }

}
