# JamonAPI Dependency Management

## Project Context
- Open source Maven Java project focused on performance monitoring
- Priority: Maintain backward compatibility and stability
- No functional changes during dependency updates

## Dependency Update Process
1. **Analysis Phase**: Scan pom.xml and identify outdated dependencies
2. **Recommendation Phase**: Research each dependency for:
    - Latest stable versions
    - Breaking changes and compatibility notes
    - Security considerations
    - Community adoption/maturity
3. **Review Phase**: Present findings in organized format for human approval
4. **Implementation Phase**: Only proceed after explicit approval

## Rules
- Never automatically apply changes without human review
- Always check for breaking changes in release notes
- Prioritize security updates but flag any that might affect compatibility
- Group related dependency updates logically
- Test builds after each logical group of changes

## Pre-execution Checklist
- [ ] Run `mvn dependency:display-updates` to identify candidates
- [ ] Research each suggested update thoroughly
- [ ] Present recommendations in clear, organized format
- [ ] Wait for explicit approval before making changes
- [ ] Make changes incrementally with testing between groups