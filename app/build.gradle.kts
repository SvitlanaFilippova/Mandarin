import java.util.Properties

// Подмена google-services.json, если он отсутствует
val googleServicesFile = file("${project.rootDir}/app/google-services.json")
val googleServicesSampleFile = file("${project.rootDir}/app/google-services.sample.json")

if (!googleServicesFile.exists() && googleServicesSampleFile.exists()) {
    println("!!! google-services.json не найден. Копирую sample...")
    googleServicesFile.writeText(googleServicesSampleFile.readText())
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.jetbrains.kotlin.parcelize)
    alias(libs.plugins.jetbrains.kotlin.compose)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
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
    kotlinOptions {
        jvmTarget = JavaVersion.valueOf(libs.versions.java.get()).toString()
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}



dependencies {
    // Android Jetpack Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.process)

    // Navigation for Compose
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.com.hilt)
    implementation(libs.androidx.runtime.livedata)
    ksp(libs.com.hilt.ksp)
    implementation(libs.androidx.hilt.navigation.compose)

    // Coil
    implementation(libs.coil3.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Core
    implementation(libs.androidx.core.ktx)

    // Material Design
    implementation(libs.material)

    // Activity
    implementation(libs.androidx.activity)

    // JUnit
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.compiler)

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
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

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.config.ktx)

    // SQLDelight
    implementation(libs.sqldelight)
    implementation(libs.sqldelight.android.driver)
    implementation(libs.sqldelight.coroutines.extensions)

    // Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.gson)

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)
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