
package com.jamonapi;

/**
 * Noop range object for when a range wasn't specified 
 * Created on November 15, 2005, 10:38 PM 
 */




import java.util.Date;

 final class NullRange extends RangeImp {

 
    /** Creates a new instance of RangeNullObject */
    
    public NullRange() {
        
    }

    
    public NullRange(RangeHolder rangeHolder) {
      if (rangeHolder!=null) {
        
        int len=rangeHolder.getEndPoints().length;
        isLessThan=rangeHolder.isLessThan();
        frequencyDist=new NullRange.FrequencyDistNullObject[len+1];
        for (int i=0;i<len;i++) {
           RangeHolder.RangeHolderItem item=rangeHolder.get(i);
           frequencyDist[i]=new NullRange.FrequencyDistNullObject(item.getDisplayHeader(), item.getEndPoint(), getFreqDistName(i));         
        }
        
        frequencyDist[len]=new NullRange.FrequencyDistNullObject(getLastHeader(), Double.MAX_VALUE, getFreqDistName(len));         
      }
        
    }
    
    public void add(double value) {
        
    }
    
    // all values return the same the same version of the null object
    public FrequencyDist getFrequencyDist(double value) {
        return (frequencyDist==null) ? null : frequencyDist[0];
    }    
    
    
      
    
    public void reset() {
    }
    
    public RangeImp copy(ActivityStats activityStats) {
        return this;
    }
    
    static class FrequencyDistNullObject extends FrequencyDistImp {
     
        
        public FrequencyDistNullObject(String displayHeader, double endValue, String name) {
            this.displayHeader=displayHeader;
            this.endValue=endValue;
            this.name=name;
        }
        
//       public  void addValue(double value) {
//           
//       }
//        
        public void reset() {
        }
        
        public double getAvgActive() {
            return 0;
        }
        
        public double getAvgGlobalActive() {
            return 0;
        }
        
        public double getAvgPrimaryActive() {
            return 0;
        }
        
        public double getHits() {
            return 0;
        }
        public void setHits(double value) {
            
        }
         
        public double getTotal() {
            return 0;
        }
        public void setTotal(double value) {
            
        }
        
       public double getAvg() {
           return 0;
       }
  
       public double getMin() {
           return 0;
       }
       public void setMin(double value) {
           
       }
  
       public double getMax() {
           return 0;
       }
       public void setMax(double value) {
           
       }
  
       public double getStdDev() {
           return 0;
       }
       
       private static Date NULL_DATE=new Date(0);
       public Date getFirstAccess() {
           return NULL_DATE;
       }
       public void setFirstAccess(Date date) {
           
       }
       
       public Date getLastAccess() {
           return NULL_DATE;
       }
       public void setLastAccess(Date date) {
       }
  
       public double getLastValue() {
           return 0;
       }
       public void setLastValue(double value) {
           
       }
 
       public String toString() {
            return "";
        }
        
        public void setActivityStats(ActivityStats activityStats) {
        }
        
       
    } // end nullfrequencyobject
    
	public void processEvent(Monitor mon) {
		
	}    
}
