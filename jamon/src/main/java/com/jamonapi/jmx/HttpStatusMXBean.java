package com.jamonapi.jmx;

/**
 * MxBean that counts httpStatus frequency.  1xx, 2xx, 3xx, 4xx, 5xx
 */

public interface HttpStatusMXBean {
    static final String UNITS = "httpStatus";
    static final String LABEL =  "com.jamonapi.http.response.getStatus().summary: ";

    public long get1xx();
    public long get2xx();
    public long get3xx();
    public long get4xx();
    public long get5xx();
}
