# Call of Duty

An application for duty arrangement.

Current supporting platform: Android, iOS.

## Before build the app...

You need set the base url in `.\multiplatform-app\composeApp\src\commonMain\kotlin\io\mocha\duty\data\net\WebReq.kt` like `http://domain.com:3000/` **(MIND THE TRAILING SLASH!!!)** and update the network policy file in `.\multiplatform-app\composeApp\src\androidMain\res\xml\network.xml` like `domain.com`.

---

> iOS build is not working by default. To use it, uncomment the code in `composeApp\build.gradle.kts`

```Kotlin
//    listOf(
//        iosX64(),
//        iosArm64(),
//        iosSimulatorArm64()
//    ).forEach { iosTarget ->
//        iosTarget.binaries.framework {
//            baseName = "ComposeApp"
//            isStatic = true
//        }
//    }
```

## Dirs
* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform, 
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.