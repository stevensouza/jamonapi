package com.jamonapi.log4j;

import com.jamonapi.MonKeyImp;
import org.apache.log4j.spi.LoggingEvent;

/**
 * MonKey used to put log4j records into jamon hashmap. It is the same as
 * MonKeyImp except it also carries with it a log4j LoggingEvent. This is not
 * used as part of the key, but is used to display log4j info in any
 * BufferListeners this object has. To take maximum advantage of the data in the
 * LoggingEvent of this key use a Log4jBufferListener for log4j JAMon monitors.
 * A regular FIFO buffer can also be used, however all info in the LoggingEvent
 * won't be used in this case.
 * 
 */
public class Log4jMonKey extends MonKeyImp {

    private static final long serialVersionUID = 279L;

    /** Constructor for building jamon key for log4j */
    public Log4jMonKey(String summaryLabel, String detailLabel, String units,
            LoggingEvent event) {
        super(summaryLabel, detailLabel, units);
        setParam(event);
    }

    /** Return the log4j LoggingEvent object that is part of this key */
    public LoggingEvent getLoggingEvent() {
        return (LoggingEvent) getParam();
    }

    /**
     * Returns any object that has a named key. For this object 'label' and
     * 'units', and 'LoggingEvent' are valid. It is case insenstive.
     */
    @Override
    public Object getValue(String key) {
        if ("LoggingEvent".equalsIgnoreCase(key))
            return getParam();
        else
            return super.getValue(key);
    }

}
