package com.jamonapi.jmx;

/**
 * MXBean that allows you to manage jamon
 */
public interface JamonMXBean {
    public String getVersion();
    public boolean getEnabled();
    public void setEnabled(boolean enable);
    public void reset();
}
