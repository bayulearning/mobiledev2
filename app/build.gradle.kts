plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.mobiledev2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mobiledev2"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

//    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
//    }
    buildTypes {
        release {
            buildConfigField("String", "MIDTRANS_CLIENT_KEY", "\"SB-Mid-client-TeCLukjvzrZc2gm4\"")
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            buildConfigField("String", "MIDTRANS_CLIENT_KEY", "\"SB-Mid-client-TeCLukjvzrZc2gm4\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("com.google.android.material:material:1.10.0")
    implementation ("com.google.android.flexbox:flexbox:3.0.0")
    implementation ("com.midtrans:uikit:2.0.0-SANDBOX")
}