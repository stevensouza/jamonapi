package com.jamonapi.http;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Handler that can be used to track request access in jetty.  See www.jamonapi.com for more info on how to
 * add this handler to the jetty.xml file.  This is a wrapper class for the true monitoring class of HttpMonFactory.
 * This is class works with Jetty version 9.
 * 
 */
public class JAMonJettyHandlerNew extends HandlerWrapper implements HttpMonManage{

    private static final String PREFIX="com.jamonapi.http.JAMonJettyHandlerNew";

    private static final String DEFAULT_SUMMARY="default, response.getContentCount().bytes, response.getStatus().value.ms";

    private HttpMonFactory httpMonFactory=new JettyHttpMonFactoryNew(PREFIX);

    private String jamonSummaryLabels="default";

    public JAMonJettyHandlerNew() {
        setSummaryLabels(jamonSummaryLabels);
    }

    /** Monitor the request and call any other requests in the decorator chain */
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpMon httpMon=null;
        try {
            httpMon=httpMonFactory.start(request, response);
            super.handle(target, baseRequest, request, response);
        } catch (Throwable e) {
            httpMon.throwException(e);
        } finally {
            httpMon.stop();
        }

    }

    public void setSummaryLabels(String jamonSummaryLabels) {
        httpMonFactory.setSummaryLabels(jamonSummaryLabels, DEFAULT_SUMMARY);
    }

    public String getSummaryLabels() {
        return httpMonFactory.getSummaryLabels();
    }

    public void addSummaryLabel(String jamonSummaryLabel) {
        httpMonFactory.addSummaryLabel(jamonSummaryLabel);
    }

    public boolean getIgnoreHttpParams() {
        return httpMonFactory.getIgnoreHttpParams();
    }

    public void setIgnoreHttpParams(boolean ignoreHttpParams) {
        httpMonFactory.setIgnoreHttpParams(ignoreHttpParams);
    }

    public void setEnabled(boolean enable) {
        httpMonFactory.setEnabled(enable);

    }

    public int getSize() {
        return httpMonFactory.getSize();
    }

    public boolean getEnabled() {
        return httpMonFactory.getEnabled();
    }

    public void setSize(int size) {
        httpMonFactory.setSize(size);

    }

}
