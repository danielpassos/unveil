# unveil-deviceinfo

Device Info plugin for [Unveil](../README.md).

Displays a snapshot of app and device information captured at plugin construction time,
grouped into four sections: **App**, **Device**, **Display**, and **Locale**.

## What Is Captured

| Section | Field          | Source                                      |
|---------|----------------|---------------------------------------------|
| App     | Version        | Provided by host app                        |
| App     | Build          | Provided by host app                        |
| App     | Variant        | Provided by host app                        |
| App     | Environment    | Provided by host app (optional)             |
| Device  | Model          | `Build.MODEL` / `UIDevice.model`            |
| Device  | Manufacturer   | `Build.MANUFACTURER` / `"Apple"`            |
| Device  | OS Version     | `Build.VERSION.RELEASE` / `systemVersion`   |
| Display | Resolution     | `DisplayMetrics` / `UIScreen.nativeBounds`  |
| Display | Density        | `DisplayMetrics.densityDpi` / `nativeScale` |
| Locale  | Locale         | `Locale.getDefault()` / `NSLocale.current`  |
| Locale  | Timezone       | `TimeZone.getDefault()` / `NSTimeZone.local`|

## Installation

```kotlin
// build.gradle.kts
implementation("me.passos.libs:unveil-deviceinfo:<version>")
```

## Usage

```kotlin
Unveil.configure {
    register(
        DeviceInfoPlugin(
            appVersionName = BuildConfig.VERSION_NAME,
            appBuildNumber = BuildConfig.VERSION_CODE.toString(),
            buildVariant = BuildConfig.BUILD_TYPE,
            environment = "staging" // optional
        )
    )
}
```

App-specific fields (`appVersionName`, `appBuildNumber`, `buildVariant`, `environment`)
must be provided by the host app. The library has no access to `BuildConfig` or bundle
metadata. Platform fields (device, display, locale) are collected automatically via
`expect/actual` implementations for Android and iOS.
