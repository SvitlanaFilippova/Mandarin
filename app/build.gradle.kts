import java.util.Properties

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
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mandarinkafe.mandarin"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Mapkit
        val keystoreFile = project.rootProject.file("apikeys.properties")
        val properties = Properties()
        properties.load(keystoreFile.inputStream())
        val mapKitApiKey = properties.getProperty("MAPKIT_API_KEY") ?: ""
        buildConfigField(
            type = "String",
            name = "MAPKIT_API_KEY",
            value = mapKitApiKey
        )

        // iiko
        buildConfigField(
            type = "String",
            name = "IIKO_API_KEY",
            value = properties.getProperty("IIKO_API_KEY") ?: ""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
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
    implementation(libs.coil.compose)

    // Retrofit
    implementation(libs.retrofit)

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