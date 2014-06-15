
package com.jamonapi;

/**
 * Simple thread safe counter class used to track activity stats 
 *
 * Created on December 16, 2005, 9:11 PM
 */


/**
 *
 * @author  ssouza 
 */
final class Counter  {
    
    private double count;
    private boolean enabled=true;
       
    public void setCount(double value) {
    	if (enabled) {
              count=value;
    	}
    }
    
    /* (non-Javadoc)
     * @see com.jamonapi.CounterInf#getCount()
     */
    public double getCount() {
    	if (enabled) {
           return count;
    	} else
    	   return 0;
    			
    }
    
    /* (non-Javadoc)
     * @see com.jamonapi.CounterInf#decrement()
     */
    public void decrement() {
    	if (enabled) {
        	  --count;
    	}
    }
  
    /* (non-Javadoc)
     * @see com.jamonapi.CounterInf#increment()
     */
    public void increment() {
    	if (enabled) {
        	++count;
        }
    }
 
    /* (non-Javadoc)
     * @see com.jamonapi.CounterInf#incrementAndReturn()
     */
    public double incrementAndReturn() {
    	if (enabled) {
        	 return ++count;
        } else
        	return 0;
    }
    
    public void enable(boolean enable) {
    	this.enabled=enable;
    }
    
    public boolean isEnabled() {
    	return enabled;
    }
 
    
        
  
}
