# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ZZLogViewer is an Android application built with Kotlin for viewing and analyzing log files directly on Android devices. This is currently a greenfield project with the basic directory structure in place.

## Project Structure

- **Package**: `com.zzlogviewer`
- **Language**: Kotlin
- **Platform**: Android (minimum and target SDK versions TBD)
- **Source**: `app/src/main/java/com/zzlogviewer/` - Main application code
- **Resources**: `app/src/main/res/` - Android resources (layouts, drawables, values)
- **Assets**: `app/src/main/assets/` - Static assets
- **Tests**:
  - `app/src/test/java/com/zzlogviewer/` - Unit tests
  - `app/src/androidTest/java/com/zzlogviewer/` - Instrumented tests

## Development Commands

Since this is a new project, the standard Android/Gradle commands will apply once the build files are set up:

**Building:**
```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
```

**Testing:**
```bash
./gradlew test                   # Run unit tests
./gradlew connectedAndroidTest   # Run instrumented tests
./gradlew testDebugUnitTest      # Run debug unit tests only
```

**Code Quality:**
```bash
./gradlew lint                   # Run Android lint checks
./gradlew ktlintCheck           # Run Kotlin linting (if configured)
```

**Cleaning:**
```bash
./gradlew clean                  # Clean build artifacts
```

**Installation:**
```bash
./gradlew installDebug           # Install debug build on connected device
```

## Key Requirements

Based on the project goals, the application should:

1. **Log File Viewing**: Display log files with proper formatting and readability
2. **Search & Filter**: Provide search functionality and filtering capabilities for log entries
3. **User-Friendly Interface**: Simple, intuitive UI for navigating and analyzing logs

## Development Notes

- This is a new Android project - build configuration files (build.gradle.kts) and manifest files still need to be created
- The project follows standard Android app structure with separate directories for main code, tests, and resources
- When adding dependencies, use the modern Gradle Kotlin DSL format
