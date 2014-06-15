package com.jamonapi;

/** Base class for ranges which are a compromise between aggregation and tracking details.
 */
final class RangeBase extends RangeImp  {

    private double[] rangeValues;


    /** The first range will catcth anything less than that value. */
    RangeBase(RangeHolder rangeHolder) {
        this.rangeHolder=rangeHolder;
        this.isLessThan=rangeHolder.isLessThan();
        this.rangeValues=rangeHolder.getEndPoints();

        int len=rangeValues.length;
        // add one to cover values less than first range
        frequencyDist=new FrequencyDistBase[len+1];
        for (int i=0;i<len;i++) {
            RangeHolder.RangeHolderItem item=rangeHolder.get(i);
            frequencyDist[i]=new FrequencyDistBase(item.getDisplayHeader(), item.getEndPoint(), getFreqDistName(i));
        }

        frequencyDist[len]=new FrequencyDistBase(getLastHeader(),Double.MAX_VALUE,getFreqDistName(len));
    }


    /** return which Distribution the value belongs to. */
    public FrequencyDist getFrequencyDist(double value) {
        int last=frequencyDist.length-1;

        // If comparison is for < else <=.  Defaults to <=
        if (isLessThan) {
            for (int i=0;i<last;i++) {
                if (value<rangeValues[i])
                    return frequencyDist[i];
            }
        } else { // <= i.e. not less than
            for (int i=0;i<last;i++) {
                if (value<=rangeValues[i])
                    return frequencyDist[i];
            }
        }

        //if nothing has matched until this point then match on the last range.
        return frequencyDist[last];
    }


    public void add(double value) {
        getFrequencyDist(value).add(value);

    }


    public void reset() {
        for (int i=0;i<frequencyDist.length;i++)
            frequencyDist[i].reset();
    }

    @Override
    public RangeImp copy(ActivityStats activityStats) {
        RangeBase rb=new RangeBase(rangeHolder);
        rb.setActivityStats(activityStats);
        return rb;
    }

    private void setActivityStats(ActivityStats stats) {
        for (int i=0;i<frequencyDist.length;i++)
            frequencyDist[i].setActivityStats(stats);
    }

    /** Ranges are implemented as JAMonListeners */
    public void processEvent(Monitor mon) {
        double value=mon.getLastValue();
        getFrequencyDist(value).add(value);
    }

    public String getName() {
        return "Range";
    }

    public void setName(String name) {
        // noop
    }


}
