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


// samples
include(":sample:auth")


// core modules
include(":core:utils")
include(":core:common")
include(":core:network")
include(":core:database")
include(":core:json")
include(":core:design-system")
include(":core:di")
include(":core:deps")
include(":core:navigation")
include(":core:build-config:api")
include(":core:build-config:internal")
include(":core:cache:api")
include(":core:cache:internal")
include(":core:logger:api")
include(":core:logger:internal")


// features
include(":feature:auth:api")
include(":feature:auth:internal")

include(":feature:catalog:api")
include(":feature:catalog:internal")

include(":feature:profile:api")
include(":feature:profile:internal")

include(":feature:product-detail:api")
include(":feature:product-detail:internal")

include(":feature:cart:api")
include(":feature:cart:internal")

include(":feature:active-orders:api")
include(":feature:active-orders:internal")

include(":feature:order-history:api")
include(":feature:order-history:internal")