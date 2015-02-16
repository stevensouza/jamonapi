package com.jamonapi.jmx;

import javax.management.ObjectName;

/**
 * MXBean that exposes jamon http status counts for 1xx, 2xx, 3xx, 4xx, 5xx.  Where 4xx would include a count of
 * http status 404 and any else in the 400 series.
 *
 * http://en.wikipedia.org/wiki/List_of_HTTP_status_codes
 *
 */
public class HttpStatusMXBeanImp implements HttpStatusMXBean {
    public static ObjectName getObjectName() {
       return JmxUtils.getObjectName(HttpStatusMXBeanImp.class.getPackage().getName() + ":type=current,name=HttpStatusCodes");
    }

    private long getCount(String label) {
       return JmxUtils.getCount(LABEL+label, UNITS);
    }

    @Override
    public long get1xx() {
        return getCount("1xx");
    }

    @Override
    public long get2xx() {
        return getCount("2xx");
    }

    @Override
    public long get3xx() {
        return getCount("3xx");
    }

    @Override
    public long get4xx() {
        return getCount("4xx");
    }

    @Override
    public long get5xx() {
        return getCount("5xx");
    }
}
