# JAMon Documentation Hub

![JAMon Logo](images/jamon1.jpg)

**Welcome to the Java Application Monitor (JAMon) Documentation Hub** - Your complete guide to monitoring Java applications with JAMon 3.0.

## 🚀 Quick Start

**New to JAMon?** Start here:
- [📖 Main Project README](../README.md) - JAMon overview, Maven dependencies, and requirements
- [⚡ Core API Guide](core-api.md) - Essential JAMon monitoring concepts and basic usage
- [📋 Release Notes](../RELEASE_NOTES_3.0.md) - What's new in JAMon 3.0

## 📚 Complete Documentation

### Core Concepts
| Guide | Description | Audience |
|-------|-------------|----------|
| [**⚡ Core API**](core-api.md) | Basic monitoring, factory methods, best practices | All developers |
| [**👂 JAMon Listeners**](listeners.md) | Event-driven monitoring, buffer listeners, custom listeners | Intermediate+ |
| [**🔌 Interface Monitoring**](interface-monitoring.md) | Dynamic proxy monitoring for any Java interface | Legacy systems |

### Web Application Monitoring
| Guide | Description | Use Case |
|-------|-------------|----------|
| [**🌐 Jakarta Servlet Filter**](servlet-filter.md) | Modern web app monitoring (JAMon 3.0) | Jakarta EE applications |
| [**🔗 HTTP Monitoring**](http-monitoring.md) | Legacy container monitoring (JAMon 2.x) | Tomcat Valve, Jetty Handler |
| [**🖥️ JAMon Web App**](jamon-war.md) | Admin interface, statistics viewing, ranges | All web deployments |

### Database & Performance
| Guide | Description | Use Case |
|-------|-------------|----------|
| [**📊 SQL Monitoring**](sql-monitoring.md) | JDBC proxy, SQL performance tracking | Database applications |
| [**📈 JMX Monitoring**](jmx-monitoring.md) | JMX beans, management consoles | Enterprise monitoring |

### Framework Integration
| Guide | Description | Framework |
|-------|-------------|-----------|
| [**🎯 Spring AOP**](spring-aop-monitoring.md) | Aspect-oriented monitoring | Spring Framework |
| [**📝 Log4j Appender**](log4j-appender.md) | Logging integration and analysis | Log4j 2.x |

### Advanced Features
| Guide | Description | Use Case |
|-------|-------------|----------|
| [**🌍 Distributed Monitoring**](distributed-monitoring.md) | Hazelcast cluster monitoring | Multi-node applications |

## 🎯 Use Case Quick Reference

### "I want to monitor..."

**Web application performance** → [Jakarta Servlet Filter](servlet-filter.md) or [HTTP Monitoring](http-monitoring.md)

**Database queries** → [SQL Monitoring](sql-monitoring.md)

**Spring beans/methods** → [Spring AOP Monitoring](spring-aop-monitoring.md)

**Application logs** → [Log4j Appender](log4j-appender.md)

**Distributed systems** → [Distributed Monitoring](distributed-monitoring.md)

**Custom metrics via JMX** → [JMX Monitoring](jmx-monitoring.md)

**Any Java interface** → [Interface Monitoring](interface-monitoring.md)

**Detailed event tracking** → [JAMon Listeners](listeners.md)

## 📊 JAMon 3.0 Module Overview

JAMon 3.0 introduces a modular architecture - include only what you need:

| Module | Runtime Java | Purpose | Documentation |
|--------|--------------|---------|---------------|
| **jamon-core** | Java 8+ | Core monitoring, JDBC proxy, JMX | [Core API](core-api.md), [SQL Monitoring](sql-monitoring.md) |
| **jamon-http-jakarta** | Java 17+ | Jakarta Servlet filters, HTTP monitoring | [Servlet Filter](servlet-filter.md) |
| **jamon-hazelcast** | Java 17+ | Distributed monitoring | [Distributed Monitoring](distributed-monitoring.md) |
| **jamon-tomcat** | Java 17+ | Tomcat 11+ valve integration | [HTTP Monitoring](http-monitoring.md) |

## 🔄 Migration Information

**Upgrading from JAMon 2.x?**
- [📋 Migration Guide](../MIGRATION_GUIDE_3.0.md) - Complete upgrade instructions
- [📄 Release Notes](../RELEASE_NOTES_3.0.md) - Detailed changes and new features

**Need legacy JAMon 2.x documentation?**
- [📁 Legacy Documentation](../src/LegacyJAMonUsersGuide285/) - Complete JAMon 2.x HTML documentation

## 🛠️ Development & Support

**Building and Testing:**
- [🏗️ Main README](../README.md#building-and-testing) - Build requirements and Maven commands
- [📊 CI/CD Pipeline](https://github.com/stevensouza/jamonapi/actions) - Automated testing status

**Getting Help:**
- [🐛 GitHub Issues](https://github.com/stevensouza/jamonapi/issues) - Bug reports and feature requests  
- [🔗 JAMon Website](http://jamonapi.sourceforge.net) - Additional examples and resources
- [📖 JavaDoc](../README.md#documentation) - Generated API documentation

## 📋 Documentation Standards

All JAMon documentation follows these conventions:
- **Quick Navigation** - Every page has cross-references to related documentation
- **Code Examples** - Practical, copy-paste ready examples
- **Version Compatibility** - Clear guidance on JAMon 2.x vs 3.0 differences
- **Use Case Focus** - Problem-solving oriented approach

---

**📍 You are here:** JAMon Documentation Hub  
**🏠 Project Home:** [JAMon GitHub Repository](https://github.com/stevensouza/jamonapi)  
**⭐ Quick Start:** [Core API Guide](core-api.md)