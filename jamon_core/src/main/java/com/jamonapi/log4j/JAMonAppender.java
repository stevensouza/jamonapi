package com.jamonapi.log4j;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import com.jamonapi.JAMonListenerFactory;
import com.jamonapi.MonKey;
import com.jamonapi.MonKeyImp;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.jamonapi.utils.DefaultGeneralizer;
import com.jamonapi.utils.Generalizer;

/**
 * Implementaton of a log4j Appender that allows you to summarize log4j stats via jamon and view
 * the tail of the log in realtime in a jamon web page.  Click here for more info on how to use the <a href="http://jamonapi.sourceforge.net/log4j_jamonappender.html">JAMonAppender</a>.
 */

public class JAMonAppender extends AppenderSkeleton {
    /* Prefix for this classes jamon monitor labels */
    private final String PREFIX = "com.jamonapi.log4j.JAMonAppender.";

    // any of these poperties can be overridden via log4j configurators.
    private int bufferSize = 100;

    private String units = "log4j"; // units in jamon montiors

    // indicates whether or not log4j LoggingEvent info is placed in buffer.
    // This could potentially be slower though I didn't test it, and I
    // wouldn't be overly concerned about it.
    private boolean enableListenerDetails = true;

    // Enable monitoring of the various log4j levels in jamon.
    private boolean enableLevelMonitoring = true;

    private boolean generalize = false;

    private Generalizer generalizer = new DefaultGeneralizer();

    static {
        // Register this object to be available for use in the
        // JAMonListenerFactory.
        JAMonListenerFactory.put(new Log4jBufferListener());
    }

    public JAMonAppender() {
    }

    /**
     * If the appender is enabled then start and stop a JAMon entry. Depending
     * on how this object is configured it may also put details into a
     * JAMonBufferLister and generalize the logging message
     * (logger.error(message) etc) and put it in jamon too. By default it will
     * only do jamon records for each of the log4j Levels.
     * 
     * @param event
     */
    @Override
    protected void append(LoggingEvent event) {

        String message = (getLayout() == null) ? event.getRenderedMessage() : getLayout().format(
                event);
        if (getEnableLevelMonitoring()) {
            // monitor that counts all calls to log4j logging methods
            MonitorFactory.add(createKey(PREFIX + "TOTAL", message, event), 1);
            // monitor that counts calls to log4j at each level (DEBUG/WARN/...)
            MonitorFactory.add(createKey(PREFIX + event.getLevel(), message, event), 1);
        }

        // if the object was configured to generalize the message then do as
        // such. This will create a jamon record with the generalized method
        // so it is important for the developer to ensure that the generalized
        // message is unique enough not to grow jamon unbounded.
        if (getGeneralize()) {
            MonitorFactory.add(createKey(generalize(message), message, event), 1);
        }

    }

    // Return a key that will put LoggingEvent info in a bufferlistenr if
    // enableListenerDetails has been enabled,
    // else simply use the standard jamon MonKeyImp
    private MonKey createKey(String summaryLabel, String detailLabel,
            LoggingEvent event) {
        if (enableListenerDetails) // put array in details buffer
            return new Log4jMonKey(summaryLabel, detailLabel, units, event);
        else
            return new MonKeyImp(summaryLabel, detailLabel, units);

    }

    /**  Required log4j method. Currently a no-op. */
    @Override
    public void close() {

    }

    /**
     * JAMonAppender doesn't have to have a layount because it is acceptable to
     * default to using the raw message. Not providing a layout will return a
     * log4j error that looks like the following, however it can safely be
     * ignored. Providing any layout for the JAMonAppender will make the error
     * go away. Unfortunately log4j doesn't have a way to specify an optional
     * layout.
     * 
     * <p>
     * log4j:ERROR Could not find value for key
     * log4j.appender.jamonAppender.layout
     * </p>
     */
    @Override
    public boolean requiresLayout() {
        return true;
    }

    /**
     * @return Returns the units. By default this is 'log4j' though it can be
     *         changed. This is used as part of the jamon key.
     */
    public String getUnits() {
        return units;
    }

    /**
     * @param units
     *            The units to set.
     */
    public void setUnits(String units) {
        this.units = units;
    }

    /**
     * Specifies whether or not LoggingEvent info will be used in the attached
     * Log4jBufferListener. By default this is enabled.
     */
    public boolean getEnableListenerDetails() {
        return enableListenerDetails;
    }

    /**
     * Specify whether or not LoggingEvent info will be used in the attached
     * Log4jBufferListener
     */
    public void setEnableListenerDetails(boolean arrayDetails) {
        this.enableListenerDetails = arrayDetails;
    }

    /**
     * Specifies whether or not there will be a JAMon record for each log4j
     * Level (DEBUG/WARN/...), and another one that corresponds to all calls to
     * log4j logging methods. It is identified by the label TOTAL. By default
     * this is enabled.
     */
    public void setEnableLevelMonitoring(boolean enableLevelMonitoring) {
        this.enableLevelMonitoring = enableLevelMonitoring;

    }

    /** Returns whether or not LevelMonitoring is enabled or not. */
    public boolean getEnableLevelMonitoring() {
        return enableLevelMonitoring;
    }

    /**
     * Note this is primarily used by the log4j configurator. Valid values are
     * the various log4j levels:
     * 
     * <ul>
     * <li>DEBUG/ERROR/WARN/INFO/ERROR/FATAL, as well as...
     * <li>TOTAL (A listener that gets called for all levels),
     * <li>BASIC (same as calling TOTAL/ERROR/FATAL),
     * <li>ALL (same as calling ERROR/WARN/INFO/ERROR/FATAL/TOTAL).
     * </ul>
     * 
     * <p>Note: Values are not case sensitive.
     * 
     * @param level
     */
    public void setEnableListeners(String level) {
        if (Level.DEBUG.toString().equalsIgnoreCase(level.toUpperCase()))
            addDefaultListener(MonitorFactory.getMonitor(PREFIX + Level.DEBUG, units));
        else if (Level.INFO.toString().equalsIgnoreCase(level.toUpperCase()))
            addDefaultListener(MonitorFactory.getMonitor(PREFIX + Level.INFO, units));
        else if (Level.WARN.toString().equalsIgnoreCase(level.toUpperCase()))
            addDefaultListener(MonitorFactory.getMonitor(PREFIX + Level.WARN, units));
        else if (Level.ERROR.toString().equalsIgnoreCase(level.toUpperCase()))
            addDefaultListener(MonitorFactory.getMonitor(PREFIX + Level.ERROR, units));
        else if (Level.FATAL.toString().equalsIgnoreCase(level.toUpperCase()))
            addDefaultListener(MonitorFactory.getMonitor(PREFIX + Level.FATAL, units));
        else if ("TOTAL".toString().equalsIgnoreCase(level.toUpperCase()))
            addDefaultListener(MonitorFactory.getMonitor(PREFIX + "TOTAL", units));
        else if (Level.ALL.toString().equalsIgnoreCase(level.toUpperCase())) {
            addDefaultListener(MonitorFactory.getMonitor(PREFIX + Level.DEBUG, units));
            addDefaultListener(MonitorFactory.getMonitor(PREFIX + Level.INFO, units));
            addDefaultListener(MonitorFactory.getMonitor(PREFIX + Level.WARN, units));
            addDefaultListener(MonitorFactory.getMonitor(PREFIX + Level.ERROR, units));
            addDefaultListener(MonitorFactory.getMonitor(PREFIX + Level.FATAL, units));
            addDefaultListener(MonitorFactory.getMonitor(PREFIX + "TOTAL", units));
        } else if ("BASIC".toString().equalsIgnoreCase(level.toUpperCase())) {
            addDefaultListener(MonitorFactory.getMonitor(PREFIX + "TOTAL", units));
            addDefaultListener(MonitorFactory.getMonitor(PREFIX + Level.ERROR, units));
            addDefaultListener(MonitorFactory.getMonitor(PREFIX + Level.FATAL, units));
        }
    }

    // Add a Log4jBufferListener to the passed in Monitor
    private void addDefaultListener(Monitor mon) {
        if (!mon.hasListeners()) {
            Log4jBufferListener listener = new Log4jBufferListener();
            listener.getBufferList().setBufferSize(bufferSize);
            mon.addListener("value", listener);
        }
    }

    /**
     * For defaultBufferSize to take hold it must be called before the first
     * call to setDefaultListeners. By default the buffer size is 100.
     * 
     * @param bufferSize
     */
    public void setListenerBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    /** Indicate whether or not a jamon record should be created from the passed in message.
     * Note you can use the DefaultGeneralizer, your own Generalizer.  It is very important that
     * you ensure the String returned by the generalizer is unique enough that JAMon doesn't grow unbounded.
     * For example by choosing to use no Generalizer you must pass in a relatively unique log4j string.
     * @param generalize
     */
    public void setGeneralize(boolean generalize) {
        this.generalize = generalize;
    }

    /** Return whether or not generalization will occur */
    public boolean getGeneralize() {
        return generalize;
    }

    /** generalize the passed in String if a Genaralizer is set */
    private String generalize(String detailedMessage) {
        return (generalizer != null) ? generalizer.generalize(detailedMessage)
                : detailedMessage;
    }

    /** Enable the use of the DefaultGeneralizer. As a side effect setGeneralize(true) is called
     * telling this class to generalize.
     * @param enableDefaultGeneralizer
     */
    public void setEnableDefaultGeneralizer(boolean enableDefaultGeneralizer) {
        if (enableDefaultGeneralizer) {
            this.generalizer = new DefaultGeneralizer();
            setGeneralize(true);
        } else
            this.generalizer = null;
    }

    /** Indicates whether or not a Generalizer has been set */
    public boolean hasGeneralizer() {
        return (generalizer != null);
    }

    /**
     * Default generalizer based on com.jamonapi.utils.SQLDeArger. It
     * generalizes by replacing numbers and strings in single or double quotes
     * with '?'. i.e. select * from table where name = 'steve' and id=50 becomes
     * select * from table where name = ? and id=?. Developers can provide their
     * own Generalizer if this is not the desired behaviour. Although the
     * example uses a query the code works equally well with any String. The
     * generalizer is used to create a record appropriate for jamon from a
     * detail String that goes to log4j.
     */
    public void setGeneralizerClass(Generalizer generalizer) {
        this.generalizer = generalizer;
    }

    /** Pass in a string class name and this generalizer will be constructed an used.  For example com.jamonapi.utils.DefaultGeneralizer could be passed in
     * 
     * @param generalizerClassStr
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public void setGeneralizerDynamic(String generalizerClassStr) throws InstantiationException,
    IllegalAccessException, ClassNotFoundException {
        this.generalizer = (Generalizer) Class.forName(generalizerClassStr).newInstance();
    }

}
