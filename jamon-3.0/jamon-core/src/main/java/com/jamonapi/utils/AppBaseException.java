package com.jamonapi.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class AppBaseException extends java.lang.Exception {
    private static final long serialVersionUID = 278L;
    String errorIndicator="";

    public AppBaseException() {
        super();
    }

    public AppBaseException(String msg) {
        super(msg);
    }

    public AppBaseException(String msg, String errorIndicator) {
        this(msg);
        this.errorIndicator = errorIndicator;
    }

    public String getErrorIndicator() {
        return errorIndicator;
    }

    public static RuntimeException getRuntimeException(Exception e) {
        // NOTE THIS should eventually be replaced by the CommandIterator.iterate() function that throws
        // a runtimeexception instead of an Exception
        StringWriter sw=new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return new RuntimeException(sw.toString());
    }
}

