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

Customization — application id, name, marker, colors, icons and tabs — is
driven by `config/app_config.json` and applied by a separate `./gradlew
parseConfig` step, which also requires your own Firebase configuration.

We are reworking that part of the setup, and the guide will be published here.
Until then, please write to support@travelpayouts.com before customizing the
template — the steps have to be done in a specific order and we would rather
walk you through it than let you hit a wall.

## Support

Questions? Write to support@travelpayouts.com.
