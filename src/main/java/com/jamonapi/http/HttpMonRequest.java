package com.jamonapi.http;


import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.jamonapi.MonKeyImp;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.jamonapi.utils.Misc;


/**
 * Generic HttpMon object that monitors http request, and http response objects.
 * Includes any dynamic data needed by HttpMonItem such as url, request and response.  This allows HttpMonItems to not be
 * created with each request. This object is constructed via HttpMonFactory and represents
 * all monitors for one page request.
 */

final class HttpMonRequest implements HttpMon {
    private static final String EXCEPTION_ATTR="javax.servlet.error.exception";// seems to be standarad exception property for tocmat, and jetty

    private final Object request;//HttpServletRequest or child of it

    private final Object response;//HttpServletResponse or a child of it

    private final HttpMonFactory httpMonFactory;

    private Monitor[] timeMons;// an array that is created for each time monitor

    private int timeMonIndex=0;// index used in start/stop methods to keep track of the current time monitor

    private String keyReadyURI;// uri that has params removed such as jsessionid.  This can be used as a jamon key.

    private String detailLabel;

    private String stackTrace;

    private Throwable requestException;

    HttpMonRequest(Object request, Object response, HttpMonFactory httpMonFactory) {
        this.request=request;
        this.response=response;
        this.httpMonFactory=httpMonFactory;
        this.timeMons=(httpMonFactory.getNumTimeMons()>0) ? new Monitor[httpMonFactory.getNumTimeMons()] : null;
        detailLabel=getRequestURI();
    }

    /**
     * Method called to start monitoring http requests, and responses.  Loop through all
     * HttpMonItems starting each of them.  Note state is passed into the stateless HttpMonItem
     * instances via 'this' instance.
     */
    public HttpMon start() {
        timeMonIndex=0;// Index that is incremented for time monitors only
        Iterator iter=iter();

        while (iter.hasNext()) {
            HttpMonItem monItem=(HttpMonItem) iter.next();
            monItem.start(this);
        }

        return this;
    }


    /** Method called to stop any active http monitoring requests, and responses */
    public void stop() {
        timeMonIndex=0;
        setException();
        Iterator iter=iter();

        while (iter.hasNext()) {
            HttpMonItem monItem=(HttpMonItem) iter.next();
            monItem.stop(this);
        }

        if (stackTrace!=null)
            changeDetails();// detailLabel now has stack trace in it.
    }

    // change details was called when stackTrace had data in it.  The result is to
    // allow the gui to show the stack tracke for all monitors buffer listeners.
    private void changeDetails() {
        int len=(timeMons==null) ? 0 : timeMons.length;
        for (int i=0;i<len;i++)
            timeMons[i].getMonKey().setDetails( getDetailLabel());
    }

    // Iterator over httpMonItems owned by the httpMonFactory
    private Iterator iter() {
        return httpMonFactory.iter();
    }

    /** Detail label used in jamon.  It will always include the requestURI, and will also include
     * a stack trace if one occurred.
     */
    public String getDetailLabel() {
        if (stackTrace==null || "".equalsIgnoreCase(stackTrace))
            return getRequestURI();
        else
            return new StringBuffer(getRequestURI()).append("\n").append(stackTrace).toString();
    }


    public Throwable getException() {
        return requestException;
    }

    public void setException(Throwable t) {
        this.requestException=t;
    }

    private void setException() {
        if (requestException==null && request instanceof HttpServletRequest) {
            Object exc=((HttpServletRequest)request).getAttribute(EXCEPTION_ATTR);// defined by all containers
            if (exc instanceof Throwable)
                setException((Throwable)exc);//sets requestException
        }
        // if an exception has occurred make neccesarry jamon records.
        if (requestException!=null) {
            StringBuffer trace=new StringBuffer("stackTrace=").append(Misc.getExceptionTrace(requestException));
            if (requestException instanceof ServletException && ((ServletException)requestException).getRootCause()!=null)
                trace.append("\nrootCause=").append(Misc.getExceptionTrace(((ServletException)requestException).getRootCause()));

            setStackTrace(trace.toString());
            String label=getLabelPrefix()+".ServletException";
            MonitorFactory.add(new MonKeyImp(label, getDetailLabel(), "Exception"), 1);
            MonitorFactory.add(new MonKeyImp(MonitorFactory.EXCEPTIONS_LABEL, getDetailLabel(), "Exception"), 1);
        }
    }

    public void throwException(Throwable t) throws IOException, ServletException {
        setException();

        if (t instanceof ServletException)
            throw (ServletException)t;
        else if (t instanceof IOException)
            throw (IOException)t;
        else
            throw new ServletException(t);

    }



    // Return the URI/URL of this request.  As this is called often enough it is cached after the first call.
    String getRequestURI() {
        if (detailLabel!=null)
            return detailLabel;
        else if (request instanceof HttpServletRequest) {
            return ((HttpServletRequest)request).getRequestURI();
        } else
            return "";
    }


    // remove httpPrams from requestURI so it can be used as a jamon key without worry
    // of creating too many keys.
    String getKeyReadyURI() {
        if (keyReadyURI==null)
            keyReadyURI=removeHttpParams(getRequestURI());

        return keyReadyURI;

    }


    String getContextPath() {
        if (request instanceof HttpServletRequest) {
            return ((HttpServletRequest)request).getContextPath();
        } else
            return "";
    }

    // used to cache Exception string as it would have to be generated once per monitor if it wasn't cached, and would also consume memory
    // for each creation.
    void setStackTrace(String stackTrace) {
        this.stackTrace=stackTrace;
    }



    // Get the prefix used in the monitor label such as:  com.jamonapi.http
    String getLabelPrefix() {
        return httpMonFactory.getLabelPrefix();

    }

    // Ignore jsessionid's and other prams as part of the request.  This is done to make requests more unique (i.e. /jamon/mypage.jsp?jessionid=ls883dds)
    // gets converted to /jamon/mypage.jsp
    boolean getIgnoreHttpParams() {
        return httpMonFactory.getIgnoreHttpParams();
    }

    // Is monitoring enabled?
    boolean isEnabled() {
        return httpMonFactory.getEnabled();
    }

    // Get the max possible size of the buffer before it fills.  This is to avoid buffer overflow problems.
    int getSize() {
        return httpMonFactory.getSize();
    }

    // httpservletrequest
    Object getRequest() {
        return request;
    }

    // httpservletresponse
    Object getResponse() {
        return response;
    }

    // If it is a timeMonitor then set the current monitor into the time array. This is called  from httpMonItem
    // i.e. it is a callback method called from httpMonItem start at the appropriate time and it is a time monitor
    // Note strictly speaking the 'if' checks aren't required.  However, my thought is that no app should ever
    // risk an exception being thrown due to monitoring
    void setTimeMon(Monitor mon) {
        if (timeMons!=null && timeMonIndex<timeMons.length)
            timeMons[timeMonIndex++]=mon;
    }

    // Stop the time monitoring.  Note this is a call back method called from httpMonItem when stop is called on it
    // and it is a time monitor
    // Note strictly speaking the 'if' checks aren't required.  However, my thought is that no app should ever
    // risk an exception being thrown due to monitoring
    void stopTimeMon() {
        if (timeMons!=null && timeMonIndex<timeMons.length)
            timeMons[timeMonIndex++].stop();
    }

    Monitor getNextTimeMon() {
        if (timeMons!=null && timeMonIndex<timeMons.length)
            return timeMons[timeMonIndex++];
        else
            return null;
    }


    private static String removeHttpParams(Object url) {
        if (url==null)
            return null;

        String urlStr=url.toString();
        int paramIndex = urlStr.indexOf(";");
        if (paramIndex == -1)// ; isn't in string
            return urlStr;
        else
            return urlStr.substring(0, paramIndex);

    }


}
