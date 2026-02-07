# JAMon Project Handoff Documentation

## Project Overview
**JAMon (Java Application Monitor)** - 20+ year open source performance monitoring library
- **Current Version:** 3.0 (modular architecture)
- **Current Branch:** `release/3.0-persister-separation`
- **Philosophy:** Maintain backward compatibility, modular design

## JAMon 3.0 Architecture

JAMon 3.0 transformed from a monolithic library to a modular ecosystem:

### Active Modules (in parent POM)
| Module | Runtime Java | Purpose |
|--------|-------------|---------|
| `jamon-core` | Java 8+ | Core monitoring, JDBC proxy, JMX, listeners, Log4j appender |
| `jamon-http-jakarta` | Java 17+ | Jakarta Servlet filters, HTTP monitoring |
| `jamon-hazelcast` | Java 17+ | Distributed monitoring with Hazelcast 5.5.0 |
| `jamon-tomcat` | Java 17+ | Tomcat 11+ valve integration |

### Separate Build Modules (not in parent POM)
| Module | Status |
|--------|--------|
| `jamon-war` | Web admin interface, built separately |
| `jamon-jetty` | Commented out in parent POM, planned for future |
| `jamon-osmon` | Legacy, not part of 3.0 build |

### Key Dependency Versions (from parent pom.xml)
```xml
<maven.compiler.source>8</maven.compiler.source>  <!-- base, overridden to 17 by profile -->
<tomcat.version>11.0.18</tomcat.version>
<hazelcast.version>5.5.0</hazelcast.version>
<servlet.jakarta.version>6.0.0</servlet.jakarta.version>
<jetty.version>12.0.15</jetty.version>
<log4j.version>2.25.3</log4j.version>
<mockito.version>5.21.0</mockito.version>
```

## Testing

### Current Test Status
- **351 tests total**, 100% success rate
- jamon-core: 332 tests
- jamon-http-jakarta: 6 tests
- jamon-hazelcast: 3 tests
- jamon-tomcat: 10 tests

### Quick Start Commands
```bash
# Clone and setup
git clone https://github.com/stevensouza/jamonapi.git
cd jamonapi

# Run all tests (requires Java 17+)
mvn clean test

# Build all modules
mvn clean package -Dmaven.test.skip=true

# Build WAR separately
cd jamon-war && mvn clean package
```

## Repository Structure

```
jamonapi/
├── CLAUDE.md                     # This documentation
├── pom.xml                       # Parent POM with all versions
├── jamon-core/                   # Core JAMon library (Java 8+)
│   ├── src/main/java/           # Source code
│   ├── src/test/java/           # Test suite (332 tests)
│   └── pom.xml
├── jamon-http-jakarta/           # Jakarta Servlet integration (Java 17+)
│   └── pom.xml
├── jamon-hazelcast/              # Distributed monitoring (Java 17+)
│   └── pom.xml
├── jamon-tomcat/                 # Tomcat valve integration (Java 17+)
│   └── pom.xml
├── jamon-war/                    # Web admin interface (separate build)
│   ├── src/main/webapp/         # JSP admin pages
│   └── pom.xml
├── jamon-jetty/                  # Jetty support (not yet active)
├── jamon-osmon/                  # Legacy OS monitoring (not in 3.0)
├── docs/                         # Markdown documentation
├── RELEASE_NOTES_3.0.md
├── MIGRATION_GUIDE_3.0.md
└── .github/workflows/            # CI/CD pipelines
```

## Key Files

### Version Management
- `/pom.xml` - Master version properties
- `jamon-core/src/main/java/com/jamonapi/MonitorFactoryInterface.java:19` - VERSION="3.0"

### Core Functionality
- `jamon-core/src/main/java/com/jamonapi/MonitorFactory.java` - Main API entry point
- `jamon-http-jakarta/src/main/java/com/jamonapi/http/JAMonServletFilter.java` - Servlet filter
- `jamon-hazelcast/src/main/java/com/jamonapi/distributed/HazelcastPersister.java` - Distributed persistence
- `jamon-tomcat/src/main/java/com/jamonapi/http/JAMonTomcatValve.java` - Tomcat valve

### Build Configuration
- `pom.xml` - Parent POM with JVM module arguments for Java 9+ test compatibility
- `.github/workflows/ci.yml` - CI pipeline (tests on JDK 8, 11, 17, 21)
- `.github/workflows/release.yml` - Release pipeline

## Development Environment

### Prerequisites
- **Java 17+** for compilation and testing
- **Maven 3.6+** for building
- **Git** for version control

### Build Notes
- Java 8/11: Only `jamon-core` tests run (CI uses `-pl jamon-core`)
- Java 17+: All module tests run
- JVM module arguments in parent POM surefire config for Java 9+ compatibility

## Release History
- **JAMon 3.0:** Modular architecture, Jakarta EE, Hazelcast 5.5.0, Tomcat 11
- **JAMon 2.85:** Final monolithic release, Java 17+ build, 344 tests
- **JAMon 2.84:** Java 8 target, Spring 5.3.39, Tomcat 9
- **JAMon 2.83:** Dependency modernization, Java module system fixes

---
This handoff document contains everything needed to continue JAMon development across Claude Code sessions.
