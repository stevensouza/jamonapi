package tomcattester;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


public class HttpMon {
    
//    
//    private static final String PREFIX="com.jamonapi.tomcat.JAMonValve.";
//    private String jamonSummaryLabels="request.getRequestURI(), response.getContentCount(), response.getStatus()";
//    private String jamonDetailFields="request.getRequestURI(), response.getContentCount(),response.getStatus()";
//    private Collection jamonSummary;
//    private Collection jamonDetails;
   // private final HttpServletRequest request;
   // private final HttpServletResponse response;
    private final Collection monitorList=new ArrayList();
   // private final MonitorComposite monComposite;
    HttpMon() {
        //this.request=request;
        //this.response=response;
        //initialize();

        
    }
    
    void addMon(HttpMonItem monItem) {
        monitorList.add(monItem);
    }
    
    public HttpMon start() {
        Iterator iter=monitorList.iterator();
        
        while (iter.hasNext()) {
            HttpMonItem monItem=(HttpMonItem) iter.next();
            monItem.start();
        }
        
        return this;
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
//    
//    
//    public void setSummaryLabels(String jamonSummaryLabels) {
//        this.jamonDetailFields=jamonSummaryLabels;
//        this.jamonSummary=new ArrayList();
//        
//        String[] summaryLabelsArr=split(jamonSummaryLabels);
//        for (int i=0;i<summaryLabelsArr.length;i++) {
//            addSummaryLabel(summaryLabelsArr[i]);
//        }
//
//    }
//    
//    public void addSummaryLabel(String jamonSummaryLabel) {
//        if (jamonSummaryLabel!=null)
//            jamonSummary.add(PREFIX+jamonSummaryLabel.trim());
//    }
//    
//    public String getSummaryLabels() {
//        return jamonSummaryLabels;
//    }
//
//    void delme() {
//        Collection possibleMonitors=new ArrayList();
//    //    possibleMonitors.add(new HttpMonItem("request.getRequestURI()", "ms."));
//        
//    
//      HttpMonItem monItem=  new HttpMonItem() {
//             
//             public double getValueToAdd() {
//                 return response.getContentCount();
//             }
//             
//         };
//         
//        
//        
//         
//         }
//    
//
//
//    
//
//    public void setDetailFields(String jamonDetailFields) {
//        this.jamonDetailFields=jamonDetailFields;
// //       setDetailFields(split(jamonDetailFields));
//    }
//    
//    public String getDetailFields() {
//        return jamonDetailFields;
//    }
//    
//    private String[] split(String str) {
//        return (str==null) ? null : str.split(",");
//    }

    

    public void stop() {
        Iterator iter=monitorList.iterator();
        
        while (iter.hasNext()) {
            HttpMonItem monItem=(HttpMonItem) iter.next();
            monItem.stop();
        }
        
        // page hits do a stop
    }



//private static final class HttpMonItem {
//    private String label=null;
//    private String units=null;
//    private String monLabel;
//    HttpMonItem(String label, String units) {
//        this.label=label;
//        this.monLabel=label.replaceFirst(".value", "");
//        this.units=units;
//    }
//
//    
//    HttpMonItem createInstance() {
//        return this;
//        
//    }
//    
//    void stop() {
//        MonitorFactory.add(new MonKeyImp(getMonLabel(), units), getValueToAdd());
//    }
//    
//    double getValueToAdd() {
//        return 1;
//    }
//    
//
//    private String getMonLabel() {
//       return monLabel;    
//    }
//    
//    String getLabel() {
//        return label;
//    }
//}
//    

}
