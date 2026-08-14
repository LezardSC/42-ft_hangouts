plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}
android {
    namespace = "com.fortytwo.hangouts"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.fortytwo.hangouts"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.19.0")
    implementation("androidx.activity:activity-ktx:1.13.0")
}