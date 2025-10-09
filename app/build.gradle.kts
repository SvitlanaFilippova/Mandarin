import java.util.Properties

// Подмена google-services.json, если он отсутствует
val googleServicesFile = file("${project.rootDir}/app/google-services.json")
val googleServicesSampleFile = file("${project.rootDir}/app/google-services.sample.json")

if (!googleServicesFile.exists() && googleServicesSampleFile.exists()) {
    println("!!! google-services.json не найден. Копирую sample...")
    googleServicesFile.writeText(googleServicesSampleFile.readText())
}

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.jetbrains.kotlin.parcelize)
    // alias(libs.plugins.google.services)
    // alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "com.mandarinkafe.mandarin"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.mandarinkafe.mandarin"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Подмена apikeys.properties, если он отсутствует
        val keystoreFile = file("${project.rootDir}/apikeys.properties")
        val keystoreSampleFile = file("${project.rootDir}/apikeys.sample.properties")

        if (!keystoreFile.exists() && keystoreSampleFile.exists()) {
            println("!!! apikeys.properties не найден. Копирую sample...")
            keystoreFile.writeText(keystoreSampleFile.readText())
        }

        // Чтение ключей из apikeys.properties
        val properties = Properties().apply {
            load(keystoreFile.inputStream())
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

        //noinspection WrongGradleMethod
        keys.forEach { key ->
            val value = properties.getProperty(key) ?: ""
            this@defaultConfig.buildConfigField("String", key, "\"$value\"")
        }
    }

    buildTypes {

        debug {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
        }

        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.valueOf(libs.versions.java.get())
        targetCompatibility = JavaVersion.valueOf(libs.versions.java.get())
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Mandarin"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.compose.activity)
            // Подключаем Shared модуль
            implementation(project(":shared"))

            // Koin
            implementation(libs.koin.core)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
            implementation(libs.koin.ktor)

            // Android Jetpack Lifecycle
            implementation(libs.androidx.lifecycle.runtime.ktx)
            implementation(libs.androidx.lifecycle.process)

            // Navigation for Compose
            implementation(libs.androidx.navigation.compose)

            // Kamel
            implementation(libs.kamel.image.default)

            // Core
            implementation(libs.androidx.core.ktx)

            // Napier
            implementation(libs.napier)

            // Material Design
            implementation(libs.material)

            // Activity
            implementation(libs.androidx.activity)

            // Jetpack Compose
            implementation(libs.androidx.compose.ui)
            implementation(libs.androidx.compose.ui.tooling)
            implementation(libs.androidx.compose.ui.tooling.preview)
            implementation(libs.androidx.compose.foundation)
            implementation(libs.androidx.compose.material)
            implementation(libs.androidx.compose.material3)
            implementation(libs.androidx.compose.runtime)
            implementation(libs.androidx.compose.activity)
            implementation(libs.androidx.compose.viewmodel)

            // Mapkit
            implementation(libs.com.yandex.maps.mobile)

            // Accompanist для управления системными UI + навигацией с BottomSheet
            implementation(libs.accompanist.navigation.material)
            implementation(libs.accompanist.systemuicontroller)

            // Firebase (временно отключено)
            // implementation(libs.firebase.analytics)
            // implementation(libs.firebase.crashlytics)
            // implementation(libs.firebase.config)

            // SQLDelight
            implementation(libs.sqldelight)
            implementation(libs.sqldelight.android.driver)
            implementation(libs.sqldelight.coroutines.extensions)

            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.okhttp)

            // Kotlinx Serialization
            implementation(libs.kotlinx.serialization.json)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}


dependencies {
    debugImplementation(compose.uiTooling)
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.mandarinkafe.mandarin.database")
            migrationOutputDirectory.set(file("src/main/sqldelight/migrations"))
        }
    }
    linkSqlite = true
}