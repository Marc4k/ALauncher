# Leewyn Launcher

Android launcher foundation for the Leewyn application.

The retained launcher functionality includes the workspace, app drawer, folders, widgets,
drag-and-drop, pinned and deep shortcuts, notification dots, local app search, package update
handling, and workspace backup and restoration.

## Supported build environment

- JDK 17
- Android SDK 36
- macOS (Apple Silicon or Intel) and Linux x86_64
- the checked-in Gradle 8.11.1 wrapper

No system Gradle installation or protobuf compiler is required.

## Build paths

There are no product flavors. The only application build paths are:

```sh
./gradlew assembleDebug
./gradlew assembleRelease
```

The APKs are generated under `build/outputs/apk/debug/` and
`build/outputs/apk/release/`. Release signing is configured through the untracked
`keystore.properties` file; without it, Gradle still produces an unsigned release artifact for
CI verification.

## Generated sources and prebuilts

The launcher uses the legacy protobuf Nano API. The canonical schemas remain in `protos/` and
`proto_overrides/`, while their generated Java output is checked into `generated_src/`. This is
intentional: the original protobuf 3.0 `javanano` generator has no native Apple Silicon binary,
and newer protoc releases removed that generator. Normal builds therefore do not run protoc.

The files under `prebuilts/libs/` have the following required purposes:

- `framework.jar` supplies hidden Android framework APIs at Java compile time through the
  configured boot classpath. It is not packaged into the APK.
- `launcher_protos.jar` supplies the protobuf Nano runtime used by the checked-in generated
  launcher event and workspace-dump messages.
- `plugin_core.jar` supplies the System UI plugin interfaces referenced by retained launcher
  code.

There are no remaining smali, dex2jar, billing, purchasing, or store-specific build
transformations.

## Dependency audit

Generate the resolved runtime dependency tree with:

```sh
./gradlew dependencies --configuration debugRuntimeClasspath
```

The maintained audit and prohibited dependency patterns are documented in
[`docs/dependency-report.md`](docs/dependency-report.md).
