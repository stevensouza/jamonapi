package com.jamonapi.aop.spring;

/**
 * Created by stevesouza on 6/16/14.
 */
public class HelloSpringBean {

    public String getMyString() {
        return "hi";
    }

    public void setMyString(String string) throws InterruptedException {
        Thread.sleep(50);
    }
}
