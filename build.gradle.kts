plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.dagger.hilt.android") version "2.57.1" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
    id("com.google.devtools.ksp") version "2.0.21-1.0.25"
    id("com.google.gms.google-services")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    id("kotlin-parcelize")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.example.tripshare"
    compileSdk = 35

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.tripshare"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["FACEBOOK_APP_ID"] = "1562962645127772"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions { jvmTarget = "11" }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    // FIX 2: DELETE THIS ENTIRE BLOCK.
    // You are using the new Compose Compiler *plugin* (from `alias(libs.plugins.kotlin.compose)`).
    // This old `composeOptions` block conflicts with it and is meant for Kotlin 1.7.
    /*
    composeOptions {
        kotlinCompilerExtensionVersion = "1.7.0"
    }
    */
}

dependencies {
    // Datastore
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.datastore:datastore-preferences-core:1.1.1")
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.inappmessaging.display)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    implementation("com.google.firebase:firebase-functions:21.0.0")
    // Room (KSP)
    // FIX 3: Room version 2.8.3 does not exist. The latest stable version is 2.6.1.
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation ("androidx.sqlite:sqlite:2.4.0")
    implementation ("androidx.sqlite:sqlite-framework:2.4.0")
    implementation("com.google.dagger:hilt-android:2.57.1")
    ksp("com.google.dagger:hilt-android-compiler:2.57.1")
    // Credentials / Auth
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // FIX 4: This library version MUST be compatible with Kotlin 2.0.21.
    // Version 1.5.1 is for Kotlin 1.8.x. Use 1.7.1 for Kotlin 2.0.x.
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")

    // Firebase / Google
    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation(libs.play.services.location) // This is probably fine if defined in libs
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.facebook.android:facebook-login:16.3.0")
    // FIX 5: You have a duplicate Facebook login dependency. Remove this line:
    // implementation ("com.facebook.android:facebook-login:latest.release")

    // Coroutines
    // FIX 6: These are slightly old. 1.8.1 is fine, but 1.8.1 is the latest for Kotlin 2.0.
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Navigation / Calendar / Time
    implementation("androidx.navigation:navigation-compose:2.8.0")
    implementation("com.kizitonwose.calendar:compose:2.0.3")
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.6")

    // Compose BOM & UI
    implementation(platform("androidx.compose:compose-bom:2024.09.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")
    // Coil
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Core AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Lifecycle Compose
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    // Maps
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.maps.android:maps-compose:2.11.4")
    // FIX 7: You have a duplicate location dependency. Remove this line:
    // (The one above, 21.3.0, is newer anyway)
    // implementation ("com.google.android.gms:play-services-location:21.2.0")

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.01"))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation ("androidx.work:work-runtime-ktx:2.8.1")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.moshi:moshi:1.15.1")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")
    implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.0")

    testImplementation("junit:junit:4.13.2")

    // Optional: If you want to use Mockito later for mocking objects
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.10.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation ("org.mockito.kotlin:mockito-kotlin:5.0.0")

}

// Room KSP args (optional)
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.generateKotlin", "true")
}