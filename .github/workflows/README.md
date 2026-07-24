# GitHub Actions Workflows for CieloPass

This directory contains the CI workflows for the CieloPass Android application.

## Structure

```text
.github/workflows/
├── pr-check.yml         # PR title semantic validation
├── format-check.yml     # Ktlint code style validation
├── android-ci.yml       # Unit tests and assembleDebug build verification
├── codeql.yml           # CodeQL SAST security analysis
└── README.md
```

## Workflows

### PR Check

- File: `pr-check.yml`
- Status check name: `PR Check`
- Purpose: Enforce `type(scope): subject` PR titles following Conventional Commits.

### Ktlint Check

- File: `format-check.yml`
- Status check name: `Ktlint Check`
- Purpose: Run `./gradlew ktlintCheck` to ensure code formatting consistency.

### Android CI

- File: `android-ci.yml`
- Status check name: `Android Unit Tests & Build`
- Purpose: Execute unit tests (`./gradlew testDebugUnitTest`) and assemble debug APK (
  `./gradlew assembleDebug`).

### CodeQL Analysis

- File: `codeql.yml`
- Status check name: `CodeQL Analysis (java-kotlin)`, `CodeQL Analysis (actions)`
- Purpose: Static security testing for Java/Kotlin codebase and GitHub Actions workflows.
