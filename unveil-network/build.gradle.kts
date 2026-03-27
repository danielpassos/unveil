import com.android.build.api.dsl.androidLibrary

plugins {
    id("unveil-kmp-library-compose")
    id("unveil-publish")
}

compose.resources {
    packageOfResClass = "me.passos.libs.unveil.network.resources"
}

kotlin {
    androidLibrary {
        namespace = "me.passos.libs.unveil.network"
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":unveil-core"))
        }
    }
}
