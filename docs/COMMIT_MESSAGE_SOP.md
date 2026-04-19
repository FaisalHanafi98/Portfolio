# Commit Message SOP

All future commit messages for this repository must use the format below.

## Required Template

```text
Project: <Project Name>
Date: <DD Month YYYY>
Task:

<One-line task summary>

<Short explanation paragraph(s)>

Verified that:
- <verification item>
- <verification item>

<Closing outcome statement>

[Assisted by AI, reviewed manually by Faisal]
```

## Rules

- `Project`, `Date`, and `Task` must always be present.
- Leave a blank line after `Task:` before the task summary.
- Use `Verified that:` for concrete checks that were actually run.
- End every message with `[Assisted by AI, reviewed manually by Faisal]`.
- Do not use conventional commit prefixes for this repository unless this SOP is updated.

## Example

```text
Project: Portfolio
Date: 20 April 2026
Task:

Fix backend profile configuration mismatch

Updated application.yml to use spring.profiles.default=dev instead of hardcoding an active profile.
Renamed application-production.yml to application-prod.yml so it correctly maps to Spring's prod profile.

The previous setup risked loading the wrong datasource configuration outside dev, which could silently break runtime or deployment.

Verified that:
- ./gradlew bootRun starts without specifying a profile
- Application falls back to dev profile (H2)
- /actuator/health returns UP
- /api/v1/projects returns seeded data
- L1 and L2 healthchecks pass through pre-commit gate

This ensures local runtime is stable and production profile is correctly resolved via environment variables.

[Assisted by AI, reviewed manually by Faisal]
```
