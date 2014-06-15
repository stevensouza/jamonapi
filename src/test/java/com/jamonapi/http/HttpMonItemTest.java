package com.jamonapi.http;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

public class HttpMonItemTest {

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
