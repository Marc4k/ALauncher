# ALC-003: Leewyn upstream base and feature scope

Status: Accepted  
Depends on: ALC-002 (the unmodified ALauncher baseline is tagged `alauncher-upstream-baseline`)

## Decision

Leewyn starts from ALauncher's **`appCustomUIProGoogle`** variant. The first reproducible development artifact is therefore built with:

```sh
gradle assembleAppCustomUIProGoogleDebug
```

This selects the only current app and recents/UI flavors (`app` and `customUI`), the `pro` feature flavor, and the `google` distribution flavor.

The `pro` flavor is the feature base because it exposes the complete launcher behavior without the free flavor's purchase screen, billing permission, upgrade prompts, and feature gates. The `google` flavor is the distribution base because Google Play is the primary Android distribution target and its dependency is available from Maven; the Amazon flavor adds an embedded Amazon IAP SDK and is not a Leewyn target. This is an upstream-base choice, not a decision to retain billing or other store integration in Leewyn.

No product flavors are to be deleted or collapsed until this exact upstream variant has been built and smoke-tested. Flavor cleanup is follow-up work.

## Feature disposition

| ALauncher feature | Leewyn requirement | Timing | Rationale / minimum behavior |
| --- | --- | --- | --- |
| Notification dots | Required | Keep | Show notification presence on supported Android versions and retain the settings/system-permission path. |
| Widgets | Required | Keep | Browse, add, move, resize, restore, and remove home-screen widgets. |
| Folders | Required | Keep | Create folders by dropping apps together; open, rename, reorder, and dissolve them. |
| Work profiles | Unwanted | Remove immediately after baseline verification | Leewyn will be integrated with a device-owner app that prevents work profiles, so profile switching, work badges, and work-profile launcher UI are unnecessary. Multi-user correctness outside the blocked work-profile path must be preserved. |
| Shortcuts | Required | Keep | Retain pinned, static, and dynamic app shortcuts and launcher long-press actions. |
| Search | Required | Keep, simplify later | Retain local app search and a usable all-apps search entry point. Provider selection and Google-specific web/voice affordances are not required. |
| Icon packs | Unwanted | Remove later | Not part of the Leewyn product. Remove after the base is stable because icon-pack selection is connected to icon caching, reloads, settings, and persistence. Stock/adaptive app icons remain required. |
| At a Glance | Unwanted | Remove immediately after baseline verification | It relies on Google/companion smartspace integration and is not part of Leewyn's core launcher experience. Remove its host, settings entry, companion assumptions, and related resources as the first feature subtraction. |

## Removal phases

“Immediate” means the first cleanup change after the untouched variant passes the baseline build/run gate; it does not mean modifying the upstream snapshot before that gate.

### Immediate removals

- At a Glance / Smartspace and its companion-app integration.
- Work-profile UI and work-profile-specific behavior. Coordinate this removal with the device-owner integration; retain shared multi-user primitives where launcher operation depends on them.
- Free-flavor purchase/upgrade UI and all Google/Amazon billing or store-specific code once flavors are consolidated around the chosen base.
- Google feed/overlay integration and Google-only web/voice search affordances. Local app search stays.
- Amazon, free, and other unused distribution/feature combinations, but only after the selected variant's behavior is captured by the baseline gate below.

### Later removals

- Icon-pack discovery, selection, persistence, and icon-cache integration.
- Nonessential search-provider customization after local app search has dedicated coverage.
- Other cosmetic ALauncher customization only when Leewyn's product design replaces it; it is outside this decision.

## Baseline build and run gate

Before flavor cleanup:

1. Run `gradle assembleAppCustomUIProGoogleDebug` from the repository root.
2. Confirm the APK is produced at `build/outputs/apk/appCustomUIProGoogle/debug/`.
3. Install it on an emulator or device with `adb install -r <apk>`.
4. Start the HOME intent with `adb shell am start -a android.intent.action.MAIN -c android.intent.category.HOME` and select ALauncher if prompted.
5. Confirm the launcher reaches the workspace without crashing; open All Apps and perform a local app search.

The build verification can run in CI. The install/launch smoke test requires an attached Android target and must be recorded separately when one is available.

## Verification record

- 2026-08-18: `assembleAppCustomUIProGoogleDebug` passed on Java 17 with Gradle 8.13 (`BUILD SUCCESSFUL`, 64 tasks).
- 2026-08-18: generated `build/outputs/apk/appCustomUIProGoogle/debug/ALauncher-app-customUI-pro-google-debug.apk` (8.1 MB).
- 2026-08-18: `Pixel_8a_API_35` booted, but its existing ADB profile reported `unauthorized`; install/launch remains pending. Flavor cleanup must not begin until steps 3–5 above pass on an authorized target.
