
package com.jamonapi;

/**
 * RangeImpInt.java
 *
 * Created on January 10, 2006, 11:26 AM
 */



/** 
 * Due to the fact that setting ranges would cause knowledge of the ActivityStats I opted
 * not to expose setRange at this point.
 * RangeImpInt stands for Range Implementation Interface.  This interface has some interface
 * details I don't want to expose.  Range is the public interface that developers can access.
 */
import java.util.List;

abstract class RangeImp implements Range, RowData, JAMonListener {
  protected FrequencyDistImp[] frequencyDist;
  protected RangeHolder rangeHolder;
  protected boolean isLessThan=false;

  
  abstract protected RangeImp copy(ActivityStats activityStats);
  
  public String getFreqDistName(int count) {
      return "Range"+count+"_";
  }

  public int getFreqDistSize() {
    return (frequencyDist==null) ? 0 : frequencyDist.length;
  }
  
   public FrequencyDist[] getFrequencyDists() {
      return frequencyDist;
   }  
   
   
   public String getLastHeader() {
        return (rangeHolder==null) ? "LastRange" : rangeHolder.getLastHeader();
    }
   
     public List getBasicHeader(List header) {
         // this isn't called but still will return no changes (ranges don't 
         // participate in basic headers)
         return header;
     }

  
     public List getDisplayHeader(List header) {
         int size=getFreqDistSize();
         
         for (int i=0;i<size;i++) {
             frequencyDist[i].getDisplayHeader(header);
         }
         
         return header;
         
     }     
    
     public List getHeader(List header) {
         int size=getFreqDistSize();
         
         for (int i=0;i<size;i++) {
           frequencyDist[i].getHeader(header);
         }       
         
         return header;
     }     
     
     
     public List getBasicRowData(List rowData) {
         // basic row data does not include ranges so simply return.  note the monitors don't 
         // even call this method to save the step.
         return rowData;
     }    
    
     public List getRowData(List rowData) {
         int size=getFreqDistSize();
         
         for (int i=0;i<size;i++) {
           frequencyDist[i].getRowData(rowData);
         }
         
         return rowData;
         
     }


     public List getRowDisplayData(List rowData) {
         int size=getFreqDistSize();
         for (int i=0;i<size;i++) {
           frequencyDist[i].getRowDisplayData(rowData);
         }
         
         return rowData;
         
     }        
     
    public RangeHolder getRangeHolder() {
        return rangeHolder;
    }

    public String getLogicalOperator() {
        return (isLessThan) ? "<" : "<=";
        
    }
    
     public void setActivityTracking(boolean trackActivity) {
         int size=getFreqDistSize();
         for (int i=0;i<size;i++) {
           frequencyDist[i].setActivityTracking(trackActivity);
         }
     }    
    
}
