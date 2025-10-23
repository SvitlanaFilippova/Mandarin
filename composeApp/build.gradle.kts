@file:Suppress("MagicNumber")

import java.util.Properties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    id("com.android.application")
    id("dev.icerock.mobile.multiplatform-resources")
    id("com.codingfeline.buildkonfig") version "0.17.1"
}

kotlin {

    androidTarget()

    val xcfName = "composeAppKit"

    iosX64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                api(compose.animation)

                // Multiplatfrom ViewModel, Runtime
                implementation(libs.jetbrains.lifecycle.viewmodel)
                implementation(libs.jetbrains.lifecycle.viewmodel.compose)
                implementation(libs.jetbrains.lifecycle.runtime.compose)

                implementation(libs.koin.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
                implementation(libs.napier)

                // Ktor
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.auth)

                // SQLDelight
                implementation(libs.sqldelight)
                implementation(libs.sqldelight.coroutines.extensions)

                // DataStore core
                implementation(libs.datastore.preferences.core)

                // MOKO Resources
                implementation(libs.resources)
                implementation(libs.resources.compose)

                // Kamel for image loading
                implementation(libs.kamel.image.default)

                // Navigation Compose
                implementation(libs.navigation.compose)

                // url encoder
                implementation(libs.urlencoder.lib)

                // pull to refresh
                implementation(libs.materii.pullrefresh)

            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.koin.android)
                implementation(libs.sqldelight.android.driver)
                // DataStore for Android
                implementation(libs.datastore.preferences)
                // Google Play Services Location
                implementation(libs.play.services.location)
                // Yandex MapKit
                implementation(libs.com.yandex.maps.mobile)
                // Ktor Android engine
                implementation(libs.ktor.client.okhttp)
                // Material 3
                implementation(libs.androidx.material3)
                implementation(libs.androidx.appcompat)
                // Activity Compose для permission requests
                implementation(libs.androidx.activity.compose)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.sqldelight.native.driver)
                implementation(libs.ktor.client.darwin)
            }
        }

        androidUnitTest {
            dependencies {
                implementation(libs.androidx.junit)
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
            }
        }
    }
}

android {
    namespace = "com.mandarinkafe.mandarin.shared"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.mandarinkafe.mandarin.shared"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.mandarinkafe.mandarin.shared.database")
            migrationOutputDirectory.set(file("src/commonMain/sqldelight/migrations"))
        }
    }
    linkSqlite = true
}

// --- Чтение ключей из apikeys.properties ---
val apiKeysFile = rootProject.file("apikeys.properties")
val apiKeysSample = rootProject.file("apikeys.sample.properties")

if (!apiKeysFile.exists() && apiKeysSample.exists()) {
    println("!!! apikeys.properties не найден. Копирую sample...")
    apiKeysFile.writeText(apiKeysSample.readText())
}

val props = Properties().apply {
    load(apiKeysFile.inputStream())
}

val keys = listOf(
    "MAPKIT_API_KEY",
    "IIKO_API_KEY",
    "TG_BOT_TOKEN",
    "TG_CHANNEL_ID",
    "DEV_TG_CHAT_ID",
    "SERVER_BASE_URL",
    "MANDARIN_API_KEY"
)

// --- Генерация BuildKonfig для KMP ---
buildkonfig {
    packageName = "com.mandarinkafe.mandarin.shared"

    defaultConfigs {
        keys.forEach { key ->
            val value = props.getProperty(key) ?: ""
            buildConfigField(Type.STRING, key, value)
        }
    }
}

multiplatformResources {
    resourcesPackage.set("com.mandarinkafe.mandarin")
}

tasks.register("packForXcode") {
    group = "build"
    dependsOn("linkDebugFrameworkIosSimulatorArm64")
    dependsOn("linkReleaseFrameworkIosArm64")
}
