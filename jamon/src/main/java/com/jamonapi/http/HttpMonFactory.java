package com.jamonapi.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/** Base class that monitors a httpServletRequest, and HttpServletResponse by returning an HttpMonRequest object per page request.  Although this
 * class can be used directly it will more often be used transparently by classes such as JAMonTomcat55Valve, JAMonServletFilter and JAMonJettyHandler.
 * To get a list of many possible (though not all) HttpServletRequest and HttpServletResponse labels pass 'demo' to the valve, handler or servlet filter.
 * You can also see these same possibiliteis by calling HttpMonFactory.getDemoLabels().  Note jetty and tomcat handler and valve respectively
 * are based on objects that inherit from HttpServletRequest and HttpServletResponse respectively and all methods that these subclasses
 * implement are available too.
 * 
 * <p>Representative values returned from response methods
 * <ul>
 *  <li> response.getBufferSize()=8192
 *  <li> response.getCharacterEncoding()=utf-8
 *  <li> response.getContentCount()=985
 *  <li> response.getContentType()=text/html;charset=utf-8
 *  <li> response.getLocale()=en_US
 * </ul>
 * 
 * <p>Representative values returned from request methods
 * <ul>
 *   <li> request.getAuthType()=null
 *   <li> request.getCharacterEncoding()=null
 *   <li> request.getContentLength()=-1
 *   <li> request.getCharacterEncoding()=null
 *   <li> request.getContentType()=null
 *   <li> request.getContextPath()=/jamon
 *   <li> request.getLocalAddr()=127.0.0.1
 *   <li> request.getLocale()=en_US
 *   <li> request.getLocalName()=localhost
 *   <li> request.getLocalPort()=8080
 *   <li> request.getMethod()=GET
 *   <li> request.getPathInfo()=null
 *   <li> request.getPathTranslated()=null
 *   <li> request.getProtocol()=HTTP/1.1
 *   <li> request.getQueryString()=name=steve%20souza&id=9898
 *   <li> request.getRemoteAddr()=127.0.0.1
 *   <li> request.getRemoteHost()=127.0.0.1
 *   <li> request.getRemotePort()=1454
 *   <li> request.getRemoteUser()=null
 *   <li> request.getRequestedSessionId()=670BFE2B4A7C7C77D9825EFA753D2058
 *   <li> request.getRequestURI()=/jamon/ZZZZ
 *   <li> request.getRequestURL()=http://localhost:8080/jamon/ZZZZ
 *   <li> request.getScheme()=http
 *   <li> request.getServerName()=localhost
 *   <li> request.getServerPort()=8080
 *   <li> request.getServletPath()=/ZZZZ
 *   <li> request.getUserPrincipal()=null
 *   <li> request.isRequestedSessionIdFromCookie()=true
 *   <li> request.isRequestedSessionIdFromURL()=false
 *   <li> request.isRequestedSessionIdValid()=false
 *   <li> request.isSecure()=false
 *   <ul>
 * 
 */
public class HttpMonFactory implements HttpMonManage, Serializable {

    private static final String DEFAULT_SUMMARY="request.getRequestURI().ms as allPages, request.getRequestURI().value.ms as page";
    private static final long serialVersionUID = 278L;
    private static final HttpMon NULL_HTTP_MON=new HttpMonNull();// used when monitoring is disabled.

    private String jamonSummaryLabels="default";// will do the above monitors if the word default is used in this variable.
    private Collection httpMonItemsHolder=new ArrayList();// Holds HttpMonItems
    private boolean ignoreHttpParams=true;// ignore http params if getRequestURI, or getRequestURL are called.  This done to primarily to prevent
    // jsessionid from becoming part of a jamon label.  By default params are removed (i.e. true)
    private String labelPrefix; // prefix used for jamon labels
    private boolean enabled=true; //Enable/Disable httpMonitoring. By default it is on
    private int numTimeMons=0;// The Number of monitors that are time based ones.

    // The size value will not allow any more http stats to be put into jamon if the total number of jamon entries exceeds 5000 entries.
    // This value may be changed to anything.  Note jamon entries can still be added via standard jamon calls (might want to add this feature
    // there too).  This is to prevent a buffer overflow should someone keep submitting invalid pages when a record is created for each page.
    private int size=5000;
    /** Create an HttpMonFactory by passing in text that occurs at the beginning of all jamon labels. ex com.jamonapi.http.JAMonTomcatValve */
    public HttpMonFactory(String labelPrefix) {
        this.labelPrefix=labelPrefix;
    }

    /** Pass a series of things (HttpServletRequest/HttpServletResponse methods) to monitor.  If the word 'default' is passed then the default
     * values will be used.  If 'demo' is passed you will see a list of possibilities (This should not be done in production as too much data would be
     * generated).  You can add to the default by doing passing "default, request.getStatus().httpStatus". Each time this method is called any previously
     * set items that were being monitored will not longer be monitored.  See http://www.jamonapi.com for more examples.
     */
    public void setSummaryLabels(String jamonSummaryLabels) {
        this.jamonSummaryLabels="";
        this.httpMonItemsHolder=new ArrayList();
        this.numTimeMons=0;

        if (jamonSummaryLabels==null)
            return;

        // replace the word 'demo' with a good sampling of possibilities
        jamonSummaryLabels=jamonSummaryLabels.replaceAll("(?i)demo", getDemoLabels());

        // replace string 'default' with the actual default values
        jamonSummaryLabels=replaceDefault(jamonSummaryLabels, DEFAULT_SUMMARY);

        // tokenize the string and add each of the HttpMonItems
        String[] summaryLabelsArr=split(jamonSummaryLabels);
        for (int i=0;i<summaryLabelsArr.length;i++) {
            addSummaryLabel(summaryLabelsArr[i]);
        }

    }


    public static String getDemoLabels() {
        String demoStr="";

        // response methods
        demoStr+=getDemoLabel("response.getBufferSize()","bytes");
        demoStr+=getDemoLabel("response.getCharacterEncoding()","charEncoding");
        demoStr+=getDemoLabel("response.getContentCount()","bytes");
        demoStr+=getDemoLabel("response.getContentType()","contentType");
        demoStr+=getDemoLabel("response.getLocale()","locale");
        demoStr+=getDemoLabel("request.getAuthType()","authType");

        demoStr+=getDemoLabel("request.getCharacterEncoding()","charEncoding");
        demoStr+=getDemoLabel("request.getContentLength()","bytes");
        demoStr+=getDemoLabel("request.getContentType()","contentType");
        demoStr+=getDemoLabel("request.getContextPath()","path");
        demoStr+="request.getContextPath().value.ms, ";
        demoStr+=getDemoLabel("request.getLocalAddr()","ip");
        demoStr+=getDemoLabel("request.getLocale()","locale");
        demoStr+=getDemoLabel("request.getLocalName()","localName");
        demoStr+=getDemoLabel("request.getLocalPort()","port");
        demoStr+=getDemoLabel("request.getMethod()","httpMethod");
        demoStr+=getDemoLabel("request.getPathInfo()","path");
        demoStr+=getDemoLabel("request.getPathTranslated()","path");
        demoStr+=getDemoLabel("request.getProtocol()","protocol");
        demoStr+=getDemoLabel("request.getQueryString()","queryStr");
        demoStr+=getDemoLabel("request.getRemoteAddr()","ip");
        demoStr+=getDemoLabel("request.getRemoteHost()","ip");
        demoStr+=getDemoLabel("request.getRemotePort()","port");
        demoStr+=getDemoLabel("request.getRemoteUser()","user");
        demoStr+=getDemoLabel("request.getRequestedSessionId()","sessionid");
        demoStr+=getDemoLabel("request.getRequestURI()","ms");
        demoStr+=getDemoLabel("request.getRequestURL()","ms");
        demoStr+=getDemoLabel("request.getScheme()","scheme");
        demoStr+=getDemoLabel("request.getServerName()","serverName");
        demoStr+=getDemoLabel("request.getServerPort()","port");
        demoStr+=getDemoLabel("request.getServletPath()","path");
        demoStr+=getDemoLabel("request.getUserPrincipal()","user");
        demoStr+=getDemoLabel("request.isRequestedSessionIdFromCookie()","sessionCookie");
        demoStr+=getDemoLabel("request.isRequestedSessionIdValid()","sessionValid");
        demoStr+=getDemoLabel("request.isSecure()","secure");
        demoStr+="request.getScheme().ms as schemeAlias";

        return demoStr;
    }


    private static String getDemoLabel(String label, String units) {
        String str=label+"."+units;// response.getBufferSize().bytes
        str+=", "+label+".value."+units;
        str+=", "+label+".contextpath.value."+units;
        str+=", "+label+".value.contextpath."+units;
        str+=", "+label+".value.url."+units+",";
        return str;
    }

    /** if passed summaryLabel has 'default' in it replace it with defaultString and then
     *  call this classes setSummaryLabels method.  This allows each implementing class to have
     *  different defaults.
     * 
     * @param summaryLabel
     * @param defaultString
     */
    public void setSummaryLabels(String summaryLabel, String defaultString) {
        summaryLabel=replaceDefault(summaryLabel,defaultString);
        setSummaryLabels(summaryLabel);
    }


    /** replace case insensitive 'default' string with passed in string. */
    static String replaceDefault(String summaryLabel, String defaultString) {
        return summaryLabel.replaceAll("(?i)default",defaultString);
    }


    /** Pass a String that has an HttpServletRequest/HttpServletResponse method such as response.getStatus() and then this class
     * will monitor that method call.
     */
    public void addSummaryLabel(String jamonSummaryLabel) {
        if (httpMonItemsHolder!=null) {
            HttpMonItem monItem=createHttpMonItem(jamonSummaryLabel.trim());
            httpMonItemsHolder.add(monItem);
            // concatenate each individual summary label to build a bigger string.  i.e response.getStatus(), request.getRequestURI()
            // Put a comma before every label added but the first.
            if (!"".equals(jamonSummaryLabels)) {
                jamonSummaryLabels+=", ";
            }

            jamonSummaryLabels+=jamonSummaryLabel;
            if (monItem.isTimeMon())// track how many time monitors there are
                numTimeMons++;
        }
    }


    /** Return the number of HttpMonItems that are being monitored for each request */
    int getNumRows() {
        return httpMonItemsHolder.size();
    }

    /** return the number of time monitors (ms.) */
    int getNumTimeMons() {
        return numTimeMons;
    }

    /** allow iteration of HttpMonItems.  Used to start/stop all monitors. */
    Iterator iter() {
        return httpMonItemsHolder.iterator();
    }

    /** Get the passed in summaryLabels. */
    public String getSummaryLabels() {
        return jamonSummaryLabels;
    }



    /** Determine if http params are ignored when creating a jamon label for request.getRequestURI(), and request.getRequestURL().  This is important
     * for jsessionid can make every label passed to jamon nonunique if not enabled.   By default this value is true (ignore params).
     */
    public boolean getIgnoreHttpParams() {
        return ignoreHttpParams;
    }


    /** Set if http params are ignored when creating a jamon label for request.getRequestURI(), and request.getRequestURL().  This is important
     * for jsessionid can make every label passed to jamon nonunique if not enabled.   By default this value is true (ignore params).
     */
    public void setIgnoreHttpParams(boolean ignoreHttpParams) {
        this.ignoreHttpParams=ignoreHttpParams;

    }


    /** Enable/disable http monitoring */
    public void setEnabled(boolean enable) {
        this.enabled=enable;

    }

    /** Determin if http monitoring is enabled */
    public boolean getEnabled() {
        return enabled;
    }



    /** Get the max number of possible HttpMonitors.  By default this is 5000 */
    public int getSize() {
        return size;
    }



    /** Set the max number of possible HttpMonitors.  By default this is 5000.  A value <=0 means there is no limit on the number
     * of jamon records that can be creating due to monitoring.  */
    public void setSize(int size) {
        this.size=size;
    }


    public String getLabelPrefix() {
        return labelPrefix;
    }


    private String[] split(String str) {
        return (str==null) ? null : str.split(",");
    }

    /* Note request will probably implement HttpServletRequest and response HttpServletResponse though this is not strictly required.  This method is
     * called to monitor the request.  usually via another class like the jamon tomcat valve.
     * 
     */
    public HttpMon getMon(Object request, Object response) {
        if (!enabled || jamonSummaryLabels==null)
            return NULL_HTTP_MON;
        else
            return new HttpMonRequest(request, response, this);
    }


    /** Method called to start monitoring a request. */
    public HttpMon start(Object request, Object response) {
        return getMon(request, response).start();
    }


    /* can be overridden in this package to create a different type of httpMonItem (jetty classes do this).  If it is required in the future
     * access could change to protected for this method.
     */
    HttpMonItem createHttpMonItem(String label) {
        return new HttpMonItem(label, this);

    }

}
