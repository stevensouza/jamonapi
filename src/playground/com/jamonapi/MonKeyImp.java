package com.jamonapi;

/**
 * <p>A key implmentation for label, and units type monitors.
 * Note this could also be implemented with the following use of MonKeyBase.  This
 * class predates that one and would not have to use a Map for basic functions
 * and so MAY be more efficient (this wasn't tested).  Using label, and units
 * is the most common monitor that will be used in most cases.</p>
 * 
 *  <p>This could be implemented like the following.
 *  LinkedHashMap lm=new LinkedHashMap();<br>
 *  lm.put("Label", "mypakcage.myclass");<br>
 *  lm.put(""Units", "ms.");<br>
 *  MonKey mk=new MonKeyBase(lm);<br>
 *  
 *  </p>
 */



 import java.util.List;
 
class MonKeyImp implements MonKey {
    
      private final String summaryLabel; // pageHits for example
      private String detailLabel; // The actual page name for the detail buffer.  pageHits for example
      private final String units; // ms. for example
      private boolean initializeDetail=true;
      
      MonKeyImp(String summaryLabel, String units) {
    	  this(summaryLabel, summaryLabel, units);
      }
   
      MonKeyImp(String summaryLabel, String detailLabel, String units) {
          this.summaryLabel = (summaryLabel==null) ? "" : summaryLabel;
          this.detailLabel = (detailLabel==null) ? "" : detailLabel;
          this.units= (units==null) ? "" : units;
    }
      
      MonKeyImp(MonKeyItem keyItem, String units) {
    	  String localSummaryLabel=keyItem.toString();
          this.summaryLabel = (localSummaryLabel==null) ? "" : localSummaryLabel;
          this.units= (units==null) ? "" : units;
          this.detailLabel=keyItem.getDetailLabel();

    	  
      }


      
      /** Returns the label for the monitor */
      public String getLabel() {
          return summaryLabel;
      }
        
      /** Returns the units for the monitor */
      public String getUnits() {
          return units;
      }
      
  	public String getDetailLabel() {
  		if (initializeDetail) {
  			initializeDetail=false;
  			detailLabel+=", "+units;
  		}
  		
	     return detailLabel;
	}
      
      /** Returns any object that has a named key.  In this keys case
       * 'label' and 'units' makes sense, but any values are acceptible.
       */
      public Object getValue(String key) {
          if (LABEL_HEADER.equalsIgnoreCase(key))
             return getLabel();
          else if (UNITS_HEADER.equalsIgnoreCase(key))
             return getUnits();
          else
             return null;
              
      }
        
/**
This method is called automatically by a HashMap when this class is used as a HashMap key.  A Coordinate is
considered equal if its x and y variables have the same value.
*/

  public boolean equals(Object compareKey) {

     return (
         compareKey instanceof MonKeyImp && 
         summaryLabel.equals(((MonKeyImp) compareKey).summaryLabel) &&
         units.equals(((MonKeyImp) compareKey).units)
         );

  }

  /** Used when key is put into a Map to look up the monitor */
  public int hashCode() {
     return (summaryLabel.hashCode() + units.hashCode());
   }

    public List getBasicHeader(List header) { 
        header.add(LABEL_HEADER);
        return header;
    }   
        
  
    public List getDisplayHeader(List header) {
        return getHeader(header);
    }
    
    public List getHeader(List header) {
        header.add(LABEL_HEADER);
        header.add(UNITS_HEADER);
        return header;
    }   
    
    public List getBasicRowData(List rowData) {
      rowData.add(getLabel()+", "+getUnits());
      return rowData;
    }
    

    
    public List getRowData(List rowData) {
      rowData.add(getLabel());
      rowData.add(getUnits());
      
      return rowData;
    }
    
    public List getRowDisplayData(List rowData) {
        return getRowData(rowData);
    }
    
    public String toString() {
        return new StringBuffer().append("JAMon Label=").append(getLabel()).append(", Units=").append(getUnits()).toString();
        
    }
    
    public String getRangeKey() {
        return getUnits();
    }



    
}
