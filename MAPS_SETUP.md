# Konfiguracja Google Maps dla City Reporter

## 1. Uzyskanie Google Maps API Key

1. Przejdź do [Google Cloud Console](https://console.cloud.google.com/)
2. Utwórz nowy projekt lub wybierz istniejący
3. Włącz następujące API:
   - **Maps SDK for Android**
   - **Places API** (opcjonalnie, do wyszukiwania adresów)
   - **Geocoding API** (opcjonalnie, do konwersji współrzędnych na adresy)

4. Utwórz klucz API:
   - Przejdź do **APIs & Services → Credentials**
   - Kliknij **Create Credentials → API Key**
   - Skopiuj wygenerowany klucz

5. (Opcjonalnie) Ogranicz klucz API:
   - Kliknij na utworzony klucz
   - W sekcji **Application restrictions** wybierz **Android apps**
   - Dodaj swój package name: `com.example.cityreporter`
   - Dodaj SHA-1 fingerprint (zobacz poniżej jak uzyskać)

## 2. Jak uzyskać SHA-1 fingerprint

### Debug keystore:
```bash
# Mac/Linux:
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

# Windows:
keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
```

### Release keystore:
```bash
keytool -list -v -keystore your-release-key.keystore -alias your-alias-name
```

## 3. Dodanie klucza do projektu

1. Otwórz plik `local.properties` w głównym katalogu projektu
2. Zastąp `YOUR_ACTUAL_API_KEY_HERE` swoim kluczem:
```properties
MAPS_API_KEY=AIzaSyB... (twój klucz)
```

⚠️ **WAŻNE**: Nigdy nie commituj pliku `local.properties` do repozytorium!

## 4. Testowanie

1. Uruchom aplikację na emulatorze lub urządzeniu
2. Przejdź do zakładki "Mapa"
3. Zaakceptuj prośbę o uprawnienia lokalizacji
4. Powinieneś zobaczyć mapę z Twoją lokalizacją

## 5. Rozwiązywanie problemów

### Mapa się nie ładuje:
- Sprawdź czy API Key jest poprawny
- Sprawdź czy Maps SDK for Android jest włączone w Google Cloud Console
- Sprawdź logi w Android Studio (szukaj błędów związanych z Google Maps)

### Brak lokalizacji:
- Upewnij się, że uprawnienia lokalizacji są przyznane
- Na emulatorze: ustaw lokalizację przez Extended Controls
- Sprawdź czy GPS jest włączony

### Błąd "API key not found":
- Sprawdź czy plik `local.properties` istnieje
- Wykonaj Clean Project i Rebuild Project
- Sprawdź czy `manifestPlaceholders` są poprawnie skonfigurowane w `build.gradle.kts`

## 6. Funkcjonalności mapy

Aktualna implementacja zawiera:
- ✅ Wyświetlanie mapy Google
- ✅ Pokazywanie aktualnej lokalizacji użytkownika
- ✅ Markery dla zgłoszeń w pobliżu
- ✅ Różne kolory markerów dla różnych kategorii
- ✅ Kliknięcie w marker pokazuje szczegóły
- ✅ Przycisk centrowania na obecnej lokalizacji
- ✅ Licznik znalezionych zgłoszeń

## 7. Dodatkowe opcje (TODO)

- [ ] Clustering markerów przy dużym zagęszczeniu
- [ ] Filtrowanie zgłoszeń po kategorii
- [ ] Heatmapa zgłoszeń
- [ ] Rysowanie obszaru wyszukiwania
- [ ] Street View dla lokalizacji zgłoszenia