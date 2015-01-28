package com.jamonapi.jmx;

/**
 * Represents a value used to grab a jmx entries values from a jamon monitor.  This is used
 * to configure jamon jmx.
 */
public interface JamonJmxBeanProperty {
        /**
         * example: com.jamonapi.Exceptions
         */
        String getLabel();

        /**
         * example: Exception
         */
        String getUnits();

        /**
         * Return logical name to be used instead of label, or empty string if it doesn't exist.
         */
        String getName();
}
