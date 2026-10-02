# HANDOFF — ClimaDesk

Última sesión: **2026-10-02 22:50** (cerrada)

## Estado
- **Proyecto:** ClimaDesk (repo [kapi21/ClimaDesk](https://github.com/kapi21/ClimaDesk))
- **Estrategia Multi-Dispositivo:** Tres ramas dedicadas y sus APKs independientes centralizadas en GitHub Releases y carpeta `apks/`:
  1. Rama **`tablet-galaxy-tab2-color`** → `apks/Climadesk-Color-v1.8.apk` (Galaxy Tab 2 — E-Ink Color)
  2. Rama **`tablet-galaxy-tab2`** → `apks/Climadesk-v1.8.apk` (Galaxy Tab 2 — Monocromo E-Ink)
  3. Rama **`redmi4x-lite`** → `apks/Climadesk-Lite-v1.8.apk` (Xiaomi Redmi 4X — Edición Lite 640x360 dp)
- **Versión app:** `1.8-lite` (`versionCode` 10) en Redmi 4X / `1.8` (`versionCode` 9) en Tablet — package `es.climadesk.app`
- **GitHub Release oficial:** [v1.8](https://github.com/kapi21/ClimaDesk/releases/tag/v1.8) con los 3 binarios disponibles para descarga directa.
- **Hoja de ruta activa:** Documentada detalladamente en [ROADMAP.md](file:///c:/@MIS%20PROYECTOS/REDMI4X/ROADMAP.md).

## Hecho en esta sesión (2026-10-02)

### 1. Tablet Galaxy Tab 2 Color (Rama `tablet-galaxy-tab2-color`)
- **Estética E-Ink Color**: Fondo blanco roto estilo papel de tinta electrónica (`#f4f1ea`), tarjetas limpias (`#fdfcf9`) y tipografía/números en negro puro (`#111111`).
- **Elementos a color**: Sol ámbar (`#f57c00`), nubes slate (`#546e7a`), lluvia y prob. precipitación en azul (`#1976d2`), humedad en cian (`#0288d1`), indicadores térmicos máx/mín (`▲` rojo / `▼` azul) y termómetro de sensación térmica con mercurio rojo.
- **Despliegue y verificación**: Instalado y probado con éxito en Galaxy Tab 2 (`c32063e15928a6f`).

### 2. Ordenación, Limpieza y Centralización de APKs
- **Carpeta `apks/`**: Centralización de los 3 APKs compilados y documentación en `apks/README.md`.
- **Nombres limpios**: Renombrados a `Climadesk-Color-v1.8.apk`, `Climadesk-v1.8.apk` y `Climadesk-Lite-v1.8.apk`.
- **Limpieza de repo**: Eliminación de capturas temporales y archivos residuales.
- **Capturas actualizadas**: Sustitución de preview en `docs/` y `README.md` por capturas reales de Tablet (`tablet-preview.png`) y Redmi 4X (`redmi-preview.png`).

### 3. Publicación Oficial en GitHub Releases
- Creación de la Release oficial **`v1.8`** con `gh release create` vinculando los 3 APKs para descarga directa.
- Enlaces de descarga directa integrados en el `README.md` de la rama `main`.

### 4. Viabilidad y Arquitectura para Apple iPhone XS
- **Estrategia definida**: Implementación PWA a pantalla completa vía Safari (sin coste de cuentas ni caducidad de certificados cada 7 días).
- **Consideraciones de hardware**: Pantalla Super Retina OLED (necesita fondo `#000000` y protección *anti-burn-in* mediante *pixel-shift*) y adaptación al Notch con `safe-area-inset`.
- **Modo Kiosk en iOS**: Bloqueo seguro mediante "Acceso Guiado" en Ajustes de iOS.

## Archivos clave y APKs
- `kiosk-app/app/src/main/assets/index.html` (interfaz, reloj SVG, cálculo de tema, selectores)
- `kiosk-app/app/src/main/java/es/climadesk/app/MainActivity.java` (WebView, bridge JS, WebChromeClient con logs)
- `kiosk-app/app/build.gradle.kts` (configuración de compilación)
- `ROADMAP.md` (hoja de ruta global de tareas y plataformas)
- `apks/Climadesk-Color-v1.8.apk` (Galaxy Tab 2 — E-Ink Color)
- `apks/Climadesk-v1.8.apk` (Galaxy Tab 2 — E-Ink Monocromo)
- `apks/Climadesk-Lite-v1.8.apk` (Xiaomi Redmi 4X — Edición Lite 640x360 dp)

## Cómo verificar y desplegar

### En Redmi 4X (`6f207ef7d440`)
```bash
git checkout redmi4x-lite
adb -s 6f207ef7d440 push apks/Climadesk-Lite-v1.8.apk /sdcard/Download/ClimaDesk-Lite.apk
adb -s 6f207ef7d440 shell "am start -a android.intent.action.VIEW -d 'file:///sdcard/Download/ClimaDesk-Lite.apk' -t 'application/vnd.android.package-archive'"
```

### En Samsung Galaxy Tab 2 (`c32063e15928a6f`)
```bash
# Versión Color:
adb -s c32063e15928a6f install -r apks/Climadesk-Color-v1.8.apk
# Versión Monocromo:
adb -s c32063e15928a6f install -r apks/Climadesk-v1.8.apk
```

## Dispositivos probados y soporte planificado
- **Xiaomi Redmi 4X (santoni)**: Android 7.1.2 / MIUI — ADB: `6f207ef7d440` (Verificado ✅)
- **Samsung Galaxy Tab 2 (espresso)**: Android 6.0 — ADB: `c32063e15928a6f` (Verificado ✅)
- **Apple iPhone XS**: iOS 17+ — Planificado como PWA OLED (Pendiente 📋)

