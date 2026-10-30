# Design: Lite — AMANECER / ATARDECER en columna

Fecha: 2026-10-30  
Proyecto: ClimaDesk  
Estado: aprobado en conversación; pendiente de plan de implementación

## Objetivo

En la edición **Lite** (Redmi 4X), mostrar **AMANECER encima de ATARDECER** en la columna izquierda, en lugar de lado a lado, sin cambiar el contenido ni el estilo de cada fila.

## Decisiones

| Tema | Elección |
|------|----------|
| Alcance | Solo rama `redmi4x-lite` |
| Layout interno de cada fila | Opción A: igual que ahora (icono + etiqueta + hora) |
| Implementación | Solo CSS en `.left-section-bottom` |
| Entrega | Código + bump de versión + APK nuevo |
| Tablet Color / Mono | Fuera de alcance |
| JS / Open-Meteo / IDs `txt-sunrise` / `txt-sunset` | Sin cambios |
| GitHub Release | Fuera de este ciclo (solo si se pide después) |

## Cambios técnicos

### CSS (`kiosk-app/app/src/main/assets/index.html`)

En `.left-section-bottom` (hoy fila con `justify-content: space-around`):

- `flex-direction: column`
- `justify-content: center`
- `gap: 6px` (ajustable en prueba en dispositivo)
- Mantener padding y fondo actuales

No hace falta tocar el HTML de `.sun-time` ni el orden de nodos (amanecer ya va primero).

### Gradle (`kiosk-app/app/build.gradle.kts`)

- `versionName` = `1.8.1-lite`
- `versionCode` = `11`

### Artefacto y docs

- Compilar `assembleDebug` en `kiosk-app/`
- Copiar APK a `apks/Climadesk-Lite-v1.8.1.apk`
- Actualizar `apks/README.md` y `HANDOFF.md` (versión Lite, nombre de APK, comandos adb)
- En `main`, actualizar enlaces/nombre Lite en `README.md` / `apks/` si el escaparate apunta a Lite v1.8

### Sin cambios

- Ramas `tablet-galaxy-tab2` y `tablet-galaxy-tab2-color`
- Lógica de tema día/noche basada en sunrise/sunset
- Estructura HTML de las filas solares

## Verificación

1. `gradlew.bat assembleDebug` en `kiosk-app/` (rama `redmi4x-lite`)
2. Instalar en Redmi 4X (`6f207ef7d440`) el APK nuevo
3. Comprobar visualmente: AMANECER arriba, ATARDECER abajo; iconos y tipografía intactos
4. Si el reloj se aplasta en altura, ajustar `flex` de `.left-section-top` / `.left-section-bottom` (enfoque de contingencia; no es el plan base)

## Fuera de alcance

- PWA iOS / GitHub Pages
- Keystore de producción
- Modo solo-reloj
- Refactor de estilos inline a clases
