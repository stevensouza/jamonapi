  # 📋 JAMon Project Handoff Documentation

  ## 🎯 Project Overview
  **JAMon (Java Application Monitor)** - 20-year-old open source performance monitoring library
  - **Current Status:** Production-ready, considering sunset after 2.84
  - **Goal:** Final modernization releases before potential retirement
  - **Philosophy:** Maintain backward compatibility, conservative approach

  ## 📊 Recent Work Completed (JAMon 2.83)

  ### Phase 1: Dependency Modernization (7 commits)
  - **JUnit:** 4.13.1 → 4.13.2
  - **Commons Lang3:** 3.3.2 → 3.18.0
  - **Maven compiler plugin:** Added 3.11.0
  - **Jetty6:** 6.1.25 → 6.1.26
  - **HSQLDB:** 2.3.2 → 2.7.4
  - **Mockito:** 1.9.5 → mockito-core 5.14.2 (major update)
  - **Tomcat Catalina:** 6.0.26 → 6.0.53
  - **Log4j:** 2.17.1 → 2.24.3
  - **AspectJ:** 1.8.0 → 1.9.24

  ### Phase 2: Java Module System Compatibility
  - **Problem:** 66 test failures on modern JVMs
  - **Solution:** Added JVM module arguments to Maven Surefire plugin:
  ```xml
  <argLine>
      --add-modules java.se
      --add-exports java.base/jdk.internal.ref=ALL-UNNAMED
      --add-opens java.base/java.lang=ALL-UNNAMED
      --add-opens java.base/java.nio=ALL-UNNAMED
      --add-opens java.base/sun.nio.ch=ALL-UNNAMED
      --add-opens java.management/sun.management=ALL-UNNAMED
      --add-opens jdk.management/com.sun.management.internal=ALL-UNNAMED
      --add-opens java.base/java.util=ALL-UNNAMED
      --add-opens java.base/java.lang.reflect=ALL-UNNAMED
  </argLine>
  - Result: 341 tests, 100% success rate

  Phase 3: Version Consistency & Security

  - Version fix: MonitorFactoryInterface.java VERSION="2.82" → "2.83"
  - Security update: Spring Framework 4.0.2 → 4.3.30.RELEASE
  - Result: Reduced GitHub Dependabot alerts from 4 to 3
  - Repository cleanup: Added .gitignore entries for runtime data

  Final Status JAMon 2.83:

  - ✅ Git tag: v2_83 created and pushed
  - ✅ All tests passing: 341/341 success
  - ✅ Security improved: Spring vulnerabilities addressed
  - ✅ Modern JVM compatible: Works with Java 8-21
  - ✅ Clean repository: Proper .gitignore, no artifacts

  🚀 JAMon 2.84 Conservative Plan (APPROVED)

  Decision Rationale:

  - Conservative approach chosen over revolutionary
  - Reason: Potential sunset after 2.84, minimize risk
  - Target users: Existing JAMon deployments needing compatibility

  Java Version Strategy:

  - Current: Java 6 compilation target
  - Target: Java 8 compilation (NOT Java 11+)
  - Rationale: Java 8 still dominant in enterprise, enables modern features
  - Build requirement: Java 11+ to compile, Java 8+ to run

  Dependency Update Strategy:

  <!-- APPROVED CONSERVATIVE TARGETS -->
  <maven.compiler.source>8</maven.compiler.source>
  <maven.compiler.target>8</maven.compiler.target>

  <!-- Safe Updates -->
  <spring.version>5.3.39</spring.version>        <!-- NOT 6.x - avoid breaking changes -->
  <hazelcast.version>3.12.13</hazelcast.version>  <!-- NOT 5.x - avoid API rewrite -->
  <assertj.version>3.26.3</assertj.version>       <!-- Safe major update -->
  <mockito.version>5.19.0</mockito.version>       <!-- Minor update -->

  <!-- Keep Compatible Versions -->
  <servlet.version>3.1.0</servlet.version>        <!-- Avoid Jakarta EE namespace -->
  <jetty9.version>10.0.24</jetty9.version>       <!-- Moderate update -->
  <tomcat-catalina.version>9.0.97</tomcat-catalina.version>

  Phase Implementation Plan:

  Phase 1: Foundation Updates (3 commits)

  Commit 1: Java version modernization
  - maven.compiler.source: 1.6 → 8
  - maven.compiler.target: 1.6 → 8
  - Test compilation on Java 8
  - Fix any Java 8 compatibility issues

  Commit 2: Build tool modernization
  - Maven compiler plugin: 3.11.0 → 3.13.0
  - Maven source plugin: 2.2.1 → 3.3.1
  - Maven javadoc plugin: 2.9.1 → 3.10.1

  Commit 3: Version bump preparation
  - Update parent version: 2.83 → 2.84
  - Update all module versions (jamon, jamon_war, jamon_osmon)
  - Update README.md Maven example

  Phase 2: Safe Dependencies (4 commits)

  Commit 4: Testing framework updates
  - Mockito: 5.14.2 → 5.19.0
  - AssertJ: 1.7.0 → 3.26.3
  - Test all existing functionality

  Commit 5: Container APIs (CAREFUL - breaking potential)
  - Servlet API: 2.4 → 3.1.0 (avoid Jakarta EE)
  - JSP API: 2.0 → 2.3.3
  - JavaEE API: 6.0 → 7.0 (NOT 8.0 - Jakarta namespace)

  Commit 6: Application servers
  - Jetty9: 9.2.1 → 10.0.24
  - Tomcat: 6.0.53 → 9.0.97
  - Test JAMon WAR deployment

  Commit 7: Framework updates
  - Spring: 4.3.30 → 5.3.39 (final 5.x, avoid 6.x)
  - Hazelcast: 3.12.6 → 3.12.13 (patch update only)
  - Test distributed JAMon functionality

  Phase 3: Testing & Documentation (2 commits)

  Commit 8: Test coverage expansion
  - Add integration tests for servlet filters
  - Add container deployment tests
  - Add performance regression tests

  Commit 9: Documentation updates
  - Update migration guide for 2.83→2.84
  - Update compatibility matrix
  - Update container deployment examples

  🧪 Testing Requirements

  Current Test Status:

  - 59 test files
  - 341 tests total
  - 100% success rate
  - Excellent coverage of core functionality

  Missing Test Coverage (IDENTIFIED):

  - ❌ Servlet filter integration tests (JAMonServletFilter)
  - ❌ Tomcat valve tests (JAMonTomcatValve)
  - ❌ Jetty handler tests (JAMonJettyHandler)
  - ❌ Distributed JAMon tests (Hazelcast clustering)
  - ❌ Container deployment tests (real server testing)
  - ❌ Performance regression tests (ensure 2.84 ≥ 2.83 performance)

  Test Enhancement Plan:

  // Add these test categories:
  @Testcontainers
  class ContainerIntegrationTest {
      @Container static TomcatContainer tomcat;
      @Container static JettyContainer jetty;
      // Test real deployments
  }

  @ParameterizedTest
  @ValueSource(strings = {"8", "11", "17", "21"})
  void testJavaVersionCompatibility(String version) {
      // Multi-JDK testing
  }

  class PerformanceRegressionTest {
      // Benchmark against 2.83 baseline
  }

  🚨 Critical Considerations

  Breaking Change Risks:

  1. Java 6→8: Code using Java 6 features may break
  2. Spring 4.3→5.3: Minor API changes possible
  3. Servlet 2.4→3.1: Deployment descriptor changes
  4. Container updates: New server versions may behave differently

  Compatibility Testing Required:

  - Java 8, 11, 17, 21 - Multi-JDK compatibility
  - Tomcat 8, 9, 10 - Server compatibility
  - Jetty 9, 10, 11 - Server compatibility
  - Spring 4.x apps - Existing application compatibility
  - JAMon WAR deployment - Admin interface functionality

  Success Criteria:

  - ✅ All 341 tests pass
  - ✅ JAMon WAR deploys successfully
  - ✅ No performance regression
  - ✅ Existing applications work without changes
  - ✅ Modern JVM compatibility maintained

  🛠 Development Environment Setup

  Prerequisites:

  - Java 11+ for compilation
  - Maven 3.6+ for building
  - Git for version control
  - Docker (optional, for container testing)

  Quick Start Commands:

  # Clone and setup
  git clone https://github.com/stevensouza/jamonapi.git
  cd jamonapi/jamon

  # Verify current state
  mvn clean test                    # Should be 341 tests, 0 failures
  git log --oneline -5              # Should see v2_83 recent commits

  # Check available dependency updates
  mvn versions:display-dependency-updates

  # Build JAMon WAR for testing
  cd ../jamon_war
  mvn clean package                 # Creates jamon.war

  Repository Structure:

  jamonapi/
  ├── CLAUDE.md                    # This documentation + format guide
  ├── pom.xml                      # Parent POM with all versions
  ├── jamon/                       # Core JAMon library
  │   ├── src/main/java/          # Source code
  │   ├── src/test/java/          # Test suite (59 files, 341 tests)
  │   └── pom.xml                 # Core module POM
  ├── jamon_war/                  # Web admin interface
  │   ├── src/main/webapp/        # JSP admin pages
  │   └── pom.xml                 # WAR module POM
  └── jamon_osmon/               # OS monitoring utilities
      └── pom.xml                # OS monitoring POM

  📚 Key Files to Understand

  Version Management:

  - /pom.xml - Master version properties
  - jamon/src/main/java/com/jamonapi/MonitorFactoryInterface.java:19 - Hardcoded version constant

  Core Functionality:

  - jamon/src/main/java/com/jamonapi/MonitorFactory.java - Main API entry point
  - jamon/src/main/java/com/jamonapi/http/ - HTTP monitoring components
  - jamon/src/main/java/com/jamonapi/distributed/ - Hazelcast clustering

  Build Configuration:

  - jamon/pom.xml - Contains JVM module arguments for Java 9+ compatibility
  - .gitignore - Updated for JAMon runtime data and temp files

  🎯 Immediate Next Steps

  To Continue JAMon 2.84:

  1. Start Phase 1, Commit 1: Java version modernization
  2. Command: Update maven.compiler.source/target to version 8
  3. Test: Run mvn clean test - expect 341 tests to pass
  4. Validate: Ensure no Java 8 compatibility issues

  Key Success Indicators:

  - Tests pass: All 341 tests successful after each commit
  - WAR builds: jamon.war compiles without errors
  - No regressions: Functionality preserved from 2.83

  If Problems Occur:

  - Java compilation errors: May need to update deprecated API usage
  - Test failures: Check JVM module arguments still compatible
  - Spring integration issues: May need intermediate Spring version
  - Container deployment failures: Update deployment descriptors

  📞 Context for New Sessions

  Communication Format:

  - This document contains complete formatting specification
  - Use [CLAUDE:X] tags for responses
  - Search patterns provided for terminal integration
  - Session numbering continues from where left off

  Project Status:

  - JAMon 2.83: Complete and tagged
  - JAMon 2.84: Planned, conservative approach approved
  - Future: Potential sunset after 2.84 unless continued for learning

  Development Philosophy:

  - Backward compatibility first
  - Conservative updates preferred
  - Test-driven changes only
  - Maintain 20-year legacy carefully

  ---
  This handoff document contains everything needed to continue JAMon development across Claude Code sessions.
