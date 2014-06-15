package tomcattester;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.servlet.http.*;


public class HttpMonFactory {

    private String jamonSummaryLabels="request.getRequestURI().value.ms, request.getRequestURI().ms, response.getContentCount(), response.getStatus()";
    private String jamonDetailFields="request.getRequestURI(), response.getContentCount(),response.getStatus()";
    private Collection jamonSummary;
    private Collection jamonDetails;

//    private final Collection monitorList=new ArrayList();
   // private final MonitorComposite monComposite;
    
    
//    public HttpMonFactory () {
//      HttpMonItem monItem=  new HttpMonItem() {
//      
//      public double getValueToAdd() {
//          if (response instanceof Response)
//            return ((Response)response).getContentCount();
//          else 
//            return super.getValueToAdd();
//      }
//      
//  };
//    }
    
    public HttpMonFactory() {
        
    }
    
//    private void initialize() {
//        // page hits do a start
//        Iterator iter=jamonSummary.iterator();
//        
//        while (iter.hasNext()) {
//            HttpMonItem monItem=(HttpMonItem)iter.next();
//            monitorList.add(monItem.createInstance());
//        }
//        
//    }
    
    
    public void setSummaryLabels(String jamonSummaryLabels) {
        this.jamonSummaryLabels=jamonSummaryLabels;
        this.jamonSummary=new ArrayList();
        
        String[] summaryLabelsArr=split(jamonSummaryLabels);
        for (int i=0;i<summaryLabelsArr.length;i++) {
            addSummaryLabel(summaryLabelsArr[i]);
        }

    }
    
    public void addSummaryLabel(String jamonSummaryLabel) {
        if (jamonSummaryLabel!=null)
            jamonSummary.add(new HttpMonItem(jamonSummaryLabel.trim()));
    }
    
    public String getSummaryLabels() {
        return jamonSummaryLabels;
    }


    


    

    public void setDetailFields(String jamonDetailFields) {
        this.jamonDetailFields=jamonDetailFields;
 //       setDetailFields(split(jamonDetailFields));
    }
    
    public String getDetailFields() {
        return jamonDetailFields;
    }
    
    private String[] split(String str) {
        return (str==null) ? null : str.split(",");
    }
    
    public HttpMon getMon(HttpServletRequest request, HttpServletResponse response) {
        
        HttpMon httpMon = new HttpMon();
        if (jamonSummary==null)
          return httpMon;
      
        Iterator iter=jamonSummary.iterator();
        
        while (iter.hasNext()) {
            HttpMonItem monItem=(HttpMonItem) iter.next();
            httpMon.addMon(monItem.createInstance(request, response));
        }
        
        return httpMon;
        
    }


}
