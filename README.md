# Altituden

An Android app that uses your phone's position together with accurate altitude data to calculate calorie consumption.

## Requirements

All development has been done in Android Studio, however that should not be a requirement.

### Gradle

Gradle version 2.10 or above is required.

Change Gradle settings in Android Studio:

1. Go to `File -> Settings -> Build/Execution -> Build Tools -> Gradle`
2. Check the box _Use default gralde wrapper_

### API keys

Altituden requires both a Google _server key_ and _Android key_ with the following APIs activated:
- Google Maps Android API
- Google Maps Elevation API
- Google Maps Directions API

Create the keyfile in `/app/src/main/res/values/api_keys.xml` using the following template:
```
<resources>
    <string name="google_server_key"  translatable="false">AIza...</string>
    <string name="google_android_key" translatable="false">AIza...</string>
</resources>
```

### SDK Packages

The SDK Package _Google Play services_ is required since Altituden uses the _fused location provider_ in Android.

1. Open the _SDK Manager_
2. Click on the tab _SDK Tools_
3. Check the box _Google Play Services_
4. Click on _Apply_

## Contributors

- Magnus Rising
- Michael Edevåg
- Mikael Bergström
- Robert Jegerås
- William Forsdal

Altituden was created at Chalmers University of Technology.
