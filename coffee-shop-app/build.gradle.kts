plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.serialization)

    alias(libs.plugins.gms.plugin)
    alias(libs.plugins.crashlytics.plugin)
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
    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.auth)

    // deps for app component
    implementation(project(path = ":core:deps"))
    implementation(project(path = ":core:di"))
    implementation(project(path = ":core:json"))
    implementation(project(path = ":core:network"))
    implementation(project(path = ":core:database"))
    implementation(project(path = ":core:build-config:api"))
    implementation(project(path = ":core:build-config:internal"))
    implementation(project(path = ":core:cache:api"))
    implementation(project(path = ":core:cache:internal"))
    implementation(project(path = ":core:logger:api"))
    implementation(project(path = ":core:logger:internal"))
    implementation(project(path = ":core:design-system"))

    // feature deps
    implementation(project(path = ":feature:auth:api"))
    implementation(project(path = ":feature:auth:internal"))
    implementation(project(path = ":feature:catalog:api"))
    implementation(project(path = ":feature:catalog:internal"))
    implementation(project(path = ":feature:profile:api"))
    implementation(project(path = ":feature:profile:internal"))
    implementation(project(path = ":feature:product-detail:api"))
    implementation(project(path = ":feature:product-detail:internal"))
    implementation(project(path = ":feature:cart:api"))
    implementation(project(path = ":feature:cart:internal"))
    implementation(project(path = ":feature:active-orders:api"))
    implementation(project(path = ":feature:active-orders:internal"))
    implementation(project(path = ":feature:order-history:api"))
    implementation(project(path = ":feature:order-history:internal"))
    implementation(project(path = ":feature:favorites:api"))
    implementation(project(path = ":feature:favorites:internal"))

    // nav deps
    implementation(project(path = ":core:navigation"))
    implementation(libs.bundles.nav3)

    // other deps
    implementation(libs.dagger.core)
    ksp(libs.dagger.compiler)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.process)
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