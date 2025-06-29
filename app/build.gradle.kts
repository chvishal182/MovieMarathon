plugins {
    alias(libs.plugins.android.application)
    id("com.google.dagger.hilt.android")
}



android {
    namespace = "com.chelv.moviemarathon"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.chelv.moviemarathon"
        minSdk = 30
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

    buildFeatures{
        dataBinding = true
    }
}

configurations.all {
    exclude(group = "org.jetbrains", module = "annotations")
}




dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.swiperefreshlayout)
    implementation (libs.github.glide)
    implementation(libs.room.compiler)

    implementation(libs.adapter.rxjava3)

    //Paging Library
    implementation(libs.paging.runtime)
    implementation(libs.paging.rxjava2)
    implementation(libs.paging.rxjava3)

    //Hilt Dagger
    implementation(libs.hilt.android)
    implementation(libs.transport.runtime)
    annotationProcessor(libs.hilt.compiler)

    // ViewModel
    implementation (libs.lifecycle.viewmodel)
    // LiveData
    implementation (libs.lifecycle.livedata)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    annotationProcessor (libs.glide.compiler)
}