package com.jamonapi.jmx;

import com.jamonapi.MonitorFactory;
import com.jamonapi.utils.Log4jUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Log4jMXBeanImpTest {
    Log4jMXBean bean = new Log4jMXBeanImp();

    @Before
    public void setUp() throws Exception {
        MonitorFactory.reset();
        Log4jUtils.log();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetTRACE() throws Exception {
        assertThat(bean.getTrace()).isEqualTo(1);
    }

    @Test
    public void testGetDEBUG() throws Exception {
        assertThat(bean.getDebug()).isEqualTo(2);
    }

    @Test
    public void testGetINFO() throws Exception {
        assertThat(bean.getInfo()).isEqualTo(3);
    }

    @Test
    public void testGetWARN() throws Exception {
        assertThat(bean.getWarn()).isEqualTo(4);
    }

    @Test
    public void testGetERROR() throws Exception {
        assertThat(bean.getError()).isEqualTo(5);
    }

    @Test
    public void testGetFATAL() throws Exception {
        assertThat(bean.getFatal()).isEqualTo(6);
    }

    @Test
    public void testGetTOTAL() throws Exception {
        assertThat(bean.getTotal()).isEqualTo(21);
    }
}
