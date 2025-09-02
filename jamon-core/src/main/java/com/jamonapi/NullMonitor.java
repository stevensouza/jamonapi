package com.jamonapi;

/** Do nothing monitor i.e. noop */
public class NullMonitor extends MonitorImp {

    private static final long serialVersionUID = 278L;

    public NullMonitor() {
        disable();
    }

}
