# JAMon 3.0 Release Notes

## 🚀 JAMon 3.0: Revolutionary Modular Architecture

**Release Date:** September 2025  
**Major Version:** 3.0 (Breaking architectural change)

JAMon 3.0 represents the most significant architectural evolution in JAMon's 20-year history, introducing a fully modular design that allows developers to include only the components they need.

## 🏗️ Major Changes

### Complete Modular Architecture
JAMon has been transformed from a monolithic library to a modular ecosystem:

**JAMon 2.x:** Single `jamon-2.85.jar` (3.2MB) with all functionality  
**JAMon 3.0:** Four focused modules:
- `jamon-core` - Core monitoring (Java 8+)
- `jamon-http-jakarta` - Jakarta Servlet integration (Java 17+)  
- `jamon-hazelcast` - Distributed monitoring (Java 17+)
- `jamon-tomcat` - Tomcat valve integration (Java 17+)

### Java Version Strategy
- **Backward Compatible:** Core functionality still supports Java 8+
- **Modern Features:** Java 17+ modules for contemporary frameworks
- **Build Requirement:** Java 17+ to compile, Java 8+ to run core

## 📦 Module Details

### jamon-core (Java 8+)
**Core JAMon functionality with zero external dependencies:**
- Performance monitoring API
- JDBC proxy monitoring  
- JMX beans integration
- Listeners and buffer management
- AOP support (Spring, EJB)
- Log4j appender

**Size:** ~800KB  
**Dependencies:** None

### jamon-http-jakarta (Java 17+)
**Modern servlet integration:**
- Jakarta Servlet API support (javax → jakarta namespace)
- HTTP request/response monitoring
- JAMonServletFilter for automatic web monitoring

**Size:** ~50KB  
**Dependencies:** jamon-core, Jakarta Servlet API 6.0.0

### jamon-hazelcast (Java 17+)
**Distributed monitoring capabilities:**
- Hazelcast 5.5.0 integration (upgraded from 3.x)
- Cluster-wide monitoring data aggregation
- Distributed persistence and synchronization

**Size:** ~100KB  
**Dependencies:** jamon-core, Hazelcast 5.5.0

### jamon-tomcat (Java 17+)
**Tomcat server integration:**
- Tomcat 11 valve implementation
- Server-level HTTP monitoring
- Comprehensive unit test coverage

**Size:** ~25KB  
**Dependencies:** jamon-core, jamon-http-jakarta, Tomcat Catalina 11.0.18

## 🔄 Migration Guide

### Zero Code Changes Required
The core JAMon API remains 100% backward compatible:
```java
// This code works identically in 2.x and 3.0
Monitor mon = MonitorFactory.start("myLabel");
// ... business logic ...
mon.stop();
```

### Dependency Updates
**Simple Migration (Java 8+):**
```xml
<!-- Replace this -->
<dependency>
    <groupId>com.jamonapi</groupId>
    <artifactId>jamon</artifactId>
    <version>2.85</version>
</dependency>

<!-- With this -->
<dependency>
    <groupId>com.jamonapi</groupId>
    <artifactId>jamon-core</artifactId>
    <version>3.0</version>
</dependency>
```

**Full Feature Migration (Java 17+):**
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
    <!-- Add other modules as needed -->
</dependencies>
```

## 🎯 Benefits

### Reduced Footprint
- **Java 8 apps:** Include only 800KB core instead of 3.2MB monolith
- **Modern apps:** Choose exactly the integrations you need
- **Container deployment:** Server-level JAR installation for shared usage

### Enhanced Compatibility
- **Legacy systems:** Continue using Java 8+ with core functionality
- **Modern systems:** Leverage Java 17+ features with contemporary frameworks
- **Gradual migration:** Adopt modules incrementally

### Improved Maintenance
- **Focused modules:** Each module has specific responsibilities
- **Independent updates:** Update only the modules you use
- **Clear dependencies:** Explicit module relationships

## 📊 Technical Improvements

### Build System
- Maven multi-module architecture
- Consistent versioning across modules
- Profile-based Java version management
- CI/CD pipeline for all modules

### Testing
- **351 tests** maintained across modules
- Comprehensive Tomcat valve test coverage
- Module-specific test isolation
- Cross-module integration testing

### Dependencies
- **Hazelcast:** Upgraded 3.x → 5.5.0
- **Jakarta EE:** Modern namespace support
- **Tomcat:** Updated to 11.0.18
- **Security:** All CVEs addressed

## 🚨 Breaking Changes

### Package Relocations
Integration classes moved to appropriate modules:
- `JAMonServletFilter` → `jamon-http-jakarta` module
- `JAMonTomcatValve` → `jamon-tomcat` module
- Hazelcast classes → `jamon-hazelcast` module

### Java Requirements
- **Hazelcast integration:** Now requires Java 17+ (was Java 8+)
- **Jakarta Servlet:** Requires Java 17+ (namespace change)
- **Tomcat valve:** Requires Java 17+ (modern Tomcat)

### WAR Deployment
- **JAMon WAR 3.0:** No longer bundles JAR dependencies
- **Server setup:** Install modular JARs in server common/lib
- **Benefit:** Multiple WARs can share single JAMon installation
- **Requires Tomcat 11+** (or equivalent Jakarta EE 10 container)

### JSP Changes (jamon-war)
- **web.xml:** Updated from Servlet 2.3 DTD to Jakarta EE 6.0 schema
- **isThreadSafe attribute:** Removed from all JSPs (removed from Jakarta Pages 4.0 spec)
- **Utils.getParameters():** Replaced with inline Jakarta-compatible code (fdsapi-1.2.jar uses javax.servlet which is incompatible with Tomcat 11)
- **Impact:** The jamon-war will **not** deploy on Tomcat 9 or earlier. Use JAMon 2.x WAR for older containers.

## 🎉 What's New

### Enhanced Tomcat Integration
- Full Tomcat 11 compatibility
- Comprehensive unit test suite
- Modern Jakarta EE namespace support

### Modernized Hazelcast Support
- Hazelcast 5.5.0 with improved performance
- Enhanced clustering capabilities
- Better resource management

### Developer Experience
- Clear module boundaries
- Improved documentation
- Migration guide provided
- Backward compatibility preserved

## 📚 Documentation

- **[Migration Guide](MIGRATION_GUIDE_3.0.md)** - Step-by-step upgrade instructions
- **[README.md](README.md)** - Updated with modular architecture examples
- **[GitHub Repository](https://github.com/stevensouza/jamonapi)** - Source code and issues

## 🔮 Future Considerations

JAMon 3.0 may be the final major release as the project approaches its 20-year milestone. This release ensures JAMon remains viable for both legacy and modern Java applications while providing a clean, modular foundation.

## 🙏 Acknowledgments

JAMon 3.0 represents two decades of performance monitoring evolution. Thanks to the community for continued support and feedback that made this modular transformation possible.

---

**Download:** [GitHub Releases](https://github.com/stevensouza/jamonapi/releases/tag/v3_0)  
**Maven Central:** `com.jamonapi:jamon-core:3.0`