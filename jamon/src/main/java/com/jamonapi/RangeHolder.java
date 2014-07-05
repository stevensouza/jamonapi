
package com.jamonapi;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**Class that allows users to create ranges with the associated FrequencyDists/Buckets.
 * @author  ssouza
 */



public class RangeHolder implements Serializable {

    private static final long serialVersionUID = -5450423615086133693L;
    List ranges=new ArrayList();
    private boolean isLessThan=false;
    private String lastHeader="";


    /** Takes values &lt; or &lt;= */
    public RangeHolder(String logical) {
        isLessThan = "<".equals(logical);

    }


    /** defaults to &lt; */
    public RangeHolder() {

    }


    public void add(String displayHeader, double endPoint) {
        ranges.add(new RangeHolderItem(displayHeader, endPoint));
    }


    public void addLastHeader(String lastHeader) {
        this.lastHeader=lastHeader;
    }


    String getLastHeader() {
        return lastHeader;
    }


    // get standard millisecond time holder
    static RangeHolder getMSHolder() {
        RangeHolder rh=new RangeHolder("<");
        rh.add("LessThan_0ms",0);
        rh.add("0_10ms",10);
        rh.add("10_20ms",20);
        rh.add("20_40ms",40);
        rh.add("40_80ms",80);
        rh.add("80_160ms",160);
        rh.add("160_320ms",320);
        rh.add("320_640ms",640);
        rh.add("640_1280ms",1280);
        rh.add("1280_2560ms",2560);
        rh.add("2560_5120ms",5120);
        rh.add("5120_10240ms",10240);
        rh.add("10240_20480ms",20480);
        rh.addLastHeader("GreaterThan_20480ms");
        // note last range is always called lastRange and is added automatically
        return rh;
    }


    // get standard Percent holder
    static RangeHolder getPercentHolder() {
        RangeHolder rh=new RangeHolder("<=");
        rh.add("LessThanEqual_0",0);
        rh.add("0_10",10);
        rh.add("10_20",20);
        rh.add("20_30",30);
        rh.add("30_40",40);
        rh.add("40_50",50);
        rh.add("50_60",60);
        rh.add("60_70",70);
        rh.add("70_80",80);
        rh.add("80_90",90);
        rh.add("90_100",100);
        rh.addLastHeader("GreaterThan_100");
        return rh;
    }


    boolean isLessThan() {
        return isLessThan;
    }


    RangeHolder.RangeHolderItem get(int i) {
        return (RangeHolder.RangeHolderItem) ranges.get(i);
    }


    String[] getDisplayHeader() {
        int size=ranges.size();
        if (size==0)
            return null;

        String[] array=new String[size];
        for (int i=0;i<size;i++) {
            RangeHolderItem item=(RangeHolderItem) ranges.get(i);
            array[i]=item.getDisplayHeader();
        }

        return array;

    }


    double[] getEndPoints() {
        int size=ranges.size();
        if (size==0)
            return null;

        double[] array=new double[size];
        for (int i=0;i<size;i++) {
            RangeHolderItem item=(RangeHolderItem) ranges.get(i);
            array[i]=item.getEndPoint();
        }

        return array;
    }


    int getSize() {
        return ranges.size();
    }


    static class RangeHolderItem implements Serializable {
        private static final long serialVersionUID = -4432326375632557487L;
        private String displayHeader;
        private double endPoint;
        public RangeHolderItem(String displayHeader, double endPoint) {
            this.displayHeader=displayHeader;
            this.endPoint=endPoint;
        }

        public String getDisplayHeader() {
            return displayHeader;
        }

        public double getEndPoint() {
            return endPoint;
        }

    }

}
