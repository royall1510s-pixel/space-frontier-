plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.spacefrontier.game"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.spacefrontier.game"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        viewBinding = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
}
