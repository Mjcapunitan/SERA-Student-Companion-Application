plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.0.0"
    id("androidx.navigation.safeargs")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.sera"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.sera"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    secrets {
        propertiesFileName = "secrets.properties"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    val nav_version = "2.8.5"

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

    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
//  implementation("androidx.room:room-coroutines:2.1.0-alpha04") // Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-paging:2.6.1")

    // Dagger
    implementation("com.google.dagger:hilt-android:2.55")
    ksp("com.google.dagger:dagger-compiler:2.55") // Dagger compiler
    ksp("com.google.dagger:hilt-compiler:2.55")   // Hilt compiler
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0-beta01")

    //LifeCycle SavedStates :

    val lifecycle_version = "2.8.7"
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version")
//    Serialization :
    implementation(kotlin("stdlib"))
    //navigation compose, serialization, splash screen
    implementation("androidx.navigation:navigation-compose:$nav_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("androidx.core:core-splashscreen:1.0.0")
    //Color picker
    implementation("com.github.skydoves:colorpicker-compose:1.1.2")
    //Moshi
    implementation("com.squareup.moshi:moshi:1.14.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.14.0")
    //Accompanist
    implementation("com.google.accompanist:accompanist-flowlayout:0.24.11-rc")
    implementation("com.google.accompanist:accompanist-insets-ui:0.24.11-rc")

    //App compat, material icon
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.compose.material:material-icons-core:1.5.4")
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    implementation("com.google.android.material:material:1.9.0")
    //Gson
    implementation("com.google.code.gson:gson:2.10.1")

    //Glance app widget
    implementation("androidx.glance:glance:1.0.0-alpha05")
    implementation("androidx.glance:glance-appwidget:1.0.0-alpha05")

    //Recycler View, constraint layout
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    //View binding, view model
    implementation("androidx.compose.ui:ui-viewbinding")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    //extracting pdf, word text
    implementation("com.tom-roush:pdfbox-android:2.0.27.0")
    implementation("com.github.shubham0204:Text2Summary-Android:alpha-05")
    implementation("org.zwobble.mammoth:mammoth:1.5.0")

    //Gemini SDK
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    implementation("com.google.android.material:material:1.12.0")

    //ocr
    implementation("com.google.mlkit:text-recognition:16.0.0")

}