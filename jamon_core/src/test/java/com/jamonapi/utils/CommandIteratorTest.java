package com.jamonapi.utils;


import static org.fest.assertions.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Vector;

import org.junit.Test;


public class CommandIteratorTest {

    /** Iterate values in the collection and ensure that they are in increasing order */
    private Command createAssertCommand() {
        return new Command() {
            private int index;
            public void execute(Object value) throws Exception {
                assertThat(value).isEqualTo(++index);
            }

        };
    }

    @Test
    public void testList() throws Exception {
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        CommandIterator.iterate(list, createAssertCommand());
    }

    @Test
    public void testVector()  throws Exception {
        Vector<Integer> vector = new Vector<Integer>();
        vector.addElement(1);
        vector.addElement(2);
        vector.addElement(3);
        vector.addElement(4);

        CommandIterator.iterate(vector.elements(), createAssertCommand());
    }

}
