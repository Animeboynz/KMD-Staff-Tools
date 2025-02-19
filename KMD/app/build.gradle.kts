@file:Suppress("ChromeOsAbiSupport")

import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.serialization)
    alias(libs.plugins.room)
    alias(libs.plugins.spotless.gradle)
    id("kotlin-parcelize")
    alias(libs.plugins.aboutLibraries)
    //id("com.google.gms.google-services")
    //id("com.google.firebase.crashlytics")
    //apply(libs.plugins.google.services.get().pluginId)
    //apply(libs.plugins.firebase.crashlytics.get().pluginId)
}

pluginManager.apply {
    apply(libs.plugins.google.services.get().pluginId)
    apply(libs.plugins.firebase.crashlytics.get().pluginId)
}

val supportedAbis = setOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")

fun getCommitCount(): Int {
    val process = ProcessBuilder("git", "rev-list", "--count", "HEAD")
        .redirectErrorStream(true)
        .start()
    return process.inputStream.bufferedReader().use { it.readText().trim().toInt() }
}

fun getFormattedBuildTime(): String {
    val now = Instant.now()
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm:ss")
        .withZone(ZoneId.systemDefault())
    return formatter.format(now)
}

fun getGitSha(): String {
    val process = ProcessBuilder("git", "rev-parse", "--short", "HEAD")
        .redirectErrorStream(true)
        .start()
    return process.inputStream.bufferedReader().use { it.readText().trim() }
}

android {
    namespace = "com.animeboynz.kmd"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.animeboynz.kmd"
        minSdk = 26
        targetSdk = 34

        buildConfigField("String", "COMMIT_COUNT", "\"${getCommitCount()}\"")
        buildConfigField("String", "BUILD_TIME", "\"${getFormattedBuildTime()}\"")
        buildConfigField("String", "COMMIT_SHA", "\"${getGitSha()}\"")


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        versionCode = 1
        versionName = "0.3.0"

        ndk {
            abiFilters += supportedAbis
        }

        buildFeatures {
            viewBinding = true
            buildConfig = true
            shaders = false
        }

    }

    splits {
        abi {
            isEnable = true
            reset()
            include(*supportedAbis.toTypedArray())
            isUniversalApk = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-${getCommitCount()}"
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
        compose = true
        buildConfig = true
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
//    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.kotlinx.immutable.collections)
    implementation(libs.compose.materialmotion)
    implementation(libs.androidx.material3.icons.extended)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.bundles.serialization)
    implementation(libs.bundles.voyager)
    implementation(libs.bundles.coil)
    implementation(libs.okhttp.core)
    implementation(libs.jsoup)
    implementation(libs.compose.prefs)
    implementation(libs.bundles.koin)
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
    implementation(libs.aboutLibraries.compose)
    implementation(libs.zxing)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
}

spotless {
    kotlin {
        target("**/*.kt", "**/*.kts")
        targetExclude("**/build/**/*.kt")
        ktlint(libs.ktlint.core.get().version)
            .editorConfigOverride(
                mapOf(
                    "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
                    "ktlint_standard_class-signature" to "disabled",
                    "ktlint_standard_discouraged-comment-location" to "disabled",
                    "ktlint_standard_function-expression-body" to "disabled",
                    "ktlint_standard_function-signature" to "disabled",
                ),
            )
        trimTrailingWhitespace()
        endWithNewline()
    }
    format("xml") {
        target("**/*.xml")
        targetExclude("**/build/**/*.xml")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

tasks {
    withType<KotlinCompile> {
        compilerOptions.freeCompilerArgs.addAll(
            "-Xcontext-receivers",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
        )
    }
}