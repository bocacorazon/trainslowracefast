plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    kotlin("plugin.compose")
}

kotlin {
    jvmToolchain(21)
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)

    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")
}

compose.desktop {
    application {
        mainClass = "com.trainerapp.desktop.MainKt"
    }
}