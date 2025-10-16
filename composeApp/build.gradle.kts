@file:Suppress("MagicNumber")

import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.sqldelight)
    id("com.android.application")
    id("dev.icerock.mobile.multiplatform-resources")
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

                // PreCompose (Navogation)
                implementation(libs.precompose)
                implementation(libs.precompose.viewmodel)
                implementation(libs.precompose.koin)

                // url encoder
                implementation("net.thauvin.erik.urlencoder:urlencoder-lib:1.6.0")
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
        minSdk = libs.versions.minSdk.get().toInt()
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

// Чтение ключей из apikeys.properties
val keystoreFile = file("${project.rootDir}/apikeys.properties")
val keystoreSampleFile = file("${project.rootDir}/apikeys.sample.properties")

// Создаём apikeys.properties, если его нет
if (!keystoreFile.exists() && keystoreSampleFile.exists()) {
    println("!!! apikeys.properties не найден. Копирую sample...")
    keystoreFile.writeText(keystoreSampleFile.readText())
}

val properties = Properties().apply {
    load(keystoreFile.inputStream())
}

fun getKey(name: String): String = properties.getProperty(name) ?: ""

// Генерируем BuildConfig.kt файл с ключами
val buildConfigContent = """
package com.mandarinkafe.mandarin.core.data.config

object BuildConfig {
    const val MAPKIT_API_KEY = "${getKey("MAPKIT_API_KEY")}"
    const val IIKO_API_KEY = "${getKey("IIKO_API_KEY")}"
    const val TG_BOT_TOKEN = "${getKey("TG_BOT_TOKEN")}"
    const val TG_CHANNEL_ID = "${getKey("TG_CHANNEL_ID")}"
    const val DEV_TG_CHAT_ID = "${getKey("DEV_TG_CHAT_ID")}"
    const val SERVER_BASE_URL = "${getKey("SERVER_BASE_URL")}"
    const val MANDARIN_API_KEY = "${getKey("MANDARIN_API_KEY")}"
}
""".trimIndent()

val buildConfigFile =
    file("src/commonMain/kotlin/com/mandarinkafe/mandarin/core/data/config/BuildConfig.kt")
buildConfigFile.parentFile.mkdirs()
buildConfigFile.writeText(buildConfigContent)

multiplatformResources {
    resourcesPackage.set("com.mandarinkafe.mandarin")
}
