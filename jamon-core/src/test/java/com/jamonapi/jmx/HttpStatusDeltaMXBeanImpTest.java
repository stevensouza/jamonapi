package com.jamonapi.jmx;

import com.jamonapi.MonitorFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpStatusDeltaMXBeanImpTest {

    private HttpStatusMXBean bean = new HttpStatusDeltaMXBeanImp();

    @Before
    public void setUp() throws Exception {
        MonitorFactory.reset();
    }

    @After
    public void tearDown() throws Exception {
        MonitorFactory.reset();
    }

    @Test
    public void testGet1xx() throws Exception {
        createStatusCodeMonitors(1);
        createStatusCodeMonitors(1);
        assertThat(bean.get1xx()).isEqualTo(2);
        assertThat(bean.get1xx()).isEqualTo(0);
        createStatusCodeMonitors(1);
        assertThat(bean.get1xx()).isEqualTo(1);
    }

    @Test
    public void testGet2xx() throws Exception {
        createStatusCodeMonitors(2);
        assertThat(bean.get2xx()).isEqualTo(2);
        assertThat(bean.get2xx()).isEqualTo(0);
        createStatusCodeMonitors(2);
        assertThat(bean.get2xx()).isEqualTo(2);
    }

    @Test
    public void testGet3xx() throws Exception {
        createStatusCodeMonitors(3);
        assertThat(bean.get3xx()).isEqualTo(3);
        assertThat(bean.get3xx()).isEqualTo(0);
        createStatusCodeMonitors(3);
        assertThat(bean.get3xx()).isEqualTo(3);
    }

    @Test
    public void testGet4xx() throws Exception {
        createStatusCodeMonitors(4);
        assertThat(bean.get4xx()).isEqualTo(4);
        assertThat(bean.get4xx()).isEqualTo(0);
        createStatusCodeMonitors(4);
        assertThat(bean.get4xx()).isEqualTo(4);
    }

    @Test
    public void testGet5xx() throws Exception {
        createStatusCodeMonitors(5);
        assertThat(bean.get5xx()).isEqualTo(5);
        assertThat(bean.get5xx()).isEqualTo(0);
        createStatusCodeMonitors(5);
        assertThat(bean.get5xx()).isEqualTo(5);
    }

    private void createStatusCodeMonitors(int statusCode) {
        for (int i=0;i<statusCode;i++) {
            MonitorFactory.add(HttpStatusMXBean.LABEL + statusCode + "xx", HttpStatusMXBean.UNITS, statusCode);
        }
    }
}