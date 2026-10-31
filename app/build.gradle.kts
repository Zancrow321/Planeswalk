plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "de.lebensmittelampel.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "de.lebensmittelampel.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 3
        versionName = "1.2"
    }

    // Fester Schlüssel, damit neue Versionen sich über alte installieren lassen.
    signingConfigs {
        create("app") {
            storeFile = file("signing/app.keystore")
            storePassword = "lebensmittel"
            keyAlias = "app"
            keyPassword = "lebensmittel"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("app")
        }
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("app")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.play.services.code.scanner)
    debugImplementation(libs.androidx.ui.tooling)

    testImplementation(libs.junit)
}
