# JAMon 3.0 Jakarta Servlet Filter

## Quick Navigation

**Core Documentation:**
- [📋 Documentation Index](README.md) - Complete guide to all JAMon documentation
- [🚀 Getting Started](../README.md) - JAMon overview and quick start
- [⚡ Core API](core-api.md) - Basic JAMon monitoring concepts

**Related Guides:**
- [🔗 HTTP Monitoring](http-monitoring.md) - Container-level HTTP monitoring
- [📊 SQL Monitoring](sql-monitoring.md) - Database monitoring
- [👂 JAMon Listeners](listeners.md) - Event listeners for detailed monitoring
- [🖥️ JAMon Web App](jamon-war.md) - Web interface for monitoring data

A Servlet filter is a simple piece of Java code that is executed whenever a Java Web Application resource is accessed. Resources include Servlets, JSP's, GIFs, JPEGs, and HTML documents. The JAMon servlet filter monitors all of these resources and becomes a powerful web site hit counter. Statistics for pages will be gathered such as hits, and execution time (avg, min, max). In addition you can see which pages are currently executing.

## Key Features

- **Zero Code Changes Required** - No application modifications needed
- **Automatic Monitoring** - Tracks all HTTP requests and responses  
- **Performance Metrics** - Response times, hit counts, concurrent users
- **Flexible Configuration** - Choose which URLs to monitor

## JAMon 3.0 Requirements

### Dependencies
```xml
<dependencies>
    <dependency>
        <groupId>com.jamonapi</groupId>
        <artifactId>jamon-core</artifactId>
        <version>3.0</version>
    </dependency>
    
    <dependency>
        <groupId>com.jamonapi</groupId>
        <artifactId>jamon-http-jakarta</artifactId>
        <version>3.0</version>
    </dependency>
</dependencies>
```

### Runtime Requirements
- **Java 17+** (required for Jakarta EE namespace)
- **Jakarta Servlet API 6.0+** (not javax.servlet)
- **Compatible servers:** Tomcat 11+, Jetty 12+, or other Jakarta EE servers

## Configuration

### web.xml Configuration
Add the following to your `web.xml` file:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee 
         https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd"
         version="6.0">

    <display-name>Your Application</display-name>

    <!-- JAMon Servlet Filter -->
    <filter>
        <filter-name>JAMonFilter</filter-name>
        <filter-class>com.jamonapi.http.JAMonServletFilter</filter-class>
        <init-param>
            <param-name>summaryLabels</param-name>
            <param-value>request.getRequestURI().ms, response.getStatus().value.httpStatus</param-value>
        </init-param>
    </filter>

    <filter-mapping>
        <filter-name>JAMonFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

    <!-- Your servlets come after filter definitions -->
    <servlet>
        <servlet-name>YourServlet</servlet-name>
        <servlet-class>com.yourapp.YourServlet</servlet-class>
    </servlet>

    <servlet-mapping>
        <servlet-name>YourServlet</servlet-name>
        <url-pattern>/your-servlet</url-pattern>
    </servlet-mapping>

</web-app>
```

### Configuration Options

The JAMon servlet filter supports several configuration parameters:

| Parameter | Description | Default |
|-----------|-------------|---------|
| `summaryLabels` | Comma-separated list of monitoring labels | `default` |
| `enabled` | Enable/disable monitoring | `true` |
| `size` | Maximum number of monitors to maintain | 500 |

### Example Configurations

**Monitor specific URL patterns:**
```xml
<filter-mapping>
    <filter-name>JAMonFilter</filter-name>
    <url-pattern>*.jsp</url-pattern>
</filter-mapping>
```

**Monitor with custom labels:**
```xml
<init-param>
    <param-name>summaryLabels</param-name>
    <param-value>request.getRequestURI().ms, response.getContentLength().bytes, response.getStatus().value.httpStatus</param-value>
</init-param>
```

## Deployment

### Server-Level Installation
1. Copy JAMon 3.0 JARs to server common library directory:
   - `jamon-core-3.0.jar`
   - `jamon-http-jakarta-3.0.jar`

2. Deploy your web application with the filter configuration

3. Optional: Deploy `jamon-3.0.war` for monitoring web interface

### Tomcat 11+ Example
```bash
# Copy JARs to Tomcat common lib
cp jamon-core-3.0.jar $TOMCAT_HOME/lib/
cp jamon-http-jakarta-3.0.jar $TOMCAT_HOME/lib/

# Deploy your application
cp your-app.war $TOMCAT_HOME/webapps/

# Optional: Deploy JAMon admin interface  
cp jamon-3.0.war $TOMCAT_HOME/webapps/
```

## Migration from JAMon 2.x

### Breaking Changes
- **Namespace:** `javax.servlet` → `jakarta.servlet`
- **Java Version:** Java 8+ → Java 17+
- **Module:** Moved from monolithic jar to `jamon-http-jakarta` module

### Migration Steps
1. Update Maven dependencies (see Requirements above)
2. Update web.xml namespace to Jakarta EE
3. Verify servlet container supports Jakarta EE (Tomcat 11+)
4. No code changes required - filter configuration remains the same

## Monitoring Data

The servlet filter automatically creates monitors for:
- **Request URIs** - Response times for each page/endpoint
- **HTTP Status Codes** - Count of 200, 404, 500, etc. responses  
- **Current Activity** - Real-time view of active requests
- **Error Tracking** - Automatic exception monitoring

## Viewing Results

Access monitoring data through:
- **JAMon Admin Interface** - Deploy `jamon-3.0.war` and visit `/jamon-admin`
- **Programmatic Access** - Use `MonitorFactory` API in your code
- **JMX** - Export monitoring data via JMX beans

## Troubleshooting

### Common Issues

**ClassNotFoundException: com.jamonapi.http.JAMonServletFilter**
- Ensure `jamon-http-jakarta-3.0.jar` is in server classpath
- Verify server supports Jakarta EE namespace

**No monitoring data appearing**
- Check filter is properly configured in web.xml
- Verify filter-mapping URL patterns match your requests
- Ensure JAMon is enabled: `MonitorFactory.isEnabled()`

**IllegalStateException during startup**
- Verify correct Jakarta Servlet API version (6.0+)
- Check server supports Jakarta EE (not legacy javax.servlet)

See the [Migration Guide](../MIGRATION_GUIDE_3.0.md) for additional troubleshooting information.

## Legacy JAMon 2.x Servlet Filter

For users still on JAMon 2.x with `javax.servlet` namespace:

### Legacy web.xml Configuration
```xml
<web-app>
    <display-name>YourApp</display-name>
    
    <filter>
        <filter-name>JAMonFilter</filter-name>
        <filter-class>com.jamonapi.JAMonFilter</filter-class>
    </filter>

    <filter-mapping>
        <filter-name>JAMonFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

    <!-- Servlet filter elements come before servlet elements -->
    <servlet>
        <servlet-name>demo</servlet-name>
        <jsp-file>/demo.jsp</jsp-file>
    </servlet>

</web-app>
```

**Note:** The order of entries in your web.xml file is important. Filter definitions must come before servlet definitions.

## Related Documentation

- [HTTP Monitoring](http-monitoring.md) - Container-level HTTP monitoring (Tomcat Valve, Jetty Handler)
- [Core API Guide](core-api.md) - Basic JAMon monitoring concepts
- [JAMon Listeners](listeners.md) - Event listeners for detailed HTTP monitoring