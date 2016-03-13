# Altituden

An Android app that uses your phone's position together with accurate altitude data to calculate calorie consumption.

## Get started

All development has been done in Android Studio, however Gradle 2.10 should be the only requirement.

### Google API keys

Altituden requires both a Google _server key_ and _Android key_ with the following APIs activated:
- Google Maps Android API
- Google Maps Elevation API
- Google Maps Directions API

Create the key file in `/app/src/main/res/values/api_keys.xml` with the following template:
```
<resources>
    <string name="google_server_key"  translatable="false">AIza...</string>
    <string name="google_android_key" translatable="false">AIza...</string>
</resources>
```

### Google Play services

The SDK Package _Google Play services_ is required since Altituden uses the _fused location provider_ in Android.

1. Open the _SDK Manager_
2. Click on tab _SDK Tools_
3. Check box _Google Play Services_
4. Click _Apply_

### Gradle settings in Android Studio

Gradle version 2.10 or above is required. This only applies for Android Studio.

1. Go to `File -> Settings -> Build/Execution -> Build Tools -> Gradle`
2. Check the box _Use default gralde wrapper_

## Contributors

- Magnus Rising
- Michael Edevåg
- Mikael Bergström
- Robert Jegerås
- William Forsdal

Altituden was created at Chalmers University of Technology.
