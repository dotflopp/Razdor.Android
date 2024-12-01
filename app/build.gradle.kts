plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.flopp.razdor"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.flopp.razdor"
        minSdk = 21
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
    viewBinding {
        enable = true
    }
}

dependencies {
    implementation (libs.webrtc)
    implementation(libs.okhttp)

    implementation("com.github.GoodDamn.VectorEditorAndroid:import:1.0")
    implementation("com.github.GoodDamn.VectorEditorAndroid:misc:1.0")
    implementation("com.github.GoodDamn.VectorEditorAndroid:core:1.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(project(":ui"))
    implementation(libs.androidx.viewpager2)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
