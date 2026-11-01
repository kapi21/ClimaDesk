# HANDOFF — ClimaDesk

Última sesión: **2026-11-01** (`main` — atenuación por elevación solar estilo WebClima; dejar y revisar luego)

## Estado
- **Proyecto:** ClimaDesk (repo [kapi21/ClimaDesk](https://github.com/kapi21/ClimaDesk))
- **Estrategia Multi-Dispositivo:** Tres ramas dedicadas y APKs en GitHub Releases + carpeta `apks/`:
  1. Rama **`tablet-galaxy-tab2-color`** → build local `apks/Climadesk-Color-v1.8.1.apk` (Galaxy Tab 2 — E-Ink Color)
  2. Rama **`tablet-galaxy-tab2`** → build local `apks/Climadesk-v1.8.1.apk` (Galaxy Tab 2 — Monocromo E-Ink)
  3. Rama **`redmi4x-lite`** → build local `apks/Climadesk-Lite-v1.8.2.apk` (Xiaomi Redmi 4X — Lite)
- **Versión app (builds locales):** `1.8.2-lite` (`versionCode` 12) en Redmi / `1.8.1` (`versionCode` 10) en Tablet Color/Mono y `main` — package `es.climadesk.app`
- **GitHub Release oficial:** [v1.8](https://github.com/kapi21/ClimaDesk/releases/tag/v1.8) (binarios v1.8; builds locales más recientes en `apks/`)
- **Hoja de ruta:** [ROADMAP.md](ROADMAP.md) — pendiente **revisar claridad de la atenuación solar**

## Hecho reciente (2026-11-01 — atenuación solar)

- **Modelo actual (estilo WebClima, opción A):** elevación del sol con lat/lon → factor `--night` (0…1) → overlay negro a `night × 0.45`. AUTO siempre activo.
- **Botón test** (icono SVG mitad/mitad en la barra superior): demo ~12 s día→noche→día; segundo toque cancela.
- **Historia previa en la misma sesión:** primero overlay por sunrise/sunset Open-Meteo + rampas 30 min; luego se alineó a elevación solar como en `WebClima`.
- **Feedback usuario:** el efecto “no está muy claro”; **se deja así** y queda anotado en ROADMAP para revisar.
- **Código committed + pushed** en `main`, `redmi4x-lite`, `tablet-galaxy-tab2`, `tablet-galaxy-tab2-color`.
- **Instalado:** Tab Color `1.8.1` vía `adb install`; Lite `1.8.2` vía push + instalador MIUI (confirmar en dispositivo si hace falta).
- **Spec/plan iniciales (sunrise/sunset):** `docs/superpowers/specs/2026-11-01-sunset-dim-overlay-design.md`, `docs/superpowers/plans/2026-11-01-sunset-dim-overlay.md` (el runtime actual es elevación solar; la spec original describe el diseño previo).
- **Fuera de alcance aún:** brillo Android / sensor de luz; recoloreado de tema tipo WebClima; slider manual.

### Cómo probar atenuación

1. Abrir build con ubicación conocida (lat/lon de ciudad o GPS).
2. De día el velo debe ser bajo/cero; al anochecer subir según elevación.
3. Pulsar el icono de prueba — oscurece al momento ~12 s.

## Hecho anterior (Lite v1.8.1, 2026-10-30)

- **Redmi 4X Lite:** layout solar vertical — AMANECER y ATARDECER apilados en columna (rama `redmi4x-lite`, `apks/Climadesk-Lite-v1.8.1.apk`, `versionCode` 11).

## Hecho en sesión anterior (2026-10-02)

### 1. Tablet Galaxy Tab 2 Color (Rama `tablet-galaxy-tab2-color`)
- **Estética E-Ink Color**: Fondo blanco roto, tarjetas limpias, tipografía negra; sol/lluvia/humedad a color.
- **Verificado** en Galaxy Tab 2 (`c32063e15928a6f`).

### 2. APKs + Release v1.8
- Carpeta `apks/`, Release GitHub **v1.8** con Color / Mono / Lite.

### 3. iPhone XS (plan)
- PWA Safari + Acceso Guiado; OLED `#000000` + pixel-shift (backlog).

## Archivos clave y APKs
- `kiosk-app/app/src/main/assets/index.html` (UI, elevación solar / `--night`, overlay, botón test)
- `kiosk-app/app/src/main/java/es/climadesk/app/MainActivity.java`
- `kiosk-app/app/build.gradle.kts`
- `ROADMAP.md`, `docs/superpowers/specs/`, `docs/superpowers/plans/`
- `apks/Climadesk-Color-v1.8.1.apk`
- `apks/Climadesk-v1.8.1.apk`
- `apks/Climadesk-Lite-v1.8.2.apk`

## Cómo verificar y desplegar

### En Redmi 4X (`6f207ef7d440`)
```bash
git checkout redmi4x-lite
adb -s 6f207ef7d440 push apks/Climadesk-Lite-v1.8.2.apk /sdcard/Download/ClimaDesk-Lite.apk
adb -s 6f207ef7d440 shell "am start -a android.intent.action.VIEW -d 'file:///sdcard/Download/ClimaDesk-Lite.apk' -t 'application/vnd.android.package-archive'"
```

### En Samsung Galaxy Tab 2 (`c32063e15928a6f`)
```bash
# Versión Color (build local reciente):
adb -s c32063e15928a6f install -r apks/Climadesk-Color-v1.8.1.apk
# Versión Monocromo:
adb -s c32063e15928a6f install -r apks/Climadesk-v1.8.1.apk
```

## Dispositivos probados y soporte planificado
- **Xiaomi Redmi 4X (santoni)**: Android 7.1.2 / MIUI — ADB: `6f207ef7d440` (Verificado ✅)
- **Samsung Galaxy Tab 2 (espresso)**: Android 6.0 — ADB: `c32063e15928a6f` (Verificado ✅)
- **Apple iPhone XS**: iOS 17+ — Planificado como PWA OLED (Pendiente 📋)
