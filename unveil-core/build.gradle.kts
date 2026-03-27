import com.android.build.api.dsl.androidLibrary

plugins {
    id("unveil-kmp-library-compose")
    id("unveil-publish")
}

compose.resources {
    packageOfResClass = "me.passos.libs.unveil.resources"
}

kotlin {
    androidLibrary {
        namespace = "org.jetbrains.kotlinx.multiplatform.library.template"
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.compose.foundation)
            api(libs.compose.material3)
            api(libs.compose.runtime)
            api(libs.compose.ui)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
    }
}
