package com.jamonapi.utils;

public class DetailDataWrapper implements DetailData {
    private String[] header;
    private Object[][] data;

    public DetailDataWrapper(String[] header, Object[][] data) {
        this.header=header;
        this.data=data;
    }

    public String[] getHeader() {
        return header;
    }

    public Object[][] getData() {
        return data;
    }

}
