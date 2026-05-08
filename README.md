# Warehouse_accounting_app (Android)

## Сеть и безопасность

- **Debug / локальный эмулятор:** по умолчанию API — `http://10.0.2.2:8080` (хост ПК с Android Emulator). Cleartext HTTP **разрешён** только в **debug** (`android:usesCleartextTraffic` через placeholder `cleartextTraffic` в `app/build.gradle.kts`).
- **Release:** cleartext **запрещён**; базовый URL задаётся в **`local.properties`** как **`api.base.url`** и **обязан** быть **`https://...`** (проверка перед `assembleRelease`). Боевой URL в репозиторий не коммитится.

Подробнее про эмулятор и `local.properties` см. `Warehouse_accounting_server/LOCAL_RUN.md`, раздел **«Android Emulator → сервер»**.

Ручной сквозной smoke (эмулятор → сервер → Postgres), curl и проверки ролей: **`Warehouse_accounting_server/E2E_CHECKLIST.md`**.

## Сборка

```bat
.\gradlew.bat assembleDebug
```

Release (нужны подпись и HTTPS в `local.properties`):

```bat
.\gradlew.bat assembleRelease
```
