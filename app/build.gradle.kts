plugins {
    id("com.android.application") // Apply Android application plugin
    id("org.jetbrains.kotlin.android") // Apply Kotlin Android plugin
    id("com.google.gms.google-services") // Apply Google Services plugin for Firebase integration
    id("maven-publish") // Add maven publishing plugin
}

android {
    namespace = "com.example.japaneseflash"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.japaneseflash"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true

        // Already included; no changes needed for test instrumentation runner
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Add wave animation dependencies if not already present

    compileOnly("com.android.support:appcompat-v7:28.0.0")

    // Existing dependencies
    implementation(platform("com.google.firebase:firebase-bom:28.3.1")) // Use the latest version
    implementation("com.google.firebase:firebase-firestore")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.material:material:1.0.0")
    implementation(libs.monitor)
    implementation(libs.androidx.junit)
    implementation(libs.activity)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.testng)

    //firebase
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.firebase:firebase-auth:21.1.0")
}

