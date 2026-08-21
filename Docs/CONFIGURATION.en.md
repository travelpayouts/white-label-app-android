# Configuring your White Label app (Android)

Everything you customise lives in the `config/` directory. You edit it, run one
Gradle task, and build.

```
config/
  app_config.json          all settings — ids, texts, colours, tabs
  google-services.json     your Firebase file (required)
  icons/                   launcher icon, tab icons
  images/                  background image of the search screen
  strings/                 your own wording for the SDK's texts
```

The order matters: **fill in the config → put your Firebase file in place →
run `./gradlew parseConfig` → build.** `parseConfig` generates the application
id, names, colours, icons and tabs from your config. Editing `app_config.json`
without running it changes nothing in the built app.

## How to read this guide

Every field below is described from the code that reads it. Where a key exists
in the template but has no effect, that is stated — see
[Keys with no effect](#keys-with-no-effect). Nothing here is left for you to
guess.

Values that can differ per language use this shape — `base` names the fallback
language, `localized` holds the translations:

```json
{
  "base": "en",
  "localized": { "en": "My App", "ru": "Моё приложение" }
}
```

## `base_configuration`

| Field | Required | Purpose |
|---|---|---|
| `identifier.android.id` | yes | Your application id, e.g. `com.mycompany.travel`. `parseConfig` refuses to run if it is empty, has spaces or is not a valid Java package name |
| `identifier.android.versionName` | yes | Version name shown in the store, e.g. `1.0.0` |
| `identifier.android.versionCode` | yes | Version code, a positive integer |
| `display_name` | yes | App name under the launcher icon (localized) |
| `booking_review_request_frequency` | no | How often to ask the user to rate the app; `0` turns the request off |

## `constants`

| Field | Purpose |
|---|---|
| `marker` | Your Travelpayouts marker — commission is credited to it. `parseConfig` warns if empty |
| `api_key` | Your Travelpayouts API key. Without it flight search returns an error. `parseConfig` warns if empty |
| `client_device_host` | Identifies your app in the SDK's requests; sent in the `Referrer` header. `parseConfig` warns if empty |
| `policy_url` | Privacy policy URL (localized) |
| `feedback_email` | Address for "Contact us" on the Info screen. Leave empty and the item disappears |
| `feedback_theme` | Subject line of that email. Empty means `App Feedback` |
| `app_store_link` | Link used by "Share app". Empty means the Google Play address built from your application id |
| `sharing_data.sharing_link` | Base address of shared ticket links. The SDK appends `/?flightSearch=...` to it, so give a full address with the scheme (`https://example.com`), not a bare host |
| `sharing_data.handling_link` | Host the app intercepts links for. `parseConfig` writes it without `https://` into `handling_link.properties`, and from there it becomes the `android:host` of the deep link filter in the manifest. Leave it empty and link interception will not work. Running `parseConfig` after editing it is required |
| `appsflyer_dev_key` | AppsFlyer key. Leave empty if you do not use AppsFlyer |

## `advertising`

The whole block is optional — with an empty `appodeal_api_key` no ads are
initialised and the app builds and runs without them.

**After editing this block, running `./gradlew parseConfig` is required.** The
task writes the resulting mode into `advertising.properties`, and that is what
decides which ad libraries end up in the build. Edit the config without running
the task and the build now stops and says so — it used to build quietly without
ads instead.

**`parseConfig` cannot share a command with a build.** The mode is chosen before
the task gets a chance to write it, so such a command would use the previous
value. The build stops if you try. If your CI ran something like
`./gradlew parseConfig assembleRelease`, split it into two commands.

To confirm the ads are actually wired in, check the dependency graph rather than
the fact that the task finished without errors:

```bash
./gradlew verifyAdvertisingWiring -PadsMode=appodeal_admob
```

Modes: `none`, `appodeal`, `appodeal_admob`.

| Field | Purpose |
|---|---|
| `appodeal_api_key` | Appodeal key. Leave it empty and no ad network adapters enter the build. The Appodeal core itself always ships with the SDK, but without a key nothing is initialised |
| `google_admob_app_id` | AdMob application id. It only works together with a filled `appodeal_api_key`: without one AdMob is not wired up, because all ads go through Appodeal |
| `placements.air_ticket_placement_interstitial` | Interstitial placement for the flight search |
| `placements.air_ticket_placement_banner` | Banner placement for the flight search |

## `style`

| Field | Values | Purpose |
|---|---|---|
| `base_color` | hex, e.g. `#d9332e` | Your accent colour. Everything else is derived from it |
| `corners_type` | `sharp`, `default`, `round` | Corner style of buttons and cards |
| `icons_type` | `filled`, `line`, `tint` | Icon style inside the SDK screens |
| `palette` | `hsv` or anything else | How the derived shades are computed. `hsv` uses the HSV model, any other value (the template says `lab`) uses Lab, which keeps perceived lightness more even |
| `overridden_color` | object | Pins individual shades instead of deriving them |

Derived shades you can pin in `overridden_color`:

```json
"overridden_color": {
  "ta_primary_light": "#d9332e",
  "ta_primary_dark": "#ff6b6b",
  "ta_primary_disable_light": "#f0a0a0",
  "ta_primary_disable_dark": "#8a4444",
  "ta_primary_pressed_light": "#b02a26",
  "ta_primary_pressed_dark": "#cc5555",
  "ta_surface_tint_light": "#fdf0f0",
  "ta_surface_tint_dark": "#2a1a1a"
}
```

A key you leave out is derived from `base_color`; a key you set is used as is.
Keys ending in `_light` are for the light theme, `_dark` for the dark one.

## `info_screen_config`

`items_to_display` — which optional items appear on the Info screen. Any of:
`about_app`, `rate_app`, `share_app`, `favorites`.

**`favorites` needs the flights tab.** The app enables favourites only when the
tab list contains `flights` *and* `favorites` is in this list. With a tab list
without `flights`, the item stays hidden however you set it.

Other items of that screen — regional settings, price display, "Contact us"
(when `feedback_email` is filled) and the privacy link — are always shown and
are not controlled here.

`about_app_info` holds the texts of the About screen: `description` and
`partner_url`, both localized. The third key, `developer`, has no effect —
see below.

## `white_label_config.screens_to_display`

The tabs of the bottom bar, in the order you list them. The Info tab is added
automatically at the end, you do not list it.

Two types are recognised:

- `flights` — the flight search;
- `other` — a tab that opens a web page.

```json
"white_label_config": {
  "screens_to_display": [
    { "type": "flights" },
    {
      "type": "other",
      "parameters": {
        "title": { "base": "en", "localized": { "en": "Deals", "ru": "Акции" } },
        "url":   { "base": "en", "localized": { "en": "https://example.com" } }
      }
    }
  ]
}
```

**At most two `other` tabs.** For each one the build generates references to
`ta_other<N>` — a screen id, a navigation graph, a title, a URL and an icon —
and the SDK ships that set for exactly two: `ta_other1` and `ta_other2`. A third
tab therefore points at resources that do not exist and the build fails to
compile.

**Their icons come from files, not from the config.** Put your own
`config/icons/ic_other_1.xml` and `ic_other_2.xml` — the first file belongs to
the first `other` tab in the list, the second to the second. Without a file the
tab uses the SDK's default icon. Vector drawables (`.xml`) only.

A `type` the app does not know — `hotels`, for example, left over from when the
SDK had hotel search — is skipped without an error. It will not appear as a tab.

## Icons, background and texts

**Launcher icon** — `config/icons/ic_launcher_foreground.xml` and
`ic_launcher_background.xml`. Both files must be present: with only one of them
the step is skipped and the template's icon stays.

**Background of the search screen** — put one image into `config/images/`. A
vector `.xml` wins if present; otherwise a `.png` or `.jpg` is used and scaled
to 1440×3216. If you do not need your own background, `config/images/` must not
exist at all: the step is then skipped and the SDK's background stays. A
directory created empty, or holding a file of another format, makes
`parseConfig` fail - a known defect of the template, not a mistake on your
side.

**Texts** — `config/strings/<lang>/strings.xml` overrides the SDK's wording.
The directory named `base` becomes the default `values/strings.xml`, any other
name becomes `values-<name>/`. Look up the keys in the SDK resources: they start
with `ta_`, except `app_not_found`. The directory is optional.

**Firebase** — `config/google-services.json`, your own file from the Firebase
Console. It must contain a client for your application id *and* one for the
debug variant (`<your id>.debug`), otherwise the build fails later with
"No matching client found for package name". `parseConfig` checks this upfront
and names the missing client.

## Keys with no effect

These keys exist in the template or in the model, and changing them changes
nothing. They are listed so you do not spend time on them.

| Key | What actually happens |
|---|---|
| `constants.appstore_id` | Not part of the configuration model at all — silently dropped when the file is read. The store link is `app_store_link` |
| `info_screen_config.about_app_info.developer` | Part of the model, read by nothing. The About screen does not show it |
| `screens_to_display[].parameters.id` | Ignored. Tab ids are assigned by position: the first `other` tab becomes `other1`, the second `other2` |
| `screens_to_display[].parameters.icon` | Ignored. The icon comes from `config/icons/ic_other_N.xml`, see above |
| `identifier.apple.*` | The iOS half of a shared configuration. The Android build does not read it |
| `advertising.placements.hotels_*` | Left from the removed hotel search. Dropped when the file is read |

## Building

### Requirements

- Android Studio with Android SDK Platform 36 installed
- JDK 17
- The app runs on Android 8.0 (API 26) and newer

The project is built against Android 16 (API 36) — the bundled `travel-sdk`
requires it.

### Signing key

Before the first build create `signing.properties` in the project root, or the
build stops with a `FileNotFoundException` on that file. Use
`signing.properties.example` as the template:

```
KEYSTORE_FILE_DEBUG=debug.keystore
KEYSTORE_FILE_PASSWORD_DEBUG=<keystore password>
KEY_ALIAS_DEBUG=<key alias>
KEY_PASSWORD_DEBUG=<key password>

KEYSTORE_FILE_RELEASE=release.keystore
KEYSTORE_FILE_PASSWORD_RELEASE=<keystore password>
KEY_ALIAS_RELEASE=<key alias>
KEY_PASSWORD_RELEASE=<key password>
```

Keystore paths are relative to the project root. Neither `signing.properties`
nor the keystore files are committed — `.gitignore` covers `signing.properties`,
`*.keystore` and `*.jks`. It cannot cover a keystore that is already tracked, or
other extensions such as `.p12` and `.bks`; check your copy with
`git ls-files | grep -i -E 'keystore|signing|\.(jks|p12|bks)$'`.

If you have no keystore yet:

```bash
keytool -genkeypair -v -keystore release.keystore -alias <alias> \
  -keyalg RSA -keysize 2048 -validity 10000
```

### Commands

```bash
./gradlew parseConfig                 # apply the configuration — run after every edit
./gradlew :app:assembleBasicDebug     # debug build
./gradlew :app:assembleBasicRelease   # release build
```

The APK is written to `app/build/outputs/apk/basic/<build type>/`.

From Android Studio: run `parseConfig` once from the Gradle panel, then build
with the `basicDebug` or `basicRelease` variant selected.

Questions — support@travelpayouts.com.
