
package com.jamonapi;

/**
 * FrequencyDist's are what Ranges are made of.  They are buckets of aggregate stats
 * within the monitor.  For example a FrequencyDist for ms. may be 0-10 ms., 20-40 ms.
 * etc.  They have end points, and all FrequencyDists within a range equate to 
 * the entire range of possible values for a monitor.
 *
 */




public interface FrequencyDist extends MonitorInt {
   /** Add the passed value to the FrequencyDist */
//   public void addValue(double value); 
   /** Get the end value of the FrequencyDist.*/
   public double getEndValue();
   /** Get the average number of active values when this frequency dist was hit */
//   public double getAvgActive();
//   /** Get the average number of primary active values when this frequency dist was hit */
//   public double getAvgPrimaryActive();
//   /** Get the average global number of active values when this frequency dist was hit */
//   public double getAvgGlobalActive();


}
