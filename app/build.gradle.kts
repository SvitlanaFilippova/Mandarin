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

        // Mapkit
        val mapKitApiKey = properties.getProperty("MAPKIT_API_KEY") ?: ""
        buildConfigField("String", "MAPKIT_API_KEY", "\"$mapKitApiKey\"")

        // iiko
        val iikoApiKey = properties.getProperty("IIKO_API_KEY") ?: ""
        buildConfigField("String", "IIKO_API_KEY", "\"$iikoApiKey\"")
    }

    buildTypes {

        debug {
            applicationIdSuffix = ".debug"
        }
        
        release {
            isMinifyEnabled = false
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

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.scalars)

    // Gson
    implementation(libs.converter.gson)

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
}