# JAMon Release Notes

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

### Maven Dependency
```xml
<dependency>
    <groupId>com.jamonapi</groupId>
    <artifactId>jamon</artifactId>
    <version>2.84</version>
</dependency>
```

### Download
- [GitHub Releases](https://github.com/stevensouza/jamonapi/releases)
- [Maven Central](https://central.sonatype.com/artifact/com.jamonapi/jamon)

### Requirements  
- **JAMon 2.84**: Java 8+ (compiles with Java 8+, runs on Java 8+)
- **JAMon 2.83**: Java 6+ (legacy compatibility)

---

*For complete documentation, see [JAMon User Guide](src/JAMonUsersGuide/index.html)*