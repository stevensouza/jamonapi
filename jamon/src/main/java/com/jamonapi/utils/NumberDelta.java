package com.jamonapi.utils;

/** This class takes successive values and returns the difference or delta between them.  Can be used to 
 * get data from jamon from one run and compare it to the next run.  The deltal could be graphed for values like
 * avg, min, max, lastValue etc.
 * 
 * In pseudocode you would graph the difference between this jamon run and the previous one like this.
 * 
 * currentMoniotr.getAvg()-previousMonitor.getAvg();
 * 
 * Or using NumberDelta
 * NumberDelta delta=new NumberDelta();
 * delta.setValue(mon.getAvg());
 * ...time elapses...
 * delta.setValue(mon.getAvg());
 * System.out.println(delta.getDelta());
 * 
 * */

public class NumberDelta {
    private double prevValue;
    private double value;
    
    public NumberDelta setValue(double val) {
        prevValue=value;
        this.value=val;
        return this;
    }
    
    public double getDelta() {
        return value-prevValue;
    }
    


}
