package com.jamonapi.http;


import com.jamonapi.*;
import com.jamonapi.utils.Misc;

import java.lang.reflect.Method;

/** Stateless class used by HttpMonRequest to represent anything a request is monitoring.
 * There will be one HttpMonItem in HttpMonFactory for each thing we are monitoring.  The state information
 * needed for monitoring a particular request is kept in HttpMonRequest.  Note any of the mehtods that take
 * a HttpMonRequest need some form of state associated with the request.  By making HttpMonRequest stateful
 * and HttpMonItem stateless a significant number of object creations were saved.
 * 
 * @author Steven Souza
 *
 */
class HttpMonItem  {

    private boolean isTimeMon;//used in JettyHttpMonItem
    private String units="noUnitsProvided";// units used by jamon
    private String label;// label used as a base by jamon
    private String methodName;// method name that monitoring calls. getStatus, getRequestURI etc.
    // u=url, v=value. s=summary (http 2xx etc) possibilities are empty, u,v,uv,vu (as well as s)
    // s does a mod and is meant only for http status values to put 2xx, 3xx etc in the key.  It will be a noop for values
    // other than Numeric and not return an error.
    private String additionToLabel="";
    private boolean isResponse=true;// Are we monitoring an HttpServletRequest or an HttpServletResponse
    private Method method;

    // containers put jessionid (and other params) as part of what is returned by getRequestURI, and getRequestURL.  This can make
    // many pages not unique enough to benefit from jamon, so by default this part of the url is removed from the monitoring label
    // i.e. /myapp/mypage.jsp;jsessionid=320sljsdofou
    //  becomes the following to jamon
    //   /myapp/mypage.jsp
    private boolean removeHttpParams=false;// remove httpParams of request from jamon label.
    private static final Monitor NULL_MON=new NullMonitor();// used when disabled

    HttpMonItem() {
    }


    /** Valid constructor arguments are case insensitive, and have to start with request, response to differentiate whether to use an object
     * that inhertis from HttpServletRequest or HttpServletResponse respectively.  This must be follwed by the methodname, units,  an optional '.value',
     * and a units.  The method name will be executed.  Note a unit of 'ms' would cause a timed monitor to be called.
     * 
     * Examples:    request.getRequestURI().ms, request.getRequestURI().ms.value, request.getRequestURI().myUnits
     *              response.getBufferSize().bytes, response.getBufferSize().bytes.value
     *
     * @param label
     */
    HttpMonItem(String label, HttpMonFactory httpMonFactory) {
        parseLabel(label, httpMonFactory);

    }

    // parse passed in string and build HttpMonItems from it to be used to monitor
    // The request. Note general form is request or response followed by a method name and units.
    // Special defined tokes are value, contextpath, url, summary (for http status 4xx etc)  and units
    private void parseLabel(String localLabel, HttpMonFactory httpMonFactory) {
        String colAlias=colAlias(localLabel); // brings back anything after 'as', if it exists, null otherwise:  request.getMethod().bytes as ColName
        String nonAlias=nonAlias(localLabel); // brings back what is before 'as alias'.  i.e. request.getMethod().bytes
        String[] parsedLabel=nonAlias.split("[.]");
        label=httpMonFactory.getLabelPrefix()+".response.";// default label

        for (int i=0;i<parsedLabel.length;i++) {
            String token=parsedLabel[i].trim();

            if ("request".equalsIgnoreCase(token)) { // request.
                label=httpMonFactory.getLabelPrefix()+".request.";
                isResponse=false;
            } else if ("response".equalsIgnoreCase(token)) { // response.
                label=httpMonFactory.getLabelPrefix()+".response.";
                isResponse=true;
            } else if (token.indexOf("()")!=-1) { // response.methodName()
                label+= (colAlias==null) ? token : colAlias;// if this is of the format:  response.methodName().value.ms as aliasName then use aliasName in jamon label
                methodName=token.replaceFirst("[(][)]", "");
            } else if ("value".equalsIgnoreCase(token)) { // response.methodName().value
                additionToLabel+="v";// indicates value
            }  else if ("summary".equalsIgnoreCase(token)) { // response.getStatus().summary - does a mod 100  - only applicable to http status
                // This is a bit of a hack.  note that label doesn't concatenate here, but replaces.  This means that it must
                // provaide getStatus() and also shows summary only applies to http status.  This was done so jmx didn't have to worry
                // about the differences between different package names being part of the key (say jetty and tomcat).  In the future
                // i could resolve this by getting rid of keynames like 'com.jamonapi.http.JAMonJettyHandlerNew.response.getStatus().summary: 5xx, httpStatus'
                // and instead using something more generic like com.jamonapi.http.response.getStatus().summary'  I would have done this but was
                // afraid to break other developers code that was dependent on this keyName.
                label = "com.jamonapi.http.response.getStatus()";
                additionToLabel+="s";// indicates httpsummary should be added to the key label i.e. summary: 4xx
            } else if ("url".equalsIgnoreCase(token)) { // response.methodName().url (note both value and url can be on the same line)
                additionToLabel+="u";// indicates url
            } else if ("contextpath".equalsIgnoreCase(token)) { // response.methodName().url (note both value and url can be on the same line)
                additionToLabel+="p";// indicates path such as /jamon
            } else if ("ms".equalsIgnoreCase(token)) { // convert ms to ms.  a unit of 'ms.' causes start/stop to be called (i.e. it is a time monitor)
                units="ms.";
                isTimeMon=true;
            } else { // any thing else is the units
                units=token;
                isTimeMon=false;
            }
        }

        // gets rid of jsessionid from jamon summary labels if ignorehttpparams is true and we are monitoring the pagehits.
        if (httpMonFactory.getIgnoreHttpParams() && ("getRequestURI".equals(methodName) || "getRequestURL".equals(methodName)))
            removeHttpParams=true;

    }

    String getUnits() {
        return units;
    }

    boolean isResponse() {
        return isResponse;
    }


    boolean isTimeMon() {
        return isTimeMon;
    }

    // This method checks to see if the size threshold has been exceeded.  If this is a time monitor and the threshold
    // has not been exceeded or if the monitor of the given time has already been created once
    // then a time monitor is started.  The method is called by HttpMonRequest for each item that is being monitored.  Due to the
    // fact that this class has no state it is being passed in via the HttpMonRequest object.
    void start(HttpMonRequest httpMonBase) {
        if (isTimeMon) {
            Monitor timeMon=null;
            // if this element puts number of monitors over the threshold size then don't create a new monitor.
            if (sizeThresholdExceeded(httpMonBase)) {
                createSizeThresholdExceededMon(httpMonBase);
                timeMon=NULL_MON;
            }

            if (timeMon==null) {
                timeMon=startTimeMon(httpMonBase);
            }

            httpMonBase.setTimeMon(timeMon);
        }
    }


    // Stop any started monitors.
    void stop(HttpMonRequest httpMonBase) {
        if (isTimeMon) {
            stopTimeMon(httpMonBase);
        } else if (sizeThresholdExceeded(httpMonBase)) {// note thresholds indicators have already been created for time monitors
            createSizeThresholdExceededMon(httpMonBase);
        } else {
            MonitorFactory.add(getMonKey(httpMonBase), getValueToAdd(httpMonBase));
        }
    }


    // This method is needed as the jetty version of HttpMonItem has to track time differently.
    Monitor startTimeMon(HttpMonRequest httpMonBase) {
        return MonitorFactory.start(getMonKey(httpMonBase));
    }

    // This method is also needed as the jetty version of HttpMonItem tracks time differently
    void stopTimeMon(HttpMonRequest httpMonBase) {
        httpMonBase.stopTimeMon();
    }

    private boolean sizeThresholdExceeded(HttpMonRequest httpMonBase) {
        // if number of jamon rows is greater than configured size (as well as size has been defined i.e. > 0) then don't create a monitor.
        // if the monitor already exists then you can proceed as no more monitors will be created.
        return MonitorFactory.getNumRows()>httpMonBase.getSize() && httpMonBase.getSize()>0 && !MonitorFactory.exists(getMonKey(httpMonBase));
    }

    // Track anytime the monitor threshold has been exceeded.
    private void createSizeThresholdExceededMon(HttpMonRequest httpMonBase) {
        String label=new StringBuffer(httpMonBase.getLabelPrefix()).append(".HttpMonFactory.sizeExceeded.").append(httpMonBase.getSize()).toString();
        String detailLabel=new StringBuffer(getLabel(httpMonBase)).append(", ").append(httpMonBase.getRequestURI()).append(", ").append(getUnits()).toString();
        MonitorFactory.add(new MonKeyImp(label, detailLabel, "Count"), httpMonBase.getSize());
    }



    // return key.  note we are passing in details that can be used in the detail buffer.  Details consist of the uri as well as a stack trace
    // if one occured.
    MonKey getMonKey(HttpMonRequest httpMonBase) {
        return new MonKeyImp(getLabel(httpMonBase), httpMonBase.getDetailLabel(), getUnits());
    }

    /** Append the key if needed (for example with something like .value (value: /jamon/jamonadmin.jsp)) or
     *  .summary (summary: 4xx), or simply return the label.  The results will be used to create
     * a jamon monitor.  Ex:  request.getMethod() or request.getMethod().post (uses value)
     *
     * @return jamon monitor label
     */
    String getLabel(HttpMonRequest httpMonBase) {
        if ("".equals(additionToLabel))
            return label;

        return appendToLabel(httpMonBase);
    }

    private String appendToLabel(HttpMonRequest httpMonBase) {
        // add value, summary and/or url if they are required in the label.
        StringBuffer sb=new StringBuffer(label);
        if ( !(label.charAt(label.length()-1)=='.'))// in some cases if this isn't done then the label has no dots, or 2 consecutive ones.
            sb.append(".");

        for (int i=0;i<additionToLabel.length();i++) {
            if (i>=1)// i.e. multiple additions are specified such as u and v
                sb.append(", ");

            if (additionToLabel.charAt(i)=='v')//value. ex1: value: post, ex2: value: 404
                sb.append("value: ").append(getValueLabel(httpMonBase));
            else if (additionToLabel.charAt(i)=='s')//summary ex1: summary: 2xx, ex2: summary: 4xx
                sb.append("summary: ").append(getHttpStatusSummaryLabel(httpMonBase));
            else if (additionToLabel.charAt(i)=='u')//url
                sb.append("url: ").append(httpMonBase.getKeyReadyURI());
            else if (additionToLabel.charAt(i)=='p')//path
                sb.append("contextpath: ").append(httpMonBase.getContextPath());

        }

        return sb.toString();
    }


    /** Used to create a jamon label like request.getMethod().post */
    Object getValueLabel(HttpMonRequest httpMonBase) {
        Object valLabel=executeMethod(httpMonBase);
        if (removeHttpParams)
            valLabel=removeHttpParams(valLabel);

        return valLabel;
    }

    // use mod on the returned http status code (i.e. 404) to return one of the following: 1xx, 2xx, 3xx, 4xx, 5xx
    String getHttpStatusSummaryLabel(HttpMonRequest httpMonBase) {
        Object status=executeMethod(httpMonBase);
        if (status instanceof Number) {
            int httpStatusCode = ((Number) status).intValue();
            int httpStatusSummary = httpStatusCode/100; // take something like http status 404 and convert to the first digit i.e. 4.
            return String.valueOf(httpStatusSummary)+"xx"; // 4xx
        } else {
            return "";
        }
   }


    /* Return value to be added to jamon.  Note this is not called when it is a time monitor.  If the object returned is a number
     * then add that value to jamon else just add the number 1. */
    double getValueToAdd(HttpMonRequest httpMonBase) {
        Object obj=executeMethod(httpMonBase);
        if (obj instanceof Number)
            return ((Number)obj).doubleValue();
        else
            return 1.0;
    }

    // Execute the request or responses method.
    private Object executeMethod(HttpMonRequest httpMonBase) {
        Object retValue=null;
        try {
            retValue = getMethod(httpMonBase).invoke(getObjectToExecute(httpMonBase), (Object[])null);// null is noargs.
        } catch (Throwable e) {// note I don't want the program to abort due to monitoring so the exception is not being passed upstream
            MonitorFactory.add(new MonKeyImp(httpMonBase.getLabelPrefix()+".monError", new Object[]{this, Misc.getExceptionTrace(e)}, "Exception"),1);
        }

        return retValue;

    }

    // return method that we are getting monitoring info from.
    Method getMethod(HttpMonRequest httpMonBase) {
        try {
            if (method==null)
                method = getObjectToExecute(httpMonBase).getClass().getMethod(methodName, (Class[])null);
        } catch (NoSuchMethodException e) {
        }

        return method;

    }

    // get the object we are calling the monitoring method from
    Object getObjectToExecute(HttpMonRequest httpMonBase) {
        if (isResponse())
            return httpMonBase.getResponse();
        else
            return httpMonBase.getRequest();
    }



    @Override
    public String toString() {
        return new StringBuffer("label=").append(label).append(", units=").append(units).append(", methodName=").append(methodName).toString();
    }


    /** get alias portion of 'request.getRequestURI() AS myAlias.
     * this is used to put a more descriptive name in jamon.
     * @VisibleForTesting
     */
    protected  static String colAlias(String str) {
        if (str==null)
            return null;

        String[] arr=str.split(" [aA][sS] ");
        if (arr.length==2) // returns pageHits when 'request.getRequestURI() as pageHits' is passed
            return arr[1].trim();
        else
            return null;  // returns request.getRequestURI() (no alias was used)
    }

    /** return nonalias portion of string.
     *     @VisibleForTesting
     */
    protected static String nonAlias(String str) {
        if (str==null)
            return null;

        String[] arr=str.split(" [aA][sS] ");
        return arr[0].trim();  // returns request.getRequestURI() (no alias was used)

    }

    /** passing this: http://www.mypage.com:8080/page;jsessionid=5akalsdjfflj;other=9s0f0udsf?pageName=my.jsp
     *  would return this: http://www.mypage.com:8080/page used to remove jsessionid from page hits.
     * @VisibleForTesting
     * @param url
     * @return
     */
    protected static String removeHttpParams(Object url) {
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
