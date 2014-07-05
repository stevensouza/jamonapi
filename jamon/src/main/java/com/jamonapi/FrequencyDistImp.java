

package com.jamonapi;


import java.util.List;

/**
 * Class that implements metrics for different buckets.   For example for time a bucket could be any monitors that take between 20 ms. and 40 ms. These buckets allow for seeing better granularity of the monitors of a given label.
 * 
 * @author stevesouza
 *
 */

abstract class FrequencyDistImp extends MonitorImp implements FrequencyDist {

    private static final long serialVersionUID = 309408157086212779L;
    protected double endValue;

    @Override
    public List getBasicHeader(List header) {
        // Frequencies don't get displayed basic headers.
        return header;
    }

    @Override
    public List getHeader(List header) {
        super.getHeader(header);
        header.add(monData.name+"AvgActive");
        header.add(monData.name+"AvgPrimaryActive");
        header.add(monData.name+"AvgGlobalActive");
        return header;
    }

    @Override
    public List getDisplayHeader(List header) {
        header.add(monData.displayHeader);
        return header;
    }

    @Override
    public List getBasicRowData(List rowData) {
        // This is not called as basic rowdata doesn't inlcude frequencydists
        return rowData;
    }


    @Override
    public List getRowData(List rowData) {
        super.getRowData(rowData);
        rowData.add(new Double(getAvgActive()));
        rowData.add(new Double(getAvgPrimaryActive()));
        rowData.add(new Double(getAvgGlobalActive()));
        return rowData;
    }

    @Override
    public List getRowDisplayData(List rowData) {
        rowData.add(toString());
        return rowData;
    }

    public double getEndValue() {
        return endValue;
    }


}
