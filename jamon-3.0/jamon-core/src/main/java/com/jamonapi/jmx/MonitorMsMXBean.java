package com.jamonapi.jmx;

/**
 * Interface that tracks range counts for 'ms.' based jamon monitors
 */
public interface MonitorMsMXBean extends MonitorMXBean {
    public long get_Count00_LessThan_0ms();
    public long get_Count01_0_10ms();
    public long get_Count02_10_20ms();
    public long get_Count03_20_40ms();
    public long get_Count04_40_80ms();
    public long get_Count05_80_160ms();
    public long get_Count06_160_320ms();
    public long get_Count07_320_640ms();
    public long get_Count08_640_1280ms();
    public long get_Count09_1280_2560ms();
    public long get_Count10_2560_5120ms();
    public long get_Count11_5120_10240ms();
    public long get_Count12_10240_20480ms();
    public long get_Count13_GreaterThan_20480ms();
}
