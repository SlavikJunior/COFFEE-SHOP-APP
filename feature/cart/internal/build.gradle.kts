plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.coffeeshop.cart.internal"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    implementation(libs.androidx.compose.foundation)
    implementation(project(path = ":feature:cart:api"))
    implementation(project(path = ":feature:catalog:api"))

    implementation(project(path = ":core:logger:api"))
    implementation(project(path = ":core:di"))
    implementation(project(path = ":core:navigation"))
    implementation(project(path = ":core:network"))
    implementation(project(path = ":core:database"))
    implementation(project(path = ":core:json"))
    implementation(project(path = ":core:design-system"))
    implementation(libs.coroutines)

    implementation(libs.dagger.core)
    ksp(libs.dagger.compiler)

    implementation(libs.bundles.lifecycle.viewmodel)

    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    
    testImplementation(libs.junit5.jupiter.api)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit5.engine)
    testImplementation(libs.junit5.launcher)
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.android)
    testImplementation(libs.mockk.agent)
    testImplementation(libs.coroutines.test)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}