package me.passos.libs.unveil.deviceinfo

import me.passos.libs.unveil.UnveilIcon
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DeviceInfoPluginTest {

    private val plugin =
        DeviceInfoPlugin(
            appVersionName = "1.0.0",
            appBuildNumber = "42",
            buildVariant = "debug",
            environment = "staging",
        )

    @Test
    fun `plugin id is device_info`() {
        assertEquals("device_info", plugin.id)
    }

    @Test
    fun `plugin title is Device Info`() {
        assertEquals("Device Info", plugin.title)
    }

    @Test
    fun `plugin icon is the phone emoji`() {
        assertIs<UnveilIcon.Emoji>(plugin.icon)
        assertEquals("📱", (plugin.icon as UnveilIcon.Emoji).character)
    }

    @Test
    fun `plugin has no quick actions`() {
        assertTrue(plugin.quickActions.isEmpty())
    }

    // region — App fields

    @Test
    fun `deviceInfo contains the provided app version name`() {
        assertEquals("1.0.0", plugin.deviceInfo.appVersionName)
    }

    @Test
    fun `deviceInfo contains the provided build number`() {
        assertEquals("42", plugin.deviceInfo.appBuildNumber)
    }

    @Test
    fun `deviceInfo contains the provided build variant`() {
        assertEquals("debug", plugin.deviceInfo.buildVariant)
    }

    @Test
    fun `deviceInfo contains the provided environment`() {
        assertEquals("staging", plugin.deviceInfo.environment)
    }

    @Test
    fun `environment defaults to null when not provided`() {
        val pluginWithoutEnv =
            DeviceInfoPlugin(
                appVersionName = "1.0.0",
                appBuildNumber = "42",
                buildVariant = "release",
            )

        assertNull(pluginWithoutEnv.deviceInfo.environment)
    }

    // endregion

    // region — Platform fields

    @Test
    fun `deviceInfo device model is not blank`() {
        assertTrue(plugin.deviceInfo.deviceModel.isNotBlank())
    }

    @Test
    fun `deviceInfo manufacturer is not blank`() {
        assertTrue(plugin.deviceInfo.manufacturer.isNotBlank())
    }

    @Test
    fun `deviceInfo os version is not blank`() {
        assertTrue(plugin.deviceInfo.osVersion.isNotBlank())
    }

    @Test
    fun `deviceInfo screen resolution is not blank`() {
        assertTrue(plugin.deviceInfo.screenResolution.isNotBlank())
    }

    @Test
    fun `deviceInfo screen density is not blank`() {
        assertTrue(plugin.deviceInfo.screenDensity.isNotBlank())
    }

    @Test
    fun `deviceInfo locale is not blank`() {
        assertTrue(plugin.deviceInfo.locale.isNotBlank())
    }

    @Test
    fun `deviceInfo timezone is not blank`() {
        assertTrue(plugin.deviceInfo.timezone.isNotBlank())
    }

    // endregion
}
