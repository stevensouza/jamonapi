# JAMon 3.0 Core API

JAMon provides a simple, powerful API for monitoring Java applications. The core API is backward compatible with all previous JAMon versions while offering new modular capabilities.

## Quick Navigation

**Core Documentation:**
- [📋 Documentation Index](README.md) - Complete guide to all JAMon documentation
- [🚀 Getting Started](../README.md) - JAMon overview and quick start

**Integration Guides:**
- [🌐 Jakarta Servlet Filter](servlet-filter.md) - Web application HTTP monitoring
- [📊 SQL Monitoring](sql-monitoring.md) - Database and JDBC monitoring
- [🔗 HTTP Monitoring](http-monitoring.md) - Container-level HTTP monitoring
- [🎯 Spring AOP](spring-aop-monitoring.md) - Spring framework integration
- [📈 JMX Monitoring](jmx-monitoring.md) - JMX metrics and management
- [📝 Log4j Appender](log4j-appender.md) - Logging framework integration

**Advanced Features:**
- [👂 JAMon Listeners](listeners.md) - Event listeners for detailed monitoring
- [🌍 Distributed Monitoring](distributed-monitoring.md) - Hazelcast cluster integration
- [🖥️ JAMon Web App](jamon-war.md) - Web interface for monitoring data

## Basic Monitoring

### Simple Usage
```java
import com.jamonapi.MonitorFactory;
import com.jamonapi.Monitor;

// Basic timing monitor
Monitor mon = MonitorFactory.start("myOperation");
try {
    // Your business logic here
    doSomething();
} finally {
    mon.stop();
}

// View the results
System.out.println("Operation took: " + mon.getLastValue() + " ms");
System.out.println("Average time: " + mon.getAvg() + " ms");
System.out.println("Total hits: " + mon.getHits());
```

### Monitor Types
JAMon supports different types of monitors:

```java
// Timing monitor (default)
Monitor timingMon = MonitorFactory.start("operation.ms");

// Counter monitor  
Monitor counterMon = MonitorFactory.add("page.hits", 1);

// Custom units
Monitor sizeMon = MonitorFactory.add("file.size.bytes", fileSize);
Monitor requestMon = MonitorFactory.start("request.processing.seconds");
```

## Advanced Features

### Exception Monitoring
```java
try {
    riskyOperation();
} catch (Exception e) {
    MonitorFactory.add("exceptions.MyException", 1);
    MonitorFactory.add("exceptions.stacktrace", e.toString());
    throw e;
}
```

### Ranges and Listeners
```java
// Configure monitoring ranges
MonitorFactory.addListener("myLabel", new MyListener());

// Custom buffer listeners for detailed tracking
MonitorFactory.addListener("database.query", 
    new JAMonBufferListener("Top Queries", 100));
```

## JAMon 3.0 Modular Features

### Module-Specific APIs

**Jakarta Servlet Integration (jamon-http-jakarta):**
```java
// Automatic HTTP monitoring via filter - no code changes needed
// Manual HTTP monitoring:
HttpMon httpMon = HttpMonFactory.start(request, response);
// ... process request ...
httpMon.stop();
```

**Hazelcast Distributed Monitoring (jamon-hazelcast):**
```java
// Enable distributed monitoring
DistributedUtils.enableDistributedJAMon();

// Use normal JAMon API - data automatically distributed
Monitor mon = MonitorFactory.start("distributed.operation");
mon.stop();

// Access cluster-wide data
MonitorComposite clusterData = DistributedUtils.getClusterStatistics();
```

**Tomcat Valve Integration (jamon-tomcat):**
```xml
<!-- Configure in server.xml - no Java code needed -->
<Valve className="com.jamonapi.http.JAMonTomcatValve"
       summaryLabels="request.getRequestURI().ms, response.getStatus().value.httpStatus"/>
```

## Monitor Management

### Factory Operations
```java
// Enable/disable monitoring
MonitorFactory.setEnabled(true);
boolean isEnabled = MonitorFactory.isEnabled();

// Get monitoring statistics  
MonitorComposite rootMonitor = MonitorFactory.getRootMonitor();
int totalMonitors = MonitorFactory.getFactory().getNumRows();

// Reset all monitors
MonitorFactory.reset();

// Get specific monitor
Monitor specificMon = MonitorFactory.getMonitor("myLabel", "ms");
```

### Monitor Information
```java
Monitor mon = MonitorFactory.getMonitor("operation.ms");

// Timing statistics
double avgTime = mon.getAvg();
double minTime = mon.getMin(); 
double maxTime = mon.getMax();
double totalTime = mon.getTotal();
long hitCount = mon.getHits();

// Current state
boolean isActive = mon.isEnabled();
Date firstAccess = mon.getFirstAccess();
Date lastAccess = mon.getLastAccess();
```

## Best Practices

### Label Naming
Use descriptive, hierarchical labels:
```java
MonitorFactory.start("database.connection.mysql.ms");
MonitorFactory.start("service.userService.findById.ms");
MonitorFactory.start("cache.redis.get.ms");
```

### Resource Management
```java
// Always use try-finally or try-with-resources
Monitor mon = MonitorFactory.start("operation");
try {
    return performOperation();
} finally {
    mon.stop();
}

// Or use JAMon's auto-closeable monitors
try (Monitor mon = MonitorFactory.startAutoClose("operation")) {
    return performOperation();
}
```

### Performance Considerations
```java
// Check if monitoring is enabled before expensive operations
if (MonitorFactory.isEnabled()) {
    Monitor mon = MonitorFactory.start("expensive.operation");
    try {
        // Only do monitoring work if enabled
        performExpensiveMonitoring();
    } finally {
        mon.stop();
    }
}
```

## Integration Patterns

### Spring Integration
```java
@Component
public class MonitoringService {
    
    public void monitorMethod() {
        Monitor mon = MonitorFactory.start("service.monitoringService.operation");
        try {
            // Business logic
        } finally {
            mon.stop();
        }
    }
}
```

### JDBC Monitoring
```java
// JAMon provides automatic JDBC monitoring
DataSource monitoredDataSource = new JAMonDataSource(actualDataSource);

// All database operations automatically monitored
Connection conn = monitoredDataSource.getConnection();
// SQL execution times tracked automatically
```

## Migration from JAMon 2.x

### API Compatibility
The core JAMon API is 100% backward compatible:
- All `MonitorFactory` methods work identically
- All `Monitor` interface methods preserved  
- No code changes required for basic monitoring

### New Capabilities
JAMon 3.0 adds:
- **Modular architecture** - Include only needed components
- **Jakarta EE support** - Modern servlet containers
- **Enhanced distributed monitoring** - Hazelcast 5.5.0
- **Improved Tomcat integration** - Comprehensive valve testing

See the [Migration Guide](../MIGRATION_GUIDE_3.0.md) for complete upgrade instructions.