package com.jamonapi.proxy;


import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import java.sql.*;
import com.jamonapi.*;

/** This class will be a proxy for the underlying jdbc driver.
 * @author steve souza
 *
 */
public class JAMonDriver implements Driver
{
    private static final String jamonURL="jdbc:jamon:";
    static {
        try {
          registerDriver("com.jamonapi.proxy.JAMonDriver");
        } catch (Exception e) 
        {/* can't happen */};
    }
    
    
    private static Driver registerDriver(String className) throws SQLException {
        Driver d=null;
        
        try {
           d=(Driver) Class.forName(className).newInstance();
           DriverManager.registerDriver(d);
        } catch (Exception e){
           String message="MonProxy-Exception: loading JDBC Driver="+e.getMessage();
           MonitorFactory.add(message, "Exception",1);
           DriverManager.println(message);
           throw new SQLException("Can not load real driver ("+className+") from JAMonDriver: "+e.getLocalizedMessage());
        }
        return d;
               
     }
    
    

      
    /**
     *  (non-Javadoc)
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
           MonitorFactory.add("MonProxy-Exception: Root cause exception="+sqlException.getClass().getName()+sqlMessage,"Exception",1); // Message for the exception
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
    private static class URLInfo {
        String realDriverName;
        String realURL;
        String jamonURL;
 
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

    
    
    public static void main(String[] args) throws Exception {
        String drivers []= {"jdbc:jamon:sybase:Tds:myserver:1234/mydatabase?jamonrealdriver=com.sybase.jdbc3.jdbc.SybDriver&LITERAL_PARAMS=true&PACKETSIZE=512&HOSTNAME=myhost"
                ,"jdbc:jamon:sybase:Tds:myserver:1234/mydatabase?LITERAL_PARAMS=true&PACKETSIZE=512&HOSTNAME=myhost&jamonrealdriver=com.sybase.jdbc3.jdbc.SybDriver2"
                ,"jdbc:jamon:sybase:Tds:myserver:1234/mydatabase?LITERAL_PARAMS=true&PACKETSIZE=512&jamonrealdriver=com.sybase.jdbc3.jdbc.SybDriver&HOSTNAME=myhost"
                ,"jdbc:jamon:sybase:Tds:myserver:1234/mydatabase?LITERAL_PARAMS=true&PACKETSIZE=512&jamonrealdriver=com.sybase.jdbc3.jdbc.SybDriver&HOSTNAME=myhost"
                ,"jdbc:jamon:hsqldb:.jamonrealdriver = org.hsqldb.jdbcDriver;"
                ,"jdbc:jamon:hsqldb:.jamonrealdriver = org.hsqldb.jdbcDriver:"
                ,"jdbc:jamon:hsqldb:.jamonrealdriver = org.hsqldb.jdbcDriver"
                ,"jdbc:jamon:microsoft:sqlserver://localhost:1433;jamonrealdriver=com.microsoft.jdbc.sqlserver.SQLServerDriver"
                ,"jdbc:jamon:microsoft:sqlserver:sybase:Tds:127.0.0.1:5000/dbname;jamonrealdriver=com.sybase.jdbc3.jdbc.SybDriver"
                ,"jdbc:jamon:informix-sqli://161.144.202.206:3000:INFORMIXSERVER=stars:jamonrealdriver=   com.informix.jdbc.IfxDriver:"};
        
        for (int i=0;i<drivers.length;i++) {
            URLInfo ui=new URLInfo(drivers[i], null);
            System.out.println("\ni="+i+"\njamonURL="+ui.getJAMonURL());
            System.out.println("readdrivername="+ui.getRealDriverName());
            System.out.println("realurl="+ui.getRealURL());
        }

  
        for (int i=0;i<5;i++) {
            Properties props=new Properties();
            props.put("user","sa");
            props.put("password","");
            Connection conn=null;
            if (i%2==0) {
                props.put("jamonrealdriver", "org.hsqldb.jdbcDriver");
                conn = DriverManager.getConnection("jdbc:jamon:hsqldb:.",  props);
            } else
                conn = DriverManager.getConnection("jdbc:jamon:hsqldb:.jamonrealdriver=org.hsqldb.jdbcDriver",  props);
            
            Statement s=conn.createStatement();
            s.close();
            conn.close();
        }
     
        System.out.println(MonitorFactory.getReport());
            
    }
    
  
}



