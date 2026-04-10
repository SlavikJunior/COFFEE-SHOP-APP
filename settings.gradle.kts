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
include(":coffee-shop-app")
include(":core:network")
include(":core:design-system")
include(":feature:auth:api")
include(":feature:auth:internal")
include(":core:common")
include(":core:navigation")
include(":core:utils")
include(":core:deps")
include(":core:build-config:api")
include(":core:build-config:internal")
include(":feature:products:api")
include(":feature:products:internal")
include(":core:di")
