plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.trainerapp.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.trainerapp.android"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0-skeleton"
    }

    buildTypes {
        debug { isMinifyEnabled = false }
        release { isMinifyEnabled = false }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        jvmToolchain(21)
    }
}

dependencies {
    implementation(project(":core"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
}
