package com.jamonapi.http;

import java.io.IOException;

import javax.servlet.ServletException;

/**
 * HttpMon that is used instead of HttpMonRequest when monitoring is disabled.
 * Essentially this class will be used as a singleton with noop methods.
 * 
 * @author steve souza
 *
 */
class HttpMonNull implements HttpMon {

    HttpMonNull() {
    }

    public HttpMon start() {
        return this;
    }

    public void stop() {
    }

    public String getDetailLabel() {
        return "";
    }

    public Throwable getException() {
        return null;
    }

    public void setException(Throwable t) {
    }

    public void throwException(Throwable t) throws IOException, ServletException {
        if (t instanceof ServletException)
            throw (ServletException)t;
        else if (t instanceof IOException)
            throw (IOException)t;
        else
            throw new ServletException(t);
    }

}
