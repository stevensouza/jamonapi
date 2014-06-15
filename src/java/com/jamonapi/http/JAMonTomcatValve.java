package com.jamonapi.http;



import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.catalina.Valve;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;

/** This valve works in tomcat 6 and jboss tomcat 5.5.  An alternative approach is to use the jamontomcat-2.7.jar and this approach is required for
 *  tomcat 4/5. The Valve architecture and signatures were changed between release 5 and 5.5. For tomcat 5.5
 *  this class should work in tomcats version of 5.5 but doesn't due to classloader issues.  Instead put com.jamontomcatvalve.http.JAMonTomcat55Valve
 *  in your server/classes/com/jamontomcat/http directory and put jamon's jar in common/lib.  This approach should also work in tomcat 6
 *  though I didn't try it. This is a wrapper class for the true monitoring class of HttpMonFactory.
 * 
 *  <p>Note</p>
 * <pre>{@code
 *
 *  <Engine name="Catalina" defaultHost="localhost" debug="0">
 *    <Valve className="com.jamonapi.http.JAMonTomcatValve"/>
 *    <Valve className="com.jamontomcat.JAMonTomcat5Valve" size="10000" summaryLabels="default"/>
 *    <Valve className="com.jamonapi.http.JAMonTomcatValve" summaryLabels="request.getRequestURI().ms, response.getContentCount().bytes, response.getStatus().value.httpStatus">
 *    <Valve className="com.jamonapi.http.JAMonTomcatValve  summaryLabels="request.getRequestURI().ms, request.getRequestURI().value.ms, response.getContentCount().pageBytes,response.getStatus().httpStatusCode, response.getStatus().value.httpStatusCode, response.getContentType().value.type"/>
 *    ...
 * }</pre>
 */


public class JAMonTomcatValve extends org.apache.catalina.valves.ValveBase implements HttpMonManage {

    private static final String PREFIX="com.jamonapi.http.JAMonTomcatValve";
    private static final String DEFAULT_SUMMARY="default, response.getContentCount().bytes, response.getStatus().value.httpStatus, request.contextpath.ms";

    private HttpMonFactory httpMonFactory=new HttpMonFactory(PREFIX);

    private final String jamonSummaryLabels="default";

    public JAMonTomcatValve() {
        setSummaryLabels(jamonSummaryLabels);
    }


    /**
     * Extract the desired request property, and pass it (along with the
     * specified request and response objects) to the protected
     * {@code process()} method to perform the actual filtering.
     * This method must be implemented by a concrete subclass.
     *
     * @param request The servlet request to be processed
     * @param response The servlet response to be created
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     * http://www.jdocs.com/tomcat/5.5.17/org/apache/catalina/valves/RequestFilterValve.html
     * 
     */

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException  {
        HttpMon httpMon=null;
        try {
            httpMon=httpMonFactory.start(request, response);

            Valve nextValve=getNext();
            if (nextValve!=null)
                nextValve.invoke(request, response);

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

    @Override
    public String getInfo() {
        return PREFIX;
    }


}
