# City Reporter Backend - Deployment Guide

## 🚀 Deployment na Railway.app

### Przygotowanie

1. **Załóż konto na Railway.app**
   - Wejdź na https://railway.app
   - Zaloguj się przez GitHub

2. **Wypchnij kod na GitHub**
   ```bash
   cd /Users/mateusz/Documents/inzynierka/city-reporter-backend
   git add .
   git commit -m "Przygotowanie do deploya"
   git push
   ```

### Deployment

1. **Utwórz nowy projekt na Railway**
   - Kliknij "New Project"
   - Wybierz "Deploy from GitHub repo"
   - Wybierz repozytorium `city-reporter-backend`
   - Railway rozpocznie automatyczny build

2. **Dodaj PostgreSQL bazę danych**
   - W projekcie kliknij "New"
   - Wybierz "Database" → "Add PostgreSQL"
   - Railway automatycznie utworzy bazę i zmienne środowiskowe
   - Połączy bazę z Twoim backendem

3. **Skonfiguruj zmienne środowiskowe**
   
   Przejdź do zakładki "Variables" w swoim backendzie i dodaj:
   
   **WAŻNE:** Railway automatycznie tworzy `DATABASE_URL`, ale musimy go przekonwertować:
   
   ```
   SPRING_PROFILES_ACTIVE=prod
   PORT=8080
   ```
   
   **JWT Secret - wygeneruj bezpieczny klucz:**
   ```bash
   # Na Macu/Linuxie:
   openssl rand -base64 32
   
   # Lub użyj dowolnego długiego, losowego ciągu (minimum 32 znaki)
   ```
   
   Dodaj do Railway:
   ```
   JWT_SECRET=<twoj-wygenerowany-klucz>
   ```
   
   **Dla bazy danych:** Railway tworzy `DATABASE_URL` w formacie:
   ```
   postgresql://postgres:password@host:port/railway
   ```
   
   Musisz dodać osobno (Railway czasem tego wymaga):
   ```
   DATABASE_USERNAME=postgres
   DATABASE_PASSWORD=<password_z_DATABASE_URL>
   ```
   
   **Lub prostsze rozwiązanie - użyj DATABASE_PRIVATE_URL:**
   Railway udostępnia też `DATABASE_PRIVATE_URL` - użyj tej zmiennej zamiast DATABASE_URL.

4. **Przebuduj projekt**
   - Po dodaniu zmiennych kliknij "Deploy" → "Redeploy"
   - Sprawdź logi w czasie rzeczywistym
   - Poszukaj błędów jeśli coś pójdzie nie tak

### Testowanie

Po wdrożeniu sprawdź:
```bash
# Health check
curl https://twoj-url.railway.app/actuator/health

# Powinno zwrócić:
{"status":"UP"}
```

---

## 🏠 Alternatywa: Lokalny serwer z ngrok (Tymczasowe)

Jeśli chcesz szybko przetestować bez wdrażania:

```bash
# 1. Zainstaluj ngrok
brew install ngrok

# 2. Uruchom backend lokalnie
./gradlew bootRun

# 3. W nowym terminalu uruchom ngrok
ngrok http 8080

# Dostaniesz URL typu: https://abc123.ngrok.io
```

⚠️ **Uwaga:** 
- Komputer musi być włączony
- URL zmienia się przy każdym uruchomieniu (w darmowej wersji)
- Wolniejsze niż prawdziwy hosting

---

## 📱 Aktualizacja aplikacji Android

Po wdrożeniu backendu zaktualizuj URL w aplikacji:

**Plik:** `CityReporterApp/app/src/main/java/com/example/cityreporter/utils/Constants.kt`

```kotlin
object Constants {
    // Zmień na URL z Railway:
    const val BASE_URL = "https://twoj-url.railway.app/"
    
    // Zamiast:
    // const val BASE_URL = "http://10.0.2.2:8080/"
}
```

**Przebuduj i wgraj APK ponownie!**

---

## 🔧 Debugowanie na Railway

### Logi aplikacji:
1. Wejdź na Railway dashboard
2. Kliknij na swój backend
3. Zakładka "Deployments" → kliknij na aktywny deployment
4. Zobacz logi w czasie rzeczywistym

### Sprawdź zmienne środowiskowe:
```bash
# W zakładce "Variables" powinieneś widzieć:
DATABASE_URL=postgresql://...
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=...
JWT_SECRET=...
PORT=8080
SPRING_PROFILES_ACTIVE=prod
```

---

## 💰 Koszty

**Railway Free Tier:**
- $5 kredytu miesięcznie
- Wystarczy dla małej aplikacji (~500MB RAM)
- Baza PostgreSQL wliczona

**Jeśli zabraknie:**
- Dodaj kartę kredytową
- Pay-as-you-go: ~$5-10/miesiąc dla małej aplikacji

---

## 🔐 Bezpieczeństwo

**WAŻNE przed wdrożeniem produkcyjnym:**

1. ✅ Zmień JWT_SECRET na bezpieczny losowy klucz
2. ✅ Używaj HTTPS (Railway daje automatycznie)
3. ✅ Nie commituj secrets do GitHuba
4. ✅ Ogranicz CORS tylko do zaufanych domen (opcjonalnie)

---

## 📊 Monitoring

Railway zapewnia:
- ✅ CPU/Memory usage graphs
- ✅ Request logs
- ✅ Health checks
- ✅ Auto-restart w razie crashu

---

## 🆘 Pomoc

Problemy z deploymentem?

### "Error creating build plan with Railpack"
✅ Sprawdź czy masz pliki: `nixpacks.toml`, `railway.json`, `Procfile`
✅ Sprawdź czy `gradlew` ma uprawnienia do wykonania
✅ Sprawdź logi budowania na Railway

### "Failed to connect to database"
✅ Sprawdź czy PostgreSQL database jest dodany do projektu
✅ Sprawdź czy zmienne DATABASE_URL/DATABASE_USERNAME/DATABASE_PASSWORD są ustawione
✅ Spróbuj użyć DATABASE_PRIVATE_URL zamiast DATABASE_URL

### "Application failed to start"
1. Sprawdź logi na Railway - zakładka "Deployments" → kliknij na deployment → "View Logs"
2. Sprawdź czy wszystkie zmienne środowiskowe są ustawione:
   - SPRING_PROFILES_ACTIVE=prod
   - JWT_SECRET=<długi-losowy-ciąg>
   - DATABASE_URL lub DATABASE_PRIVATE_URL
   - PORT (opcjonalne, Railway ustawia automatycznie)
3. Sprawdź czy baza danych działa (powinien być zielony status)

### Typowe błędy:

**Błąd:** `JWT secret cannot be null`
**Rozwiązanie:** Dodaj zmienną `JWT_SECRET` w Variables

**Błąd:** `Failed to configure a DataSource`  
**Rozwiązanie:** 
- Upewnij się że PostgreSQL database jest połączony z backendem
- W Railway Variables użyj `DATABASE_PRIVATE_URL` (lub DATABASE_URL)
- Możesz też ręcznie dodać: DATABASE_USERNAME i DATABASE_PASSWORD

**Błąd:** `Port 8080 already in use`
**Rozwiązanie:** Railway automatycznie ustawia PORT - nie musisz go ustawiać ręcznie

**Pytania? Napisz na:** [twoj-email]
