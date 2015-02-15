package com.jamonapi.http;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.jamonapi.jmx.JamonJmxBeanProperty;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpMonItemTest {
    private HttpServletResponse response = mock(HttpServletResponse.class);
    private HttpServletRequest request = mock(HttpServletRequest.class);
    private HttpMonFactory monFactory = new HttpMonFactory("labelPrefix");
    private HttpMonRequest monRequest;

    @Before
    public void setUp() throws Exception {
        MonitorFactory.reset();
        monRequest = new HttpMonRequest(request, response, monFactory);
    }

    @After
    public void tearDown() throws Exception {
        MonitorFactory.reset();
    }

    @Test
    public void testHttpStatus() {
        when(response.getStatus()).thenReturn(404);
        HttpMonItem monItem = new HttpMonItem("response.getStatus().value.httpStatus", monFactory);
        monItem.start(monRequest);
        monItem.stop(monRequest);
        Monitor mon = MonitorFactory.getMonitor("labelPrefix.response.getStatus().value: 404", "httpStatus");

        assertThat(mon.getHits()).isEqualTo(1);
        assertThat(mon.getLastValue()).isEqualTo(404);

    }

    @Test
    public void testHttpStatusSummary() {
        when(response.getStatus()).thenReturn(404);
        HttpMonItem monItem = new HttpMonItem("response.getStatus().summary.httpStatus", monFactory);
        monItem.start(monRequest);
        monItem.stop(monRequest);
        monItem.start(monRequest);
        monItem.stop(monRequest);

        Monitor mon = MonitorFactory.getMonitor("com.jamonapi.http.response.getStatus().summary: 4xx", "httpStatus");

        assertThat(mon.getHits()).isEqualTo(2);
        assertThat(mon.getAvg()).isEqualTo(404);

        when(response.getStatus()).thenReturn(200);
        monItem.start(monRequest);
        monItem.stop(monRequest);
        when(response.getStatus()).thenReturn(299);
        monItem.start(monRequest);
        monItem.stop(monRequest);

        mon = MonitorFactory.getMonitor("com.jamonapi.http.response.getStatus().summary: 2xx", "httpStatus");
        assertThat(mon.getHits()).isEqualTo(2);
        assertThat(mon.getAvg()).isEqualTo(249.5);
    }

    @Test
    public void testColAlias() {
        assertThat(HttpMonItem.colAlias("request.getMethod().units.value as MyLabel")).isEqualTo("MyLabel");
        assertThat(HttpMonItem.colAlias("request.getMethod().units.value")).isNull();  // null no alias
        assertThat(HttpMonItem.colAlias("request.getMethod()")).isNull();// null no alias
    }

    @Test
    public void testNonAlias() {
        assertThat(HttpMonItem.nonAlias("request.getMethod().units.value as MyLabel")).isEqualTo("request.getMethod().units.value");
    }

    @Test
    public void testRemoveHttpParams() {
        assertThat(HttpMonItem.removeHttpParams("http://www.mypage.com:8080/page;jsessionid=5akalsdjfflj;other=9s0f0udsf?pageName=my.jsp")).isEqualTo("http://www.mypage.com:8080/page");
        assertThat(HttpMonItem.removeHttpParams("http://www.mypage.com:8080/page?pageName=my.jsp")).isEqualTo("http://www.mypage.com:8080/page?pageName=my.jsp");
    }

}
