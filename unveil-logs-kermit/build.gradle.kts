import com.android.build.api.dsl.androidLibrary

plugins {
    id("unveil-kmp-library")
    id("unveil-publish")
}

kotlin {
    androidLibrary {
        namespace = "me.passos.libs.unveil.logs.kermit"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":unveil-logs"))
            implementation(libs.kermit)
        }
    }
}
