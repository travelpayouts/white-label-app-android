# Travelpayouts White Label App for Android

> **Template version:** 2026.09.10 · **Bundled SDK:** 1.7.2
>
> This is the version of the template itself, not of your app — your app's
> version is what you set in `config/app_config.json`. If the header in your
> copy shows an older date than the one here on GitHub, the changes listed
> below are what you are missing. Each handover is also tagged, so you can
> diff against a known state: `template-2026.09.10`.

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
  "client_device_host": "..."      // identifies your app in the SDK's requests
}
```

`parseConfig` refuses to run without the application id and the version, and
warns when the three values above are empty — so you find out before building,
not after.

**2. Replace `config/google-services.json`** with your own file from the
Firebase Console. It must contain a client for your application id *and* one
for the debug variant (`<your id>.debug`), otherwise debug builds fail with
"No matching client found for package name".

The file in this repository is a placeholder — `replace-with-your-firebase-project`,
package `com.example.travel`, zeroed ids. It is deliberately not a working
configuration: a template that shipped somebody else's Firebase project would
either send your app's data to that project or fail on you later with a less
obvious error. Same for `app/src/*/google-services.json`, which `parseConfig`
overwrites from your file anyway.

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

**Every field, in detail** — including the keys that look configurable but have
no effect, and the limit of two `other` tabs:
[Configuration guide (English)](Docs/CONFIGURATION.en.md) ·
[Инструкция по настройке (на русском)](Docs/CONFIGURATION.ru.md).

Advertising is off by default: `advertising.appodeal_api_key` and
`advertising.google_admob_app_id` are empty, and the app builds and runs
without them. Fill them in only if you monetize with ads.

## What changed

### 2026.09.10 (bundled SDK 1.7.2)

**A full configuration guide.** [Docs/CONFIGURATION.en.md](Docs/CONFIGURATION.en.md)
and [Docs/CONFIGURATION.ru.md](Docs/CONFIGURATION.ru.md) describe every field of
`app_config.json` as the code actually reads it, plus the icon, image and string
directories next to it. There is a section for keys that look configurable but
do nothing — `appstore_id`, `about_app_info.developer`, `parameters.id` and
`parameters.icon` of an `other` tab — so you don't spend an evening on them.

Two limits are now written down rather than discovered: **an app can have at
most two `other` tabs** (the SDK ships the `ta_other<N>` resources for exactly
two, so a third one does not compile), and **`favorites` on the Info screen
needs the `flights` tab** — without it the item stays hidden however you
configure it.

**The config template no longer carries dead keys.** Removed from
`config/app_config.json`: `constants.appstore_id`, the `identifier.apple` block,
the hotel ad placements, `about_app_info.developer`, the `hotels` tab and the
`id` parameter of `other` tabs. None of them were read; they only suggested
settings that do not exist. If your own config still has them, nothing breaks —
they are ignored exactly as before.

**The bundled Firebase files are placeholders.** `config/google-services.json`
and the per-variant copies used to carry our own Firebase projects. They are now
stubs with obvious values, so no Firebase project of ours is configured in what
you build. Note that this replaces the files, not the history: a git repository
carries its past, and earlier commits still contain the previous ones. There is no
chance of a build quietly reporting into a project you do not own. You replace
them with your own file exactly as before — step 2 above.

**Turning ads on used to be impossible to notice; now it also has to compile.**
With the wiring fixed, enabling Appodeal actually pulls the ad adapters in - and
they raise `androidx.activity` from 1.8.1 to 1.9.2, where
`ComponentActivity.onNewIntent` is annotated `@NonNull`. The template's own
`StartActivity` declared the parameter nullable, so the first partner to switch
ads on would have hit a compile error in code we shipped. The signature is
non-null now, which compiles against both versions. If you have your own
`Activity` overriding `onNewIntent`, check its parameter too.

**`parseConfig` can no longer share a command with a build.** If your CI ran
`./gradlew parseConfig assembleRelease` in one line, split it in two - the
combined form now stops with an explanation. It had been quietly wrong all
along: the task writes `app_version.properties` and `handling_link.properties`
while it executes, and the build reads both while Gradle configures the project,
which happens first - so the build in that same command always used the previous
values.

**Advertising is wired up by data, not by text substitution.** `parseConfig`
used to configure ads by finding a line in `app/build.gradle.kts` and replacing
it. When the SDK dependencies moved into the `travel-sdk` module that line went
away, the replacement quietly matched nothing, and filling in your Appodeal key
stopped adding the ad adapters to the build - no error, no warning, no ads. The
dependency list now lives in `app/build.gradle.kts` under a `when`, and the
branch is chosen from the `advertising` block of `config/app_config.json` itself.
There is nothing to search for, so nothing to break. `parseConfig` still
generates the advertising resources from that block and records its fingerprint
in `advertising.properties`, which is how the build knows those resources went
stale.

Two consequences for you. After editing the `advertising` block, running
`./gradlew parseConfig` is required - the build stops and says so if you forget,
instead of quietly building with stale advertising resources. And you can check
the result rather than trust it: `./gradlew verifyAdvertisingWiring` resolves the
dependency graph of a release build and fails if the adapters are not in it.
Fill in both `placements` while you are there - with an empty placement the
adapters arrive and no ad is ever requested, and every check stays green.

Appodeal adapter versions are now paired with the core the SDK ships (3.12.0).
They had drifted to 3.7.0.0, which would not have registered the ad networks
even once the wiring was fixed.

**Release builds now shrink the SDK.** This template used to carry a
hand-written `-keep class com.travelapp.** { *; }` and a set of companion rules.
They are gone: the SDK ships its own R8 rules inside the AAR, so repeating them
here only widened what R8 had to keep. Read this before you update if your code
touches the SDK by reflection, or through anything that resolves classes by
name, such as a serialization library working off class names: those paths were
covered by the blanket keep and are not covered now. Add your own keep rules for
them in `app/proguard-rules.pro`, and test a release build, not just a debug one
- the difference only shows after shrinking.

**The bundled `travel-sdk-release.aar` is rebuilt.** Beside the R8 rules above,
it brings the SDK changes made since the previous handover: the `Referrer` header
now carries the application prefix taken from the `wlsdk` resource, navigation
bar icons stay light in the dark theme on phones, the paths that end with an
empty search screen file a non-fatal report so an empty result can be told apart
from a failed request in Crashlytics, and the dead Google Maps metadata is gone
from the archive - the empty `google_maps_api_key` resource and the
`com.google.android.geo.API_KEY` manifest entry. The SDK version is unchanged,
1.7.2.

**No more Google Maps key.** The configuration no longer asks for
`google_maps_api_key`, and the README no longer tells you to fill it in. The
maps dependency went out with the hotel screens that used it, so obtaining a key
from Google is no longer part of setting up this template.

### 2026.08.16 (bundled SDK 1.7.2)

**Google Play deadline.** From 31 August 2026 Google Play requires API 36 for
new apps and for any update you submit. An app already published against API 35
remains available, including to new users — but you cannot submit an update
after that date until it targets API 36. You can request an extension to
1 November 2026.

The template now targets API 36, so take this update before your next release
rather than in a rush afterwards. When an API 36 build runs on Android 16 or
newer, two behavior changes are worth testing:

- On displays 600dp and wider — tablets and unfolded foldables — the portrait
  lock is ignored, so the app can rotate and fills the screen. Check your tabs,
  background image and any screens you added in landscape, and check that
  rotation does not lose state.
- `onBackPressed()` and `KEYCODE_BACK` are no longer delivered. The SDK's own
  screens use `OnBackPressedDispatcher` and are unaffected. If you added an
  Activity that overrides `onBackPressed()`, or any code handling
  `KEYCODE_BACK` — including fragment back handling routed through the host
  activity — migrate it to `OnBackPressedDispatcher`, or back navigation will
  silently stop working there.

**The template builds again.** After SDK 1.7.0 was bundled, the build stopped
with a compatibility error.

**An app built from the untouched template starts.** It used to close
immediately on launch because advertising was wired up while the ad keys in the
config are empty. Advertising is off by default now; to enable it, fill in
`advertising.*` in the config **and run `./gradlew parseConfig`** — the keys
alone change nothing.

**Configuring your own app works and is documented** — fill in the config,
replace the Firebase file, run `parseConfig`, build. The task now stops with a
readable message when the application id, the app version or a matching Firebase
client is missing, and warns when `marker`, `api_key` or `client_device_host` are empty. Before, a missing application id produced an
empty one and the build failed later on google-services with an opaque error.

**Signing.** Added `signing.properties.example`, and `.gitignore` now covers
`signing.properties`, `*.keystore` and `*.jks`. It cannot cover every case: a
keystore you already committed stays tracked, and other extensions (`.p12`,
`.bks`) are not matched. Run `git ls-files | grep -i -E 'keystore|signing|\.(jks|p12|bks)$'` in your copy
to check.

**Bundled SDK.** The `.aar` in this template is 1.7.2. If your copy predates
2026.08.16, it may carry 1.7.0, which means you also get two SDK fixes with this
update: the device identifier in the `Client-Device-Info` header is now an md5
hash rather than the raw value (1.7.1), and affiliate links from a specific
departure airport are built correctly (1.7.2). For the full release history ask
Travelpayouts support.

### Earlier

No version header means your copy predates 2026.08.16 — before that the template
carried no version at all. To see what you are missing, compare your copy with
the tag `template-2026.08.16` in this repository.

## Support

Questions about configuration, the build or this update —
support@travelpayouts.com.
