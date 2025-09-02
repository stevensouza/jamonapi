# jamonapi

[![CI Status](https://github.com/stevensouza/jamonapi/workflows/JAMon%20CI/CD%20Pipeline/badge.svg)](https://github.com/stevensouza/jamonapi/actions)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/stevensouza/jamonapi)](https://github.com/stevensouza/jamonapi/releases/latest)
[![Maven Central](https://img.shields.io/maven-central/v/com.jamonapi/jamon)](https://central.sonatype.com/artifact/com.jamonapi/jamon)

The Java Application Monitor (JAMon) is a free, simple, high performance, thread safe, Java API that allows developers to easily monitor production applications.

see [jamonapi.sourceforge.net](http://jamonapi.sourceforge.net) for more information.

## 📋 Release Notes

- **[Latest Releases](https://github.com/stevensouza/jamonapi/releases)** - Complete release history
- **[Release Notes](RELEASE_NOTES.md)** - Detailed changes and upgrade guide
- **Current Version**: 3.0 ([What's New](https://github.com/stevensouza/jamonapi/releases/tag/v3_0))

## Requirements

### Build & Test Requirements
- **Java 17 or higher** (required for compilation and testing due to modern dependencies)

### Runtime Requirements
- **Java 8+** - Core JAMon functionality (monitoring, JDBC proxy, basic servlet filters)
- **Java 11+** - Jetty 10.x integration features  
- **Java 17+** - Hazelcast 5.5.0 distributed monitoring features

**Note:** JAMon 3.0 uses a modular architecture with different Java version requirements per module. Core functionality is Java 8+, while modern integrations require Java 17+.

## Maven Dependency

The JAMon repositories on both sourceforge and github are the latest version of JAMon.
* https://github.com/stevensouza/jamonapi
* https://sourceforge.net/p/jamonapi/jamonapi/ci/master/tree/

```xml
<!-- JAMon 3.0 Modular Dependencies -->
<dependencies>
    <!-- Core JAMon functionality (Java 8+) -->
    <dependency>
        <groupId>com.jamonapi</groupId>
        <artifactId>jamon-core</artifactId>
        <version>3.0</version>
    </dependency>
    
    <!-- Jakarta Servlet integration (Java 17+) -->
    <dependency>
        <groupId>com.jamonapi</groupId>
        <artifactId>jamon-http-jakarta</artifactId>
        <version>3.0</version>
    </dependency>
    
    <!-- Hazelcast distributed monitoring (Java 17+) -->
    <dependency>
        <groupId>com.jamonapi</groupId>
        <artifactId>jamon-hazelcast</artifactId>
        <version>3.0</version>
    </dependency>
    
    <!-- Tomcat valve integration (Java 17+) -->
    <dependency>
        <groupId>com.jamonapi</groupId>
        <artifactId>jamon-tomcat</artifactId>
        <version>3.0</version>
    </dependency>
</dependencies>
```

## 🏗️ JAMon 3.0 Modular Architecture

JAMon 3.0 introduces a completely modular architecture, allowing you to include only the components you need:

### Core Modules

| Module | Java Version | Purpose | Dependencies |
|--------|-------------|---------|--------------|
| `jamon-core` | Java 8+ | Core monitoring, JDBC proxy, JMX | None |
| `jamon-http-jakarta` | Java 17+ | Jakarta Servlet filters | jamon-core |
| `jamon-hazelcast` | Java 17+ | Distributed monitoring | jamon-core, Hazelcast 5.5.0 |
| `jamon-tomcat` | Java 17+ | Tomcat valve integration | jamon-core, jamon-http-jakarta |

### Framework Compatibility

| Framework | Supported Versions | JAMon Module | Java Version |
|-----------|-------------------|--------------|--------------|
| **Servlet API** | 3.1.0+ (javax), 6.0.0+ (jakarta) | jamon-http-jakarta | Java 17+ |
| **Tomcat** | 11.0.2+ | jamon-tomcat | Java 17+ |
| **Jetty** | Not supported | N/A | Legacy support in JAMon 2.x only |
| **Hazelcast** | 5.5.0+ | jamon-hazelcast | Java 17+ |
| **JSP** | 2.3.3+ | jamon-http-jakarta | Java 17+ |

### Module Selection Guide

**Basic monitoring (Java 8+):**
```xml
<dependency>
    <groupId>com.jamonapi</groupId>
    <artifactId>jamon-core</artifactId>
    <version>3.0</version>
</dependency>
```

**Modern web applications (Java 17+):**
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

**Distributed applications (Java 17+):**
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
