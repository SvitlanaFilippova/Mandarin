plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.navigation.safeargs.kotlin)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.jetbrains.kotlin.parcelize)
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // AdapterDelegates
    implementation(libs.adapterdelegates.dsl)
    // AdapterDelegates для работы с ViewBinding
    implementation(libs.adapterdelegates.dsl.viewbinding)

    // Navigation
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Fragment
    implementation(libs.androidx.fragment.ktx)

    // ViewPager2
    implementation(libs.androidx.viewpager2)

    // Hilt
    implementation(libs.com.hilt)
    ksp(libs.com.hilt.ksp)

    // Glide
    implementation(libs.glide)
    ksp(libs.compiler)

    // DotsIndicator
    implementation(libs.circleindicator)

    // Retrofit
    implementation(libs.retrofit)

    // Gson
    implementation(libs.converter.gson)

    // Core
    implementation(libs.androidx.core.ktx)

    // AppCompat
    implementation(libs.androidx.appcompat)

    // Material
    implementation(libs.material)

    // Activity
    implementation(libs.androidx.activity)

    // ConstraintLayout
    implementation(libs.androidx.constraintlayout)

    // CoordinatorLayout
    implementation(libs.androidx.coordinatorlayout)

    // Legacy
    implementation(libs.androidx.legacy.support.v4)

    // Lifecycle
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // JUnit
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}