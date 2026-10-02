# HANDOFF — ClimaDesk

Última sesión: **2026-10-01** (cerrada)

## Estado
- **Proyecto:** ClimaDesk (repo [kapi21/ClimaDesk](https://github.com/kapi21/ClimaDesk))
- **Estrategia Multi-Dispositivo:** Tres ramas dedicadas y sus APKs independientes:
  1. Rama **`tablet-galaxy-tab2`** → `app-tablet-debug.apk` (Samsung Galaxy Tab 2 - Monocromo E-Ink)
  2. Rama **`tablet-galaxy-tab2-color`** → `app-tablet-color-debug.apk` (Samsung Galaxy Tab 2 - E-Ink Blanco Roto + Elementos a Color)
  3. Rama **`redmi4x-lite`** → `app-redmi-debug.apk` (Xiaomi Redmi 4X Lite)
- **Versión app:** `1.8-lite` (`versionCode` 10) en Redmi 4X / `1.8` (`versionCode` 9) en Tablet — package `es.climadesk.app`

## Hecho en esta sesión

### 1. Tablet Samsung Galaxy Tab 2 (Rama `tablet-galaxy-tab2`)
- **Estética SwitchBot E-Ink**: Diseño minimalista de 3 columnas con contraste nítido, sin elementos flotantes ni solapamientos.
- **Previsión semanal 7 días**: Tarjetas con badges de probabilidad de lluvia, temperaturas máx/mín y descripción de condiciones.
- **Compatibilidad Android 6.0**: Soporte para WebView Chromium 51 mediante bridge nativo SSL bypass para peticiones HTTPS de Open-Meteo.
- **Selector de tipografías retro**: Soporte para Casio FX-115, Casio FX-9860, Dot Matrix, Amstrad CPC 464 y Retro Computer.
- **Tema Día / Noche automático**: Cambio dinámico según la hora real de amanecer y atardecer calculada por Open-Meteo.
- **Accesos directos del sistema**: Botones en panel de ajustes para abrir Ajustes de Android y Explorador de archivos nativo.

### 2. Xiaomi Redmi 4X Lite (Rama `redmi4x-lite`)
- **Diseño Lite proporcional (640x360 dp)**: Ajuste exhaustivo de tipografías, alturas y paddings para encajar al 100% en la pantalla de 720p sin desbordes ni scroll.
- **Columna 1 (Tiempo y Sol)**: Fecha completa en mayúsculas (`JUEVES 1 DE OCTUBRE`), reloj digital 7 segmentos gigante y tarjetas de Amanecer / Atardecer.
- **Columna 2 (Clima Actual y 4 Horas)**: Temperatura actual destacada (ej. `23°`), condición, Sensación térmica, Rango Hoy y fila de próximas 4 horas.
- **Columna 3 (4 Métricas Clave)**: Humedad (%), Probabilidad Ahora (%), Probabilidad Máx Hoy (%) y Precipitación (mm).
- **Rendimiento ultra-ligero**: Petición Open-Meteo reducida a los datos necesarios (sin carga pesada de calidad del aire ni días sobrantes), `fetch()` nativo prioritario con fallback y puente `WebConsole` para depuración en logcat.

## Archivos clave
- `kiosk-app/app/src/main/assets/index.html` (interfaz, reloj SVG, cálculo de tema, selectores)
- `kiosk-app/app/src/main/java/es/climadesk/app/MainActivity.java` (WebView, bridge JS, WebChromeClient con logs)
- `kiosk-app/app/build.gradle.kts` (configuración de compilación)
- `app-tablet-debug.apk` (compilación lista para Galaxy Tab 2)
- `app-redmi-debug.apk` (compilación lista para Redmi 4X Lite)

## Cómo verificar y desplegar

### En Redmi 4X (`6f207ef7d440`)
1. Cambiar a rama: `git checkout redmi4x-lite`
2. Compilar: `cd kiosk-app && gradlew.bat assembleDebug`
3. Instalar: Si MIUI bloquea ADB directo, enviar por push y abrir:
   ```bash
   adb -s 6f207ef7d440 push app-redmi-debug.apk /sdcard/Download/ClimaDesk-Lite.apk
   adb -s 6f207ef7d440 shell "am start -a android.intent.action.VIEW -d 'file:///sdcard/Download/ClimaDesk-Lite.apk' -t 'application/vnd.android.package-archive'"
   ```

### En Samsung Galaxy Tab 2 (`c32063e15928a6f`)
1. Cambiar a rama: `git checkout tablet-galaxy-tab2`
2. Compilar: `cd kiosk-app && gradlew.bat assembleDebug`
3. Instalar directo vía ADB:
   ```bash
   adb -s c32063e15928a6f install -r app-tablet-debug.apk
   ```

## Roadmap

| Prioridad | Ítem | Estado | Notas |
|-----------|------|--------|-------|
| P1 | Selector ciudad UI + geolocalización | ✅ | Autodetección y buscador de municipios |
| P1 | `applicationId` unificado | ✅ | `es.climadesk.app` en ambos dispositivos |
| P2 | Dashboard E-Ink SwitchBot Tablet | ✅ | Rama `tablet-galaxy-tab2` con previsión semanal |
| P2 | Dashboard E-Ink SwitchBot Lite Redmi | ✅ | Rama `redmi4x-lite` con layout 3 columnas proporcional |
| P2 | Fuentes retro y tema Día/Noche auto | ✅ | Casio, Amstrad, DotMatrix, cálculo solar real |
| P2 | Modo solo-reloj | Pendiente | Ocultar columnas de clima/métricas en ajustes |
| P3 | Kiosk duro | ✅ | Autoarranque en boot y launcher por defecto |
| P3 | Release firmada | Pendiente | Requiere creación de keystore local (`.jks`) |

## Dispositivos de prueba
- **Xiaomi Redmi 4X (santoni)**: Android 7.1.2 / MIUI — ADB: `6f207ef7d440`
- **Samsung Galaxy Tab 2 (espresso)**: Android 6.0 Marshmallow — ADB: `c32063e15928a6f`
