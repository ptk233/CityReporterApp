# ✅ MINIMALNA KONFIGURACJA RAILWAY - CHECKLIST

## Variables tab - MUSISZ MIEĆ:

### 1. Backend Variables (dodaj ręcznie):
```
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=<twój-wygenerowany-secret>
```

### 2. Database Variables (automatyczne, gdy dodasz PostgreSQL):
```
DATABASE_PRIVATE_URL=postgresql://...
DATABASE_URL=postgresql://...
PGHOST=...
PGPORT=5432
PGUSER=postgres
PGPASSWORD=...
PGDATABASE=railway
```

### 3. Railway System Variables (automatyczne):
```
PORT=<Railway ustawi automatycznie>
RAILWAY_ENVIRONMENT=production
```

---

## Jak sprawdzić czy wszystko działa:

### Krok 1: Sprawdź status
W Railway Dashboard:
- ✅ PostgreSQL - zielony status (Running)
- ✅ Backend - zielony status (Running)
- ❌ Backend - czerwony/żółty = CRASH

### Krok 2: Zobacz logi
Deployments → View Logs → szukaj błędów:
```
❌ "JWT secret cannot be null" → brak JWT_SECRET
❌ "Failed to configure a DataSource" → problem z bazą
❌ "Port 8080 is already in use" → Railway nie ustawił PORT
✅ "Started CityReporterBackendApplicationKt" → działa!
```

### Krok 3: Test HTTP
```bash
curl https://twoj-url.railway.app/actuator/health
```

Powinno zwrócić:
```json
{"status":"UP"}
```

---

## 🔥 QUICK FIX - Jeśli crashuje:

1. **Variables** → dodaj:
   ```
   SPRING_PROFILES_ACTIVE=prod
   JWT_SECRET=pAlx9LICb3VcAifUSGlT0vGbtb93MSwCVo4kTT+BBrQ=
   ```

2. **Sprawdź czy PostgreSQL działa** (zielony status)

3. **Settings** → **Redeploy** 

4. **Deployments** → **View Logs** → czekaj na:
   ```
   Started CityReporterBackendApplicationKt in X.XXX seconds
   ```

---

## Jeśli dalej nie działa - prześlij logi!
