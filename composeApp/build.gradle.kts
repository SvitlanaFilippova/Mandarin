@file:Suppress("MagicNumber")

import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.sqldelight)
}

kotlin {

    androidLibrary {
        namespace = "com.mandarinkafe.mandarin.shared"
        compileSdk = 36
        minSdk = 24

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    // For iOS targets, this is also where you should
    // configure native binary output. For more information, see:
    // https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

    // A step-by-step guide on how to include this library in an XCode
    // project can be found here:
    // https://developer.android.com/kotlin/multiplatform/migrate
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

    // Source set declarations.
    // Declaring a target automatically creates a source set with the same name. By default, the
    // Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
    // common to share sources between related targets.
    // See: https://kotlinlang.org/docs/multiplatform-hierarchy.html
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
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)
                implementation(libs.koin.core)
                implementation(libs.ktor.client.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
                implementation(libs.napier)
                
                // SQLDelight
                implementation(libs.sqldelight)
                implementation(libs.sqldelight.coroutines.extensions)
                
                // DataStore core
                implementation(libs.datastore.preferences.core)
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
            }
        }

        iosMain {
            dependencies {
                implementation(libs.sqldelight.native.driver)
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.junit)
            }
        }
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

val buildConfigFile = file("src/commonMain/kotlin/com/mandarinkafe/mandarin/core/data/config/BuildConfig.kt")
buildConfigFile.parentFile.mkdirs()
buildConfigFile.writeText(buildConfigContent)
