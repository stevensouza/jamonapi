package com.jamonapi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


public class MonitorCompositeIteratorTest {

    @Before
    public void setUp() throws Exception {
        MonitorFactory.reset();
    }

    @After
    public void tearDown() throws Exception {
        MonitorFactory.reset();
    }

    @Test
    public void testWorksWithOneComposite() throws Exception {
        MonitorFactory.start("hello").stop();
        MonitorFactory.start("world").stop();
        MonitorComposite monitorComposite = MonitorFactory.getRootMonitor();
        Set<MonitorComposite> set = new HashSet<MonitorComposite>();
        set.add(monitorComposite);
        MonitorCompositeIterator iter = new MonitorCompositeIterator(set);
        int size = iter.toList().size();

        assertThat(size).isEqualTo(monitorComposite.getNumRows());
    }

    @Test
    public void testWorksWithMultipleComposites() throws Exception {
        MonitorFactory.start("hello").stop();
        MonitorFactory.start("world").stop();
        MonitorComposite monitorComposite1 = MonitorFactory.getRootMonitor().copy().setInstanceName("mc1");
        MonitorFactory.reset();

        MonitorFactory.start("hello").stop();
        MonitorFactory.start("world").stop();
        MonitorFactory.add("page", "counter", 1);
        MonitorComposite monitorComposite2 = MonitorFactory.getRootMonitor().copy().setInstanceName("mc2");

        Set<MonitorComposite> set = new HashSet<MonitorComposite>();
        set.add(monitorComposite1);
        set.add(monitorComposite2);

        MonitorCompositeIterator compositeIterator = new MonitorCompositeIterator(set);
        List<Monitor> list = compositeIterator.toList();
        int size = list.size();

        assertThat(size).isEqualTo(monitorComposite1.getNumRows()+monitorComposite2.getNumRows());
        assertThat(instanceNames(list)).containsOnly("mc1","mc2");
    }

    private Set<String> instanceNames(List<Monitor> list) {
        Iterator<Monitor> iter = list.iterator();
        Set<String> set = new HashSet<String>();
        while (iter.hasNext()) {
            set.add(iter.next().getMonKey().getInstanceName());
        }
        return set;
    }

}
