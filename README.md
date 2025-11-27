# 🏙️ City Reporter

Aplikacja do zgłaszania problemów miejskich - projekt inżynierski.

## 📁 Struktura projektu

```
inzynierka/
├── CityReporterApp/          # Aplikacja mobilna (Android)
│   └── README.md             # Instrukcje dla aplikacji
├── city-reporter-backend/    # Backend (Spring Boot + Kotlin)
│   ├── DEPLOYMENT.md         # 📘 Instrukcje wdrożenia
│   └── RAILWAY_CHECKLIST.md  # ✅ Checklist przed deployem
└── README.md                 # Ten plik
```

## 🚀 Szybki start

### Backend (Spring Boot)
```bash
cd city-reporter-backend
./gradlew bootRun
```
Backend dostępny na: `http://localhost:8080`

### Aplikacja Android
1. Otwórz `CityReporterApp` w Android Studio
2. Uruchom na emulatorze lub urządzeniu

## 🌐 Deployment (Railway)

**Ważne:** Przy deploymencie na Railway wybierz:
- **Root Directory:** `city-reporter-backend`
- Railway automatycznie wykryje i zbuduje tylko backend!

Szczegółowe instrukcje: [`city-reporter-backend/DEPLOYMENT.md`](city-reporter-backend/DEPLOYMENT.md)

## 📱 Konfiguracja API w aplikacji

Po wdrożeniu backendu zaktualizuj URL w aplikacji:

**Plik:** `CityReporterApp/app/src/main/java/com/example/cityreporter/utils/Constants.kt`

```kotlin
const val BASE_URL = "https://twoj-railway-url.railway.app/"
```

## 🛠️ Technologie

### Backend
- Kotlin + Spring Boot 3.2
- PostgreSQL
- JWT Authentication
- Spring Security

### Frontend (Android)
- Kotlin
- Jetpack Compose
- Hilt (Dependency Injection)
- Retrofit (API)
- Coil (Image Loading)
- Google Maps

## 📄 Dokumentacja

- [Backend Deployment Guide](city-reporter-backend/DEPLOYMENT.md)
- [Railway Checklist](city-reporter-backend/RAILWAY_CHECKLIST.md)

## 👨‍💻 Autor

Mateusz - Politechnika Wrocławska, Informatyczne Systemy Automatyki
