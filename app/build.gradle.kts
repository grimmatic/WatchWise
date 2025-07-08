plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id ("kotlin-parcelize")

}
android {
    namespace = "com.vakifbank.WatchWise"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.vakifbank.WatchWise"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures{
        viewBinding=true
    }
    configurations.all {
        exclude(group = "com.intellij", module = "annotations")
        resolutionStrategy {
            force("org.jetbrains:annotations:23.0.0")
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/gradle/incremental.annotation.processors"
        }
    }
}
dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("org.jetbrains:annotations:23.0.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.0")
    implementation("androidx.navigation:navigation-ui:2.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("androidx.room:room-runtime:2.7.2")
    implementation("androidx.room:room-ktx:2.7.2")
    implementation("com.google.dagger:hilt-android:2.56.2")
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.cardview)
    //kapt("androidx.room:room-compiler:2.7.2")
    ksp("androidx.room:room-compiler:2.7.2")
    // kapt("com.google.dagger:hilt-compiler:2.48.1")
    ksp("com.google.dagger:hilt-compiler:2.48.1")
    implementation("com.google.dagger:hilt-compiler:2.48.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.airbnb.android:lottie:6.2.0")
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}