plugins {
    id("com.android.application")
}

android {
    namespace = "es.redmi4x.relojclima"
    compileSdk = 34

    defaultConfig {
        applicationId = "es.redmi4x.relojclima"
        minSdk = 21
        targetSdk = 25
        versionCode = 7
        versionName = "1.6"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
        debug {
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // none — system WebView only
}

