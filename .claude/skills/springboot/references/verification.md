# Spring Boot Verification Loop

Run all phases in order. Fix failures before proceeding.

## Phase 1: Build

```bash
# Clean build
./mvnw clean compile

# Or Gradle
./gradlew clean compileJava
```

Build failures are hard blockers — nothing else can pass if compilation fails.

## Phase 2: Static Analysis

```bash
# SpotBugs
./mvnw spotbugs:check

# PMD
./mvnw pmd:check

# Checkstyle
./mvnw checkstyle:check

# Combined
./mvnw verify -DskipTests
```

Treat warnings as defects in production code.

## Phase 3: Tests and Coverage

```bash
# Unit + integration tests with coverage
./mvnw verify

# Parallel for speed
./mvnw -T 4 verify

# Specific test class
./mvnw test -Dtest=ProjectServiceTest

# Coverage report (opens at target/site/jacoco/index.html)
./mvnw jacoco:report
```

Require 80%+ line coverage. Fail build below threshold (JaCoCo rule configured in pom.xml).

## Phase 4: Security Scan

```bash
# OWASP Dependency Check
./mvnw verify -Powasp

# Search for hardcoded secrets
grep -r "password\s*=\s*['\"]" --include="*.java" src/
grep -r "secret\s*=\s*['\"]" --include="*.java" src/
grep -r "System\.out\.println" --include="*.java" src/  # Should be zero in prod
```

**Block deployment if:**
- Known CVE in direct dependency
- Hardcoded credentials found
- `System.out.println` in production code (use SLF4J)

## Phase 5: Lint and Format

```bash
# Optional: Spotless formatting
./mvnw spotless:check
./mvnw spotless:apply  # Auto-fix
```

## Phase 6: Diff Review

Before creating PR, manually review:
```bash
git diff main...HEAD --stat    # What files changed
git diff main...HEAD           # What changed
```

Quality checklist:
- [ ] No `System.out.println` (use `log.info/warn/error`)
- [ ] No hardcoded credentials or URLs
- [ ] All new endpoints have `@PreAuthorize`
- [ ] All new service methods have appropriate `@Transactional`
- [ ] Test coverage added for new code
- [ ] DTO validation annotations present

## Full Pipeline

```bash
./mvnw clean verify && \
./mvnw verify -Powasp && \
echo "✅ All checks passed — ready for PR"
```
