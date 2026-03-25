import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.vanniktech.mavenPublish) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlint) apply false
}

// Capture catalog references before entering subprojects scope where libs is unavailable
val detektPluginId =
    libs.plugins.detekt
        .get()
        .pluginId
val ktlintPluginId =
    libs.plugins.ktlint
        .get()
        .pluginId
val ktlintVersion =
    libs.versions.ktlint.tool
        .get()
val nlopezDetekt = libs.nlopez.compose.rules.detekt
val nlopezKtlint = libs.nlopez.compose.rules.ktlint

subprojects {
    apply(plugin = ktlintPluginId)
    apply(plugin = detektPluginId)

    configure<KtlintExtension> {
        version.set(ktlintVersion)
        reporters {
            reporter(ReporterType.HTML)
        }
        filter {
            exclude { it.file.path.contains("/build/") }
            exclude { it.file.path.contains("Test/") }
        }
    }

    configure<DetektExtension> {
        buildUponDefaultConfig = true
        config.from(rootProject.files("detekt.yml"))
        source.from(
            fileTree("src") { include("*Main/kotlin/**/*.kt") }
        )
        reports {
            html.required.set(true)
            txt.required.set(false)
            xml.required.set(false)
            sarif.required.set(false)
        }
    }

    dependencies {
        "detektPlugins"(nlopezDetekt)
        "ktlintRuleset"(nlopezKtlint)
    }
}
