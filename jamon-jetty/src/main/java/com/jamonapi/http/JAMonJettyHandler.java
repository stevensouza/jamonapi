package com.jamonapi.http;


import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Handler that can be used to track request access in jetty.  See www.jamonapi.com for more info on how to
 * add this handler to the jetty.xml file.  This is a wrapper class for the true monitoring class of HttpMonFactory.
 * 
 */
public class JAMonJettyHandler implements Handler.Wrapper, HttpMonManage {

    private static final String PREFIX="com.jamonapi.http.JAMonJettyHandler";
    private static final String DEFAULT_SUMMARY="default, response.getContentCount().bytes, response.getStatus().value.httpStatus, response.getStatus().summary.httpStatus";
    private HttpMonFactory httpMonFactory=new JettyHttpMonFactory(PREFIX);
    private String jamonSummaryLabels="default";

    public JAMonJettyHandler() {
        setSummaryLabels(jamonSummaryLabels);
    }

    private Handler _handler;

    @Override
    public Handler getHandler() {
        return _handler;
    }

    @Override
    public void setHandler(Handler handler) {
        _handler = handler;
    }

    /** Monitor the request and call any other requests in the decorator chain */
    @Override
    public boolean handle(Request request, Response response, Callback callback) throws Exception {
        HttpMon httpMon=null;
        try {
            httpMon=httpMonFactory.start(request, response);
            return Handler.Wrapper.super.handle(request, response, callback);
        } catch (Throwable e) {
            httpMon.throwException(e);
            throw e;
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
