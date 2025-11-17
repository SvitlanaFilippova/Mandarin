@file:Suppress("MagicNumber")

import com.codingfeline.buildkonfig.compiler.FieldSpec.Type
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.swiftklib)
    id("com.android.application")
    id("dev.icerock.mobile.multiplatform-resources")
    id("com.codingfeline.buildkonfig") version "0.17.1"
}

kotlin {
    compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")

    androidTarget()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.compilations {
            val main by getting { cinterops { create("yookassa") } }
        }
    }


    cocoapods {
        version = "1.0"
        summary = "Shared code for Mandarin"
        homepage = "https://example.com"
        ios.deploymentTarget = "16.0"

        framework {
            baseName = "composeApp"
            isStatic = true
        }

        pod("YandexMapsMobile") {
            version = "4.25.0-full"
            packageName = "YandexMapKit"
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
                // Google Play Services
                implementation(libs.play.services.location)
                implementation(libs.play.services.auth)
                // Yandex MapKit
                implementation(libs.com.yandex.maps.mobile)
                // Ktor Android engine
                implementation(libs.ktor.client.okhttp)
                // Material 3
                implementation(libs.androidx.material3)
                implementation(libs.androidx.appcompat)
                // Activity Compose для permission requests
                implementation(libs.androidx.activity.compose)
                // ЮKassa
                implementation(libs.yookassa.android.sdk)
                // Явно указываем версии work-runtime для разрешения конфликтов
                implementation(libs.androidx.work.runtime)
                implementation(libs.androidx.work.runtime.ktx)
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

// Разрешение конфликтов зависимостей для androidx.work
configurations.all {
    resolutionStrategy {
        val workVersion = libs.versions.work.get()
        force("androidx.work:work-runtime:$workVersion")
        force("androidx.work:work-runtime-ktx:$workVersion")
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
    "MANDARIN_API_KEY",
    "YOOKASSA_CLIENT_APPLICATION_KEY",
    "YOOKASSA_SHOP_ID"
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

// Update iOS version from gradle
tasks.register("updateIOSVersion") {
    group = "build"

    doLast {
        val configFile = file("${rootProject.projectDir}/iosApp/Configuration/Config.xcconfig")
        val versionName = libs.versions.versionName.get()
        val versionCode = libs.versions.versionCode.get()

        val content = configFile.readText()
        val updated = content
            .replace(Regex("MARKETING_VERSION=(.*)"), "MARKETING_VERSION=$versionName")
            .replace(Regex("CURRENT_PROJECT_VERSION=(.*)"), "CURRENT_PROJECT_VERSION=$versionCode")

        configFile.writeText(updated)
    }
}

// Make packForXcode depend on version update
tasks.named("packForXcode") {
    dependsOn("updateIOSVersion")
}

swiftklib {
    create("yookassa") {
        path = file("../iosApp/iosApp/yookassa")
        packageName("com.mandarinkafe.mandarin.yookassa")
    }
}
