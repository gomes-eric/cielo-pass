// Plugin Management
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

// Plugins
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// Dependency Resolution Management
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

// Project Structure
rootProject.name = "CieloPass"

include(":app")
