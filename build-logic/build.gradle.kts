plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
    // compose-compiler-gradle-plugin provides org.jetbrains.kotlin.plugin.compose (separate artifact in Kotlin 2.x)
    implementation("org.jetbrains.kotlin:compose-compiler-gradle-plugin:${libs.versions.kotlin.get()}")
    // AGP provides com.android.kotlin.multiplatform.library
    implementation("com.android.tools.build:gradle:${libs.versions.agp.get()}")
    implementation("org.jetbrains.compose:compose-gradle-plugin:${libs.versions.composeMultiplatform.get()}")
}
