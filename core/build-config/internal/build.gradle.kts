import java.util.Properties

plugins {
    alias(libs.plugins.android.library)

    alias(libs.plugins.ksp)
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

val baseUrl: String = localProperties.getProperty("COFFEE_SHOP_BASE_URL", "\"\"")
val testBaseUrl: String = localProperties.getProperty("COFFEE_SHOP_TEST_BASE_URL", "\"\"")
val callTimeOut: String = localProperties.getProperty("CALL_TIMEOUT_SEC", "\"\"")
val readTimeOut: String = localProperties.getProperty("READ_TIMEOUT_SEC", "\"\"")
val writeTimeOut: String = localProperties.getProperty("WRITE_TIMEOUT_SEC", "\"\"")

android {
    namespace = "com.coffeeshop.buildconfig.internal"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "COFFEE_SHOP_BASE_URL", "\"$baseUrl\"")
        buildConfigField("String", "COFFEE_SHOP_TEST_BASE_URL", "\"$testBaseUrl\"")
        buildConfigField("String", "CALL_TIMEOUT_SEC", "\"$callTimeOut\"")
        buildConfigField("String", "READ_TIMEOUT_SEC", "\"$readTimeOut\"")
        buildConfigField("String", "WRITE_TIMEOUT_SEC", "\"$writeTimeOut\"")
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

    implementation(libs.dagger.core)
    ksp(libs.dagger.compiler)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}