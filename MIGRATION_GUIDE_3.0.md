# JAMon 2.x to 3.0 Migration Guide

## Overview

JAMon 3.0 introduces a major architectural change from a monolithic library to a modular system. This guide helps you migrate from JAMon 2.x to 3.0.

## Key Changes

### 1. Modular Architecture
- **JAMon 2.x:** Single `jamon-2.85.jar` with all functionality
- **JAMon 3.0:** Separate modules for different integrations

### 2. Java Version Requirements
- **Core functionality:** Still supports Java 8+
- **Modern integrations:** Require Java 17+ (Jakarta EE, Hazelcast 5.x, Tomcat 11)

## Migration Steps

### Step 1: Update Dependencies

**Before (JAMon 2.x):**
```xml
<dependency>
    <groupId>com.jamonapi</groupId>
    <artifactId>jamon</artifactId>
    <version>2.85</version>
</dependency>
```

**After (JAMon 3.0):**
```xml
<!-- Choose modules based on your needs -->
<dependencies>
    <!-- Required: Core functionality -->
    <dependency>
        <groupId>com.jamonapi</groupId>
        <artifactId>jamon-core</artifactId>
        <version>3.0</version>
    </dependency>
    
    <!-- Optional: Jakarta Servlet support (Java 17+) -->
    <dependency>
        <groupId>com.jamonapi</groupId>
        <artifactId>jamon-http-jakarta</artifactId>
        <version>3.0</version>
    </dependency>
    
    <!-- Optional: Distributed monitoring (Java 17+) -->
    <dependency>
        <groupId>com.jamonapi</groupId>
        <artifactId>jamon-hazelcast</artifactId>
        <version>3.0</version>
    </dependency>
    
    <!-- Optional: Tomcat valve integration (Java 17+) -->
    <dependency>
        <groupId>com.jamonapi</groupId>
        <artifactId>jamon-tomcat</artifactId>
        <version>3.0</version>
    </dependency>
</dependencies>
```

### Step 2: Code Changes

#### Basic Monitoring (No Changes Required)
Core JAMon API remains unchanged:
```java
import com.jamonapi.MonitorFactory;
import com.jamonapi.Monitor;

// This code works identically in 2.x and 3.0
Monitor mon = MonitorFactory.start("myLabel");
// ... business logic ...
mon.stop();
```

#### Servlet Filter Changes
**JAMon 2.x:** Classes in main JAMon jar
**JAMon 3.0:** Moved to `jamon-http-jakarta` module

**Before:**
```xml
<!-- web.xml - JAMon 2.x -->
<filter>
    <filter-name>JAMonFilter</filter-name>
    <filter-class>com.jamonapi.http.JAMonServletFilter</filter-class>
</filter>
```

**After:** 
- Add `jamon-http-jakarta` dependency
- Same configuration works (class moved to new module)

#### Tomcat Valve Changes
**JAMon 2.x:** Valve in main JAMon jar
**JAMon 3.0:** Moved to `jamon-tomcat` module

**Before:**
```xml
<!-- server.xml - JAMon 2.x -->
<Valve className="com.jamonapi.http.JAMonTomcatValve"/>
```

**After:**
- Add `jamon-tomcat` dependency
- Same configuration works (class moved to new module)

#### Hazelcast Integration Changes
**JAMon 2.x:** Used Hazelcast 3.x
**JAMon 3.0:** Uses Hazelcast 5.5.0 (requires Java 17+)

**Code changes required:** None - API preserved
**Deployment changes:** Requires Java 17+ runtime

## Framework Compatibility Matrix

### Supported Versions

| Framework | JAMon 2.x | JAMon 3.0 | Notes |
|-----------|-----------|-----------|-------|
| **Java** | 8+ | 8+ (core), 17+ (modern modules) | Modular approach |
| **Servlet API** | javax.servlet 2.4+ | jakarta.servlet 6.0.0+ | Namespace change |
| **Tomcat** | 6.x, 7.x, 8.x, 9.x | 11.0.2+ | Major version jump |
| **Jetty** | 9.x, 10.x | Not supported | Use JAMon 2.x for Jetty |
| **Hazelcast** | 3.x | 5.5.0+ | Major version upgrade |
| **JSP** | 2.0+ | 2.3.3+ | Updated for Jakarta |

### Migration Paths

**Legacy Applications (Java 8):**
- Use only `jamon-core` module
- Stick with existing Tomcat/Jetty versions
- No framework changes required

**Modern Applications (Java 17+):**
- Upgrade to Tomcat 11+ or Jetty 12+
- Use Jakarta EE namespace
- Leverage all JAMon 3.0 modules

### Step 3: Runtime Environment Updates

#### Java 8 Applications (Legacy)
- Use only `jamon-core` module
- No changes required
- All existing functionality preserved

#### Java 17+ Applications (Modern)
- Can use all modules
- Jakarta EE namespace support
- Modern container integration
- Distributed monitoring with Hazelcast 5.x

## Module Selection Guide

### Minimal Setup (Java 8+)
```xml
<dependency>
    <groupId>com.jamonapi</groupId>
    <artifactId>jamon-core</artifactId>
    <version>3.0</version>
</dependency>
```
**Includes:** Basic monitoring, JDBC proxy, JMX beans, listeners

### Web Application (Java 17+)
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
**Adds:** Jakarta Servlet filters, HTTP monitoring

### Distributed Application (Java 17+)
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
**Adds:** Hazelcast 5.5.0 clustering, distributed data persistence

### Tomcat Integration (Java 17+)
```xml
<dependencies>
    <dependency>
        <groupId>com.jamonapi</groupId>
        <artifactId>jamon-core</artifactId>
        <version>3.0</version>
    </dependency>
    <dependency>
        <groupId>com.jamonapi</groupId>
        <artifactId>jamon-tomcat</artifactId>
        <version>3.0</version>
    </dependency>
</dependencies>
```
**Adds:** Tomcat 11 valve integration (includes jamon-http-jakarta)

## Breaking Changes

### None for Core API
The core JAMon monitoring API is 100% backward compatible.

### Package Relocations
Some integration classes moved to new modules:
- `JAMonServletFilter` → `jamon-http-jakarta` module
- `JAMonTomcatValve` → `jamon-tomcat` module  
- Hazelcast classes → `jamon-hazelcast` module

### Dependency Updates
- **Hazelcast:** 3.x → 5.5.0 (API compatible, but requires Java 17+)
- **Servlet API:** javax.servlet → jakarta.servlet (for Java 17+ modules)

## Testing Your Migration

### 1. Build Verification
```bash
mvn clean compile test
```

### 2. Runtime Verification
```java
// Verify core functionality works
Monitor mon = MonitorFactory.start("test");
mon.stop();
System.out.println("JAMon 3.0 migration successful!");
```

### 3. Integration Testing
- **Servlet filters:** Deploy and check HTTP monitoring
- **Tomcat valves:** Verify server.xml configuration
- **Hazelcast:** Test distributed monitoring if used

## Rollback Plan

If issues occur, you can rollback to JAMon 2.85:
```xml
<dependency>
    <groupId>com.jamonapi</groupId>
    <artifactId>jamon</artifactId>
    <version>2.85</version>
</dependency>
```

## Support

- **Issues:** [GitHub Issues](https://github.com/stevensouza/jamonapi/issues)
- **Documentation:** [JAMon Website](http://jamonapi.sourceforge.net)
- **Source:** [GitHub Repository](https://github.com/stevensouza/jamonapi)