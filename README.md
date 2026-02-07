# jamonapi

> **End of Life Notice:** JAMon 3.0 is the **final release** of the Java Application Monitor. No further updates, bug fixes, or new features are planned. Existing users are encouraged to migrate to modern observability solutions such as [Micrometer](https://micrometer.io/), [OpenTelemetry](https://opentelemetry.io/), or [Spring Boot Actuator](https://docs.spring.io/spring-boot/reference/actuator/). If you have questions about JAMon or need help migrating, feel free to [open an issue](https://github.com/stevensouza/jamonapi/issues) -- I'm still happy to help.

[![CI Status](https://github.com/stevensouza/jamonapi/workflows/JAMon%20CI/CD%20Pipeline/badge.svg)](https://github.com/stevensouza/jamonapi/actions)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/stevensouza/jamonapi)](https://github.com/stevensouza/jamonapi/releases/latest)
[![Maven Central](https://img.shields.io/maven-central/v/com.jamonapi/jamon-core)](https://central.sonatype.com/artifact/com.jamonapi/jamon-core)

The Java Application Monitor (JAMon) is a free, simple, high performance, thread safe, Java API that allows developers to easily monitor production applications.

see [jamonapi.sourceforge.net](http://jamonapi.sourceforge.net) for more information.

## 📋 Release Notes

- **[Latest Releases](https://github.com/stevensouza/jamonapi/releases)** - Complete release history
- **[Release Notes](RELEASE_NOTES.md)** - Detailed changes and upgrade guide
- **Current Version**: 3.0 ([What's New](https://github.com/stevensouza/jamonapi/releases/tag/v3_0))

## Requirements

### Build Requirements
- **Java 17 or higher** (required for compilation and testing due to modern dependencies)

## Maven Dependency

**JAMon 3.0 is exclusively hosted on GitHub.** SourceForge support has been retired as of this release.
* **Primary Repository:** https://github.com/stevensouza/jamonapi
* **Issues & Support:** https://github.com/stevensouza/jamonapi/issues

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

## 📚 Documentation

### 📖 Complete Documentation Hub
- **[📚 JAMon Documentation Hub](docs/README.md)** - **START HERE** - Complete guide with navigation and use cases

### JAMon 3.0 Guides
- **[Core API Guide](docs/core-api.md)** - Complete JAMon monitoring API reference
- **[Jakarta Servlet Filter](docs/servlet-filter.md)** - Web application HTTP monitoring
- **[Distributed Monitoring](docs/distributed-monitoring.md)** - Hazelcast cluster integration
- **[Migration Guide](MIGRATION_GUIDE_3.0.md)** - Upgrade from JAMon 2.x to 3.0
- **[Release Notes](RELEASE_NOTES_3.0.md)** - Complete 3.0 feature overview

### Additional Resources
- **[JAMon Website](http://jamonapi.sourceforge.net)** - Legacy documentation and examples
- **[GitHub Issues](https://github.com/stevensouza/jamonapi/issues)** - Support and bug reports
- **JavaDoc:** Generated documentation included in JAR files

## 🏗️ JAMon 3.0 Modular Architecture

JAMon 3.0 introduces a completely modular architecture, allowing you to include only the components you need:

### Core Modules

| Module | Runtime Java | Purpose | Dependencies |
|--------|-------------|---------|--------------|
| `jamon-core` | **Java 8+** | Core monitoring, JDBC proxy, JMX beans | None |
| `jamon-http-jakarta` | **Java 17+** | Jakarta Servlet filters, HTTP monitoring | jamon-core |
| `jamon-hazelcast` | **Java 17+** | Distributed monitoring with Hazelcast 5.5.0 | jamon-core |
| `jamon-tomcat` | **Java 17+** | Tomcat 11+ valve integration | jamon-core, jamon-http-jakarta |

**Build Requirements:** Java 17+ to compile all modules  
**Runtime Requirements:** Java 8+ (core) or Java 17+ (modern integrations)

### Framework Compatibility

| Framework | Supported Versions | JAMon Module | Java Version |
|-----------|-------------------|--------------|--------------|
| **Servlet API** | 3.1.0+ (javax), 6.0.0+ (jakarta) | jamon-http-jakarta | Java 17+ |
| **Tomcat** | 11.0.18+ | jamon-tomcat | Java 17+ |
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
