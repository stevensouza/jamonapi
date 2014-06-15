
package com.jamonapi;

/**
 * Noop null monitor for when monitors are disabled.
 *
 */



import java.util.*;
 
final class NullMon extends MonitorImp {
    /** Creates a new instance of NullMonitor */
    NullMon(MonKey key, RangeImp range) {
        disable();
        this.key=key;
        this.range=range;

    }
    
    
    private static final MonKey NULL_KEY=new MonKeyImp("NullLabel", "NullUnit");
    private static final RangeImp NULL_RANGE=new NullRange();

    NullMon() {
        this(NULL_KEY, NULL_RANGE);
        
    }
//    
//    public Monitor add(double value) {
//        return this;
//    }
//    
//    public double getActive() {
//        return 0;
//    }
//    
//    public double getAvg() {
//        return 0;
//    }
//    
//    public double getAvgActive() {
//        return 0;
//    }
//    
//    private static Date NULL_DATE=new Date(0);
//    public Date getFirstAccess() {
//        return NULL_DATE;
//    }
//    
//    public double getHits() {
//        return 0;
//    }
//    
//    public Date getLastAccess() {
//        return NULL_DATE;
//    }
//    
//    public double getMax() {
//        return 0;
//    }
//    
//    public double getMaxActive() {
//        return 0;
//    }
//    
//    public double getMin() {
//        return 0;
//    }
//    
//    public Range getRange() {
//        return range;
//    }
//    
//    public double getStdDev() {
//        return 0;
//    }
//    
//    public double getTotal() {
//        return 0;
//    }
//    
//
//    
//    public boolean isPrimary() {
//        return false;
//    }
//    
//    public void reset() {
//       
//    }
//    
//    
//   
//    public void setActive(double value) {
//    }
//    
//    public void setFirstAccess(java.util.Date date) {
//    }
//    
//    public void setHits(double value) {
//    }
//    
//    public void setLastAccess(java.util.Date date) {
//    }
//    
//    public void setMax(double value) {
//    }
//    
//    public void setMaxActive(double value) {
//    }
//    
//    public void setMin(double value) {
//    }
//    
//    public void setPrimary(boolean isPrimary) {
//    }
//    
//
//    public void setTotal(double value) {
//    }
//    
//    public void setTotalActive(double value) {
//    }
//    
//    public Monitor start() {
//        return this;
//    }
//    
//    public Monitor stop() {
//        return this;
//    }
//    
//    public void disable() {
//    }
//    
//    public void enable() {
//    }
//    
//    public boolean isEnabled() {
//        return false;
//    }
//
//    
//    public double getLastValue() {
//        return 0;
//    }
//    
//    public void setLastValue(double value) {
//        
//    }
//
//    public void setAccessStats(long now) {
//        
//    }    
//    
//    public String toString() {
//        return "";
//    }
//    

}
