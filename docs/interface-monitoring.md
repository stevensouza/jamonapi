# JAMon Interface Monitoring

![JAMon Logo](images/jamon1.jpg)

## Quick Navigation

**Core Documentation:**
- [📋 Documentation Index](README.md) - Complete guide to all JAMon documentation
- [🚀 Getting Started](../README.md) - JAMon overview and quick start
- [⚡ Core API](core-api.md) - Basic JAMon monitoring concepts

**Related Guides:**
- [📊 SQL Monitoring](sql-monitoring.md) - JDBC interface monitoring example
- [🎯 Spring AOP](spring-aop-monitoring.md) - Better alternative for method monitoring
- [👂 JAMon Listeners](listeners.md) - Event listeners for detailed monitoring
- [🖥️ JAMon Web App](jamon-war.md) - Web interface for monitoring data

## Table of Contents

- [Monitoring Interfaces](#monitoring-interfaces)
- [Method Calls (JAMon Summary Statistics)](#method-calls-jamon-summary-statistics)
- [Exceptions (JAMon Summary Statistics)](#exceptions-jamon-summary-statistics)
- [Exceptions (Exception Details)](#exceptions-exception-details)
- [Note on Exception Monitoring](#note-on-exception-monitoring)

## Monitoring Interfaces

**Note:** Interface monitoring is useful, but if it is possible it is better to use JAMon with capabilities of Spring, AOP, EJB's to monitor your interfaces.

With one line of code you can monitor ANY java interface. Any interface means you can easily monitor standard java interfaces such as JDBC, open source interfaces such as the log4J appender interface or your own custom interfaces. To monitor an interface you simply have to call the following JAMon method:

```java
import com.jamonapi.proxy.*;

// The MyObject() class has to implement MyInterface for monitoring to work.
MyInterface myObject = (MyInterface) MonProxyFactory.monitor(new MyObject());
myObject.myMethod(); // method call will be monitored with JAMon.
```

That's it! Simply make method calls to myObject as you normally would, and the calls will be monitored. (Note it is best to hide the use of JAMon in a monitoring class of your own creation).

Any methods on the interface will be timed and any exceptions they throw will be tracked. The following `jamonadmin.jsp` screenshot shows methods called against JDBC interfaces. Any methods you call against your custom interfaces should look similar.

![JDBC Interface Monitoring](images/jamon_sql_jdbc.png)

## Method Calls (JAMon Summary Statistics)

JAMon summary statistics such as hits, time statistics (avg,total,min,max), concurrency statistics and more will be tracked for all methods of the interface. The JAMon label for monitoring of interface method calls always begins with: **MonProxy-Interface**. JAMon statistics are viewable via the `jamonadmin.jsp` page in `jamon.war` (discussed later). The following is an example of the JAMon label associated with the Connection class's `close()` method. Note the JAMon label consists of the concrete class as well as the interface's method signature.

```
MonProxy-Interface (class=org.hsqldb.jdbcConnection): public abstract void java.sql.Connection.close() throws java.sql.SQLException
```

Interface summary statistics are viewable via `jamonadmin.jsp`.

## Exceptions (JAMon Summary Statistics)

JAMon summary statistics are kept whenever a monitored interface throws an Exception.

Any time a monitored interface throws an exception several JAMon summary records will be added. JAMon labels for Exception summary statistics begin with: **MonProxy-Exception**. These summary statistics allow developers to easily see how many Exceptions the application has thrown. For each exception that the interface throws the following three types of JAMon summary labels will appear in the report:

### Exception Summary Types

- **General Exception Count** - How many Exceptions were thrown by all monitored interfaces. This allows you to easily see how many Exceptions your applications have thrown as well as when it last threw one.
  - Example: `MonProxy-Exception: InvocationTargetException`

- **Specific Exception Types** - How many Exceptions of each exception type was thrown. For SQL exceptions the ErrorCode and SQLState are also added. This will be discussed further below.
  - Example: `MonProxy-Exception: Root cause exception=java.sql.SQLException,ErrorCode=-22,SQLState=S0002`

- **Method-Specific Exceptions** - How many exceptions were thrown by each method of the monitored interfaces.
  - Example: `MonProxy-Exception: (class=org.hsqldb.jdbcStatement) Exception: public abstract java.sql.ResultSet java.sql.Statement.executeQuery(java.lang.String) throws java.sql.SQLException`

Exception summary statistics are viewable via `jamonadmin.jsp`.

## Exceptions (Exception Details)

The details of the N most recent Exceptions thrown by monitored interfaces (including the stack trace) are kept in a rolling buffer and viewable via `exceptions.jsp`. The exception buffer size is configurable via the web page, and defaults to the most recent 50 exceptions. Typically such stack trace information is only available in a log which a developer might not have access to and even if they do the log must be parsed to view the stack traces.

Each row in the report represents a recent Exception thrown from a monitored interface. Each row contains the following columns:

### Exception Detail Columns

- **ID** - An increasing integer that indicates the number of the exception since the server was last booted
- **StartTime** - The time the exception was thrown
- **ExceptionStackTrace** - The stack trace of the exception
- **MethodName** - The method name of the monitored interface's method that threw the exception

Exception details statistics are viewable via `exceptions.jsp`.

## Note on Exception Monitoring

One nice thing about Exception monitoring is that even when developers gobble/hide exceptions in the following manner they will show up in the JAMonAdmin report and exception detail report. The following example is taken from the JAMon demo:

```java
// Throw an exception and show that it is also monitored in jamonadmin.jsp and exceptions.jsp
// Note also even though the catch block is empty it will show up in these pages.
try {
    // get a query to throw an Exception. If enabled will show in jamon report and sql details.
    st.executeQuery("select * from i_do_not_exist");
} catch (Exception e) {
    // Empty catch block - exception still tracked by JAMon!
}
```

This capability is invaluable for discovering hidden exceptions that might otherwise go unnoticed in production applications.

## Best Practices

### When to Use Interface Monitoring
- **Legacy systems** where AOP is not available
- **Third-party libraries** where you cannot modify source code
- **Quick prototyping** for understanding interface usage patterns
- **JDBC monitoring** (though JAMon's built-in JDBC proxy is preferred)

### Alternatives to Consider
- **Spring AOP** - [Spring AOP Monitoring](spring-aop-monitoring.md) for Spring-based applications
- **EJB Interceptors** - For EJB-based applications
- **AspectJ** - For compile-time weaving scenarios
- **Built-in JAMon Features** - Like JDBC proxy for database monitoring

### Performance Considerations
- Interface monitoring uses dynamic proxies which have some overhead
- Monitor at appropriate granularity - not every method call
- Consider using JAMon's enable/disable features in production
- Be mindful of memory usage with exception tracking

## Related Documentation

- [SQL Monitoring](sql-monitoring.md) - JDBC interface monitoring example
- [Spring AOP Monitoring](spring-aop-monitoring.md) - Preferred approach for Spring applications
- [JAMon Listeners](listeners.md) - Event listeners for detailed interface monitoring
- [Core API Guide](core-api.md) - Basic JAMon monitoring concepts