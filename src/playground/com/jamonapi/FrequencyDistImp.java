/** 
 * 
 *
 * Created on January 22, 2006, 11:11 AM
 */

package com.jamonapi;



import java.util.List;

 
//abstract class FrequencyDistImp extends BaseStatsImp implements FrequencyDist, RowData {

abstract class FrequencyDistImp extends MonitorImp implements FrequencyDist {
//    protected String name;
//    protected String displayHeader;
    protected double endValue;
//    protected boolean trackActivity=false;
    
     public List getBasicHeader(List header) {
         // Frequencies don't get displayed basic headers.
         return header;
     }

    
    
     public List getHeader(List header) {
    	 super.getHeader(header);
//         header.add(name+"_Hits");
//         header.add(name+"_Avg");
//         header.add(name+"_Total");
//         header.add(name+"_StdDev");
//         header.add(name+"_LastValue");
//         header.add(name+"_Min");
//         header.add(name+"_Max");
//         header.add(name+"_FirstAccess");
//         header.add(name+"_LastAccess");
         header.add(name+"AvgActive");
         header.add(name+"AvgPrimaryActive");
         header.add(name+"AvgGlobalActive");
        
         return header;
         
     }   
     
     public List getDisplayHeader(List header) {
         header.add(displayHeader);
         return header;
     }      

    public List getBasicRowData(List rowData) {
        // This is not called as basic rowdata doesn't inlcude frequencydists
         return rowData;
     }
     
     
    public List getRowData(List rowData) {
    	super.getRowData(rowData);
       
//         rowData.add(new Double(getHits()));
//         rowData.add(new Double(getAvg()));
//         rowData.add(new Double(getTotal()));
//         rowData.add(new Double(getStdDev()));
//         rowData.add(new Double(getLastValue()));
//         rowData.add(new Double(getMin()));
//         rowData.add(new Double(getMax())); 
//         rowData.add(getFirstAccess()); 
//         rowData.add(getLastAccess()); 
         rowData.add(new Double(getAvgActive()));
         rowData.add(new Double(getAvgPrimaryActive()));
         rowData.add(new Double(getAvgGlobalActive()));
        
         return rowData;
        

        
    }
    
    public List getRowDisplayData(List rowData) {
          rowData.add(toString());
          return rowData;
          
    }
 
    public double getEndValue() {
            return endValue;
    }   


//     
//     abstract public void setActivityStats(ActivityStats activityStats);
//
//     public void setActivityTracking(boolean trackActivity) {
//         this.trackActivity=trackActivity;
//     }    
//     
//
//    
     
     
}
