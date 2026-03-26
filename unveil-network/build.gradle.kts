import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = "me.passos.libs.unveil"
version = "0.0.2"

kotlin {
    androidLibrary {
        namespace = "me.passos.libs.unveil.network"
        compileSdk =
            libs.versions.android.compileSdk
                .get()
                .toInt()
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()

        compilations.configureEach {
            compilerOptions.configure {
                jvmTarget.set(JvmTarget.JVM_21)
            }
        }
    }
    if (System.getenv("CI") == "true") {
        linuxX64()
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(project(":unveil-core"))
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

mavenPublishing {
    publishToMavenCentral()

    coordinates(group.toString(), "unveil-network", version.toString())

    pom {
        name = "Unveil Network"
        description = "Network traffic capture plugin for Unveil."
        inceptionYear = "2024"
        url = "https://github.com/danielpassos/unveil/"
        licenses {
            licenses {
                license {
                    name = "Apache License, Version 2.0"
                    url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                    distribution = "repo"
                }
            }
        }
        developers {
            developer {
                id = "danielpassos"
                name = "Daniel Passos"
                url = "https://daniel.passos.me"
            }
        }
        scm {
            url = "https://github.com/danielpassos/unveil/"
            connection = "scm:git:git://github.com/danielpassos/unveil.git"
            developerConnection = "scm:git:ssh://git@github.com:danielpassos/unveil.git"
        }
    }
}
