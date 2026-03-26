// Apply via pluginManager rather than the plugins {} block so that
// generatePrecompiledScriptPluginAccessors does not try to resolve the
// vanniktech plugin at build-logic compile time (its Kotlin metadata version
// is newer than Gradle's embedded Kotlin). The plugin is available at runtime
// through the root build classpath where it is declared with `apply false`.
pluginManager.apply("com.vanniktech.maven.publish")

// All shared POM metadata (SCM, developer, license, host, inception year, URL)
// is declared in the root gradle.properties and read automatically by the plugin.
// Module-specific fields (POM_ARTIFACT_ID, POM_NAME, POM_DESCRIPTION) live in
// each module's own gradle.properties.
