# Runtime dependency audit

Audit date: 2026-08-19

Command:

```sh
./gradlew dependencies --configuration debugRuntimeClasspath
```

Direct runtime dependencies:

- `com.android.tools:desugar_jdk_libs:2.1.5`
- `androidx.dynamicanimation:dynamicanimation:1.1.0`
- `androidx.recyclerview:recyclerview:1.4.0`
- `androidx.preference:preference:1.2.1`
- `androidx.annotation:annotation:1.9.1`
- local `:IconLoader` module
- local `prebuilts/libs/launcher_protos.jar`
- local `prebuilts/libs/plugin_core.jar`

Result: the resolved `debugRuntimeClasspath` contains no Google Play Billing, Amazon IAP,
Amazon Purchasing, or other store SDK. CI rejects dependencies matching these prohibited
coordinates or names:

- `com.android.billing`
- `billingclient`
- Amazon IAP / in-app purchasing artifacts
- Amazon purchasing artifacts

This report must be regenerated and reviewed whenever runtime dependencies change.
