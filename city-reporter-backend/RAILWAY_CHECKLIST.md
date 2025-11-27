# ✅ Railway Deployment Checklist

## Przed deploymentem

- [ ] Wszystkie zmiany commitowane do GitHuba
- [ ] Pliki konfiguracyjne Railway są na miejscu:
  - [ ] `nixpacks.toml`
  - [ ] `railway.json`
  - [ ] `Procfile`
  - [ ] `application-prod.yml`
- [ ] `gradlew` ma uprawnienia wykonania (`chmod +x gradlew`)
- [ ] Build działa lokalnie: `./gradlew clean build -x test`

## Na Railway

### 1. Utwórz projekt
- [ ] Nowy projekt z GitHub repo
- [ ] Dodaj PostgreSQL database

### 2. Zmienne środowiskowe (Variables tab)
- [ ] `SPRING_PROFILES_ACTIVE=prod`
- [ ] `JWT_SECRET=<wygenerowany-klucz>` (min. 32 znaki)
- [ ] Railway automatycznie doda: `DATABASE_PRIVATE_URL`, `DATABASE_URL`, `PORT`

### 3. Deploy
- [ ] Poczekaj na build (3-5 minut pierwszym razem)
- [ ] Sprawdź logi czy nie ma błędów
- [ ] Skopiuj URL projektu

### 4. Testowanie
- [ ] `curl https://twoj-url.railway.app/actuator/health` → powinno zwrócić `{"status":"UP"}`
- [ ] Sprawdź czy można się zarejestrować/zalogować

## W aplikacji Android

- [ ] Zmień `BASE_URL` w `Constants.kt` na Railway URL
- [ ] Przebuduj aplikację (Clean + Rebuild)
- [ ] Przetestuj na urządzeniu

## 🎯 Railway URL Format
```
https://city-reporter-backend-production-xxxx.up.railway.app
```

## 🔐 Jak wygenerować JWT_SECRET
```bash
openssl rand -base64 32
```

## ⚠️ Częste problemy

**Build fails:** Sprawdź czy `gradlew` ma permissions
**Can't connect to DB:** Użyj `DATABASE_PRIVATE_URL` zamiast `DATABASE_URL`  
**JWT error:** Upewnij się że JWT_SECRET jest ustawiony w Variables
