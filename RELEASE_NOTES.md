# JAMon Release Notes

## [3.0] - 2025-09

See **[JAMon 3.0 Release Notes](RELEASE_NOTES_3.0.md)** for the complete modular architecture release notes.

---

## [2.85] - 2025-08-29

### 🎯 Final Maintenance Release
- **End-of-Life Release**: Final JAMon maintenance update focusing on compatibility and cleanup
- **Java 8 Compatible**: Maintained Java 8+ compatibility for existing deployments
- **Ultra-Minimal Approach**: Conservative dependency updates prioritizing stability

### 🔧 Code Quality Improvements  
- **Deprecation Fixes**: Resolved 39+ Java deprecation warnings across 18 files
  - Fixed wrapper constructors: `new Double()` → `Double.valueOf()`
  - Updated collection operations for type safety
  - Enhanced array initialization patterns
- **Version Consistency**: Updated all references from 2.82/2.84 → 2.85
- **Legacy File Management**: Marked obsolete Ant builds as deprecated (last working: v2.75)

### 🧪 Container Integration Testing
- **New Test Coverage**: Added `ContainerIntegrationTest` with 5 focused integration tests
- **Mock-Based Validation**: Tests servlet filters, Tomcat valves, Jetty handlers without container deployment
- **JDBC Workflow Testing**: Validates Generate Data button functionality with in-memory HSQLDB
- **Exception Handling**: Verifies monitoring continues during error conditions

### 📦 Infrastructure Updates
- **Hazelcast 5.5.0**: Major distributed caching upgrade (completed in earlier commits)
- **GitHub Actions CI/CD**: Automated release pipeline implementation
- **Tomcat Security**: Updated to 9.0.107 addressing 9 CVEs
- **Legacy Script Updates**: Updated deployment scripts for version 2.85

### 🔒 Security Status
- **Dependency Health**: All major security vulnerabilities addressed
- **CVE Resolutions**: Tomcat and Spring framework security patches applied
- **Production Ready**: Safe for production deployments requiring Java 17+ compatibility

### 📋 Technical Validation
- **344/344 Tests Passing**: All tests successful including new container integration tests
- **Multi-JDK Compatible**: Verified on Java 17, 21
- **Build System**: Maven-only builds (Ant deprecated)
- **Container Testing**: Servlet filters, valves, and handlers validated

### 🎯 Migration Notes
- **Build Change**: Java 17+ required for compilation/testing (due to modern dependencies)
- **Runtime**: Core JAMon still supports Java 8+, advanced features require higher versions
- **From 2.84**: No breaking changes for basic JAMon usage
- **Hazelcast Users**: Must upgrade to Java 17+ for distributed features
- **Jetty Users**: Must upgrade to Java 11+ for Jetty 10.x integration

---

## [2.84] - 2025-01-26

### 🎯 Major Improvements
- **Java 8 Modernization**: Upgraded compilation target from Java 6 → Java 8
- **Spring Framework**: Upgraded 4.3.30 → 5.3.39 (security fixes + modern compatibility)  
- **Apache Tomcat**: Upgraded 7.0.109 → 9.0.98 (critical security fixes)
- **Build Tools**: Maven compiler 3.11.0 → 3.13.0, source plugin 2.2.1 → 3.3.1, javadoc plugin 2.9.1 → 3.10.1

### 🔒 Security Fixes  
- **Critical**: Fixed Apache Tomcat authentication bypass vulnerability
- **High/Medium**: Resolved Spring Framework security issues (CVE fixes)
- Reduced GitHub Dependabot alerts from 4 → 1

### 🧪 Testing & Quality
- **Spring 5.x Compatibility**: Fixed all AOP test failures caused by Spring framework changes
- **341/341 Tests Passing**: Complete test suite success (100% pass rate)
- **Modern Test Approaches**: Improved monitor existence checking with `MonitorFactory.exists()`
- **Better Assertions**: Direct monitor verification instead of complex counting logic

### 📦 Dependency Updates
- **Testing**: Mockito 5.14.2 → 5.19.0, AssertJ 1.7.0 → 3.26.3
- **Container APIs**: Servlet 2.4 → 3.1.0, JSP 2.0 → 2.3.3, JavaEE 6.0 → 7.0  
- **Application Servers**: Jetty9 9.2.1 → 10.0.24
- **Frameworks**: Hazelcast 3.12.6 → 3.12.13 (patch update)

### ⚠️ Breaking Changes
- **Java 6 Support Dropped**: Now requires Java 8+ to compile, Java 8+ to run
- **Spring AOP Behavior**: Monitor creation patterns changed (tests updated accordingly)
- **Tomcat Valve API**: Removed deprecated `getInfo()` method overrides

### 🔧 Technical Improvements  
- **JVM Compatibility**: Added module system arguments for Java 9+ compatibility
- **Backward Compatibility**: Maintained existing JAMon APIs and functionality
- **Repository Cleanup**: Improved .gitignore for JAMon runtime data

### 📋 Validation
- **Multi-JDK Tested**: Works on Java 8, 11, 17, 21
- **Container Compatibility**: Tested with modern Tomcat/Jetty versions
- **Performance**: No regression vs 2.83 baseline

---

## [2.83] - 2024-12-15

### 🎯 Foundation Work  
- **Dependency Updates**: JUnit 4.13.1 → 4.13.2, Commons Lang3 3.3.2 → 3.18.0
- **Build Modernization**: Added Maven compiler plugin 3.11.0  
- **Container Updates**: Jetty6 6.1.25 → 6.1.26, HSQLDB 2.3.2 → 2.7.4
- **Testing Framework**: Mockito 1.9.5 → mockito-core 5.14.2 (major update)
- **Application Servers**: Tomcat Catalina 6.0.26 → 6.0.53
- **Logging**: Log4j 2.17.1 → 2.24.3
- **AOP**: AspectJ 1.8.0 → 1.9.24

### 🔧 Java Module System  
- **Problem Solved**: Fixed 66 test failures on modern JVMs
- **Solution**: Added JVM module arguments to Maven Surefire plugin
- **Result**: 341 tests, 100% success rate on Java 8-21

### 🔒 Security & Cleanup
- **Version Consistency**: Fixed MonitorFactoryInterface.java VERSION="2.82" → "2.83"  
- **Repository Cleanup**: Added proper .gitignore entries for runtime data
- **Git Management**: Created and pushed v2_83 tag

### 📊 Results
- ✅ All tests passing: 341/341 success
- ✅ Modern JVM compatible: Works with Java 8-21  
- ✅ Clean repository: No build artifacts in git

---

## Installation

### Maven Dependency (JAMon 3.0)
```xml
<dependency>
    <groupId>com.jamonapi</groupId>
    <artifactId>jamon-core</artifactId>
    <version>3.0</version>
</dependency>
```

### Maven Dependency (Legacy JAMon 2.x)
```xml
<dependency>
    <groupId>com.jamonapi</groupId>
    <artifactId>jamon</artifactId>
    <version>2.85</version>
</dependency>
```

### Download
- [GitHub Releases](https://github.com/stevensouza/jamonapi/releases)

### Requirements
- **JAMon 3.0**: Java 17+ (build), Java 8+ (core runtime), Java 17+ (modern modules)
- **JAMon 2.85**: Java 17+ (build), Java 8+ (core runtime)
- **JAMon 2.84**: Java 8+ (build and runtime)

---

*For complete documentation, see [JAMon Documentation Hub](docs/README.md)*