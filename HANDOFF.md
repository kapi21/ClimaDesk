# HANDOFF — ClimaDesk

Última sesión: **2026-09-28**

## Estado
- **Proyecto:** ClimaDesk (repo [kapi21/ClimaDesk](https://github.com/kapi21/ClimaDesk))
- **Versión app:** `1.7` (`versionCode` 8) — package `es.climadesk.app`
- **Hecho:**
  - App kiosk WebView autónoma (Open-Meteo)
  - Ubicación GPS/Wi‑Fi + ciudad manual
  - Refresh, día/noche, cache offline, mini-pronóstico
  - Fix overflow de horas dentro de la tarjeta de clima
  - Repo limpio (sin `downloads/` ni `panel/`)
  - README + captura landscape en `docs/climadesk-preview.png`
  - Selector de ciudad con búsqueda (sin prompt)
  - Rename `applicationId` / package → `es.climadesk.app` (v1.7)
  - Icono launcher flat reloj+sol (adaptive API 26+ / vector legacy)
- **Pendiente (siguiente sesión):**
  1. Modo solo-reloj (ocultar stats / mini-pronóstico)
- **Archivos clave:**
  - `kiosk-app/app/src/main/assets/index.html`
  - `kiosk-app/app/src/main/java/es/climadesk/app/MainActivity.java`
  - `kiosk-app/app/src/main/res/drawable/ic_launcher_foreground.xml`
  - `README.md`, `docs/climadesk-preview.png`
- **Cómo verificar:**
  - Compilar: `cd kiosk-app` → `gradlew.bat assembleDebug`
  - Instalar en Redmi (`6f207ef7d440`): si ADB falla por MIUI, push a `/sdcard/Download/` y aceptar instalador
- **Riesgos / notas:**
  - MIUI bloquea `adb install` → `INSTALL_FAILED_USER_RESTRICTED`
  - Dos dispositivos ADB a veces: usar `-s 6f207ef7d440`
  - `local.properties` es local (no va al repo)
  - Tras el rename hay que **reinstalar** el APK; desinstala la app antigua `es.redmi4x.relojclima` si no quieres dos iconos

## Roadmap
| Prioridad | Ítem | Notas |
|-----------|------|--------|
| P1 | Selector ciudad UI | ✅ Lista/autocomplete Open-Meteo geocoding |
| P1 | `applicationId` ClimaDesk | ✅ `es.climadesk.app` v1.7; reinstalar / desinstalar id viejo |
| P2 | Icono + branding | ✅ Reloj+sol flat; adaptive + legacy vector |
| P2 | Modo solo-reloj | Ocultar stats / mini-pronóstico |
| P3 | Kiosk duro | Autoarranque, brillo, launcher por defecto (por dispositivo) |
| P3 | Release firmada | APK release + tag GitHub |

## Dispositivo de prueba
- Xiaomi Redmi 4X (santoni), Android 7.1.2 / MIUI
- Serial ADB: `6f207ef7d440`
