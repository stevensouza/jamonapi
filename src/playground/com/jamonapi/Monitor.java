
package com.jamonapi;

/**
 * Used to interact with monitor objects.  I would have preferred to make this an 
 *interface, but didn't do that as jamon 1.0 code would have broken.  Live and learn
 *
 * Created on December 11, 2005, 10:19 PM
 */


import java.util.Date;




// Note this was done as an empty abstract class so a recompile isn't needed
// to go to jamon 2.0.  I had originally tried to make Monitor an interface.
//public abstract class Monitor extends BaseStatsImp implements MonitorInt {
public abstract class Monitor implements MonitorInt {
     
    protected MonKey key;
    /*** Added in 2.4 brought in from BaseStatsImp */
    
    
    /** seed value to ensure that the first value always sets the max */
    static double MAX=-Double.MAX_VALUE;
    /** seed value to ensure that the first value always sets the min */
    static double MIN=Double.MAX_VALUE;
    
    /** the total for all values */
    double total=0.0; 
    /** The minimum of all values */
    double min=MIN;
    /** The maximum of all values */
    double max=MAX;
    /** The total number of occurrences/calls to this object */
    double hits=0.0;
    /** Intermediate value used to calculate std dev */
    double sumOfSquares=0.0;
    /** The most recent value that was passed to this object */
    double lastValue=0.0;
    /** The first time this object was accessed */
    long firstAccess=0;
    /** The last time this object was accessed */
    long lastAccess=0;
    /** Is this a time monitor object?  Used for performance optimizations */
    boolean isTimeMonitor=false;
    
   	private JAMonListener valueListener;
    private JAMonListener minListener;	
	private JAMonListener maxListener;	
	private JAMonListener maxActiveListener;

	/*** jamon 2.4 from BaseMon enable/disable */
    
	protected boolean enabled=true;
	private boolean trackActivity=false;
	protected String name="";// for regular monitors empty.  For range monitors "Range1_"
	protected String displayHeader="";// for regular monitors empty.  rangeholder name for ranges (i.e. 0_20ms)

	/*** added for jamon 2.4 from Mon */
	double maxActive = 0.0;
	double totalActive = 0.0;
	boolean isPrimary = false;
	boolean startHasBeenCalled = true;
	ActivityStats activityStats;
	// from MonitorImp
    protected RangeImp range;
	private double allActiveTotal; // used to calculate the average active total monitors for this distribution
	private double primaryActiveTotal;
	private double thisActiveTotal;
	
    
    public MonKey getMonKey() {
      return key;
    }    
 
    /** Returns the label for the monitor */
    public String getLabel() {
        return (String) getMonKey().getValue(MonKey.LABEL_HEADER);
    }
      
    /** Returns the units for the monitor */
    public String getUnits() {
        return (String) getMonKey().getValue(MonKey.UNITS_HEADER);
    }
    
//    public String getDetailLabel() {
//    	if (getMonKey()==null)
//    	 return "";
//    	else 
//    	 return getMonKey().getDetailLabel();
//    }
    
   
 
      /** Calculate aggregate stas (min, max */
//      public synchronized void addValue(double value) {
            
//
//          // most recent value
//          lastValue=value;
//          if (valListener!=null)
//          	valListener.processEvent(this);
//          
//            // calculate min
//    	  //  if (valListener!=null || maxListener!=null || minListener!=null)
//            if (value < min) {
//                min = value;
//                if (minListener!=null)
//                	minListener.processEvent(this);
//            }
//            
//            // calculate max
//            if (value > max) {
//                max = value;
//                if (maxListener!=null)
//                	maxListener.processEvent(this);
//            }
//            
//
//            // calculate hits i.e. n
//            hits++;
//            
//            // calculate total i.e. sumofX's
//            total+=value;
//
//            // used in std deviation
//            sumOfSquares+=value*value;
//            
//            // Being as TimeMonitors already have the current time and are passing it in
//            // the value (casted as long) for last access need not be recalculated. 
//            // Using this admittedly ugly approach saved about 20% performance 
//            // overhead on timing monitors.
//            if (!isTimeMonitor) 
//               setAccessStats(System.currentTimeMillis());
           
//      }
      
       public  void setAccessStats(long now) {
    	   if (enabled) {
       		synchronized(this) {     
           // set the first and last access times.
             if (firstAccess==0)
               firstAccess=now;
            
             lastAccess=now;
       		}
    	   }
       }
          

        public void reset() {
         if (enabled) {
     	  synchronized(this) {  
            hits=total=sumOfSquares=lastValue=0.0;
            firstAccess=lastAccess=0;
            min=MIN;
            max=MAX;


        // added from mon class
		   maxActive = totalActive = 0.0;
		   activityStats.thisActive.setCount(0);
		   
		   // added from frequencydistbase
			allActiveTotal = primaryActiveTotal = thisActiveTotal = 0;
			if (range!=null)
		      range.reset();
     	 }
        }
      
        }
        
        public double getTotal() {
            if (enabled) {
        		synchronized(this) {  
                 return total;
        		}
            } else
             return 0;
        }
        public void setTotal(double value) {
            if (enabled) {
        		synchronized(this) {  
                 total=value;
        		}
            } 
        }
        
        public double getAvg() {
            if (enabled)
              return avg(total);
            else
              return 0;
        }


        public double getMin() {
            if (enabled) {
        		synchronized(this) {  
                 return min;
        		}
            } else
             return 0;
        }
        
        public void setMin(double value) {
            if (enabled) {
        		synchronized(this) {  
                  min=value;
        		}
            }
        }

            
        public double getMax() {
            if (enabled) {
        		synchronized(this) {  
                 return max;
        		}
            } else
              return 0;
        }
        public void setMax(double value) {
            if (enabled) {
        		synchronized(this) {  
                  max=value; 
        		}
            } 
        }
        
        public double getHits() {
            if (enabled) {
        		synchronized(this) {  
      	         return hits;
        		}
            } else
              return 0;
        }
        
        public void setHits(double value) {
            if (enabled) {
        		synchronized(this) {  
                 hits=value;
        		}
            }
        }    

        
        public double getStdDev() {
        	
            if (enabled) {
        	  synchronized(this) {  
               double stdDeviation=0;
               if (hits!=0) {
                double sumOfX=total;
                double n=hits;
                double nMinus1= (n<=1) ? 1 : n-1;  // avoid 0 divides;
                
                double numerator = sumOfSquares-((sumOfX*sumOfX)/n);
                stdDeviation=java.lang.Math.sqrt(numerator/nMinus1);
               } 
            
               return stdDeviation;
        	  }
            }
            else
            	return 0;
        }
        
        
       public void setFirstAccess(Date date) {
           if (enabled) {
       		synchronized(this) {  
              firstAccess=date.getTime();
       		}
           }
       }
       
       private static final Date NULL_DATE=new Date(0);     
       public Date getFirstAccess() {
           if (enabled) {
       		synchronized(this) {  
   	          return new Date(firstAccess);
       		}
           } else 
        	 return NULL_DATE;
        	   
       }
       
       
       public  void setLastAccess(Date date) {
           if (enabled) {
       		synchronized(this) {  
        	   lastAccess=date.getTime();
       		}
           }
       }
       
       
       public Date getLastAccess() {
           if (enabled) {
       		synchronized(this) {  
        	   return new Date(lastAccess);
       		}
           } else
        	   return NULL_DATE;
       }
       
        
        public double getLastValue() {
            if (enabled) {
        		synchronized(this) {  
                  return lastValue;
        		}
            } else
              return 0;
           
        }
        
        public void setLastValue(double value) {
            if (enabled) {
        		synchronized(this) {  
            	 lastValue=value;
        		}
            }
        }

        // new methods.  not sure about them
    	public void disable() {
    		enabled=false;
//            if (nullMon==null) { // lazy initialization
//                // range for null monitor should have the same number and values as the regular range
//                RangeImp range=new NullRange(realMon.getRangeHolder());
//                nullMon=new NullMon(realMon.getMonKey(), range);
//              }
//           
//              // Make the NullMonitor the active monitor
//              mon=nullMon;
//          }
    		
    	}

    	public void enable() {
    		enabled=true;
    		
    	}

    	public boolean isEnabled() {
    		return enabled;
    	}
    	
    	/** new stuff */
    	
//    	private JAMonListener valListener=new JAMonBufferListener();
//    	private JAMonListener minListener=new JAMonBufferListener();	
//    	private JAMonListener maxListener=new JAMonBufferListener();	
//???????? MOVE LISTENER METHODS UP TO INTERFACE!!!!!!!!!!
 //   	private JAMonListener valListener=new JAMonBufferListener();

    //	protected JAMonListener delmeListener=new JAMonBufferListener();	
    	
    	public void setValueListener(JAMonListener valueListener) {
            if (enabled) {
        		synchronized(this) { 
        			this.valueListener=valueListener;
	
        		}
            }
    		
    	}
    	
    	public JAMonListener getValueListener() {
    		return valueListener;
    	}
    	
    	public void setMinListener(JAMonListener minListener) {
            if (enabled) {
        		synchronized(this) { 
        			this.minListener=minListener;
	
        		}
            }
    		
    	}
    	
    	public JAMonListener getMinListener() {
    		return minListener;
    	}
    	
    	
    	public void setMaxListener(JAMonListener maxListener) {
            if (enabled) {
        		synchronized(this) { 
        			this.maxListener=maxListener;
	
        		}
            }
    		
    	}
    	
    	
    	public JAMonListener getMaxListener() {
    		return maxListener;
    	}
    	
    	
    	
    	public void setMaxActiveListener(JAMonListener maxActiveListener) {
            if (enabled) {
        		synchronized(this) { 
        			this.maxActiveListener=maxActiveListener;
	
        		}
            }
    		
    	}
    	
    	
    	public JAMonListener getMaxActiveListener() {
    		return maxActiveListener;
    	}
    	
//
//    	public String getDetailLabel() {
//    		// TODO Auto-generated method stub
//    		return "detailLabel";
//    	}

    //	JAMonDetailValue jamonDetailValue=new  JAMonDetailValue("",0.0,1);
    	public JAMonDetailValue getJAMonDetailValue() {
    	//	if (jamonDetailValue.value!=value || jamonDetailValue.time!=time || jamonDetailValue.equals())

    		return null;
//    		synchronized(this) {  
//    		
//    		// TODO Auto-generated method stub
//    		return new JAMonDetailValue(getDetailLabel(),lastValue,lastAccess);
//    		}
    	}
        
    	

        
    	public Monitor start() {
            if (enabled) {
            	
             synchronized(this) {
           	
    		   activityStats.allActive.increment();

    		   if (isPrimary) {
    			  activityStats.primaryActive.increment();
    		   }

    		// tracking current active/avg active/max active for this instance
    		   double active=activityStats.thisActive.incrementAndReturn();

//    		 synchronized (this) {
    			totalActive += active;// allows us to track the average active for THIS instance.
    			if (active > maxActive)
    				maxActive = active;
//    		 }

    		// The only way activity tracking need be done is if start has been entered.
    		  if (!startHasBeenCalled) {
    			startHasBeenCalled = true;
    			if (range!=null)
    			  range.setActivityTracking(true);
    		  }

            } // end synchronized
           } // end enabled
            
    		return this;

    	}

    	public Monitor stop() {
            if (enabled) {    
              synchronized(this) {
    		    activityStats.thisActive.decrement();
    		  
    		    if (isPrimary) {
    			 activityStats.primaryActive.decrement();
    		    }
    		  
    		    activityStats.allActive.decrement();
              }
  
            }

    		return this;

    	}

    	
    	public Monitor add(double value) {
            if (enabled) {
        		synchronized(this) {  
            // most recent value
                 lastValue=value;

                 if (valueListener!=null)
            	   valueListener.processEvent(this);
            
              // calculate min
      	  //  if (valListener!=null || maxListener!=null || minListener!=null)
                 if (value < min) {
                   min = value;
                   if (minListener!=null)
                  	minListener.processEvent(this);
                 }
              
              // calculate max
                 if (value > max) {
                  max = value;
                  if (maxListener!=null)
                  	maxListener.processEvent(this);
                 }
              

                // calculate hits i.e. n
                hits++;
              
                // calculate total i.e. sumofX's
                total+=value;

                // used in std deviation
                sumOfSquares+=value*value;
              
                // Being as TimeMonitors already have the current time and are passing it in
                // the value (casted as long) for last access need not be recalculated. 
                // Using this admittedly ugly approach saved about 20% performance 
                // overhead on timing monitors.
                if (!isTimeMonitor) 
                 setAccessStats(System.currentTimeMillis());
    
        		// tracking activity is only done if start was called on the monitor
        		// there is no need to synchronize and perform activity tracking if this
        		// monitor doesn't have a start and stop called.
        		if (trackActivity) {
         				thisActiveTotal += activityStats.thisActive.getCount(); // total of this monitors active  
        				primaryActiveTotal += activityStats.primaryActive.getCount(); // total of primary monitors active  
        				allActiveTotal += activityStats.allActive.getCount(); // total of all monitors active  
        		} 

               if (range!=null)
    		     range.processEvent(this);
        	}
    		  //   range.add(value);

            }
            
    		return this;

    	}

//    	public synchronized void reset() {
//
//    		super.reset();
//
//    		maxActive = totalActive = 0.0;
//
//    		activityStats.thisActive.setCount(0);
//
//    		range.reset();
//
//    	}

    	public Range getRange() {
    		return range;
    	}

    	public double getActive() {
            if (enabled) {
        		synchronized(this) {  
    		     return activityStats.thisActive.getCount();
        		}
            } else
             return 0;
    	}

    	public void setActive(double value) {
            if (enabled) {
        		synchronized(this) {  
    		      activityStats.thisActive.setCount(value);
        		}
            } 
    	}

    	public double getMaxActive() {
            if (enabled) {
        		synchronized(this) {  
    		      return maxActive;
        		}
            } else
             return 0;
    	}

    	public void setMaxActive(double value) {
            if (enabled) {	
        		synchronized(this) {  
    		      maxActive = value;
        		}
            }
    	}

    	/** Neeed to reset this to 0.0 to remove avg active numbers */
    	public void setTotalActive(double value) {
            if (enabled) {
        		synchronized(this) {  
    		     totalActive = value;
        		}
            }
    	}

    	// calculated


    	public boolean isPrimary() {
    		 return isPrimary;
    	}

    	public void setPrimary(boolean isPrimary) {
            if (enabled) {    		
    		  this.isPrimary = isPrimary;
            }
    	}

    	public String toString() {
            if (enabled) {
    		// This character string is about 275 characters now, but made
    		// the default a little bigger, so the JVM doesn't have to grow
    		// the StringBuffer should I add more info.

    		  StringBuffer b = new StringBuffer(400);
    		  b.append(getMonKey() + ": (Hits=");
    		  b.append(getHits());
    		  b.append(", Avg=");
    		  b.append(getAvg());
    		  b.append(", Total=");
    		  b.append(getTotal());
    		  b.append(", Min=");
    		  b.append(getMin());
    		  b.append(", Max=");
    		  b.append(getMax());
    		  b.append(", Active=");
    		  b.append(getActive());
    		  b.append(", Avg Active=");
    		  b.append(getAvgActive());
    		  b.append(", Max Active=");
    		  b.append(getMaxActive());
    		  b.append(", First Access=");
    		  b.append(getFirstAccess());
    		  b.append(", Last Access=");
    		  b.append(getLastAccess());
    		  b.append(")");

    		  return b.toString(); 
            } else
              return "";

    	}





    	/** FROM frequencydistimp */
        public void setActivityTracking(boolean trackActivity) {
            this.trackActivity=trackActivity;
        } 
        
        public boolean isActivityTracking() {
        	return trackActivity;
        }

    	
        private synchronized double avg(double value) {
    		if (hits == 0)
   			  return 0;
    		else
    	      return value / hits;
    	}



    	
    	public double getAvgActive() {
            if (enabled) {	
            	// can be two ways to get active.  For ranges
            	// thisActiveTotal is sued and for nonranges
            	// totalActive is used.
            	if (trackActivity) {
            		return avg(thisActiveTotal);
            	} else
            		return avg(totalActive);
            } else
            	return 0;
            
    	}
   
    	public double getAvgGlobalActive() {
    		return avg(allActiveTotal);
    	}

    	public double getAvgPrimaryActive() {
    		return avg(primaryActiveTotal);
    	}




  
}
