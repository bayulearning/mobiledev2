// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
}
buildscript {
    repositories {
        google()
        mavenCentral() // Menambahkan repositori Maven Central
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.1") // Menentukan versi plugin Gradle
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()

        maven { url = uri("https://jitpack.io") } // Menambahkan repositori JitPack
    }

}

