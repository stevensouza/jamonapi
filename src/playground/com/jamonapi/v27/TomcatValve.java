package tomcattester;

 
//import org.apache.catalina.valves.RemoteHostValve; final class
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Valve;


import java.io.IOException;
import com.jamonapi.*;
import java.util.*;


public class TomcatValve extends org.apache.catalina.valves.ValveBase {
    
    /* 
     * 

RESPONSE

value, HttpServletResponse, bytes        response.getBufferSize()=8192
value, HttpServletResponse, charEncoding         response.getCharacterEncoding()=utf-8
, HttpServletResponse, bytes         response.getContentCount()=985
value, HttpServletResponse, type         response.getContentType()=text/html;charset=utf-8
               response.getContentType().value.type
value, HttpServletResponse, type         response.getLocale()=en_US

valve=org.apache.catalina.connector.Request, org.apache.catalina.connector.Response

default      , Response, bytes,  response.getContentCount()=24578
     , Response, bytes, response.getContentLength()=-1
value         response.getIncluded()=false
value         response.getInfo()=org.apache.coyote.tomcat5.CoyoteResponse/1.0
value        response.getMessage()=/jamon/ZZZZ
default, value         response.getStatus()=404


REQUEST

          request.getAuthType()=null
          request.getCharacterEncoding()=null
          request.getContentLength()=-1
          request.getCharacterEncoding()=null
          request.getContentType()=null
          request.getContextPath()=/jamon
          request.getLocalAddr()=127.0.0.1
          request.getLocale()=en_US
          request.getLocalName()=localhost
          request.getLocalPort()=8080
          request.getMethod()=GET
          request.getPathInfo()=null
          request.getPathTranslated()=null
          request.getProtocol()=HTTP/1.1
          request.getQueryString()=name=steve%20souza&id=9898
          request.getRemoteAddr()=127.0.0.1
          request.getRemoteHost()=127.0.0.1
          request.getRemotePort()=1454
          request.getRemoteUser()=null
          request.getRequestedSessionId()=670BFE2B4A7C7C77D9825EFA753D2058
          request.getRequestURI()=/jamon/ZZZZ
          request.getRequestURL()=http://localhost:8080/jamon/ZZZZ
          request.getScheme()=http
          request.getServerName()=localhost
          request.getServerPort()=8080
          request.getServletPath()=/ZZZZ
          request.getUserPrincipal()=null
          request.isRequestedSessionIdFromCookie()=true
          request.isRequestedSessionIdFromURL()=false
          request.isRequestedSessionIdValid()=false
          request.isSecure()=false
          
          valve request only
          
                    request.getDecodedRequestURI()=/jamon/ZZZZ
          request.getInfo()=org.apache.coyote.catalina.CoyoteRequest/1.0
                    request.getPrincipal()=null



    */

    
         /**
          * Extract the desired request property, and pass it (along with the
          * specified request and response objects) to the protected
          * <code>process()</code> method to perform the actual filtering.
          * This method must be implemented by a concrete subclass.
          *
          * @param request The servlet request to be processed
          * @param response The servlet response to be created
          *
          * @exception IOException if an input/output error occurs
          * @exception ServletException if a servlet error occurs
          * http://www.jdocs.com/tomcat/5.5.17/org/apache/catalina/valves/RequestFilterValve.html
          * 
          * log response, request to see what they do.
          * debug mode?
          * test xml - read property
          */
    
    
    
     
     private static final String PREFIX="com.jamonapi.tomcat.JAMonValve.";
     private HttpMonFactory httpMonFactory=new HttpMonFactory();
       
     private static final String jamonSummaryLabels="request.getRequestURI(), response.getContentCount(), response.getStatus()";
     private static final String jamonDetailFields="request.getRequestURI(), response.getContentCount(),response.getStatus()";
     
     public TomcatValve() {
         setSummaryLabels(jamonSummaryLabels);
     }
     
     public void setSummaryLabels(String jamonSummaryLabels) {
         httpMonFactory.setSummaryLabels(jamonSummaryLabels);
     }

     
     public String getSummaryLabels() {
         return httpMonFactory.getSummaryLabels();
     }

     public void setDetailFields(String jamonDetailFields) {
         httpMonFactory.setDetailFields(jamonDetailFields);

     }
     
  

     
     public String getDetailFields() {
         return httpMonFactory.getDetailFields();
     }
     
     private String[] split(String str) {
         return (str==null) ? null : str.split(",");
     }


      public String getInfo() {
          return "com.jamonapi.http.JAMonValve";
      }
      
      
      
      public void invoke(Request request, Response response) throws IOException, ServletException  {
        HttpMon httpMon=null;

        try {
            httpMon=httpMonFactory.getMon(request, response);
            httpMon.start();
            
            Valve nextValve=getNext();
            if (nextValve!=null)
              nextValve.invoke(request, response);
            
        } finally {
            
            if (httpMon!=null)
              httpMon.stop();
            
        }

       
                       
    }
     
      



}
