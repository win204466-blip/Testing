# AniTracker

Android app untuk tracking anime dan manga menggunakan AniList API.

## Fitur
- Browse trending anime
- Browse popular this season
- Browse upcoming anime
- Top rated anime
- Search anime & manga
- Detail page dengan synopsis, score, genres, recommendations

## Build dengan GitHub Actions

1. Push repo ini ke GitHub
2. Pergi ke tab **Actions**
3. Klik workflow **Build APK**
4. Klik **Run workflow** (atau otomatis jalan saat push)
5. Setelah selesai, download APK dari **Artifacts**

## Build Manual

```bash
./gradlew assembleDebug
```

APK akan ada di: `app/build/outputs/apk/debug/app-debug.apk`

## Tech Stack
- Kotlin
- Retrofit + OkHttp
- Glide
- Material Design 3
- Coroutines
- AniList GraphQL API
