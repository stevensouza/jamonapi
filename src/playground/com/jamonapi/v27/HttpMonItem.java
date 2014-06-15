package tomcattester;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;

import com.jamonapi.MonKeyImp;
import com.jamonapi.MonitorFactory;
import com.jamonapi.Monitor;

class HttpMonItem  {

        private boolean isTimeMon;
        private Monitor mon; 
        private String units="noUnitsProvided";
        private String label;
        private String methodName;
        private boolean hasValueLabel=false;
        private boolean isResponse=true;
        private Object objToExecute;
        private Method method;


        private HttpMonItem() {
            
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
        public HttpMonItem(String label) {
            parseLabel(label);
            
        }
        
        
        
        private void parseLabel(String localLabel) {
            String[] parsedLabel=localLabel.split("[.]");
            
            label="com.jamonapi.response.";// default label
           
            for (int i=0;i<parsedLabel.length;i++) {
                
                if ("request".equalsIgnoreCase(parsedLabel[i])) { // request.
                    label="com.jamonapi.request.";
                    isResponse=false;
                } else if ("response".equalsIgnoreCase(parsedLabel[i])) { // response.
                    label="com.jamonapi.response.";
                    isResponse=true;
                } else if (parsedLabel[i].indexOf("()")!=-1) { // response.methodName()
                    label+=parsedLabel[i];
                    methodName=parsedLabel[i].replaceFirst("[(][)]", "");       
                } else if ("value".equalsIgnoreCase(parsedLabel[i])) { // response.methodName().value
                    label+="."+parsedLabel[i]+": ";
                    hasValueLabel=true;
                } else if ("ms".equalsIgnoreCase(parsedLabel[i])) { // convert ms to ms.
                    units="ms.";
                    isTimeMon=true;
                } else { // any thing else is the units
                    units=parsedLabel[i];
                    isTimeMon=false;
                }
                    
                
            }
                            
            
        }
        
        
        public void setMethod() {
            
            try {
              method =  objToExecute.getClass().getMethod(methodName, null);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

        }


        HttpMonItem createInstance(HttpServletRequest request, HttpServletResponse response) {
            HttpMonItem m=new HttpMonItem();
           
            m.isTimeMon=isTimeMon;
            m.units=units;
            m.label=label;
            m.methodName=methodName;
            m.hasValueLabel=hasValueLabel;
            m.isResponse=isResponse;
            
            if (isResponse)
              m.objToExecute=response;
            else
              m.objToExecute=request;
            
            m.setMethod();
            
            return m;
        }
        


        /** Used to create a jamon label like request.getMethod().post */

        public Object getValueLabel() {
          return executeMethod();    
        }
        
        
        // Execute the request or responses method.
        private Object executeMethod() {
            Object retValue=null;
            try {
                retValue = method.invoke(objToExecute, null);// null is noargs.
            } catch (Exception e) {// note I don't want the program to abort due to monitoring so the exception is not being passed upstream
                 e.printStackTrace();
             }
            
            return retValue;
            
        }
        

         
        /** If a value label is to be used then append it to the end of the label, else just return the label.  The results will be used to create
         * a jamon record.  Ex:  request.getMethod() or request.getMethod().post 
         * @return
         */
        public String getLabel() {
            if (hasValueLabel)
                return new StringBuffer(label).append(".").append(getValueLabel()).toString();
            else               
                return label;
        }

        /* Return value to be added to jamon.  Note this is not called when it is a time monitor.  If the object returned is a number
         * then add that value to jamon else just add the number 1. */
        public double getValueToAdd() {
            Object obj=executeMethod();
            if (obj instanceof Number)
              return ((Number)obj).doubleValue();
            else 
              return 1.0;
        }
        
        public String getUnits() {
            return units;
        }
        
        public void start() {
            if (isTimeMon)
                mon=MonitorFactory.start(new MonKeyImp(getLabel(), getUnits()));
        }
        
        public void stop() {
            if (mon!=null && isTimeMon)
               mon.stop();
            else 
               MonitorFactory.add(new MonKeyImp(getLabel(), getUnits()), getValueToAdd());
        }
        
        public String toString() {
            return new StringBuffer("label=").append(label).append(", units=").append(units).append(", methodName=").append(methodName).toString();
        }

        
        public static void main(String[] args) {
            System.out.println(new HttpMonItem("request.getContents().ms."));
            System.out.println(new HttpMonItem("response.getContents().bytes"));
            System.out.println(new HttpMonItem("response.getContents().value.bytes"));
            System.out.println(new HttpMonItem("getContents().value.bytes"));          
            System.out.println(new HttpMonItem("getContents().bytes"));  
             
          }
        

        
}
