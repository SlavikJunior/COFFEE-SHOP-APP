plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.coffeshop.catalog.internal"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":core:design-system"))
    implementation(project(":core:navigation"))
    implementation(project(":core:common"))
    implementation(project(":core:utils"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:json"))
    implementation(project(":core:di"))
    implementation(project(":core:build-config:api"))
    implementation(project(":core:cache:api"))
    implementation(project(":core:logger:api"))

    implementation(project(":feature:catalog:api"))
    implementation(project(":feature:auth:api"))
    implementation(project(":feature:profile:api"))
    implementation(project(":feature:product-detail:api"))
    implementation(project(":feature:cart:api"))

    // DI
    implementation(libs.dagger.core)
    ksp(libs.dagger.compiler)

    // compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // navigation
    implementation(libs.navigation3.runtime)
    implementation(libs.navigation3.ui)
    implementation(libs.navigation3.router)

    // bundles
    implementation(libs.bundles.lifecycle.viewmodel)

    implementation(libs.coroutines)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}