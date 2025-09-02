package com.jamonapi.utils;

/** Very Simple Utility class used for Logging.  **/
public class Logger {
    String prefix="";

    protected Logger() {
    }

    private static Logger logger = new Logger();

    private static Logger createInstance() {
        return logger;
    }

    public static void log(Object obj) {
        createInstance().instanceLog(obj);
    }

    public static void logInfo(Object obj) {
        // This function will be able to be disabled at runtime.  i.e. do nothing whereas log is permanent.
        // for now they do the same thing however.
        createInstance().instanceLog(obj);
    }

    public static void logDebug(Object obj) {
        // This function will be able to be disabled at runtime.  i.e. do nothing whereas log is permanent.
        // for now they do the same thing however.
        createInstance().instanceLog(obj);
    }

    protected void instanceLog(Object obj) {
        System.out.println(prefix+obj);
    }
}

