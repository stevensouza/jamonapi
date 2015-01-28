package com.jamonapi.jmx;


/**
 * Value object that holds the label, units and logical name of a monitor.  This is used
 * to get values from jamon for display in jmx.
 */
public class JamonJmxBeanPropertyDefault implements JamonJmxBeanProperty {
    protected final String label;
    protected final String units;
    protected final String name;

    public JamonJmxBeanPropertyDefault(String label, String units, String name) {
        this.label = label;
        this.units = units;
        this.name = name;
    }

    /** example: com.jamonapi.Exceptions */
    public String getLabel() {
        return label;
    }

    /** example: Exception */
    public String getUnits() {
        return units;
    }

    /** Return logical name to be used instead of label, or empty string if it doesn't exist. */
    public String getName() {
        return name;
    }
}
