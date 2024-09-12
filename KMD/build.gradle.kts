// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("app.cash.sqldelight") version "2.0.1" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    kotlin("jvm") version "1.9.25"
}
// Turn it off and on again