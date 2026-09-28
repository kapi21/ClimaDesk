# Design: Rename applicationId / package a ClimaDesk (P1.2)

Fecha: 2026-09-28  
Proyecto: ClimaDesk  
Estado: aprobado en conversación; pendiente de plan de implementación

## Objetivo

Unificar la identidad Android de la app bajo `es.climadesk.app` (Gradle + Java + nombres internos residuales de RelojClima) y subir versión a 1.7 / versionCode 8. Es un cambio breaking de instalación: el sistema trata el nuevo id como una app distinta.

## Decisiones

| Tema | Elección |
|------|----------|
| applicationId / namespace / Java package | `es.climadesk.app` |
| Alcance Java | Mover `MainActivity` y eliminar `es.redmi4x.relojclima` |
| Branding interno | `Theme.ClimaDesk`; `rootProject.name = "ClimaDesk"` |
| Versión | `versionName` 1.7, `versionCode` 8 |
| APK | No empaquetar/instalar salvo petición explícita |

## Fuera de alcance

- Icono launcher (P2)
- Modo solo-reloj, kiosk duro, release firmada
- Migración de datos / `localStorage` entre el package viejo y el nuevo
- Publicación en stores

## Cambios por archivo

| Archivo | Cambio |
|---------|--------|
| `kiosk-app/app/build.gradle.kts` | `namespace` + `applicationId` → `es.climadesk.app`; versión 1.7 / 8 |
| `kiosk-app/settings.gradle.kts` | `rootProject.name` → `ClimaDesk` |
| `kiosk-app/app/src/main/java/es/climadesk/app/MainActivity.java` | Crear (contenido actual, `package es.climadesk.app`) |
| `kiosk-app/app/src/main/java/es/redmi4x/relojclima/MainActivity.java` | Eliminar (y carpetas vacías) |
| `kiosk-app/app/src/main/res/values/styles.xml` | `Theme.RelojClima` → `Theme.ClimaDesk` |
| `kiosk-app/app/src/main/AndroidManifest.xml` | Referencias de theme → `Theme.ClimaDesk` |
| `HANDOFF.md` / `README.md` | Marcar P1.2 hecho; package documentado; siguiente = icono |

`strings.xml` (`app_name` = ClimaDesk) no requiere cambio.  
`index.html` / assets no dependen del package Java.

## Comportamiento e impacto

- Tras instalar el nuevo id, coexisten dos apps si no se desinstala `es.redmi4x.relojclima`.
- Datos WebView (`localStorage`) del id viejo **no** se copian al nuevo.
- El manifiesto sigue usando `.MainActivity` relativo al namespace; no hace falta FQCN si el namespace coincide.

## Verificación

Preferido sin instalar:

1. Confirmar que no quedan literales `es.redmi4x.relojclima` ni `Theme.RelojClima` en `kiosk-app/` (salvo historial git).
2. Opcional: `cd kiosk-app` → `gradlew.bat :app:assembleDebug` solo para validar compile (artefacto debug; no instalar salvo que el usuario lo pida).

En dispositivo (solo si el usuario lo pide): desinstalar la app vieja o aceptar dos iconos; instalar la nueva.

## Criterios de éxito

- `applicationId` / `namespace` / `package` Java = `es.climadesk.app`.
- Versión reportada 1.7 (8).
- Nombres internos RelojClima de theme y proyecto Gradle renombrados a ClimaDesk.
- Docs de roadmap/handoff actualizados.
- Sin APK release ni push de instalable no solicitado.
