plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.coffeshop.deps"
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
}

dependencies {
    implementation(project(path = ":core:build-config:api"))
    implementation(project(path = ":core:build-config:internal"))
    implementation(project(path = ":core:cache:api"))
    implementation(project(path = ":core:cache:internal"))
    implementation(project(path = (":core:logger:api")))
    implementation(project(path = (":core:logger:internal")))
    implementation(project(path = ":core:di"))
    implementation(project(path = ":core:navigation"))

    implementation(libs.bundles.nav3)

    implementation(libs.dagger.core)
    ksp(libs.dagger.compiler)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}