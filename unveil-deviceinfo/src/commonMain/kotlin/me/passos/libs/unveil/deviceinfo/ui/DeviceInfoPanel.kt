package me.passos.libs.unveil.deviceinfo.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.passos.libs.unveil.deviceinfo.DeviceInfo
import me.passos.libs.unveil.ui.components.UnveilSectionHeader
import me.passos.libs.unveil.ui.components.UnveilValueRow

@Composable
internal fun DeviceInfoPanel(deviceInfo: DeviceInfo) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
    ) {
        UnveilSectionHeader(title = "App")
        UnveilValueRow(label = "Version", value = deviceInfo.appVersionName)
        UnveilValueRow(label = "Build", value = deviceInfo.appBuildNumber)
        UnveilValueRow(label = "Variant", value = deviceInfo.buildVariant)
        if (deviceInfo.environment != null) {
            UnveilValueRow(label = "Environment", value = deviceInfo.environment)
        }

        UnveilSectionHeader(title = "Device")
        UnveilValueRow(label = "Model", value = deviceInfo.deviceModel)
        UnveilValueRow(label = "Manufacturer", value = deviceInfo.manufacturer)
        UnveilValueRow(label = "OS Version", value = deviceInfo.osVersion)

        UnveilSectionHeader(title = "Display")
        UnveilValueRow(label = "Resolution", value = deviceInfo.screenResolution)
        UnveilValueRow(label = "Density", value = deviceInfo.screenDensity)

        UnveilSectionHeader(title = "Locale")
        UnveilValueRow(label = "Locale", value = deviceInfo.locale)
        UnveilValueRow(label = "Timezone", value = deviceInfo.timezone)
    }
}
