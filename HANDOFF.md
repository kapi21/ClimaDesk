# HANDOFF — ClimaDesk

Última sesión: **2026-11-19** (tablet sin actualizar desde el 17-nov 17:22: diagnóstico, arreglo de red/reintentos en las 4 ramas, push y APK nuevas)

## Estado
- **Proyecto:** ClimaDesk (repo [kapi21/ClimaDesk](https://github.com/kapi21/ClimaDesk))
- **Estrategia Multi-Dispositivo:** Tres ramas dedicadas y APKs en GitHub Releases + carpeta `apks/`:
  1. Rama **`tablet-galaxy-tab2-color`** (`e405328`) → build local `apks/Climadesk-Color-v1.8.3.apk` (Galaxy Tab 2 — E-Ink Color) — **instalada y verificada**
  2. Rama **`tablet-galaxy-tab2`** (`c492206`) → build local `apks/Climadesk-v1.8.3.apk` (Monocromo E-Ink) — compilada, **sin instalar ni probar**
  3. Rama **`redmi4x-lite`** (`da32b88`) → build local `apks/Climadesk-Lite-v1.8.4.apk` (Redmi 4X — Lite) — **instalada por el usuario** (sin probar el refresco)
  4. Rama **`main`** (`dad1145`) — solo reintentos; no se distribuye como APK
- **Versión app (builds locales):** `1.8.4-lite` (`versionCode` 14) en Redmi / `1.8.3` (`versionCode` 12) en Tablet Color/Mono / `1.8.1` (10) en `main` — package `es.climadesk.app`. Las APK `apks/` están en `.gitignore` (solo locales).
- **GitHub Release oficial:** [v1.8.3](https://github.com/kapi21/ClimaDesk/releases/tag/v1.8.3) (2026-11-18: Color/Mono `1.8.2`, Lite `1.8.3`; **desactualizada**: no incluye el arreglo de red 2026-11-19, README sigue enlazando a ella; anterior [v1.8](https://github.com/kapi21/ClimaDesk/releases/tag/v1.8))
- **Hoja de ruta:** [ROADMAP.md](ROADMAP.md) — pendiente **revisar claridad de la atenuación solar**
- **Pendiente próximo:** instalar/probar la Mono; comprobar en el Redmi que refresca; publicar Release nueva (v1.8.4) con las APK actuales y actualizar enlaces del README; opcional: confirmar el código HTTP exacto que recibe la tablet.

## Hecho reciente (2026-11-19 — tablet sin actualizar)

- **Síntoma:** la Tab Color mostraba «Actualizado 17:22» con datos del martes 17-nov (días «Lun (Ayer)/Mar (Hoy)») aunque hoy era jueves. Era la caché: `loadData` caía en `catch`, repintaba la caché con su `ts` viejo y no reintentaba hasta 20 min después.
- **Causa:** en el logcat de la tablet, `httpGet` (`MainActivity.java`) daba `FileNotFoundException` (404/410) solo en la URL del pronóstico; la de `air-quality` iba bien. Desde el PC, la misma URL con User-Agent `Dalvik/…` daba 403 (reproducible con `current=pressure_msl`); con UA propio o `curl`, 200. Causa probable: Open-Meteo rechaza el UA por defecto de Dalvik. **No se confirmó el código HTTP exacto en la tablet**; sí se verificó que con el UA nuevo sincroniza («Actualizado 12:23»).
- **Arreglo** (mismo commit en Color/Mono/Lite): `User-Agent: ClimaDesk/1.8.3 (Android kiosk)` + `Accept` en `httpGet`; reintentos con backoff 30 s/1/2/5 min (`scheduleRetry`); etiqueta «Sin conexión · datos de HH:MM» si se usa la caché (`data.stale`); cambiar el intervalo en ajustes aplica al instante (`startRefreshTimer`, antes `setInterval` fijo al arrancar).
- **`main`**: usa `fetch` del WebView (sin puente Java, sin problema de UA) y ya mostraba «Sin internet»; solo se añadió reintento con backoff. Verificado solo con `node --check`.
- **Ramas** (pusheadas): Color `e405328` (1.8.3/12), Mono `c492206` (1.8.3/12), Lite `da32b88` (1.8.4-lite/14: ya existía 1.8.3-lite; conflicto resuelto a mano, la Lite no tiene `renderForecast`), `main` `dad1145`.
- **Instalado:** Tab Color 1.8.3 vía `adb install -r` ✅ verificado. Redmi: APK copiada a Descargas + instalador lanzado, el usuario confirmó la instalación ✅. Mono **no** instalada: la única Galaxy Tab 2 conectada lleva la Color y comparten paquete (la Mono la sustituiría); el dispositivo `54426981535870` (P80X_EEA) no tiene la app y no se sabe si es la Mono.
- **Notas:** los logs de `Log.e` de `UnknownHostException` salen sin traza; `FileNotFoundException` sí la trae. Se vio también un DNS fallido a los pocos segundos de arrancar la tablet (red aún no lista), ahora cubierto por los reintentos. El `adb shell screencap -p` a stdout se corrompe con avisos del linker: guardar en `/sdcard` y `adb pull` desde PowerShell.

## Hecho anterior (2026-11-18 — revisión y arreglos)

- **`main` (`2dc4d10`)**: tema claro/oscuro por elevación solar (antes horario fijo 7–20 h); caché sin conexión solo de la ciudad actual; "Act. HH:MM" con la hora real de los datos; previsión por horas con `utc_offset_seconds` de la ciudad.
- **Ramas de dispositivo** (mismo parche en las 3; su tema ya iba por amanecer/atardecer): caché solo de la ciudad actual (`sameCity`), "Actualizado" con `data.ts`, horas de previsión y "prob. ahora" en la zona horaria de la ciudad (`cityTimeToMs`).
  - `redmi4x-lite` `76ab922` → `1.8.3-lite` (13) → `apks/Climadesk-Lite-v1.8.3.apk`
  - `tablet-galaxy-tab2` `b5914c8` → `1.8.2` (11) → `apks/Climadesk-v1.8.2.apk`
  - `tablet-galaxy-tab2-color` `6f6ee79` → `1.8.2` (11) → `apks/Climadesk-Color-v1.8.2.apk`
- **Release [v1.8.3](https://github.com/kapi21/ClimaDesk/releases/tag/v1.8.3)** (Latest, desde `main`) con las 3 APK; README/ROADMAP apuntan a ella. Todas las ramas pusheadas.
- **Instalado:** Galaxy Tab 2 Color `1.8.2` vía `adb install -r` ✅ (la Mono `1.8.2` está compilada pero no instalada). Redmi: MIUI bloquea `adb install` (`INSTALL_FAILED_USER_RESTRICTED`); APK copiada + instalador abierto y confirmado en el teléfono → `1.8.3-lite` instalada ✅.
- **Nota Git Bash:** usar `MSYS_NO_PATHCONV=1` con `adb push ... /sdcard/...` (si no, convierte la ruta a `C:/Program Files/Git/sdcard`). `gradlew.bat` hay que lanzarlo desde PowerShell (la ruta con espacio rompe Bash).
- **Sigue pendiente (11-18):** claridad de la atenuación solar (el velo no baja la retroiluminación; valorar `screenBrightness` vía puente Java). `main` sigue en `1.8.1` (no se distribuye como APK). El tema de las ramas compara amanecer/atardecer de la ciudad con la hora del dispositivo (solo falla con ciudades de otro huso).

## Hecho anterior (2026-11-01 — atenuación solar)

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
- `apks/Climadesk-Color-v1.8.3.apk`
- `apks/Climadesk-v1.8.3.apk`
- `apks/Climadesk-Lite-v1.8.4.apk`

## Cómo verificar y desplegar

### En Redmi 4X (`6f207ef7d440`)
```bash
git checkout redmi4x-lite
adb -s 6f207ef7d440 push apks/Climadesk-Lite-v1.8.4.apk /sdcard/Download/ClimaDesk-Lite.apk
adb -s 6f207ef7d440 shell "am start -a android.intent.action.VIEW -d 'file:///sdcard/Download/ClimaDesk-Lite.apk' -t 'application/vnd.android.package-archive'"
```

### En Samsung Galaxy Tab 2 (`c32063e15928a6f`)
```bash
# Versión Color (build local reciente):
adb -s c32063e15928a6f install -r apks/Climadesk-Color-v1.8.3.apk
# Versión Monocromo:
adb -s c32063e15928a6f install -r apks/Climadesk-v1.8.3.apk   # sustituye a la Color (mismo paquete)
```

## Dispositivos probados y soporte planificado
- **Xiaomi Redmi 4X (santoni)**: Android 7.1.2 / MIUI — ADB: `6f207ef7d440` (Verificado ✅)
- **Samsung Galaxy Tab 2 (espresso)**: Android 6.0 — ADB: `c32063e15928a6f` (Verificado ✅)
- **Apple iPhone XS**: iOS 17+ — Planificado como PWA OLED (Pendiente 📋)
