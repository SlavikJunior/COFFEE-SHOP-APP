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
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "COFFEE SHOP APP"

// app
include(":coffee-shop-app")


// sample apps
include(":sample-catalog")


// core modules
include(":core:utils")
include(":core:common")
include(":core:network")
include(":core:design-system")
include(":core:di")
include(":core:deps")
include(":core:navigation")
include(":core:build-config:api")
include(":core:build-config:internal")


// features
include(":feature:auth:api")
include(":feature:auth:internal")

include(":feature:catalog:api")
include(":feature:catalog:internal")

include(":feature:profile:api")
include(":feature:profile:internal")