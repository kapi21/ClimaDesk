# HANDOFF — ClimaDesk

Última sesión: **2026-09-27**

## Estado
- **Proyecto:** ClimaDesk (repo [kapi21/ClimaDesk](https://github.com/kapi21/ClimaDesk))
- **Versión app:** `1.6` (`versionCode` 7) — package aún `es.redmi4x.relojclima`
- **Hecho:**
  - App kiosk WebView autónoma (Open-Meteo)
  - Ubicación GPS/Wi‑Fi + ciudad manual
  - Refresh, día/noche, cache offline, mini-pronóstico
  - Fix overflow de horas dentro de la tarjeta de clima
  - Repo limpio (sin `downloads/` ni `panel/`)
  - README + captura landscape en `docs/climadesk-preview.png`
- **Pendiente (siguiente sesión):**
  1. Selector de ciudad con búsqueda (sustituir `prompt`)
  2. Renombrar `applicationId` / package a algo tipo `es.climadesk.app` (implica reinstalar)
  3. Icono launcher propio con marca ClimaDesk
- **Archivos clave:**
  - `kiosk-app/app/src/main/assets/index.html`
  - `kiosk-app/app/src/main/java/es/redmi4x/relojclima/MainActivity.java`
  - `README.md`, `docs/climadesk-preview.png`
- **Cómo verificar:**
  - Compilar: `cd kiosk-app` → `gradlew.bat assembleDebug`
  - Instalar en Redmi (`6f207ef7d440`): si ADB falla por MIUI, push a `/sdcard/Download/` y aceptar instalador
- **Riesgos / notas:**
  - MIUI bloquea `adb install` → `INSTALL_FAILED_USER_RESTRICTED`
  - Dos dispositivos ADB a veces: usar `-s 6f207ef7d440`
  - `local.properties` es local (no va al repo)

## Roadmap
| Prioridad | Ítem | Notas |
|-----------|------|--------|
| P1 | Selector ciudad UI | Lista/autocomplete Open-Meteo geocoding |
| P1 | `applicationId` ClimaDesk | Breaking install; actualizar label ya es ClimaDesk |
| P2 | Icono + branding | Sustituir `ic_launcher.xml` genérico |
| P2 | Modo solo-reloj | Ocultar stats / mini-pronóstico |
| P3 | Kiosk duro | Autoarranque, brillo, launcher por defecto (por dispositivo) |
| P3 | Release firmada | APK release + tag GitHub |

## Dispositivo de prueba
- Xiaomi Redmi 4X (santoni), Android 7.1.2 / MIUI
- Serial ADB: `6f207ef7d440`
