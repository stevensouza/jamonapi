
package com.jamonapi;

/**
 * Simple counter class used to track activity stats
 *
 * Created on December 16, 2005, 9:11 PM
 *  @author  ssouza
 */

final class Counter  {

    private double count;
    private boolean enabled=true;

    /** explicitly set the counters value */
    public void setCount(double value) {
        if (enabled) {
            count=value;
        }
    }

    /** return the counters value
     */
    public double getCount() {
        if (enabled) {
            return count;
        } else
            return 0;

    }

    /** decrement the counters value
     */
    public void decrement() {
        if (enabled) {
            --count;
        }
    }

    /** increment the counters value
     */
    public void increment() {
        if (enabled) {
            ++count;
        }
    }

    /** increment the counters value and return it
     */
    public double incrementAndReturn() {
        if (enabled) {
            return ++count;
        } else
            return 0;
    }

    /** enable (true) or disable (false) the counter */
    public void enable(boolean enable) {
        this.enabled=enable;
    }

    /** return true if the counter is enabled */
    public boolean isEnabled() {
        return enabled;
    }

}
