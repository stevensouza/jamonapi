package com.jamonapi.proxy;

import com.jamonapi.MonKey;
import com.jamonapi.MonKeyImp;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.jamonapi.utils.Misc;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** This class will be a proxy for the underlying jdbc driver. */
public class JAMonDriver implements Driver
{
    private static final String jamonURL="jdbc:jamon:";
    static {
        try {
            registerDriver("com.jamonapi.proxy.JAMonDriver");
        } catch (Exception e)
        {/* can't happen */}
    }

    /** Register the JAMon driver.  Any access to this object in general will register the driver.
     * This method is provided to enable a more explicit way of registering it.
     */
    public static void register() {
        // this is a noop. But by accessing this method the static initializer automatically is automatically called which registers the JAMonDriver
    }


    private static Driver registerDriver(String className) throws SQLException {
        Driver d=null;

        try {
            d=(Driver) Class.forName(className).newInstance();
            DriverManager.registerDriver(d);
        } catch (Exception e){
            String message="MonProxy-Exception: loading JDBC Driver="+e.getMessage();
            MonitorFactory.add(new MonKeyImp(message, new Object[]{message, Misc.getExceptionTrace(e)}, "Exception"),1);
            DriverManager.println(message);
            throw new SQLException("Can not load real driver ("+className+") from JAMonDriver: "+e.getLocalizedMessage());
        }

        return d;

    }


    /**
     * @see java.sql.Driver#connect(java.lang.String, java.util.Properties)
     */
    public Connection connect(String url, Properties info) throws SQLException  {

        if (!acceptsURL(url))
            return null;
        // use jamonrealdriver from properties if it is there.
        URLInfo urlInfo=new URLInfo(url, info);
        Driver realDriver=getRegisteredDriver(urlInfo.getRealURL());

        if (realDriver==null) {
            // for example: com.sybase.jdbc2.jdbc.SybDriver
            realDriver=registerDriver(urlInfo.getRealDriverName());
        }


        Monitor mon = MonitorFactory.start("MonProxy-Interface (class=com.jamonapi.proxy.JAMonDriver): public java.sql.Connection com.jamonapi.proxy.JAMonDriver.connect(java.lang.String, java.util.Properties) throws java.sql.SQLException");
        Connection conn=null;
        try {
            conn=MonProxyFactory.monitor(realDriver.connect(urlInfo.getRealURL(), info));
        } catch (SQLException sqlException) {
            String sqlMessage=",ErrorCode="+sqlException.getErrorCode()+",SQLState="+sqlException.getSQLState();
            String label="MonProxy-Exception: Root cause exception="+sqlException.getClass().getName()+sqlMessage;
            MonKey key=new MonKeyImp(label, new Object[]{label, Misc.getExceptionTrace(sqlException)}, "Exception");
            MonitorFactory.add(key,1); // Message for the exception
            throw sqlException;
        } finally {
            mon.stop();
        }

        // call the following method just in case the realDriver returns a proxied connection itself.
        return conn;
    }

    private Driver getRegisteredDriver(String url) {
        Driver registeredDriver=null;
        try {
            registeredDriver = DriverManager.getDriver(url);
        } catch (Exception e) {/* not an error */}

        return registeredDriver;
    }

    /** Returns true if this driver can respond to the url  */
    public boolean acceptsURL(String url) throws SQLException {
        if (url==null)
            return false;
        else if (url.toLowerCase().startsWith(jamonURL))
            return true;
        else
            return false;
    }

    /** Returns this drivers properties.  Currently the only property is 'jamonrealdriver' */
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException     {
        return new DriverPropertyInfo[] {
                getPropertyInfo("jamonrealdriver",null, "The driver to be proxy monitored.  Example: jdbc:jamon:sybase:Tds:myserver:1234/mydatabase?LITERAL_PARAMS=true&PACKETSIZE=512&jamonrealdriver=com.sybase.jdbc3.jdbc.SybDriver&HOSTNAME=myhost", true),
        };
    }

    private static DriverPropertyInfo getPropertyInfo(String name, String value, String description, boolean required) {
        DriverPropertyInfo prop=new DriverPropertyInfo(name, value);
        prop.description=description;
        prop.required=required;

        return prop;
    }

    public Logger getParentLogger() {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

    /**
     * boolean	acceptsURL(String url)
     Retrieves whether the driver thinks that it can open a connection to the given URL.
     Connection	connect(String url, Properties info)
     Attempts to make a database connection to the given URL.
     int	getMajorVersion()
     Retrieves the driver's major version number.
     int	getMinorVersion()
     Gets the driver's minor version number.
     Logger	getParentLogger()
     Return the parent Logger of all the Loggers used by this driver.
     DriverPropertyInfo[]	getPropertyInfo(String url, Properties info)
     Gets information about the possible properties for this driver.
     boolean	jdbcCompliant()
     Reports whether this driver is a genuine JDBC CompliantTM driver.
     * @return
     */

    public int getMajorVersion() {
        return 2;
    }

    public int getMinorVersion()     {
        return 3;
    }

    public boolean jdbcCompliant()     {
        return true;
    }

    /** Takes a url of the jamon format: jdbc:jamon:sybase:Tds:myserver:1234/mydatabase?jamonrealdriver=com.sybase.jdbc3.jdbc.SybDriver&LITERAL_PARAMS=true&PACKETSIZE=512&HOSTNAME=myhost
        and returns:  com.sybase.jdbc3.jdbc.SybDriver
     */

    public static String getRealDriverName(String url) {
        return new URLInfo(url).getRealDriverName();
    }

    /** Takes a url of the jamon format: jdbc:jamon:sybase:Tds:myserver:1234/mydatabase?jamonrealdriver=com.sybase.jdbc3.jdbc.SybDriver&LITERAL_PARAMS=true&PACKETSIZE=512&HOSTNAME=myhost
    and returns the real url associated with the underlying driver: jdbc:sybase:Tds:myserver:1234/mydatabase?LITERAL_PARAMS=true&PACKETSIZE=512&HOSTNAME=myhost
     */
    public static String getRealURL(String url) {
        return new URLInfo(url).getRealURL();
    }



    // Parses a passed in URL.
    //@VisibleForTesting
    static class URLInfo {
        String realDriverName;
        String realURL;
        String jamonURL;
        int maxSqlSize;

        // sample url:  jdbc:jamon:hsqldb:.:pw=steve;jamonrealDriver=org.hsqldb.jdbcDriver;
        URLInfo(String jamonURL) {
            this(jamonURL,null);
        }
        // sample url:  jdbc:jamon:hsqldb:.:pw=steve;jamonrealDriver=org.hsqldb.jdbcDriver;
        URLInfo(String jamonURL, Properties info) {
            this.jamonURL=jamonURL;
            // .*jamonrealdriver - any characters up to jamonrealdriver
            //  [\\s=]* - any number of optional spaces surrounding the equal sign.
            // ([\\w\\.]*) - trying to get a package name which can have any number of characters [a-zA-Z_0-9] and '.'. This value is what is needed
            //  [\\W]? - 0 or 1 characters that aren't in a package name.
            realDriverName = (info==null) ? null : info.getProperty("jamonrealdriver");
            if (realDriverName==null)
                realDriverName=parseURL(".*jamonrealdriver[\\s=]*([\\w\\.]*)[\\W]?", jamonURL);

            // remove all traces of jamon in the url.  this might not strictly be required as drivers
            // probably ignore anything they don't recognize
            realURL = jamonURL.replaceAll("jdbc:jamon:", "jdbc:");
            realURL = realURL.replaceAll("jamonrealdriver[\\s=\\.\\w]*[\\W]?", "");
        }

        private String parseURL(String pattern, String url) {
            String result="";
            if (url==null)
                return result;

            Pattern re = Pattern.compile(pattern);
            Matcher matcher = re.matcher(url);
            if (matcher.lookingAt())
                result = matcher.group(1);

            return result.trim();
        }



        String getRealDriverName() {
            return realDriverName;
        }

        String getRealURL() {
            return realURL;
        }

        String getJAMonURL() {
            return jamonURL;
        }

    }

}



