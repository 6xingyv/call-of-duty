plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.platform.android.application) apply false
    alias(libs.plugins.platform.android.library) apply false
    alias(libs.plugins.ui.jetbrains.compose) apply false
    alias(libs.plugins.ui.compose.compiler) apply false
    alias(libs.plugins.lang.kotlin.multiplatform) apply false
    alias(libs.plugins.lang.kotlin.ksp) apply false
}