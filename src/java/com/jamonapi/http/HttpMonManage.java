package com.jamonapi.http;

/** Interface for controlling what http request/response info that should be monitored */
interface HttpMonManage {

    public void setSummaryLabels(String jamonSummaryLabels);

    public String getSummaryLabels();

    public void addSummaryLabel(String jamonSummaryLabel);

    /**
     * Containers (tomcat/jetty etc) put jessionid (and other params) as part of what is returned by HttpServletRequest.getRequestURI, and HttpServletRequest.getRequestURL.
     * This can make many pages not unique enough to benefit from jamon, so by default this part of the url is removed from the monitoring label.
     * Example this: /myapp/mypage.jsp;jsessionid=320sljsdofou
     * becomes this in the jamon label: /myapp/mypage.jsp
     * 
     * getIgnoreHttpParams() - return if this is enabled or disabled (true means the params will be removed/ignored. This is the default behaviour)
     * setIgnoreHttpParams(boolean httpIgnoreParams) - set whether it is enabled or disabled (true means the params will be removed/ignored.  This is the default behaviour)
     * 
     */
    public boolean getIgnoreHttpParams();

    /**
     * Containers (tomcat/jetty etc) put jessionid (and other params) as part of what is returned by HttpServletRequest.getRequestURI, and HttpServletRequest.getRequestURL.
     * This can make many pages not unique enough to benefit from jamon, so by default this part of the url is removed from the monitoring label.
     * Example this: /myapp/mypage.jsp;jsessionid=320sljsdofou
     * becomes this in the jamon label: /myapp/mypage.jsp
     * 
     * getIgnoreHttpParams() - return if this is enabled or disabled (true means the params will be removed/ignored. This is the default behaviour)
     * setIgnoreHttpParams(boolean httpIgnoreParams) - set whether it is enabled or disabled (true means the params will be removed/ignored.  This is the default behaviour)
     * 
     */
    public void setIgnoreHttpParams(boolean ignoreHttpParams);

    /** Set maximum number of rows that can be in jamon before no more records are added.  This will prevent jamon from growing unbounded */
    public void setSize(int size);

    public int getSize();

    /** Enable/disable monitoring.  Would be better to name them enable and isEnabled, but as far as I could tell tomcat can only initialize
     * getter and setter methods.*/
    public void setEnabled(boolean enable);

    public boolean getEnabled();

}
