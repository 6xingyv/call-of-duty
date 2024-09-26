import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.lang.kotlin.multiplatform)
    alias(libs.plugins.platform.android.application)
    alias(libs.plugins.ui.jetbrains.compose)
    alias(libs.plugins.ui.compose.compiler)
    kotlin("plugin.serialization").version("2.0.0")
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

//    listOf(
//        iosX64(),
//        iosArm64(),
//        iosSimulatorArm64()
//    ).forEach { iosTarget ->
//        iosTarget.binaries.framework {
//            baseName = "ComposeApp"
//            isStatic = true
//        }
//    }

    sourceSets {

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.platform.androidx.activity.compose)
            implementation(libs.web.ktor.okhttp)
            implementation(libs.utils.lang.coroutines.android)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.platform.androidx.lifecycle.viewmodel)
            implementation(libs.platform.androidx.lifecycle.runtime.compose)
            implementation(libs.ui.compose.navigation)
            implementation(libs.utils.compose.hooks)
            implementation(libs.utils.lang.coroutines.core)
            implementation(libs.utils.lang.serialization.json)
            implementation(libs.web.ktor.core)
            implementation(libs.web.ktor.negotiation)
            implementation(libs.web.ktor.serialization.json)
            //implementation(libs.data.settings.multiplatform)
        }
//        iosMain.dependencies {
//            implementation(libs.web.ktor.darwin)
//        }
    }
}

android {
    namespace = "io.mocha.duty"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "io.mocha.duty"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    dependencies {
        debugImplementation(compose.uiTooling)
    }
}

