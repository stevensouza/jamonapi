# JAMon 3.0 Distributed Monitoring

## Quick Navigation

**Core Documentation:**
- [📋 Documentation Index](README.md) - Complete guide to all JAMon documentation
- [🚀 Getting Started](../README.md) - JAMon overview and quick start
- [⚡ Core API](core-api.md) - Basic JAMon monitoring concepts

**Related Guides:**
- [🌐 Jakarta Servlet Filter](servlet-filter.md) - Web application monitoring
- [📊 SQL Monitoring](sql-monitoring.md) - Database monitoring
- [🖥️ JAMon Web App](jamon-war.md) - Web interface for monitoring
- [👂 JAMon Listeners](listeners.md) - Event listeners for detailed monitoring

JAMon 3.0 provides powerful distributed monitoring capabilities through integration with Hazelcast 5.5.0. This allows you to aggregate monitoring data across multiple application instances in a cluster.

## Overview

Distributed JAMon allows you to:
- **Aggregate monitoring data** across cluster nodes
- **Share monitoring state** between application instances  
- **Persist monitoring data** to distributed storage
- **View cluster-wide statistics** from any node

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
        <artifactId>jamon-hazelcast</artifactId>
        <version>3.0</version>
    </dependency>
</dependencies>
```

### Runtime Requirements
- **Java 17+** (required for Hazelcast 5.5.0)
- **Hazelcast 5.5.0+** (major upgrade from 3.x in JAMon 2.x)
- **Network connectivity** between cluster nodes

## Configuration

### Hazelcast Configuration
Create `hazelcast.xml` in your classpath:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<hazelcast xmlns="http://www.hazelcast.com/schema/config"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://www.hazelcast.com/schema/config
           http://www.hazelcast.com/schema/config/hazelcast-config-5.5.xsd">

    <cluster-name>jamon-cluster</cluster-name>
    
    <network>
        <port auto-increment="true" port-count="100">5701</port>
        <join>
            <multicast enabled="true">
                <multicast-group>224.2.2.3</multicast-group>
                <multicast-port>54327</multicast-port>
            </multicast>
        </join>
    </network>
    
    <!-- JAMon distributed maps -->
    <map name="jamonData">
        <backup-count>1</backup-count>
        <eviction eviction-policy="NONE"/>
    </map>
    
</hazelcast>
```

### JAMon Distributed Configuration
Configure distributed persistence in your application:

```java
// Enable distributed monitoring
DistributedUtils.enableDistributedJAMon();

// Configure Hazelcast-based persistence
JamonDataPersisterFactory.setInstance(
    new HazelcastPersister("jamonData")
);

// Start periodic data synchronization (every 30 seconds)
DistributedUtils.startPeriodicSync(30000);
```

## Usage Examples

### Basic Distributed Setup
```java
import com.jamonapi.distributed.DistributedUtils;
import com.jamonapi.MonitorFactory;

// Initialize distributed monitoring
DistributedUtils.enableDistributedJAMon();

// Use JAMon normally - data automatically shared across cluster
Monitor mon = MonitorFactory.start("distributed.operation");
// ... business logic ...
mon.stop();

// View cluster-wide data
MonitorComposite clusterStats = DistributedUtils.getClusterStatistics();
```

### Web Application Integration
```java
@WebListener
public class JAMonDistributedListener implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Initialize distributed JAMon on application startup
        DistributedUtils.enableDistributedJAMon();
        DistributedUtils.startPeriodicSync(60000); // Sync every minute
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup on shutdown
        DistributedUtils.shutdown();
    }
}
```

## Migration from JAMon 2.x

### Breaking Changes
- **Hazelcast Version:** 3.x → 5.5.0 (API compatible but configuration changes)
- **Java Version:** Java 8+ → Java 17+ (Hazelcast 5.x requirement)
- **Module:** Moved to separate `jamon-hazelcast` module

### Migration Steps
1. **Update dependencies** to JAMon 3.0 modules
2. **Upgrade Hazelcast configuration** to 5.x format
3. **Update Java runtime** to 17+ for cluster nodes
4. **Test cluster connectivity** after upgrade

### Configuration Changes
JAMon 2.x Hazelcast 3.x config:
```xml
<!-- OLD - Hazelcast 3.x -->
<hazelcast>
    <group>
        <name>jamon</name>
    </group>
    <!-- ... -->
</hazelcast>
```

JAMon 3.0 Hazelcast 5.x config:
```xml
<!-- NEW - Hazelcast 5.x -->
<hazelcast>
    <cluster-name>jamon-cluster</cluster-name>
    <!-- ... -->
</hazelcast>
```

## Monitoring Distributed Applications

### Cluster-Wide Metrics
- **Node Health** - Monitor individual cluster member performance
- **Data Distribution** - Track how monitoring data spreads across nodes
- **Network Performance** - Monitor cluster communication overhead
- **Failover Behavior** - Observe monitoring during node failures

### Best Practices
1. **Monitor cluster size** - Track active cluster members
2. **Balance sync frequency** - Trade-off between data freshness and performance
3. **Handle network partitions** - Design for split-brain scenarios
4. **Resource monitoring** - Monitor Hazelcast memory usage

## Advanced Features

### Custom Data Persistence
```java
// Implement custom persistence strategy
public class CustomPersister implements JamonDataPersister {
    public void save(MonitorComposite data) {
        // Custom storage logic (database, file system, etc.)
    }
    
    public MonitorComposite load() {
        // Custom retrieval logic
        return loadFromCustomStorage();
    }
}

// Use custom persister
JamonDataPersisterFactory.setInstance(new CustomPersister());
```

### Monitoring Hazelcast Itself
```java
// Monitor Hazelcast operations
Monitor hazelcastMon = MonitorFactory.start("hazelcast.put");
hazelcastMap.put(key, value);
hazelcastMon.stop();
```

## Troubleshooting

### Common Issues

**HazelcastInstanceNotActiveException**
- Verify Hazelcast 5.5.0+ is in classpath
- Check cluster configuration is valid
- Ensure network connectivity between nodes

**ClassNotFoundException for distributed classes**
- Verify `jamon-hazelcast-3.0.jar` is in server classpath
- Check all cluster nodes have same JAMon version

**Performance degradation**
- Reduce sync frequency if cluster communication is expensive
- Monitor Hazelcast memory usage
- Consider local-only monitoring for high-throughput operations

**Split-brain scenarios**
- Configure Hazelcast split-brain protection
- Implement monitoring data reconciliation strategy

See the [Migration Guide](../MIGRATION_GUIDE_3.0.md) for additional troubleshooting information.