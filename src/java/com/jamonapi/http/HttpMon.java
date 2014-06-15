package com.jamonapi.http;

import java.io.IOException;

import javax.servlet.ServletException;

/** Generic monitoring interface used with HttpServletRequest, and HttpServletResponse objects used in servlet containers.
 * It will also monitor any objects that implement these interfaces as well as any of the methods the implementing classes
 * add to the interface.  Examples would be requests/responses provided by tomcat, jboss, jetty containers.
 * 
 * @author steve souza
 *
 */
public interface HttpMon {

    public HttpMon start();

    public void stop();

    public String getDetailLabel();

    public void setException(Throwable t);

    public void throwException(Throwable t) throws IOException, ServletException;

    public Throwable getException();

}
