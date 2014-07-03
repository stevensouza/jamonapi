package com.jamonapi;

import java.io.Serializable;
import java.util.Date;

/**
 * Used to interact with monitor objects.  I would have preferred to make this an
 * interface, but didn't do that as jamon 1.0 code would have broken.  Live and learn
 * 
 */


// Note this was done as an empty abstract class so a recompile isn't needed
// to go to jamon 2.0. I had originally tried to make Monitor an interface.
// public abstract class Monitor extends BaseStatsImp implements MonitorInt {
public abstract class Monitor implements MonitorInt, Serializable {

	private static final long serialVersionUID = -1040490585063203451L;
	
	// Internal data passed from monitor to monitor.
    MonInternals monData;
    private double active; // note this needs to be an instance variable purely to support skip which has to subtract out the active if the variable is skipped.

    Monitor(MonInternals monData) {
        this.monData = monData;
    }

    Monitor() {
        this(new MonInternals());
    }

    final MonInternals getMonInternals() {
        synchronized (monData) {
            return monData;
        }
    }

    public MonKey getMonKey() {
        return monData.key;
    }

    /** Returns the label for the monitor */
    public String getLabel() {
        return (String) getMonKey().getValue(MonKey.LABEL_HEADER);
    }

    /** Returns the units for the monitor */
    public String getUnits() {
        return (String) getMonKey().getValue(MonKey.UNITS_HEADER);
    }

    public void setAccessStats(long now) {
        if (monData.enabled) {
            synchronized (monData) {
                // set the first and last access times.
                if (monData.firstAccess == 0)
                    monData.firstAccess = now;

                monData.lastAccess = now;
            }
        }
    }

    public void reset() {
        if (monData.enabled) {
            synchronized (monData) {
                monData.reset();
            }
        }

    }

    public double getTotal() {
        if (monData.enabled) {
            synchronized (monData) {
                return monData.total;
            }
        } else
            return 0;
    }

    public void setTotal(double value) {
        if (monData.enabled) {
            synchronized (monData) {
                monData.total = value;
            }
        }
    }

    public double getAvg() {
        if (monData.enabled)
            return avg(monData.total);
        else
            return 0;
    }

    public double getMin() {
        if (monData.enabled) {
            synchronized (monData) {
                return monData.min;
            }
        } else
            return 0;
    }

    public void setMin(double value) {
        if (monData.enabled) {
            synchronized (monData) {
                monData.min = value;
            }
        }
    }

    public double getMax() {
        if (monData.enabled) {
            synchronized (monData) {
                return monData.max;
            }
        } else
            return 0;
    }

    public void setMax(double value) {
        if (monData.enabled) {
            synchronized (monData) {
                monData.max = value;
            }
        }
    }

    public double getHits() {
        if (monData.enabled) {
            synchronized (monData) {
                return monData.hits;
            }
        } else
            return 0;
    }

    public void setHits(double value) {
        if (monData.enabled) {
            synchronized (monData) {
                monData.hits = value;
            }
        }
    }

    public double getStdDev() {

        if (monData.enabled) {
            synchronized (monData) {
                double stdDeviation = 0;
                if (monData.hits != 0) {
                    double sumOfX = monData.total;
                    double n = monData.hits;
                    double nMinus1 = (n <= 1) ? 1 : n - 1; // avoid 0 divides;

                    double numerator = monData.sumOfSquares
                    - ((sumOfX * sumOfX) / n);
                    stdDeviation = java.lang.Math.sqrt(numerator / nMinus1);
                }

                return stdDeviation;
            }
        } else
            return 0;
    }

    public void setFirstAccess(Date date) {
        if (monData.enabled) {
            synchronized (monData) {
                monData.firstAccess = date.getTime();
            }
        }
    }

    private static final Date NULL_DATE = new Date(0);

    public Date getFirstAccess() {
        if (monData.enabled) {
            synchronized (monData) {
                return new Date(monData.firstAccess);
            }
        } else
            return NULL_DATE;

    }

    public void setLastAccess(Date date) {
        if (monData.enabled) {
            synchronized (monData) {
                monData.lastAccess = date.getTime();
            }
        }
    }

    public Date getLastAccess() {
        if (monData.enabled) {
            synchronized (monData) {
                return new Date(monData.lastAccess);
            }
        } else
            return NULL_DATE;
    }

    public double getLastValue() {
        if (monData.enabled) {
            synchronized (monData) {
                return monData.lastValue;
            }
        } else
            return 0;

    }

    public void setLastValue(double value) {
        if (monData.enabled) {
            synchronized (monData) {
                monData.lastValue = value;
            }
        }
    }

    public void disable() {
        monData.enabled = false;
    }

    public void enable() {
        monData.enabled = true;

    }

    public boolean isEnabled() {
        return monData.enabled;
    }

    Listeners getListeners() {
        return monData.getListeners();
    }

    /** new jamon 2.4 stuff */

    /**
     * Add a listener that receives notification every time this monitors add
     * method is called. If null is passed all associated Listeners will be
     * detached.
     */


    /** pass in a valid listener type (min, max, value, maxactive) and get the ListenerType
     * 
     */

    public ListenerType getListenerType(String listenerType) {
        Listeners listeners=getListeners();
        if (listeners==null)
            return null;
        else
            return listeners.getListenerType(listenerType);
    }

    /** Returns true if this listenertype ('max', 'min', 'value', 'maxactive') has any listeners at all
     * 
     * @param listenerTypeName
     * @return boolean
     */
    public boolean hasListeners(String listenerTypeName) {
        synchronized (monData) {
            if (!monData.hasListeners())
                return false;

            ListenerType type=getListenerType(listenerTypeName);
            if (type==null)
                return false;
            else
                return type.hasListeners();
        }
    }

    /** Introduced as a way to add listeners that allows for lazy initialization saving a fair amount of memory.  Note
     * a future enhancement would be to delete the Listeners object when all listeners are removed.
     * 
     *  @since 2.71
     */
    public void addListener(String listenerTypeName, JAMonListener listener) {
        synchronized (monData) {
            Listeners listeners;
            if (monData.hasListeners())
                listeners=monData.getListeners();
            else
                listeners=monData.createListeners();

            listeners.getListenerType(listenerTypeName).addListener(listener);
        }

    }

    /** Introduced as a way to check for listeners that allows for lazy initialization saving a fair amount of memory.  Note
     * a future enhancement would be to delete the Listeners object when all listeners are removed.
     * 
     * @since 2.71
     */
    public boolean hasListener(String listenerTypeName, String listenerName) {
        synchronized (monData) {
            if (!monData.hasListeners())
                return false;

            ListenerType type=getListenerType(listenerTypeName);
            if (type==null)
                return false;
            else
                return type.hasListener(listenerName);
        }
    }



    /** Introduced as a way to remove listeners that allows for lazy initialization saving a fair amount of memory.  Note
     * a future enhancement would be to delete the Listeners object when all listeners are removed.
     * 
     * @since 2.71
     * 
     */
    public void removeListener(String listenerTypeName, String listenerName) {
        synchronized (monData) {
            if (!monData.hasListeners()) // return if there is nothing to remove
                return;

            ListenerType type=getListenerType(listenerTypeName);
            if (type!=null)
                type.removeListener(listenerName);
        }
    }



    public Monitor start() {
        if (monData.enabled) {

            synchronized (monData) {

                monData.incrementActivity();
                active=monData.incrementThisActive();

                // The only way activity tracking need be done is if start has
                // been entered.
                if (!monData.startHasBeenCalled) {
                    monData.startHasBeenCalled = true;
                    if (monData.trackActivity && monData.range != null)
                        monData.range.setActivityTracking(true);
                }

            } // end synchronized
        } // end enabled

        return this;

    }

    /** decrement counters but don't add aggregate stats to monitor.  have to also back out the totalactive figures. */
    public Monitor skip() {
        if (monData.enabled) {
            synchronized (monData) {
                monData.skip();
            }

        }

        return this;

    }

    public Monitor stop() {
        if (monData.enabled) {
            synchronized (monData) {

                // moved into stop section to support skip.  i.e. shouldn't affect avgactive or maxactive if skip is called.
                // before this was done in start which occurred before skip.
                if (active >= monData.maxActive) {
                    monData.maxActive = active;

                    if (monData.hasListener(Listeners.MAXACTIVE_LISTENER_INDEX) && active>1)
                        monData.getListener(Listeners.MAXACTIVE_LISTENER_INDEX).processEvent(this);
                }

                // being as avgactive needs hits to calculate we don't calculate totalActive till stop is called
                // hits are tallied in add as it is common behavior to all monitors.

                monData.stop(active);
            }

        }

        return this;
    }

    public Monitor add(double value) {
        if (monData.enabled) {
            synchronized (monData) {
                /* Being as TimeMonitors already have the current time and are passing it in the value (casted as long)
                 * for last access need not be recalculated. Using this admittedly ugly approach saved about 20%
                 * performance overhead on timing monitors.
                 */
                if (!monData.isTimeMonitor)
                    setAccessStats(System.currentTimeMillis());

                // most recent value
                monData.lastValue = value;

                // calculate hits i.e. n
                monData.hits++;

                // calculate total i.e. sumofX's
                monData.total += value;

                // used in std deviation
                monData.sumOfSquares += value * value;

                /* tracking activity is only done if start was called on the monitor there is no need to synchronize
                 * and perform activity tracking  if this  monitor doesn't have a start and stop called.
                 */
                monData.updateActivity();

                /* calculate min. note saving min if it is a tie so listener will be called and checking to see if
                 * the new min was less than the current min seemed to cost more. Same for max below
                 */
                if (value <= monData.min) {
                    monData.min = value;

                    if (monData.hasListener(Listeners.MIN_LISTENER_INDEX))
                        monData.getListener(Listeners.MIN_LISTENER_INDEX).processEvent(this);

                }

                // calculate max
                if (value >= monData.max) {
                    monData.max = value;

                    if (monData.hasListener(Listeners.MAX_LISTENER_INDEX))
                        monData.getListener(Listeners.MAX_LISTENER_INDEX).processEvent(this);
                }

                if (monData.hasListener(Listeners.VALUE_LISTENER_INDEX))
                    monData.getListener(Listeners.VALUE_LISTENER_INDEX).processEvent(this);

                if (monData.range != null)
                    monData.range.processEvent(this);

            }

        }

        return this;

    }

    public Range getRange() {
        return monData.range;
    }

    public double getActive() {
        if (monData.enabled) {
            synchronized (monData) {
                return monData.getThisActiveCount();
            }
        } else
            return 0;
    }

    public void setActive(double value) {
        if (monData.enabled) {
            synchronized (monData) {
                monData.setThisActiveCount(value);
            }
        }
    }

    public double getMaxActive() {
        if (monData.enabled) {
            synchronized (monData) {
                return monData.maxActive;
            }
        } else
            return 0;
    }

    public void setMaxActive(double value) {
        if (monData.enabled) {
            synchronized (monData) {
                monData.maxActive = value;
            }
        }
    }

    /** Neeed to reset this to 0.0 to remove avg active numbers */
    public void setTotalActive(double value) {
        if (monData.enabled) {
            synchronized (monData) {
                monData.totalActive = value;
            }
        }
    }

    public boolean isPrimary() {
        return monData.isPrimary;
    }

    public void setPrimary(boolean isPrimary) {
        if (monData.enabled) {
            this.monData.isPrimary = isPrimary;
        }
    }

    public boolean hasListeners() {
        synchronized (monData) {
            return monData.hasListeners();
        }
    }


    /** Alternative method of getting the values in the 'get' methods like getHits(), getAvg() etc. */
    public Object getValue(String key) {
        if (AVG.equalsIgnoreCase(key))
            return new Double(getAvg());
        else if (HITS.equalsIgnoreCase(key))
            return new Double(getHits());
        else if (MIN.equalsIgnoreCase(key))
            return new Double(getMin());
        else if (MAX.equalsIgnoreCase(key))
            return new Double(getMax());
        else if (TOTAL.equalsIgnoreCase(key))
            return new Double(getTotal());
        else if (ACTIVE.equalsIgnoreCase(key))
            return new Double(getActive());
        else if (STDDEV.equalsIgnoreCase(key))
            return new Double(getStdDev());
        else if (VALUE.equalsIgnoreCase(key) || LASTVALUE.equalsIgnoreCase(key))
            return new Double(getLastValue());
        else if (LASTACCESS.equalsIgnoreCase(key))
            return getLastAccess();
        else if (FIRSTACCESS.equalsIgnoreCase(key))
            return getFirstAccess();
        else if (AVGACTIVE.equalsIgnoreCase(key))
            return new Double(getAvgActive());
        else if (MAXACTIVE.equalsIgnoreCase(key))
            return new Double(getMaxActive());
        else if (MonKey.LABEL_HEADER.equalsIgnoreCase(key))
            return getLabel();
        else if (MonKey.UNITS_HEADER.equalsIgnoreCase(key))
            return getUnits();
        else
            return null;

    }


    @Override
    public String toString() {
        if (monData.enabled) {
            /* This character string is about 275 characters now, but made the default a little bigger,
             * so the JVM doesn't have to grow the StringBuffer should I add more info.
             */
            StringBuffer b = new StringBuffer(400);
            b.append(getMonKey() + ": (");
            b.append("LastValue=");
            b.append(getLastValue());
            b.append(", Hits=");
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
        this.monData.trackActivity = trackActivity;
        if (monData.range != null)
            monData.range.setActivityTracking(trackActivity);
    }

    public boolean isActivityTracking() {
        return monData.trackActivity;
    }

    private double avg(double value) {
        synchronized (monData) {
            if (monData.hits == 0)
                return 0;
            else
                return value / monData.hits;
        }
    }



    public double getAvgActive() {
        if (monData.enabled) {
            /* can be two ways to get active. For ranges thisActiveTotal is used and for nonranges totalActive is used.
             * This is because the ranges show how many of that range are active (thisActiveTotal) and totalActive
             * shows how many are active for the entire monitor
             */
            if (monData.trackActivity) {
                return avg(monData.thisActiveTotal);
            } else
                return avg(monData.totalActive);
        } else
            return 0;

    }

    /** for low numbers this can be negative */
    public double getAvgGlobalActive() {
        return avgNoNeg(monData.allActiveTotal);
    }

    public double getAvgPrimaryActive() {
        return avgNoNeg(monData.primaryActiveTotal);
    }

    /** Used due to the fact when activity tracking is turned on it goes through the jamon web filter and does a start,
     * then turns on activity tracking and in stop performs a decrement (even though no increment was done in start)
     * this results in potential negative values.  Note this can't happen with the activity measure for this object as it
     * is always enabled. it can only happen for primary and global active.  note bigger numbers are also slightly off
     * however the values won't be negative and will be consistently off by less than 1.
     * 
     * @param value
     * @return average
     */
    private double avgNoNeg(double value) {
        double v=avg(value);
        return (v<=0) ? 0 : v;
    }

    public JAMonDetailValue getJAMonDetailRow() {
        if (monData.enabled) {
            synchronized (monData) {
                return new JAMonDetailValue(getMonKey(),
                        monData.lastValue, monData.getThisActiveCount(),  monData.lastAccess);
            }
        } else
            return JAMonDetailValue.NULL_VALUE;
    }



}
