import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.jetbrains.kotlin.parcelize) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.detekt)
}


tasks.withType<Detekt>().configureEach {
    parallel = true
    autoCorrect = false
    disableDefaultRuleSets = false
    buildUponDefaultConfig = true

    jvmTarget = JavaVersion.valueOf(libs.versions.java.get()).toString()

    setSource(files(projectDir))
    include("**/*.kt")
    include("**/*.kts")
    exclude("**/resources/**")
    exclude("**/build/**")

    reports {
        xml.required.set(false)
        html.required.set(true)
        txt.required.set(true)
        sarif.required.set(false)
        md.required.set(false)
    }

    config.setFrom(files(project.rootDir.resolve("conf/custom-detekt.yml")))
}

tasks.register<Detekt>("detektAll") {
    description = "Runs detekt over the whole code base"
    group = "verification"

    parallel = true
    autoCorrect = false
    disableDefaultRuleSets = false
    buildUponDefaultConfig = false

    jvmTarget = JavaVersion.valueOf(libs.versions.java.get()).toString()
    setSource(files(projectDir))
    include("**/*.kt", "**/*.kts")
    exclude("**/resources/**", "**/build/**")

    config.setFrom(files(rootProject.file("conf/custom-detekt.yml")))

    reports {
        xml.required.set(true)
        html.required.set(true)
        txt.required.set(true)
    }
}

tasks.register<Detekt>("detektFormat") {
    description = "Auto-corrects the code base using Detekt formatting rules"
    group = "formatting"

    parallel = true
    autoCorrect = true
    disableDefaultRuleSets = false
    buildUponDefaultConfig = false

    jvmTarget = JavaVersion.valueOf(libs.versions.java.get()).toString()
    setSource(files(projectDir))
    include("**/*.kt", "**/*.kts")
    exclude("**/resources/**", "**/build/**")

    config.setFrom(files(rootProject.file("conf/custom-detekt.yml")))

    reports {
        xml.required.set(false)
        html.required.set(false)
        txt.required.set(true)
    }
}

tasks.register<DetektCreateBaselineTask>("detektProjectBaseline") {
    description = "Creates or overrides the Detekt baseline"
    group = "verification"

    setSource(files(projectDir))
    include("**/*.kt", "**/*.kts")
    exclude("**/resources/**", "**/build/**")

    buildUponDefaultConfig.set(true)
    ignoreFailures.set(true)
    parallel.set(true)
    jvmTarget = JavaVersion.valueOf(libs.versions.java.get()).toString()

    config.setFrom(files(rootProject.file("conf/custom-detekt.yml")))
}

dependencies {
    add("detekt", libs.staticAnalysis.detektCli)
    add("detektPlugins", libs.staticAnalysis.detektFormatting)
    add("detektPlugins", libs.staticAnalysis.detektLibraries)
}