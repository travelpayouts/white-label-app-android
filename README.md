# Travelpayouts White Label App for Android

A ready-to-build Android travel app (flight search) that you make your own —
name, icon, colors, tabs — and publish under your own account.

This app is part of the
[Travelpayouts White Label program](https://support.travelpayouts.com/hc/en-us/categories/17120350065042-White-Label-App).
Learn more: [What is Travelpayouts?](https://support.travelpayouts.com/hc/en-us/articles/203955593-What-is-Travelpayouts-and-how-does-it-work)

## Requirements

- Android Studio with **Android SDK Platform 36** installed
- JDK 17

The project compiles against and targets Android 16 (API 36) — the bundled
`travel-sdk` requires the former, and Google Play requires the latter for new
apps and updates from 31 August 2026. The app still installs on Android 8.0
(API 26) and newer.

## Signing

Before the first build, create `signing.properties` in the project root. Copy
`signing.properties.example` and fill in your own values — it lists the
required keys and the `keytool` commands that create the keystores.

Without this file the build stops with
`java.io.FileNotFoundException: signing.properties`.

Neither `signing.properties` nor the keystore files are committed to git.

## Building

```bash
./gradlew :app:assembleBasicDebug     # debug build
./gradlew :app:assembleBasicRelease   # release build
```

The APK lands in `app/build/outputs/apk/basic/<build type>/`.

You can also build from Android Studio with the `basicDebug` or `basicRelease`
variant selected.

## Configuring your own app

Everything you customize — application id, app name, marker, colors, icons,
tabs — lives in `config/app_config.json` and is applied by a separate step.
The order matters:

**1. Fill in `config/app_config.json`.** At minimum:

```jsonc
"base_configuration": {
  "identifier": {
    "android": {
      "id": "com.mycompany.travel",   // your application id
      "versionName": "1.0.0",
      "versionCode": 1
    }
  }
},
"constants": {
  "marker": "123456",              // your Travelpayouts marker — commissions are credited to it
  "api_key": "...",                // your Travelpayouts API key — flight search needs it
  "client_device_host": "...",     // identifies your app in the SDK's requests
  "google_maps_api_key": "..."     // without it map screens render blank
}
```

`parseConfig` refuses to run without the first three and warns about the rest,
so you find out before building, not after.

**2. Replace `config/google-services.json`** with your own file from the
Firebase Console. It must contain a client for your application id *and* one
for the debug variant (`<your id>.debug`), otherwise debug builds fail with
"No matching client found for package name".

**3. Apply the configuration:**

```bash
./gradlew parseConfig
```

This regenerates the app id, names, colors, icons and tabs, and copies your
Firebase file into every build variant. If something required is missing, the
task stops and says what to fill in.

**4. Build** as described above.

Optional, alongside `app_config.json`: `config/icons/` (launcher and tab
icons), `config/images/` (background), `config/strings/<lang>/strings.xml`
(your own wording).

Advertising is off by default: `advertising.appodeal_api_key` and
`advertising.google_admob_app_id` are empty, and the app builds and runs
without them. Fill them in only if you monetize with ads.

Questions about configuration — support@travelpayouts.com.

## Support

Questions? Write to support@travelpayouts.com.
