package com.jamontomcat;


import com.jamonapi.http.HttpMon;
import com.jamonapi.http.HttpMonFactory;
import org.apache.catalina.Valve;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;

import javax.servlet.ServletException;
import java.io.IOException;

/** Note this is simply a copy of com.jamonapi.http.JAMonTomcatValve. Tomcat 5.5 would not work with the valve in the same jar as the jamon classes
 * and also display stats in jamon.war.  Simply compile this class separately and put it in tomcats /server/classes/com/jamontomcatvalve/http and put jamon-2.7.jar or higher in tomcats
 * common/lib directory.   Note this class should also work in tomcat 6 although it is easier simply to put jamon-2.7.jar (or higher in the server/lib)
 * for tomcat 6.
 * 
 * <pre>
 * {@code
 *  <Valve className="com.jamonapi.http.JAMonTomcatValve"/>
 *   same as default above
 *    <Valve className="com.jamonapi.http.JAMonTomcatValve" summaryLabels="request.getRequestURI().ms, response.getContentCount().bytes, response.getStatus().value.httpStatus">
 *  <Valve className="com.jamonapi.http.JAMonTomcatValve  summaryLabels="request.getRequestURI().ms, request.getRequestURI().value.ms, response.getContentCount().pageBytes,response.getStatus().httpStatusCode, response.getStatus().value.httpStatusCode,     response.getContentType().value.type"/>
 * }</pre>
 *
 */


//START tomcat 4/5

public class JAMonTomcat55Valve extends org.apache.catalina.valves.ValveBase {
    private static final String PREFIX="com.jamontomcat.http.JAMonTomcat55Valve";
    private static final String DEFAULT_SUMMARY="default, response.getContentCount().bytes, response.getStatus().value.httpStatus, request.contextpath.ms";
    HttpMonFactory httpMonFactory=new HttpMonFactory(PREFIX);

    private final String jamonSummaryLabels="default";

    public JAMonTomcat55Valve() {
        setSummaryLabels(jamonSummaryLabels);
    }


    /**
     * Extract the desired request property, and pass it (along with the
     * specified request and response objects) to the protected
     * <code>process()</code> method to perform the actual filtering.
     * This method must be implemented by a concrete subclass.
     * 
     * http://www.jdocs.com/tomcat/5.5.17/org/apache/catalina/valves/RequestFilterValve.html
     *
     * @param request The servlet request to be processed
     * @param response The servlet response to be created
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
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
