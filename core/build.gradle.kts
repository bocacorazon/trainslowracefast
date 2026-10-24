plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
}

android {
    namespace = "com.trainerapp.core"
    compileSdk = 35
    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

kotlin {
    androidTarget()
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
        }
        jvmMain.dependencies {
            // Blue Falcon 3.x BlueZ engine (sdbus/BlueZ over D-Bus, JVM target).
            // Transitive: dev.bluefalcon:blue-falcon-core.
            implementation("com.monkopedia:blue-falcon-sdbus:1.1.0-3.0.3")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")
        }
    }
}
