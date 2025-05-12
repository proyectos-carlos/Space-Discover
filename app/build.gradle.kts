plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.example.solarsystemapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.solarsystemapp"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.material)
        implementation(libs.navigation.fragment.ktx)
        implementation(libs.navigation.ui.ktx)
          implementation(libs.androidx.activity)
            implementation(libs.androidx.constraintlayout)
            testImplementation(libs.junit)

    //Retrofit:
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)

    //Picasso:
    implementation(libs.picasso)

    //Data Store:
    implementation(libs.datastore.preferences)

    //Room:
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    //Chips
    implementation(libs.material.v1110)

    //Glide:
    implementation(libs.glide)
    ksp(libs.compiler)

    //Data store:
    implementation (libs.androidx.datastore.preferences.v113)

    //Splash Screen:
    implementation (libs.androidx.core.splashscreen)

    //Photo view:
 //   implementation (libs.photoview)



//    //Safe args:
//    implementation(libs.navigation.safe.args.gradle.plugin)

//    //Grid Layout:
//    implementation(libs.gridlayout.v7)
//    implementation(libs.appcompat.v7)

    androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)

}