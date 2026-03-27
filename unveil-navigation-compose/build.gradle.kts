import com.android.build.api.dsl.androidLibrary

plugins {
    id("unveil-kmp-library")
    id("unveil-publish")
}

kotlin {
    androidLibrary {
        namespace = "me.passos.libs.unveil.navigation.compose"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":unveil-navigation"))
            implementation(libs.navigation.compose)
        }
    }
}
