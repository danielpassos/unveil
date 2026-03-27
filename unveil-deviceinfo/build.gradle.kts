import com.android.build.api.dsl.androidLibrary

plugins {
    id("unveil-kmp-library-compose")
    id("unveil-publish")
}

compose.resources {
    packageOfResClass = "me.passos.libs.unveil.deviceinfo.resources"
}

kotlin {
    androidLibrary {
        namespace = "me.passos.libs.unveil.deviceinfo"
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":unveil-core"))
        }
    }
}
