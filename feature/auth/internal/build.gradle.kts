plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.coffeeshop.auth.internal"
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
    buildToolsVersion = "36.1.0"
}

dependencies {
    // modules
    implementation(project(":core:network"))
    implementation(project(":core:common"))
    implementation(project(":core:utils"))
    implementation(project(":core:build-config:api"))
    implementation(project(":core:design-system"))
    implementation(project(":feature:auth:api"))
    implementation(project(":feature:products:api"))

    // compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // navigation
    implementation(libs.navigation3.runtime)
    implementation(libs.navigation3.ui)
    implementation(libs.navigation3.router)

    // bundles
    implementation(libs.bundles.lifecycle.viewmodel)

    // android core
    implementation(libs.androidx.core.ktx)

    // lifecycle + viewmodel
    implementation(libs.lifecycle.viewmodel.compose)

    // coroutines
    implementation(libs.coroutines)

    // dagger
    implementation(libs.dagger.core)
    ksp(libs.dagger.compiler)
    implementation(project(path = ":core:di"))

    // test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
