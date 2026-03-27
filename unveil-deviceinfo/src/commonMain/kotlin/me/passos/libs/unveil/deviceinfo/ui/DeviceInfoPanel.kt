package me.passos.libs.unveil.deviceinfo.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.passos.libs.unveil.deviceinfo.DeviceInfo
import me.passos.libs.unveil.deviceinfo.resources.Res
import me.passos.libs.unveil.deviceinfo.resources.deviceinfo_label_build
import me.passos.libs.unveil.deviceinfo.resources.deviceinfo_label_density
import me.passos.libs.unveil.deviceinfo.resources.deviceinfo_label_environment
import me.passos.libs.unveil.deviceinfo.resources.deviceinfo_label_locale
import me.passos.libs.unveil.deviceinfo.resources.deviceinfo_label_manufacturer
import me.passos.libs.unveil.deviceinfo.resources.deviceinfo_label_model
import me.passos.libs.unveil.deviceinfo.resources.deviceinfo_label_os_version
import me.passos.libs.unveil.deviceinfo.resources.deviceinfo_label_resolution
import me.passos.libs.unveil.deviceinfo.resources.deviceinfo_label_timezone
import me.passos.libs.unveil.deviceinfo.resources.deviceinfo_label_variant
import me.passos.libs.unveil.deviceinfo.resources.deviceinfo_label_version
import me.passos.libs.unveil.deviceinfo.resources.deviceinfo_section_app
import me.passos.libs.unveil.deviceinfo.resources.deviceinfo_section_device
import me.passos.libs.unveil.deviceinfo.resources.deviceinfo_section_display
import me.passos.libs.unveil.deviceinfo.resources.deviceinfo_section_locale
import me.passos.libs.unveil.ui.components.UnveilSectionHeader
import me.passos.libs.unveil.ui.components.UnveilValueRow
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DeviceInfoPanel(deviceInfo: DeviceInfo) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
    ) {
        UnveilSectionHeader(title = stringResource(Res.string.deviceinfo_section_app))
        UnveilValueRow(label = stringResource(Res.string.deviceinfo_label_version), value = deviceInfo.appVersionName)
        UnveilValueRow(label = stringResource(Res.string.deviceinfo_label_build), value = deviceInfo.appBuildNumber)
        UnveilValueRow(label = stringResource(Res.string.deviceinfo_label_variant), value = deviceInfo.buildVariant)
        if (deviceInfo.environment != null) {
            UnveilValueRow(
                label = stringResource(Res.string.deviceinfo_label_environment),
                value = deviceInfo.environment
            )
        }

        UnveilSectionHeader(title = stringResource(Res.string.deviceinfo_section_device))
        UnveilValueRow(label = stringResource(Res.string.deviceinfo_label_model), value = deviceInfo.deviceModel)
        UnveilValueRow(
            label = stringResource(Res.string.deviceinfo_label_manufacturer),
            value = deviceInfo.manufacturer
        )
        UnveilValueRow(label = stringResource(Res.string.deviceinfo_label_os_version), value = deviceInfo.osVersion)

        UnveilSectionHeader(title = stringResource(Res.string.deviceinfo_section_display))
        UnveilValueRow(
            label = stringResource(Res.string.deviceinfo_label_resolution),
            value = deviceInfo.screenResolution
        )
        UnveilValueRow(label = stringResource(Res.string.deviceinfo_label_density), value = deviceInfo.screenDensity)

        UnveilSectionHeader(title = stringResource(Res.string.deviceinfo_section_locale))
        UnveilValueRow(label = stringResource(Res.string.deviceinfo_label_locale), value = deviceInfo.locale)
        UnveilValueRow(label = stringResource(Res.string.deviceinfo_label_timezone), value = deviceInfo.timezone)
    }
}
