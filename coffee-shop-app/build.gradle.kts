plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.coffeeshop.coffeeshopapp"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.coffeeshop.coffeeshopapp"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildToolsVersion = "36.1.0"
}

dependencies {
    // deps for app component
    implementation(project(path = ":core:deps"))
    implementation(project(path = ":core:di"))
    implementation(project(path = ":core:json"))
    implementation(project(path = ":core:network"))
    implementation(project(path = ":core:database"))
    implementation(project(path = ":core:build-config:api"))
    implementation(project(path = ":core:build-config:internal"))

    // feature deps
    implementation(project(path = ":feature:auth:api"))
    implementation(project(path = ":feature:auth:internal"))
    implementation(project(path = ":feature:catalog:api"))
    implementation(project(path = ":feature:catalog:internal"))
    implementation(project(path = ":feature:profile:api"))
    implementation(project(path = ":feature:profile:internal"))
    implementation(project(path = ":feature:product-detail:api"))
    implementation(project(path = ":feature:product-detail:internal"))

    // nav deps
    implementation(project(path = ":core:navigation"))
    implementation(libs.bundles.nav3)

    // other deps
    implementation(libs.dagger.core)
    ksp(libs.dagger.compiler)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}