plugins {
    id(libs.plugins.androidLibrary.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.google.services.get().pluginId)
}

android {
    namespace = "com.ihridoydas.sduicompose.common"

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(projects.theme)
    // Gradle
    implementation(platform(libs.compose.bom))
    implementation(platform(libs.firebase.bom))
    // UI
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.android.material)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.hilt.compose.navigation)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    //FireBase
    implementation(libs.firebase.database)
    implementation (libs.androidx.lifecycle.runtime.compose)

}
