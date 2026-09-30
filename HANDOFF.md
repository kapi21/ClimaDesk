# HANDOFF — ClimaDesk

Última sesión: **2026-09-30** (cerrada)

## Estado
- **Proyecto:** ClimaDesk (repo [kapi21/ClimaDesk](https://github.com/kapi21/ClimaDesk))
- **Versión app:** `1.8` (`versionCode` 9) — package `es.climadesk.app`
- **Hecho (esta sesión y previas en main):**
  - Limpieza de UI: eliminada etiqueta confusa «Ciudad en vivo» / origen y estado de red en `#updated`; ahora solo muestra `Act. HH:MM`
  - App kiosk WebView autónoma (Open-Meteo)
  - Ubicación GPS/Wi‑Fi + ciudad manual + selector con búsqueda
  - Refresh, día/noche, cache offline, mini-pronóstico
  - Package `es.climadesk.app` (v1.7) + icono reloj+sol
  - **Kiosk duro v1.8:** `BootReceiver` + HOME launcher (Autostart MIUI)
  - APK compilado e inyectado en Redmi 4X (`/sdcard/Download/ClimaDesk.apk`) vía ADB
- **Pendiente (próxima sesión):**
  1. **P2** Modo solo-reloj (ocultar stats / mini-pronóstico) — candidato lógico siguiente
  2. **P3-B** Release firmada + tag GitHub — **aplazado**: no hay keystore; crear uno local (`.jks` + `keystore.properties` gitignored) cuando se retome
- **Archivos clave:**
  - `kiosk-app/app/src/main/assets/index.html`
  - `kiosk-app/app/src/main/java/es/climadesk/app/MainActivity.java`
  - `kiosk-app/app/src/main/java/es/climadesk/app/BootReceiver.java`
  - `kiosk-app/app/src/main/AndroidManifest.xml`
  - `kiosk-app/app/src/main/res/drawable/ic_launcher_foreground.xml`
  - Specs: `docs/superpowers/specs/`
- **Cómo verificar:**
  - Compilar: `cd kiosk-app` → `gradlew.bat assembleDebug`
  - Instalar en Redmi (`6f207ef7d440`): si ADB falla por MIUI, push a `/sdcard/Download/` y aceptar instalador
  - Kiosk: Home → selector launcher; Autostart MIUI para boot
- **Riesgos / notas:**
  - MIUI bloquea `adb install` → `INSTALL_FAILED_USER_RESTRICTED`
  - Dos dispositivos ADB a veces: usar `-s 6f207ef7d440`
  - Si ClimaDesk es launcher por defecto y no puedes salir: `adb shell am force-stop es.climadesk.app` y `adb shell am start -a android.settings.SETTINGS`
  - Pantalla de bloqueo «Deslizar» en este MIUI **no se pudo quitar** sin root / opción Ninguno; con USB, `svc power stayon` ayuda a no apagar
  - `local.properties` y futuros `*.jks` / `keystore.properties` son locales (no van al repo)

## Kiosk duro (activar / quitar)

Referencia: `docs/superpowers/specs/2026-09-28-kiosk-hard-design.md`

### Activar

1. Instalar o actualizar ClimaDesk (v1.8+).
2. **MIUI:** Seguridad / Autostart → permitir ClimaDesk.
3. Pulsar **Home** → elegir **ClimaDesk** → **Siempre**.

### Quitar

1. **Ajustes → Apps → ClimaDesk → Abrir de forma predeterminada** → borrar defaults (o elegir el launcher del sistema al pulsar Home).
2. Desactivar **Autostart** de ClimaDesk en MIUI.
3. O desinstalar la app.
4. Si no puedes abrir Ajustes: ADB → `am start -a android.settings.SETTINGS` (tras `force-stop` de ClimaDesk).

## Roadmap
| Prioridad | Ítem | Notas |
|-----------|------|--------|
| P1 | Selector ciudad UI | ✅ |
| P1 | `applicationId` ClimaDesk | ✅ `es.climadesk.app` |
| P2 | Icono + branding | ✅ |
| P2 | Modo solo-reloj | Pendiente — siguiente candidato |
| P3 | Kiosk duro | ✅ v1.8 HOME + BOOT |
| P3 | Release firmada | Pendiente (P3-B) — aplazado sin keystore |

## Dispositivo de prueba
- Xiaomi Redmi 4X (santoni), Android 7.1.2 / MIUI
- Serial ADB: `6f207ef7d440`
