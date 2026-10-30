# RX • Compose Preview

An independent black/white Android app showing a visual concept for Rensize X.

- Kotlin / Jetpack Compose, 4 clickable tabs
- `preview.html` is an optional browser mockup with clickable tabs (not actual Android rendering)
- Smooth screen transitions and animated toggles
- No permissions, account, login, telemetry, or network connections
- UI-only demo; launch buttons show an explanation
- Different package name from the real RX: `com.rensize.rxpreview`

## Building on AndroidIDE (on the phone)

1. Install [AndroidIDE](https://github.com/AndroidIDEOfficial/AndroidIDE/releases) from its official releases and install its Android build tools following the AndroidIDE docs.
2. Extract this archive to a regular folder and open its root directory (`settings.gradle.kts`) in AndroidIDE. 
3. Allow Gradle sync and SDK packages to download; this needs Wi-Fi and free storage.
4. Tap **Run / Quick run**; this triggers `assembleDebug`, then offers install.
5. If AndroidIDE can't resolve the Gradle wrapper (this ZIP does not ship binary wrapper components), use the built-in Terminal with its installed Gradle, or create a new AndroidIDE Kotlin project and copy the `app/src/main/java/.../MainActivity.kt` screen into it with Compose dependencies. Alternatively use GitHub Actions below.

## Building with GitHub Actions (on a phone, in the cloud)

1. Create an empty GitHub repository (private is fine).
2. Upload **the extracted files and directories**, keeping `.github/workflows/build-apk.yml` at the correct location. Browser uploads may not preserve folders; a Git client or AndroidIDE Git is recommended for pushing the folder tree.
3. Open the repository's **Actions** tab, select **Build RX Preview APK**, and run **Run workflow** (or push to main).
4. After success open the run and download **RX-Preview-debug-apk** in Artifacts. Extract that ZIP to obtain `app-debug.apk` and tap the APK to install.

You can also use Android Studio with JDK 17 and Gradle 8.11.1 on a PC.

## Versions

AGP 8.9.2 · Gradle 8.11.1 · Kotlin 2.0.21 + Compose compiler plugin · compileSdk 35 · Compose BOM 2025.04.01

NOTE: This is source code, not a precompiled APK. It has not been compiled in this environment. 
