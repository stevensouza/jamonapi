package com.jamonapi;

/**
 * Class that keeps time from the Factories but does not save it.  Note this object
 *should not be reused.  You should always get a new instance from MonitorFactory.
 *
 * Created on February 19, 2006, 9:05 AM
 */





class TimeMon2 extends Monitor {
    
    private long startTime, elapsedTime;
    
    private static final Range NULL_RANGE=new NullRange();


    public TimeMon2() {
    }
    
    public Monitor add(double value) {
     //?? not sure what should do in jamon 2.4??????????????   addValue(value);
        return this;
    }    

    public void disable() {
    }    
    
    public void enable() {
    }
    
    public double getActive() {
        return 0;
    }
    
    public double getAvgActive() {
        return 0;
    }
    
    public double getMaxActive() {
        return 0;
    }
    
    public MonKey getMonKey() {
        return null;
    }
    
    public Range getRange() {
        return NULL_RANGE;
    }
    
    public boolean isEnabled() {
        return true;
    }
    
    public boolean isPrimary() {
        return false;
    }
    
    public void setActive(double value) {
        
    }
    
    public void setMaxActive(double value) {
    }
    
    public void setPrimary(boolean isPrimary) {
    }
    
    public void setTotalActive(double value) {
    }
    
    public Monitor start() {
        startTime=System.currentTimeMillis();
        return this;
    }
    
    
    public Monitor stop() {
      elapsedTime=System.currentTimeMillis()-startTime;
     // jnot sure what should do in jamon 2.4 ???????? addValue(elapsedTime);
      return this;
    }
    
    
    public void reset() {
        super.reset();
        elapsedTime=startTime=0;

    }    
    
    public String toString() {
        return elapsedTime+" ms.";
    }
    
}
