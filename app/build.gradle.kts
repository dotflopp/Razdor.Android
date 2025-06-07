plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.example.zov_android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.zov_android"
        minSdk = 34
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
    implementation("com.google.dagger:hilt-android:2.51.1")
    implementation("org.testng:testng:7.7.0") {
        exclude(group = "junit") // Исключаем junit
        exclude(group = "org.hamcrest") // Исключаем hamcrest
    }
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    implementation ("com.google.code.gson:gson:2.11.0")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")

    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("io.coil-kt:coil:2.4.0")


    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation ("androidx.activity:activity-ktx:1.7.0")
    implementation ("androidx.fragment:fragment-ktx:1.6.0")

    implementation ("com.microsoft.signalr:signalr:9.0.5")
    implementation ("com.mesibo.api:webrtc:1.0.5")

    testImplementation ("junit:junit:4.13.2") {
        exclude(group = "org.hamcrest", module = "hamcrest-core")
    }
    testImplementation ("org.hamcrest:hamcrest:2.2")
    testImplementation ("org.mockito:mockito-core:3.12.4") {
        exclude(group = "org.hamcrest")
    }


    implementation ("com.guolindev.permissionx:permissionx:1.6.1")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

kapt {
    correctErrorTypes = true
}