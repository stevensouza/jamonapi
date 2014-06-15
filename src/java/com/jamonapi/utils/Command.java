package com.jamonapi.utils;

/**
 * Simple interface that is used in the implementation of the Gang Of 4 Command pattern in Java.
 * Implement this Interface to pass a command to an internal iterator.
 **/
public interface Command {
    public void execute(Object value) throws Exception;
}

