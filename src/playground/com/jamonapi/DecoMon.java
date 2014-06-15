
package com.jamonapi;

/**
 * This monitor is used as a front for the actual monitor via the decorator pattern.
 * It allows the actual monitor to be used or the diabled/null monitor while the 
 * client holds this handle.  This class handles enabling and disabling.   If enabled it 
 * passes calls on to the underlying Monitor object, and if disabled it passes commands 
 * on to the null monitor.
 *
 * Note disabling the monitor makes all calls in the monitor the equivalent of a noop.  The class
 * consists of a bunch of getters and setters.  Often neither be called directly as the jamon
 * report and an array of jamon data are easier to work with.
 *
 * @author  ssouza
 * 
 */

 
import java.util.*;


class DecoMon extends MonitorImp{ 
    private MonitorImp nullMon;  // handle to the disabled NullMonitor
    private MonitorImp realMon; // handle to the enabled monitor
    private MonitorImp mon; // handle to whichever of the two monitors above is active/current
    
    
    /** Creates a new instance of BaseMon.  It takes a reference to the enabled monitor it calls */
    public DecoMon(MonitorImp realMon) {
        this.mon=realMon;
        this.realMon=realMon;
    }

    private MonKey decoKey;
    public DecoMon(MonKey key,MonitorImp realMon) {
        this.decoKey=key;
    	this.mon=realMon;
        this.realMon=realMon;
    }

    
    /** Disable the monitor i.e. call the NullMonitor.  Note it lazily initializes it and makes sure the NullMonitor
      * has the same range as the enabled monitor, so the null monitor displays the same.  There is no effect if the 
     * monitor is already disabled.
     */
    public synchronized void disable() {
        if (nullMon==null) { // lazy initialization
          // range for null monitor should have the same number and values as the regular range
          RangeImp range=new NullRange(realMon.getRangeHolder());
          nullMon=new NullMon(realMon.getMonKey(), range);
        }
     
        // Make the NullMonitor the active monitor
        mon=nullMon;
    }
        
    
    
    /** Make the real Monitor the active one.  There is no effect if the real monitor is already enabled. */
    public synchronized void enable() {
        mon=realMon;
    }
    
    /** Determine if the monitor is currently enabled */
    public boolean isEnabled() {
        return mon.isEnabled();
    }
    
    /** Add the value to the monitor.  This is the meat and potatoes function for a monitor. start(), and stop() may also optionally
      * be called.*/
    public Monitor add(double value) {
      if (mon.enabled) {
       // the decoKey differes via the details from the other
       // key possibly.  This way when a listener calls getMonKey()
       // it can get the proper key detail.
       synchronized (mon) {
    	 mon.key=decoKey;
         mon.add(value);
       
       }
      }
       return this;
    }  
    
    /** Start the monitor which will increment any activity counters.  This is never required to be called however, if it is 
     * called stop() should also be called to decrement the values.  However, there is no memory leak if stop() is not called.
     * add(value) should be called in between start and stop to add the actual value.
     *
     * Sample Code:
     * Monitor mon=MonitorFactory.getMonitor("memory","MB");
     * mon.start();
     * mon.add(500);
     * mon.stop();
     */
    
    public Monitor start() {
        mon.start();
        return this;
    }


    /** Stop the monitor which will decrement any activity counters.  This is never required to be called however, if it is 
     * called it should be called after start() which increments the activity counters.  
     * However, there is no memory leak if stop() is not called.
     * add(value) should be called in between start and stop to add the actual value.
     *
     * Sample Code:
     * Monitor mon=MonitorFactory.getMonitor("memory","MB");
     * mon.start();
     * mon.add(500);
     * mon.stop();
     */
    public Monitor stop() {
        mon.stop();// decrement active.
        return this;
    }    
   
    /** Get the number of active monitors of this type (returns 0 if start() was never called */
    public double getActive() {
       return mon.getActive();
    }
    
    /** Get the average value for the monitor */
    public double getAvg() {
        return mon.getAvg();
    }
    
    /** Get the average number of active monitors of this type*/
    public double getAvgActive() {
        return mon.getAvgActive();
    }
    
    /** Get the first date/time that this monitor was accessed */
    public Date getFirstAccess() {
        return mon.getFirstAccess();
    }
    
    /** Get the number of hits for this monitor */
    public double getHits() {
        return mon.getHits();
    }
    
    /** Returns the label for the monitor */
    public String getLabel() {
        return mon.getLabel();
    }
    
    /** Get the most recent date/time that this monitor was accessed */
    public Date getLastAccess() {
        return mon.getLastAccess();
    }
    
    /** Get the maximum value that was passed to the add(value) method */
    public double getMax() {
        return mon.getMax();
    }
    
    /** Get the maximum number of active monitors of this thread */
    public double getMaxActive() {
        return mon.getMaxActive();
    }
    
    /** Get the minimum value that was passed to the add(value) method.*/
    public double getMin() {
        return mon.getMin();
    }
    
    
    /** Get the key for this monitor.  Example:  MonKey could contain "pageHits", "ms." */
    public MonKey getMonKey() {
    	if (decoKey!=null)
    	  return decoKey;
    	else
          return mon.getMonKey();
    }  
 

      
    /** Get the range associated with this monitor type.  Range is a user definable frequency distribution of the 
     * values passed to add(value)
     */
    public Range getRange() {
        return mon.getRange();
    }
    

    /** Get the standard deviation of the values passed to the add(value) function */
    public double getStdDev() {
        return mon.getStdDev();
    }
    
    /** Get the total of all values passed to the add(value) function */
    public double getTotal() {
        return mon.getTotal();
    }
    
    /** Returns the units for the monitor */
    public String getUnits() {
        return mon.getUnits();
    }
    
    /** Reset all statistics for this monitor (such as hits, total, min, max, and all range data) */
    public void reset() {
        mon.reset();
    }
    
    /** Set the number of active monitors of this type */
    public void setActive(double value) {
        mon.setActive(value);
    }
    
    
    public void setFirstAccess(Date date) {
        mon.setFirstAccess(date);
    }
    
    public void setHits(double value) {
        mon.setHits(value);
    }
    
    public void setLastAccess(Date date) {
        mon.setLastAccess(date);
    }
    
    public void setMax(double value) {
        mon.setMax(value);
    }
    
    public void setMaxActive(double value) {
        mon.setMaxActive(value);
    }
    
    public void setMin(double value) {
        mon.setMin(value);
    }
    
    
    public void setTotal(double value) {
        mon.setTotal(value);
    }
    
    public void setTotalActive(double value) {
        mon.setTotalActive(value);
    }

    /** Is this monitor primary.  Primary is a concept that maps all monitors performance to the main/primary monitors that drive performance */
    public boolean isPrimary() {
        return mon.isPrimary();
    }    
    
    public void setPrimary(boolean isPrimary) {
        mon.setPrimary(isPrimary);
    }
    

    /** Get the most recent value passed to add(value) */
     public double getLastValue() {
        return mon.getLastValue();
     }     

     public void setLastValue(double value) {
         mon.setLastValue(value);
     }     
     
     public String toString() {
         return mon.toString();
     }

     /** Get basic data as an array */
     public List getBasicRowData(List rowData) {
         return mon.getBasicRowData(rowData);
     }

     /** get basic and range data as an array */
     public List getRowData(List rowData) {
         return mon.getRowData(rowData);
     }
     
   
     public List getRowDisplayData(List rowData) {
         return mon.getRowDisplayData(rowData);
     }
     
       
     public void setAccessStats(long now) {
       mon.setAccessStats(now);
     }

     public List getBasicHeader(List header) {
         return mon.getBasicHeader(header);
     }
     
     public List getHeader(List header) {
         return mon.getHeader(header);
     }
     
     public List getDisplayHeader(List header) {
         return mon.getDisplayHeader(header);
     }
     

     
      
}
