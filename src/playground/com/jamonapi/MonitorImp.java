package com.jamonapi;

/**
 * Main workhorse for monitors.
 *
 * Created on January 21, 2006, 6:08 PM
 */





import java.util.List;

 abstract class MonitorImp extends Monitor implements RowData  {

        
     
    public List getBasicHeader(List header) {
        key.getBasicHeader(header);
        getThisData(header);
        
        return header;
     }    
     
     private List getThisData(List header) {
        header.add(name+"Hits");
        header.add(name+"Avg");
        header.add(name+"Total");
        header.add(name+"StdDev");
        header.add(name+"LastValue");
        header.add(name+"Min");
        header.add(name+"Max");
        header.add(name+"Active");
        header.add(name+"AvgActive");
        header.add(name+"MaxActive");
        header.add(name+"FirstAccess");
        header.add(name+"LastAccess");
        header.add(name+"Enabled");
        header.add(name+"Primary");       
        return header;
     }
   
     public List getHeader(List header) {
        key.getHeader(header);
        getThisData(header);
        if (range!=null)
         range.getHeader(header);

        return header;
    }    
     
     public List getDisplayHeader(List header) {
         key.getDisplayHeader(header);
         getThisData(header);
         if (range!=null)
        	 range.getDisplayHeader(header);

         return header;
     }
     

    public List getBasicRowData(List rowData) {
      key.getBasicRowData(rowData);
      return getThisRowData(rowData);
    }
    
    private List getThisRowData(List rowData) {
      rowData.add(new Double(getHits()));
      rowData.add(new Double(getAvg()));
      rowData.add(new Double(getTotal()));
      rowData.add(new Double(getStdDev()));
      rowData.add(new Double(getLastValue()));
      rowData.add(new Double(getMin()));
      rowData.add(new Double(getMax())); 
      rowData.add(new Double(getActive())); 
      rowData.add(new Double(getAvgActive())); 
      rowData.add(new Double(getMaxActive())); 
      rowData.add(getFirstAccess()); 
      rowData.add(getLastAccess()); 
      rowData.add(new Boolean(isEnabled()));
      rowData.add(new Boolean(isPrimary()));
      
      return rowData;
      
        
    }
    
    public List getRowData(List rowData) {
      key.getRowData(rowData);
      getThisRowData(rowData); 

      if (range!=null)      
        range.getRowData(rowData);
      
      return rowData;
      
    }        

     
    public List getRowDisplayData(List rowData) {
      key.getRowDisplayData(rowData);
      getThisRowData(rowData);   
      
      if (range!=null)       
        range.getRowDisplayData(rowData);
      
      return rowData;
    }    
    
    public RangeHolder getRangeHolder() {
    	RangeImp r=(RangeImp) getRange();
    	if (r!=null)
          return r.getRangeHolder();
    	else
    	  return null;
    }
    
    void setActivityStats(ActivityStats activityStats) {
    	this.activityStats=activityStats;
    }

}
