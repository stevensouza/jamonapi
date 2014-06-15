package com.jamonapi;

/** Do nothing monitor i.e. noop */
public class NullMonitor extends MonitorImp {

    public NullMonitor() {
        disable();
    }

}
