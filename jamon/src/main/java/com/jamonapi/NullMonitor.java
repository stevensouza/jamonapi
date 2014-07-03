package com.jamonapi;

/** Do nothing monitor i.e. noop */
public class NullMonitor extends MonitorImp {

    private static final long serialVersionUID = 4295103641818797645L;

    public NullMonitor() {
        disable();
    }

}
