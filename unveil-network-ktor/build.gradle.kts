import com.android.build.api.dsl.androidLibrary

plugins {
    id("unveil-kmp-library")
    id("unveil-publish")
}

kotlin {
    androidLibrary {
        namespace = "me.passos.libs.unveil.network.ktor"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":unveil-network"))
            implementation(libs.ktor.client.core)
        }
        commonTest.dependencies {
            implementation(libs.ktor.client.mock)
        }
    }
}
